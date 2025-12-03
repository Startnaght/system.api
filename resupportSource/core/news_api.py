# core/news_api.py
from flask import Flask, jsonify, request
from typing import List, Dict, Optional
from utils.logger import setup_logger
from config.settings import get_config
from core.crawler_manager import CrawlerManager

class NewsAPI:
    """新闻API接口类"""
    
    def __init__(self, crawler_manager: CrawlerManager):
        self.manager = crawler_manager
        self.config = get_config()
        self.logger = setup_logger('NewsAPI')
        self.app = Flask(__name__)
        self._setup_routes()
    
    def _setup_routes(self):
        """设置API路由"""
        
        @self.app.route('/')
        def index():
            return jsonify({
                'message': 'News Crawler API',
                'version': '1.0.0',
                'endpoints': {
                    '/news': '获取新闻数据',
                    '/news/search': '搜索新闻',
                    '/analysis': '获取分析报告',
                    '/status': '系统状态',
                    '/crawl': '手动触发爬取'
                }
            })
        
        @self.app.route('/news', methods=['GET'])
        def get_news():
            """获取新闻数据"""
            try:
                days = int(request.args.get('days', 1))
                category = request.args.get('category')
                platform = request.args.get('platform')
                limit = int(request.args.get('limit', 50))
                
                news_list = self.manager.storage.get_recent_news(
                    days=days, 
                    category=category, 
                    platform=platform, 
                    limit=limit
                ) if self.manager.storage else []
                
                return jsonify({
                    'success': True,
                    'count': len(news_list),
                    'data': news_list
                })
            except Exception as e:
                self.logger.error(f"获取新闻失败: {e}")
                return jsonify({'success': False, 'error': str(e)}), 500
        
        @self.app.route('/news/search', methods=['GET'])
        def search_news():
            """搜索新闻"""
            try:
                keyword = request.args.get('q')
                if not keyword:
                    return jsonify({'success': False, 'error': '缺少搜索关键词'}), 400
                
                limit = int(request.args.get('limit', 20))
                fields = request.args.getlist('fields') or ['title']
                
                results = self.manager.storage.search_news(
                    keyword=keyword,
                )
                return jsonify({
                    'success': True,
                    'count': len(results),
                    'data': results
                })
            except Exception as e:
                self.logger.error(f"搜索新闻失败: {e}")
                return jsonify({'success': False, 'error': str(e)}), 500