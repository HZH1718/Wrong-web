package com.project.wrongweb.service;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.PaperGenerateRequest;
import com.project.wrongweb.vo.PaperDetailVO;
import com.project.wrongweb.vo.PaperListItemVO;

import java.util.List;

/**
 * 试卷服务。
 */
public interface PaperService {

    /**
     * 随机组卷并永久保存。
     */
    ResultVO<PaperDetailVO> generate(Long userId, PaperGenerateRequest request);

    /**
     * 查询当前用户所有历史试卷列表。
     */
    ResultVO<List<PaperListItemVO>> list(Long userId);

    /**
     * 查询试卷详情。
     */
    ResultVO<PaperDetailVO> detail(Long userId, Long paperId);

    /**
     * 提交试卷。
     */
    ResultVO<Void> submit(Long userId, Long paperId);
}
