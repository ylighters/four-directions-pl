-- MySQL 8.0+
-- 订单分表DDL（建议 64 分片，可按业务量扩到 128/256）
-- 路由键: merchant_no_hash % 64

USE payment_center;

CREATE TABLE IF NOT EXISTS pay_order_template (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no            VARCHAR(64) NOT NULL COMMENT '平台支付单号',
    merchant_no         VARCHAR(32) NOT NULL,
    app_id              VARCHAR(64) NOT NULL,
    merchant_order_no   VARCHAR(64) NOT NULL,
    pay_scene           VARCHAR(32) NOT NULL COMMENT 'APP/H5/JSAPI/QR',
    pay_way             VARCHAR(32) NOT NULL COMMENT 'ALIPAY/WX/UNION',
    channel_code        VARCHAR(32) NOT NULL,
    channel_order_no    VARCHAR(64) DEFAULT NULL,
    amount              BIGINT NOT NULL COMMENT '支付金额(分)',
    currency            CHAR(3) NOT NULL DEFAULT 'CNY',
    status              VARCHAR(32) NOT NULL,
    subject             VARCHAR(128) NOT NULL,
    body                VARCHAR(512) DEFAULT NULL,
    notify_url          VARCHAR(512) DEFAULT NULL,
    return_url          VARCHAR(512) DEFAULT NULL,
    expire_time         DATETIME(3) DEFAULT NULL,
    success_time        DATETIME(3) DEFAULT NULL,
    fail_reason         VARCHAR(256) DEFAULT NULL,
    version             INT NOT NULL DEFAULT 0 COMMENT '乐观锁',
    deleted             TINYINT NOT NULL DEFAULT 0,
    created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_order_no (order_no),
    UNIQUE KEY uk_merchant_order (merchant_no, app_id, merchant_order_no),
    KEY idx_merchant_status_time (merchant_no, status, created_at),
    KEY idx_channel_order_no (channel_order_no),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB;

DELIMITER $$
DROP PROCEDURE IF EXISTS create_pay_order_shards $$
CREATE PROCEDURE create_pay_order_shards(IN shard_count INT)
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE table_name VARCHAR(64);
    DECLARE sql_text TEXT;

    WHILE i < shard_count DO
        SET table_name = CONCAT('pay_order_', LPAD(i, 2, '0'));
        SET sql_text = CONCAT('CREATE TABLE IF NOT EXISTS ', table_name, ' LIKE pay_order_template');
        PREPARE stmt FROM sql_text;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SET i = i + 1;
    END WHILE;
END $$
DELIMITER ;

CALL create_pay_order_shards(64);

-- 线上执行后可删除模板表（如不需要）
-- DROP TABLE pay_order_template;

-- 示例: 单独查看某个分片表DDL
-- SHOW CREATE TABLE pay_order_00;

-- 可选: 按月归档历史订单，保持热数据可控
-- CREATE TABLE pay_order_202603_00 LIKE pay_order_00;
