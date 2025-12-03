# crawlers/__init__.py
from .pengpai_crawler import PengPaiCrawler
from .cctv_crawler import CCTVCrawler
from .huanqiu_crawler import HuanQiuCrawler

# 爬虫工厂类
class CrawlerFactory:
    """爬虫工厂"""
    
    @staticmethod
    def create_crawler(crawler_type: str):
        """创建爬虫实例"""
        crawlers = {
            'pengpai': PengPaiCrawler,
            'cctv': CCTVCrawler,
            'huanqiu': HuanQiuCrawler
        }
        
        if crawler_type in crawlers:
            return crawlers[crawler_type]()
        else:
            raise ValueError(f"不支持的爬虫类型: {crawler_type}")
    
    @staticmethod
    def get_available_crawlers():
        """获取可用爬虫列表"""
        return ['pengpai', 'cctv', 'huanqiu']

__all__ = ['PengPaiCrawler', 'CCTVCrawler', 'HuanQiuCrawler', 'CrawlerFactory']