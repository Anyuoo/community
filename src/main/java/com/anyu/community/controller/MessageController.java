package com.anyu.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.anyu.community.entity.Message;
import com.anyu.community.entity.Page;
import com.anyu.community.entity.User;
import com.anyu.community.service.MessageService;
import com.anyu.community.service.UserService;
import com.anyu.community.utils.CommunityConstant;
import com.anyu.community.utils.CommunityUtil;
import com.anyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {
    private final String PREFIX = "site/";
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    /**
     * 私信列表
     *
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/letter/list")
    public String getLettersList(Model model, Page page) {
        //获取当前用户
        User holder = hostHolder.getUser();
        //设置分页信息
        int conversationsCount = messageService.countConversations(holder.getId());
        page.setRows(conversationsCount);
        page.setPath("/letter/list");

        List<Message> conversationList = messageService.listConversations(holder.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> msgVOList = new ArrayList<>(conversationsCount);
        for (Message conversion : conversationList) {
            Map<String, Object> msgVO = new HashMap<>(4);
            //信
            msgVO.put("conversion", conversion);
            //发信人
            int targetId = holder.getId() == conversion.getFromId() ? conversion.getToId() : conversion.getFromId();
            User target = userService.findUserById(targetId);
            msgVO.put("target", target);
            //对话数量
            int lettersCount = messageService.countLetters(conversion.getConversationId());
            msgVO.put("lettersCount", lettersCount);
            //未读信息数量
            int unreadLettersCount = messageService.countUnreadLetters(holder.getId(), conversion.getConversationId());
            msgVO.put("unreadLettersCount", unreadLettersCount);
            msgVOList.add(msgVO);
        }
        model.addAttribute("msgVOList", msgVOList);
        //未读消息数量
        int unreadMessagesCount = messageService.countUnreadLetters(holder.getId(), null);
        model.addAttribute("unreadMsgCount", unreadMessagesCount);
        int unreadNoticeCount = messageService.countUnreadNotices(holder.getId(), null);
        model.addAttribute("unreadNoticeCount", unreadNoticeCount);

        return PREFIX + "letter";
    }

    /**
     * 私信详情
     *
     * @param conversationId
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/letter/detail/{conversationId}")
    public String detail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        //获取当前用户
        User holder = hostHolder.getUser();
        //设置分页信息
        int lettersCount = messageService.countLetters(conversationId);
        page.setRows(lettersCount);
        page.setPath("/letter/detail/" + conversationId);

        //写信人
        User fromUser = getFromUser(conversationId);
        model.addAttribute("fromUser", fromUser);

        List<Message> conversationList = messageService.listLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> msgVOList = new ArrayList<>(lettersCount);
        for (Message conversion : conversationList) {
            Map<String, Object> msgVO = new HashMap<>(2);
            //信
            msgVO.put("conversion", conversion);
            //发信人
            int targetId = conversion.getFromId();
            User target = userService.findUserById(targetId);
            msgVO.put("target", target);
            msgVOList.add(msgVO);
        }
        model.addAttribute("msgVOList", msgVOList);
        //将消息设为已读
        int unreadCount = messageService.countUnreadLetters(holder.getId(), conversationId);

        if (!getLettersId(conversationList, unreadCount).isEmpty())
            messageService.readLetter(getLettersId(conversationList, unreadCount));
        return PREFIX + "letter-detail";
    }

    /**
     * 找到发信人
     *
     * @param conversationId
     * @return
     */
    private User getFromUser(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.valueOf(ids[0]);
        int id1 = Integer.valueOf(ids[1]);
        if (hostHolder.getUser().getId() == id0)
            return userService.findUserById(id1);
        else
            return userService.findUserById(id0);
    }

    /**
     * 查找未读消息id
     *
     * @param conversationList
     * @param unreadCount
     * @return
     */
    private List<Integer> getLettersId(List<Message> conversationList, int unreadCount) {
        List<Integer> ids = new ArrayList<>(unreadCount);
        if (conversationList != null)
            for (Message message : conversationList) {
                if (message.getStatus() == 0 && message.getToId() == hostHolder.getUser().getId())
                    ids.add(message.getId());
            }
        return ids;
    }

    /**
     * 发送信息
     *
     * @param content
     * @param toUsername
     * @return
     */
    @PostMapping("/letter/send")
    @ResponseBody
    public String sendMessage(String content, String toUsername) {
        if (content == null)
            return CommunityUtil.getJSONString(403, "消息内容为空！");
        if (toUsername == null)
            return CommunityUtil.getJSONString(403, "未填写收件人！");
        User fromUser = hostHolder.getUser();
        if (fromUser == null)
            return CommunityUtil.getJSONString(403, "未登录！");
        User toUser = userService.findByName(toUsername);
        if (toUser == null)
            return CommunityUtil.getJSONString(403, "收信人不存在！");
        Message message = new Message();
        message.setFromId(fromUser.getId());
        message.setToId(toUser.getId());
        message.setContent(content);
        message.setCreateTime(new Date());
        String conversationId = fromUser.getId() > toUser.getId() ? toUser.getId() + "_" + fromUser.getId() : fromUser.getId() + "_" + toUser.getId();
        message.setConversationId(conversationId);
        messageService.saveLetter(message);
        return CommunityUtil.getJSONString(1, "发送成功！");
    }


    /**
     * 删除私信
     *
     * @param id
     * @return
     */
    @PostMapping("/letter/delete")
    @ResponseBody
    public String deleteMessage(int id) {
        if (id == 0)
            return CommunityUtil.getJSONString(403, "删除失败！");
        List<Integer> ids = new ArrayList<>(1);
        ids.add(id);
        messageService.deleteMessage(ids);
        return CommunityUtil.getJSONString(1, "删除成功！");
    }

    /**
     * 系统消息列表
     *
     * @param model
     * @return
     */
    @GetMapping("/notice/list")
    public String getNoticeList(Model model) {
        User holder = hostHolder.getUser();

        //评论
        Map<String, Object> noticeVO = null;
        Message commentNotice = messageService.findLatestNotice(holder.getId(), TOPIC_TYPE_COMMENT);
        noticeVO = formatNoticeVO(commentNotice, holder, TOPIC_TYPE_COMMENT, false);
        model.addAttribute("commentNoticeVO", noticeVO);
        //关注
        Message followNotice = messageService.findLatestNotice(holder.getId(), TOPIC_TYPE_FOLLOW);
        noticeVO = formatNoticeVO(followNotice, holder, TOPIC_TYPE_FOLLOW, false);
        model.addAttribute("followNoticeVO", noticeVO);

        //赞
        Message likeNotice = messageService.findLatestNotice(holder.getId(), TOPIC_TYPE_LIKE);
        noticeVO = formatNoticeVO(likeNotice, holder, TOPIC_TYPE_LIKE, false);
        model.addAttribute("likeNoticeVO", noticeVO);

        //未读消息数量
        int unreadLetterCount = messageService.countUnreadLetters(holder.getId(), null);
        model.addAttribute("unreadLetterCount", unreadLetterCount);
        int unreadNoticeCount = messageService.countUnreadNotices(holder.getId(), null);
        model.addAttribute("unreadNoticeCount", unreadNoticeCount);

        return PREFIX + "notice";
    }

    /**
     * 组装前端通知需要显示的数据
     *
     * @param notice    某个具体通知
     * @param holder    当前用户
     * @param topicType 通知主题
     * @param isDetail  是否为详情页面
     * @return
     */
    private Map<String, Object> formatNoticeVO(Message notice, User holder, String topicType, boolean isDetail) {
        Map<String, Object> noticeVO = new HashMap<>(8);
        //前端根据notice是否为null，显示样式，map中必须存有notice字段
        noticeVO.put("notice", notice);
        if (notice != null) {
            var content = HtmlUtils.htmlUnescape(notice.getContent());
            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            noticeVO.put("user", userService.findUserById((Integer) data.get("userId")));
            noticeVO.put("entityType", data.get("entityType"));
            noticeVO.put("entityId", data.get("entityId"));
            //不是关注，关注只能关注人，没有postId字段
            if (!TOPIC_TYPE_FOLLOW.equals(topicType)) {
                noticeVO.put("postId", data.get("postId"));
            }
            if (!isDetail) {
                //已读数量
                int count = messageService.countNotices(holder.getId(), topicType);
                noticeVO.put("count", count);
                //未读数量
                int unreadCount = messageService.countUnreadNotices(holder.getId(), topicType);
                noticeVO.put("unreadCount", unreadCount);
            } else {
                //通知作者
                noticeVO.put("fromUser", userService.findUserById(notice.getFromId()));
            }
        }
        return noticeVO;
    }

    /**
     * 系统信息详情页面
     *
     * @param topic
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Model model, Page page) {
        User holder = hostHolder.getUser();

        //分页设置
        page.setPath("/notice/detail/" + topic);
        page.setLimit(5);
        page.setRows(messageService.countNotices(holder.getId(), topic));

        List<Message> noticesList = messageService.listNotices(holder.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVOList = new ArrayList<>(page.getLimit());
        for (Message notice : noticesList) {
            Map<String, Object> noticeVO = null;
            noticeVO = formatNoticeVO(notice, holder, topic, true);
            noticeVOList.add(noticeVO);
        }
        model.addAttribute("noticeVOList", noticeVOList);

        //设置已读
        List<Integer> ids = getLettersId(noticesList, messageService.countUnreadNotices(holder.getId(), topic));
        if (!ids.isEmpty()) {
            messageService.readLetter(ids);
        }
        return PREFIX + "notice-detail";
    }
}
