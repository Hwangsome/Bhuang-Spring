CREATE TABLE job (
                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     job_name VARCHAR(255) NOT NULL,
                     status ENUM('CANCELLED', 'PROCESSING', 'COMPLETED') NOT NULL,
                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO job (job_name, status, created_at, updated_at)
VALUES
    ('Job 1', 'PROCESSING', NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE, NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE),
    ('Job 2', 'COMPLETED', NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE, NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE),
    ('Job 3', 'CANCELLED', NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE, NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE),
    ('Job 4', 'PROCESSING', NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE, NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE),
    ('Job 5', 'COMPLETED', NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE, NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE),
    ('Job 6', 'PROCESSING', NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE, NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE),
    ('Job 7', 'CANCELLED', NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE, NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE),
    ('Job 8', 'PROCESSING', NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE, NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE),
    ('Job 9', 'COMPLETED', NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE, NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE),
    ('Job 10', 'PROCESSING', NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE, NOW() - INTERVAL FLOOR(RAND() * 10) MINUTE);


CREATE TABLE user_log_entity (
                                 id INT PRIMARY KEY AUTO_INCREMENT,
                                 name VARCHAR(255),
                                 age INT,
                                 email VARCHAR(255),
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table user
(
    id    int auto_increment
        primary key,
    name  varchar(255) not null,
    age   int          null,
    email varchar(255) not null
);

