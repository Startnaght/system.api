import time

import requests
import re
from lxml import etree
import persistenceStsyem as mongdb
import datetime
import bufferStruct as util
from queue import Queue
# 需要实现一个聚合算法，并让其自动归类
# 網址
# http://gracg.com/home/artist?call=&page=5

# home_url = "http://gracg.com/home/artist"

class gracg(util.stream):

    def __init__(self,client) -> object:
        self.client = client
        # 每次请求的返回网页也就是分页,每个分页大概是16个id
        self.list_url = mongdb.saveData.mongo_select_url(client)
        # 初始化链表用来存放全局数据
        self.grace_global = util.img_struct_head()
        self.state=Queue()#声明五种状态  第三种状态则是获取总的页面 第四种声明用户界面的获取成功 第五种状态说明获取每个状态集成功
        # 测试时只设置为1
        self.requests_max_page = 1
        self.production=[object]


# 这个方法保存到mongdb数据库中 值分别为 /home /artist ?call= &page= 页数即具体的page->int
    def url(self,args):
        url = "https://www.gracg.com" + args[0] + "" + args[1] + "" + args[2] + "" + args[3] + "" + args[4] + ""
        try:
            for item in range(0,16):
                self.state = mongdb.saveData.mongo_insrt(self.client, args[4], url,item,Self.state)
            raise util.errSavePersistence(self.state.get())
        except util.errSavePersistence as err:
            pass
             
            
        
# 获得完成状态
    def resp_code(self): 
        # 调用函数成对主页的具体url的统计 100
        for item in range(0, 20):
            self.url(["/home", "/artist", "?call=", "&page=", str(item)])
            
        # 用来判断mongdb是否存在数据的状态
        if not self.state:
            print("all value nalready exists!!")
        else:
            print("成功！")

# 开始通过requests请求数据
    def resp_quests(self):
        
        headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36 Edg/94.0.992.38'}
        # 默认为真
        if  self.state == False:
            # 获取用户主界面的页面
            for item in range(0, self.requests_max_page):
                response = requests.get(url=self.list_url[item], headers=headers)
                if response.status_code == 200:
                    print('请求状态处理成功')
                    if self.requests_max_page == item:
                        self.state = True
                        self.responseFiltrate(response.text)
                    else:
                        self.responseFiltrate(response.text)
                else:
                    print('状态返回失败%d', format(response.status_code))
        # state当状态处于true是则代表获取主页面已经获取完成
        elif state:
           pass 
        else:
            while util.img_struct_data_field().next == None:
                url = "https://www.gracg.com/"+util.img_struct_data_field().mongo_data[1]
                response_user_page = requests.get(url=url,headers=headers,timeout=10)
                if response_user_page.status_code == 200:
                    print("请求结果成功,现在开始加载用户production数据流")
                    self.atlas(response_user_page.text)
                else:
                    print("返回状态异常，异常返回状态码为{0}",format(response_user_page.status_code))
        
                    

    #筛选返回的数据s
    def responseFiltrate(self, resp:str)->None:
        # [^>]([^>][\u4e00-\u9fa5])([\u4e00-\u9fa5]+)[^<] 获取name的
        allList = re.findall(r'href="\/user\/.*" class="font\-20 font\-weight\-bold d\-block.*',resp)
        for items in range(0,len(allList)):
            self.url  =re.search('href="(?<=").*?(?=")',allList[items]).group().split("\"")[1]
            self.name = re.search('(?<=\>).*?(?=<)',allList[items]).group()
            # 把数据调给grace_globle 一部分持久化
            save_input = util.save_input_parameter(home=self.name,url=self.url,page_id=items)
            if self.grace_global == None:
                self.grace_global.head=util.img_struct_data_field(data=save_input.updata_paraeter_mongodb().__dict__,path=" ")
            else:
                util.struct_status_headAndtail().questsTail().next = util.img_struct_data_field(data=save_input.updata_paraeter_mongodb().__dict__,path=" ")
                print("name={0} url={1}".format(self.name,self.url))
                mongdb.saveData.mongo_update(self.client,save_input.updata_paraeter_mongodb())

# 获取保存的数据
    def atlas(self, response_user_page):

        img_re = re.findall('data-src=".*?"', str(response_user_page))
        img_names = re.findall('<title>.*?_', str(response_user_page))
        img_one = "".join(img_re).split("\"")[1]
        img_name = "".join(img_names).split(">")[1]
        # img_two = eval(str(img_re[0]).split("=")[1])
        self.save_img(img_one, img_name)


# 用来保存到本地的数据
    def save_img(self, img_name):
        time.sleep(1)
        file_name = str(self).split("/")+[4] + img_name
        response = requests.get(self, headers=self.headers, timeout=1)
        with open(file_name + ".jpeg", 'wb') as fp:
            fp.write(response.content)

