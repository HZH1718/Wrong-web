package com.project.wrongweb.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 题型实体，对应表 question_type。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionType {

    /** 题型ID。 */
    private Long questionTypeId;

    /** 所属用户ID，数据隔离字段。 */
    private Long userId;

    /** 题型名称，例如：选择、填空、解答。 */
    private String typeName;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 更新时间。 */
    private LocalDateTime updatedAt;
}
