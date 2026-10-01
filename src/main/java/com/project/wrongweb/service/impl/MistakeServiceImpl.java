package com.project.wrongweb.service.impl;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.MistakeRequest;
import com.project.wrongweb.entity.Mistake;
import com.project.wrongweb.entity.QuestionType;
import com.project.wrongweb.entity.Subject;
import com.project.wrongweb.mapper.MistakeMapper;
import com.project.wrongweb.mapper.QuestionTypeMapper;
import com.project.wrongweb.mapper.SubjectMapper;
import com.project.wrongweb.service.MistakeService;
import com.project.wrongweb.vo.MistakeHomeGroupVO;
import com.project.wrongweb.vo.MistakeHomeItemVO;
import com.project.wrongweb.vo.MistakePageVO;
import com.project.wrongweb.vo.MistakeStatisticsVO;
import com.project.wrongweb.vo.SubjectMistakeCountVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 错题服务实现。
 *
 * <p>负责错题录入、修改、删除、查询、首页统计和首页按科目展示。</p>
 */
@Service
@RequiredArgsConstructor
public class MistakeServiceImpl implements MistakeService {

    private final MistakeMapper mistakeMapper;
    private final SubjectMapper subjectMapper;
    private final QuestionTypeMapper questionTypeMapper;

    /**
     * 录入错题。
     */
    @Override
    public ResultVO<Mistake> create(Long userId, MistakeRequest request) {
        // TODO 【需要我实现：错题录入业务，摘要：
        // 1. 校验 subjectId、questionTypeId、题干、错误答案、正确答案；
        // 2. 校验 subjectId 和 questionTypeId 必须属于当前 userId；
        // 3. entryTime 为空时取当前时间；
        // 4. 设置 Mistake.userId = userId；
        // 5. 调用 mistakeMapper.insert 保存并返回。】

        // 1. 校验用户和请求体是否为空，避免后续出现空指针。
        if (userId == null || request == null) {
            return ResultVO.fail(400, "用户ID和请求参数不能为空");
        }

        if (request.getSubjectId() == null) {
            return ResultVO.fail(400, "科目ID不能为空");
        }

        if (request.getQuestionTypeId() == null) {
            return ResultVO.fail(400, "题型ID不能为空");
        }

        if (!StringUtils.hasText(request.getQuestionContent())
                || !StringUtils.hasText(request.getWrongAnswer())
                || !StringUtils.hasText(request.getCorrectAnswer())) {
            return ResultVO.fail(400, "题目、错误答案、正确答案均不能为空");
        }

        // 2. 确保科目和题型都属于当前用户，防止通过接口写入其他用户的数据。
        Subject subject = subjectMapper.selectByUserIdAndSubjectId(userId, request.getSubjectId());
        if (subject == null) {
            return ResultVO.fail(400, "科目不存在或不属于当前用户");
        }

        QuestionType questionType = questionTypeMapper.selectByUserIdAndQuestionTypeId(
                userId, request.getQuestionTypeId());
        if (questionType == null) {
            return ResultVO.fail(400, "题型不存在或不属于当前用户");
        }

        // 3. 录入时间如果不传，则使用当前时间。
        LocalDateTime entryTime = request.getEntryTime() == null
                ? LocalDateTime.now()
                : request.getEntryTime();

        // 4. 组装错题实体，userId 必须来自当前登录用户。
        Mistake mistake = Mistake.builder()
                .userId(userId)
                .subjectId(request.getSubjectId())
                .questionTypeId(request.getQuestionTypeId())
                .questionContent(request.getQuestionContent().trim())
                .wrongAnswer(request.getWrongAnswer().trim())
                .correctAnswer(request.getCorrectAnswer().trim())
                .entryTime(entryTime)
                .knowledgePoint(request.getKnowledgePoint())
                .analysis(request.getAnalysis())
                .build();

        // 5. 保存错题，MyBatis 会自动回填 mistakeId。
        int rows = mistakeMapper.insert(mistake);
        if (rows != 1) {
            return ResultVO.fail(500, "错题保存失败");
        }

        return ResultVO.success(mistake);
    }

    /**
     * 修改错题。
     */
    @Override
    public ResultVO<Mistake> update(Long userId, Long mistakeId, MistakeRequest request) {
        // TODO 【需要我实现：错题修改业务，摘要：
        // 1. 校验 mistakeId 和入参；
        // 2. 确保 subjectId、questionTypeId 属于当前用户；
        // 3. 调用 mistakeMapper.updateByUserIdAndMistakeId，SQL 必须同时匹配 userId。】

        if (userId == null || mistakeId == null || request == null) {
            return ResultVO.fail(400, "用户ID、错题ID和请求参数不能为空");
        }

        if (request.getSubjectId() == null) {
            return ResultVO.fail(400, "科目ID不能为空");
        }

        if (request.getQuestionTypeId() == null) {
            return ResultVO.fail(400, "题型ID不能为空");
        }

        if (!StringUtils.hasText(request.getQuestionContent())
                || !StringUtils.hasText(request.getWrongAnswer())
                || !StringUtils.hasText(request.getCorrectAnswer())) {
            return ResultVO.fail(400, "题目、错误答案、正确答案均不能为空");
        }

        // 先确认要修改的错题属于当前用户。
        Mistake existing = mistakeMapper.selectByUserIdAndMistakeId(userId, mistakeId);
        if (existing == null) {
            return ResultVO.fail(404, "错题不存在或不属于当前用户");
        }

        // 新指定的科目和题型也必须属于当前用户。
        Subject subject = subjectMapper.selectByUserIdAndSubjectId(userId, request.getSubjectId());
        if (subject == null) {
            return ResultVO.fail(400, "科目不存在或不属于当前用户");
        }

        QuestionType questionType = questionTypeMapper.selectByUserIdAndQuestionTypeId(
                userId, request.getQuestionTypeId());
        if (questionType == null) {
            return ResultVO.fail(400, "题型不存在或不属于当前用户");
        }

        // 修改时如果不传录入时间，则保留原录入时间。
        LocalDateTime entryTime = request.getEntryTime() == null
                ? existing.getEntryTime()
                : request.getEntryTime();

        Mistake mistake = Mistake.builder()
                .mistakeId(mistakeId)
                .userId(userId)
                .subjectId(request.getSubjectId())
                .questionTypeId(request.getQuestionTypeId())
                .questionContent(request.getQuestionContent().trim())
                .wrongAnswer(request.getWrongAnswer().trim())
                .correctAnswer(request.getCorrectAnswer().trim())
                .entryTime(entryTime)
                .knowledgePoint(request.getKnowledgePoint())
                .analysis(request.getAnalysis())
                .build();

        int rows = mistakeMapper.updateByUserIdAndMistakeId(userId, mistakeId, mistake);
        if (rows != 1) {
            return ResultVO.fail(500, "错题更新失败");
        }

        return ResultVO.success(mistake);
    }

    /**
     * 删除错题。
     */
    @Override
    public ResultVO<Void> delete(Long userId, Long mistakeId) {
        // TODO 【需要我实现：错题删除业务，摘要：调用 mistakeMapper.deleteByUserIdAndMistakeId，SQL 必须同时匹配 userId，防止跨用户删除】

        if (userId == null || mistakeId == null) {
            return ResultVO.fail(400, "用户ID和错题ID不能为空");
        }

        int rows = mistakeMapper.deleteByUserIdAndMistakeId(userId, mistakeId);
        if (rows != 1) {
            return ResultVO.fail(404, "错题不存在或不属于当前用户");
        }

        return ResultVO.success();
    }

    /**
     * 查询错题详情。
     */
    @Override
    public ResultVO<Mistake> detail(Long userId, Long mistakeId) {
        // TODO 【需要我实现：错题详情业务，摘要：调用 mistakeMapper.selectByUserIdAndMistakeId 查询详情，未找到时返回错误信息】

        if (userId == null || mistakeId == null) {
            return ResultVO.fail(400, "用户ID和错题ID不能为空");
        }

        Mistake mistake = mistakeMapper.selectByUserIdAndMistakeId(userId, mistakeId);
        if (mistake == null) {
            return ResultVO.fail(404, "错题不存在或不属于当前用户");
        }

        return ResultVO.success(mistake);
    }

    /**
     * 查询当前用户全部错题。
     */
    @Override
    public ResultVO<List<Mistake>> list(Long userId) {
        // TODO 【需要我实现：错题列表业务，摘要：调用 mistakeMapper.selectByUserId 查询当前用户错题，可按录入时间倒序】

        if (userId == null) {
            return ResultVO.fail(400, "用户ID不能为空");
        }

        List<Mistake> mistakes = mistakeMapper.selectByUserId(userId);
        if (mistakes == null) {
            mistakes = Collections.emptyList();
        }

        return ResultVO.success(mistakes);
    }

    /**
     * 分页查询当前用户错题，支持按科目、题型可选多值筛选。
     */
    @Override
    public ResultVO<MistakePageVO> page(Long userId, List<Long> subjectIds, List<Long> questionTypeIds,
                                        Integer page, Integer size) {
        if (userId == null) {
            return ResultVO.fail(400, "用户ID不能为空");
        }

        // 页码和每页数量做边界保护：页码从 1 开始，每页最多 100 条。
        int currentPage = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1) ? 10 : Math.min(size, 100);
        int offset = (currentPage - 1) * pageSize;

        // 空集合等价于不筛选。
        List<Long> subjectFilter = (subjectIds == null || subjectIds.isEmpty()) ? null : subjectIds;
        List<Long> questionTypeFilter = (questionTypeIds == null || questionTypeIds.isEmpty())
                ? null
                : questionTypeIds;

        // 先统计满足筛选条件的总数，再查询当前页数据。
        long total = mistakeMapper.countByUserIdWithFilter(userId, subjectFilter, questionTypeFilter);

        List<Mistake> mistakes = Collections.emptyList();
        if (total > 0) {
            mistakes = mistakeMapper.selectByUserIdWithFilter(
                    userId, subjectFilter, questionTypeFilter, offset, pageSize);
            if (mistakes == null) {
                mistakes = Collections.emptyList();
            }
        }

        MistakePageVO pageVO = MistakePageVO.builder()
                .total(total)
                .page(currentPage)
                .size(pageSize)
                .list(mistakes)
                .build();

        return ResultVO.success(pageVO);
    }

    /**
     * 首页统计：错题总数以及按科目分组统计。
     */
    @Override
    public ResultVO<MistakeStatisticsVO> statistics(Long userId) {
        // TODO 【需要我实现：首页统计业务，摘要：
        // 1. 调用 mistakeMapper.countByUserId 获取总数量；
        // 2. 调用 mistakeMapper.countGroupBySubject 获取按科目分组数量；
        // 3. 组装 MistakeStatisticsVO 返回。】

        if (userId == null) {
            return ResultVO.fail(400, "用户ID不能为空");
        }

        long totalCount = mistakeMapper.countByUserId(userId);

        List<Map<String, Object>> rows = mistakeMapper.countGroupBySubject(userId);
        List<SubjectMistakeCountVO> subjectGroups = new ArrayList<>();

        if (rows != null) {
            for (Map<String, Object> row : rows) {
                Long subjectId = toLong(row.get("subject_id"));
                String subjectName = row.get("subject_name") == null
                        ? null
                        : row.get("subject_name").toString();
                Long mistakeCount = toLong(row.get("mistake_count"));

                subjectGroups.add(SubjectMistakeCountVO.builder()
                        .subjectId(subjectId)
                        .subjectName(subjectName)
                        .mistakeCount(mistakeCount)
                        .build());
            }
        }

        MistakeStatisticsVO statistics = MistakeStatisticsVO.builder()
                .totalCount(totalCount)
                .subjectGroups(subjectGroups)
                .build();

        return ResultVO.success(statistics);
    }

    /**
     * 首页展示：按科目分组返回错题，并且只返回题目和录入时间。
     */
    @Override
    public ResultVO<List<MistakeHomeGroupVO>> homeList(Long userId) {
        // TODO 【需要我实现：首页错题展示业务，摘要：
        // 1. 查询当前用户错题；
        // 2. 按 subjectId 分组，每组只返回 mistakeId、questionContent、entryTime；
        // 3. 组装 List<MistakeHomeGroupVO> 返回。】

        if (userId == null) {
            return ResultVO.fail(400, "用户ID不能为空");
        }

        List<Mistake> mistakes = mistakeMapper.selectByUserId(userId);
        List<Subject> subjects = subjectMapper.selectByUserId(userId);

        // 先把科目ID映射到科目名称，方便分组结果中展示科目名称。
        Map<Long, String> subjectNameMap = new LinkedHashMap<>();
        if (subjects != null) {
            for (Subject subject : subjects) {
                subjectNameMap.put(subject.getSubjectId(), subject.getSubjectName());
            }
        }

        // 使用 LinkedHashMap 保留科目出现的顺序。
        Map<Long, List<MistakeHomeItemVO>> groupedMap = new LinkedHashMap<>();

        if (mistakes != null) {
            for (Mistake mistake : mistakes) {
                Long subjectId = mistake.getSubjectId();

                MistakeHomeItemVO item = MistakeHomeItemVO.builder()
                        .mistakeId(mistake.getMistakeId())
                        .questionContent(mistake.getQuestionContent())
                        .entryTime(mistake.getEntryTime())
                        .build();

                groupedMap
                        .computeIfAbsent(subjectId, key -> new ArrayList<>())
                        .add(item);
            }
        }

        List<MistakeHomeGroupVO> groups = new ArrayList<>();
        for (Map.Entry<Long, List<MistakeHomeItemVO>> entry : groupedMap.entrySet()) {
            groups.add(MistakeHomeGroupVO.builder()
                    .subjectId(entry.getKey())
                    .subjectName(subjectNameMap.get(entry.getKey()))
                    .mistakes(entry.getValue())
                    .build());
        }

        return ResultVO.success(groups);
    }

    /**
     * 将数据库返回的统计数字转换为 Long。
     */
    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.valueOf(value.toString());
    }
}
