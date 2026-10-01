package com.project.wrongweb.dto;

import lombok.Data;

/**
 * 科目新增或修改请求。
 */
@Data
public class SubjectRequest {

    /** 科目名称，例如：高数、线代、概率论，必填。 */
    private String subjectName;
}
