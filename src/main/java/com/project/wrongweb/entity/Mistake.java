package com.project.wrongweb.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 错题实体，对应表 mistake。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mistake {

    /** 错题ID。 */
    private Long mistakeId;

    /** 所属用户ID，数据隔离字段。 */
    private Long userId;

    /** 所属科目ID。 */
    private Long subjectId;

    /** 所属题型ID。 */
    private Long questionTypeId;

    /** 题干内容。 */
    private String questionContent;

    /** 错误答案。 */
    private String wrongAnswer;

    /** 正确答案。 */
    private String correctAnswer;

    /** 录入时间。 */
    private LocalDateTime entryTime;

    /** 知识点。 */
    private String knowledgePoint;

    /** 解题步骤与过程，支持 LaTeX。 */
    private String analysis;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 更新时间。 */
    private LocalDateTime updatedAt;
}
