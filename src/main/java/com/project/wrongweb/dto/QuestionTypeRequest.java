package com.project.wrongweb.dto;

import lombok.Data;

/**
 * 题型新增或修改请求。
 */
@Data
public class QuestionTypeRequest {

    /** 题型名称，例如：选择、填空、解答，必填。 */
    private String typeName;
}
