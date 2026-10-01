package com.project.wrongweb.mapper;

import com.project.wrongweb.entity.Subject;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 科目 Mapper。
 */
@Mapper
public interface SubjectMapper {

    /**
     * 新增科目。
     */
    // TODO 【需要我实现：科目新增 SQL，摘要：向 subject 插入 userId 和 subjectName，并处理同一用户科目重名唯一约束】
    @Insert("INSERT INTO subject(user_id, subject_name) " +
            "VALUES(#{userId}, #{subjectName})")
    @Options(useGeneratedKeys = true, keyProperty = "subjectId")
    int insert(Subject subject);

    /**
     * 按用户和科目ID修改科目。
     */
    // TODO 【需要我实现：科目修改 SQL，摘要：根据 subjectId 和 userId 更新 subject_name，只允许修改自己的科目】
    @Update("UPDATE subject SET subject_name = #{subjectName}, updated_at = NOW() " +
            "WHERE subject_id = #{subjectId} AND user_id = #{userId}")
    int updateByUserIdAndSubjectId(@Param("userId") Long userId,
                                   @Param("subjectId") Long subjectId,
                                   @Param("subjectName") String subjectName);

    /**
     * 按用户和科目ID删除科目。
     */
    // TODO 【需要我实现：科目删除 SQL，摘要：根据 subjectId 和 userId 删除科目；若已有错题，需要先判断是否允许删除或级联处理】
    @Delete("DELETE FROM subject WHERE subject_id = #{subjectId} AND user_id = #{userId}")
    int deleteByUserIdAndSubjectId(@Param("userId") Long userId,
                                   @Param("subjectId") Long subjectId);

    /**
     * 按用户和科目ID查询科目。
     */
    // TODO 【需要我实现：科目详情 SQL，摘要：根据 subjectId 和 userId 查询单条科目】
    @Select("SELECT subject_id, user_id, subject_name, created_at, updated_at " +
            "FROM subject WHERE subject_id = #{subjectId} AND user_id = #{userId}")
    Subject selectByUserIdAndSubjectId(@Param("userId") Long userId,
                                       @Param("subjectId") Long subjectId);

    /**
     * 查询当前用户全部科目。
     */
    // TODO 【需要我实现：科目列表 SQL，摘要：按 userId 查询科目列表，可按创建时间正序或名称排序】
    @Select("SELECT subject_id, user_id, subject_name, created_at, updated_at " +
            "FROM subject WHERE user_id = #{userId} ORDER BY created_at ASC")
    List<Subject> selectByUserId(@Param("userId") Long userId);
}
