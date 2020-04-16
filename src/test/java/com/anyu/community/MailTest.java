package com.anyu.community;

import com.anyu.community.utils.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    void mail() {
        mailClient.sendMail("anyuzhao1998@qq.com", "TEST", "Hello Word");
    }

    @Test
    void mailHtml() {
        Context context = new Context();
        context.setVariable("mailName", "zhangsan");
        String content = templateEngine.process("/mail/activation", context);
        System.out.println(content);
        mailClient.sendMail("anyuzhao1998@qq.com", "test", content);
    }
}
