package com.anyu.community.event;

import com.alibaba.fastjson.JSONObject;
import com.anyu.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 处理事件，将消息发送出去
     *
     * @param event
     */
    public void fireEvent(Event event) {
        //发送到指定主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
