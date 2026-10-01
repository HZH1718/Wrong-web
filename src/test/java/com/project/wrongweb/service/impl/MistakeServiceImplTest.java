package com.project.wrongweb.service.impl;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.MistakeRequest;
import com.project.wrongweb.entity.Mistake;
import com.project.wrongweb.entity.QuestionType;
import com.project.wrongweb.entity.Subject;
import com.project.wrongweb.mapper.MistakeMapper;
import com.project.wrongweb.mapper.QuestionTypeMapper;
import com.project.wrongweb.mapper.SubjectMapper;
import com.project.wrongweb.vo.MistakeHomeGroupVO;
import com.project.wrongweb.vo.MistakeStatisticsVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MistakeServiceImplTest {

    @Mock
    private MistakeMapper mistakeMapper;

    @Mock
    private SubjectMapper subjectMapper;

    @Mock
    private QuestionTypeMapper questionTypeMapper;

    private MistakeServiceImpl mistakeService;

    @BeforeEach
    void setUp() {
        mistakeService = new MistakeServiceImpl(mistakeMapper, subjectMapper, questionTypeMapper);
    }

    @Test
    void createShouldSaveMistakeAndReturnId() {
        MistakeRequest request = new MistakeRequest();
        request.setSubjectId(1L);
        request.setQuestionTypeId(2L);
        request.setQuestionContent("题目");
        request.setWrongAnswer("错误答案");
        request.setCorrectAnswer("正确答案");
        request.setKnowledgePoint("导数");

        when(subjectMapper.selectByUserIdAndSubjectId(1L, 1L)).thenReturn(new Subject());
        when(questionTypeMapper.selectByUserIdAndQuestionTypeId(1L, 2L)).thenReturn(new QuestionType());
        when(mistakeMapper.insert(any(Mistake.class))).thenAnswer(invocation -> {
            Mistake mistake = invocation.getArgument(0);
            mistake.setMistakeId(9L);
            return 1;
        });

        ResultVO<Mistake> result = mistakeService.create(1L, request);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(9L, result.getData().getMistakeId());
        assertEquals(1L, result.getData().getUserId());
        assertNotNull(result.getData().getEntryTime());
    }

    @Test
    void createShouldRejectSubjectNotOwnedByUser() {
        MistakeRequest request = new MistakeRequest();
        request.setSubjectId(1L);
        request.setQuestionTypeId(2L);
        request.setQuestionContent("题目");
        request.setWrongAnswer("错误答案");
        request.setCorrectAnswer("正确答案");

        when(subjectMapper.selectByUserIdAndSubjectId(1L, 1L)).thenReturn(null);

        ResultVO<Mistake> result = mistakeService.create(1L, request);

        assertEquals(400, result.getCode());
    }

    @Test
    void updateShouldPreserveEntryTimeWhenNotProvided() {
        MistakeRequest request = new MistakeRequest();
        request.setSubjectId(1L);
        request.setQuestionTypeId(2L);
        request.setQuestionContent("新题目");
        request.setWrongAnswer("新错误答案");
        request.setCorrectAnswer("新正确答案");

        Mistake existing = Mistake.builder()
                .mistakeId(9L)
                .userId(1L)
                .subjectId(1L)
                .questionTypeId(2L)
                .entryTime(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();

        when(mistakeMapper.selectByUserIdAndMistakeId(1L, 9L)).thenReturn(existing);
        when(subjectMapper.selectByUserIdAndSubjectId(1L, 1L)).thenReturn(new Subject());
        when(questionTypeMapper.selectByUserIdAndQuestionTypeId(1L, 2L)).thenReturn(new QuestionType());
        when(mistakeMapper.updateByUserIdAndMistakeId(anyLong(), anyLong(), any(Mistake.class))).thenReturn(1);

        ResultVO<Mistake> result = mistakeService.update(1L, 9L, request);

        assertEquals(200, result.getCode());
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), result.getData().getEntryTime());
    }

    @Test
    void statisticsShouldReturnTotalAndGroups() {
        when(mistakeMapper.countByUserId(1L)).thenReturn(10L);
        when(mistakeMapper.countGroupBySubject(1L)).thenReturn(List.of(
                Map.of(
                        "subject_id", 1L,
                        "subject_name", "高数",
                        "mistake_count", 6L
                ),
                Map.of(
                        "subject_id", 2L,
                        "subject_name", "线代",
                        "mistake_count", 4L
                )
        ));

        ResultVO<MistakeStatisticsVO> result = mistakeService.statistics(1L);

        assertEquals(200, result.getCode());
        assertEquals(10L, result.getData().getTotalCount());
        assertEquals(2, result.getData().getSubjectGroups().size());
        assertEquals(6L, result.getData().getSubjectGroups().get(0).getMistakeCount());
    }

    @Test
    void homeListShouldGroupMistakesBySubject() {
        Mistake mistake1 = Mistake.builder()
                .mistakeId(1L)
                .subjectId(10L)
                .questionContent("题目1")
                .entryTime(LocalDateTime.of(2025, 1, 1, 9, 0))
                .build();

        Mistake mistake2 = Mistake.builder()
                .mistakeId(2L)
                .subjectId(10L)
                .questionContent("题目2")
                .entryTime(LocalDateTime.of(2025, 1, 2, 9, 0))
                .build();

        Subject subject = Subject.builder()
                .subjectId(10L)
                .userId(1L)
                .subjectName("高数")
                .build();

        when(mistakeMapper.selectByUserId(1L)).thenReturn(List.of(mistake1, mistake2));
        when(subjectMapper.selectByUserId(1L)).thenReturn(List.of(subject));

        ResultVO<List<MistakeHomeGroupVO>> result = mistakeService.homeList(1L);

        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().size());
        assertEquals("高数", result.getData().get(0).getSubjectName());
        assertEquals(2, result.getData().get(0).getMistakes().size());
        assertEquals("题目1", result.getData().get(0).getMistakes().get(0).getQuestionContent());
    }
}
