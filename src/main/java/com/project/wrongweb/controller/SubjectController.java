package com.project.wrongweb.controller;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.SubjectRequest;
import com.project.wrongweb.entity.Subject;
import com.project.wrongweb.service.SubjectService;
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
 * 科目管理接口。
 */
@RestController
@RequestMapping("/api/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * 查询当前用户全部科目。
     *
     * <p>请求方式：GET</p>
     * <p>接口地址：/api/subject/list</p>
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
     *       "subjectId": 1,
     *       "userId": 1,
     *       "subjectName": "高数",
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
     *   <li>code：状态码，200 表示成功</li>
     *   <li>message：提示信息</li>
     *   <li>data：科目数组</li>
     *   <li>data[].subjectId：科目ID</li>
     *   <li>data[].userId：所属用户ID</li>
     *   <li>data[].subjectName：科目名称</li>
     *   <li>data[].createdAt：创建时间</li>
     *   <li>data[].updatedAt：更新时间</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param userId 当前用户ID，来自 X-User-Id 请求头
     * @return 当前用户科目列表
     */
    @GetMapping("/list")
    public ResultVO<List<Subject>> list(@RequestHeader("X-User-Id") Long userId) {
        return subjectService.list(userId);
    }

    /**
     * 新增科目。
     *
     * <p>请求方式：POST</p>
     * <p>接口地址：/api/subject</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     *   <li>subjectName：科目名称，请求体，String，必填，例如高数、线代、概率论</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "subjectId": 1,
     *     "userId": 1,
     *     "subjectName": "高数",
     *     "createdAt": "2026-10-01 10:00:00",
     *     "updatedAt": "2026-10-01 10:00:00"
     *   },
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>data.subjectId：科目ID</li>
     *   <li>data.userId：所属用户ID</li>
     *   <li>data.subjectName：科目名称</li>
     *   <li>data.createdAt：创建时间</li>
     *   <li>data.updatedAt：更新时间</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @param request 科目新增请求
     * @return 新增后的科目
     */
    @PostMapping
    public ResultVO<Subject> create(@RequestHeader("X-User-Id") Long userId,
                                    @RequestBody SubjectRequest request) {
        return subjectService.create(userId, request);
    }

    /**
     * 修改科目。
     *
     * <p>请求方式：PUT</p>
     * <p>接口地址：/api/subject/{subjectId}</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     *   <li>subjectId：科目ID，路径参数，Long，必填</li>
     *   <li>subjectName：科目名称，请求体，String，必填</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "subjectId": 1,
     *     "userId": 1,
     *     "subjectName": "高数",
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
     *   <li>data.subjectId：科目ID</li>
     *   <li>data.userId：所属用户ID</li>
     *   <li>data.subjectName：科目名称</li>
     *   <li>data.createdAt：创建时间</li>
     *   <li>data.updatedAt：更新时间</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @param subjectId 科目ID
     * @param request 科目修改请求
     * @return 修改后的科目
     */
    @PutMapping("/{subjectId}")
    public ResultVO<Subject> update(@RequestHeader("X-User-Id") Long userId,
                                    @PathVariable Long subjectId,
                                    @RequestBody SubjectRequest request) {
        return subjectService.update(userId, subjectId, request);
    }

    /**
     * 删除科目。
     *
     * <p>请求方式：DELETE</p>
     * <p>接口地址：/api/subject/{subjectId}</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     *   <li>subjectId：科目ID，路径参数，Long，必填</li>
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
     * @param subjectId 科目ID
     * @return 删除结果，data 为 null
     */
    @DeleteMapping("/{subjectId}")
    public ResultVO<Void> delete(@RequestHeader("X-User-Id") Long userId,
                                 @PathVariable Long subjectId) {
        return subjectService.delete(userId, subjectId);
    }
}
