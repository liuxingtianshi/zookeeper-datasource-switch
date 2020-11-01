package com.zs.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.SQLException;

/**
 * ServletContextListener在web程序运行期间一直监听
 */
public class InitListener implements ServletContextListener {

    private static final String CONNENT_ADDR = "127.0.0.1:2181";
    private static final String URL = "/db/url";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNENT_ADDR)
                .connectionTimeoutMs(5000)  //连接超时时间
                .sessionTimeoutMs(5000)     //会话超时时间
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorFramework.start();

        // 监听ZK数据源节点的变化，如果节点数据源发生变化（CRUD），则进行回调（切换数据源）
        final NodeCache cache = new NodeCache(curatorFramework, URL, false);
        cache.getListenable().addListener(new NodeCacheListener() {

            @Override
            public void nodeChanged() throws Exception {
                if (URL.equals(cache.getCurrentData().getPath())) {
                    try {
                        MyDataSource datasource = (MyDataSource) RuntimeContext.getBean("dataSource");
                        String newConnStr = new String(cache.getCurrentData().getData());
                        System.out.println("切换到新的数据源：" + newConnStr);
                        datasource.setUrl(newConnStr);
                        datasource.changeDataSource();
                    } catch (Exception e) {
                        if(e instanceof NullPointerException){
                            System.out.println("DatasourceBean为空");
                        }else{
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

        try {
            cache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        MyDataSource datasource = (MyDataSource) RuntimeContext.getBean("dataSource");
        try {
            datasource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}