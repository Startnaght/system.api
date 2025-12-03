# storage/mariadb_storage.py
import pymysql
import hashlib
from datetime import datetime, timedelta
from typing import List, Dict, Optional, Tuple
from utils.logger import setup_logger
from config.settings import get_config

class MariaDBStorage:
    """MariaDB 数据存储类"""
    
    def __init__(self, db_config: Dict = None):
        self.config = get_config()
        self.db_config = db_config or self.config.DB_CONFIG
        self.logger = setup_logger('MariaDBStorage')
        self.connection = None
        self._connect()
    
    def _connect(self):
        """连接数据库"""
        try:
            self.connection = pymysql.connect(
                host=self.db_config['host'],
                port=self.db_config['port'],
                user=self.db_config['user'],
                password=self.db_config['password'],
                database=self.db_config['database'],
                charset=self.db_config['charset'],
                connect_timeout=self.db_config['connect_timeout'],
                autocommit=True
            )
            self.logger.info(f"MariaDB连接成功: {self.db_config['host']}:{self.db_config['port']}")
        except pymysql.Error as e:
            self.logger.error(f"MariaDB连接失败: {e}")
            self.connection = None
    
    def is_connected(self) -> bool:
        """检查连接状态"""
        if self.connection is None:
            return False
        
        try:
            with self.connection.cursor() as cursor:
                cursor.execute("SELECT 1")
            return True
        except pymysql.Error:
            self.connection = None
            return False
    
    def _reconnect(self):
        """重新连接"""
        if self.connection:
            self.connection.close()
        self._connect()
    
    def _generate_unique_hash(self, title: str, platform: str, publish_time: str) -> str:
        """生成唯一哈希用于去重"""
        content = f"{title}_{platform}_{publish_time}"
        return hashlib.sha256(content.encode('utf-8')).hexdigest()
    
    def get_platform_id(self, platform_code: str) -> Optional[int]:
        """获取平台ID"""
        if not self.is_connected():
            return None
        
        try:
            with self.connection.cursor() as cursor:
                cursor.execute(
                    "SELECT id FROM platforms WHERE platform_code = %s", 
                    (platform_code,)
                )
                result = cursor.fetchone()
                return result[0] if result else None
        except pymysql.Error as e:
            self.logger.error(f"获取平台ID失败: {e}")
            return None
    
    def get_category_id(self, category_name: str) -> Optional[int]:
        """获取分类ID"""
        if not self.is_connected():
            return None
        
        try:
            with self.connection.cursor() as cursor:
                cursor.execute(
                    "SELECT id FROM categories WHERE category_name = %s", 
                    (category_name,)
                )
                result = cursor.fetchone()
                return result[0] if result else None
        except pymysql.Error as e:
            self.logger.error(f"获取分类ID失败: {e}")
            return None
    
    def store_news(self, news_list: List[Dict]) -> int:
        """存储新闻数据"""
        if not self.is_connected() or not news_list:
            return 0
        
        inserted_count = 0
        
        try:
            with self.connection.cursor() as cursor:
                for news in news_list:
                    # 获取平台ID和分类ID
                    platform_id = self.get_platform_id(news.get('platform_code', ''))
                    category_id = self.get_category_id(news.get('category', '其他'))
                    
                    if not platform_id:
                        self.logger.warning(f"未知平台: {news.get('platform_code')}")
                        continue
                    
                    # 生成唯一哈希
                    unique_hash = self._generate_unique_hash(
                        news['title'], 
                        news.get('platform_code', ''), 
                        news.get('publish_time', '')
                    )
                    
                    # 插入或更新新闻
                    sql = """
                    INSERT INTO news (
                        platform_id, category_id, title, content, url, image_url, 
                        publish_time, crawl_time, view_count, like_count, comment_count,
                        tags, author, source, unique_hash
                    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                    ON DUPLICATE KEY UPDATE 
                        title = VALUES(title),
                        content = VALUES(content),
                        image_url = VALUES(image_url),
                        view_count = VALUES(view_count),
                        like_count = VALUES(like_count),
                        comment_count = VALUES(comment_count),
                        updated_at = CURRENT_TIMESTAMP
                    """
                    
                    cursor.execute(sql, (
                        platform_id,
                        category_id,
                        news['title'],
                        news.get('content', ''),
                        news['url'],
                        news.get('image_url', ''),
                        news.get('publish_time', ''),
                        datetime.now(),
                        news.get('view_count', 0),
                        news.get('like_count', 0),
                        news.get('comment_count', 0),
                        news.get('tags', ''),
                        news.get('author', ''),
                        news.get('source', ''),
                        unique_hash
                    ))
                    
                    if cursor.rowcount > 0:
                        inserted_count += 1
                        
                        # 如果插入成功，处理关键词
                        if cursor.lastrowid:
                            self._store_keywords(cursor.lastrowid, news['title'])
            
            self.logger.info(f"存储完成，新增 {inserted_count} 条记录，总计 {len(news_list)} 条")
            return inserted_count
            
        except pymysql.Error as e:
            self.logger.error(f"存储数据失败: {e}")
            return 0
    
    def _store_keywords(self, news_id: int, title: str):
        """存储新闻关键词"""
        if not self.is_connected():
            return
        
        try:
            # 简单的关键词提取（实际应用中可以使用jieba等分词库）
            import re
            words = re.findall(r'[\u4e00-\u9fa5]{2,6}', title)
            
            with self.connection.cursor() as cursor:
                for word in words:
                    if len(word) >= 2:  # 至少两个字
                        # 检查关键词是否已存在
                        cursor.execute(
                            "SELECT id, frequency FROM news_keywords WHERE news_id = %s AND keyword = %s",
                            (news_id, word)
                        )
                        result = cursor.fetchone()
                        
                        if result:
                            # 更新频率
                            cursor.execute(
                                "UPDATE news_keywords SET frequency = frequency + 1 WHERE id = %s",
                                (result[0],)
                            )
                        else:
                            # 插入新关键词
                            cursor.execute(
                                "INSERT INTO news_keywords (news_id, keyword) VALUES (%s, %s)",
                                (news_id, word)
                            )
        
        except pymysql.Error as e:
            self.logger.error(f"存储关键词失败: {e}")
    
    def get_recent_news(self, days: int = 1, category: str = None, 
                       platform: str = None, limit: int = 100) -> List[Dict]:
        """获取近期新闻"""
        if not self.is_connected():
            return []
        
        try:
            with self.connection.cursor(pymysql.cursors.DictCursor) as cursor:
                # 构建查询条件
                where_conditions = ["n.crawl_time >= %s"]
                params = [datetime.now() - timedelta(days=days)]
                
                if category:
                    where_conditions.append("c.category_name = %s")
                    params.append(category)
                
                if platform:
                    where_conditions.append("p.platform_code = %s")
                    params.append(platform)
                
                where_clause = " AND ".join(where_conditions)
                
                sql = f"""
                SELECT 
                    n.id, p.platform_name as platform, c.category_name as category,
                    n.title, n.content, n.url, n.image_url, n.publish_time,
                    n.crawl_time, n.view_count, n.like_count, n.comment_count,
                    n.tags, n.author, n.source, n.created_at
                FROM news n
                JOIN platforms p ON n.platform_id = p.id
                LEFT JOIN categories c ON n.category_id = c.id
                WHERE {where_clause}
                ORDER BY n.publish_time DESC, n.crawl_time DESC
                LIMIT %s
                """
                
                params.append(limit)
                cursor.execute(sql, params)
                
                results = cursor.fetchall()
                self.logger.info(f"查询到 {len(results)} 条新闻")
                return results
                
        except pymysql.Error as e:
            self.logger.error(f"查询数据失败: {e}")
            return []
    
    def search_news(self, keyword: str, fields: List[str] = None, 
                   limit: int = 50) -> List[Dict]:
        """搜索新闻"""
        if not self.is_connected():
            return []
        
        try:
            with self.connection.cursor(pymysql.cursors.DictCursor) as cursor:
                if fields is None:
                    fields = ['title', 'content']
                
                # 构建搜索条件
                search_conditions = []
                params = []
                
                for field in fields:
                    if field in ['title', 'content']:
                        search_conditions.append(f"n.{field} LIKE %s")
                        params.append(f"%{keyword}%")
                
                if not search_conditions:
                    return []
                
                where_clause = " OR ".join(search_conditions)
                
                sql = f"""
                SELECT 
                    n.id, p.platform_name as platform, c.category_name as category,
                    n.title, n.content, n.url, n.image_url, n.publish_time,
                    n.crawl_time, n.view_count, n.like_count, n.comment_count,
                    n.tags, n.author, n.source, n.created_at
                FROM news n
                JOIN platforms p ON n.platform_id = p.id
                LEFT JOIN categories c ON n.category_id = c.id
                WHERE {where_clause}
                ORDER BY n.publish_time DESC
                LIMIT %s
                """
                
                params.append(limit)
                cursor.execute(sql, params)
                
                results = cursor.fetchall()
                self.logger.info(f"搜索 '{keyword}' 得到 {len(results)} 条结果")
                return results
                
        except pymysql.Error as e:
            self.logger.error(f"搜索失败: {e}")
            return []
    
    def get_news_stats(self, days: int = 7) -> Dict:
        """获取新闻统计信息"""
        if not self.is_connected():
            return {}
        
        try:
            with self.connection.cursor() as cursor:
                # 平台统计
                cursor.execute("""
                SELECT p.platform_name, COUNT(*) as count
                FROM news n
                JOIN platforms p ON n.platform_id = p.id
                WHERE n.crawl_time >= %s
                GROUP BY p.platform_name
                ORDER BY count DESC
                """, (datetime.now() - timedelta(days=days),))
                
                platform_stats = {row[0]: row[1] for row in cursor.fetchall()}
                
                # 分类统计
                cursor.execute("""
                SELECT c.category_name, COUNT(*) as count
                FROM news n
                LEFT JOIN categories c ON n.category_id = c.id
                WHERE n.crawl_time >= %s
                GROUP BY c.category_name
                ORDER BY count DESC
                """, (datetime.now() - timedelta(days=days),))
                
                category_stats = {row[0]: row[1] for row in cursor.fetchall()}
                
                # 每日统计
                cursor.execute("""
                SELECT DATE(n.crawl_time) as date, COUNT(*) as count
                FROM news n
                WHERE n.crawl_time >= %s
                GROUP BY DATE(n.crawl_time)
                ORDER BY date DESC
                """, (datetime.now() - timedelta(days=days),))
                
                daily_stats = {row[0].strftime('%Y-%m-%d'): row[1] for row in cursor.fetchall()}
                
                # 热点关键词
                cursor.execute("""
                SELECT keyword, SUM(frequency) as total_frequency
                FROM news_keywords nk
                JOIN news n ON nk.news_id = n.id
                WHERE n.crawl_time >= %s
                GROUP BY keyword
                ORDER BY total_frequency DESC
                LIMIT 10
                """, (datetime.now() - timedelta(days=days),))
                
                hot_keywords = [{'keyword': row[0], 'count': row[1]} for row in cursor.fetchall()]
                
                stats = {
                    'platform_stats': platform_stats,
                    'category_stats': category_stats,
                    'daily_stats': daily_stats,
                    'hot_keywords': hot_keywords,
                    'total_count': sum(platform_stats.values())
                }
                
                return stats
                
        except pymysql.Error as e:
            self.logger.error(f"获取统计信息失败: {e}")
            return {}
    
    def log_crawl_result(self, platform_code: str, status: str, 
                        news_count: int = 0, error_message: str = None):
        """记录爬取日志"""
        if not self.is_connected():
            return
        
        try:
            platform_id = self.get_platform_id(platform_code)
            if not platform_id:
                return
            
            with self.connection.cursor() as cursor:
                cursor.execute("""
                INSERT INTO crawl_logs (platform_id, status, news_count, error_message, start_time, end_time)
                VALUES (%s, %s, %s, %s, %s, %s)
                """, (
                    platform_id,
                    status,
                    news_count,
                    error_message,
                    datetime.now() - timedelta(minutes=5),  # 假设爬取用了5分钟
                    datetime.now()
                ))
        
        except pymysql.Error as e:
            self.logger.error(f"记录爬取日志失败: {e}")
    
    def cleanup_old_data(self, days: int = 30) -> int:
        """清理旧数据"""
        if not self.is_connected():
            return 0
        
        try:
            with self.connection.cursor() as cursor:
                # 先删除关联的关键词
                cursor.execute("""
                DELETE nk FROM news_keywords nk
                JOIN news n ON nk.news_id = n.id
                WHERE n.crawl_time < %s
                """, (datetime.now() - timedelta(days=days),))
                
                keywords_deleted = cursor.rowcount
                
                # 再删除新闻
                cursor.execute("""
                DELETE FROM news WHERE crawl_time < %s
                """, (datetime.now() - timedelta(days=days),))
                
                news_deleted = cursor.rowcount
                
                self.logger.info(f"清理了 {news_deleted} 条新闻和 {keywords_deleted} 条关键词记录")
                return news_deleted
                
        except pymysql.Error as e:
            self.logger.error(f"清理数据失败: {e}")
            return 0
    
    def close(self):
        """关闭连接"""
        if self.connection:
            self.connection.close()
            self.logger.info("MariaDB连接已关闭")