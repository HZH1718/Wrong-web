package com.project.wrongweb.service.impl;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.PaperGenerateRequest;
import com.project.wrongweb.entity.Mistake;
import com.project.wrongweb.entity.Paper;
import com.project.wrongweb.entity.QuestionType;
import com.project.wrongweb.entity.Subject;
import com.project.wrongweb.mapper.MistakeMapper;
import com.project.wrongweb.mapper.PaperMapper;
import com.project.wrongweb.mapper.QuestionTypeMapper;
import com.project.wrongweb.mapper.SubjectMapper;
import com.project.wrongweb.service.PaperService;
import com.project.wrongweb.vo.PaperDetailVO;
import com.project.wrongweb.vo.PaperListItemVO;
import com.project.wrongweb.vo.PaperQuestionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 试卷服务实现。
 *
 * <p>负责按单一科目随机抽题、永久保存历史试卷、查看试卷详情和提交试卷。</p>
 */
@Service
@RequiredArgsConstructor
public class PaperServiceImpl implements PaperService {

    private static final int DEFAULT_TOTAL_COUNT = 22;
    private static final int DEFAULT_CHOICE_COUNT = 12;
    private static final int DEFAULT_BLANK_COUNT = 5;
    private static final int DEFAULT_SOLVE_COUNT = 5;

    private static final String CHOICE_TYPE_NAME = "选择";
    private static final String BLANK_TYPE_NAME = "填空";
    private static final String SOLVE_TYPE_NAME = "解答";

    private final PaperMapper paperMapper;
    private final SubjectMapper subjectMapper;
    private final MistakeMapper mistakeMapper;
    private final QuestionTypeMapper questionTypeMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 随机组卷并永久保存。
     */
    @Override
    public ResultVO<PaperDetailVO> generate(Long userId, PaperGenerateRequest request) {
        // TODO 【需要我实现：随机组卷业务，摘要：
        // 1. 校验 request.subjectId 必须属于当前 userId；
        // 2. 按单科目抽题，不允许跨科目；
        // 3. 题量默认值：totalCount=22、choiceCount=12、blankCount=5、solveCount=5；
        //    前端不传任何题量时使用默认配置；传入时校验 choice+blank+solve 与 total 的关系；
        // 4. 根据错题池按选择题、填空题、解答题分别随机抽取对应数量；
        // 5. 将抽中的题目和答案快照序列化为 questionSnapshot JSON；
        // 6. 保存 Paper，status=0，保证试卷永久保存可回看；
        // 7. 未提交状态返回题目但不返回 wrongAnswer、correctAnswer。】

        if (userId == null || request == null) {
            return ResultVO.fail(400, "用户ID和请求参数不能为空");
        }

        if (request.getSubjectId() == null) {
            return ResultVO.fail(400, "抽题科目ID不能为空");
        }

        // 1. 确保抽题科目属于当前用户。
        Subject subject = subjectMapper.selectByUserIdAndSubjectId(userId, request.getSubjectId());
        if (subject == null) {
            return ResultVO.fail(400, "科目不存在或不属于当前用户");
        }

        // 2. 计算各类题量。
        Integer totalCount = request.getTotalCount();
        Integer choiceCount = request.getChoiceCount();
        Integer blankCount = request.getBlankCount();
        Integer solveCount = request.getSolveCount();

        boolean allCountsEmpty = totalCount == null
                && choiceCount == null
                && blankCount == null
                && solveCount == null;

        if (allCountsEmpty) {
            totalCount = DEFAULT_TOTAL_COUNT;
            choiceCount = DEFAULT_CHOICE_COUNT;
            blankCount = DEFAULT_BLANK_COUNT;
            solveCount = DEFAULT_SOLVE_COUNT;
        } else {
            if (choiceCount == null) {
                choiceCount = DEFAULT_CHOICE_COUNT;
            }
            if (blankCount == null) {
                blankCount = DEFAULT_BLANK_COUNT;
            }
            if (solveCount == null) {
                solveCount = DEFAULT_SOLVE_COUNT;
            }
            if (totalCount == null) {
                totalCount = choiceCount + blankCount + solveCount;
            }
        }

        if (totalCount < 0 || choiceCount < 0 || blankCount < 0 || solveCount < 0) {
            return ResultVO.fail(400, "各类题量不能为负数");
        }

        if (totalCount != choiceCount + blankCount + solveCount) {
            return ResultVO.fail(400, "总题数必须等于选择题、填空题、解答题数量之和");
        }

        // 3. 查找当前用户的三种题型ID。
        List<QuestionType> questionTypes = questionTypeMapper.selectByUserId(userId);
        Long choiceTypeId = findQuestionTypeId(questionTypes, CHOICE_TYPE_NAME);
        Long blankTypeId = findQuestionTypeId(questionTypes, BLANK_TYPE_NAME);
        Long solveTypeId = findQuestionTypeId(questionTypes, SOLVE_TYPE_NAME);

        if (choiceTypeId == null || blankTypeId == null || solveTypeId == null) {
            return ResultVO.fail(400, "当前用户未配置完整的选择、填空、解答题型");
        }

        // 4. 按三种题型分别从当前用户的错题池中随机抽题。
        List<Mistake> choiceMistakes = mistakeMapper.selectRandomByUserIdAndSubjectIdAndQuestionTypeId(
                userId, request.getSubjectId(), choiceTypeId, choiceCount);
        List<Mistake> blankMistakes = mistakeMapper.selectRandomByUserIdAndSubjectIdAndQuestionTypeId(
                userId, request.getSubjectId(), blankTypeId, blankCount);
        List<Mistake> solveMistakes = mistakeMapper.selectRandomByUserIdAndSubjectIdAndQuestionTypeId(
                userId, request.getSubjectId(), solveTypeId, solveCount);

        if (choiceMistakes == null || choiceMistakes.size() < choiceCount) {
            return ResultVO.fail(400, "选择题错题数量不足");
        }
        if (blankMistakes == null || blankMistakes.size() < blankCount) {
            return ResultVO.fail(400, "填空题错题数量不足");
        }
        if (solveMistakes == null || solveMistakes.size() < solveCount) {
            return ResultVO.fail(400, "解答题错题数量不足");
        }

        // 5. 组装题目快照。快照中保存完整答案，历史回看时才根据状态隐藏答案。
        List<PaperQuestionVO> questions = new ArrayList<>();
        questions.addAll(toPaperQuestions(choiceMistakes));
        questions.addAll(toPaperQuestions(blankMistakes));
        questions.addAll(toPaperQuestions(solveMistakes));

        String questionSnapshot = writeJson(questions);

        Paper paper = Paper.builder()
                .userId(userId)
                .subjectId(request.getSubjectId())
                .totalCount(totalCount)
                .choiceCount(choiceCount)
                .blankCount(blankCount)
                .solveCount(solveCount)
                .status(0)
                .questionSnapshot(questionSnapshot)
                .build();

        // 6. 保存试卷，成功后 paperId 会自动回填。
        int rows = paperMapper.insert(paper);
        if (rows != 1) {
            return ResultVO.fail(500, "试卷保存失败");
        }

        // 7. 生成后试卷状态为未提交，返回详情时由转换方法隐藏答案。
        return ResultVO.success(toPaperDetail(paper));
    }

    /**
     * 查询当前用户所有历史试卷列表。
     */
    @Override
    public ResultVO<List<PaperListItemVO>> list(Long userId) {
        // TODO 【需要我实现：历史试卷列表业务，摘要：调用 paperMapper.selectByUserId 查询当前用户试卷列表，列表不返回 questionSnapshot】

        if (userId == null) {
            return ResultVO.fail(400, "用户ID不能为空");
        }

        List<Paper> papers = paperMapper.selectByUserId(userId);
        if (papers == null) {
            papers = Collections.emptyList();
        }

        List<PaperListItemVO> items = new ArrayList<>();
        for (Paper paper : papers) {
            items.add(toPaperListItem(paper));
        }

        return ResultVO.success(items);
    }

    /**
     * 查看试卷详情。
     */
    @Override
    public ResultVO<PaperDetailVO> detail(Long userId, Long paperId) {
        // TODO 【需要我实现：试卷详情业务，摘要：
        // 1. 调用 paperMapper.selectByUserIdAndPaperId 查询试卷，必须匹配 userId；
        // 2. 将 questionSnapshot JSON 转换为 PaperQuestionVO 列表；
        // 3. status=0 时隐藏 wrongAnswer 和 correctAnswer；
        // 4. status=1 时返回完整题目和答案。】

        if (userId == null || paperId == null) {
            return ResultVO.fail(400, "用户ID和试卷ID不能为空");
        }

        Paper paper = paperMapper.selectByUserIdAndPaperId(userId, paperId);
        if (paper == null) {
            return ResultVO.fail(404, "试卷不存在或不属于当前用户");
        }

        return ResultVO.success(toPaperDetail(paper));
    }

    /**
     * 提交试卷。
     */
    @Override
    public ResultVO<Void> submit(Long userId, Long paperId) {
        // TODO 【需要我实现：提交试卷业务，摘要：
        // 1. 查询试卷并校验属于当前 userId；
        // 2. 校验当前状态为未提交；
        // 3. 调用 paperMapper.submitByUserIdAndPaperId 更新状态和提交时间。】

        if (userId == null || paperId == null) {
            return ResultVO.fail(400, "用户ID和试卷ID不能为空");
        }

        Paper paper = paperMapper.selectByUserIdAndPaperId(userId, paperId);
        if (paper == null) {
            return ResultVO.fail(404, "试卷不存在或不属于当前用户");
        }

        if (paper.getStatus() == null || paper.getStatus() != 0) {
            return ResultVO.fail(400, "试卷已提交或状态异常");
        }

        int rows = paperMapper.submitByUserIdAndPaperId(userId, paperId);
        if (rows != 1) {
            return ResultVO.fail(500, "试卷提交失败");
        }

        return ResultVO.success();
    }

    /**
     * 将 Paper 转换为详情对象，并根据提交状态决定是否隐藏答案。
     */
    private PaperDetailVO toPaperDetail(Paper paper) {
        List<PaperQuestionVO> questions = parseQuestions(paper.getQuestionSnapshot());

        boolean submitted = paper.getStatus() != null && paper.getStatus() == 1;
        if (!submitted) {
            for (PaperQuestionVO question : questions) {
                question.setWrongAnswer(null);
                question.setCorrectAnswer(null);
                question.setAnalysis(null);
            }
        }

        return PaperDetailVO.builder()
                .paperId(paper.getPaperId())
                .userId(paper.getUserId())
                .subjectId(paper.getSubjectId())
                .totalCount(paper.getTotalCount())
                .choiceCount(paper.getChoiceCount())
                .blankCount(paper.getBlankCount())
                .solveCount(paper.getSolveCount())
                .status(paper.getStatus())
                .questions(questions)
                .createdAt(paper.getCreatedAt())
                .submittedAt(paper.getSubmittedAt())
                .build();
    }

    /**
     * 将 Paper 转换为历史试卷列表项，不包含 questionSnapshot 大字段。
     */
    private PaperListItemVO toPaperListItem(Paper paper) {
        return PaperListItemVO.builder()
                .paperId(paper.getPaperId())
                .userId(paper.getUserId())
                .subjectId(paper.getSubjectId())
                .totalCount(paper.getTotalCount())
                .choiceCount(paper.getChoiceCount())
                .blankCount(paper.getBlankCount())
                .solveCount(paper.getSolveCount())
                .status(paper.getStatus())
                .createdAt(paper.getCreatedAt())
                .submittedAt(paper.getSubmittedAt())
                .build();
    }

    /**
     * 将错题实体转换为试卷题目对象。
     */
    private List<PaperQuestionVO> toPaperQuestions(List<Mistake> mistakes) {
        List<PaperQuestionVO> questions = new ArrayList<>();
        for (Mistake mistake : mistakes) {
            questions.add(PaperQuestionVO.builder()
                    .mistakeId(mistake.getMistakeId())
                    .subjectId(mistake.getSubjectId())
                    .questionTypeId(mistake.getQuestionTypeId())
                    .questionContent(mistake.getQuestionContent())
                    .knowledgePoint(mistake.getKnowledgePoint())
                    .wrongAnswer(mistake.getWrongAnswer())
                    .correctAnswer(mistake.getCorrectAnswer())
                    .analysis(mistake.getAnalysis())
                    .build());
        }
        return questions;
    }

    /**
     * 根据题型名称查找当前用户的题型ID。
     */
    private Long findQuestionTypeId(List<QuestionType> questionTypes, String typeName) {
        if (questionTypes == null) {
            return null;
        }

        for (QuestionType questionType : questionTypes) {
            if (typeName.equals(questionType.getTypeName())) {
                return questionType.getQuestionTypeId();
            }
        }

        return null;
    }

    /**
     * 将对象序列化为 JSON 字符串。
     */
    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException("试卷题目快照序列化失败", exception);
        }
    }

    /**
     * 将 JSON 字符串解析为试卷题目列表。
     */
    private List<PaperQuestionVO> parseQuestions(String json) {
        if (!StringUtils.hasText(json)) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(
                    json,
                    new TypeReference<List<PaperQuestionVO>>() {
                    });
        } catch (Exception exception) {
            throw new IllegalStateException("试卷题目快照解析失败", exception);
        }
    }
}
