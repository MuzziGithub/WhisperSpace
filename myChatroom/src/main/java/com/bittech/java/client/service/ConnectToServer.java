package com.bittech.java.client.service;

import com.bittech.java.util.CommUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

/**
 * 用于与服务器端建立连接
 */
public class ConnectToServer {
    private static final String IP;
    private static final int PORT;
    static{
        //1.加载资源文件
        Properties props = CommUtils.loadProperties("socket.properties");
        IP = props.getProperty("address");
        PORT = Integer.parseInt(props.getProperty("port"));
    }
    private Socket client;
    //获取客户端的输入输出流
    private InputStream in;
    private OutputStream out;

    public ConnectToServer(){
        try {
            client = new Socket(IP,PORT);
            in = client.getInputStream();
            out = client.getOutputStream();
        } catch (IOException e) {
            System.out.println("与服务器建立连接失败");
            e.printStackTrace();
        }
    }
    //在不同页面之间传递的时候只需要获取输入输出流就可以了。拿输入流获取服务器发来的信息，拿输出流给服务器发送信息。
    public InputStream getIn() {
        return in;
    }

    public OutputStream getOut() {
        return out;
    }
}
