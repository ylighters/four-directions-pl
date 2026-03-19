CREATE DEFINER=`root`@`localhost` PROCEDURE `migrate_pay_order_shards_to_non_padded`()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE src_table VARCHAR(64);
    DECLARE dst_table VARCHAR(64);

    WHILE i < 64 DO
        SET src_table = CONCAT('pay_order_', LPAD(i, 2, '0'));
        SET dst_table = CONCAT('pay_order_', i);

        IF EXISTS (
            SELECT 1
            FROM information_schema.tables
            WHERE table_schema = DATABASE()
              AND table_name = src_table
        ) THEN
            SET @sql_text = CONCAT('CREATE TABLE IF NOT EXISTS ', dst_table, ' LIKE ', src_table);
ELSE
            SET @sql_text = CONCAT('CREATE TABLE IF NOT EXISTS ', dst_table, ' LIKE pay_order_template');
END IF;

PREPARE stmt FROM @sql_text;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET i = i + 1;
END WHILE;
END