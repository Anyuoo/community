package com.anyu.community.service.impl;

import com.anyu.community.entity.Message;
import com.anyu.community.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageServiceImpl extends BaseClass implements MessageService {
    @Override
    public int saveLetter(Message message) {
        //文本过滤
        message.setContent(sensitiveFilter.filter(message.getContent()));
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        int row = messageMapper.insertMessage(message);
        return row;
    }

    @Override
    public List<Message> listConversations(int userId, int offset, int limit) {
        return messageMapper.listConversations(userId, offset, limit);
    }

    @Override
    public int countConversations(int userId) {
        return messageMapper.countConversations(userId);
    }

    @Override
    public List<Message> listLetters(String conversationId, int offset, int limit) {
        return messageMapper.listLetters(conversationId, offset, limit);
    }

    @Override
    public int countLetters(String conversationId) {
        return messageMapper.countLetters(conversationId);
    }

    @Override
    public int countUnreadLetters(int userId, String conversationId) {
        return messageMapper.countUnreadLetters(userId, conversationId);
    }

    @Override
    public int readLetter(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    @Override
    public int deleteMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 2);
    }

    @Override
    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    @Override
    public int countNotices(int userId, String topic) {
        return messageMapper.countNotices(userId, topic);
    }

    @Override
    public int countUnreadNotices(int userId, String topic) {
        return messageMapper.countUnreadNotices(userId, topic);
    }

    @Override
    public List<Message> listNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.listNotices(userId, topic, offset, limit);
    }
}
