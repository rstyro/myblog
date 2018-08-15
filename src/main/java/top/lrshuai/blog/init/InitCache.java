package top.lrshuai.blog.init;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import top.lrshuai.blog.service.ICacheService;
import top.lrshuai.blog.util.ParameterMap;

@Component
public class InitCache /*implements CommandLineRunner*/{
	
	@Autowired
	private ICacheService cacheService;

	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 初始化方法
	 */
	@PostConstruct
	public void init() {
		try {
			ParameterMap pm = new ParameterMap();
			cacheService.cacheAllArticle(pm);
			cacheService.cacheLabelArticle(pm);
			cacheService.cacheMonthArticle(pm);
			cacheService.cacheRecommendArticle(pm);
			cacheService.cacheHotArticle(pm);
			cacheService.cacheMusicList(pm);
			cacheService.cacheJoke();
			cacheService.cacheTimeLineList();
			cacheService.cacheIntroduce();
			cacheService.cacheFriendLink();
			cacheService.cacheBlackIP();
			
			log.info("初始化缓存成功");
		} catch (Exception e) {
			log.error("初始化缓存出错,可能redis服务没开启", e);
		}
	}

}
