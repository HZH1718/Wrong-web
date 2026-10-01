package com.project.wrongweb.mapper;

import com.project.wrongweb.entity.Mistake;
import org.apache.ibatis.annotations.*;


import java.util.List;
import java.util.Map;

/**
 * 错题 Mapper。
 */
@Mapper
public interface MistakeMapper {

    /**
     * 新增错题。
     */
    // TODO 【需要我实现：错题新增 SQL，摘要：向 mistake 表插入题目、答案、科目、题型、知识点等字段，并保证 userId 来自当前登录用户】
    @Insert("insert into mistake (user_id, subject_id, question_type_id, question_content, wrong_answer, " +
            "correct_answer, entry_time, knowledge_point, analysis)" +
            "values (#{userId},#{subjectId},#{questionTypeId},#{questionContent},#{wrongAnswer}," +
            "#{correctAnswer},#{entryTime},#{knowledgePoint},#{analysis})")
    @Options(useGeneratedKeys = true,keyProperty = "mistakeId")
    int insert(Mistake mistake);

    /**
     * 按用户和错题ID修改错题，只允许修改自己的错题。
     */
    // TODO 【需要我实现：错题修改 SQL，摘要：根据 mistakeId 和 userId 更新题目、错误答案、正确答案、知识点等字段】
    @Update("update mistake set " +
            "subject_id = #{mistake.subjectId}, " +
            "question_type_id = #{mistake.questionTypeId}, " +
            "question_content = #{mistake.questionContent}, " +
            "wrong_answer = #{mistake.wrongAnswer}, " +
            "correct_answer = #{mistake.correctAnswer}, " +
            "entry_time = #{mistake.entryTime}, " +
            "knowledge_point = #{mistake.knowledgePoint}, " +
            "analysis = #{mistake.analysis}, " +
            "updated_at = NOW() " +
            "where mistake_id = #{mistakeId} and user_id = #{userId}")
    int updateByUserIdAndMistakeId(@Param("userId") Long userId,
                                   @Param("mistakeId") Long mistakeId,
                                   @Param("mistake") Mistake mistake);

    /**
     * 按用户和错题ID删除错题，只允许删除自己的错题。
     */
    // TODO 【需要我实现：错题删除 SQL，摘要：根据 mistakeId 和 userId 删除错题，防止跨用户删除】
    @Delete("delete from mistake where user_id = #{userId} and mistake_id = #{mistakeId}")
    int deleteByUserIdAndMistakeId(@Param("userId") Long userId,
                                   @Param("mistakeId") Long mistakeId);

    /**
     * 按用户和错题ID查询错题详情。
     */
    // TODO 【需要我实现：错题详情 SQL，摘要：根据 mistakeId 和 userId 查询单条错题，未匹配返回 null】
    @Select("select * from mistake where mistake_id = #{mistakeId} and user_id = #{userId}")
    Mistake selectByUserIdAndMistakeId(@Param("userId") Long userId,
                                       @Param("mistakeId") Long mistakeId);

    /**
     * 查询当前用户全部错题。
     */
    // TODO 【需要我实现：当前用户错题列表 SQL，摘要：按 userId 查询错题，可按录入时间倒序】
    @Select("select * from mistake where user_id = #{userId} order by entry_time desc")
    List<Mistake> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询当前用户指定科目下的错题。
     */
    // TODO 【需要我实现：按用户和科目查询错题 SQL，摘要：根据 userId 和 subjectId 查询错题列表】
    @Select("select * from mistake where user_id = #{userId} and subject_id = #{subjectId}")
    List<Mistake> selectByUserIdAndSubjectId(@Param("userId") Long userId,
                                             @Param("subjectId") Long subjectId);

    /**
     * 统计当前用户错题总数。
     */
    // TODO 【需要我实现：错题总数统计 SQL，摘要：SELECT COUNT(*) FROM mistake WHERE user_id = #{userId}】
    @Select("select count(*) from mistake where user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 按可选多值筛选条件统计当前用户错题数量。
     *
     * <p>subjectIds、questionTypeIds 均为可选集合：为空或长度为 0 时该组条件不参与过滤；
     * 组内多个取值之间为「或」（IN），科目组与题型组之间为「与」；
     * SQL 始终携带 user_id 保证用户数据隔离。</p>
     */
    @Select("<script>" +
            "select count(*) from mistake " +
            "where user_id = #{userId} " +
            "<if test='subjectIds != null and subjectIds.size() > 0'>" +
            " and subject_id in " +
            "<foreach collection='subjectIds' item='subjectId' open='(' separator=',' close=')'>#{subjectId}</foreach>" +
            "</if>" +
            "<if test='questionTypeIds != null and questionTypeIds.size() > 0'>" +
            " and question_type_id in " +
            "<foreach collection='questionTypeIds' item='questionTypeId' open='(' separator=',' close=')'>#{questionTypeId}</foreach>" +
            "</if>" +
            "</script>")
    long countByUserIdWithFilter(@Param("userId") Long userId,
                                 @Param("subjectIds") List<Long> subjectIds,
                                 @Param("questionTypeIds") List<Long> questionTypeIds);

    /**
     * 按可选多值筛选条件分页查询当前用户错题。
     *
     * <p>subjectIds、questionTypeIds 均为可选集合：为空或长度为 0 时该组条件不参与过滤；
     * 组内多个取值之间为「或」（IN），科目组与题型组之间为「与」；
     * SQL 始终携带 user_id 保证用户数据隔离，并按 entry_time 倒序分页返回。</p>
     */
    @Select("<script>" +
            "select * from mistake " +
            "where user_id = #{userId} " +
            "<if test='subjectIds != null and subjectIds.size() > 0'>" +
            " and subject_id in " +
            "<foreach collection='subjectIds' item='subjectId' open='(' separator=',' close=')'>#{subjectId}</foreach>" +
            "</if>" +
            "<if test='questionTypeIds != null and questionTypeIds.size() > 0'>" +
            " and question_type_id in " +
            "<foreach collection='questionTypeIds' item='questionTypeId' open='(' separator=',' close=')'>#{questionTypeId}</foreach>" +
            "</if>" +
            "order by entry_time desc limit #{offset}, #{size}" +
            "</script>")
    List<Mistake> selectByUserIdWithFilter(@Param("userId") Long userId,
                                           @Param("subjectIds") List<Long> subjectIds,
                                           @Param("questionTypeIds") List<Long> questionTypeIds,
                                           @Param("offset") Integer offset,
                                           @Param("size") Integer size);

    /**
     * 按科目分组统计当前用户错题数量。
     */
    // TODO 【需要我实现：按科目分组统计 SQL，摘要：按 subject_id 分组统计错题数，可关联 subject 取科目名称】
    @Select("select m.subject_id , s.subject_name , count(*) as mistake_count " +
            "from mistake m left join subject s on m.subject_id = s.subject_id " +
            "where m.user_id = #{userId} group by m.subject_id , s.subject_name")
    List<Map<String, Object>> countGroupBySubject(@Param("userId") Long userId);

    /**
     * 按用户、科目、题型随机抽取指定数量的错题。
     */
    // TODO 【需要我实现：随机抽题 SQL，摘要：在 userId + subjectId + questionTypeId 范围内使用 ORDER BY RAND() LIMIT #{limit} 抽题；数据量大时建议改为随机数过滤或应用层随机】
    @Select("select * from mistake " +
            "where user_id = #{userId} and subject_id = #{subjectId} and question_type_id = #{questionTypeId} " +
            "order by rand() limit #{limit}")
    List<Mistake> selectRandomByUserIdAndSubjectIdAndQuestionTypeId(@Param("userId") Long userId,
                                                                     @Param("subjectId") Long subjectId,
                                                                     @Param("questionTypeId") Long questionTypeId,
                                                                     @Param("limit") Integer limit);
}
