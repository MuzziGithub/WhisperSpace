package com.bittech.java.client.service;

import com.bittech.java.util.CommUtils;
import com.bittech.java.vo.MessageVO;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

public class GroupChatGUI {
    private JPanel groupPanel;
    private JTextArea readFromServer;
    private JTextField sendToServer;
    private JPanel friendsPanel;
    private JFrame frame;

    private String groupName;
    private Set<String> friends;
    private String myName;
    private ConnectToServer connectToServer;

    public GroupChatGUI(String groupName, Set<String> groupFriends,String username,ConnectToServer connectToServer) {
        this.groupName = groupName;
        this.friends = groupFriends;
        this.myName = username;
        this.connectToServer = connectToServer;
        frame = new JFrame(groupName+"群聊中...");
        frame.setContentPane(groupPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(500,400);
        frame.setVisible(true);
        //加载群好友列表
        friendsPanel.setLayout(new BoxLayout(friendsPanel,
                BoxLayout.Y_AXIS));
        Iterator<String> iterator = friends.iterator();
        while (iterator.hasNext()) {
            String labelName = iterator.next();
            JLabel jLabel = new JLabel(labelName);
            friendsPanel.add(jLabel);
        }

        sendToServer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                StringBuilder sb = new StringBuilder();
                sb.append(sendToServer.getText());
                // 捕捉回车按键
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String strToServer = sb.toString();
                    // type:4
                    // content:myName-msg
                    // to:groupName
                    MessageVO messageVO = new MessageVO();
                    messageVO.setType("4");
                    messageVO.setContent(myName+"-"+strToServer);
                    messageVO.setTo(groupName);
                    try {
                        PrintStream out = new PrintStream(connectToServer.getOut(),
                                true,"UTF-8");
                        out.println(CommUtils.objectToJson(messageVO));

                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    public void readFromServer(String msg) {
        readFromServer.append(msg+"\n");
    }
    public JFrame getFrame() {
        return frame;
    }
}
