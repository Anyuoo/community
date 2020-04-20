package com.anyu.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤器
 */
@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLACEMENT = "***";
    private final TrieNode root = new TrieNode();

    /**
     * 初始化类，读取敏感字文件，添加到前缀数
     */
    @PostConstruct
    private void init() {
        try (
                InputStream stream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream))
        ) {
            String keyWord;
            while ((keyWord = reader.readLine()) != null) {
                this.addKeyWord(keyWord);
            }
        } catch (IOException e) {
            logger.error("读取敏感字文件失败：" + e.getMessage());
        }

    }

    /**
     * 添加敏感字到前缀数
     *
     * @param keyWord
     */
    private void addKeyWord(String keyWord) {
        TrieNode temNode = root;
        for (int i = 0; i < keyWord.length(); i++) {
            char ch = keyWord.charAt(i);
            if (temNode.getNextNode(ch) == null) {
                TrieNode node = new TrieNode();
                temNode.setNextNode(ch, node);
            }
            temNode = temNode.getNextNode(ch);
            if (i == keyWord.length() - 1)
                temNode.setEnd(true);
        }
    }

    /**
     * 过滤敏感词
     *
     * @param context 需要过滤的文本
     * @return 过滤的文本
     */
    public String filter(String context) {
        if (context == null && context.length() < 1) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        TrieNode curNode;
        int cur = 0, end;
        while (cur < context.length()) {
            char ch = context.charAt(cur);
            curNode = root;
            end = cur;
            //是否符号
            if (isSymble(ch)) {
                cur++;
                sb.append(ch);
                continue;
            }
            //判断是否是敏感字
            //end 边界可能越界
            while (curNode.getNextNode(ch) != null) {
                curNode = curNode.getNextNode(ch);
                ch = context.charAt(++end);
                while (isSymble(ch)) {
                    ch = context.charAt(++end);
                }
            }
            if (curNode.isEnd()) {
                sb.append(REPLACEMENT);
                cur = end;
            } else {
                sb.append(ch);
            }
            cur++;
        }
        return sb.toString();
    }

    /**
     * 判断是否符号
     *
     * @param ch
     * @return
     */
    private boolean isSymble(Character ch) {
        return !CharUtils.isAsciiAlphanumeric(ch) && (ch < 0x2E80 || ch > 0x9FFF);
    }

    private class TrieNode {
        //KEY下个节点字符，value，下个子节点上
        private final Map<Character, TrieNode> nextNode = new HashMap<>();
        //是否结束
        private boolean isEnd;

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

        public TrieNode getNextNode(Character key) {
            return this.nextNode.get(key);
        }

        public void setNextNode(Character key, TrieNode node) {
            this.nextNode.put(key, node);
        }
    }


}
