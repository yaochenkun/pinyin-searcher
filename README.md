# PinyinSearcher
一个支持以汉字、拼音首字母、拼音前缀、非汉字串前缀、非汉字串后缀等及他们混合进行关键字搜索的jar包。
## 配置
1. 引入依赖jar包：pinyin4j-2.5.0.jar 或 采用Maven
```XML
<dependency>
    <groupId>com.belerweb</groupId>
    <artifactId>pinyin4j</artifactId>
    <version>2.5.0</version>
</dependency>
```
2. 引入pinyin_searcher.jar
```Shell
#进入pinyin_searcher.jar所在目录
mvn install:install-file -Dfile=pinyin_searcher.jar -DgroupId=org.ken -DartifactId=searcher -Dversion=1.0.0 -Dpackaging=jar
```
```XML
<dependency>
    <groupId>org.ken</groupId>
    <artifactId>searcher</artifactId>
    <version>1.0.0</version>
</dependency>
```
## 使用方法
1. 调用PinyinSearcher构造搜索器
2. 调用match(关键字,  待搜的实体列表,  待搜实体字段名)进行搜索
3. 返回匹配上的实体列表
```Java
List<Object> res = new PinyinSearcher().match("逍yao", beans, "name");
```
## 示例
```Java
//构造你的实体列表(这里必须是List<Object>)
List<Object> beans = new ArrayList<Object>();
beans.add(new YourBean(1, "李逍遥", "四川省成都市锦江区"));
beans.add(new YourBean(2, "李晓姚", "四川省成都市"));
beans.add(new YourBean(3, "李xiaoyao", "四川省自贡市"));
beans.add(new YourBean(4, "xiaoyao", "四川省南充市阆中"));
beans.add(new YourBean(5, "lixiao遥", "北京市海淀区"));
beans.add(new YourBean(6, "阳sunny光", "北京市朝阳区"));
beans.add(new YourBean(7, "阳sunny光好", "北京市朝阳区"));
beans.add(new YourBean(8, "阳sunnyguang", "北京"));
beans.add(new YourBean(9, "阳sunguang", "北京"));
beans.add(new YourBean(10, "阳光", "河北省保定市"));
beans.add(new YourBean(11, "", "河北省邢台市"));
beans.add(new YourBean(12, null, "河北省安新县"));

//调用拼音搜索器
PinyinSearcher searcher = new PinyinSearcher();
List<Object> res_name = searcher.match("xy", beans, "name"); 
List<Object> res_address = searcher.match("sichuans", beans, "address");

//得到搜索结果(从这里就可以写自己的业务了)
for(Object object : res_name) {
    YourBean bean = (YourBean) object; //取得匹配name的bean
    System.out.println(bean.getName());
}
for(Object object : res_address) {
    YourBean bean = (YourBean) object; //取得匹配address的bean
    System.out.println(bean.getAddress());
}
```
## 示例结果
### name:
李逍遥<br>
李晓姚
### address:
四川省成都市锦江区<br>
四川省成都市<br>
四川省自贡市<br>
四川省南充市阆中
## 支持的关键字形式
* 全汉字：李 -> 李逍遥、李晓姚、李xiaoyao
* 全拼音首字母：lxy -> 李逍遥、李晓姚
* 全拼音前缀：lixia -> 李逍遥、李晓姚、李xiaoyao
* 汉字 + 拼音首字母：李xy -> 李逍遥、李晓姚
* 汉字 + 拼音前缀：李xi -> 李逍遥、李晓姚、李xiaoyao
* 全非汉字前缀：sun -> 阳sunny光、阳sunnyguang、阳sun光、阳sunny光好
* 全非汉字后缀：nny -> 阳sunny光、阳sunnyguang、阳sunny光好
* 汉字 + 非汉字串前缀 ：阳sun -> 阳sunny光、阳sunnyguang、阳sun光、阳sunny光好
* 完整非汉字串 + 拼音前缀：sunnygu -> 阳sunny光、阳sunnyguang、阳sunny光好
* 完整非汉字串 + 拼音首字母：sunnygh -> 阳sunny光好
* 非汉字后缀 + 汉字：nny光 -> 阳sunny光、阳sunny光好
* 还有很多组合形式这里就不一一列举了。
## 注意
* 由于算法原因，目前暂时不支持对 *.?+$^(){}|\/和中括号 的搜索
