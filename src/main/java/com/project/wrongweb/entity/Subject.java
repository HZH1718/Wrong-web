package com.project.wrongweb.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 科目实体，对应表 subject。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    /** 科目ID。 */
    private Long subjectId;

    /** 所属用户ID，数据隔离字段。 */
    private Long userId;

    /** 科目名称，例如：高数、线代、概率论。 */
    private String subjectName;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 更新时间。 */
    private LocalDateTime updatedAt;
}
