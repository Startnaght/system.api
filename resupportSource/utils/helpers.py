# utils/helpers.py
import re
import random
import time
from datetime import datetime
from typing import List, Dict, Any
from fake_useragent import UserAgent
from config.settings import get_config

class HelperFunctions:
    """辅助函数类"""
    
    def __init__(self):
        self.config = get_config()
        self.ua = UserAgent()
    
    @staticmethod
    def extract_time(time_text: str) -> str:
        """提取时间信息"""
        if not time_text:
            return datetime.now().strftime('%Y-%m-%d')
        
        time_patterns = [
            r'\d{4}-\d{2}-\d{2}',
            r'\d{4}\.\d{2}\.\d{2}',
            r'\d{2}-\d{2} \d{2}:\d{2}',
            r'\d{4}年\d{1,2}月\d{1,2}日'
        ]
        
        for pattern in time_patterns:
            match = re.search(pattern, time_text)
            if match:
                return match.group()
        
        return datetime.now().strftime('%Y-%m-%d')
    
    def get_random_headers(self) -> Dict[str, str]:
        """获取随机请求头"""
        return {
            'User-Agent': self.ua.random,
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
            'Accept-Language': 'zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3',
            'Accept-Encoding': 'gzip, deflate, br',
            'Connection': 'keep-alive',
            'Upgrade-Insecure-Requests': '1',
        }
    
    def random_delay(self):
        """随机延迟"""
        delay = random.uniform(*self.config.CRAWLER_CONFIG['delay_range'])
        time.sleep(delay)
    
    @staticmethod
    def clean_text(text: str) -> str:
        """清理文本"""
        if not text:
            return ""
        # 去除多余空格和换行
        text = re.sub(r'\s+', ' ', text)
        return text.strip()
    
    @staticmethod
    def validate_url(url: str) -> bool:
        """验证URL格式"""
        pattern = re.compile(
            r'^(?:http|ftp)s?://'  # http:// or https://
            r'(?:(?:[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?\.)+(?:[A-Z]{2,6}\.?|[A-Z0-9-]{2,}\.?)|'  # domain...
            r'localhost|'  # localhost...
            r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})'  # ...or ip
            r'(?::\d+)?'  # optional port
            r'(?:/?|[/?]\S+)$', re.IGNORECASE)
        return re.match(pattern, url) is not None

# 创建全局实例
helpers = HelperFunctions()