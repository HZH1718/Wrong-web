package com.project.wrongweb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 试卷详情。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaperDetailVO {

    /** 试卷ID。 */
    private Long paperId;

    /** 所属用户ID。 */
    private Long userId;

    /** 抽题科目ID。 */
    private Long subjectId;

    /** 总题数。 */
    private Integer totalCount;

    /** 选择题数量。 */
    private Integer choiceCount;

    /** 填空题数量。 */
    private Integer blankCount;

    /** 解答题数量。 */
    private Integer solveCount;

    /** 试卷状态：0-未提交，1-已提交。 */
    private Integer status;

    /** 题目列表。 */
    private List<PaperQuestionVO> questions;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 提交时间，未提交时为 null。 */
    private LocalDateTime submittedAt;
}
