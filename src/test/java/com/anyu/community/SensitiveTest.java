package com.anyu.community;

import com.anyu.community.utils.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    void test() {
        String text = "你嫖....娼。。。不魔魔鬼 恶魔鬼 恶魔魔鬼 恶魔赌博。。";
//        String text="你嫖娼。。。不赌博。。";
        String filter = sensitiveFilter.filter(text);
        System.out.printf(filter);
    }
}
