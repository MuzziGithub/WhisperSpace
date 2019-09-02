package com.bittech.java.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 封装公共工具方法，如加载配置文件、json序列化等
 */
public class CommUtils {
    //创建第三方库对象
    // private static final Gson GSON = new GsonBuilder().create();序列化所依赖的库
    private static final Gson GSON = new GsonBuilder().create();
    /**
     * 加载配置文件
     *
     * @param fileName 要加载的配置文件名称
     * @return
     */
    //CTRL+shift+T 创建单元测试
    public static Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        InputStream in = CommUtils.class.getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(in);
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
        return properties;
    }

    /**
     * 将任意对象序列化成json字符串
     * @param obj
     * @return
     */
    public static String objectToJson(Object obj){
        return GSON.toJson(obj);
    }

    /**
     * 将任意json字符串反序列化为对象
     * @param jsonStr json字符串
     * @param objClass 反序列化的类反射对象
     * @return
     */
    public static Object jsonToObject(String jsonStr,Class objClass){
        return GSON.fromJson(jsonStr,objClass);
    }

}
