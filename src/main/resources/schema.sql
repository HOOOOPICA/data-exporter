DROP TABLE IF EXISTS orders;

CREATE TABLE orders (
                        id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                        order_no      VARCHAR(32)    NOT NULL,
                        customer_name VARCHAR(64)    NOT NULL,
                        amount        DECIMAL(10, 2) NOT NULL,
                        status        TINYINT        NOT NULL DEFAULT 0,
                        created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE export_task (
                         id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                         status        TINYINT      NOT NULL DEFAULT 0,  -- 0待处理 1进行中 2成功 3失败
                         file_path     VARCHAR(255),                     -- 生成的文件路径
                         error_message VARCHAR(500),                     -- 失败时记录原因
                         created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 任务创建时间
                         finished_at   TIMESTAMP                         -- 任务完成时间（你说的export_time）
);

CREATE TABLE customers (
                           id             BIGINT AUTO_INCREMENT PRIMARY KEY,
                           customer_name  VARCHAR(64) NOT NULL,
                           level          VARCHAR(16) NOT NULL  -- NORMAL, VIP, SVIP
);

CREATE TABLE products (
                          id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                          order_no      VARCHAR(32) NOT NULL,
                          product_name  VARCHAR(128) NOT NULL
);