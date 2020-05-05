package com.anyu.community.service;

import com.anyu.community.entity.Message;

import java.util.List;


public interface MessageService {
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

    //添加私信
    int saveLetter(Message message);

    //已读信息状态
    int readLetter(List<Integer> ids);

    //删除信息
    int deleteMessage(List<Integer> ids);

    //查询某用户最新的通知
    Message findLatestNotice(int userId, String topic);

    //查询某用户的通知数量
    int countNotices(int userId, String topic);

    //查询某用户未读通知数量
    int countUnreadNotices(int userId, String topic);

    //查询某用户所有通知
    List<Message> listNotices(int userId, String topic, int offset, int limit);
}
