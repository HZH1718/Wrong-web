package com.project.wrongweb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 试卷题目详情。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaperQuestionVO {

    /** 错题ID。 */
    private Long mistakeId;

    /** 科目ID。 */
    private Long subjectId;

    /** 题型ID。 */
    private Long questionTypeId;

    /** 题干内容。 */
    private String questionContent;

    /** 知识点。 */
    private String knowledgePoint;

    /** 错误答案，试卷未提交时不返回。 */
    private String wrongAnswer;

    /** 正确答案，试卷未提交时不返回。 */
    private String correctAnswer;

    /** 解题步骤与过程，试卷未提交时不返回，支持 LaTeX。 */
    private String analysis;
}
