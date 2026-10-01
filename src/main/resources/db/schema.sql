-- WrongWeb 初始化建表脚本
-- 使用 MySQL 8.x，执行前请先创建数据库：
-- CREATE DATABASE wrong_web DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sys_user (
    user_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，同时作为数据隔离字段 userId',
    account VARCHAR(100) NOT NULL COMMENT '登录账号，注册时存入邮箱',
    email VARCHAR(100) NOT NULL COMMENT '邮箱，注册必填，唯一',
    phone VARCHAR(20) NOT NULL COMMENT '手机号码，注册必填，11 位',
    password VARCHAR(255) NOT NULL COMMENT '登录密码，存储 BCrypt 哈希',
    birth_date DATE NOT NULL COMMENT '出生日期',
    gender TINYINT NOT NULL COMMENT '性别：1-男，2-女',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_sys_user_account (account),
    UNIQUE KEY uk_sys_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS subject (
    subject_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '科目ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID，数据隔离字段',
    subject_name VARCHAR(50) NOT NULL COMMENT '科目名称，例如高数、线代、概率论',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (subject_id),
    KEY idx_subject_user_id (user_id),
    UNIQUE KEY uk_subject_user_name (user_id, subject_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科目表';

CREATE TABLE IF NOT EXISTS question_type (
    question_type_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '题型ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID，数据隔离字段',
    type_name VARCHAR(50) NOT NULL COMMENT '题型名称，例如选择、填空、解答',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (question_type_id),
    KEY idx_question_type_user_id (user_id),
    UNIQUE KEY uk_question_type_user_name (user_id, type_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题型表';

CREATE TABLE IF NOT EXISTS mistake (
    mistake_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '错题ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID，数据隔离字段',
    subject_id BIGINT NOT NULL COMMENT '所属科目ID',
    question_type_id BIGINT NOT NULL COMMENT '所属题型ID',
    question_content TEXT NOT NULL COMMENT '题干内容',
    wrong_answer TEXT NOT NULL COMMENT '错误答案',
    correct_answer TEXT NOT NULL COMMENT '正确答案',
    entry_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
    knowledge_point VARCHAR(255) DEFAULT NULL COMMENT '知识点',
    analysis TEXT DEFAULT NULL COMMENT '解题步骤与过程，支持 LaTeX',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (mistake_id),
    KEY idx_mistake_user_id (user_id),
    KEY idx_mistake_user_subject (user_id, subject_id),
    KEY idx_mistake_user_subject_type (user_id, subject_id, question_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题表';

CREATE TABLE IF NOT EXISTS paper (
    paper_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID，数据隔离字段',
    subject_id BIGINT NOT NULL COMMENT '抽题科目ID，试卷按单一科目生成',
    total_count INT NOT NULL COMMENT '总题数',
    choice_count INT NOT NULL COMMENT '选择题数量',
    blank_count INT NOT NULL COMMENT '填空题数量',
    solve_count INT NOT NULL COMMENT '解答题数量',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '试卷状态：0-未提交，1-已提交',
    question_snapshot LONGTEXT NOT NULL COMMENT '题目快照 JSON，永久保存抽题时的题干与答案',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    submitted_at DATETIME DEFAULT NULL COMMENT '提交时间，未提交时为 NULL',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (paper_id),
    KEY idx_paper_user_id (user_id),
    KEY idx_paper_user_subject (user_id, subject_id),
    KEY idx_paper_user_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='历史试卷表，永久保存组卷结果';
