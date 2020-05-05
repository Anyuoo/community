package com.anyu.community.controller;

import com.anyu.community.entity.Comment;
import com.anyu.community.entity.DiscussPost;
import com.anyu.community.entity.Page;
import com.anyu.community.entity.User;
import com.anyu.community.service.CommentService;
import com.anyu.community.service.DiscussPostServicce;
import com.anyu.community.service.LikeService;
import com.anyu.community.service.UserService;
import com.anyu.community.utils.CommunityConstant;
import com.anyu.community.utils.CommunityUtil;
import com.anyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostServicce discussPostServicce;
    @Autowired
    private HostHolder hostHolder;
    private final String PREFIX = "site/";
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    /**
     * 添加帖子
     * http://localhost:8080/community/discuss/add
     *
     * @param title
     * @param content
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setUserId(user.getId());
        discussPost.setCreateTime(new Date());
        System.out.println(discussPost);
        discussPostServicce.addDiscussPost(discussPost);
        return CommunityUtil.getJSONString(0, "发布成功");
    }

    /**
     * 帖子详情
     *
     * @param discussPostId
     * @param model
     * @return
     */
    @GetMapping("/details/{discussPostId}")
    public String getDiscussPostDetails(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        User holder = hostHolder.getUser();
        //查询帖子内容
        DiscussPost post = discussPostServicce.findDiscussPostById(discussPostId);
        model.addAttribute("discuss", post);
        //查询作者信息
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        //分页设置
        page.setPath("/discuss/details/" + discussPostId);
        page.setLimit(5);
        page.setRows(post.getCommentCount());
        //查询帖子评论
        //评论:帖子的评论 回复：给评论的评论
        List<Comment> comments = commentService.findCommentsByEntity(EntityType.POST, post.getId(), page.getOffset(), page.getLimit());
        int commentCount = commentService.findCommentCount(EntityType.POST, post.getId());
        //每一条评论:包含了回复者信息和回复
        List<Map<String, Object>> commentVOList = new ArrayList<>(page.getLimit());
        if (comments != null) {
            //得到每条评论
            for (Comment comment : comments) {
                Map<String, Object> commentVO = new HashMap<>(8);
                //评论
                commentVO.put("comment", comment);
                //评论者
                commentVO.put("observer", userService.findUserById(comment.getUserId()));
                //赞
                long commentLikeCount = likeService.countLike(EntityType.COMMENT, comment.getId());
                commentVO.put("commentLikeCount", commentLikeCount);
                int commentLikeStatus = holder == null ? 0 : likeService.findEntityLikeStatus(EntityType.COMMENT, comment.getId(), holder.getId());
                commentVO.put("commentLikeStatus", commentLikeStatus);

                //回复列表:每条评论的回复
                int replyCount = commentService.findCommentCount(EntityType.COMMENT, comment.getId());
                List<Comment> replyList = commentService.findCommentsByEntity(EntityType.COMMENT, comment.getId(), 0, replyCount);
                List<Map<String, Object>> replyVOList = new ArrayList<>(replyCount);
                for (Comment reply : replyList) {
                    Map<String, Object> replyVO = new HashMap<>(8);
                    //回复
                    replyVO.put("reply", reply);
                    //回复者
                    replyVO.put("replier", userService.findUserById(reply.getUserId()));
                    //回复目标
                    User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                    replyVO.put("target", target);
                    //赞
                    long replyLikeCount = likeService.countLike(EntityType.COMMENT, reply.getId());
                    replyVO.put("replyLikeCount", replyLikeCount);
                    int replyLikeStatus = holder == null ? 0 : likeService.findEntityLikeStatus(EntityType.COMMENT, reply.getId(), holder.getId());
                    replyVO.put("replyLikeStatus", replyLikeStatus);
                    replyVOList.add(replyVO);
                }
                //回复数
                commentVO.put("replyCount", replyCount);
                //回复列表
                commentVO.put("replyVOList", replyVOList);
                commentVOList.add(commentVO);
            }
            model.addAttribute("commentCount", commentCount);
            model.addAttribute("commentVOList", commentVOList);
            //赞
            long likeCount = likeService.countLike(EntityType.POST, discussPostId);
            model.addAttribute("likeCount", likeCount);
            int likeStatus = holder == null ? 0 : likeService.findEntityLikeStatus(EntityType.POST, discussPostId, holder.getId());
            model.addAttribute("likeStatus", likeStatus);
        }
        return PREFIX + "discuss-detail";
    }


}
