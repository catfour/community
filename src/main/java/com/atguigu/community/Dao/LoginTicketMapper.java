package com.atguigu.community.Dao;

import com.atguigu.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

public interface LoginTicketMapper {
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insert(LoginTicket loginTicket);

    @Select({
            "select * from login_ticket where ticket=#{ticket}"
    })
    LoginTicket select(String ticket);

    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int update(@Param("ticket") String ticket,@Param("status") int status);
}
