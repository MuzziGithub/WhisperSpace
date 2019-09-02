package com.bittech.java.client.service;

import com.bittech.java.client.dao.AccountDao;
import com.bittech.java.client.entity.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UserReg {
    private JPanel userRegPanel;
    private JTextField userNameText;
    private JPasswordField passwordText;
    private JTextField briefText;
    private JButton registerBtn;

    //创建数据库操作类对象，用于向数据库中保存注册数据
    private AccountDao accountDao = new AccountDao();

    public UserReg(){
        //输出窗体的名称可以修改
        JFrame frame = new JFrame("用户注册");
        //设置最外层大盘子
        //主方法需要通过构造new一个对象才有里面的属性，因为这就是构造方法，所以可以直接使用属性，不能再次调用构造，否则会造成无限循环
        //frame.setContentPane(new UserReg().userRegPanel);
        frame.setContentPane(userRegPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //当新建一个界面的时候的放置位置
        //在Java中每个窗体都是一个线程，只有当把窗体关闭后线程才能终止
        frame.setLocationRelativeTo(null);
        //窗口显示默认大小，也可以通过setSize重置窗口大小
        frame.pack();
        frame.setVisible(true);
        //点击注册按钮，将信息持久化到数据库db中，成功传出提示框
        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //1.获取用户输入的用户信息
                String userName = userNameText.getText();
                String password = String.valueOf(passwordText.getPassword());
                String brief = briefText.getText();
                //2.将输入的信息包装成User类，然后保存到数据库中
                User user = new User();
                user.setUserName(userName);
                user.setPassword(password);
                user.setBrief(brief);
                //调用dao对象,进行用户注册
                boolean flag = accountDao.userRegister(user);
                if(flag){
                    //注册成功，返回登陆页面
                    //弹出提示框
                    JOptionPane.showMessageDialog(frame,"注册成功！","提示信息",JOptionPane.INFORMATION_MESSAGE);
                    //将当前注册页面置为不可见
                    frame.setVisible(false);
                }else{
                    //注册失败，应该保留注册页面
                    JOptionPane.showMessageDialog(frame,"注册失败","错误信息",JOptionPane.ERROR_MESSAGE);
                }

            }
        });
    }
}
