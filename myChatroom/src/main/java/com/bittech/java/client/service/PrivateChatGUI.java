package com.bittech.java.client.service;

import com.bittech.java.util.CommUtils;
import com.bittech.java.vo.MessageVO;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class PrivateChatGUI {
    private JPanel privateChatPanel;
    private JTextArea readFromServer;
    private JTextField sendToServer;

    private String friendName;
    private String myName;
    private ConnectToServer connectToServer;
    private JFrame frame;
    private PrintStream out;

    public PrivateChatGUI(String friendName,String myName,ConnectToServer connectToServer) {
        this.friendName = friendName;
        this.myName = myName;
        this.connectToServer = connectToServer;
        try {
            this.out = new PrintStream(connectToServer.getOut(),true,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        frame = new JFrame("与"+friendName+"私聊中...");
        frame.setContentPane(privateChatPanel);
        //将窗口关闭操作设置为隐藏
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(500,400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //捕捉输入框的键盘输入
        sendToServer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
               StringBuilder sb = new StringBuilder();
               //拼接输入框输入的内容
               sb.append(sendToServer.getText());
               //1.捕捉到按下enter键
               if(e.getKeyCode() == KeyEvent.VK_ENTER){
                   //2.将当前信息发送给服务器端
                   String msg = sb.toString();
                   MessageVO messageVO = new MessageVO();
                   messageVO.setType("2");
                   messageVO.setContent(myName+"-"+CommUtils.objectToJson(msg));
                   messageVO.setTo(friendName);
                   PrivateChatGUI.this.out.println(CommUtils.objectToJson(messageVO));
                   //3.将自己发送的信息展示到当前私聊界面
                   readFromServer(myName+"说:"+msg);
                   //4.将输入框置空
                   sendToServer.setText("");
               }
            }

        });
    }

    public void readFromServer(String s) {
        readFromServer.append(s+"\n");
    }
    public JFrame getFrame(){
        return frame;
    }
}
