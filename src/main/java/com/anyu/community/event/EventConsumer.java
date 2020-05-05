package com.anyu.community.event;

import com.alibaba.fastjson.JSONObject;
import com.anyu.community.entity.Event;
import com.anyu.community.entity.Message;
import com.anyu.community.service.MessageService;
import com.anyu.community.utils.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {TOPIC_TYPE_COMMENT, TOPIC_TYPE_FOLLOW, TOPIC_TYPE_LIKE})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息为空！");
            return;
        }

        //发送站内消息
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return;
        }
        //将消息封装成message，存入数据库
        Message message = new Message();
        message.setFromId(SYSTEM_USER);
        message.setToId(event.getEntityTypeUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));

        messageService.saveLetter(message);
    }

}
