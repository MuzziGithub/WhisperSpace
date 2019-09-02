package com.bittech.java.server;

import com.bittech.java.util.CommUtils;
import com.bittech.java.vo.MessageVO;

import javax.swing.event.CaretListener;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {
    private static final String IP;
    private static final int PORT;
    //使用Map缓存当前服务器所有在线的客户端信息
    private static Map<String,Socket> clients = new ConcurrentHashMap<>();
    //使用Map缓存所有注册的群名和群成员
    private static Map<String,Set<String>> groups = new ConcurrentHashMap<>();
    //在类加载时堆socket套接字进行初始化
    static{
        //1.加载资源文件
        Properties props = CommUtils.loadProperties("socket.properties");
        IP = props.getProperty("address");
        PORT = Integer.parseInt(props.getProperty("port"));

    }
    //在创建客户端连接的时候需要创建新的线程区处理，要不然accept()会形成阻塞
    //创建一个内部类，用于处理客户端连接，与客户端进行交互
    private static class ExecuteClient implements Runnable{
        //客户端是一个Socket类
        private Socket client;
        //客户端与服务器进行交互只需要只要客户端的输入输出流就可以了，不需要对整个socket进行操作。
        private Scanner in;
        private PrintStream out;
        //用于交互的类
        public ExecuteClient(Socket client){
            this.client = client;
            try {
                this.in = new Scanner(client.getInputStream());
                this.out = new PrintStream(client.getOutputStream(),true,"UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run(){
            while(true){
                if(in.hasNextLine()){
                    //获取客户端输入流
                   String jsonFromClient = in.nextLine();
                   //将获取到的信息转化为指定格式
                   MessageVO msgFromClient = (MessageVO) CommUtils.jsonToObject(jsonFromClient,MessageVO.class);
                   //将用户注册到服务器端
                   if(msgFromClient.getType().equals("1")){
                       /**
                        * type:1
                        * content:username
                        */
                       String userName = msgFromClient.getContent();
                       //将当前所有上线用户名发送给客户端
                       MessageVO msgToClient = new MessageVO();
                       msgToClient.setType("1");
                       msgToClient.setContent(CommUtils.objectToJson(clients.keySet()));
                       out.println(CommUtils.objectToJson(msgToClient));
                       //将当前用户的上线信息发送给所有在线用户
                       sendUserLongin("newLogin:"+userName);
                       //将当前用户信息保存到服务器当前在线客户缓存中
                       clients.put(userName,client);
                       //服务器端显示
                       System.out.println(userName+"上线了！");
                       System.out.println("当前聊天室共有："+clients.size()+"人");
                   }else if(msgFromClient.getType().equals("2")){
                       //私聊
                       /**
                        * type:2
                        * content:userName-msg
                        * to:friendsName
                        */
                       String friendName = msgFromClient.getTo();
                       Socket clientSocket = clients.get(friendName);
                       try {
                           PrintStream out = new PrintStream(clientSocket.getOutputStream(),
                                   true,"UTF-8");
                           MessageVO msg2Client = new MessageVO();
                           msg2Client.setType("2");
                           msg2Client.setContent(msgFromClient.getContent());
                           System.out.println("收到私聊信息,内容为"+msgFromClient.getContent());
                           out.println(CommUtils.objectToJson(msg2Client));
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }else if(msgFromClient.getType().equals("3")){
                       /**
                        * 注册群
                        * type:3
                        * content:群名
                        * to:[user1,user2,...]
                        */
                       String groupName = msgFromClient.getContent();
                       // 该群的所有群成员
                       Set<String> friends = (Set<String>) CommUtils.jsonToObject(
                                   msgFromClient.getTo(),Set.class);
                           groups.put(groupName,friends);
                           System.out.println("有新的群注册成功,群名称为"+
                                   groupName+",一共有"+groups.size() + "个群");
                   }else if (msgFromClient.getType().equals("4")) {
                       // 群聊信息
                       System.out.println("服务器收到的群聊信息为:"+msgFromClient);
                       String groupName = msgFromClient.getTo();
                       Set<String> names = groups.get(groupName);
                       Iterator<String> iterator = names.iterator();
                       while (iterator.hasNext()) {
                           String socketName = iterator.next();
                           Socket client = clients.get(socketName);
                           try {
                               PrintStream out = new PrintStream(client.getOutputStream(),
                                       true,"UTF-8");
                               MessageVO messageVO = new MessageVO();
                               messageVO.setType("4");
                               messageVO.setContent(msgFromClient.getContent());
                               // 群名-[]
                               messageVO.setTo(groupName+"-"+CommUtils.objectToJson(names));
                               out.println(CommUtils.objectToJson(messageVO));
                               //System.out.println("服务端发送的群聊信息为:"+messageVO);
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }
                   }
                }
            }
        }

        /**
         * 发送新用户上线信息
         * @param s
         */
        private void sendUserLongin(String s) {
            //private static Map<String,Socket> clients = new ConcurrentHashMap<>();
            //for循环遍历缓存当前登陆客户端信息的map,给每个客户发送新用户上线信息
            for(Map.Entry<String,Socket> entry:clients.entrySet()){
                Socket socket = entry.getValue();
                try {
                    PrintStream out = new PrintStream(socket.getOutputStream(),true,"UTF-8");
                    out.println(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args) throws IOException {
        //新建一个serverSocket,在本地建立基站
        ServerSocket serverSocket = new ServerSocket(PORT);
        //使用线程池创建线程.最大支持50个线程并发
        ExecutorService executors = Executors.newFixedThreadPool(50);
        for(int i = 0; i < 50;i++){
            System.out.println("等待客户端连接...");
            //获取客户端连接，若未连接，则一直等待，直到连接
            Socket client = serverSocket.accept();
            System.out.println("有新的客户端连接，端口号为："+client.getPort());
            //具体的连接交给子线程去处理，服务器继续监听新的客户端连接
            executors.submit(new ExecuteClient(client));

        }
    }
}
