# from queue import Queue
# from pymongo import MongoClient
# from gracgImgMain import gracg
# import datetime
# import pymysql #mysql数据库

# ''' persistenceSystem 该类主要用于持久化保存数据
# # 直接创建对象不带默认参数认为本机local host端口号27017
# #  client = MongoClient()
# # # 也可以显示的指名对应Mongo服务器器上的ip和端口'''
# class saveData:
#     @classmethod
#     def chient(self, db, collection):
#         return MongoClient('localhost', 27017)[db][collection]

#     @classmethod
#     def mongo_update(self,parameter:object)->None:
#         # gracg_name=
#         client.update_one({
#             "home_user":parameter[0],
#             "page_id":parameter[2],
#         },{"$set":{
#            "gracg_name":parameter[0],
#             "page_url":parameter[1]
#         }},True)
#         return None

# #声明持久化的数据利用queue来做状态管理
# # 
#     @classmethod
#     def mongo_insrt(self,client,args,state:Queue):
#         if self.mongo_select(client, args[1]) == 1:
#             # 重复则返回状态码-1
#             state.put(-1)
#             return  state
#         else:
#              client.insert_one({
#                 "key_id": args[0],
#                 "page_id":args[2],
#                 "gracg_name": "",
#                 "home_user": args[1],
#                 "page_url": "",
#                 "insert_date": datetime.datetime.now(),
#                 "update_date": "",
#                 "operate_id ": "",
#                 "path": "",
#              })
#         return state

#     def mongo_isEnty(self,user, url):
#         return None

#     @classmethod
#     def mongo_select(self,chient,url):
#         reon = chient.count_documents({
#             'home_user':url
#         })
#         # 集合不能存在重复数据，
#         # 如果需要索引获取值那么可以通过list来转换数据,成为列表后可以通过索引执行
#         if (reon >= 15):
#             return 1
#         else:
#             return "已经添加完成数据！"
        
        
#     @classmethod
#     def mongo_select_url(self,client):
#         page_url = []
#         reons = list(client.find({},{
#             'home_user':1,
#             "page_id":1
#         }))
#         for reon in range(0,9):
#             page_url.append(
#                 reons[reon].get("home_user")
#             )
#         return page_url
    
    


# # 创建数据库对象

# db = pymysql.connect(host='localhost',
#                      user='testuser',
#                      password='test123',
#                      database='TESTDB'
# )

# # 使用 cursor() 方法创建一个游标对象 cursor
# cursor = db.cursor()

# # 使用 execute()  方法执行 SQL 查询
# cursor.execute("SELECT VERSION()")

# # 使用 fetchone() 方法获取单条数据.
# data = cursor.fetchone()

# print("Database version : %s " % data)

# # 关闭数据库连接
# db.close()