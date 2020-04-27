package com.anyu.community.controller;

import com.anyu.community.entity.User;
import com.anyu.community.service.LikeService;
import com.anyu.community.utils.CommunityConstant;
import com.anyu.community.utils.CommunityUtil;
import com.anyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;

    /**
     * 点赞
     *
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId) {
        User user = hostHolder.getUser();

        EntityType type = null;
        switch (entityType) {
            case 1:
                type = EntityType.POST;
                break;
            case 2:
                type = EntityType.COMMENT;
                break;
        }
        //点赞
        likeService.like(user.getId(), type, entityId);
        //统计点赞数量
        long likeCount = likeService.countLike(type, entityId);
        //状态
        int likeStatus = likeService.findEntityLikeStatus(type, entityId, user.getId());
        Map<String, Object> map = new HashMap<>(2);
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        return CommunityUtil.getJSONString(1, null, map);
    }

}
