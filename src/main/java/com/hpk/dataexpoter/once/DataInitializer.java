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
        if (ordersTableExists()) {
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Long.class);
            if (count != null && count > 0) {
                System.out.println("数据已存在（" + count + "条），跳过初始化");
                return;
            }
        } else {
            System.out.println("orders 表不存在，正在创建表结构...");
            createOrdersTable();
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

    private boolean ordersTableExists() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                        "WHERE TABLE_SCHEMA = CURRENT_SCHEMA() AND TABLE_NAME = 'ORDERS'",
                Long.class);
        return count != null && count > 0;
    }

    private void createOrdersTable() {
        jdbcTemplate.execute("""
                CREATE TABLE orders (
                    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                    order_no      VARCHAR(32)    NOT NULL,
                    customer_name VARCHAR(64)    NOT NULL,
                    amount        DECIMAL(10, 2) NOT NULL,
                    status        TINYINT        NOT NULL DEFAULT 0,
                    created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
    }
}
