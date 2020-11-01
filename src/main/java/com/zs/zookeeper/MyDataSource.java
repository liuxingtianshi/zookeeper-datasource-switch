package com.zs.zookeeper;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.SQLNestedException;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.sql.SQLException;

public class MyDataSource extends BasicDataSource {

    // 定义数据源的切换
    public static void changeDataSource() {

        MyDataSource newDataSource = (MyDataSource) RuntimeContext.getBean("dataSource");
        try {
            newDataSource.close();
            newDataSource.createDataSource();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void close() throws SQLException {
        // 这个地方注释掉是因为父类的createDataSource判断close则抛出异常程序不执行了
        // closed = true;
        GenericObjectPool oldpool = connectionPool;
        connectionPool = null;
        dataSource = null;
        try {
            if (oldpool != null) {
                oldpool.close();
            }
        } catch(SQLException e) {
            throw e;
        } catch(RuntimeException e) {
            throw e;
        } catch(Exception e) {
            throw new SQLNestedException("Cannot close connection pool", e);
        }
    }

}
