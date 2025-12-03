-- 创建数据库
CREATE DATABASE IF NOT EXISTS news_crawler CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE news_crawler;

-- 平台表
CREATE TABLE IF NOT EXISTS platforms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    platform_code VARCHAR(50) UNIQUE NOT NULL,
    platform_name VARCHAR(100) NOT NULL,
    base_url VARCHAR(500),
    is_enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 新闻分类表
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 新闻主表
CREATE TABLE IF NOT EXISTS news (
    id INT AUTO_INCREMENT PRIMARY KEY,
    platform_id INT NOT NULL,
    category_id INT,
    title VARCHAR(500) NOT NULL,
    content TEXT,
    url VARCHAR(1000) NOT NULL,
    image_url VARCHAR(1000),
    publish_time VARCHAR(100),
    crawl_time DATETIME NOT NULL,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    tags VARCHAR(500),
    author VARCHAR(200),
    source VARCHAR(200),
    unique_hash VARCHAR(64) UNIQUE, -- 用于去重
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (platform_id) REFERENCES platforms(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_platform (platform_id),
    INDEX idx_category (category_id),
    INDEX idx_publish_time (publish_time),
    INDEX idx_crawl_time (crawl_time),
    INDEX idx_unique_hash (unique_hash)
);

-- 新闻关键词表（用于搜索和热点分析）
CREATE TABLE IF NOT EXISTS news_keywords (
    id INT AUTO_INCREMENT PRIMARY KEY,
    news_id INT NOT NULL,
    keyword VARCHAR(100) NOT NULL,
    frequency INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE,
    INDEX idx_keyword (keyword),
    INDEX idx_news_keyword (news_id, keyword)
);

-- 爬取日志表
CREATE TABLE IF NOT EXISTS crawl_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    platform_id INT NOT NULL,
    status ENUM('success', 'error', 'partial') NOT NULL,
    news_count INT DEFAULT 0,
    error_message TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (platform_id) REFERENCES platforms(id) ON DELETE CASCADE,
    INDEX idx_platform_time (platform_id, start_time)
);

-- 插入默认数据
INSERT IGNORE INTO platforms (platform_code, platform_name, base_url, is_enabled) VALUES
('pengpai', '澎湃新闻', 'https://www.thepaper.cn/', TRUE),
('cctv', '央视网', 'http://news.cctv.com/', TRUE),
('huanqiu', '环球网', 'https://world.huanqiu.com/', TRUE);

INSERT IGNORE INTO categories (category_name, description) VALUES
('政治', '政治与国际关系类新闻'),
('经济', '经济与金融类新闻'),
('军事', '军事与国防类新闻'),
('科技', '科技与创新类新闻'),
('社会', '社会与民生类新闻'),
('其他', '其他类别新闻');

-- 创建新闻全文搜索索引（MariaDB 10.0.5+ 支持）
ALTER TABLE news ADD FULLTEXT INDEX ft_title_content (title, content);