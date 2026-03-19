USE payment_center;

CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    username        VARCHAR(64) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    display_name    VARCHAR(128) NOT NULL,
    status          TINYINT NOT NULL DEFAULT 1,
    created_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sys_role (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code       VARCHAR(64) NOT NULL,
    role_name       VARCHAR(128) NOT NULL,
    status          TINYINT NOT NULL DEFAULT 1,
    created_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sys_menu (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id       BIGINT NOT NULL DEFAULT 0,
    menu_name       VARCHAR(128) NOT NULL,
    menu_path       VARCHAR(255) NOT NULL,
    icon            VARCHAR(64) DEFAULT NULL,
    sort_no         INT NOT NULL DEFAULT 100,
    status          TINYINT NOT NULL DEFAULT 1,
    created_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_menu_path (menu_path),
    KEY idx_parent_sort (parent_id, sort_no)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sys_user_role_rel (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    role_id         BIGINT NOT NULL,
    created_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sys_role_menu_rel (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id         BIGINT NOT NULL,
    menu_id         BIGINT NOT NULL,
    created_at      DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    KEY idx_menu_id (menu_id)
) ENGINE=InnoDB;

INSERT INTO sys_role (role_code, role_name, status)
SELECT 'ADMIN', '超级管理员', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'ADMIN');

INSERT INTO sys_role (role_code, role_name, status)
SELECT 'OPERATOR', '运营人员', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'OPERATOR');

INSERT INTO sys_role (role_code, role_name, status)
SELECT 'AUDITOR', '审计人员', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'AUDITOR');

INSERT INTO sys_menu (parent_id, menu_name, menu_path, icon, sort_no, status)
SELECT 0, '业务看板', '/home/dashboard', 'chart', 10, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_path = '/home/dashboard');

INSERT INTO sys_menu (parent_id, menu_name, menu_path, icon, sort_no, status)
SELECT 0, '支付订单', '/home/orders', 'order', 20, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_path = '/home/orders');

INSERT INTO sys_menu (parent_id, menu_name, menu_path, icon, sort_no, status)
SELECT 0, '用户管理', '/home/system/users', 'users', 100, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_path = '/home/system/users');

INSERT INTO sys_menu (parent_id, menu_name, menu_path, icon, sort_no, status)
SELECT 0, '角色管理', '/home/system/roles', 'roles', 110, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_path = '/home/system/roles');

INSERT INTO sys_menu (parent_id, menu_name, menu_path, icon, sort_no, status)
SELECT 0, '菜单管理', '/home/system/menus', 'menus', 120, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_path = '/home/system/menus');

INSERT INTO sys_menu (parent_id, menu_name, menu_path, icon, sort_no, status)
SELECT 0, '支付渠道', '/home/system/channels', 'channel', 130, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_path = '/home/system/channels');

INSERT INTO sys_user (username, password, display_name, status)
SELECT 'admin', 'admin123', '超级管理员', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

INSERT INTO sys_user_role_rel (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u
JOIN sys_role r ON r.role_code = 'ADMIN'
WHERE u.username = 'admin'
  AND NOT EXISTS (
    SELECT 1
    FROM sys_user_role_rel ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );
