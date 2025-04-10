import time

import requests
import re
from lxml import etree
import persistenceStsyem as mongdb
import datetime
import bufferStruct as util
from queue import Queue
import os
# 需要实现一个聚合算法，并让其自动归类
# 網址
# http://gracg.com/home/artist?call=&page=5

# home_url = "http://gracg.com/home/artist"

class Status:
    INITIAL = 0
    FETCHING_USER = 1
    FETCHING_IMAGES = 2
    COMPLETED = 3

class gracg:
    def __init__(self):
        # 初始化状态和请求头
        self.state =False  # 初始状态
        self.code =Status.INITIAL  # 状态码
        self.code = Status.INITIAL
        self.headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36 Edg/94.0.992.38',
            'Referer': 'http://gracg.com/',
        }

    # 发送请求并处理响应
    def resp_quests(self, pre: str) -> None:
        """
        根据传入的路径发送请求，并根据状态处理不同的逻辑。
        """
        time.sleep(3)  # 延时3秒，避免请求过于频繁
        try:
            response = requests.get(url="http://gracg.com/home/" + pre, headers=self.headers)
            if response.status_code == 200 and self.state == False:
                # 初始状态下处理主页内容
                print('请求状态处理成功')
                print(f'当前请求的url为：{"https://gracg.com/home/" + str(pre)}')
                self.responseNameIndex(response.text)
                print("开始执行")
            elif self.state == True and Status.INITIAL ==self.code :
                # 第二层逻辑：获取用户主页
                time.sleep(1)  # 延时1秒
                print('开始请求第二层 获取用户id主页')
                namePageResponse = requests.get(url="https://gracg.com" + pre, headers=self.headers)
                self.response_namePageImg(namePageResponse.text)
                self.codes =Status.INITIAL  # 更新状态码
                print("当前页面请求的url为：", "https://gracg.com/" + pre)
            elif self.code == Status.FETCHING_IMAGES:
                # 第三层逻辑：获取用户主页图片集合
                time.sleep(2)  # 延时2秒
                print('开始请求第二层的用户主页图片集合: code ' + str(response.status_code))
                nameImgPageResponse = requests.get(url="https://gracg.com" + pre, headers=self.headers)
                self.response_PageNamefile(nameImgPageResponse)
                print("当前页面请求的url为：", "https://gracg.com" + pre)
            else:
                print('请求失败，状态码：', response.status_code)
                print('请求的url为：', "http://gracg.com" + pre)
                print("error",response)
        except Exception as e:
            print(f"请求时出错: {e}")

    # 第二层循环：获取作者主页
    def responseNameIndex(self, resp: str) -> None:
        """
        解析主页内容，提取作者主页链接，并递归调用处理。
        """
        if self.state == False:
            print("开始拿到作者主页")
            allList = re.findall(r'/u/user[0-9a-zA-Z]+', resp)
            print(allList)
            print(len(allList))
            self.state = True  # 改变状态码
            self.resp_quests(str(allList[0]))  # 重新调用函数，处理第一个链接
        # elif self.state == True:
        #     for item in range(1, len(allList)):
        #         self.resp_quests(str(allList[item]))  # 处理剩余链接
        #         # self.code=Status.INITIAL
            self.code = Status.FETCHING_USER  # 更新状态码

    # 用来处理主页下的图片
    def response_namePageImg(self, response: str) -> None:
        """
        解析用户主页内容，提取图片相关信息并去重。
        """
        if not isinstance(response, str):
            print("错误：response 不是字符串类型")
            return

        results = []
        # 提取 data-dddd 和 data-cccc 的值
        response_namePageImg_cont = re.findall(r'data-dddd="([^"]+)"|data-cccc="([^"]+)"', response)

        # 过滤掉空字符并提取有效数据
        filtered_results = list(["".join(item) for item in response_namePageImg_cont if any(item)])
        print("过滤后的结果: ", filtered_results)

        # 处理提取的结果
        for item in range(len(filtered_results)):
            if "/" not in filtered_results[item]:
                # 拼接路径
                rest = "/" + filtered_results[item + 1] + "/" + filtered_results[item].replace("//", "/")
                results.append(rest)
            else:
                rest = filtered_results[item].replace("//", "/")
                results.append(rest)

        # 去重并处理每个结果
        self.response_tupianjie(list(set(results)))

    def response_tupianjie(self, linklist: list) -> None:
        # 每个图片集
        for items in range(len(linklist) - 100):
                # 更新状态码为 FETCHING_IMAGES
            print("去重后的数据:\n " + linklist[items]) 
            self.code = Status.FETCHING_IMAGES  # 更新状态码
            self.resp_quests(linklist[items])  # 递归调用处理
           

    # 用来获取图片的 URL
    def response_PageNamefile(self, resp: str) -> None:
        """
        解析用户主页图片集合页面，提取图片链接并保存。
        """
        print("开始获取图片url")
        img_urls = extract_image_urls(resp)
        print("提取的图片链接：", img_urls)
        for item in range(len(img_urls)):
            print("第" + str(item) + "个图片链接：", img_urls[item])
            self.save_img(img_urls[item])  # 保存图片

    # 保存图片到本地
    def save_img(self, img_url, save_dir="images"):
        """
        下载图片并保存到指定目录。
        """
        if not os.path.exists(save_dir):
            os.makedirs(save_dir)

        img_name = os.path.basename(img_url.split("?")[0])
        file_path = os.path.join(save_dir, img_name)

        if os.path.exists(file_path):
            print(f"图片已存在，跳过下载: {file_path}")
            return

        try:
            response = requests.get(url="https://www.gracg.com/"+img_url, header
                                    
                                    s=self.headers, stream=True, timeout=10)
            print("save url:", "https://www.gracg.com/"+img_url)
            if response.status_code == 200:
                with open(file_path, "wb") as img_file:
                    for chunk in response.iter_content(1024):
                        img_file.write(chunk)
                print(f"图片已保存: {file_path}")
            else:
                print(f"无法下载图片: {img_url}, 状态码: {response.status_code}")
        except Exception as e:
            print(f"下载图片时出错: {img_url}, 错误: {e}")

    # 主运行逻辑
    def run(self):
        """
        启动抓取流程。
        """
        print("开始抓取数据...")
        self.resp_quests("artist?call=&page=")
        print("抓取完成！")


# 提取图片链接的工具函数
def extract_image_urls(response: str) -> list:
    """
    从 HTML 响应中提取图片链接。
    """
    if not isinstance(response, str):
        print("错误：response 不是字符串类型")
        print("copy response\n:", response)
        return []
    return re.findall(r"background-image: url\('([^']+)'\)", response)


if __name__ == '__main__':
    # 初始化抓取器并运行
    gr = gracg()
    gr.run()