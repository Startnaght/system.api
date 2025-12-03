# main.py
#!/usr/bin/env python3
import argparse
import sys
import os

# 添加项目根目录到Python路径
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from core.crawler_manager import CrawlerManager
from core.news_api import NewsAPI
from config.settings import get_config

def main():
    parser = argparse.ArgumentParser(description='新闻爬虫系统')
    parser.add_argument('--crawl', action='store_true', help='运行爬虫')
    parser.add_argument('--report', action='store_true', help='生成报告')
    parser.add_argument('--export', type=str, nargs='*', help='导出格式 [json, text, csv, html]')
    parser.add_argument('--api', action='store_true', help='启动API服务')
    parser.add_argument('--host', type=str, default='localhost', help='API服务主机')
    parser.add_argument('--port', type=int, default=8000, help='API服务端口')
    parser.add_argument('--schedule', type=str, help='设置定时任务，例如 "09:00"')
    parser.add_argument('--status', action='store_true', help='显示系统状态')
    
    args = parser.parse_args()
    
    # 获取配置
    config = get_config()
    
    # 创建爬虫管理器
    manager = CrawlerManager(config)
    
    if args.status:
        status = manager.get_status()
        print("系统状态:")
        print(f"  爬虫数量: {status['crawlers']['total']}")
        print(f"  爬虫列表: {', '.join(status['crawlers']['list'])}")
        print(f"  存储连接: {status['storage']['connected']}")
        return
    
    if args.crawl:
        print("开始爬取新闻...")
        news_list = manager.run_crawlers()
        print(f"爬取完成，共获取 {len(news_list)} 条新闻")
        
        if args.report or args.export:
            report = manager.generate_report(news_list)
            print("生成分析报告完成")
            
            if args.export:
                export_formats = args.export if args.export else ['json', 'text']
                results = manager.export_report(report, export_formats)
                print(f"导出报告: {list(results.keys())}")
    
    elif args.report:
        print("生成报告...")
        report = manager.generate_report(use_stored_data=True)
        print(f"报告生成完成，总计 {report['total_count']} 条新闻")
        
        if args.export:
            export_formats = args.export if args.export else ['json', 'text']
            results = manager.export_report(report, export_formats)
            print(f"导出报告: {list(results.keys())}")
        else:
            # 打印文本摘要
            print("\n" + "="*50)
            print(report.get('summary', '暂无摘要'))
    
    elif args.api:
        print(f"启动API服务... http://{args.host}:{args.port}")
        api = NewsAPI(manager)
        api.app.run(host=args.host, port=args.port, debug=config.API_CONFIG['debug'])
    
    elif args.schedule:
        print(f"设置定时任务: 每天 {args.schedule} 执行爬取")
        manager.run_scheduler(schedule_time=args.schedule, run_immediately=True)
    
    else:
        # 默认行为：运行爬虫并生成报告
        print("运行爬虫并生成报告...")
        news_list = manager.run_crawlers()
        report = manager.generate_report(news_list)
        
        # 导出报告
        results = manager.export_report(report, ['json', 'text'])
        print(f"爬取完成: {len(news_list)} 条新闻")
        print(f"导出报告: {list(results.keys())}")
        
        # 打印摘要
        print("\n" + "="*50)
        print(report.get('summary', '暂无摘要'))

if __name__ == "__main__":
    main()