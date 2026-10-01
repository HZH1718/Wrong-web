package com.project.wrongweb.dto;

import lombok.Data;

/**
 * 随机组卷请求。
 */
@Data
public class PaperGenerateRequest {

    /** 选定的抽题科目ID，必填。 */
    private Long subjectId;

    /** 总题数，非必填，默认 22。 */
    private Integer totalCount;

    /** 选择题数量，非必填，默认 12。 */
    private Integer choiceCount;

    /** 填空题数量，非必填，默认 5。 */
    private Integer blankCount;

    /** 解答题数量，非必填，默认 5。 */
    private Integer solveCount;
}
