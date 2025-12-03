# analysis/news_analyzer.py
import re
from datetime import datetime
from typing import List, Dict, Tuple
from collections import Counter
from utils.logger import setup_logger
from config.settings import get_config

class NewsAnalyzer:
    """新闻分析类"""
    
    def __init__(self):
        self.config = get_config()
        self.logger = setup_logger('NewsAnalyzer')
    
    def classify_news(self, title: str, content: str = "") -> str:
        """新闻分类"""
        combined_text = (title + " " + content).lower()
        
        for category, keywords in self.config.CATEGORY_KEYWORDS.items():
            if any(keyword in combined_text for keyword in keywords):
                return category
        
        return '其他'
    
    def extract_keywords(self, text: str, top_n: int = 10) -> List[Tuple[str, int]]:
        """提取关键词"""
        # 中文分词（简单实现，实际应用中可以使用jieba等分词库）
        words = re.findall(r'[\u4e00-\u9fa5]{2,6}', text)
        
        # 过滤停用词
        stopwords = {'的', '了', '在', '是', '我', '有', '和', '就', '不', '人', '都', '一', '一个', '上', '也', '很', '到', '说', '要', '去', '你', '会', '着', '没有', '看', '好', '自己', '这', '那', '他', '她', '它'}
        filtered_words = [word for word in words if word not in stopwords and len(word) >= 2]
        
        # 统计词频
        word_counts = Counter(filtered_words)
        return word_counts.most_common(top_n)
    
    def analyze_sentiment(self, text: str) -> Dict[str, float]:
        """情感分析（简单实现）"""
        # 正面词汇
        positive_words = {'好', '优秀', '成功', '胜利', '进步', '发展', '增长', '改善', '利好', '积极', '乐观'}
        # 负面词汇
        negative_words = {'坏', '失败', '问题', '困难', '下降', '衰退', '恶化', '利空', '消极', '悲观', '危机'}
        
        words = set(re.findall(r'[\u4e00-\u9fa5]{2,4}', text))
        
        positive_count = len(words & positive_words)
        negative_count = len(words & negative_words)
        total_relevant = positive_count + negative_count
        
        if total_relevant == 0:
            return {'score': 0, 'sentiment': '中性'}
        
        score = (positive_count - negative_count) / total_relevant
        sentiment = '正面' if score > 0.1 else '负面' if score < -0.1 else '中性'
        
        return {
            'score': round(score, 3),
            'sentiment': sentiment,
            'positive_count': positive_count,
            'negative_count': negative_count
        }
    
    def analyze_hot_topics(self, news_list: List[Dict], top_n: int = 5) -> List[Dict]:
        """分析热点话题"""
        all_titles = " ".join([news['title'] for news in news_list])
        keywords = self.extract_keywords(all_titles, top_n * 2)
        
        hot_topics = []
        for keyword, count in keywords[:top_n]:
            # 找到包含该关键词的新闻
            related_news = [news for news in news_list if keyword in news['title']]
            
            topic = {
                'keyword': keyword,
                'count': count,
                'related_news_count': len(related_news),
                'platforms': list(set([news['platform'] for news in related_news])),
                'latest_news': related_news[0]['title'] if related_news else ""
            }
            hot_topics.append(topic)
        
        return hot_topics
    
    def generate_report(self, news_list: List[Dict]) -> Dict:
        """生成分析报告"""
        if not news_list:
            return {
                'timestamp': datetime.now().isoformat(),
                'total_count': 0,
                'summary': '暂无数据'
            }
        
        # 分类统计
        categorized = {}
        platform_stats = {}
        source_stats = {}
        sentiment_stats = {'正面': 0, '负面': 0, '中性': 0}
        
        for news in news_list:
            # 确保分类
            if 'category' not in news:
                news['category'] = self.classify_news(news['title'])
            
            # 分类统计
            category = news['category']
            if category not in categorized:
                categorized[category] = []
            categorized[category].append(news)
            
            # 平台统计
            platform = news['platform']
            platform_stats[platform] = platform_stats.get(platform, 0) + 1
            
            # 来源统计
            source = news.get('source', '未知')
            source_stats[source] = source_stats.get(source, 0) + 1
            
            # 情感分析统计
            sentiment_result = self.analyze_sentiment(news['title'])
            sentiment_stats[sentiment_result['sentiment']] += 1
        
        # 热点分析
        hot_topics = self.analyze_hot_topics(news_list)
        
        # 时间分布（简单按日期）
        date_stats = {}
        for news in news_list:
            date = news.get('publish_time', '')[:10]  # 取YYYY-MM-DD
            if date:
                date_stats[date] = date_stats.get(date, 0) + 1
        
        report = {
            'timestamp': datetime.now().isoformat(),
            'total_count': len(news_list),
            'categorized_news': categorized,
            'platform_stats': platform_stats,
            'source_stats': source_stats,
            'sentiment_stats': sentiment_stats,
            'date_stats': date_stats,
            'hot_topics': hot_topics,
            'summary': self._generate_summary_text(categorized, platform_stats, hot_topics)
        }
        
        self.logger.info(f"生成分析报告，总计 {len(news_list)} 条新闻")
        return report
    
    def _generate_summary_text(self, categorized: Dict, platform_stats: Dict, 
                             hot_topics: List[Dict]) -> str:
        """生成文本简报"""
        summary = "📊 新闻分析简报\n"
        summary += "=" * 70 + "\n\n"
        
        # 基本信息
        total_count = sum(len(news_list) for news_list in categorized.values())
        summary += f"📈 基本信息:\n"
        summary += f"   总计新闻: {total_count} 条\n"
        summary += f"   平台分布: {', '.join([f'{k}({v})' for k, v in platform_stats.items()])}\n"
        summary += f"   分类数量: {len(categorized)} 类\n\n"
        
        # 热点话题
        if hot_topics:
            summary += "🔥 热点话题:\n"
            for i, topic in enumerate(hot_topics[:5], 1):
                summary += f"   {i}. {topic['keyword']} (出现{topic['count']}次)\n"
            summary += "\n"
        
        # 分类详情
        summary += "🏷️ 分类详情:\n"
        for category, news_list in sorted(categorized.items(), 
                                        key=lambda x: len(x[1]), reverse=True):
            summary += f"   {category}: {len(news_list)} 条\n"
        
        summary += "\n" + "=" * 70 + "\n"
        summary += f"报告生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n"
        
        return summary
    
    def export_analysis_data(self, report: Dict, format_type: str = 'json') -> str:
        """导出分析数据"""
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        filename = f"news_analysis_{timestamp}.{format_type}"
        
        try:
            if format_type == 'json':
                import json
                with open(filename, 'w', encoding='utf-8') as f:
                    json.dump(report, f, ensure_ascii=False, indent=2)
            
            elif format_type == 'txt':
                with open(filename, 'w', encoding='utf-8') as f:
                    f.write(report['summary'])
            
            else:
                self.logger.warning(f"不支持的格式: {format_type}")
                return ""
            
            self.logger.info(f"分析数据已导出: {filename}")
            return filename
            
        except Exception as e:
            self.logger.error(f"导出分析数据失败: {e}")
            return ""