package top.lrshuai.blog.task;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import top.lrshuai.blog.service.ICacheService;
import top.lrshuai.blog.service.IPublicService;
import top.lrshuai.blog.util.ParameterMap;

/**
 * 定时执行器
 * 
 * @author Rs
 *
 */
@Component
public class TimeTask {

	@Autowired
	private ICacheService cacheService;
	
	@Autowired
	private IPublicService publicService;

	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * 每天凌晨1点10分刷一次
	 */
	@Scheduled(cron = "10 1 0 ? * *")
	public void homeArticle() {
		int issuccess = cacheService.cacheAllArticle(new ParameterMap());
		if (issuccess == 1) {
			log.info("定时刷新首页数据 成功");
		} else {
			log.info("定时刷新首页数据 失败");
		}
	}

	/**
	 * 每天凌晨1点刷一次
	 */
	@Scheduled(cron = "0 1 0 ? * *")
	public void monthArticle() {
		int issuccess = cacheService.cacheMonthArticle(new ParameterMap());
		if (issuccess == 1) {
			log.info("定时刷新归档数据 成功");
		} else {
			log.info("定时刷新归档数据 失败");
		}
	}

	/**
	 * 每天凌晨1点5分刷一次
	 */
	@Scheduled(cron = "5 1 0 ? * *")
	public void labelArticle() {
		int issuccess = cacheService.cacheLabelArticle(new ParameterMap());
		if (issuccess == 1) {
			log.info("定时刷新标签数据 成功");
		} else {
			log.info("定时刷新标签数据 失败");
		}
	}
	
	/**
	 * 每天凌晨1点5分刷一次
	 */
	@Scheduled(cron = "0 0 0 ? * *")
	public void cacheJoke() {
		int issuccess = cacheService.cacheJoke();
		if (issuccess == 1) {
			log.info("定时刷新笑话 成功");
		} else {
			log.info("定时刷新笑话 失败");
		}
	}

	/**
	 * 每天一个小时 保存热门搜索缓存id
	 */
	@Scheduled(cron = "0 0 0/1 ? * *")
	public void reloadArticleHotNum() {
		int issuccess = cacheService.reloadArticleHotNum();
		if (issuccess == 1) {
			log.info("定时刷新搜索数据 成功");
		} else if (issuccess == 2) {
			log.info("没有要插入的数据");
		} else {
			log.info("定时刷新搜索数据 失败");
		}
	}
	
	/**
	 * 热门 2点
	 */
	@Scheduled(cron = "0 2 0 ? * *")
	public void reloadHotArticle() {
		int issuccess = cacheService.cacheHotArticle(new ParameterMap());
		if (issuccess == 1) {
			log.info("定时刷新热门数据 成功");
		} else {
			log.info("定时刷新热门数据 失败");
		}
	}
	
	/**
	 * 推荐 每周一 0点30分
	 */
	@Scheduled(cron = "30 0 0 ? * MON")
	public void reloadRemArticle() {
		int issuccess = cacheService.cacheRecommendArticle(new ParameterMap());
		if (issuccess == 1) {
			log.info("定时刷新推荐数据 成功");
		} else {
			log.info("定时刷新推荐数据 失败");
		}
	}
	
	/**
	 * 凌晨3点
	 */
	@Scheduled(cron = "0 3 0 ? * *")
	public void saveEveryDayBrowseNum() {
		int issuccess = publicService.saveDayBrowseNum(null);
		if (issuccess == 1) {
			log.info("保存每天浏览数统计 成功");
		} else {
			log.info("保存每天浏览数统计 失败");
		}
	}
	

}
