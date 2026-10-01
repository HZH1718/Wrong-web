package com.project.wrongweb.service;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.MistakeRequest;
import com.project.wrongweb.entity.Mistake;
import com.project.wrongweb.vo.MistakeHomeGroupVO;
import com.project.wrongweb.vo.MistakePageVO;
import com.project.wrongweb.vo.MistakeStatisticsVO;

import java.util.List;

/**
 * 错题服务。
 */
public interface MistakeService {

    /**
     * 录入错题。
     */
    ResultVO<Mistake> create(Long userId, MistakeRequest request);

    /**
     * 修改错题。
     */
    ResultVO<Mistake> update(Long userId, Long mistakeId, MistakeRequest request);

    /**
     * 删除错题。
     */
    ResultVO<Void> delete(Long userId, Long mistakeId);

    /**
     * 查询错题详情。
     */
    ResultVO<Mistake> detail(Long userId, Long mistakeId);

    /**
     * 查询当前用户全部错题。
     */
    ResultVO<List<Mistake>> list(Long userId);

    /**
     * 分页查询当前用户错题，支持按科目、题型可选多值筛选。
     */
    ResultVO<MistakePageVO> page(Long userId, List<Long> subjectIds, List<Long> questionTypeIds, Integer page, Integer size);

    /**
     * 首页统计：当前用户错题总数与按科目分组统计。
     */
    ResultVO<MistakeStatisticsVO> statistics(Long userId);

    /**
     * 首页展示：按科目分组返回错题列表，只返回题目和录入时间。
     */
    ResultVO<List<MistakeHomeGroupVO>> homeList(Long userId);
}
