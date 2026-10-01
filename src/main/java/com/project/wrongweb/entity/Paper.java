package com.project.wrongweb.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 试卷实体，对应表 paper。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paper {

    /** 试卷ID。 */
    private Long paperId;

    /** 所属用户ID，数据隔离字段。 */
    private Long userId;

    /** 抽题科目ID，试卷按单一科目生成。 */
    private Long subjectId;

    /** 总题数。 */
    private Integer totalCount;

    /** 选择题数量。 */
    private Integer choiceCount;

    /** 填空题数量。 */
    private Integer blankCount;

    /** 解答题数量。 */
    private Integer solveCount;

    /**
     * 试卷状态：0-未提交，1-已提交。
     */
    private Integer status;

    /**
     * 试卷题目快照 JSON，永久保存抽题时的题目内容与答案，避免后续错题编辑影响历史试卷。
     */
    private String questionSnapshot;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 提交时间，未提交时为 null。 */
    private LocalDateTime submittedAt;

    /** 更新时间。 */
    private LocalDateTime updatedAt;
}
