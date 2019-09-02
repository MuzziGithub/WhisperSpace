package com.bittech.java.client.service;

import com.bittech.java.util.CommUtils;
import com.bittech.java.vo.MessageVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//创建群聊
public class CreateGroupGUI {
    private JPanel createGroupPanel;
    private JPanel friendLabelPanel;
    private JTextField groupNameText;
    private JButton onformBtn;
    //获取自己的信息
    private String myName;
    //获取在线朋友信息
    private Set<String> friends;
    //创建一个新连接
    ConnectToServer connectToServer = new ConnectToServer();
    private friendsList friendList;
    public CreateGroupGUI(String myName,Set<String> friends,ConnectToServer connectToServer,friendsList friendList) {
        this.myName = myName;
        this.friends = friends;
        this.connectToServer = connectToServer;
        this.friendList = friendList;

        JFrame frame = new JFrame("创建群组");
        frame.setContentPane(createGroupPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,300);
        //居中显示
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //首先需要在主聊天页面中点击创建群组才会跳出当前页面。
        //在当前页面的最上方需要动态的生成当前在线的用户信息

        //将在线好友以checkBox的形式展示到界面中
        //JCheckBox[]其实就是一个数组，长度为在线好友数的长度，每遍历到一个好友就添加一个chaeckbox
        //将好友列表按照树型展示（默认标签时横向排列的，现在让他纵向排列）
        friendLabelPanel.setLayout(new BoxLayout(friendLabelPanel,BoxLayout.Y_AXIS));
        JCheckBox[] checkBoxes = new JCheckBox[friends.size()];
        //遍历这个size集合
        Iterator<String> iterator = friends.iterator();
        int i =0;
        while(iterator.hasNext()){
            String labelName = iterator.next();
            checkBoxes[i] = new JCheckBox(labelName);
            friendLabelPanel.add(checkBoxes[i]);
            i++;
        }
        //刷新页面
        friendLabelPanel.revalidate();
        //点击提交按钮提交信息到服务端
        onformBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              //1.判断哪些好友选中加入群聊
                //需要使用一个集合去保存被选中的好友的checkbox
                Set<String> selectedFriends = new HashSet<>();
                //checkbox的顶层父类JComponent,getComponents用来获取frienLabelPanel下的所有组件
                Component[] comps = (Component[]) friendLabelPanel.getComponents();
                //遍历所有组件，看看有哪些checkbox被选中了
                for(Component comp : comps){
                    JCheckBox checkBox = (JCheckBox) comp;
                    if(checkBox.isSelected()){
                        String labelName = checkBox.getText();
                        selectedFriends.add(labelName);
                    }
                }
                selectedFriends.add(myName);
              //2.获取群名输入框输入的群名称
                String groupName = groupNameText.getText();

              //3.将群名+选中好友信息发送到服务器端
              //type:3
              //content:群名
              //to:[user1,user2,...]
                MessageVO messageVO = new MessageVO();
                messageVO.setType("3");
                messageVO.setContent(groupName);
                messageVO.setTo(CommUtils.objectToJson(selectedFriends));
                try {
                    //发送
                    PrintStream out = new PrintStream(connectToServer.getOut(),true,"UTF-8");
                    out.println(CommUtils.objectToJson(messageVO));
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                //4.将当前创建群的界面隐藏，刷新好友列表界面的群聊列表
                frame.setVisible(false);
                //返回好友列表界面，堆群聊列表作刷新
                // addGroupInfo
                // loadGroup
                friendList.addGroup(groupName,selectedFriends);
                friendList.loadGroupList();
            }
        });
    }


}
