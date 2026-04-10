CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_name VARCHAR(50),
    email VARCHAR(100),
    password VARCHAR(100),
    reset_token VARCHAR(100),
    reset_token_expire TIMESTAMP,
    chat_history TEXT
    );
