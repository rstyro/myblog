package top.lrshuai.blog.dao;

import java.util.List;

import top.lrshuai.blog.util.ParameterMap;


public interface CacheDao {

	/**
	 * 音乐列表
	 * 
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> findMusiclist(ParameterMap pm);
	
	/**
	 * 友情链接
	 * 
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> friendlinkList(ParameterMap pm);
	
	/**
	 * 时间轴列表
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getTimeLineList();
	
	/**
	 * 笑话
	 * @param pm
	 * @return
	 */
	public ParameterMap getJokeByRandom();
	
	public ParameterMap getIntroduce();
}
