package com.bittech.java;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.bittech.java.util.CommUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Properties;

 /*import com.alibaba.druid.pool.DruidDataSource;
        import com.alibaba.druid.pool.DruidDataSourceFactory;
        import com.bittech.java.util.CommUtils;
        import javax.sql.ConnectionEvent;
        import javax.sql.DataSource;
        import java.sql.*;
        import java.util.Properties;
        import com.alibaba.druid.pool.DruidDataSource;
        import com.alibaba.druid.pool.DruidDataSourceFactory;
        import com.bittech.java.util.CommUtils;
        import org.apache.commons.codec.digest.DigestUtils;
        import org.jcp.xml.dsig.internal.DigesterOutputStream;
        import org.junit.jupiter.api.Test;
//        import org.junit.Test;
*/
public class JDBCTest {
    private static DruidDataSource dataSource;
    //静态加载资源文件
    static{
        Properties props = CommUtils.loadProperties("datasource.properties");

        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testQuery(){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try{
            //需要使用连接池的datasource数据源，因此在主函数里创建数据源的时候不能创建接口类型，必须是DruidDataSource
            connection = (Connection)dataSource.getPooledConnection();
            //String sql = "SELECT * FROM my_chatroom.user WHERE userName = ? AND password = ?";
            String sql = "SELECT * FROM my_chatroom.user";
            statement = connection.prepareStatement(sql);
            /*String user = "test";
            String pass = "123";
            statement.setString(1,user);
            statement.setString(2,pass);
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                System.out.println("登陆成功");
            }else{
                System.out.println("登陆失败");
            }*/
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String userName = resultSet.getString("username");
                String password = resultSet.getNString("password");
                String brief = resultSet.getNString("brief");
                System.out.println("id为"+id+"，用户名为："+userName+",密码为："+password+"，简介为："+brief);
            }
        }catch (SQLException e){

        }finally{
            closeResources(connection,statement,resultSet);
        }
    }
    @Test
    public void testInsert(){
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = (Connection) dataSource.getPooledConnection();
            String password = DigestUtils.md5Hex("123");
            String sql = "INSERT INTO my_chatroom.user(username,password,brief)"+"VALUES(?,?,?)";
            statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,"test1");
            statement.setString(2,password);
            statement.setString(3,"真帅");
            int rows = statement.executeUpdate();
            Assert.assertEquals(1,rows);
        }catch(SQLException e){

        }finally {
            closeResources(connection,statement);
        }
    }
    @Test
    public void testLogin(){

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try{
            connection = (Connection) dataSource.getPooledConnection();
            String sql = "SELECT * FROM my_chatroom.user WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(sql);
            String userName = "test1";
            String password = "123";
            statement.setString(1,userName);
            statement.setString(2,DigestUtils.md5Hex(password));
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                System.out.println("登录成功");
            }else{
                System.out.println("登录失败");
            }
        }catch(SQLException e){
            System.out.println("有异常");
        }finally{
            closeResources(connection,statement,resultSet);
        }
    }
    //资源关闭的方法1：用于更新、删除和插入connection和statement资源
    public void closeResources(Connection connection, Statement statement){
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //资源关闭方法2：相比上面多了一个resultSet资源
    public void closeResources(Connection connection,Statement statement,ResultSet resultSet){
        closeResources(connection,statement);
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
