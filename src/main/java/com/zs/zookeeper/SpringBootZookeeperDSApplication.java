package com.zs.zookeeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication
public class SpringBootZookeeperDSApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootZookeeperDSApplication.class, args);
    }

    @Bean
    public ServletListenerRegistrationBean servletListenerRegistrationBean() {
        ServletListenerRegistrationBean servletListenerRegistrationBean = new ServletListenerRegistrationBean();
        servletListenerRegistrationBean.setListener(new InitListener());
        return servletListenerRegistrationBean;
    }

    @Bean
    public DataSource dataSource() {
        MyDataSource dataSource = new MyDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/zk_datasource_1?serverTimezone=GMT%2B8");
        dataSource.setUsername("root");
        dataSource.setPassword("handsomez@666");
        dataSource.setDefaultReadOnly(false);
        return dataSource;
    }


    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}
