package com.anyu.community.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件对象
 */
public class Event {
    private final int DEFAULT_CAPACITY = 2;
    private String topic;
    private int userId;
    private int entityType;
    private int entityId;
    private int entityTypeUserId;
    private Map<String, Object> data;
    private int capacity = DEFAULT_CAPACITY;

    public Event() {
        init();
    }

    public Event(int capacity) {
        this.capacity = capacity;
        init();
    }

    private void init() {
        data = new HashMap<>(capacity);
    }

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityTypeUserId() {
        return entityTypeUserId;
    }

    public Event setEntityTypeUserId(int entityTypeUserId) {
        this.entityTypeUserId = entityTypeUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityTypeUserId=" + entityTypeUserId +
                ", data=" + data +
                '}';
    }
}
