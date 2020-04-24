package com.anyu.community.mapper;

import com.anyu.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface MessageMapper {
    //添加私信
    int insertMessage(Message message);

    //查询当前用户的私信列表,只返回最新一条消息
    List<Message> listConversations(int userId, int offset, int limit);

    //查询当前用户的私信列表,只返回最新一条消息
    int countConversations(int userId);

    //查询某个回话私信
    List<Message> listLetters(String conversationId, int offset, int limit);

    //查询某个回话私信数量
    int countLetters(String conversationId);

    //查询未读私信数量
    int countUnreadLetters(int userId, String conversationId);

    //更新私信状态
    int updateStatus(List<Integer> ids, int status);


}
