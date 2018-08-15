package top.lrshuai.blog.dao;

import java.util.List;

import top.lrshuai.blog.util.ParameterMap;


public interface LabelDao {
	/**
	 * 获取文章的所有标签
	 * 
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getArticleLabels(ParameterMap pm);
	
	
	/**
	 * 获取标签通过label_type
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getLabelsByType(ParameterMap pm);

	/**
	 * 获取单个文章的标签
	 * 
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getArticleLabelById(ParameterMap pm);

	/**
	 * 批量插入数据
	 * 
	 * @param list
	 * @return
	 */
	public int batchSaveArticleLabel(List<ParameterMap> list);
	
	/**
	 * 批量插入数据
	 * @param list
	 * @return
	 */
	public int batchSaveUserLabel(List<ParameterMap> list);

	/**
	 * 删除标签
	 * 
	 * @param pm
	 * @return
	 */
	public int delArticleLabel(ParameterMap pm);
	
	/**
	 * 删除用户标签
	 * @param pm
	 * @return
	 */
	public int delUserLabel(ParameterMap pm);

	/**
	 * 获取用户标签
	 * 
	 * @param pm
	 * @return
	 */
	public List<ParameterMap> getUserLabel(ParameterMap pm);
}
