package com.atguigu.community.Dao;

import com.atguigu.community.entity.Message;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;

import java.util.List;

public interface MessageMapper {
    @Select({
            "select * from message where id in ",
            "(select max(id) from message where from_id!=1 and status!=2 and (from_id=#{userId} or to_id=#{userId}) group by conversation_id) ",
            "order by id desc"
    })
    public List<Message> selectConversations(int userId);

    @Select({
            "select count(m.maxid) from ",
            "(select max(id) as maxid from message where from_id!=1 and status!=0 and (from_id=#{userId} or to_id=#{userId}) group by conversation_id)",
            "as m"
    })
    public int selectConversationCount(int userId);

    @Select({
            "select * from message where status!=2 and from_id!=1 and conversation_id=#{conversationId} order by id desc"
    })
    public List<Message> selectLetters(String conversationId);

    @Select( {
            "select count(id) from message where status!=2 and from_id!=1 and conversation_id=#{conversationId}"
    })
    public int selectLetterCount(String conversationId);

//    @Select({
//            "select count(id) from message where status==0 and from_id!=1 and to_id=#{userId} and conversation_id=#{conversationId}"
//    })
    @SelectProvider(type = MessageSqlProvider.class,method = "selectLetterUnreadCount")
    public int selectLetterUnreadCount(int userId, String conversationId);
    //新增消息
    @Insert({
            "insert into message (from_id,to_id,conversation_id,content,status,create_time) ",
            "values(#{fromId},#{toId},#{conversationId},#{content},#{status},#{createTime})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    public int insertMessage(Message message);

    //修改消息的状态

    public int updateStatus(List<Integer> ids,int status);

    class MessageSqlProvider{
        public static String selectLetterUnreadCount(@Param("userId") int userId,@Param("conversationId")  String conversationId){
            return new SQL(){{
                SELECT("count(id)");
                FROM("message");
                WHERE("status=0");
                AND();
                WHERE("from_id!=1");
                AND();
                WHERE("to_id=#{userId}");
                if(conversationId != null ){
                    AND();
                    WHERE("conversation_id=#{conversationId}");
                }
            }}.toString();
        }


    }
}
