package com.anyu.community.controller;

import com.anyu.community.entity.User;
import com.anyu.community.service.FollowService;
import com.anyu.community.utils.CommunityConstant;
import com.anyu.community.utils.CommunityUtil;
import com.anyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;

    /**
     * 关注，取消关注
     *
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/follow")
    public String follow(String entityType, int entityId) {
        if (entityId == 0)
            return CommunityUtil.getJSONString(403, "关注失败！");
        EntityType type = CommunityUtil.getEntityType(entityType);
        if (type == null)
            return CommunityUtil.getJSONString(403, "关注失败！");
        User host = hostHolder.getUser();
        if (host == null)
            return CommunityUtil.getJSONString(403, "未登录！");

        //当前用户是否已经关注
        if (followService.followStatus(host.getId(), type, entityId)) {
            followService.cancelFollow(host.getId(), type, entityId);
            return CommunityUtil.getJSONString(1, "取消关注成功!");
        } else {
            followService.follow(host.getId(), type, entityId);
            return CommunityUtil.getJSONString(1, "关注成功!");
        }
    }
}
