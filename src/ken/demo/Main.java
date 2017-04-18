package ken.demo;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import ken.searcher.PinyinSearcher;


public class Main {
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		//构造数据集（需要自己构造bean）
		List<Object> beans = new ArrayList<Object>();
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
		
		//调用关键字搜索器
		PinyinSearcher searcher = new PinyinSearcher();
		List<Object> res_name = searcher.match("xy", beans, "name");
		List<Object> res_address = searcher.match("成d", beans, "address");
		
		
		//打印搜索结果
		System.out.println("——————————name——————————");
		for(Object object : res_name) {
			YourBean bean = (YourBean) object; //获得匹配的bean
			System.out.println(bean.getName());
		}
		
		System.out.println("——————————address——————————");
		for(Object object : res_address) {
			YourBean bean = (YourBean) object; //获得匹配的bean
			System.out.println(bean.getAddress());
		}
	}
}
