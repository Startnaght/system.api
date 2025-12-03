# utils/logger.py
import logging
import sys
from config.settings import get_config

def setup_logger(name: str) -> logging.Logger:
    """设置日志记录器"""
    config = get_config()
    
    logger = logging.getLogger(name)
    
    if not logger.handlers:
        logger.setLevel(getattr(logging, config.LOG_CONFIG['level']))
        
        # 控制台处理器
        console_handler = logging.StreamHandler(sys.stdout)
        console_handler.setLevel(logging.DEBUG)
        
        # 文件处理器
        file_handler = logging.FileHandler(config.LOG_CONFIG['file'])
        file_handler.setLevel(logging.INFO)
        
        # 格式化器
        formatter = logging.Formatter(config.LOG_CONFIG['format'])
        console_handler.setFormatter(formatter)
        file_handler.setFormatter(formatter)
        
        # 添加处理器
        logger.addHandler(console_handler)
        logger.addHandler(file_handler)
    
    return logger