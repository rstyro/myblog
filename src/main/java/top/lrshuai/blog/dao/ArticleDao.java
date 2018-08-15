package top.lrshuai.blog.dao;

import java.util.List;

import top.lrshuai.blog.plugin.Page;
import top.lrshuai.blog.util.ParameterMap;


public interface ArticleDao {

	/**
	 * 获取文章归档
	 * 
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getArticleMonthNum(ParameterMap pm);

	/**
	 * 获取文章列表
	 * 
	 * @param page
	 * @return
	 */
	public List<ParameterMap> getArticlelistPage(Page page);

	/**
	 * 获取文章详情
	 * 
	 * @param pm
	 * @return
	 */
	public ParameterMap getArticleDetail(ParameterMap pm);

	/**
	 * 保存文章
	 * 
	 * @param pm
	 * @return
	 */
	public int saveArticle(ParameterMap pm);

	/**
	 * 更新文章
	 * 
	 * @param pm
	 * @return
	 */
	public int updateArticle(ParameterMap pm);

	/**
	 * 更新文章的hot_num
	 * 
	 * @param pm
	 * @return
	 */
	public int updateArticleHotNum(List<String> list);

	/**
	 * 删除文章
	 * 
	 * @param pm
	 * @return
	 */
	public int delArticle(ParameterMap pm);
	
	/**
	 * 删除文章的标签
	 * @param pm
	 * @return
	 */
	public int delArticleLabel(ParameterMap pm);

	/**
	 * 获取文章id列表
	 * 
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getArticleIdsByLabelId(ParameterMap pm);

	/**
	 * 获取标签文章列表
	 * 
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getLabelArticleList(List<ParameterMap> list);

	/**
	 * 获取推荐文章列表
	 * 
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getRecommendArticle(ParameterMap pm);

	/**
	 * 热门文章列表
	 * 
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getHotArticle(ParameterMap pm);
}
