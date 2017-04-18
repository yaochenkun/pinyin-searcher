package ken.searcher;

import java.util.LinkedList;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;

//拆解元素（单字 或 非字串）
public class Word{
	
	private String content=""; //内容（如 姚 sunny 12*lj）
	
	//汉字使用
	private String[] pinyin=null; //拼音(如 yao 非字串为null,作标志) 类型为字符串数组是因为多音字
	private String hanziReg = "";//汉字完整正则式（如 姚|y|ya|yao）
	
	//非汉字串使用
	private String prefixReg=""; //前缀正则
	private String fullfixReg=""; //全排列正则
	private String suffixReg=""; //后缀正则
	
	private String completeReg = ""; //满的子正则式（如 (姚|yao) (sunny)）
	
	Word(String content)
	{
		this.content = content;
	}

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String[] getPinyin() {
		return pinyin;
	}

	public void setPinyin(String[] pinyin) {
		this.pinyin = pinyin;
	}

	public String getHanziReg() {
		return hanziReg;
	}

	public void setHanziReg(String hanziReg) {
		this.hanziReg = hanziReg;
	}

	public String getPrefixReg() {
		return prefixReg;
	}

	public void setPrefixReg(String prefixReg) {
		this.prefixReg = prefixReg;
	}
	
	public String getFullfixReg() {
		return fullfixReg;
	}

	public void setFullfixReg(String fullfixReg) {
		this.fullfixReg = fullfixReg;
	}


	public String getSuffixReg() {
		return suffixReg;
	}

	public void setSuffixReg(String suffixReg) {
		this.suffixReg = suffixReg;
	}
	
	public String getCompleteReg() {
		return completeReg;
	}

	public void setCompleteReg(String completeReg) {
		this.completeReg = completeReg;
	}

	
	//生成汉字的正则式：姚|y|ya|yao
	public void formHanziReg()
	{
		//由汉字content生成pinyin：姚->yao
		String[] pinyin = disrepeat(PinyinHelper.toHanyuPinyinStringArray(content.charAt(0)));
		
		//单字完整正则式、单字子正则式
		hanziReg = "(" + content;
		for (int j = 0; j < pinyin.length; j++) {//一个字的每个拼音
			for (int k = 0; k < pinyin[j].length(); k++) {//一个拼音的每个字母
				hanziReg += "|" + pinyin[j].substring(0, k + 1);
			}
		}
		hanziReg += ")";
		
		completeReg = "(" + content;
		for(int j=0;j<pinyin.length;j++)
			completeReg += "|" +pinyin[j];
		completeReg += ")";
	}
	
	//生成非汉字串的前缀、后缀、全排列
	//如 sunny
	//s|su|sun|sunn|sunny
	//u|un|unn|unny
	//n|nn|nny
	//n|ny
	//y
	public void formFixReg()
	{
//		//递增序列(二维数组方式)
//		int length = content.length();
//		String[][] fullFix = new String[length][length];
//		fullfixReg = "(";
//		for(int i=0;i<length;i++)
//		{
//			for(int j=0;j<length;j++)
//			{
//				fullFix[i][j] = "";
//				for(int k=i;k<j+1;k++)
//				{
//					String sub = content.substring(i, k + 1);
//					fullFix[i][j-i] = sub;
//					fullfixReg += sub + "|";
//				}
//			}
//		}
//		fullfixReg = fullfixReg.substring(0,fullfixReg.length()-1) + ")";
//		
//		//前缀
//		prefixReg = "(";
//		for(int i=0;i<length;i++)
//			prefixReg += fullFix[0][i] + "|";
//		prefixReg = prefixReg.substring(0,prefixReg.length()-1) + ")";
//		
//		//后缀
//		suffixReg = "(";
//		for(int j=length-1;j>=0;j--)
//			suffixReg += fullFix[length-j-1][j] + "|";
//		suffixReg = suffixReg.substring(0,suffixReg.length()-1) + ")";
//		
//		//子正则式(sunny)
//		subReg = "(" + content + ")";
		
		
		
		
		//递增序列(一维数组的方式)
		int length = content.length();
		char[] charStr = content.toCharArray();
		int fullFixLength = length*(length+1)/2;
		String[] fullFix = new String[fullFixLength];

		fullfixReg = "(";
		int n=0;
		for(int i=0;i<length;i++)
		{
			for(int j=i;j<length;j++)
			{
				fullFix[n] = "";
				for(int k=i;k<=j;k++)
				{
					char sub = charStr[k];
					fullFix[n] += sub;
				}
				fullfixReg += fullFix[n] + "|";
				n++;
			}
		}
		fullfixReg = fullfixReg.substring(0,fullfixReg.length()-1) + ")";
		
		//前缀
		prefixReg = "(";
		for(int i=0;i<length;i++)
			prefixReg += fullFix[i] + "|";
		prefixReg = prefixReg.substring(0,prefixReg.length()-1) + ")";
		
		//后缀
		suffixReg = "(";
		for(int i=length-1,j=0;j<length;j++,i=i+length-j)
			suffixReg += fullFix[i] + "|";
		suffixReg = suffixReg.substring(0,suffixReg.length()-1) + ")";
		
		//子正则式(sunny)
		completeReg = "(" + content + ")";
		
//		System.out.println("全排列" + fullfixReg);
//		System.out.println("前缀" + prefixReg);
//		System.out.println("后缀" + suffixReg);
		
	}
	
	/**
	 * 去重<br/>
	 * 我会删掉声调<br/>
	 * 不要随便用我，我是专用的，不是通用的，想去重自己写去，别用我
	 */
	private String[] disrepeat(String[] array) {
		List<String> tmpArray = new LinkedList<String>();
		for (String py : array) {
			String tpy = py.substring(0, py.length() - 1);//去声调
			if (!tmpArray.contains(tpy)) {
				tmpArray.add(tpy);
			}
		}
		
		String [] result = new String[tmpArray.size()];
		for (int i = 0; i < tmpArray.size(); i++) {
			result[i] = tmpArray.get(i);
		}
		return result;
	}
}