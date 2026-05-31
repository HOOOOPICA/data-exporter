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
        ensureTablesExist();

        boolean ordersSeeded = seedOrdersIfEmpty();
        boolean customersSeeded = seedCustomersIfEmpty();
        boolean productsSeeded = seedProductsIfEmpty();

        if (!ordersSeeded && !customersSeeded && !productsSeeded) {
            System.out.println("数据已存在，跳过初始化");
        }
    }

    private void ensureTablesExist() {
        if (!tableExists("orders")) {
            System.out.println("orders 表不存在，正在创建表结构...");
            createOrdersTable();
        }
        if (!tableExists("customers")) {
            System.out.println("customers 表不存在，正在创建表结构...");
            createCustomersTable();
        }
        if (!tableExists("products")) {
            System.out.println("products 表不存在，正在创建表结构...");
            createProductsTable();
        }
        if(!tableExists("export_task")){
            System.out.println("export_task 表不存在，正在创建表结构...");
            createExportTaskTable();
        }
    }

    private boolean seedOrdersIfEmpty() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Long.class);
        if (count != null && count > 0) {
            System.out.println("orders 数据已存在（" + count + "条），跳过初始化");
            return false;
        }

        System.out.println("开始生成 orders 测试数据...");
        List<Object[]> batchData = new ArrayList<>();
        for (int i = 1; i < 50000; i++) {
            batchData.add(new Object[]{
                    "ORD" + String.format("%08d", i),
                    "Client" + i,
                    Math.round(Math.random() * 9900 + 100) / 100.0,
                    (int) (Math.random() * 3)
            });
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO orders (order_no, customer_name, amount, status) VALUES (?,?,?,?)",
                batchData
        );
        System.out.println("orders 测试数据生成完毕！共50000条");
        return true;
    }

    private boolean seedCustomersIfEmpty() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM customers", Long.class);
        if (count != null && count > 0) {
            System.out.println("customers 数据已存在（" + count + "条），跳过初始化");
            return false;
        }

        System.out.println("开始生成 customers 测试数据...");
        List<Object[]> batchData = new ArrayList<>();
        for (int i = 1; i < 50000; i++) {
            batchData.add(new Object[]{
                    "Client" + i,
                    randomCustomerLevel()
            });
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO customers (customer_name, level) VALUES (?,?)",
                batchData
        );
        System.out.println("customers 测试数据生成完毕！共50000条");
        return true;
    }

    private boolean seedProductsIfEmpty() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Long.class);
        if (count != null && count > 0) {
            System.out.println("products 数据已存在（" + count + "条），跳过初始化");
            return false;
        }

        Long ordersCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Long.class);
        if (ordersCount == null || ordersCount == 0) {
            System.out.println("orders 为空，无法生成 products（缺少 order_no），跳过 products 初始化");
            return false;
        }

        System.out.println("开始生成 products 测试数据...");
        final int maxOrdersForProducts = 50000;
        List<String> orderNos = jdbcTemplate.queryForList(
                "SELECT order_no FROM orders ORDER BY id LIMIT ?",
                String.class,
                Math.min(maxOrdersForProducts, ordersCount.intValue())
        );

        List<Object[]> batchData = new ArrayList<>();
        for (String orderNo : orderNos) {
            // 每个orderNo 只插入1条产品
            batchData.add(new Object[]{ orderNo, randomProductName() });
        }

        jdbcTemplate.batchUpdate(
                "INSERT INTO products (order_no, product_name) VALUES (?,?)",
                batchData
        );
        System.out.println("products 测试数据生成完毕！共" + batchData.size() + "条");
        return true;
    }

    private boolean tableExists(String tableName) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                        "WHERE TABLE_SCHEMA = CURRENT_SCHEMA() AND TABLE_NAME = ?",
                Long.class,
                tableName.toUpperCase()
        );
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

    private void createCustomersTable() {
        jdbcTemplate.execute("""
                CREATE TABLE customers (
                    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
                    customer_name  VARCHAR(64) NOT NULL,
                    level          VARCHAR(16) NOT NULL
                )
                """);
    }

    private void createProductsTable() {
        jdbcTemplate.execute("""
                CREATE TABLE products (
                    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                    order_no      VARCHAR(32) NOT NULL,
                    product_name  VARCHAR(128) NOT NULL
                )
                """);
    }

    private void createExportTaskTable() {
        jdbcTemplate.execute("""
                CREATE TABLE export_task (
                          id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                          status        TINYINT      NOT NULL DEFAULT 0,  -- 0待处理 1进行中 2成功 3失败
                          file_path     VARCHAR(255),                     -- 生成的文件路径
                          error_message VARCHAR(500),                     -- 失败时记录原因
                          created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 任务创建时间
                          finished_at   TIMESTAMP                         -- 任务完成时间（你说的export_time）
                 );
                """);
    }

    private String randomCustomerLevel() {
        int r = (int) (Math.random() * 100);
        if (r < 70) return "NORMAL";
        if (r < 95) return "VIP";
        return "SVIP";
    }

    private String randomProductName() {
        String[] pool = new String[]{
                "Keyboard", "Mouse", "Monitor", "Laptop Stand", "USB-C Cable",
                "SSD 1TB", "Router", "Headset", "Webcam", "Docking Station"
        };
        return pool[(int) (Math.random() * pool.length)];
    }
}
