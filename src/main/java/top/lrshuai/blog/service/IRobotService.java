package top.lrshuai.blog.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

import top.lrshuai.blog.util.ParameterMap;

public interface IRobotService {

	public Map<String,Object> save(ParameterMap pm,HttpSession session);
	public Map<String,Object> answer(ParameterMap pm,HttpSession session);
	
	public Map<String,Object> find(ParameterMap pm);
	public Map<String,Object> del(ParameterMap pm,HttpSession session);
	public Map<String,Object> update(ParameterMap pm,HttpSession session);
	
	public Map<String,Object> getBrainList(ParameterMap pm);
}
