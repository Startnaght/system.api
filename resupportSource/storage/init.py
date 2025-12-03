# storage/__init__.py
from .mariadb_storage import MariaDBStorage

class StorageFactory:
    """存储工厂"""
    
    @staticmethod
    def create_storage(storage_type: str = 'mariadb', **kwargs):
        """创建存储实例"""
        if storage_type == 'mariadb':
            return MariaDBStorage(**kwargs)
        elif storage_type == 'mongo':
            # 保留MongoDB支持
            from .mongo_storage import MongoStorage
            return MongoStorage(**kwargs)
        else:
            raise ValueError(f"不支持的存储类型: {storage_type}")
    
    @staticmethod
    def get_available_storages():
        """获取可用存储列表"""
        return ['mariadb', 'mongo']  # 保留MongoDB支持

__all__ = ['MariaDBStorage', 'StorageFactory']