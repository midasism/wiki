### 项目结构
* entity：实体类
* resp：统一接口返回值
    * 有时只返回部分字段给前端，可以单独为实体类封装一个返回类
  
* req：封装参数(Spring自动将参数映射到实体类的属性)

* util：工具类
  * CopyUtil：封装BeanUtils，方便复制对象