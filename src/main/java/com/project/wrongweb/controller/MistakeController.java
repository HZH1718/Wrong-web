package com.project.wrongweb.controller;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.MistakeRequest;
import com.project.wrongweb.entity.Mistake;
import com.project.wrongweb.service.MistakeService;
import com.project.wrongweb.vo.MistakeHomeGroupVO;
import com.project.wrongweb.vo.MistakePageVO;
import com.project.wrongweb.vo.MistakeStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 错题接口。
 */
@RestController
@RequestMapping("/api/mistake")
@RequiredArgsConstructor
public class MistakeController {

    private final MistakeService mistakeService;

    /**
     * 录入错题。
     *
     * <p>请求方式：POST</p>
     * <p>接口地址：/api/mistake</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     *   <li>subjectId：科目ID，请求体，Long，必填</li>
     *   <li>questionTypeId：题型ID，请求体，Long，必填</li>
     *   <li>questionContent：题干内容，请求体，String，必填</li>
     *   <li>wrongAnswer：错误答案，请求体，String，必填</li>
     *   <li>correctAnswer：正确答案，请求体，String，必填</li>
     *   <li>entryTime：录入时间，请求体，String，非必填，不传时服务端取当前时间</li>
     *   <li>knowledgePoint：知识点，请求体，String，非必填</li>
     *   <li>analysis：解题步骤与过程，请求体，String，非必填，支持 LaTeX</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "mistakeId": 1,
     *     "userId": 1,
     *     "subjectId": 1,
     *     "questionTypeId": 1,
     *     "questionContent": "题目内容",
     *     "wrongAnswer": "错误答案",
     *     "correctAnswer": "正确答案",
     *     "entryTime": "2026-10-01 10:00:00",
     *     "knowledgePoint": "导数",
     *     "analysis": "解题步骤与过程，支持 LaTeX",
     *     "createdAt": "2026-10-01 10:00:00",
     *     "updatedAt": "2026-10-01 10:00:00"
     *   },
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>data.mistakeId：错题ID</li>
     *   <li>data.userId：所属用户ID</li>
     *   <li>data.subjectId：科目ID</li>
     *   <li>data.questionTypeId：题型ID</li>
     *   <li>data.questionContent：题干内容</li>
     *   <li>data.wrongAnswer：错误答案</li>
     *   <li>data.correctAnswer：正确答案</li>
     *   <li>data.entryTime：录入时间</li>
     *   <li>data.knowledgePoint：知识点</li>
     *   <li>data.analysis：解题步骤与过程</li>
     *   <li>data.createdAt：创建时间</li>
     *   <li>data.updatedAt：更新时间</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @param request 错题录入请求
     * @return 录入后的错题
     */
    @PostMapping
    public ResultVO<Mistake> create(@RequestHeader("X-User-Id") Long userId,
                                    @RequestBody MistakeRequest request) {
        return mistakeService.create(userId, request);
    }

    /**
     * 修改错题。
     *
     * <p>请求方式：PUT</p>
     * <p>接口地址：/api/mistake/{mistakeId}</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     *   <li>mistakeId：错题ID，路径参数，Long，必填</li>
     *   <li>请求体字段同录入错题接口</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "mistakeId": 1,
     *     "userId": 1,
     *     "subjectId": 1,
     *     "questionTypeId": 1,
     *     "questionContent": "题目内容",
     *     "wrongAnswer": "错误答案",
     *     "correctAnswer": "正确答案",
     *     "entryTime": "2026-10-01 10:00:00",
     *     "knowledgePoint": "导数",
     *     "analysis": "解题步骤与过程，支持 LaTeX",
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
     *   <li>data.mistakeId：错题ID</li>
     *   <li>data.userId：所属用户ID</li>
     *   <li>data.subjectId：科目ID</li>
     *   <li>data.questionTypeId：题型ID</li>
     *   <li>data.questionContent：题干内容</li>
     *   <li>data.wrongAnswer：错误答案</li>
     *   <li>data.correctAnswer：正确答案</li>
     *   <li>data.entryTime：录入时间</li>
     *   <li>data.knowledgePoint：知识点</li>
     *   <li>data.analysis：解题步骤与过程</li>
     *   <li>data.createdAt：创建时间</li>
     *   <li>data.updatedAt：更新时间</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @param mistakeId 错题ID
     * @param request 错题修改请求
     * @return 修改后的错题
     */
    @PutMapping("/{mistakeId}")
    public ResultVO<Mistake> update(@RequestHeader("X-User-Id") Long userId,
                                    @PathVariable Long mistakeId,
                                    @RequestBody MistakeRequest request) {
        return mistakeService.update(userId, mistakeId, request);
    }

    /**
     * 删除错题。
     *
     * <p>请求方式：DELETE</p>
     * <p>接口地址：/api/mistake/{mistakeId}</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     *   <li>mistakeId：错题ID，路径参数，Long，必填</li>
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
     * @param mistakeId 错题ID
     * @return 删除结果，data 为 null
     */
    @DeleteMapping("/{mistakeId}")
    public ResultVO<Void> delete(@RequestHeader("X-User-Id") Long userId,
                                 @PathVariable Long mistakeId) {
        return mistakeService.delete(userId, mistakeId);
    }

    /**
     * 查询错题详情。
     *
     * <p>请求方式：GET</p>
     * <p>接口地址：/api/mistake/{mistakeId}</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     *   <li>mistakeId：错题ID，路径参数，Long，必填</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "mistakeId": 1,
     *     "userId": 1,
     *     "subjectId": 1,
     *     "questionTypeId": 1,
     *     "questionContent": "题目内容",
     *     "wrongAnswer": "错误答案",
     *     "correctAnswer": "正确答案",
     *     "entryTime": "2026-10-01 10:00:00",
     *     "knowledgePoint": "导数",
     *     "analysis": "解题步骤与过程，支持 LaTeX",
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
     *   <li>data.mistakeId：错题ID</li>
     *   <li>data.userId：所属用户ID</li>
     *   <li>data.subjectId：科目ID</li>
     *   <li>data.questionTypeId：题型ID</li>
     *   <li>data.questionContent：题干内容</li>
     *   <li>data.wrongAnswer：错误答案</li>
     *   <li>data.correctAnswer：正确答案</li>
     *   <li>data.entryTime：录入时间</li>
     *   <li>data.knowledgePoint：知识点</li>
     *   <li>data.analysis：解题步骤与过程</li>
     *   <li>data.createdAt：创建时间</li>
     *   <li>data.updatedAt：更新时间</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @param mistakeId 错题ID
     * @return 错题详情
     */
    @GetMapping("/{mistakeId}")
    public ResultVO<Mistake> detail(@RequestHeader("X-User-Id") Long userId,
                                    @PathVariable Long mistakeId) {
        return mistakeService.detail(userId, mistakeId);
    }

    /**
     * 查询当前用户全部错题。
     *
     * <p>请求方式：GET</p>
     * <p>接口地址：/api/mistake/list</p>
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
     *       "mistakeId": 1,
     *       "userId": 1,
     *       "subjectId": 1,
     *       "questionTypeId": 1,
     *       "questionContent": "题目内容",
     *       "wrongAnswer": "错误答案",
     *       "correctAnswer": "正确答案",
     *       "entryTime": "2026-10-01 10:00:00",
     *       "knowledgePoint": "导数",
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
     *   <li>data：错题数组</li>
     *   <li>data[].mistakeId：错题ID</li>
     *   <li>data[].userId：所属用户ID</li>
     *   <li>data[].subjectId：科目ID</li>
     *   <li>data[].questionTypeId：题型ID</li>
     *   <li>data[].questionContent：题干内容</li>
     *   <li>data[].wrongAnswer：错误答案</li>
     *   <li>data[].correctAnswer：正确答案</li>
     *   <li>data[].entryTime：录入时间</li>
     *   <li>data[].knowledgePoint：知识点</li>
     *   <li>data[].analysis：解题步骤与过程</li>
     *   <li>data[].createdAt：创建时间</li>
     *   <li>data[].updatedAt：更新时间</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @return 当前用户错题列表
     */
    @GetMapping("/list")
    public ResultVO<List<Mistake>> list(@RequestHeader("X-User-Id") Long userId) {
        return mistakeService.list(userId);
    }

    /**
     * 分页查询错题接口，支持按科目、题型可选多值筛选。
     *
     * <p>请求方式：GET</p>
     * <p>接口地址：/api/mistake/page</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     *   <li>subjectId：科目ID集合，查询参数，Long，非必填，多个值用英文逗号分隔，
     *       例如 subjectId=1,2；不传或传空表示不过滤科目</li>
     *   <li>questionTypeId：题型ID集合，查询参数，Long，非必填，多个值用英文逗号分隔，
     *       例如 questionTypeId=3,4；不传或传空表示不过滤题型</li>
     *   <li>page：页码，查询参数，Integer，非必填，默认 1，小于 1 时按 1 处理</li>
     *   <li>size：每页数量，查询参数，Integer，非必填，默认 10，最大 100</li>
     * </ul>
     *
     * <p>筛选逻辑：科目与题型均为多选，组内多个取值之间为「或」（IN 查询），
     * 科目组与题型组之间为「与」。两组都不传时返回当前用户全部错题；
     * 只传其中一组时按该组过滤；两组都传时同时满足两组条件。
     * 所有查询均按录入时间倒序排列，并且始终携带 userId 保证用户数据隔离。</p>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "total": 25,
     *     "page": 1,
     *     "size": 10,
     *     "list": [
     *       {
     *         "mistakeId": 1,
     *         "userId": 1,
     *         "subjectId": 1,
     *         "questionTypeId": 1,
     *         "questionContent": "题目内容",
     *         "wrongAnswer": "错误答案",
     *         "correctAnswer": "正确答案",
     *         "entryTime": "2026-10-01 10:00:00",
     *         "knowledgePoint": "导数",
     *         "analysis": "解题步骤与过程，支持 LaTeX",
     *         "createdAt": "2026-10-01 10:00:00",
     *         "updatedAt": "2026-10-01 10:00:00"
     *       }
     *     ]
     *   },
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>code：状态码，200 表示成功</li>
     *   <li>message：提示信息</li>
     *   <li>data.total：满足筛选条件的错题总数量</li>
     *   <li>data.page：当前页码</li>
     *   <li>data.size：每页数量</li>
     *   <li>data.list：当前页错题数组</li>
     *   <li>data.list[].mistakeId：错题ID</li>
     *   <li>data.list[].userId：所属用户ID</li>
     *   <li>data.list[].subjectId：科目ID</li>
     *   <li>data.list[].questionTypeId：题型ID</li>
     *   <li>data.list[].questionContent：题干内容</li>
     *   <li>data.list[].wrongAnswer：错误答案</li>
     *   <li>data.list[].correctAnswer：正确答案</li>
     *   <li>data.list[].entryTime：录入时间</li>
     *   <li>data.list[].knowledgePoint：知识点</li>
     *   <li>data.list[].analysis：解题步骤与过程</li>
     *   <li>data.list[].createdAt：创建时间</li>
     *   <li>data.list[].updatedAt：更新时间</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @param subjectId 科目ID集合，可选，逗号分隔
     * @param questionTypeId 题型ID集合，可选，逗号分隔
     * @param page 页码，可选
     * @param size 每页数量，可选
     * @return 错题分页结果
     */
    @GetMapping("/page")
    public ResultVO<MistakePageVO> page(@RequestHeader("X-User-Id") Long userId,
                                        @RequestParam(value = "subjectId", required = false) List<Long> subjectId,
                                        @RequestParam(value = "questionTypeId", required = false) List<Long> questionTypeId,
                                        @RequestParam(value = "page", required = false) Integer page,
                                        @RequestParam(value = "size", required = false) Integer size) {
        return mistakeService.page(userId, subjectId, questionTypeId, page, size);
    }

    /**
     * 首页统计接口。
     *
     * <p>请求方式：GET</p>
     * <p>接口地址：/api/mistake/statistics</p>
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
     *   "data": {
     *     "totalCount": 10,
     *     "subjectGroups": [
     *       {
     *         "subjectId": 1,
     *         "subjectName": "高数",
     *         "mistakeCount": 6
     *       },
     *       {
     *         "subjectId": 2,
     *         "subjectName": "线代",
     *         "mistakeCount": 4
     *       }
     *     ]
     *   },
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>data.totalCount：当前用户错题总数量</li>
     *   <li>data.subjectGroups：按科目分组的错题数量</li>
     *   <li>data.subjectGroups[].subjectId：科目ID</li>
     *   <li>data.subjectGroups[].subjectName：科目名称</li>
     *   <li>data.subjectGroups[].mistakeCount：该科目错题数量</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @return 首页统计结果
     */
    @GetMapping("/statistics")
    public ResultVO<MistakeStatisticsVO> statistics(@RequestHeader("X-User-Id") Long userId) {
        return mistakeService.statistics(userId);
    }

    /**
     * 首页展示接口。
     *
     * <p>请求方式：GET</p>
     * <p>接口地址：/api/mistake/home-list</p>
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
     *       "subjectName": "高数",
     *       "mistakes": [
     *         {
     *           "mistakeId": 1,
     *           "questionContent": "题目内容",
     *           "entryTime": "2026-10-01 10:00:00"
     *         }
     *       ]
     *     }
     *   ],
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>data[].subjectId：科目ID</li>
     *   <li>data[].subjectName：科目名称</li>
     *   <li>data[].mistakes：该科目下的错题列表，只包含题目和录入时间</li>
     *   <li>data[].mistakes[].mistakeId：错题ID</li>
     *   <li>data[].mistakes[].questionContent：题目内容</li>
     *   <li>data[].mistakes[].entryTime：录入时间</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @return 首页按科目分组的错题列表
     */
    @GetMapping("/home-list")
    public ResultVO<List<MistakeHomeGroupVO>> homeList(@RequestHeader("X-User-Id") Long userId) {
        return mistakeService.homeList(userId);
    }
}
