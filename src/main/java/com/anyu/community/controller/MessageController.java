package com.anyu.community.controller;

import com.anyu.community.entity.Message;
import com.anyu.community.entity.Page;
import com.anyu.community.entity.User;
import com.anyu.community.service.MessageService;
import com.anyu.community.service.UserService;
import com.anyu.community.utils.CommunityUtil;
import com.anyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/letter")
public class MessageController {
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
    @GetMapping("/list")
    public String getLettersList(Model model, Page page) {
        //获取当前用户
        User user = hostHolder.getUser();
        //设置分页信息
        int conversationsCount = messageService.countConversations(user.getId());
        page.setRows(conversationsCount);
        page.setPath("/letter/list");

        List<Message> conversationList = messageService.listConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> msgVOList = new ArrayList<>(conversationsCount);
        for (Message conversion : conversationList) {
            Map<String, Object> msgVO = new HashMap<>(4);
            //信
            msgVO.put("conversion", conversion);
            //发信人
            int targetId = user.getId() == conversion.getFromId() ? conversion.getToId() : conversion.getFromId();
            User target = userService.findUserById(targetId);
            msgVO.put("target", target);
            //对话数量
            int lettersCount = messageService.countLetters(conversion.getConversationId());
            msgVO.put("lettersCount", lettersCount);
            //未读信息数量
            int unreadLettersCount = messageService.countUnreadLetters(user.getId(), conversion.getConversationId());
            msgVO.put("unreadLettersCount", unreadLettersCount);
            msgVOList.add(msgVO);
        }
        model.addAttribute("msgVOList", msgVOList);
        int unreadMessagesCount = messageService.countUnreadLetters(user.getId(), null);
        model.addAttribute("unreadMsgCount", unreadMessagesCount);
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
    @GetMapping("/detail/{conversationId}")
    public String detail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        //获取当前用户
        User user = hostHolder.getUser();
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
        int unreadCount = messageService.countUnreadLetters(user.getId(), conversationId);

        if (!getLettersId(conversationList, unreadCount).isEmpty())
            messageService.readMessage(getLettersId(conversationList, unreadCount), 1);
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
    @PostMapping("/send")
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
        messageService.saveMessage(message);
        return CommunityUtil.getJSONString(1, "发送成功！");
    }


    /**
     * 删除私信
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public String deleteMessage(int id) {
        if (id == 0)
            return CommunityUtil.getJSONString(403, "删除失败！");
        List<Integer> ids = new ArrayList<>(1);
        ids.add(id);
        System.out.println(id);
        messageService.deleteMessage(ids, 2);
        return CommunityUtil.getJSONString(1, "删除成功！");
    }


}
