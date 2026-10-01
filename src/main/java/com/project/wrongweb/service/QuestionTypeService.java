package com.project.wrongweb.service;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.QuestionTypeRequest;
import com.project.wrongweb.entity.QuestionType;

import java.util.List;

/**
 * 题型服务。
 */
public interface QuestionTypeService {

    /**
     * 查询当前用户全部题型。
     */
    ResultVO<List<QuestionType>> list(Long userId);

    /**
     * 新增题型。
     */
    ResultVO<QuestionType> create(Long userId, QuestionTypeRequest request);

    /**
     * 修改题型。
     */
    ResultVO<QuestionType> update(Long userId, Long questionTypeId, QuestionTypeRequest request);

    /**
     * 删除题型。
     */
    ResultVO<Void> delete(Long userId, Long questionTypeId);
}
