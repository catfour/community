package com.atguigu.community.Dao;

import com.atguigu.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

//@Mapper
public interface DiscussPostMapper {

    //@SelectProvider(type = DiscussPost.class, method = "selectDiscussPosts")
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId,@Param("offset") int offset,@Param("limit") int limit);

    @SelectProvider(type = DiscussPostSqlProvider.class, method = "selectDiscussPostsRows")
    int selectDiscussPostsRows(@Param("userId") int userId);


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
