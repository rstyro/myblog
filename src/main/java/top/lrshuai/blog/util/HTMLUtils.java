package top.lrshuai.blog.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author lrshuai
 * @since 2017-10-13
 * @version 0.0.1
 */
public class HTMLUtils {
	/**
	 * 过滤所有HTML 标签
	 * @param htmlStr
	 * @return
	 */
	public static String filterHTMLTag(String htmlStr) {
		//定义HTML标签的正则表达式 
		String reg_html="<[^>]+>"; 
        Pattern pattern=Pattern.compile(reg_html,Pattern.CASE_INSENSITIVE); 
        Matcher matcher=pattern.matcher(htmlStr); 
        htmlStr=matcher.replaceAll(""); //过滤html标签 
		return htmlStr;
	}
	
	/**
	 * 过滤标签，通过标签名
	 * @param htmlStr
	 * @param tagName
	 * @return
	 */
	public static String filterTagByName(String htmlStr,String tagName) {
		String reg_html="<"+tagName+"[^>]*?>[\\s\\S]*?<\\/"+tagName+">";
		Pattern pattern=Pattern.compile(reg_html,Pattern.CASE_INSENSITIVE); 
		Matcher matcher=pattern.matcher(htmlStr); 
		htmlStr=matcher.replaceAll(""); //过滤html标签 
		return htmlStr;
	}

	/**
	 * 过滤标签上的 style 样式
	 * @param htmlStr
	 * @return
	 */
	public static String filterHTMLTagInStyle(String htmlStr) {
		String reg_html="style=('|\")(.*?)('|\")";
		Pattern pattern=Pattern.compile(reg_html,Pattern.CASE_INSENSITIVE); 
		Matcher matcher=pattern.matcher(htmlStr); 
		htmlStr=matcher.replaceAll(""); //过滤html标签 
		return htmlStr;
	}
	
	/**
	 * 替换表情
	 * @param htmlStr
	 * @param tagName
	 * @return
	 */
	public static String replayFace(String htmlStr) {
		String reg_html="\\[qq_\\d{1,}\\]";
		Pattern pattern =Pattern.compile(reg_html,Pattern.CASE_INSENSITIVE); 
		Matcher matcher=pattern.matcher(htmlStr);
		if(matcher.find()) {
			matcher.reset();
			while(matcher.find()) {
				String num = matcher.group(0);
				String number=num.substring(num.lastIndexOf('_')+1, num.length()-1);
				int numb = Integer.parseInt(number);
				if(numb <= 75) {
					htmlStr = htmlStr.replace(num, "<img src='/comment/face/emoji1/"+number+".gif' border='0' />");
				}
			}
		}
		
		String reg_html2="\\[em_\\d{1,}\\]";
		Pattern pattern2 =Pattern.compile(reg_html2,Pattern.CASE_INSENSITIVE); 
		Matcher matcher2=pattern2.matcher(htmlStr);
		if(matcher2.find()) {
			matcher2.reset();
			while(matcher2.find()) {
				String num = matcher2.group(0);
				String number=num.substring(num.lastIndexOf('_')+1, num.length()-1);
				int numb = Integer.parseInt(number);
				if(numb <= 52) {
					htmlStr = htmlStr.replace(num, "<img src='/comment/face/emoji2/"+number+".png' border='0' />");
				}
			}
		}
		return htmlStr;
	}
	
	public static void main(String[] args) {
		String html = "<script>alert('test');</script><img src='/face/arclist/5.gif' border='0' /><div style='position:fixs;s'></div><span style='position:fixs;s'></span><style>body{color:#fff;}</style><Style>body{color:#fff;}</Style><STYLE>body{color:#fff;}</STYLE>";
		String html2 = "[em_15]asdfa[em_1]s[em_12][em_][em_72][em_75]sdfs[em_76][em_74]";
//		System.out.println("html="+html);
//		html = HTMLUtils.filterTagByName(html, "style");
//		System.out.println("html="+html);
//		html = HTMLUtils.filterTagByName(html, "script");
//		System.out.println("html="+html);
//		html = HTMLUtils.filterHTMLTagInStyle(html);
//		System.out.println("html="+html);
		html= HTMLUtils.replayFace(html2);
		System.out.println("html="+html);
	}
	
}
