package top.lrshuai.blog.dao;

import java.util.List;

import top.lrshuai.blog.plugin.Page;
import top.lrshuai.blog.util.ParameterMap;

public interface PublicDao {
	/**
	 * 保存浏览记录
	 * 
	 * @param pm
	 * @return
	 */
	public int saveBrowse(ParameterMap pm);
	
	/**
	 * 保存每日浏览的统计数
	 * @param pm
	 * @return
	 */
	public int saveEveryDayBrowseNum(ParameterMap pm);
	
	/**
	 * 保存黑名单
	 * @param pm
	 * @return
	 */
	public int addBlackIP(ParameterMap pm);


	/**
	 * 更改浏览数
	 * 
	 * @param pm
	 * @return
	 */
	public int updateBrowseNum(ParameterMap pm);
	
	public int updateBlackIP(ParameterMap pm);

	/**
	 * 保存点赞记录
	 * 
	 * @param pm
	 * @return
	 */
	public int savePraise(ParameterMap pm);

	/**
	 * 保存评论
	 * 
	 * @param pm
	 * @return
	 */
	public int saveComment(ParameterMap pm);
	

	/**
	 * 保存留言
	 * 
	 * @param pm
	 * @return
	 */
	public int saveLeaveWord(ParameterMap pm);
	
	/**
	 * 添加友链
	 * @param pm
	 * @return
	 */
	public int saveFriendLink(ParameterMap pm);

	/**
	 * 删除点赞记录
	 * 
	 * @param pm
	 * @return
	 */
	public int delPraise(ParameterMap pm);

	
	/**
	 * 删除评论
	 * 
	 * @param pm
	 * @return
	 */
	public int delComment(ParameterMap pm);


	/**
	 * 更改点赞数
	 * 
	 * @param pm
	 * @return
	 */
	public int updatePraiseNum(ParameterMap pm);

	/**
	 * 更改评论数
	 * 
	 * @param pm
	 * @return
	 */
	public int updateCommentNum(ParameterMap pm);
	
	
	public int saveQrcode(ParameterMap pm);

	/**
	 * 是否重复点赞
	 * 
	 * @param pm
	 * @return
	 */
	public ParameterMap repeatPraise(ParameterMap pm);
	
	/**
	 * 通过类型获取留言
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getleaveWordByTime(ParameterMap pm);


	/**
	 * 检测是否是楼层评论
	 * 
	 * @param pm
	 * @return
	 */
	public ParameterMap checkCommentId(ParameterMap pm);

	/**
	 * 获取楼主的评论
	 * 
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getCommentlistPage(Page page);
	
	/**
	 * 获取留言列表
	 * @param page
	 * @return
	 */
	public List<ParameterMap> getleaveWordlistPage(Page page);


	/**
	 * 获取回复楼主的评论
	 * 
	 * @param page
	 * @return
	 */
	public List<ParameterMap> getCommentlist(ParameterMap pm);
	
	
	public List<ParameterMap> getJokelist(ParameterMap pm);
	
	public List<ParameterMap> getBlackIpList();
	
	public List<ParameterMap> getDayBrowseList();

	/**
	 * 文章的作者iD
	 * 
	 * @param pm
	 * @return
	 */
	public ParameterMap getArticleAutherId(ParameterMap pm);

	/**
	 * 评论插件获取数据接口
	 * @param page
	 * @return
	 */
	public List<ParameterMap> getleaveWordslistPage(Page page);
	public List<ParameterMap> getleaveWordlist(ParameterMap pm);

}
