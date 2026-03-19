-- MySQL 8.0+
-- 聚合支付中台完整基础表结构（不含订单分表，分表DDL在 02_order_sharding.sql）

CREATE DATABASE IF NOT EXISTS payment_center DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE payment_center;

CREATE TABLE IF NOT EXISTS merchant (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_no         VARCHAR(32) NOT NULL COMMENT '商户号',
    merchant_name       VARCHAR(128) NOT NULL,
    contact_mobile      VARCHAR(32) DEFAULT NULL,
    status              TINYINT NOT NULL DEFAULT 1 COMMENT '1-启用 0-禁用',
    sign_type           VARCHAR(16) NOT NULL DEFAULT 'RSA2',
    sign_public_key     TEXT,
    sign_private_key    TEXT,
    created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_merchant_no (merchant_no),
    KEY idx_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS merchant_app (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    app_id              VARCHAR(64) NOT NULL,
    merchant_no         VARCHAR(32) NOT NULL,
    app_name            VARCHAR(128) NOT NULL,
    app_secret          VARCHAR(128) NOT NULL,
    notify_url          VARCHAR(512) DEFAULT NULL,
    return_url          VARCHAR(512) DEFAULT NULL,
    status              TINYINT NOT NULL DEFAULT 1,
    created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_app_id (app_id),
    KEY idx_merchant_no (merchant_no),
    KEY idx_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pay_channel (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    channel_code        VARCHAR(32) NOT NULL COMMENT 'ALI_QR/WX_JSAPI等',
    channel_name        VARCHAR(64) NOT NULL,
    channel_type        VARCHAR(16) NOT NULL COMMENT 'ALIPAY/WECHAT/UNION',
    mch_id              VARCHAR(64) NOT NULL,
    api_config          JSON NOT NULL,
    fee_rate            DECIMAL(10,6) NOT NULL DEFAULT 0,
    status              TINYINT NOT NULL DEFAULT 1,
    created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_channel_code (channel_code),
    KEY idx_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pay_route_rule (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_no         VARCHAR(32) NOT NULL,
    app_id              VARCHAR(64) NOT NULL,
    pay_scene           VARCHAR(32) NOT NULL COMMENT 'APP/H5/JSAPI/QR',
    pay_way             VARCHAR(32) NOT NULL COMMENT 'ALIPAY/WX/UNION',
    channel_code        VARCHAR(32) NOT NULL,
    min_amount          BIGINT NOT NULL DEFAULT 0 COMMENT '分',
    max_amount          BIGINT NOT NULL DEFAULT 999999999,
    weight              INT NOT NULL DEFAULT 100,
    priority            INT NOT NULL DEFAULT 100,
    status              TINYINT NOT NULL DEFAULT 1,
    created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    KEY idx_rule_query (merchant_no, app_id, pay_scene, pay_way, status, priority)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pay_order_ext (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no            VARCHAR(64) NOT NULL,
    merchant_no         VARCHAR(32) NOT NULL,
    ext_json            JSON DEFAULT NULL,
    attach              VARCHAR(512) DEFAULT NULL,
    device_info         VARCHAR(128) DEFAULT NULL,
    client_ip           VARCHAR(64) DEFAULT NULL,
    created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_merchant_no (merchant_no)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pay_order_status_log (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no            VARCHAR(64) NOT NULL,
    merchant_no         VARCHAR(32) NOT NULL,
    from_status         VARCHAR(32) NOT NULL,
    to_status           VARCHAR(32) NOT NULL,
    event_type          VARCHAR(32) NOT NULL,
    reason              VARCHAR(256) DEFAULT NULL,
    operator            VARCHAR(64) DEFAULT NULL,
    event_time          DATETIME(3) NOT NULL,
    created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_order_no_time (order_no, event_time),
    KEY idx_merchant_time (merchant_no, event_time)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pay_refund_order (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    refund_no           VARCHAR(64) NOT NULL,
    order_no            VARCHAR(64) NOT NULL,
    merchant_no         VARCHAR(32) NOT NULL,
    app_id              VARCHAR(64) NOT NULL,
    channel_code        VARCHAR(32) NOT NULL,
    channel_refund_no   VARCHAR(64) DEFAULT NULL,
    refund_amount       BIGINT NOT NULL COMMENT '分',
    refund_status       VARCHAR(32) NOT NULL,
    reason              VARCHAR(128) DEFAULT NULL,
    success_time        DATETIME(3) DEFAULT NULL,
    fail_reason         VARCHAR(256) DEFAULT NULL,
    created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_refund_no (refund_no),
    KEY idx_order_no (order_no),
    KEY idx_merchant_status_time (merchant_no, refund_status, created_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pay_notify_task (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    biz_type            VARCHAR(16) NOT NULL COMMENT 'PAY/REFUND',
    biz_no              VARCHAR(64) NOT NULL COMMENT 'order_no/refund_no',
    merchant_no         VARCHAR(32) NOT NULL,
    notify_url          VARCHAR(512) NOT NULL,
    notify_body         JSON NOT NULL,
    notify_status       VARCHAR(16) NOT NULL DEFAULT 'WAIT',
    retry_count         INT NOT NULL DEFAULT 0,
    next_retry_time     DATETIME(3) NOT NULL,
    last_notify_time    DATETIME(3) DEFAULT NULL,
    last_result         VARCHAR(1024) DEFAULT NULL,
    created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    KEY idx_retry_scan (notify_status, next_retry_time),
    KEY idx_biz_no (biz_type, biz_no)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS mq_outbox (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    msg_id              VARCHAR(64) NOT NULL,
    topic               VARCHAR(128) NOT NULL,
    biz_key             VARCHAR(64) NOT NULL,
    msg_body            JSON NOT NULL,
    send_status         VARCHAR(16) NOT NULL DEFAULT 'INIT',
    retry_count         INT NOT NULL DEFAULT 0,
    next_retry_time     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_msg_id (msg_id),
    KEY idx_send_scan (send_status, next_retry_time),
    KEY idx_biz_key (biz_key)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS idempotent_record (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    idem_key            VARCHAR(128) NOT NULL,
    biz_type            VARCHAR(32) NOT NULL,
    request_digest      VARCHAR(128) DEFAULT NULL,
    response_snapshot   JSON DEFAULT NULL,
    expired_at          DATETIME(3) DEFAULT NULL,
    created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_idem_key (idem_key),
    KEY idx_biz_type_expired (biz_type, expired_at)
) ENGINE=InnoDB;
