package com.bittech.java.util;

import com.bittech.java.client.entity.User;
import org.junit.Assert;
import org.junit.Test;
import lombok.Data;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;
public class CommUtilsTest {

    @Test   //单元测试
    public void loadProperties() {
        String fileName = "datasource.properties";
        Properties properties = CommUtils.loadProperties(fileName);
        //System.out.println(properties);
        Assert.assertNotNull(properties);
    }

    @Test
    public void objectToJson() {
        User user = new User();
        user.setId(1);
        user.setUserName("test");
        user.setPassword("123");
        user.setBrief("真好");
        Set<String> strings = new HashSet<>();
        strings.add("test1");
        strings.add("test2");
        strings.add("test3");
        user.setUserNames(strings);
        String str = CommUtils.objectToJson(user);
        System.out.println(str);
    }

    @Test
    public void jsonToObject() {
        String jsonStr = "{\"id\":1,\"userName\":\"test\",\"password\":\"123\",\"brief\":\"真好\",\"userNames\":[\"test2\",\"test3\",\"test1\"]}";
        User user = (User)CommUtils.jsonToObject(jsonStr,User.class);
        System.out.println(user.getUserNames());
    }
}