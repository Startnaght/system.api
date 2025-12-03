# storage/mongo_storage.py
from pymongo import MongoClient
from pymongo.errors import ConnectionFailure, PyMongoError
from datetime import datetime, timedelta
from typing import List, Dict, Optional
from utils.logger import setup_logger
from config.settings import get_config

class MongoStorage:
    """MongoDB数据存储类"""
    
    def __init__(self, db_url: str = None, db_name: str = None):
        self.config = get_config()
        self.db_url = db_url or self.config.DB_CONFIG['url']
        self.db_name = db_name or self.config.DB_CONFIG['name']
        self.logger = setup_logger('MongoStorage')
        self.client = None
        self.db = None
        self._connect()
    
    def _connect(self):
        """连接数据库"""
        try:
            self.client = MongoClient(
                self.db_url, 
                serverSelectionTimeoutMS=self.config.DB_CONFIG['timeout']
            )
            # 测试连接
            self.client.admin.command('ismaster')
            self.db = self.client[self.db_name]
            self.logger.info(f"MongoDB连接成功: {self.db_url}")
        except ConnectionFailure as e:
            self.logger.error(f"MongoDB连接失败: {e}")
            self.client = None
            self.db = None
    
    def is_connected(self) -> bool:
        """检查连接状态"""
        if self.client is None:
            return False
        try:
            self.client.admin.command('ismaster')
            return True
        except ConnectionFailure:
            self.client = None
            self.db = None
            return False
    
    def store_news(self, news_list: List[Dict]) -> int:
        """存储新闻数据"""
        if not self.is_connected():
            self.logger.warning("数据库未连接，跳过存储")
            return 0
        
        try:
            collection = self.db['daily_news']
            inserted_count = 0
            
            for news in news_list:
                # 创建唯一标识（标题+平台+发布时间）
                unique_id = f"{news['title']}_{news['platform']}_{news['publish_time']}"
                
                # 添加唯一标识和存储时间
                news['_unique_id'] = unique_id
                news['storage_time'] = datetime.now().isoformat()
                
                # 去重插入
                result = collection.update_one(
                    {'_unique_id': unique_id},
                    {'$set': news},
                    upsert=True
                )
                if result.upserted_id:
                    inserted_count += 1
            
            self.logger.info(f"存储完成，新增 {inserted_count} 条记录，总计 {len(news_list)} 条")
            return inserted_count
            
        except PyMongoError as e:
            self.logger.error(f"存储数据失败: {e}")
            return 0
    
    def get_recent_news(self, days: int = 1, category: str = None, 
                        platform: str = None, limit: int = 100) -> List[Dict]:
        """获取近期新闻"""
        if not self.is_connected():
            self.logger.warning("数据库未连接，返回空列表")
            return []
        
        try:
            collection = self.db['daily_news']
            
            # 构建查询条件
            query = {
                'storage_time': {
                    '$gte': (datetime.now() - timedelta(days=days)).isoformat()
                }
            }
            
            if category:
                query['category'] = category
            
            if platform:
                query['platform'] = platform
            
            # 查询并排序
            cursor = collection.find(query).sort('publish_time', -1).limit(limit)
            
            # 移除MongoDB的_id字段
            results = []
            for doc in cursor:
                doc.pop('_id', None)
                results.append(doc)
            
            self.logger.info(f"查询到 {len(results)} 条新闻")
            return results
            
        except PyMongoError as e:
            self.logger.error(f"查询数据失败: {e}")
            return []
    
    def search_news(self, keyword: str, fields: List[str] = None, 
                   limit: int = 50) -> List[Dict]:
        """搜索新闻"""
        if not self.is_connected():
            return []
        
        try:
            collection = self.db['daily_news']
            
            if fields is None:
                fields = ['title', 'content']
            
            # 构建搜索条件
            search_conditions = []
            for field in fields:
                search_conditions.append({field: {'$regex': keyword, '$options': 'i'}})
            
            query = {'$or': search_conditions} if search_conditions else {}
            
            cursor = collection.find(query).limit(limit)
            
            results = []
            for doc in cursor:
                doc.pop('_id', None)
                results.append(doc)
            
            self.logger.info(f"搜索 '{keyword}' 得到 {len(results)} 条结果")
            return results
            
        except PyMongoError as e:
            self.logger.error(f"搜索失败: {e}")
            return []
    
    def get_news_stats(self, days: int = 7) -> Dict:
        """获取新闻统计信息"""
        if not self.is_connected():
            return {}
        
        try:
            collection = self.db['daily_news']
            
            # 时间范围
            start_time = datetime.now() - timedelta(days=days)
            
            # 平台统计
            platform_stats = list(collection.aggregate([
                {'$match': {'storage_time': {'$gte': start_time.isoformat()}}},
                {'$group': {'_id': '$platform', 'count': {'$sum': 1}}},
                {'$sort': {'count': -1}}
            ]))
            
            # 分类统计
            category_stats = list(collection.aggregate([
                {'$match': {'storage_time': {'$gte': start_time.isoformat()}}},
                {'$group': {'_id': '$category', 'count': {'$sum': 1}}},
                {'$sort': {'count': -1}}
            ]))
            
            # 每日统计
            daily_stats = list(collection.aggregate([
                {'$match': {'storage_time': {'$gte': start_time.isoformat()}}},
                {'$group': {
                    '_id': {'$substr': ['$storage_time', 0, 10]},
                    'count': {'$sum': 1}
                }},
                {'$sort': {'_id': 1}}
            ]))
            
            stats = {
                'platform_stats': {stat['_id']: stat['count'] for stat in platform_stats},
                'category_stats': {stat['_id']: stat['count'] for stat in category_stats},
                'daily_stats': {stat['_id']: stat['count'] for stat in daily_stats},
                'total_count': sum(stat['count'] for stat in platform_stats)
            }
            
            return stats
            
        except PyMongoError as e:
            self.logger.error(f"获取统计信息失败: {e}")
            return {}
    
    def cleanup_old_data(self, days: int = 30) -> int:
        """清理旧数据"""
        if not self.is_connected():
            return 0
        
        try:
            collection = self.db['daily_news']
            
            cutoff_date = datetime.now() - timedelta(days=days)
            result = collection.delete_many({
                'storage_time': {'$lt': cutoff_date.isoformat()}
            })
            
            self.logger.info(f"清理了 {result.deleted_count} 条 {days} 天前的数据")
            return result.deleted_count
            
        except PyMongoError as e:
            self.logger.error(f"清理数据失败: {e}")
            return 0
    
    def close(self):
        """关闭连接"""
        if self.client:
            self.client.close()
            self.logger.info("MongoDB连接已关闭")