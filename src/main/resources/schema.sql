DROP TABLE IF EXISTS orders;

CREATE TABLE orders (
                        id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                        order_no      VARCHAR(32)    NOT NULL,
                        customer_name VARCHAR(64)    NOT NULL,
                        amount        DECIMAL(10, 2) NOT NULL,
                        status        TINYINT        NOT NULL DEFAULT 0,
                        created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);