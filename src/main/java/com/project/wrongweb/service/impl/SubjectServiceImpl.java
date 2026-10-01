package com.project.wrongweb.service.impl;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.SubjectRequest;
import com.project.wrongweb.entity.Mistake;
import com.project.wrongweb.entity.Subject;
import com.project.wrongweb.mapper.MistakeMapper;
import com.project.wrongweb.mapper.SubjectMapper;
import com.project.wrongweb.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 科目服务实现。
 *
 * <p>科目只允许当前用户读写，删除前会检查该科目下是否已有错题。</p>
 */
@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectMapper subjectMapper;
    private final MistakeMapper mistakeMapper;

    /**
     * 查询当前用户的科目列表。
     */
    @Override
    public ResultVO<List<Subject>> list(Long userId) {
        // TODO 【需要我实现：科目列表业务，摘要：调用 subjectMapper.selectByUserId 查询当前用户科目列表并返回】

        if (userId == null) {
            return ResultVO.fail(400, "用户ID不能为空");
        }

        List<Subject> subjects = subjectMapper.selectByUserId(userId);
        if (subjects == null) {
            subjects = Collections.emptyList();
        }

        return ResultVO.success(subjects);
    }

    /**
     * 新增科目。
     */
    @Override
    public ResultVO<Subject> create(Long userId, SubjectRequest request) {
        // TODO 【需要我实现：科目新增业务，摘要：
        // 1. 校验 subjectName 非空；
        // 2. 设置 Subject.userId = userId；
        // 3. 调用 subjectMapper.insert 保存并返回。】

        if (userId == null || request == null) {
            return ResultVO.fail(400, "用户ID和请求参数不能为空");
        }

        if (!StringUtils.hasText(request.getSubjectName())) {
            return ResultVO.fail(400, "科目名称不能为空");
        }

        String subjectName = request.getSubjectName().trim();

        // 在业务层提前检查同一用户下是否已经存在同名科目。
        if (existsSameSubjectName(userId, subjectName, null)) {
            return ResultVO.fail(409, "当前用户已存在同名科目");
        }

        Subject subject = Subject.builder()
                .userId(userId)
                .subjectName(subjectName)
                .build();

        int rows = subjectMapper.insert(subject);
        if (rows != 1) {
            return ResultVO.fail(500, "科目保存失败");
        }

        return ResultVO.success(subject);
    }

    /**
     * 修改科目。
     */
    @Override
    public ResultVO<Subject> update(Long userId, Long subjectId, SubjectRequest request) {
        // TODO 【需要我实现：科目修改业务，摘要：
        // 1. 校验 subjectId 和 subjectName；
        // 2. 调用 subjectMapper.updateByUserIdAndSubjectId，SQL 中必须同时匹配 userId，防止跨用户修改。】

        if (userId == null || subjectId == null || request == null) {
            return ResultVO.fail(400, "用户ID、科目ID和请求参数不能为空");
        }

        if (!StringUtils.hasText(request.getSubjectName())) {
            return ResultVO.fail(400, "科目名称不能为空");
        }

        // 先确认科目属于当前用户。
        Subject existing = subjectMapper.selectByUserIdAndSubjectId(userId, subjectId);
        if (existing == null) {
            return ResultVO.fail(404, "科目不存在或不属于当前用户");
        }

        String subjectName = request.getSubjectName().trim();

        if (existsSameSubjectName(userId, subjectName, subjectId)) {
            return ResultVO.fail(409, "当前用户已存在同名科目");
        }

        int rows = subjectMapper.updateByUserIdAndSubjectId(userId, subjectId, subjectName);
        if (rows != 1) {
            return ResultVO.fail(500, "科目更新失败");
        }

        Subject updated = subjectMapper.selectByUserIdAndSubjectId(userId, subjectId);
        return ResultVO.success(updated);
    }

    /**
     * 删除科目。
     */
    @Override
    public ResultVO<Void> delete(Long userId, Long subjectId) {
        // TODO 【需要我实现：科目删除业务，摘要：
        // 1. 判断该科目下是否已有错题；
        // 2. 根据业务规则决定拒绝删除、级联删除或解绑；
        // 3. 调用 subjectMapper.deleteByUserIdAndSubjectId，SQL 中必须同时匹配 userId。】

        if (userId == null || subjectId == null) {
            return ResultVO.fail(400, "用户ID和科目ID不能为空");
        }

        Subject existing = subjectMapper.selectByUserIdAndSubjectId(userId, subjectId);
        if (existing == null) {
            return ResultVO.fail(404, "科目不存在或不属于当前用户");
        }

        // 当前业务规则：如果科目下已有错题，则不允许直接删除，避免错题失去归属。
        List<Mistake> mistakes = mistakeMapper.selectByUserIdAndSubjectId(userId, subjectId);
        if (mistakes != null && !mistakes.isEmpty()) {
            return ResultVO.fail(400, "该科目下已有错题，不能删除");
        }

        int rows = subjectMapper.deleteByUserIdAndSubjectId(userId, subjectId);
        if (rows != 1) {
            return ResultVO.fail(500, "科目删除失败");
        }

        return ResultVO.success();
    }

    /**
     * 检查当前用户下是否已经存在同名科目。
     *
     * @param excludeSubjectId 修改时排除当前科目本身
     */
    private boolean existsSameSubjectName(Long userId, String subjectName, Long excludeSubjectId) {
        List<Subject> subjects = subjectMapper.selectByUserId(userId);
        if (subjects == null) {
            return false;
        }

        for (Subject subject : subjects) {
            if (excludeSubjectId != null && excludeSubjectId.equals(subject.getSubjectId())) {
                continue;
            }

            if (subjectName.equalsIgnoreCase(subject.getSubjectName())) {
                return true;
            }
        }

        return false;
    }
}
