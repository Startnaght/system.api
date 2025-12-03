# class Node:
#     """链表节点类"""
#     def __init__(self, data=None, path=None):
#         self.data = data  # 节点数据
#         self.path = path  # 节点路径
#         self.next = None  # 指向下一个节点


# class LinkedList:
#     """链表类"""
#     def __init__(self):
#         self.head = None  # 链表头节点

#     def __repr__(self):
#         """返回链表的字符串表示"""
#         nodes = []
#         current = self.head
#         while current:
#             nodes.append(f"({current.data}, {current.path})")
#             current = current.next
#         nodes.append("None")
#         return " -> ".join(nodes)

#     def is_empty(self):
#         """判断链表是否为空"""
#         return self.head is None

#     def append(self, data, path=None):
#         """在链表末尾添加节点"""
#         new_node = Node(data, path)
#         if self.is_empty():
#             self.head = new_node
#         else:
#             current = self.head
#             while current.next:
#                 current = current.next
#             current.next = new_node

#     def prepend(self, data, path=None):
#         """在链表头部添加节点"""
#         new_node = Node(data, path)
#         new_node.next = self.head
#         self.head = new_node

#     def find(self, data):
#         """查找链表中是否存在指定数据的节点"""
#         current = self.head
#         while current:
#             if current.data == data:
#                 return current
#             current = current.next
#         return None

#     def delete(self, data):
#         """删除链表中第一个匹配的数据节点"""
#         if self.is_empty():
#             return

#         if self.head.data == data:
#             self.head = self.head.next
#             return

#         current = self.head
#         while current.next:
#             if current.next.data == data:
#                 current.next = current.next.next
#                 return
#             current = current.next

#     def to_list(self):
#         """将链表转换为列表"""
#         result = []
#         current = self.head
#         while current:
#             result.append((current.data, current.path))
#             current = current.next
#         return result


# # 保存节点输入的参数，即返回格式化参数
# class save_input_parameter:
#     def __init__(self, home: str, url: str, page_id: int) -> list:
#         self.home = home
#         self.url = url
#         self.page_id = page_id
#         self.data = []
#         self.production_url = []

#     # 作为数据整合放入链表
#     @classmethod
#     def updata_paraeter_mongodb(self) -> list:
#         # home_data作为临时变量存储数据 data则是真正返回数据
#         home_data = []
#         home_data.append(self.home)
#         home_data.append(self.url)
#         home_data.append(self.page_id)
#         self.data.append(home_data)

#         return self.data


# # err
# class errSavePersistence(Exception):
#     # 当输入的参数不能被用于持久话时，那么会触发此错误
#     def __init__(self, valse: object) -> None:
#         self.valse = valse

#     def __str__(self) -> str:
#         return ("Err:{}传入参数不合法", format(repr(self.valse)))
