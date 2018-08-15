package top.lrshuai.blog.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

import top.lrshuai.blog.util.ParameterMap;

public interface IPublicService {

	/**
	 * 保存浏览记录
	 * 
	 * @param pm
	 * @return
	 */
	public int saveBrowse(ParameterMap pm);
	
	public int saveDayBrowseNum(ParameterMap pm);

	/**
	 * 保存点赞记录
	 * 
	 * @param pm
	 * @return
	 */
	public Map<String, Object> savePraise(ParameterMap pm,HttpSession session);
	
	
	public Map<String, Object> saveFriendLink(ParameterMap pm,HttpSession session);


	/**
	 * 留言
	 * 
	 * @param pm
	 * @return
	 */
	public Map<String, Object> leaveword(ParameterMap pm, HttpSession session);
	
	/**
	 * 获取留言
	 * @param pm
	 * @param session
	 * @return
	 */
	public Map<String, Object> getLeaveWord(ParameterMap pm,HttpSession session);
	
	public Map<String, Object> editBlackIpList(ParameterMap pm,HttpSession session);

	/**
	 * 评论
	 * 
	 * @param pm
	 * @return
	 */
	public Map<String, Object> comment(ParameterMap pm,HttpSession session);


	/**
	 * 获取评论
	 * 
	 * @param pm
	 * @return
	 */
	public Map<String, Object> getComment(ParameterMap pm,HttpSession session);

	/**
	 * 删除评论
	 * 
	 * @param pm
	 * @return
	 */
	public Map<String, Object> delComment(ParameterMap pm,HttpSession session);
	public int saveBlackIp(ParameterMap pm);
	
	public Map<String, Object> talk(ParameterMap pm,HttpSession session);
	public Map<String, Object> gettalk(ParameterMap pm,HttpSession session);
	public Map<String, Object> qrcode(ParameterMap pm,HttpSession session);
}
