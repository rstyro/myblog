package top.lrshuai.blog.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

import top.lrshuai.blog.util.ParameterMap;


public interface IUserService {


	/**
	 * 登陆
	 * 
	 * @param pm
	 * @return
	 */
	public Map<String, Object> login(ParameterMap pm,HttpSession session);

	/**
	 * 回调
	 * 
	 * @param pm
	 * @return
	 */
	public Map<String, Object> qqredirect(ParameterMap pm,HttpSession session);


}
