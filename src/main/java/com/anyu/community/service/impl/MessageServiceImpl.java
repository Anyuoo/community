package com.anyu.community.service.impl;

import com.anyu.community.entity.Message;
import com.anyu.community.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageServiceImpl extends BaseClass implements MessageService {
    @Override
    public int saveMessage(Message message) {
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
    public int readMessage(List<Integer> ids, int status) {
        return messageMapper.updateStatus(ids, status);
    }

    @Override
    public int deleteMessage(List<Integer> ids, int status) {
        return messageMapper.updateStatus(ids, status);
    }
}
