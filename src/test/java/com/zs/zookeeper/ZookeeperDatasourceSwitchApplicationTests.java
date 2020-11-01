package com.zs.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ZookeeperDatasourceSwitchApplicationTests {

    private CuratorFramework curatorClient = null;

    private final String path = "/db/url";

    @BeforeEach
    void getZkConnection() {

        RetryPolicy exponentialBackoffRetry = new ExponentialBackoffRetry(1000, 3);

        // 使用fluent编程风格
        curatorClient = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(50000)
                .connectionTimeoutMs(30000)
                .retryPolicy(exponentialBackoffRetry)
                .namespace(null)
                .build();

        curatorClient.start();
        System.out.println("会话创建了");
    }

    @Test
    void deleteNode() throws Exception {
        curatorClient.delete().deletingChildrenIfNeeded().withVersion(-1).forPath(path);
        System.out.println("删除成功，删除的节点" + path);
    }


    @Test
    void createNode() throws Exception {
        //创建数据源节点
        String connectString = "jdbc:mysql://localhost:3306/zk_datasource_2?serverTimezone=GMT%2B8";
        String s = curatorClient.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT).forPath(path, connectString.getBytes());
    }

    @Test
    void getNode() throws Exception {

        // 数据内容
        byte[] bytes = curatorClient.getData().forPath(path);
        System.out.println("获取到的节点数据内容：" + new String(bytes));

        // 状态信息
        Stat stat = new Stat();
        curatorClient.getData().storingStatIn(stat).forPath(path);

        System.out.println("获取到的节点状态信息：" + stat );
    }

    @Test
    void updateNode() throws Exception {
        String connectString = "jdbc:mysql://localhost:3306/zk_datasource_1?serverTimezone=GMT%2B8";

        // 状态信息
        Stat stat = new Stat();
        byte[] bytes1 = curatorClient.getData().storingStatIn(stat).forPath(path);
        System.out.println("当前节点数据内容：" + new String(bytes1));

        // 更新节点内容
        int version = curatorClient.setData().withVersion(stat.getVersion()).forPath(path, connectString.getBytes()).getVersion();
        byte[] bytes2 = curatorClient.getData().forPath(path);
        System.out.println("修改后的节点数据内容：" + new String(bytes2));
    }

}
