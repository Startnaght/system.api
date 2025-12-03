# crawlers/base_crawler.py (部分更新)
class BaseCrawler(ABC):
    """爬虫基类 - 更新以支持MariaDB"""
    
    def __init__(self, platform_code: str):
        self.config = get_config()
        self.logger = setup_logger(self.__class__.__name__)
        self.platform_code = platform_code
        self.platform_config = self.config.PLATFORMS.get(platform_code, {})
        self.session = requests.Session()
    
    def crawl_and_store(self, storage) -> int:
        """爬取并存储数据（整合方法）"""
        self.logger.info(f"开始爬取{self.get_platform_name()}新闻")
        
        try:
            news_list = self.crawl_news()
            
            if news_list:
                # 添加平台代码到每条新闻
                for news in news_list:
                    news['platform_code'] = self.platform_code
                
                stored_count = storage.store_news(news_list)
                
                # 记录成功日志
                storage.log_crawl_result(
                    self.platform_code, 
                    'success' if stored_count > 0 else 'partial',
                    stored_count
                )
                
                self.logger.info(f"{self.get_platform_name()}爬取完成: {stored_count}/{len(news_list)} 条存储成功")
                return stored_count
            else:
                # 记录无数据日志
                storage.log_crawl_result(self.platform_code, 'error', 0, "未获取到数据")
                self.logger.warning(f"{self.get_platform_name()}未获取到数据")
                return 0
                
        except Exception as e:
            # 记录错误日志
            if hasattr(storage, 'log_crawl_result'):
                storage.log_crawl_result(self.platform_code, 'error', 0, str(e))
            
            self.logger.error(f"{self.get_platform_name()}爬取失败: {e}")
            return 0