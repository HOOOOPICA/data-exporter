package com.hpk.dataexpoter.once;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {

        // 先检查表里有没有数据
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Long.class);

        if (count != null && count > 0) {
            System.out.println("数据已存在（" + count + "条），跳过初始化");
            return;  // 有数据就直接退出，不重复插入
        }

        System.out.println("开始生成测试数据...");

        List<Object[]> batchData = new ArrayList<>();
        for (int i = 0; i < 50000; i++) {
            batchData.add(new Object[]{
               "ORD" + String.format("%08d", i),
               "Client" + i,
               Math.round(Math.random() * 9900 + 100) / 100.0,  // amount
               (int)(Math.random() * 3)             // status: 0,1,2
            });
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO orders (order_no, customer_name, amount, status) VALUES (?,?,?,?)",
                batchData
        );
        System.out.println("测试数据生成完毕！共50000条");
    }
}
