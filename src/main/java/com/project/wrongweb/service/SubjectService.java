package com.project.wrongweb.service;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.SubjectRequest;
import com.project.wrongweb.entity.Subject;

import java.util.List;

/**
 * 科目服务。
 */
public interface SubjectService {

    /**
     * 查询当前用户全部科目。
     */
    ResultVO<List<Subject>> list(Long userId);

    /**
     * 新增科目。
     */
    ResultVO<Subject> create(Long userId, SubjectRequest request);

    /**
     * 修改科目。
     */
    ResultVO<Subject> update(Long userId, Long subjectId, SubjectRequest request);

    /**
     * 删除科目。
     */
    ResultVO<Void> delete(Long userId, Long subjectId);
}
