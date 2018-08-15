package top.lrshuai.blog.util;

/**
 * 常量
 * @author tyro
 *
 */
public class Const {
	public static final String SESSION_USER="SESSION_USER";
	public static final String SESSION_TOKEN="SESSION_TOKEN";
	public static final String SESSION_SAVE_QUESTION="SESSION_SAVE_QUESTION";
	public static final String AUTH_CODE="AUTH_CODE";
	public static final String AUTH_PAGE="AUTH_PAGE";
	
	//下面是为了点赞状态
	public static final String BLOG_ARTICLE_PRAISE_INDEX_="SPRINGBOOT_BLOG_ARTICLE_PRAISE_INDEX_";
	public static final String BLOG_COMMENT_PRAISE_INDEX_="SPRINGBOOT_BLOG_COMMENT_PRAISE_INDEX_";
	//为了查找上下篇
	public static final String USER_LAST_ARTICLE_LIST="SPRINGBOOT_BLOG_COMMENT_PRAISE_INDEX_";
	//下面是浏览数统计
	public static final String BLOG_BROWSE_INDEX="SPRINGBOOT_BLOG_BROWSE_INDEX"; //文章详情统计
	public static final String BLOG_BROWSE_PEOPLE_NUMBER="BLOG_BROWSE_PEOPLE_NUMBER";	//每日访问统计
	public static final String BLOG_BROWSE_TOTAL_NUMBER="BLOG_BROWSE_TOTAL_NUMBER";	//每日访问统计
	public static final String ADD_LINKS_INDEX="ADD_LINKS_INDEX";
	public static final String LEAVEWORD_NUMBER="LEAVEWORD_NUMBER";
	public static final String COMMENT_NUMBER="COMMENT_NUMBER";
	
	//留言 数统计
	public static final String LEAVEWORD_FIRE_NUM="LEAVEWORD_FIRE_NUM_";
	public static final String LEAVEWORD_NICE_NUM="LEAVEWORD_NICE_NUM_";
	public static final String LEAVEWORD_TREAD_NUM="LEAVEWORD_TREAD_NUM_";
	
	//地址访问统计
	public static final String VISIT_="VISIT_";
	
	
	// 下面是文章缓存前缀
	public static final String ARTICLE_HOME = "SPRINGBOOT_ALL_ARTICLE"; // 文章首页
	public static final String ARTICLE_LABEL = "SPRINGBOOT_LABEL_ARTICLE"; // 文章的所有标签列表
	public static final String ARTICLE_MONTH = "SPRINGBOOT_MONTH_ARTICLE"; // 文章的归档列表
	public static final String ARTICLE_RECOMMEND = "SPRINGBOOT_RECOMMEND_ARTICLE"; // 推荐文章列表
	public static final String ARTICLE_HOT = "SPRINGBOOT_HOT_ARTICLE"; // 热门文章列表

	public static final String MUSIC_LIST = "SPRINGBOOT_MUSIC_LIST"; // 音乐列表

	public static final String ARTICLE_LABEL_ = "SPRINGBOOT_LABEL_ARTICLE_"; // 标签下的文章列表
	public static final String ARTICLE_MONTH_ = "SPRINGBOOT_MONTH_ARTICLE_";
	public static final String BLOG_FRIEND_LINK = "SPRINGBOOT_FRIEND_BLOG_LINK"; // 友情链接
	
	
	public static final String EVERYDAY_JOKE = "EVERYDAY_JOKE"; // 每日一句
	public static final String TIME_LINE_LIST = "TIME_LINE_LIST"; // 时间轴
	public static final String INTRODUCE = "INTRODUCE"; // 时间轴
	
	public static final String BLACK_IP_LIST = "BLACK_IP_LIST"; // 时间轴
	public static final String DAY_BROWSE_LIST = "DAY_BROWSE_LIST"; // 时间轴

	// 热门搜索缓存的id
	public static final String HOT_ARTICLE_ID_LIST = "SPRINGBOOT_HOT_ARTICLE_ID_LIST"; // 归档下的文字列表
	
}
