package com.project.wrongweb.service.impl;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.SubjectRequest;
import com.project.wrongweb.entity.Mistake;
import com.project.wrongweb.entity.Subject;
import com.project.wrongweb.mapper.MistakeMapper;
import com.project.wrongweb.mapper.SubjectMapper;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SubjectServiceImplTest {

    @Mock
    private SubjectMapper subjectMapper;

    @Mock
    private MistakeMapper mistakeMapper;

    private SubjectServiceImpl subjectService;

    @BeforeEach
    void setUp() {
        subjectService = new SubjectServiceImpl(subjectMapper, mistakeMapper);
    }

    @Test
    void createShouldSaveSubjectAndReturnId() {
        SubjectRequest request = new SubjectRequest();
        request.setSubjectName("高数");

        when(subjectMapper.selectByUserId(1L)).thenReturn(Collections.emptyList());
        when(subjectMapper.insert(any(Subject.class))).thenAnswer(invocation -> {
            Subject subject = invocation.getArgument(0);
            subject.setSubjectId(5L);
            return 1;
        });

        ResultVO<Subject> result = subjectService.create(1L, request);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(5L, result.getData().getSubjectId());
        assertEquals("高数", result.getData().getSubjectName());
    }

    @Test
    void createShouldRejectDuplicateName() {
        SubjectRequest request = new SubjectRequest();
        request.setSubjectName("高数");

        Subject existing = Subject.builder()
                .subjectId(1L)
                .userId(1L)
                .subjectName("高数")
                .build();

        when(subjectMapper.selectByUserId(1L)).thenReturn(List.of(existing));

        ResultVO<Subject> result = subjectService.create(1L, request);

        assertEquals(409, result.getCode());
    }

    @Test
    void updateShouldReturnUpdatedSubject() {
        SubjectRequest request = new SubjectRequest();
        request.setSubjectName("线代");

        Subject existing = Subject.builder()
                .subjectId(5L)
                .userId(1L)
                .subjectName("高数")
                .build();

        Subject updated = Subject.builder()
                .subjectId(5L)
                .userId(1L)
                .subjectName("线代")
                .build();

        when(subjectMapper.selectByUserIdAndSubjectId(1L, 5L)).thenReturn(existing);
        when(subjectMapper.selectByUserId(1L)).thenReturn(List.of(existing));
        when(subjectMapper.updateByUserIdAndSubjectId(1L, 5L, "线代")).thenReturn(1);
        when(subjectMapper.selectByUserIdAndSubjectId(1L, 5L)).thenReturn(existing, updated);

        ResultVO<Subject> result = subjectService.update(1L, 5L, request);

        assertEquals(200, result.getCode());
        assertEquals("线代", result.getData().getSubjectName());
    }

    @Test
    void deleteShouldRejectWhenMistakesExist() {
        Subject existing = Subject.builder()
                .subjectId(5L)
                .userId(1L)
                .subjectName("高数")
                .build();

        when(subjectMapper.selectByUserIdAndSubjectId(1L, 5L)).thenReturn(existing);
        when(mistakeMapper.selectByUserIdAndSubjectId(1L, 5L))
                .thenReturn(List.of(new Mistake()));

        ResultVO<Void> result = subjectService.delete(1L, 5L);

        assertEquals(400, result.getCode());
        verify(subjectMapper, never()).deleteByUserIdAndSubjectId(anyLong(), anyLong());
    }

    @Test
    void deleteShouldSucceedWhenNoMistakesExist() {
        Subject existing = Subject.builder()
                .subjectId(5L)
                .userId(1L)
                .subjectName("高数")
                .build();

        when(subjectMapper.selectByUserIdAndSubjectId(1L, 5L)).thenReturn(existing);
        when(mistakeMapper.selectByUserIdAndSubjectId(1L, 5L)).thenReturn(Collections.emptyList());
        when(subjectMapper.deleteByUserIdAndSubjectId(1L, 5L)).thenReturn(1);

        ResultVO<Void> result = subjectService.delete(1L, 5L);

        assertEquals(200, result.getCode());
        verify(subjectMapper).deleteByUserIdAndSubjectId(1L, 5L);
    }
}
