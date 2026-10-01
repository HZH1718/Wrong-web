package com.project.wrongweb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 首页统计结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MistakeStatisticsVO {

    /** 当前用户错题总数量。 */
    private Long totalCount;

    /** 按科目分组的错题数量。 */
    private List<SubjectMistakeCountVO> subjectGroups;
}
