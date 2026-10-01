package com.project.wrongweb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 首页按科目分组的错题列表。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MistakeHomeGroupVO {

    /** 科目ID。 */
    private Long subjectId;

    /** 科目名称。 */
    private String subjectName;

    /** 该科目下的错题列表。 */
    private List<MistakeHomeItemVO> mistakes;
}
