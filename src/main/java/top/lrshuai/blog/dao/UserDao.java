package top.lrshuai.blog.dao;

import top.lrshuai.blog.util.ParameterMap;

public interface UserDao {

	/**
	 * 保存用户信息
	 * 
	 * @param pm
	 * @return
	 */
	public int saveUser(ParameterMap pm);
	
	
	public ParameterMap getTotalPraiseNum(ParameterMap pm);

	/**
	 * 更新用户信息
	 * 
	 * @param pm
	 * @return
	 */
	public int updateUserInfo(ParameterMap pm);

	/**
	 * 获取用户信息
	 * 
	 * @param pm
	 * @return
	 */
	public ParameterMap getUserInfo(ParameterMap pm);
	
	
	public ParameterMap getAboutMeInfo(ParameterMap pm);

	/**
	 * 查看是否有重复的email
	 * 
	 * @param pm
	 * @return
	 */
	public ParameterMap repeatEmail(ParameterMap pm);
}
