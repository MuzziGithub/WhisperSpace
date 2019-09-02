package com.bittech.java.client.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.bittech.java.util.CommUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @Author:
 * @Data:2019-8-25
 * @Description:封装基础的dao操作，获取数据源、连接、关闭资源等
 */
public class BaseDao {
    //DruidDataSource:阿里的一个数据源
    private static DruidDataSource dataSource;
    //保证数据源的获取在类加载时就执行并且执行一次
    static{
        //加载阿里的数据源首先要获取数据源的配置文件
        Properties properties = CommUtils.loadProperties("datasource.properties");
        //数据源的创建：通过DriudDataSourcefactory(com.alibaba.druid.pool)创建
        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            //错误原因
            System.out.println("数据源加载失败");
            //打印错误堆栈
            e.printStackTrace();
        }
    }
    //提供一个方法：用于获取连接，需要继承权限，只有继承了BaseDao才有能力获取连接
    //DruidPooledConnection:关于池的一个连接
    protected DruidPooledConnection getConnection(){
        try {
            return (DruidPooledConnection) dataSource.getPooledConnection();
        } catch (SQLException e) {
            System.out.println("数据库连接获取失败");
            e.printStackTrace();
        }
        //如果获取连接失败返回null
        return null;
    }
    //关闭资源1：对于更新操作，只关闭statement和connection，无需关闭resultSet


    protected void closeResources(Connection connection, Statement statement){
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
    protected  void closeResources(Connection connection, Statement statement, ResultSet resultSet){
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
