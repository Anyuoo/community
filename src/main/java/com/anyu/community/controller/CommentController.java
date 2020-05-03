package com.anyu.community.controller;

import com.anyu.community.entity.Comment;
import com.anyu.community.entity.DiscussPost;
import com.anyu.community.entity.Event;
import com.anyu.community.event.EventProducer;
import com.anyu.community.service.CommentService;
import com.anyu.community.service.DiscussPostServicce;
import com.anyu.community.utils.CommunityConstant;
import com.anyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostServicce discussPostServicce;


    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //触发事件
        Event event = new Event(1)
                .setTopic(TOPIC_TYPE_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        if (comment.getEntityType() == EntityType.POST.value()) {
            DiscussPost target = discussPostServicce.findDiscussPostById(discussPostId);
            event.setEntityTypeUserId(target.getUserId());
        } else if (comment.getEntityType() == EntityType.COMMENT.value()) {
            //目标实体id得到comment
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityTypeUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);
        return "redirect:/discuss/details/" + discussPostId;
    }
}
