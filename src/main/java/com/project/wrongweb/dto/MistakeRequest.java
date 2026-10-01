package com.project.wrongweb.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 错题录入或修改请求。
 */
@Data
public class MistakeRequest {

    /** 科目ID，必填。 */
    private Long subjectId;

    /** 题型ID，必填。 */
    private Long questionTypeId;

    /** 题干内容，必填。 */
    private String questionContent;

    /** 错误答案，必填。 */
    private String wrongAnswer;

    /** 正确答案，必填。 */
    private String correctAnswer;

    /** 录入时间，非必填，不传时服务端取当前时间。 */
    private LocalDateTime entryTime;

    /** 知识点，非必填。 */
    private String knowledgePoint;

    /** 解题步骤与过程，非必填，支持 LaTeX。 */
    private String analysis;
}
