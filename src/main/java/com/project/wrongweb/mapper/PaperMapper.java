package com.project.wrongweb.mapper;

import com.project.wrongweb.entity.Paper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 试卷 Mapper。
 */
@Mapper
public interface PaperMapper {

    /**
     * 新增试卷，试卷题目快照会永久保存。
     */
    // TODO 【需要我实现：试卷新增 SQL，摘要：向 paper 插入 userId、subjectId、各类题量、状态和 questionSnapshot JSON】
    @Insert("insert into paper (user_id, subject_id, total_count, choice_count, blank_count, solve_count, status, question_snapshot) "
            + "VALUES(#{userId}, #{subjectId}, #{totalCount}, #{choiceCount}, #{blankCount}, #{solveCount}, #{status}, #{questionSnapshot})")
    @Options(useGeneratedKeys = true, keyProperty = "paperId")
    int insert(Paper paper);

    /**
     * 查询当前用户全部历史试卷列表。
     */
    // TODO 【需要我实现：历史试卷列表 SQL，摘要：按 userId 查询 paper，可按创建时间倒序；列表可不返回 questionSnapshot 大字段】
    @Select("select paper_id, user_id, subject_id, total_count, choice_count, blank_count, " +
            "solve_count, status,  created_at, submitted_at, updated_at from paper where user_id = #{userId} order by created_at desc")
    List<Paper> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询当前用户指定科目历史试卷列表。
     */
    // TODO 【需要我实现：按用户和科目查询历史试卷 SQL，摘要：根据 userId 和 subjectId 查询 paper 列表】
    @Select("select paper_id, user_id, subject_id, total_count, choice_count, blank_count, solve_count, status, created_at, submitted_at, updated_at " +
            "from paper where user_id = #{userId} and subject_id  = #{subjectId} order by created_at desc")
    List<Paper> selectByUserIdAndSubjectId(@Param("userId") Long userId,
                                           @Param("subjectId") Long subjectId);

    /**
     * 根据用户和试卷ID查询试卷详情。
     */
    // TODO 【需要我实现：试卷详情 SQL，摘要：根据 paperId 和 userId 查询单张试卷，包含 questionSnapshot 字段】
    @Select("select paper_id, user_id, subject_id, total_count, choice_count, blank_count, solve_count, status, question_snapshot, created_at, submitted_at, updated_at from paper " +
            "where user_id = #{userId} and paper_id = #{paperId} ")
    Paper selectByUserIdAndPaperId(@Param("userId") Long userId,
                                   @Param("paperId") Long paperId);

    /**
     * 提交试卷，将状态从 0 修改为 1，并记录提交时间。
     */
    // TODO 【需要我实现：试卷提交 SQL，摘要：根据 paperId 和 userId 更新 status=1 和 submitted_at=当前时间；若已提交则不应重复更新】
    @Update("update paper set status = 1, submitted_at = now(), updated_at = now() " +
            "where paper_id = #{paperId} and user_id = #{userId} and status = 0")
    int submitByUserIdAndPaperId(@Param("userId") Long userId,
                                 @Param("paperId") Long paperId);
}
