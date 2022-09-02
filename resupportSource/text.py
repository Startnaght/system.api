from numpy import append

data=[]
str1 ="zhangsan"
st2="lisi"
#python lambda不适合用于list数组的添加，
lambda a,b:data.append(a+","+b)(str1,st2)
print(data)
