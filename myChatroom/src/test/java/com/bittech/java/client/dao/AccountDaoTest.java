package com.bittech.java.client.dao;

import com.bittech.java.client.entity.User;
import org.junit.Assert;
import org.junit.Test;

public class AccountDaoTest {
    private AccountDao accountDao = new AccountDao();
    @Test
    public void userRegister() {
        User user  = new User();
        user.setUserName("张三");
        user.setPassword("123");
        user.setBrief("。。。");
        boolean flag = accountDao.userRegister(user);
        Assert.assertTrue(flag);
        System.out.println(user);
    }
    @Test
    public void userLogin() {
        String username = "张三";
        String password = "123";
        User user = accountDao.userLogin(username,password);
        System.out.println(user);
        Assert.assertNotNull(user);
    }
}