package top.lrshuai.blog.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.lrshuai.blog.dao.ArticleDao;
import top.lrshuai.blog.dao.LabelDao;
import top.lrshuai.blog.plugin.Page;
import top.lrshuai.blog.service.IArticleService;
import top.lrshuai.blog.util.Const;
import top.lrshuai.blog.util.DateUtil;
import top.lrshuai.blog.util.ParameterMap;
import top.lrshuai.blog.util.Tools;

@Service
public class ArticleService implements IArticleService {

	@Autowired
	private ArticleDao articleDao;

	@Autowired
	private LabelDao labelDao;
	
	@Autowired
	private RedisTemplate<String, List<String>> sredis;
	
	@Override
	public List<ParameterMap> getArticleMonthNum(ParameterMap pm) throws Exception {
		// TODO Auto-generated method stub
		return articleDao.getArticleMonthNum(pm);
	}

	@Override
	public List<ParameterMap> getArticlelistPage(Page page) throws Exception {
		List<ParameterMap> articleList = articleDao.getArticlelistPage(page);
		//搜索热值
		if (Tools.notEmpty(page.getPm().getString("keyword"))) {
			if (articleList.size() > 0) {
				List<String> cacheArticleIds = sredis.opsForValue().get(Const.HOT_ARTICLE_ID_LIST);
				if (cacheArticleIds == null) {
					cacheArticleIds = new ArrayList<>();
				}
				for (ParameterMap spm : articleList) {
					cacheArticleIds.add(spm.getString("article_id"));
				}
				System.out.println("设置搜索 cacheArticleIds="+cacheArticleIds);
				sredis.opsForValue().set(Const.HOT_ARTICLE_ID_LIST, cacheArticleIds);
			}
		}
		for (ParameterMap ptm : articleList) {
			// 给文章添加标签
			List<ParameterMap> articleLabel = labelDao.getArticleLabelById(ptm);
			if (articleLabel != null && articleLabel.size() > 0) {
				ptm.put("labels", articleLabel);
			}else{
				ptm.put("labels", new ArrayList<>());
			}
		}
		return articleList;
	}

	@Override
	public ParameterMap getArticleDetail(ParameterMap pm) throws Exception {
		ParameterMap article = articleDao.getArticleDetail(pm);
		if (article == null || article.size() < 1) {
			return null;
		}
		// 给文章添加标签
		List<ParameterMap> articleLabel = labelDao.getArticleLabelById(article);
		if (articleLabel != null && articleLabel.size() > 0) {
			article.put("labels", articleLabel);
		}else {
			article.put("labels", new ArrayList<>());
		}
		return article;
	}

	@Override
	public ParameterMap saveArticle(ParameterMap pm,HttpSession session) throws Exception {
		String token = pm.getString("token");
		String sessionToken = (String) session.getAttribute(Const.SESSION_TOKEN);
		if(token.equals(sessionToken)){
			pm.put("title", "表单重复提交");
			pm.put("msg", "failed");
			return pm;
		}
		pm.put("create_time", DateUtil.getTime());
		articleDao.saveArticle(pm);
		//文章标签
		String labelidStirng = pm.getString("labels");
		if (Tools.notEmpty(labelidStirng)) {
			String[] labelIds = pm.getString("labels").split(",");
			if (labelIds.length > 0) {
				List<ParameterMap> articlelabelList = new ArrayList<>();
				String articleId = pm.getString("id");
				for (int i = 0; i < labelIds.length; i++) {
					String labelId = labelIds[i];
					ParameterMap parameData = new ParameterMap();
					parameData.put("article_id", articleId);
					parameData.put("label_id", labelId);
					articlelabelList.add(parameData);
				}
				if (articlelabelList.size() > 0) {
					labelDao.batchSaveArticleLabel(articlelabelList);
				}
			}
		}
		session.setAttribute(Const.SESSION_TOKEN, token);
		pm.put("msg", "success");
		return pm;
	}
	

	@Override
	public ParameterMap updateArticle(ParameterMap pm,HttpSession session) throws Exception {
		String token = pm.getString("token");
		String sessionToken = (String) session.getAttribute(Const.SESSION_TOKEN);
		if(token.equals(sessionToken)){
			pm.put("title", "表单重复提交");
			pm.put("msg", "failed");
			return pm;
		}
		System.out.println("pm="+pm);
		articleDao.updateArticle(pm);
		String articleId = pm.getString("article_id");
		String labelidStirng = pm.getString("labels");
		if (Tools.notEmpty(labelidStirng)) {
			String[] labelIds = pm.getString("labels").split(",");
			if (labelIds.length > 0) {
				List<ParameterMap> articlelabelList = new ArrayList<>();
				System.out.println("id===" + articleId);
				for (int i = 0; i < labelIds.length; i++) {
					String labelId = labelIds[i];
					ParameterMap parameData = new ParameterMap();
					parameData.put("article_id", articleId);
					parameData.put("label_id", labelId);
					articlelabelList.add(parameData);
				}
				if (articlelabelList.size() > 0) {
					labelDao.delArticleLabel(pm);
					labelDao.batchSaveArticleLabel(articlelabelList);
				}
			}
		}
		pm.put("id", articleId);
		pm.put("msg", "success");
		return pm;
	}

	@Override
	public List<ParameterMap> getLabelArticleListPage(ParameterMap pm) throws Exception {
		List<ParameterMap> list = articleDao.getArticleIdsByLabelId(pm);
		if (list.size() > 0) {
			List<ParameterMap> articleList = articleDao.getLabelArticleList(list);
			for (ParameterMap ptm : articleList) {
				// 给文章添加标签
				List<ParameterMap> articleLabel = labelDao.getArticleLabelById(ptm);
				if (articleLabel != null && articleLabel.size() > 0) {
					ptm.put("labels", articleLabel);
				}
			}
			return articleList;
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public List<ParameterMap> getRecommendArticle(ParameterMap pm) throws Exception {
		// TODO Auto-generated method stub
		return articleDao.getRecommendArticle(pm);
	}

	@Override
	public List<ParameterMap> gethotArticle(ParameterMap pm) throws Exception {
		// TODO Auto-generated method stub
		return articleDao.getHotArticle(pm);
	}

	@Override
	public int delArticle(ParameterMap pm) throws Exception {
		return articleDao.delArticle(pm);
	}
}
