package com.project.wrongweb.vo;

import com.project.wrongweb.entity.Mistake;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 错题分页查询结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MistakePageVO {

    /** 满足筛选条件的错题总数量。 */
    private Long total;

    /** 当前页码，从 1 开始。 */
    private Integer page;

    /** 每页数量。 */
    private Integer size;

    /** 当前页错题列表。 */
    private List<Mistake> list;
}
