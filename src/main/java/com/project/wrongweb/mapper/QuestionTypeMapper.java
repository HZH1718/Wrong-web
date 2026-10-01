package com.project.wrongweb.mapper;

import com.project.wrongweb.entity.QuestionType;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 题型 Mapper。
 */
@Mapper
public interface QuestionTypeMapper {

    /**
     * 新增题型。
     */
    // TODO 【需要我实现：题型新增 SQL，摘要：向 question_type 插入 userId 和 typeName，并处理同一用户题型重名唯一约束】
    @Insert("INSERT INTO question_type(user_id, type_name) " +
            "VALUES(#{userId}, #{typeName})")
    @Options(useGeneratedKeys = true, keyProperty = "questionTypeId")
    int insert(QuestionType questionType);

    /**
     * 按用户和题型ID修改题型。
     */
    // TODO 【需要我实现：题型修改 SQL，摘要：根据 questionTypeId 和 userId 更新 type_name，只允许修改自己的题型】
    @Update("UPDATE question_type SET type_name = #{typeName}, updated_at = NOW() " +
            "WHERE question_type_id = #{questionTypeId} AND user_id = #{userId}")
    int updateByUserIdAndQuestionTypeId(@Param("userId") Long userId,
                                        @Param("questionTypeId") Long questionTypeId,
                                        @Param("typeName") String typeName);

    /**
     * 按用户和题型ID删除题型。
     */
    // TODO 【需要我实现：题型删除 SQL，摘要：根据 questionTypeId 和 userId 删除题型；若已有错题，需要先判断是否允许删除或级联处理】
    @Delete("DELETE FROM question_type WHERE question_type_id = #{questionTypeId} AND user_id = #{userId}")
    int deleteByUserIdAndQuestionTypeId(@Param("userId") Long userId,
                                        @Param("questionTypeId") Long questionTypeId);

    /**
     * 按用户和题型ID查询题型。
     */
    // TODO 【需要我实现：题型详情 SQL，摘要：根据 questionTypeId 和 userId 查询单条题型】
    @Select("SELECT question_type_id, user_id, type_name, created_at, updated_at " +
            "FROM question_type WHERE question_type_id = #{questionTypeId} AND user_id = #{userId}")
    QuestionType selectByUserIdAndQuestionTypeId(@Param("userId") Long userId,
                                                 @Param("questionTypeId") Long questionTypeId);

    /**
     * 查询当前用户全部题型。
     */
    // TODO 【需要我实现：题型列表 SQL，摘要：按 userId 查询题型列表，可按创建时间正序或名称排序】
    @Select("SELECT question_type_id, user_id, type_name, created_at, updated_at " +
            "FROM question_type WHERE user_id = #{userId} ORDER BY created_at ASC")
    List<QuestionType> selectByUserId(@Param("userId") Long userId);
}
