# from persistenceStsyem import saveData as mongdb

# 获取链表的头节点
class img_struct_head:
    def __init__(self) -> None:
        self.head=None
    #书写类的返回对象说明
    def __repr__(self) -> str:
        node=self.head
        nodes=[]
        while node is not None:
            nodes.append(node.data)
            node = node.next
        nodes.append("None")
        return "->".join(nodes)
    

# 定义全局链表 所有数据都会归结到这里
# 数据接受LIST和Object  
class img_struct_data_field:
    def __init__(self,data:object,path:object):
        # 如果方法的周期没变的话，那么LIST返回的数据有且仅有一个，如果并不是那么就会需要遍历取最后一个
        self.mongo_data=data
        self.path=path
        self.len=len(self.mongo_data)
        self.next=None
        
# 添加节点数据
class struct_status_headAndtail:
    def __init__(self) -> None:
        self.temp = img_struct_data_field()
        
    def questsHead(self):
        pass
    
    @classmethod
    def questsTail(self)->img_struct_data_field():
        while self.temp.next == None:
            self.temp = self.temp.next
        return self.temp

# 保存节点输入的参数，即返回格式化参数
class save_input_parameter:
    def __init__(self,home:str,url:str,page_id:int) ->list:
        self.home=home
        self.url=url
        self.page_id=page_id
        self.data = []
        self.production_url=[]
        
        # 作为数据整合放入链表
    @classmethod
    def updata_paraeter_mongodb(self)->list:
        # home_data作为临时变量存储数据 data则是真正返回数据
        home_data =[]
        home_data.append(self.home) 
        home_data.append(self.url)
        home_data.append(self.page_id)
        self.data.append(home_data)
        
        return self.data

#err 
class errSavePersistence(Exception):
    # 当输入的参数不能被用于持久话时，那么会触发此错误
    def __init__(self,valse:object) -> None:
        self.valse=valse
        
    def __str__(self) -> str:
        return ("Err:{}传入参数不合法",format(repr(self.valse)))
    
# 继承父类
class system():
    # def __init__(self) -> None:
    #     pass
    print("***----------")