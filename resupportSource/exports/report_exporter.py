# exports/report_exporter.py
import json
import csv
import os
from datetime import datetime
from typing import Dict, List
from utils.logger import setup_logger
from config.settings import get_config

class ReportExporter:
    """报告导出类"""
    
    def __init__(self):
        self.config = get_config()
        self.logger = setup_logger('ReportExporter')
        self.ensure_output_dir()
    
    def ensure_output_dir(self):
        """确保输出目录存在"""
        output_dir = self.config.EXPORT_CONFIG['output_dir']
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)
            self.logger.info(f"创建输出目录: {output_dir}")
    
    def export_report(self, report: Dict, format_type: str) -> str:
        """导出报告"""
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        filename = f"news_report_{timestamp}.{format_type}"
        filepath = os.path.join(self.config.EXPORT_CONFIG['output_dir'], filename)
        
        try:
            if format_type == 'json':
                self.export_json(report, filepath)
            elif format_type == 'text':
                self.export_text(report, filepath)
            elif format_type == 'csv':
                self.export_csv(report, filepath)
            elif format_type == 'html':
                self.export_html(report, filepath)
            else:
                self.logger.error(f"不支持的导出格式: {format_type}")
                return ""
            
            self.logger.info(f"报告已导出: {filepath}")
            return filepath
        except Exception as e:
            self.logger.error(f"导出报告失败: {e}")
            return ""
    
    def export_json(self, report: Dict, filepath: str):
        """导出JSON格式"""
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(report, f, ensure_ascii=False, indent=2)
    
    def export_text(self, report: Dict, filepath: str):
        """导出文本格式"""
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(report.get('summary', '暂无摘要'))
    
    def export_csv(self, report: Dict, filepath: str):
        """导出CSV格式"""
        # 提取新闻数据
        news_list = []
        for category, items in report.get('categorized_news', {}).items():
            for news in items:
                news_list.append({
                    '平台': news.get('platform', ''),
                    '标题': news.get('title', ''),
                    '分类': category,
                    '发布时间': news.get('publish_time', ''),
                    'URL': news.get('url', '')
                })
        
        if news_list:
            with open(filepath, 'w', newline='', encoding='utf-8') as f:
                writer = csv.DictWriter(f, fieldnames=news_list[0].keys())
                writer.writeheader()
                writer.writerows(news_list)
    
    def export_html(self, report: Dict, filepath: str):
        """导出HTML格式"""
        html_content = f"""
        <!DOCTYPE html>
        <html lang="zh-CN">
        <head>
            <meta charset="UTF-8">
            <title>新闻简报 {datetime.now().strftime('%Y-%m-%d')}</title>
            <style>
                body {{ font-family: Arial, sans-serif; margin: 20px; }}
                h1 {{ color: #333; }}
                .section {{ margin-bottom: 30px; }}
                .news-item {{ margin: 10px 0; padding: 10px; border-left: 3px solid #007cba; }}
                .stats {{ background: #f5f5f5; padding: 15px; }}
                .category {{ font-weight: bold; color: #007cba; }}
            </style>
        </head>
        <body>
            <h1>新闻简报</h1>
            <div class="section">
                <p>生成时间: {report.get('timestamp', '')}</p>
                <p>总计新闻: {report.get('total_count', 0)} 条</p>
            </div>
            
            <div class="section stats">
                <h2>统计信息</h2>
                <p>平台分布: {', '.join([f'{k}({v})' for k, v in report.get('platform_stats', {}).items()])}</p>
                <p>分类数量: {len(report.get('categorized_news', {}))} 类</p>
            </div>
            
            <div class="section">
                <h2>新闻详情</h2>
                {self._generate_html_news_section(report)}
            </div>
        </body>
        </html>
        """
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(html_content)
    
    def _generate_html_news_section(self, report: Dict) -> str:
        """生成HTML新闻部分"""
        html = ""
        for category, news_list in report.get('categorized_news', {}).items():
            html += f'<div class="category">{category} ({len(news_list)}条)</div>'
            for news in news_list:
                html += f"""
                <div class="news-item">
                    <strong>{news.get('platform', '')}</strong>: 
                    <a href="{news.get('url', '')}" target="_blank">{news.get('title', '')}</a>
                    <span style="color: #666; font-size: 0.9em;">({news.get('publish_time', '')})</span>
                </div>
                """
        return html
    
    def batch_export(self, report: Dict, formats: List[str] = None) -> Dict[str, str]:
        """批量导出多种格式"""
        if formats is None:
            formats = self.config.EXPORT_CONFIG['formats']
        
        results = {}
        for format_type in formats:
            filepath = self.export_report(report, format_type)
            if filepath:
                results[format_type] = filepath
        
        return results