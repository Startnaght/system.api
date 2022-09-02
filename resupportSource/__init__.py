import persistenceStsyem as mongdb
from gracgImgMain import gracg 
# from bufferStruct import system

if __name__=='__main__':
    # 初始化数据库
    client = mongdb.saveData().chient("gracg", "page_url")
    gr = gracg(client)
    gr.resp_quests()