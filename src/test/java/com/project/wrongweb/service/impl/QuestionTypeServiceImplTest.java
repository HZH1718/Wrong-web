package com.project.wrongweb.service.impl;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.QuestionTypeRequest;
import com.project.wrongweb.entity.Mistake;
import com.project.wrongweb.entity.QuestionType;
import com.project.wrongweb.mapper.MistakeMapper;
import com.project.wrongweb.mapper.QuestionTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QuestionTypeServiceImplTest {

    @Mock
    private QuestionTypeMapper questionTypeMapper;

    @Mock
    private MistakeMapper mistakeMapper;

    private QuestionTypeServiceImpl questionTypeService;

    @BeforeEach
    void setUp() {
        questionTypeService = new QuestionTypeServiceImpl(questionTypeMapper, mistakeMapper);
    }

    @Test
    void createShouldSaveQuestionType() {
        QuestionTypeRequest request = new QuestionTypeRequest();
        request.setTypeName("选择");

        when(questionTypeMapper.selectByUserId(1L)).thenReturn(Collections.emptyList());
        when(questionTypeMapper.insert(any(QuestionType.class))).thenAnswer(invocation -> {
            QuestionType questionType = invocation.getArgument(0);
            questionType.setQuestionTypeId(7L);
            return 1;
        });

        ResultVO<QuestionType> result = questionTypeService.create(1L, request);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(7L, result.getData().getQuestionTypeId());
    }

    @Test
    void createShouldRejectDuplicateName() {
        QuestionTypeRequest request = new QuestionTypeRequest();
        request.setTypeName("选择");

        QuestionType existing = QuestionType.builder()
                .questionTypeId(7L)
                .userId(1L)
                .typeName("选择")
                .build();

        when(questionTypeMapper.selectByUserId(1L)).thenReturn(List.of(existing));

        ResultVO<QuestionType> result = questionTypeService.create(1L, request);

        assertEquals(409, result.getCode());
    }

    @Test
    void updateShouldReturnUpdatedQuestionType() {
        QuestionTypeRequest request = new QuestionTypeRequest();
        request.setTypeName("填空");

        QuestionType existing = QuestionType.builder()
                .questionTypeId(7L)
                .userId(1L)
                .typeName("选择")
                .build();

        QuestionType updated = QuestionType.builder()
                .questionTypeId(7L)
                .userId(1L)
                .typeName("填空")
                .build();

        when(questionTypeMapper.selectByUserIdAndQuestionTypeId(1L, 7L))
                .thenReturn(existing, updated);
        when(questionTypeMapper.selectByUserId(1L)).thenReturn(List.of(existing));
        when(questionTypeMapper.updateByUserIdAndQuestionTypeId(1L, 7L, "填空")).thenReturn(1);

        ResultVO<QuestionType> result = questionTypeService.update(1L, 7L, request);

        assertEquals(200, result.getCode());
        assertEquals("填空", result.getData().getTypeName());
    }

    @Test
    void deleteShouldRejectWhenMistakesExist() {
        QuestionType existing = QuestionType.builder()
                .questionTypeId(7L)
                .userId(1L)
                .typeName("选择")
                .build();

        Mistake mistake = Mistake.builder()
                .mistakeId(1L)
                .userId(1L)
                .questionTypeId(7L)
                .build();

        when(questionTypeMapper.selectByUserIdAndQuestionTypeId(1L, 7L)).thenReturn(existing);
        when(mistakeMapper.selectByUserId(1L)).thenReturn(List.of(mistake));

        ResultVO<Void> result = questionTypeService.delete(1L, 7L);

        assertEquals(400, result.getCode());
        verify(questionTypeMapper, never()).deleteByUserIdAndQuestionTypeId(anyLong(), anyLong());
    }

    @Test
    void deleteShouldSucceedWhenNoMistakesExist() {
        QuestionType existing = QuestionType.builder()
                .questionTypeId(7L)
                .userId(1L)
                .typeName("选择")
                .build();

        when(questionTypeMapper.selectByUserIdAndQuestionTypeId(1L, 7L)).thenReturn(existing);
        when(mistakeMapper.selectByUserId(1L)).thenReturn(Collections.emptyList());
        when(questionTypeMapper.deleteByUserIdAndQuestionTypeId(1L, 7L)).thenReturn(1);

        ResultVO<Void> result = questionTypeService.delete(1L, 7L);

        assertEquals(200, result.getCode());
        verify(questionTypeMapper).deleteByUserIdAndQuestionTypeId(1L, 7L);
    }
}
