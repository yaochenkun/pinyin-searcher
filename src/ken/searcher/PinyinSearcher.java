package ken.searcher;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinyinSearcher {

	private List<Object> queriedBeanList; // 待查实体列表
	private List<String> regex; // 待查实体指定关键字列构成的正则式列表

	/**
	 * 匹配与整理bean到最终返回集合中
	 * term:要搜索的关键词
	 */
	public List<Object> startMatching(String term) {

		List<Object> resultList = new ArrayList<Object>();
		for (int i = 0; i < regex.size(); i++) {
			if (term.matches(regex.get(i)))
				resultList.add(queriedBeanList.get(i));
		}

		return resultList;
	}

	/**
	 * 预处理被搜索的关键词 将其拆解为正则表达式
	 */
	public void preProcessKeywords(Method getValueMethod)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		if (queriedBeanList == null || queriedBeanList.isEmpty())
			return;

		// 初始化正则式
		for (Object bean : queriedBeanList) {

			// 拆分汉字与非汉字字符串
			// 如 姚sunny陈ken堃 {"姚","sunny","陈","ken","堃"}
			String queryString = (String) getValueMethod.invoke(bean);
			if (queryString == null || queryString.equals("")) // 若为空regax置为""
			{
				regex.add("");
				continue;
			}

			Word[] words = splitStrIntoHanziAndOthers(queryString);
			int length = words.length;

			// 若为汉字转成拼音及相应正则，若为非汉字串转成前缀、全排列、后缀
			String chineseReg = "[\\u4e00-\\u9fa5]";
			for (int i = 0; i < length; i++) {

				if (words[i].getContent().matches(chineseReg)) // 汉字
				{
					words[i].formHanziReg(); // 生成拼音正则
				} else // 非汉字串
				{
					words[i].formFixReg(); // 生成非汉字串前缀、后缀、全排列
				}
			}

			String mRegex = "";
			String subRegex = "";
			boolean ADD_SUBREG_FLAG = false; // 是否输出子正则表达式（如 姚|yao sunny）
			for (int i = 0; i < length; i++) {
				for (int j = i; j < length; j++) {
					ADD_SUBREG_FLAG = false;
					subRegex = "";
					for (int k = j; k >= i; k--) {
						String hanziReg = words[k].getHanziReg();
						if ("".equals(hanziReg)) // 非汉字串
						{
							// 判断该非汉字串所在位置
							if (j > i) // 中间
							{
								if (k < j && k > i) // 中间 取子正则式
								{
									subRegex = words[k].getCompleteReg() + subRegex;
									ADD_SUBREG_FLAG = true;
								} else if (k >= j) // 最后一个 取前缀
								{
									subRegex = words[k].getPrefixReg() + subRegex;
									ADD_SUBREG_FLAG = true;
								} else // 第一个k <= i 取后缀
								{
									subRegex = words[k].getSuffixReg() + subRegex;
								}
							} else // 第一个且最后一个 取全排列
							{
								subRegex = words[k].getFullfixReg() + subRegex;
							}
						} else // 汉字
						{
							if (ADD_SUBREG_FLAG) // 输出子正则式
								subRegex = words[k].getCompleteReg() + subRegex;
							else
								subRegex = hanziReg + subRegex;
						}
					}
					mRegex += subRegex + "|";
				}
			}

			mRegex = mRegex.substring(0, mRegex.length() - 1);
			mRegex = mRegex.replaceAll("\\.", "#"); // 屏蔽掉邮箱的.号，防止其加入正则表达式
			regex.add(mRegex);
		}
	}

	/**
	 * 将字符串拆分成单个汉字与非字符串
	 */
	public Word[] splitStrIntoHanziAndOthers(String str) {
		Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]|[^\\u4e00-\\u9fa5]+");
		Matcher m = p.matcher(str);
		List<String> list = new ArrayList<String>();
		while (m.find()) {
			list.add(m.group());
		}
		Word[] res = new Word[list.size()];
		for (int i = 0; i < list.size(); i++)
			res[i] = new Word(list.get(i).toLowerCase());

		return res;

	}

	/**
	 * keyword:输入的关键字 kws:被查关键字集 ids:被查关键字所在索引集
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Object> match(String keyword, List<Object> queriedBeanList, String domainName)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		if (keyword == null || queriedBeanList == null || queriedBeanList.isEmpty())
			return Collections.emptyList();

		this.queriedBeanList = queriedBeanList;
		this.regex = new ArrayList<String>();

		// 利用反射获取方法
		Class beanClass = queriedBeanList.get(0).getClass();
		String methodName = convertToMethodName(domainName);
		Method getValueMethod = beanClass.getDeclaredMethod(methodName);

		// 预处理待搜索的关键字
		preProcessKeywords(getValueMethod);

		// 返回被匹配上的关键词对象
		return startMatching(keyword.toLowerCase());
	}

	// 将domainName转换成getDomainName
	private String convertToMethodName(String domainName) {
		char firstCh = domainName.charAt(0);
		if (firstCh >= 'a' && firstCh <= 'z')
			firstCh = (char) (firstCh - 'a' + 'A');

		return "get" + firstCh + domainName.substring(1);
	}

}
