package com.anyu.community.controller;

import com.anyu.community.entity.Event;
import com.anyu.community.entity.User;
import com.anyu.community.event.EventProducer;
import com.anyu.community.service.LikeService;
import com.anyu.community.utils.CommunityConstant;
import com.anyu.community.utils.CommunityUtil;
import com.anyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;

    /**
     * 点赞
     *
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/like")
    public String like(String entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();

        EntityType type = CommunityUtil.getEntityType(entityType);
        //点赞
        likeService.like(user.getId(), type, entityId, entityUserId);
        //统计点赞数量
        long likeCount = likeService.countLike(type, entityId);
        //状态
        int likeStatus = likeService.findEntityLikeStatus(type, entityId, user.getId());
        Map<String, Object> map = new HashMap<>(2);
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        //触发事件
        if (likeStatus == 1) {
            Event event = new Event(1)
                    .setTopic(TOPIC_TYPE_LIKE)
                    .setEntityType(type.value())
                    .setEntityId(entityId)
                    .setEntityTypeUserId(entityUserId)
                    .setUserId(user.getId())
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }
        return CommunityUtil.getJSONString(1, null, map);
    }

}
