# config/settings.py
import os
from datetime import timedelta

class Config:
    """配置文件"""
    
    # 数据库配置 - MariaDB
    DB_CONFIG = {
        'host': os.getenv('MARIADB_HOST', 'localhost'),
        'port': int(os.getenv('MARIADB_PORT', 3306)),
        'user': os.getenv('MARIADB_USER', 'root'),
        'password': os.getenv('MARIADB_PASSWORD', ''),
        'database': os.getenv('MARIADB_DATABASE', 'news_crawler'),
        'charset': 'utf8mb4',
        'connect_timeout': 10
    }
    
    # 爬虫配置
    CRAWLER_CONFIG = {
        'delay_range': (1, 3),
        'max_retries': 3,
        'timeout': 30,
        'use_proxy': False,
        'max_news_per_crawler': 20
    }
    
    # 平台配置
    PLATFORMS = {
        'pengpai': {
            'enabled': True,
            'url': 'https://www.thepaper.cn/',
            'name': '澎湃新闻',
            'list_selector': '.newsbox, .news_item, [class*="news"]',
            'title_selector': 'a, h2, h3',
            'time_selector': '.time, .date, [class*="time"]'
        },
        'cctv': {
            'enabled': True,
            'url': 'http://news.cctv.com/world/',
            'name': '央视网',
            'list_selector': '.news_list li, li[class*="news"]',
            'title_selector': 'a',
            'time_selector': '.time, .date'
        },
        'huanqiu': {
            'enabled': True,
            'url': 'https://world.huanqiu.com/',
            'name': '环球网',
            'list_selector': '.news-item, [class*="news"]',
            'title_selector': 'a, h3, h4',
            'time_selector': '.time, .date'
        }
    }
    
    # 分类关键词
    CATEGORY_KEYWORDS = {
        '政治': ['外交', '领土', '总统', '总理', '联合国', '大使', '国际关系', '会谈', '协议', '条约'],
        '经济': ['经济', '金融', '贸易', '市场', '支出', '投资', '股市', '货币', 'GDP', '财政'],
        '军事': ['国防', '军事', '安全', '部队', '基地', '武器', '演习', '导弹', '军队', '战争'],
        '科技': ['科技', '创新', '数字', '网络', 'AI', '人工智能', '卫星', '航天', '互联网', '芯片'],
        '社会': ['民生', '医疗', '教育', '社会', '犯罪', '疫情', '灾害', '事故', '环境', '民生'],
        '其他': []
    }
    
    # 导出配置
    EXPORT_CONFIG = {
        'formats': ['json', 'text', 'html', 'csv'],
        'output_dir': 'reports'
    }
    
    # API配置
    API_CONFIG = {
        'host': 'localhost',
        'port': 8000,
        'debug': True
    }

class DevelopmentConfig(Config):
    """开发环境配置"""
    CRAWLER_CONFIG = {
        **Config.CRAWLER_CONFIG,
        'delay_range': (0.5, 1.5),
        'max_news_per_crawler': 10
    }

class ProductionConfig(Config):
    """生产环境配置"""
    CRAWLER_CONFIG = {
        **Config.CRAWLER_CONFIG,
        'use_proxy': True,
        'delay_range': (2, 5)
    }

def get_config():
    env = os.getenv('ENVIRONMENT', 'development')
    if env == 'production':
        return ProductionConfig()
    else:
        return DevelopmentConfig()