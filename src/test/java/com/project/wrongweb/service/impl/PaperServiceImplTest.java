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
import com.project.wrongweb.vo.PaperDetailVO;
import com.project.wrongweb.vo.PaperListItemVO;
import com.project.wrongweb.vo.PaperQuestionVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaperServiceImplTest {

    @Mock
    private PaperMapper paperMapper;

    @Mock
    private SubjectMapper subjectMapper;

    @Mock
    private MistakeMapper mistakeMapper;

    @Mock
    private QuestionTypeMapper questionTypeMapper;

    private PaperServiceImpl paperService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        paperService = new PaperServiceImpl(
                paperMapper,
                subjectMapper,
                mistakeMapper,
                questionTypeMapper
        );
    }

    @Test
    void generateShouldUseDefaultCountsAndHideAnswers() {
        PaperGenerateRequest request = new PaperGenerateRequest();
        request.setSubjectId(1L);

        when(subjectMapper.selectByUserIdAndSubjectId(1L, 1L)).thenReturn(new Subject());
        when(questionTypeMapper.selectByUserId(1L)).thenReturn(questionTypes());

        List<Mistake> choiceMistakes = mistakes(12, 101L);
        List<Mistake> blankMistakes = mistakes(5, 102L);
        List<Mistake> solveMistakes = mistakes(5, 103L);

        when(mistakeMapper.selectRandomByUserIdAndSubjectIdAndQuestionTypeId(
                eq(1L), eq(1L), eq(101L), eq(12))).thenReturn(choiceMistakes);
        when(mistakeMapper.selectRandomByUserIdAndSubjectIdAndQuestionTypeId(
                eq(1L), eq(1L), eq(102L), eq(5))).thenReturn(blankMistakes);
        when(mistakeMapper.selectRandomByUserIdAndSubjectIdAndQuestionTypeId(
                eq(1L), eq(1L), eq(103L), eq(5))).thenReturn(solveMistakes);

        when(paperMapper.insert(any(Paper.class))).thenAnswer(invocation -> {
            Paper paper = invocation.getArgument(0);
            paper.setPaperId(100L);
            return 1;
        });

        ResultVO<PaperDetailVO> result = paperService.generate(1L, request);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(100L, result.getData().getPaperId());
        assertEquals(22, result.getData().getTotalCount());
        assertEquals(12, result.getData().getChoiceCount());
        assertEquals(5, result.getData().getBlankCount());
        assertEquals(5, result.getData().getSolveCount());
        assertEquals(0, result.getData().getStatus());
        assertEquals(22, result.getData().getQuestions().size());
        assertNull(result.getData().getQuestions().get(0).getWrongAnswer());
        assertNull(result.getData().getQuestions().get(0).getCorrectAnswer());
    }

    @Test
    void generateShouldRejectCountMismatch() {
        PaperGenerateRequest request = new PaperGenerateRequest();
        request.setSubjectId(1L);
        request.setTotalCount(20);
        request.setChoiceCount(12);
        request.setBlankCount(5);
        request.setSolveCount(5);

        when(subjectMapper.selectByUserIdAndSubjectId(1L, 1L)).thenReturn(new Subject());

        ResultVO<PaperDetailVO> result = paperService.generate(1L, request);

        assertEquals(400, result.getCode());
    }

    @Test
    void detailShouldHideAnswersBeforeSubmit() throws Exception {
        List<PaperQuestionVO> questions = paperQuestions();
        String snapshot = objectMapper.writeValueAsString(questions);

        Paper paper = Paper.builder()
                .paperId(100L)
                .userId(1L)
                .subjectId(1L)
                .totalCount(1)
                .choiceCount(1)
                .blankCount(0)
                .solveCount(0)
                .status(0)
                .questionSnapshot(snapshot)
                .build();

        when(paperMapper.selectByUserIdAndPaperId(1L, 100L)).thenReturn(paper);

        ResultVO<PaperDetailVO> result = paperService.detail(1L, 100L);

        assertEquals(200, result.getCode());
        assertNull(result.getData().getQuestions().get(0).getWrongAnswer());
        assertNull(result.getData().getQuestions().get(0).getCorrectAnswer());
    }

    @Test
    void detailShouldReturnAnswersAfterSubmit() throws Exception {
        List<PaperQuestionVO> questions = paperQuestions();
        String snapshot = objectMapper.writeValueAsString(questions);

        Paper paper = Paper.builder()
                .paperId(100L)
                .userId(1L)
                .subjectId(1L)
                .totalCount(1)
                .choiceCount(1)
                .blankCount(0)
                .solveCount(0)
                .status(1)
                .questionSnapshot(snapshot)
                .build();

        when(paperMapper.selectByUserIdAndPaperId(1L, 100L)).thenReturn(paper);

        ResultVO<PaperDetailVO> result = paperService.detail(1L, 100L);

        assertEquals(200, result.getCode());
        assertEquals("错误答案", result.getData().getQuestions().get(0).getWrongAnswer());
        assertEquals("正确答案", result.getData().getQuestions().get(0).getCorrectAnswer());
    }

    @Test
    void listShouldReturnPaperListItemVos() {
        Paper paper = Paper.builder()
                .paperId(100L)
                .userId(1L)
                .subjectId(1L)
                .totalCount(22)
                .choiceCount(12)
                .blankCount(5)
                .solveCount(5)
                .status(0)
                .build();

        when(paperMapper.selectByUserId(1L)).thenReturn(List.of(paper));

        ResultVO<List<PaperListItemVO>> result = paperService.list(1L);

        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().size());
        assertEquals(100L, result.getData().get(0).getPaperId());
    }

    @Test
    void submitShouldRejectAlreadySubmittedPaper() {
        Paper paper = Paper.builder()
                .paperId(100L)
                .userId(1L)
                .status(1)
                .build();

        when(paperMapper.selectByUserIdAndPaperId(1L, 100L)).thenReturn(paper);

        ResultVO<Void> result = paperService.submit(1L, 100L);

        assertEquals(400, result.getCode());
    }

    private List<QuestionType> questionTypes() {
        QuestionType choice = QuestionType.builder()
                .questionTypeId(101L)
                .userId(1L)
                .typeName("选择")
                .build();

        QuestionType blank = QuestionType.builder()
                .questionTypeId(102L)
                .userId(1L)
                .typeName("填空")
                .build();

        QuestionType solve = QuestionType.builder()
                .questionTypeId(103L)
                .userId(1L)
                .typeName("解答")
                .build();

        return List.of(choice, blank, solve);
    }

    private List<Mistake> mistakes(int count, Long questionTypeId) {
        List<Mistake> mistakes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            long id = questionTypeId * 100 + i;
            mistakes.add(Mistake.builder()
                    .mistakeId(id)
                    .userId(1L)
                    .subjectId(1L)
                    .questionTypeId(questionTypeId)
                    .questionContent("题目" + id)
                    .wrongAnswer("错误答案" + id)
                    .correctAnswer("正确答案" + id)
                    .knowledgePoint("知识点")
                    .build());
        }
        return mistakes;
    }

    private List<PaperQuestionVO> paperQuestions() {
        PaperQuestionVO question = PaperQuestionVO.builder()
                .mistakeId(1L)
                .subjectId(1L)
                .questionTypeId(101L)
                .questionContent("题目")
                .knowledgePoint("知识点")
                .wrongAnswer("错误答案")
                .correctAnswer("正确答案")
                .build();

        return List.of(question);
    }
}
