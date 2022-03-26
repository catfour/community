package com.atguigu.community.Dao;

import com.atguigu.community.entity.Comment;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommentMapper {

    @Select("select * from comment where status=0 and entity_type=#{entityType} and entity_id=#{entityId} order by create_time asc")
    List<Comment> selectCommentsByEntity(int entityType,int entityId);

    @Select("select count(id) from comment where status=0 and entity_type=#{entityType} and entity_id=#{entityId}")
    int selectCountByEntity(int entityType,int entityId);

    @Insert({
            "insert into comment (user_id,entity_type,entity_id,target_id,content,status,create_time) ",
            "values(#{userId},#{entityType},#{entityId},#{targetId},#{content},#{status},#{createTime})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertComment(Comment comment);
}
