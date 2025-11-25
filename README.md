## 📋 项目概述
 Star Image API​ 是一个基于 Spring Boot 的文件上传和一语（Hitokoto）API 服务。该项目提供了文件管理、内容分发和一言服务等功能。

##🚀 功能特性

###核心功能
  ✅ 文件上传服务​ - 支持多种文件格式上传
  ✅ 一言API服务​ - 集成 Hitokoto 内容API
  ✅ 数据库存储​ - 完整的操作日志和统计
  ✅ RESTful API​ - 标准的接口设计

### 技术特性
  Spring Boot 2.7.0 + WebFlux 响应式编程
  MariaDB/MySQL 数据库支持
  文件分片上传支持
  完整的异常处理机制
  详细的访问日志记录

## 🏗️ 系统架构

Client → Spring Boot API → 业务逻辑层 → 数据存储层
                    ↓
            外部API（Hitokoto）

## 📁 项目结构

src/main/java/com/star/imgapi/
├── config/          # 配置类
├── controller/      # 控制器层
├── service/         # 服务接口
├── service/impl/    # 服务实现
├── entity/          # 实体类
├── util/            # 工具类
└── exception/       # 异常处理

## 🔌 API 接口文档

健康检查接口

```path
GET /api/health
GET http://localhost:8000/api/health
响应示例：
{
    "status": "UP",
    "service": "Star Image API",
    "timestamp": 1732464000000,
    "version": "1.0.0"
}

```

一言相关接口
获取一言（POST）
POST /api/yiyan
POST http://localhost:8000/api/yiyan
Content-Type: application/json

{
    "name": "分类名称",
    "index": 索引值
}
参数说明：
参数
类型
必填
默认值
说明
name
String
❌
"b"
分类名称(a-k)
index
Integer
❌
0
索引值
分类对应关系：
a: Anime 动画
b: Comic 漫画
c: Game 游戏
d: Novel 小说
e: Original 原创
f: Internet 网络
g: Other 其他
h: Movie 影视
i: Poetry 诗词
j: Netease 网易云
k: Philosophy 哲学
响应示例：
{
    "success": true,
    "hitokoto": "生活就像海洋，只有意志坚强的人才能到达彼岸。",
    "from": "出处信息",
    "type": "b",
    "responseTime": 150
}
快速获取一言（GET）
GET /api/yiyan/quick
GET http://localhost:8000/api/yiyan/quick?category=b
查询参数：
category: 分类名称（可选，默认"b"）
文件上传接口
单文件上传
POST /api/upload
POST http://localhost:8000/api/upload
Content-Type: multipart/form-data

参数：
- file: 文件（必填）
- fileName: 文件名（必填）
- chunkIndex: 分片索引（必填）
支持的文件类型：
文本文件：.txt
图片文件：.jpg, .jpeg, .png, .gif, .bmp
文档文件：.pdf, .doc, .docx
压缩文件：.zip
响应示例：
{
    "success": true,
    "message": "文件上传成功",
    "storedName": "uuid文件名.扩展名",
    "originalName": "原始文件名",
    "fileSize": 1024,
    "fileType": "文件类型"
}
批量文件上传
POST /api/upload/batch
POST http://localhost:8000/api/upload/batch
Content-Type: multipart/form-data

参数：
- files: 多个文件数组（必填）
统计信息接口
文件统计
GET /api/stats/files
GET http://localhost:8000/api/stats/files
一言统计
GET /api/stats/hitokoto
GET http://localhost:8000/api/stats/hitokoto

## ⚙️ 配置说明

数据库配置
# application.properties
spring.datasource.url=jdbc:mariadb://localhost:3306/star_bigdata
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# 连接池配置
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
文件上传配置
# 文件上传大小限制
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# 上传文件存储路径
file.upload.dir=/path/to/upload/directory
一言API配置
# Hitokoto API 配置
hitokoto.api.url=https://v1.hitokoto.cn
hitokoto.api.timeout=5000

## 🚀 快速开始

环境要求
Java 11+
Maven 3.6+
MariaDB/MySQL 5.7+
部署步骤
克隆项目
git clone <项目地址>
cd star-image-api
数据库初始化
CREATE DATABASE star_bigdata;
# 执行 init_database.sql 初始化表结构
配置修改
# 复制并修改配置文件
cp src/main/resources/application.properties.example src/main/resources/application.properties
编译运行
# 编译项目
mvn clean package

# 运行项目
java -jar target/star-image-api-1.0.0.jar

# 或使用Maven运行
mvn spring-boot:run
验证部署
# 健康检查
curl http://localhost:8000/api/health

# 测试一言API
curl -X POST http://localhost:8000/api/yiyan \
  -H "Content-Type: application/json" \
  -d '{"name":"b"}'

##🔧 开发指南

项目结构说明
com.star.imgapi/
├── EmailApiApplication.java     # 主启动类
├── config/                      # 配置类
│   ├── WebClientConfig.java     # WebClient配置
│   └── AppConfig.java          # 应用配置
├── controller/                  # 控制器
│   └── CompleteController.java # 主要API控制器
├── service/                     # 服务接口
│   └── HitokotoService.java    # 一言服务接口
├── service/impl/               # 服务实现
│   └── HitokotoServiceImpl.java
├── entity/                      # 实体类
│   ├── hitokotoCode.java       # 一言实体
│   └── Uploadteam.java         # 上传参数实体
├── util/                        # 工具类
│   ├── DatabaseUtil.java       # 数据库工具
│   ├── ResponseWrapper.java    # 响应包装器
│   └── GlobalLog.java          # 日志工具
└── exception/                  # 异常处理
    └── GlobalExceptionHandler.java
添加新API接口
在Controller中添加端点
@PostMapping("/api/new-endpoint")
public ResponseWrapper<Map<String, Object>> newEndpoint(@RequestBody MyRequest request) {
    // 业务逻辑
    return ResponseWrapper.success(result);
}
在Service层实现业务逻辑
@Service
public class NewServiceImpl implements NewService {
    // 实现业务方法
}
添加实体类（如需要）
@Data
public class MyRequest {
    private String param1;
    private Integer param2;
}
日志配置
项目使用统一的日志工具类：
// 记录信息日志
GlobalLog.info("操作描述");

// 记录错误日志
GlobalLog.error("错误描述");

// 记录警告日志
GlobalLog.warn("警告信息");

##🐛 故障排除

常见问题
数据库连接失败
检查数据库服务是否启动
验证连接字符串和凭据
检查防火墙设置
文件上传失败
检查上传目录权限
验证文件大小限制配置
检查磁盘空间
一言API调用失败
检查网络连接
验证外部API可用性
查看超时设置
日志调试
启用调试模式：
# application.properties
logging.level.com.star.imgapi=DEBUG
logging.level.org.springframework.web=DEBUG

## 📊 监控与维护

健康检查端点
GET /api/health
统计信息端点
GET /api/stats/files     # 文件统计
GET /api/stats/hitokoto  # 一言统计
性能监控建议
定期检查数据库连接池状态
监控文件存储空间使用情况
关注API响应时间指标

## 🔒 安全建议

生产环境配置
使用HTTPS加密传输
配置适当的CORS策略
实施API访问限流
文件安全
验证上传文件类型
扫描恶意文件内容
设置文件大小限制
数据库安全
使用强密码
定期备份数据
限制数据库访问IP

## 🤝 贡献指南

欢迎提交Issue和Pull Request来改进项目。
开发流程
Fork 项目
创建功能分支
提交更改
推送到分支
创建Pull Request

## 📄 许可证

本项目采用 MIT 许可证。详见 LICENSE文件。

## 📞 支持与联系

如有问题或建议，请通过以下方式联系：
提交GitHub Issue
发送邮件至项目维护者
最后更新: 2025年11月
