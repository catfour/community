package com.atguigu.community.Dao;

import com.atguigu.community.entity.DiscussPost;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

//@Mapper
public interface DiscussPostMapper {

    //@SelectProvider(type = DiscussPost.class, method = "selectDiscussPosts")
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId,@Param("offset") int offset,@Param("limit") int limit);

    @SelectProvider(type = DiscussPostSqlProvider.class, method = "selectDiscussPostsRows")
    int selectDiscussPostsRows(@Param("userId") int userId);

    @Insert({
            "insert into discuss_post(user_id,title,content,type,status,create_time,comment_count,score) ",
            "values(#{userId},#{title},#{content},#{type},#{status},#{createTime},#{commentCount},#{score})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertDiscussPost(DiscussPost discussPost);

    @Select("select * from discuss_post where id=#{id}")
    DiscussPost selectDiscussPostById(int id);

    @Update({
            "update discuss_post set comment_count=#{commentCount} where id=#{id}"
    })
    int updateCommentCount(int id, int commentCount);


    class DiscussPostSqlProvider{
//        public static String selectDiscussPosts(final Integer userId,final Integer offset,final  Integer limit){
//            return new SQL(){{
//                SELECT("*");
//                FROM("user");
//                WHERE("status != 2");
//                if( userId != 0){
//                    AND();
//                    WHERE("userId=#{userId}");
//                }
//                ORDER_BY("type desc,create_time desc limit #{offset},#{limit}");
//            }}.toString();
//        }
        public static String selectDiscussPostsRows(@Param("userId") int userId){
            return new SQL(){{
                SELECT("count(id)");
                FROM("discuss_post");
                WHERE("status != 2");
                if(userId != 0 ){
                    AND();
                    WHERE("user_id=#{userId}");
                }


            }}.toString();
        }
    }
}
