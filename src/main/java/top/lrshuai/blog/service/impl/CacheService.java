package top.lrshuai.blog.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import top.lrshuai.blog.dao.ArticleDao;
import top.lrshuai.blog.dao.CacheDao;
import top.lrshuai.blog.dao.LabelDao;
import top.lrshuai.blog.dao.PublicDao;
import top.lrshuai.blog.plugin.Page;
import top.lrshuai.blog.service.ICacheService;
import top.lrshuai.blog.util.Const;
import top.lrshuai.blog.util.MyUtil;
import top.lrshuai.blog.util.ParameterMap;

@Service
public class CacheService implements ICacheService {

	@Autowired
	private CacheDao cacheDao;

	@Autowired
	private ArticleDao articleDao;

	@Autowired
	private LabelDao labelDao;
	
	@Autowired
	private PublicDao publicDao;

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private RedisTemplate<String, List<String>> sredis;
	
	@Autowired
	private RedisTemplate<String, List<ParameterMap>> redis;
	
	@Autowired
	private RedisTemplate<String, ParameterMap> oredis;
	
	@Override
	public int cacheAllArticle(ParameterMap pm) {
		try {
			Page page = new Page();
			page.setShowCount(Integer.MAX_VALUE);
			page.setPm(pm);
			List<ParameterMap> articleList = articleDao.getArticlelistPage(page);
			if (articleList != null && articleList.size() > 0) {
				for (ParameterMap ptm : articleList) {
					// 给文章添加标签
					List<ParameterMap> articleLabel = labelDao.getArticleLabelById(ptm);
					if (articleLabel != null && articleLabel.size() > 0) {
						ptm.put("labels", articleLabel);
					} else {
						ptm.put("labels", new ArrayList<>());
					}
				}
				redis.opsForValue().set(Const.ARTICLE_HOME, articleList);
				log.info("cache article-home is success");
			} else {
				redis.opsForValue().set(Const.ARTICLE_HOME, new ArrayList<>());
				log.info("article-home is null or article-home this size is zero");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("cache article home is failed:" + e.getMessage(), e);
			return 0;
		}
		return 1;
	}

	@Override
	public int cacheHotArticle(ParameterMap pm) {
		try {
			List<ParameterMap> hotList = articleDao.getHotArticle(pm);
			if (hotList != null && hotList.size() > 0) {
				redis.opsForValue().set(Const.ARTICLE_HOT, hotList);
				log.info("cache hot article success");
			} else {
				redis.opsForValue().set(Const.ARTICLE_HOT, new ArrayList<>());
				log.info("cache hot article success but hot Article is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("cache hot_article is failed:" + e.getMessage(), e);
			return 0;
		}
		return 1;
	}

	@Override
	public int cacheMonthArticle(ParameterMap pm) {
		try {
			// 缓存归档列表
			List<ParameterMap> articleMonthNum = articleDao.getArticleMonthNum(pm);
			if (articleMonthNum != null && articleMonthNum.size() > 0) {
				redis.opsForValue().set(Const.ARTICLE_MONTH, articleMonthNum);
				log.info("cache month is success");
			} else {
				redis.opsForValue().set(Const.ARTICLE_MONTH, new ArrayList<>());
				return 0;
			}
			String articleMonth = pm.getString("article_month");
			// 缓存所有归档日期下的列表
			if (null == articleMonth || "".equals(articleMonth) || "all".equals(articleMonth)) {
				for (ParameterMap ptMap : articleMonthNum) {
					Page page = new Page();
					page.setShowCount(Integer.MAX_VALUE);
					page.setPm(ptMap);
					List<ParameterMap> articleList = articleDao.getArticlelistPage(page);
					if (articleList != null && articleList.size() > 0) {
						for (ParameterMap ptm : articleList) {
							// 给文章添加标签
							List<ParameterMap> articleLabel = labelDao.getArticleLabelById(ptm);
							if (articleLabel != null && articleLabel.size() > 0) {
								ptm.put("labels", articleLabel);
							} else {
								ptm.put("labels", new ArrayList<>());
							}
						}
						String month = ptMap.getString("article_month");
						// 缓存归档日期下的文章列表
						redis.opsForValue().set(Const.ARTICLE_MONTH_ + month, articleList);
						log.info("cache month =" + month + " success");
					}
				}
			} else {
				// 只缓存特定的归档日期
				Page page = new Page();
				page.setShowCount(Integer.MAX_VALUE);
				page.setPm(pm);
				List<ParameterMap> articleList = articleDao.getArticlelistPage(page);
				// 缓存归档日期下的文章列表
				redis.opsForValue().set(Const.ARTICLE_MONTH_ + articleMonth, articleList);
				log.info("cache month =" + articleMonth + " success");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("cache month is err:" + e.getMessage(), e);
			return 0;
		}
		return 1;
	}

	@Override
	public int cacheLabelArticle(ParameterMap pm) {
		try {
			// 所有文章的标签
			List<ParameterMap> articleLabels = labelDao.getArticleLabels(pm);
			if (articleLabels != null && articleLabels.size() > 0) {
				redis.opsForValue().set(Const.ARTICLE_LABEL, articleLabels);
				log.info("cache labels is success");
			} else {
				return 0;
			}
			// 缓存标签下的文章列表
			String articleLabelId = pm.getString("label_id");
			if (null == articleLabelId || "".equals(articleLabelId) || "all".equals(articleLabelId)) {
				for (ParameterMap ptMap : articleLabels) {
					List<ParameterMap> list = articleDao.getArticleIdsByLabelId(ptMap);
					String labelId = ptMap.getString("label_id");
					if (list != null && list.size() > 0) {
						List<ParameterMap> articleList = articleDao.getLabelArticleList(list);
						if (articleList != null && articleList.size() > 0) {
							for (ParameterMap ptm : articleList) {
								// 给文章添加标签
								List<ParameterMap> articleLabel = labelDao.getArticleLabelById(ptm);
								if (articleLabel != null && articleLabel.size() > 0) {
									ptm.put("labels", articleLabel);
								} else {
									ptm.put("labels", new ArrayList<>());
								}
							}
							// 缓存归档日期下的文章列表
							redis.opsForValue().set(Const.ARTICLE_LABEL_ + labelId, articleList);
							log.info("cache label =" + ptMap.getString("label_name") + " success");
						} else {
							redis.opsForValue().set(Const.ARTICLE_LABEL_ + labelId,new ArrayList<>());
							log.info("cache label = " + ptMap.getString("label_name")
									+ " success but this is null Array");
						}
					} else {
						redis.opsForValue().set(Const.ARTICLE_LABEL_ + labelId,new ArrayList<>());
						log.info("cache label = " + ptMap.getString("label_name") + " success but this is null Array");
					}
				}
			} else {
				List<ParameterMap> list = articleDao.getArticleIdsByLabelId(pm);
				List<ParameterMap> articleList = articleDao.getLabelArticleList(list);
				if (articleList != null && articleList.size() > 0) {
					for (ParameterMap ptm : articleList) {
						// 给文章添加标签
						List<ParameterMap> articleLabel = labelDao.getArticleLabelById(ptm);
						if (articleLabel != null && articleLabel.size() > 0) {
							ptm.put("labels", articleLabel);
						} else {
							ptm.put("labels", new ArrayList<>());
						}
					}
					// 缓存归档日期下的文章列表
					redis.opsForValue().set(Const.ARTICLE_LABEL_ + articleLabelId, articleList);
					log.info("cache label_id =" + articleLabelId + " success");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("cache label is failed:" + e.getMessage(), e);
			return 0;
		}
		return 1;
	}

	/**
	 * 保存在缓存中的热门搜索ids
	 * 
	 * @param pm
	 * @return
	 */
	@Override
	public int reloadArticleHotNum() {
		try {
			List<String> ids = sredis.opsForValue().get(Const.HOT_ARTICLE_ID_LIST);
			if (ids != null && ids.size() > 0) {
				articleDao.updateArticleHotNum(ids);
				// 重置
				redis.opsForValue().set(Const.HOT_ARTICLE_ID_LIST, new ArrayList<>());
			} else {
				return 2;
			}
		} catch (Exception e) {
			log.error("刷新 热值错误", e);
			return 0;
		}
		return 1;
	}

	@Override
	public int cacheRecommendArticle(ParameterMap pm) {
		try {
			List<ParameterMap> recommendList = articleDao.getRecommendArticle(pm);
			if (recommendList != null && recommendList.size() > 0) {
				redis.opsForValue().set(Const.ARTICLE_RECOMMEND, recommendList);
				log.info("cache recommend article success");
			} else {
				redis.opsForValue().set(Const.ARTICLE_RECOMMEND, new ArrayList<ParameterMap>());
				log.info("cache recommend article success but recommend Article is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("cache recommend is failed:" + e.getMessage(), e);
			return 0;
		}
		return 1;
	}

	@Override
	public int cacheFriendLink() {
		try {
			List<ParameterMap> links = cacheDao.friendlinkList(new ParameterMap());
			if (links != null && links.size() > 0) {
				redis.opsForValue().set(Const.BLOG_FRIEND_LINK, links);
				log.info("cache friend links success");
			} else {
				redis.opsForValue().set(Const.BLOG_FRIEND_LINK, new ArrayList<>());
				log.info("cache friend links success but links  is null");
			}
		} catch (Exception e) {
			e.getMessage();
			log.error("cache link error", e);
			return 0;
		}
		return 1;
	}



	/**
	 * 获取所有文章列表
	 */
	@Override
	public List<ParameterMap> getCacheAllArticle(ParameterMap pm) throws Exception {
		List<ParameterMap> atcs =   redis.opsForValue().get(Const.ARTICLE_HOME);
		if (atcs != null )
			return atcs;
		return new ArrayList<>();
	}

	/**
	 * 参数为空获取文章所有归档列表，参数有month,则返回month下的文章
	 */
	@Override
	public List<ParameterMap> getCacheMonthArticle(ParameterMap pm) throws Exception {
		String month = pm.getString("article_month");
		List<ParameterMap> monthatcs = null;
		if (MyUtil.isEmpty(month) || "all".equals(month)) {
			monthatcs = redis.opsForValue().get(Const.ARTICLE_MONTH);
		} else {
			monthatcs = redis.opsForValue().get(Const.ARTICLE_MONTH_ + month);
		}
		if (monthatcs != null)
			return monthatcs;
		return new ArrayList<>();
	}

	/**
	 * 参数为空获取文章所有标签，参数有labelid,则返回labelid下的文章
	 */
	@Override
	public List<ParameterMap> getCacheLabelArticle(ParameterMap pm) throws Exception {
		List<ParameterMap> labelatcs = null;
		try {
			String labelId = pm.getString("label_id");
			if ("".equals(labelId) || "all".equals(labelId)) {
				labelatcs = redis.opsForValue().get(Const.ARTICLE_LABEL);
			} else {
				labelatcs = redis.opsForValue().get((Const.ARTICLE_LABEL_ + labelId));
			}
			if (labelatcs != null) {
				return labelatcs;
			}
		} catch (Exception e) {
			System.out.println("异常里面的！！！！！！！！！！！！！！！！！！");
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public List<ParameterMap> getCacheRecommendArticle(ParameterMap pm) throws Exception {
		return redis.opsForValue().get(Const.ARTICLE_RECOMMEND);
	}

	@Override
	public List<ParameterMap> getCacheHotArticle(ParameterMap pm) throws Exception {
		return  redis.opsForValue().get(Const.ARTICLE_HOT);
	}

	@Override
	public List<ParameterMap> getCacheLinks(ParameterMap pm) throws Exception {
		return  redis.opsForValue().get(Const.BLOG_FRIEND_LINK);
	}
	
	@Override
	public int cacheMusicList(ParameterMap pm) {
		try {
			List<ParameterMap> list = cacheDao.findMusiclist(pm);
			if (list != null && list.size() > 0) {
				redis.opsForValue().set(Const.MUSIC_LIST, list);
			} else {
				redis.opsForValue().set(Const.MUSIC_LIST, new ArrayList<>());
			}
		} catch (Exception e) {
			e.getMessage();
			log.info("音乐缓存失败:" + e.getMessage(), e);
			return 0;
		}
		return 1;
	}

	@Override
	public List<ParameterMap> findMusiclist(ParameterMap pm) throws Exception {
		return redis.opsForValue().get(Const.MUSIC_LIST);
	}
	
	@Override
	public int cacheJoke() {
		ParameterMap joke = cacheDao.getJokeByRandom();
		if(joke != null){
			oredis.opsForValue().set(Const.EVERYDAY_JOKE, joke);
		}
		return 1;
	}
	
	@Override
	public ParameterMap getCacheJoke(ParameterMap pm) {
		return oredis.opsForValue().get(Const.EVERYDAY_JOKE);
	}

	@Override
	public int cacheTimeLineList() {
		List<ParameterMap> lists = cacheDao.getTimeLineList();
		if(lists != null) {
			redis.opsForValue().set(Const.TIME_LINE_LIST, lists);
		}else {
			redis.opsForValue().set(Const.TIME_LINE_LIST, new ArrayList<>());
		}
		return 1;
	}
	
	@Override
	public List<ParameterMap> getTimeLineList() throws Exception {
		return redis.opsForValue().get(Const.TIME_LINE_LIST);
	}
	@Override
	public int cacheIntroduce() {
		ParameterMap introduce = cacheDao.getIntroduce();
		if(introduce != null){
			oredis.opsForValue().set(Const.INTRODUCE, introduce);
		}
		return 1;
	}
	
	@Override
	public ParameterMap getCacheIntroduce() {
		return oredis.opsForValue().get(Const.INTRODUCE);
	}
	
	@Override
	public int cacheBlackIP() {
		List<ParameterMap> blackipList = publicDao.getBlackIpList();
		if(blackipList == null)blackipList = new ArrayList<>();
		redis.opsForValue().set(Const.BLACK_IP_LIST, blackipList);
		return 1;
	}
	@Override
	public List<ParameterMap> getBlackIpList() throws Exception {
		return redis.opsForValue().get(Const.BLACK_IP_LIST);
	}
	
	@Override
	public int cacheDayBrowseNumList() {
		List<ParameterMap> dayBrowseList = publicDao.getDayBrowseList();
		if(dayBrowseList == null)dayBrowseList = new ArrayList<>();
		redis.opsForValue().set(Const.DAY_BROWSE_LIST, dayBrowseList);
		return 1;
	}
	
	@Override
	public List<ParameterMap> getEveryDayBrowseNumList() throws Exception {
		return redis.opsForValue().get(Const.DAY_BROWSE_LIST);
	}
}
