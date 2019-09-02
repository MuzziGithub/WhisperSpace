package com.bittech.java.vo;

import lombok.Data;

/**
 * 服务器端与客户端传递信息的载体
 */
@Data
public class MessageVO {
    /**
     * type:表示告知服务器要进行的动作
     *      1：用户注册
     *      2：私聊
     * content：发送到服务器的具体内容
     * to:例如私聊：告知服务器要将信息发送给那个用户
     */
    private String type;
    private String content;
    private String to;

}
