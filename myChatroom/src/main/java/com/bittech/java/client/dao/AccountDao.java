package com.bittech.java.client.dao;

import com.bittech.java.client.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;

/**
 *用户具体JDBC操作
 */
public class AccountDao extends BaseDao {
    //注册操作：返回注册成功或失败
    //主要执行语句：insert
    //参数：dao层是用户来调用的，业务在传入、登录、注册的时候传入的都应该是数据库的实体类，也就是传递的是具体的对象，堆实体对象进行操作
    public boolean userRegister(User user){
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            //使用继承父类的方法getConnection()来获取连接
            connection = getConnection();
            //先预编译sql语句，然后执行sql语句
            //sql语句中插入的三个参数都需要从外部传入
            String sql = "INSERT INTO my_chatroom.user(username, password, brief) "+" VALUES(?,?,?) ";
            //运行sql语句，并返回受影响的行数，用于下面检测插入的行数是否正确
            statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            //设置三个参数
            statement.setString(1,user.getUserName());
            //对密码进行加密：DIgestUtils.mdsHex()
            statement.setString(2,DigestUtils.md5Hex(user.getPassword()));
            statement.setString(3,user.getBrief());
            //获取受影响的行数
            int rows = statement.executeUpdate();
            if(rows == 1){
                return true;
            }
        }catch (SQLException e) {
            System.out.println("用户注册失败");
            e.printStackTrace();
        }finally{
            closeResources(connection,statement);
        }
        return false;
    }
    //用户登录:登陆成功，返回User类，失败返回null
    public User userLogin(String username,String password){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try{
            connection = getConnection();
            String sql = "SELECT * FROM my_chatroom.user WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1,username);
            statement.setString(2,DigestUtils.md5Hex(password));
            //返回的结果都都保存在resultSet中，要返回一个user实例，必须经过一个从结果集到实例对象的过程，所以需要一个方法来做这件事情
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                User user = getUser(resultSet);
                return user;
            }
        }catch(SQLException e){
            System.out.println("用户登录失败");
            e.printStackTrace();
        }finally{
            closeResources(connection,statement,resultSet);
        }
        System.out.println("用户登录失败");
        return null;
    }
    private User getUser(ResultSet resultSet)throws SQLException{
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUserName(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setBrief(resultSet.getString("brief"));
        return user;
    }
}
