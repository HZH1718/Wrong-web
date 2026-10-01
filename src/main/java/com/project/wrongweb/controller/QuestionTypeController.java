package com.project.wrongweb.controller;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.QuestionTypeRequest;
import com.project.wrongweb.entity.QuestionType;
import com.project.wrongweb.service.QuestionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 题型管理接口。
 */
@RestController
@RequestMapping("/api/question-type")
@RequiredArgsConstructor
public class QuestionTypeController {

    private final QuestionTypeService questionTypeService;

    /**
     * 查询当前用户全部题型。
     *
     * <p>请求方式：GET</p>
     * <p>接口地址：/api/question-type/list</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": [
     *     {
     *       "questionTypeId": 1,
     *       "userId": 1,
     *       "typeName": "选择",
     *       "createdAt": "2026-10-01 10:00:00",
     *       "updatedAt": "2026-10-01 10:00:00"
     *     }
     *   ],
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>data[].questionTypeId：题型ID</li>
     *   <li>data[].userId：所属用户ID</li>
     *   <li>data[].typeName：题型名称</li>
     *   <li>data[].createdAt：创建时间</li>
     *   <li>data[].updatedAt：更新时间</li>
     * </ul>
     *
     * @param userId 当前用户ID，来自 X-User-Id 请求头
     * @return 当前用户题型列表
     */
    @GetMapping("/list")
    public ResultVO<List<QuestionType>> list(@RequestHeader("X-User-Id") Long userId) {
        return questionTypeService.list(userId);
    }

    /**
     * 新增题型。
     *
     * <p>请求方式：POST</p>
     * <p>接口地址：/api/question-type</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     *   <li>typeName：题型名称，请求体，String，必填，例如选择、填空、解答</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "questionTypeId": 1,
     *     "userId": 1,
     *     "typeName": "选择",
     *     "createdAt": "2026-10-01 10:00:00",
     *     "updatedAt": "2026-10-01 10:00:00"
     *   },
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>code：状态码，200 表示成功</li>
     *   <li>message：提示信息</li>
     *   <li>data.questionTypeId：题型ID</li>
     *   <li>data.userId：所属用户ID</li>
     *   <li>data.typeName：题型名称</li>
     *   <li>data.createdAt：创建时间</li>
     *   <li>data.updatedAt：更新时间</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @param request 题型新增请求
     * @return 新增后的题型
     */
    @PostMapping
    public ResultVO<QuestionType> create(@RequestHeader("X-User-Id") Long userId,
                                         @RequestBody QuestionTypeRequest request) {
        return questionTypeService.create(userId, request);
    }

    /**
     * 修改题型。
     *
     * <p>请求方式：PUT</p>
     * <p>接口地址：/api/question-type/{questionTypeId}</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     *   <li>questionTypeId：题型ID，路径参数，Long，必填</li>
     *   <li>typeName：题型名称，请求体，String，必填</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "questionTypeId": 1,
     *     "userId": 1,
     *     "typeName": "选择",
     *     "createdAt": "2026-10-01 10:00:00",
     *     "updatedAt": "2026-10-01 10:10:00"
     *   },
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>code：状态码，200 表示成功</li>
     *   <li>message：提示信息</li>
     *   <li>data.questionTypeId：题型ID</li>
     *   <li>data.userId：所属用户ID</li>
     *   <li>data.typeName：题型名称</li>
     *   <li>data.createdAt：创建时间</li>
     *   <li>data.updatedAt：更新时间</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @param questionTypeId 题型ID
     * @param request 题型修改请求
     * @return 修改后的题型
     */
    @PutMapping("/{questionTypeId}")
    public ResultVO<QuestionType> update(@RequestHeader("X-User-Id") Long userId,
                                         @PathVariable Long questionTypeId,
                                         @RequestBody QuestionTypeRequest request) {
        return questionTypeService.update(userId, questionTypeId, request);
    }

    /**
     * 删除题型。
     *
     * <p>请求方式：DELETE</p>
     * <p>接口地址：/api/question-type/{questionTypeId}</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     *   <li>questionTypeId：题型ID，路径参数，Long，必填</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": null,
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>code：状态码，200 表示成功</li>
     *   <li>message：提示信息</li>
     *   <li>data：删除操作无业务数据，固定为 null</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @param questionTypeId 题型ID
     * @return 删除结果，data 为 null
     */
    @DeleteMapping("/{questionTypeId}")
    public ResultVO<Void> delete(@RequestHeader("X-User-Id") Long userId,
                                 @PathVariable Long questionTypeId) {
        return questionTypeService.delete(userId, questionTypeId);
    }
}
