package com.atguigu.community.Dao;

import com.atguigu.community.entity.User;
import org.apache.ibatis.annotations.*;

//@Mapper
public interface UserMapper {
    @Select("select * from user where id=#{id}")
    User selectById(@Param("id") int id);

    @Select("select * from user where username=#{userName}")
    User selectByName(@Param("userName") String userName);

    @Select("select * from user where email=#{email}")
    User selectByEmail(@Param("email") String email);

    //读取主键
    @Insert("insert into user (username,password,salt,email,type,status,activation_code,header_url,create_time) " +
            "values(#{username},#{password},#{salt},#{email},#{type},#{status},#{activationCode},#{headerUrl},#{createTime})")
    @Options(useGeneratedKeys = true)
    int insertUser( User user);

    @Update("update user set status = #{status} where id = #{id}")
    int updateStatus(@Param("status") Integer status,@Param("id") Integer id);

    @Update("update user set header_url = #{headerUrl} where id = #{id}")
    int updateHeader(@Param("headerUrl") String headerUrl,@Param("id") Integer id);

    @Update("update user set password = #{password} where id = #{id}")
    int updatePassword(@Param("password") String password,@Param("id") Integer id);

    @Delete("delete from user where id = #{id}")
    int deleteUser(Integer id);
}
