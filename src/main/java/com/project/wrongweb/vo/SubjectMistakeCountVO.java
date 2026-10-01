package com.project.wrongweb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 首页按科目统计错题数量。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectMistakeCountVO {

    /** 科目ID。 */
    private Long subjectId;

    /** 科目名称。 */
    private String subjectName;

    /** 该科目下错题数量。 */
    private Long mistakeCount;
}
