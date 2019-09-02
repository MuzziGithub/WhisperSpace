package com.bittech.java.client.service;

import com.bittech.java.client.dao.AccountDao;
import com.bittech.java.client.entity.User;
import com.bittech.java.util.CommUtils;
import com.bittech.java.vo.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Set;

public class UserLogin {
    private JPanel userLogin;
    private JPanel userPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel buttonPanel;
    private JButton registerBtn;
    private JButton loginBtn;
    //加载客户端的数据库操作类，用户登录时检验用户信息
    private AccountDao accountDao = new AccountDao();

    public UserLogin() {
        JFrame frame = new JFrame("用户登录");
        frame.setContentPane(userLogin);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //窗体默认置中
        frame.setLocationRelativeTo(null);
        frame.setSize(400,500);
        //frame.pack();
        frame.setVisible(true);
        //注册按钮监听
        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //当按钮被点击后弹出注册页面
                new UserReg();
            }
        });
        //登录按钮监听
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //1.校验用户信息
                String username = usernameField.getText();
                //因为password是char型，所以要包装一下
                String password = String.valueOf(passwordField.getPassword());
                User use = accountDao.userLogin(username,password);
                if(use != null){
                    //成功，加载用户列表
                    JOptionPane.showMessageDialog(frame,"登录成功","提示信息",JOptionPane.INFORMATION_MESSAGE);
                    //登录成功之后将当前登录界面关闭
                    frame.setVisible(false);
                    //与服务器建立连接，将当前用户的用户名与密码发送到服务器端
                    //与服务器创建连接,产生一个连接对象
                    ConnectToServer connectToServer = new ConnectToServer();
                    //格式化发送信息
                    MessageVO messageVO = new MessageVO();
                    messageVO.setType("1");
                    messageVO.setContent(username);
                    //将要发送的内容转为json字符串
                    String jsonToServer = CommUtils.objectToJson(messageVO);
                    try {
                        //向服务端发送登录信息
                        PrintStream out = new PrintStream(connectToServer.getOut(),true,"UTF-8");
                        out.println(jsonToServer);
                        //读取服务器端回应信息
                        Scanner in = new Scanner(connectToServer.getIn());
                        if (in.hasNextLine()) {
                            String msgFromServerStr = in.nextLine();
                            MessageVO msgFromServer =
                                    (MessageVO) CommUtils.jsonToObject(msgFromServerStr, MessageVO.class);
                            //获取当前在线用户集合
                            Set<String> users =
                                    (Set<String>) CommUtils.jsonToObject(msgFromServer.getContent(), Set.class);
                            System.out.println("所有在线用户为:"+users);
                            // 加载用户列表界面
                            // 将当前用户名、所有在线好友、与服务器建立连接传递到好友列表界面
                            new friendsList(username,users,connectToServer);
                        }
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                }else {
                    // 失败，停留在当前登录页面，提示用户信息错误
                    JOptionPane.showMessageDialog(frame, "登录失败!","错误信息", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        UserLogin login = new UserLogin();
    }
}
