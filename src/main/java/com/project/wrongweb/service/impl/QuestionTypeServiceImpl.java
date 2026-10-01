package com.project.wrongweb.service.impl;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.QuestionTypeRequest;
import com.project.wrongweb.entity.Mistake;
import com.project.wrongweb.entity.QuestionType;
import com.project.wrongweb.mapper.MistakeMapper;
import com.project.wrongweb.mapper.QuestionTypeMapper;
import com.project.wrongweb.service.QuestionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 题型服务实现。
 *
 * <p>题型只允许当前用户读写，删除前会检查该题型下是否已有错题。</p>
 */
@Service
@RequiredArgsConstructor
public class QuestionTypeServiceImpl implements QuestionTypeService {

    private final QuestionTypeMapper questionTypeMapper;
    private final MistakeMapper mistakeMapper;

    /**
     * 查询当前用户的题型列表。
     */
    @Override
    public ResultVO<List<QuestionType>> list(Long userId) {
        // TODO 【需要我实现：题型列表业务，摘要：调用 questionTypeMapper.selectByUserId 查询当前用户题型列表并返回】

        if (userId == null) {
            return ResultVO.fail(400, "用户ID不能为空");
        }

        List<QuestionType> questionTypes = questionTypeMapper.selectByUserId(userId);
        if (questionTypes == null) {
            questionTypes = Collections.emptyList();
        }

        return ResultVO.success(questionTypes);
    }

    /**
     * 新增题型。
     */
    @Override
    public ResultVO<QuestionType> create(Long userId, QuestionTypeRequest request) {
        // TODO 【需要我实现：题型新增业务，摘要：
        // 1. 校验 typeName 非空；
        // 2. 设置 QuestionType.userId = userId；
        // 3. 调用 questionTypeMapper.insert 保存并返回。】

        if (userId == null || request == null) {
            return ResultVO.fail(400, "用户ID和请求参数不能为空");
        }

        if (!StringUtils.hasText(request.getTypeName())) {
            return ResultVO.fail(400, "题型名称不能为空");
        }

        String typeName = request.getTypeName().trim();

        // 提前检查同一用户下是否已经存在同名题型。
        if (existsSameTypeName(userId, typeName, null)) {
            return ResultVO.fail(409, "当前用户已存在同名题型");
        }

        QuestionType questionType = QuestionType.builder()
                .userId(userId)
                .typeName(typeName)
                .build();

        int rows = questionTypeMapper.insert(questionType);
        if (rows != 1) {
            return ResultVO.fail(500, "题型保存失败");
        }

        return ResultVO.success(questionType);
    }

    /**
     * 修改题型。
     */
    @Override
    public ResultVO<QuestionType> update(Long userId, Long questionTypeId, QuestionTypeRequest request) {
        // TODO 【需要我实现：题型修改业务，摘要：
        // 1. 校验 questionTypeId 和 typeName；
        // 2. 调用 questionTypeMapper.updateByUserIdAndQuestionTypeId，SQL 中必须同时匹配 userId。】

        if (userId == null || questionTypeId == null || request == null) {
            return ResultVO.fail(400, "用户ID、题型ID和请求参数不能为空");
        }

        if (!StringUtils.hasText(request.getTypeName())) {
            return ResultVO.fail(400, "题型名称不能为空");
        }

        // 先确认题型属于当前用户。
        QuestionType existing = questionTypeMapper.selectByUserIdAndQuestionTypeId(userId, questionTypeId);
        if (existing == null) {
            return ResultVO.fail(404, "题型不存在或不属于当前用户");
        }

        String typeName = request.getTypeName().trim();

        if (existsSameTypeName(userId, typeName, questionTypeId)) {
            return ResultVO.fail(409, "当前用户已存在同名题型");
        }

        int rows = questionTypeMapper.updateByUserIdAndQuestionTypeId(userId, questionTypeId, typeName);
        if (rows != 1) {
            return ResultVO.fail(500, "题型更新失败");
        }

        QuestionType updated = questionTypeMapper.selectByUserIdAndQuestionTypeId(userId, questionTypeId);
        return ResultVO.success(updated);
    }

    /**
     * 删除题型。
     */
    @Override
    public ResultVO<Void> delete(Long userId, Long questionTypeId) {
        // TODO 【需要我实现：题型删除业务，摘要：
        // 1. 判断该题型下是否已有错题；
        // 2. 根据业务规则决定拒绝删除、级联删除或解绑；
        // 3. 调用 questionTypeMapper.deleteByUserIdAndQuestionTypeId，SQL 中必须同时匹配 userId。】

        if (userId == null || questionTypeId == null) {
            return ResultVO.fail(400, "用户ID和题型ID不能为空");
        }

        QuestionType existing = questionTypeMapper.selectByUserIdAndQuestionTypeId(userId, questionTypeId);
        if (existing == null) {
            return ResultVO.fail(404, "题型不存在或不属于当前用户");
        }

        // 当前业务规则：如果题型下已有错题，则不允许直接删除。
        List<Mistake> mistakes = mistakeMapper.selectByUserId(userId);
        boolean hasMistake = mistakes != null && mistakes.stream()
                .anyMatch(mistake -> questionTypeId.equals(mistake.getQuestionTypeId()));

        if (hasMistake) {
            return ResultVO.fail(400, "该题型下已有错题，不能删除");
        }

        int rows = questionTypeMapper.deleteByUserIdAndQuestionTypeId(userId, questionTypeId);
        if (rows != 1) {
            return ResultVO.fail(500, "题型删除失败");
        }

        return ResultVO.success();
    }

    /**
     * 检查当前用户下是否已经存在同名题型。
     *
     * @param excludeQuestionTypeId 修改时排除当前题型本身
     */
    private boolean existsSameTypeName(Long userId, String typeName, Long excludeQuestionTypeId) {
        List<QuestionType> questionTypes = questionTypeMapper.selectByUserId(userId);
        if (questionTypes == null) {
            return false;
        }

        for (QuestionType questionType : questionTypes) {
            if (excludeQuestionTypeId != null && excludeQuestionTypeId.equals(questionType.getQuestionTypeId())) {
                continue;
            }

            if (typeName.equalsIgnoreCase(questionType.getTypeName())) {
                return true;
            }
        }

        return false;
    }
}
