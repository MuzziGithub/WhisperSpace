package com.bittech.java.client.service;

import com.bittech.java.util.CommUtils;
import com.bittech.java.vo.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class friendsList {
    private JPanel friendsPanel;
    private JScrollPane groupList;
    private JButton createGroupBtn;
    private JScrollPane friendslist;
    private JFrame frame;
    private String userName;
    //存储所有在线好友信息
    private Set<String> users;
    private ConnectToServer connectToServer;
    //缓存所有私聊界面
    private Map<String,PrivateChatGUI> privateChatGUIList = new ConcurrentHashMap<>();
    //缓存所有注册的群聊
    private Map<String,Set<String>> groupsList = new ConcurrentHashMap<>();
    // 缓存所有群聊界面
    private Map<String,GroupChatGUI> groupChatGUIList = new ConcurrentHashMap<>();
    // 好友列表后台任务，不断监听服务器发来的信息
    // 好友上线信息、用户私聊、群聊
    private class DaemonTask implements Runnable {
        private Scanner in = new Scanner(connectToServer.getIn());
        @Override
        public void run() {
            while (true) {
                // 收到服务器发来的信息
                if (in.hasNextLine()) {
                    String strFromServer = in.nextLine();
                    // 此时服务器发来的是一个json字符串
                    if (strFromServer.startsWith("{")) {
                        MessageVO messageVO = (MessageVO) CommUtils.jsonToObject(strFromServer, MessageVO.class);
                        if (messageVO.getType().equals("2")) {
                            // 服务器发来的私聊信息
                            String friendName = messageVO.getContent().split("-")[0];
                            String msg = messageVO.getContent().split("-")[1];
                            // 判断此私聊是否是第一次创建
                            if (privateChatGUIList.containsKey(friendName)) {
                                PrivateChatGUI privateChatGUI = privateChatGUIList.get(friendName);
                                privateChatGUI.getFrame().setVisible(true);
                                privateChatGUI.readFromServer(friendName+"说:"+msg);
                            }else {
                                PrivateChatGUI privateChatGUI = new PrivateChatGUI(friendName,
                                        userName,connectToServer);
                                privateChatGUIList.put(friendName,privateChatGUI);
                                privateChatGUI.readFromServer(friendName+"说:"+msg);
                            }
                        }
                        else if (messageVO.getType().equals("4")) {
                            // 收到服务器发来的群聊信息
                            // type:4
                            // content:sender-msg
                            // to:groupName-[1,2,3,...]
                            String groupName = messageVO.getTo().split("-")[0];
                            String senderName = messageVO.getContent().split("-")[0];
                            String groupMsg = messageVO.getContent().split("-")[1];
                            // 若此群名称在群聊列表
                            if (groupsList.containsKey(groupName)) {
                                if (groupChatGUIList.containsKey(groupName)) {
                                    // 群聊界面弹出
                                    GroupChatGUI groupChatGUI = groupChatGUIList.get(groupName);
                                    groupChatGUI.getFrame().setVisible(true);
                                    groupChatGUI.readFromServer(senderName+"说:"+groupMsg);
                                }else {
                                    Set<String> names = groupsList.get(groupName);
                                    GroupChatGUI groupChatGUI = new GroupChatGUI(groupName, names,userName, connectToServer);
                                    groupChatGUIList.put(groupName,groupChatGUI);
                                    groupChatGUI.readFromServer(senderName+"说:"+groupMsg);
                                }
                            }else {
                                // 若群成员第一次收到群聊信息
                                // 1.将群名称以及群成员保存到当前客户端群聊列表
                                Set<String> friends = (Set<String>) CommUtils.jsonToObject(messageVO.getTo().split("-")[1], Set.class);
                                groupsList.put(groupName, friends);
                                loadGroupList();
                                // 2.弹出群聊界面
                                GroupChatGUI groupChatGUI = new GroupChatGUI(groupName,
                                        friends,userName,connectToServer);
                                groupChatGUIList.put(groupName,groupChatGUI);
                                groupChatGUI.readFromServer(senderName+"说:"+groupMsg);
                            }
                        }
                    }else {
                        // newLogin:userName
                        if (strFromServer.startsWith("newLogin:")) {
                            String newFriendName = strFromServer.split(":")[1];
                            users.add(newFriendName);
                            // 弹框提示用户上线
                            JOptionPane.showMessageDialog(frame,
                                    newFriendName+"上线了!",
                                    "上线提醒",JOptionPane.INFORMATION_MESSAGE);
                            // 刷新好友列表
                            loadUserList();
                        }
                    }
                }
            }
        }
    }
  //群聊点击事件
    private class GroupLabelAction implements MouseListener {
        private String groupName;

        public GroupLabelAction(String groupName) {
            this.groupName = groupName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (groupChatGUIList.containsKey(groupName)) {
                GroupChatGUI groupChatGUI = groupChatGUIList.get(groupName);
                groupChatGUI.getFrame().setVisible(true);
            }else {
                Set<String> names = groupsList.get(groupName);
                GroupChatGUI groupChatGUI = new GroupChatGUI(groupName,names,userName,connectToServer);
                groupChatGUIList.put(groupName,groupChatGUI);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
    //好友列表界面的标签监听
    private class PrivateLabelAction implements MouseListener {
        private String labelName;
        public PrivateLabelAction(String labelName){
            this.labelName = labelName;
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            //查看私聊界面缓存中是否存在该标签
            if(privateChatGUIList.containsKey(labelName)){
                PrivateChatGUI privateChatGUI = privateChatGUIList.get(labelName);
                privateChatGUI.getFrame().setVisible(true);
            }else{
                //第一次点击，创建私聊界面
                PrivateChatGUI privateChatGUI = new PrivateChatGUI(labelName,userName,connectToServer);
                privateChatGUIList.put(labelName,privateChatGUI);
            }

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    public friendsList(String username, Set<String> users, ConnectToServer connectToServer){

        this.userName = username;
        this.users = users;
        this.connectToServer = connectToServer;
        this.frame = new JFrame(userName);
        frame.setContentPane(friendsPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        loadUserList();
        //启动后台进程不断监听服务器端发来的信息
        Thread daemonThread = new Thread(new DaemonTask());
        daemonThread.setDaemon(true);
        daemonThread.start();

        createGroupBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateGroupGUI(userName,users,connectToServer,friendsList.this);
            }
        });
    }
    //加载好友列表
    private void loadUserList() {
        JLabel[] userLabel = new JLabel[users.size()];
        JPanel friends = new JPanel();
        friends.setLayout(new BoxLayout(friends,BoxLayout.Y_AXIS));
        //遍历用户的set集合，添加进好友列表
        Iterator<String> iterator = users.iterator();
        int i = 0;
        while(iterator.hasNext()){
            String userName = iterator.next();
            userLabel[i] = new JLabel(userName);
            //添加标签点击事件
           userLabel[i].addMouseListener(new PrivateLabelAction(userName));
            friends.add(userLabel[i]);
            i++;
        }
        //将friends添加至friends中
        friendslist.setViewportView(friends);
        //设置滚动条垂直滚动
        friendslist.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        friends.revalidate();
        friendslist.revalidate();
    }
    //添加新群
    public void loadGroupList(){
        // 存储所有群名称标签Jpanel
        JPanel groupNamePanel = new JPanel();
        groupNamePanel.setLayout(new BoxLayout(groupNamePanel,
                BoxLayout.Y_AXIS));
        JLabel[] labels = new JLabel[groupsList.size()];
        // Map遍历
        Set<Map.Entry<String,Set<String>>> entries = groupsList.entrySet();
        Iterator<Map.Entry<String,Set<String>>> iterator =
                entries.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<String,Set<String>> entry = iterator.next();
            labels[i] = new JLabel(entry.getKey());
            labels[i].addMouseListener(new GroupLabelAction(entry.getKey()));
            groupNamePanel.add(labels[i]);
            i++;
        }
        groupList.setViewportView(groupNamePanel);
        groupList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        groupList.revalidate();
    }
    public void addGroup(String groupName,Set<String> selectedFriends){
       groupsList.put(groupName,selectedFriends);
    }
}
