package com.atguigu.community.util;


import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    //替换符
    private static final String REPLACE ="***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyWord(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }


    // 将一个敏感词添加到前缀树中
    private void addKeyWord(String keyWord){
        TrieNode tempNode = rootNode;
        for (int  i= 0; i<keyWord.length();i++){
            char c = keyWord.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if(subNode == null){
                //初始化节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //移到下一层
            tempNode = subNode;
            if(i==keyWord.length() - 1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     *  过滤敏感词
     * @param text 需要过滤的文本
     * @return  过滤后文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        //指针1
        TrieNode tempNode = rootNode;
        //指针2
        int start = 0;
        //指针3
        int position = 0;
        //存放结果
        StringBuilder result = new StringBuilder();
        while(position < text.length()){
            char c = text.charAt(position);

            //跳过符号
            if(isSymbol(c)){
                //若指针1处于根节点
                if(tempNode == rootNode){
                    result.append(c);
                    start++;
                }
                //无论符号在中间还是开头，指针3都要移动
                position++;
                continue;
            }
            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                //以start开头的字符串不是敏感词
                result.append(text.charAt(start));
                //进入下一个位置
                position = ++start;
                //重新指向根节点
                tempNode = rootNode;
            }else if(tempNode.getKeyWordEnd()) {
                //发现敏感词
                result.append(REPLACE);
                //进入下一个位置
                start = ++position;
                //重新指向根节点
                tempNode = rootNode;
            }else {
                position++;
            }

        }
        //将最后一批字符计入结果
        result.append(text.substring(start));
        return result.toString();
    }
    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }



    //前缀树
    public class TrieNode{
        //  关键词结束标志
        private boolean isKeyWordEnd = false;
        //子节点
        private Map<Character,TrieNode> subNode = new HashMap<>();
        //设置标志
        public void setKeyWordEnd(boolean keyWordEnd){
            this.isKeyWordEnd = keyWordEnd;
        }
        //
        public boolean getKeyWordEnd(){
            return isKeyWordEnd;
        }

        //添加子节点
        public void addSubNode(Character c,TrieNode node) {
            subNode.put(c,node);
        }
        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNode.get(c);
        }
    }


}
