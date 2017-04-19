# PinyinSearcher
一个支持以汉语拼音首字母、拼音前缀、拼音后缀、英文及其他符号作为关键字混合搜索的jar包
## 配置
1. 引入pinyin_searcher.jar
2. 引入依赖jar包：pinyin4j-2.5.0.jar 或 采用Maven
```XML
<dependency>
    <groupId>com.belerweb</groupId>
    <artifactId>pinyin4j</artifactId>
    <version>2.5.0</version>
</dependency>
```
## 使用方法
```Java
//"逍遥"：用户输入的关键字
//beans：你的实体列表
//"name"：你的实体属性字段名称，作为待搜索的列
//res：搜索到的你的实体列表
List<Object> res = searcher.match("逍遥", beans, "name");
```
## 示例
```Java
//构造数据集（需要构造自己的bean）
List<Object> beans = new ArrayList<Object>(); 、
beans.add(new YourBean(1, "李逍遥", "四川省成都市锦江区"));
beans.add(new YourBean(2, "李晓姚", "四川省成都市"));
beans.add(new YourBean(3, "李xiaoyao", "四川省自贡市"));
beans.add(new YourBean(4, "xiaoyao", "四川省南充市阆中"));
beans.add(new YourBean(5, "lixiao遥", "北京市海淀区"));
beans.add(new YourBean(6, "阳sunny光", "北京市朝阳区"));
beans.add(new YourBean(7, "阳sunnyguang", "北京"));
beans.add(new YourBean(8, "阳光", "河北省保定市"));
beans.add(new YourBean(9, "", "河北省邢台市"));
beans.add(new YourBean(10, null, "河北省安新县"));

//调用拼音搜索器
PinyinSearcher searcher = new PinyinSearcher();
List<Object> res_name = searcher.match("xy", beans, "name");
List<Object> res_address = searcher.match("成d", beans, "address");


//得到搜索结果
//res_name和res_address
for(Object object : res_name) {
    YourBean bean = (YourBean) object; //获得匹配的bean
    System.out.println(bean.getName());
}
```
