package top.lrshuai.blog.service;

import java.util.List;

import top.lrshuai.blog.util.ParameterMap;

public interface ICacheService {
	/**
	 * 首页的文章缓存
	 * 
	 * @param pm
	 * @return
	 * @throws Exception
	 */
	public int cacheAllArticle(ParameterMap pm);

	/**
	 * 归档文章缓存
	 * 
	 * @param pm
	 * @return
	 * @throws Exception
	 */
	public int cacheMonthArticle(ParameterMap pm);

	/**
	 * 标签文章缓存
	 * 
	 * @param pm
	 * @return
	 * @throws Exception
	 */
	public int cacheLabelArticle(ParameterMap pm);

	/**
	 * 文章推荐缓存
	 * 
	 * @param pm
	 * @return
	 * @throws Exception
	 */
	public int cacheRecommendArticle(ParameterMap pm);

	/**
	 * 缓存热门文章
	 * 
	 * @param pm
	 */
	public int cacheHotArticle(ParameterMap pm);

	/**
	 * 保存缓存中的热门ids
	 * 
	 * @param pm
	 * @return
	 */
	public int reloadArticleHotNum();

	/**
	 * 缓存友链
	 * 
	 * @return
	 */
	public int cacheFriendLink();
	
	/**
	 * 缓存笑话
	 * @return
	 */
	public int cacheJoke();
	
	
	public int cacheTimeLineList();
	
	public int cacheIntroduce();
	
	public int cacheBlackIP();

	
	/**
	 * 获取首页的文章缓存
	 * 
	 * @param pm
	 * @return
	 * @throws Exception
	 */
	public List<ParameterMap> getCacheAllArticle(ParameterMap pm) throws Exception;

	/**
	 * 获取归档文章缓存
	 * 
	 * @param pm
	 * @return
	 * @throws Exception
	 */
	public List<ParameterMap> getCacheMonthArticle(ParameterMap pm) throws Exception;

	/**
	 * 获取标签文章缓存
	 * 
	 * @param pm
	 * @return
	 * @throws Exception
	 */
	public List<ParameterMap> getCacheLabelArticle(ParameterMap pm) throws Exception;

	/**
	 * 获取文章推荐缓存
	 * 
	 * @param pm
	 * @return
	 * @throws Exception
	 */
	public List<ParameterMap> getCacheRecommendArticle(ParameterMap pm) throws Exception;

	/**
	 * 获取文章热门缓存
	 * 
	 * @param pm
	 * @return
	 * @throws Exception
	 */
	public List<ParameterMap> getCacheHotArticle(ParameterMap pm) throws Exception;

	/**
	 * 获取友情链接缓存
	 * 
	 * @param pm
	 * @return
	 * @throws Exception
	 */
	public List<ParameterMap> getCacheLinks(ParameterMap pm) throws Exception;
	
	/**
	 * 获取缓存笑话
	 * @param pm
	 * @return
	 */
	public ParameterMap getCacheJoke(ParameterMap pm);
	
	public ParameterMap getCacheIntroduce();
	
	/**
	 * 缓存音乐列表
	 * 
	 * @param pm
	 */
	public int cacheMusicList(ParameterMap pm);
	
	/**
	 * 缓存每日的统计列表
	 * @return
	 */
	public int cacheDayBrowseNumList();
	public List<ParameterMap> getEveryDayBrowseNumList() throws Exception;
	
	/**
	 * 获取音乐列表
	 * 
	 * @param pm
	 * @return
	 * @throws Exception
	 */
	public List<ParameterMap> findMusiclist(ParameterMap pm) throws Exception;
	
	/**
	 * 时间轴列表
	 * @param pm
	 * @return
	 * @throws Exception
	 */
	public List<ParameterMap> getTimeLineList() throws Exception;
	
	
	public List<ParameterMap> getBlackIpList() throws Exception;
	
}
