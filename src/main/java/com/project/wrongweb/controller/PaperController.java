package com.project.wrongweb.controller;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.PaperGenerateRequest;
import com.project.wrongweb.service.PaperService;
import com.project.wrongweb.vo.PaperDetailVO;
import com.project.wrongweb.vo.PaperListItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 随机组卷与历史试卷接口。
 */
@RestController
@RequestMapping("/api/paper")
@RequiredArgsConstructor
public class PaperController {

    private final PaperService paperService;

    /**
     * 生成随机试卷并永久保存。
     *
     * <p>请求方式：POST</p>
     * <p>接口地址：/api/paper/generate</p>
     *
     * <p>请求头：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，Long，必填</li>
     * </ul>
     *
     * <p>请求体字段：</p>
     * <ul>
     *   <li>subjectId：选定的抽题科目ID，Long，必填</li>
     *   <li>totalCount：总题数，Integer，非必填，默认 22</li>
     *   <li>choiceCount：选择题数量，Integer，非必填，默认 12</li>
     *   <li>blankCount：填空题数量，Integer，非必填，默认 5</li>
     *   <li>solveCount：解答题数量，Integer，非必填，默认 5</li>
     * </ul>
     *
     * <p>组卷规则：按单一科目组卷，不能跨科目抽题；从当前用户选定科目下的错题池随机抽题。</p>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "paperId": 1,
     *     "userId": 1,
     *     "subjectId": 1,
     *     "totalCount": 22,
     *     "choiceCount": 12,
     *     "blankCount": 5,
     *     "solveCount": 5,
     *     "status": 0,
     *     "questions": [
     *       {
     *         "mistakeId": 1,
     *         "subjectId": 1,
     *         "questionTypeId": 1,
     *         "questionContent": "题目内容",
     *         "knowledgePoint": "导数",
     *         "wrongAnswer": null,
     *         "correctAnswer": null,
     *         "analysis": null
     *       }
     *     ],
     *     "createdAt": "2026-10-01 10:00:00",
     *     "submittedAt": null
     *   },
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>data.paperId：试卷ID，永久保存后可通过该ID回看</li>
     *   <li>data.userId：所属用户ID</li>
     *   <li>data.subjectId：抽题科目ID</li>
     *   <li>data.totalCount：总题数</li>
     *   <li>data.choiceCount：选择题数量</li>
     *   <li>data.blankCount：填空题数量</li>
     *   <li>data.solveCount：解答题数量</li>
     *   <li>data.status：试卷状态，0-未提交，1-已提交</li>
     *   <li>data.questions：试卷题目列表</li>
     *   <li>data.questions[].mistakeId：原错题ID</li>
     *   <li>data.questions[].subjectId：科目ID</li>
     *   <li>data.questions[].questionTypeId：题型ID</li>
     *   <li>data.questions[].questionContent：题干内容</li>
     *   <li>data.questions[].knowledgePoint：知识点</li>
     *   <li>data.questions[].wrongAnswer：错误答案，未提交时返回 null</li>
     *   <li>data.questions[].correctAnswer：正确答案，未提交时返回 null</li>
     *   <li>data.questions[].analysis：解题步骤与过程，支持 LaTeX，未提交时返回 null</li>
     *   <li>data.createdAt：创建时间</li>
     *   <li>data.submittedAt：提交时间，未提交时为 null</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @param request 随机组卷请求
     * @return 已保存的试卷详情，未提交状态不返回答案
     */
    @PostMapping("/generate")
    public ResultVO<PaperDetailVO> generate(@RequestHeader("X-User-Id") Long userId,
                                            @RequestBody PaperGenerateRequest request) {
        return paperService.generate(userId, request);
    }

    /**
     * 查询当前用户所有历史试卷列表。
     *
     * <p>请求方式：GET</p>
     * <p>接口地址：/api/paper/list</p>
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
     *       "paperId": 1,
     *       "userId": 1,
     *       "subjectId": 1,
     *       "totalCount": 22,
     *       "choiceCount": 12,
     *       "blankCount": 5,
     *       "solveCount": 5,
     *       "status": 0,
     *       "createdAt": "2026-10-01 10:00:00",
     *       "submittedAt": null
     *     }
     *   ],
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>data[].paperId：试卷ID</li>
     *   <li>data[].userId：所属用户ID</li>
     *   <li>data[].subjectId：抽题科目ID</li>
     *   <li>data[].totalCount：总题数</li>
     *   <li>data[].choiceCount：选择题数量</li>
     *   <li>data[].blankCount：填空题数量</li>
     *   <li>data[].solveCount：解答题数量</li>
     *   <li>data[].status：试卷状态，0-未提交，1-已提交</li>
     *   <li>data[].createdAt：创建时间</li>
     *   <li>data[].submittedAt：提交时间，未提交时为 null</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @return 当前用户历史试卷列表
     */
    @GetMapping("/list")
    public ResultVO<List<PaperListItemVO>> list(@RequestHeader("X-User-Id") Long userId) {
        return paperService.list(userId);
    }

    /**
     * 根据试卷ID查看试卷详情。
     *
     * <p>请求方式：GET</p>
     * <p>接口地址：/api/paper/{paperId}</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     *   <li>paperId：试卷ID，路径参数，Long，必填</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "paperId": 1,
     *     "userId": 1,
     *     "subjectId": 1,
     *     "totalCount": 22,
     *     "choiceCount": 12,
     *     "blankCount": 5,
     *     "solveCount": 5,
     *     "status": 1,
     *     "questions": [
     *       {
     *         "mistakeId": 1,
     *         "subjectId": 1,
     *         "questionTypeId": 1,
     *         "questionContent": "题目内容",
     *         "knowledgePoint": "导数",
     *         "wrongAnswer": "错误答案",
     *         "correctAnswer": "正确答案",
     *         "analysis": "解题步骤与过程，支持 LaTeX"
     *       }
     *     ],
     *     "createdAt": "2026-10-01 10:00:00",
     *     "submittedAt": "2026-10-01 10:10:00"
     *   },
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>data.paperId：试卷ID</li>
     *   <li>data.userId：所属用户ID</li>
     *   <li>data.subjectId：抽题科目ID</li>
     *   <li>data.totalCount：总题数</li>
     *   <li>data.choiceCount：选择题数量</li>
     *   <li>data.blankCount：填空题数量</li>
     *   <li>data.solveCount：解答题数量</li>
     *   <li>data.status：试卷状态，0-未提交，1-已提交</li>
     *   <li>data.questions[].wrongAnswer：错误答案，status=0 时为 null，status=1 时返回</li>
     *   <li>data.questions[].correctAnswer：正确答案，status=0 时为 null，status=1 时返回</li>
     *   <li>data.questions[].analysis：解题步骤与过程，支持 LaTeX，status=0 时为 null，status=1 时返回</li>
     *   <li>data.createdAt：创建时间</li>
     *   <li>data.submittedAt：提交时间，未提交时为 null</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @param paperId 试卷ID
     * @return 试卷详情；未提交时隐藏答案，提交后返回完整题目和答案
     */
    @GetMapping("/{paperId}")
    public ResultVO<PaperDetailVO> detail(@RequestHeader("X-User-Id") Long userId,
                                          @PathVariable Long paperId) {
        return paperService.detail(userId, paperId);
    }

    /**
     * 提交试卷。
     *
     * <p>请求方式：POST</p>
     * <p>接口地址：/api/paper/{paperId}/submit</p>
     *
     * <p>入参：</p>
     * <ul>
     *   <li>X-User-Id：当前用户ID，请求头，Long，必填</li>
     *   <li>paperId：试卷ID，路径参数，Long，必填</li>
     * </ul>
     *
     * <p>提交后试卷状态从 0 变为 1，再次打开详情即可查看完整题目和答案。</p>
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
     *   <li>data：提交操作无业务数据，固定为 null</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param userId 当前用户ID
     * @param paperId 试卷ID
     * @return 提交结果，data 为 null
     */
    @PostMapping("/{paperId}/submit")
    public ResultVO<Void> submit(@RequestHeader("X-User-Id") Long userId,
                                 @PathVariable Long paperId) {
        return paperService.submit(userId, paperId);
    }
}
