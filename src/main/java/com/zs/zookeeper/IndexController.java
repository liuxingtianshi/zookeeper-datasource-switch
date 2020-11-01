package com.zs.zookeeper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/getInfo")
    public String getInfo() {
        String sql = "select name from user where id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, 1);
    }
}