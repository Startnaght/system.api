# core/crawler_manager.py
import time
import schedule
from typing import List, Dict, Optional
from utils.logger import setup_logger
from config.settings import get_config
from crawlers import CrawlerFactory
from storage import StorageFactory
from analysis import NewsAnalyzer
from exports.report_exporter import ReportExporter

class CrawlerManager:
    """爬虫管理器"""
    
    def __init__(self, config: Dict = None):
        self.config = config or get_config()
        self.logger = setup_logger('CrawlerManager')
        
        # 初始化组件
        self.crawlers = []
        self.storage = StorageFactory.create_storage('mongo')
        self.analyzer = NewsAnalyzer()
        self.exporter = ReportExporter()
        
        self._init_crawlers()
    
    def _init_crawlers(self):
        """初始化爬虫实例"""
        from crawlers import CrawlerFactory
        
        for platform_id, platform_config in self.config.PLATFORMS.items():
            if platform_config['enabled']:
                try:
                    crawler = CrawlerFactory.create_crawler(platform_id)
                    self.crawlers.append(crawler)
                    self.logger.info(f"初始化爬虫: {platform_config['name']}")
                except Exception as e:
                    self.logger.error(f"初始化爬虫失败 {platform_id}: {e}")
    
    def add_crawler(self, crawler_type: str, enabled: bool = True):
        """动态添加爬虫"""
        if enabled:
            try:
                crawler = CrawlerFactory.create_crawler(crawler_type)
                self.crawlers.append(crawler)
                self.logger.info(f"动态添加爬虫: {crawler_type}")
            except Exception as e:
                self.logger.error(f"添加爬虫失败 {crawler_type}: {e}")
    
    def remove_crawler(self, crawler_type: str):
        """移除爬虫"""
        self.crawlers = [c for c in self.crawlers if c.get_platform_name() != crawler_type]
        self.logger.info(f"移除爬虫: {crawler_type}")
    
    def run_crawlers(self, use_delay: bool = True) -> List[Dict]:
        """运行所有爬虫"""
        all_news = []
        
        if not self.crawlers:
            self.logger.warning("没有可用的爬虫")
            return all_news
        
        self.logger.info(f"开始运行 {len(self.crawlers)} 个爬虫")
        
        for i, crawler in enumerate(self.crawlers):
            try:
                self.logger.info(f"运行爬虫 ({i+1}/{len(self.crawlers)}): {crawler.get_platform_name()}")
                
                news = crawler.crawl_news()
                all_news.extend(news)
                
                self.logger.info(f"{crawler.get_platform_name()} 爬取完成: {len(news)} 条")
                
                # 爬虫间延迟
                if use_delay and i < len(self.crawlers) - 1:
                    delay = self.config.CRAWLER_CONFIG['delay_range'][1]
                    self.logger.info(f"等待 {delay} 秒后继续下一个爬虫")
                    time.sleep(delay)
                    
            except Exception as e:
                self.logger.error(f"爬虫运行失败 {crawler.get_platform_name()}: {e}")
                continue
        
        # 存储数据
        if all_news and self.storage:
            stored_count = self.storage.store_news(all_news)
            self.logger.info(f"数据存储完成: {stored_count} 条新记录")
        
        self.logger.info(f"所有爬虫运行完成，总计 {len(all_news)} 条新闻")
        return all_news
    
    def generate_report(self, news_list: List[Dict] = None, 
                       use_stored_data: bool = False) -> Dict:
        """生成分析报告"""
        if news_list is None and use_stored_data:
            # 从数据库获取最近一天的数据
            news_list = self.storage.get_recent_news(days=1) if self.storage else []
        
        if not news_list:
            self.logger.warning("没有新闻数据生成报告")
            return self.analyzer.generate_report([])
        
        return self.analyzer.generate_report(news_list)
    
    def export_report(self, report: Dict, formats: List[str] = None) -> Dict[str, str]:
        """导出报告"""
        if formats is None:
            formats = self.config.EXPORT_CONFIG['formats']
        
        export_results = {}
        for format_type in formats:
            if format_type in self.config.EXPORT_CONFIG['formats']:
                filename = self.exporter.export_report(report, format_type)
                if filename:
                    export_results[format_type] = filename
        
        self.logger.info(f"报告导出完成: {list(export_results.keys())}")
        return export_results
    
    def setup_scheduler(self, schedule_time: str = "09:00"):
        """设置定时任务"""
        def scheduled_task():
            self.logger.info("执行定时爬取任务")
            news_list = self.run_crawlers()
            report = self.generate_report(news_list)
            self.export_report(report, ['json', 'text'])
        
        # 每天指定时间执行
        schedule.every().day.at(schedule_time).do(scheduled_task)
        self.logger.info(f"定时任务已设置: 每天 {schedule_time} 执行")
        
        return schedule
    
    def run_scheduler(self, schedule_time: str = "09:00", run_immediately: bool = False):
        """运行定时任务"""
        scheduler = self.setup_scheduler(schedule_time)
        
        if run_immediately:
            self.logger.info("立即执行一次任务")
            list(scheduler.get_jobs())[0].run()
        
        self.logger.info("开始运行定时任务调度器")
        try:
            while True:
                scheduler.run_pending()
                time.sleep(60)  # 每分钟检查一次
        except KeyboardInterrupt:
            self.logger.info("定时任务已停止")
    
    def get_status(self) -> Dict:
        """获取系统状态"""
        status = {
            'timestamp': time.strftime('%Y-%m-%d %H:%M:%S'),
            'crawlers': {
                'total': len(self.crawlers),
                'list': [crawler.get_platform_name() for crawler in self.crawlers]
            },
            'storage': {
                'connected': self.storage.is_connected() if self.storage else False,
                'type': 'MongoDB' if self.storage else 'None'
            },
            'config': {
                'use_proxy': self.config.CRAWLER_CONFIG['use_proxy'],
                'max_news_per_crawler': self.config.CRAWLER_CONFIG['max_news_per_crawler']
            }
        }
        
        # 添加存储统计
        if self.storage and self.storage.is_connected():
            stats = self.storage.get_news_stats(days=7)
            status['storage']['stats'] = stats
        
        return status
    
    def cleanup(self, days: int = 30):
        """清理资源"""
        if self.storage:
            cleaned_count = self.storage.cleanup_old_data(days)
            self.logger.info(f"资源清理完成: 清理了 {cleaned_count} 条数据")
        
        self.logger.info("爬虫管理器清理完成")