# crawlers/huanqiu_crawler.py
import re
from bs4 import BeautifulSoup
from datetime import datetime
from typing import List, Dict
from .base_crawler import BaseCrawler
from config.settings import get_config
from utils.helpers import helpers

class HuanQiuCrawler(BaseCrawler):
    """环球网爬虫"""
    
    def __init__(self):
        super().__init__()
        self.config = get_config()
        self.platform_config = self.config.PLATFORMS['huanqiu']
    
    def get_platform_name(self) -> str:
        return self.platform_config['name']
    
    def crawl_news(self) -> List[Dict]:
        if not self.platform_config['enabled']:
            self.logger.info("环球网爬虫已禁用")
            return []
        
        self.logger.info("开始爬取环球网国际新闻")
        url = self.platform_config['url']
        
        response = self.make_request(url)
        if not response:
            return []
        
        try:
            soup = BeautifulSoup(response.text, 'html.parser')
            news_list = []
            
            # 环球网选择器
            articles = soup.select('.news-item, [class*="news"]')[:self.config.CRAWLER_CONFIG['max_news_per_crawler']]
            
            for article in articles:
                try:
                    title_elem = article.select_one('a') or article.find('h3') or article.find('h4')
                    if not title_elem or not title_elem.get_text().strip():
                        continue
                    
                    title = helpers.clean_text(title_elem.get_text())
                    link = title_elem.get('href', '')
                    
                    if link and not link.startswith('http'):
                        link = 'https://world.huanqiu.com' + link
                    
                    if not helpers.validate_url(link):
                        continue
                    
                    # 提取时间
                    time_elem = article.select_one('.time, .date')
                    publish_time = helpers.extract_time(time_elem.get_text() if time_elem else "")
                    
                    news = {
                        'platform': self.get_platform_name(),
                        'title': title,
                        'url': link,
                        'publish_time': publish_time,
                        'category': self.classify_news(title),
                        'crawl_time': datetime.now().isoformat(),
                        'source': '环球网'
                    }
                    news_list.append(news)
                    
                except Exception as e:
                    self.logger.error(f"解析环球新闻条目失败: {e}")
                    continue
            
            self.logger.info(f"环球网爬取完成，共 {len(news_list)} 条")
            return news_list
            
        except Exception as e:
            self.logger.error(f"环球网爬取异常: {e}")
            return []