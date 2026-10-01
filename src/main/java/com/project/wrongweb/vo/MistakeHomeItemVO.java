package com.project.wrongweb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 首页错题列表项，只返回题目和录入时间。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MistakeHomeItemVO {

    /** 错题ID。 */
    private Long mistakeId;

    /** 题干内容。 */
    private String questionContent;

    /** 录入时间。 */
    private LocalDateTime entryTime;
}
