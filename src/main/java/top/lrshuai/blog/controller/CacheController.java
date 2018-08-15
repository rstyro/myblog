package top.lrshuai.blog.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import top.lrshuai.blog.controller.base.BaseController;
import top.lrshuai.blog.service.ICacheService;
import top.lrshuai.blog.util.ParameterMap;

/**
 * 更新刷新控制层
 * 
 * @author Rs
 *
 */
@RequestMapping(value = "/reload")
@RestController
public class CacheController extends BaseController {
	@Autowired
	private ICacheService cacheService;
	
	/**
	 * 刷新首页文章刷新
	 * 
	 * @return
	 * @throws Exception
	 */
	@GetMapping(path="/home_atc")
	public Object cacheArticle() throws Exception {
		log.info("刷新首页文章刷新");
		Map<String, Object> map = new HashMap<>();
		ParameterMap pm = this.getParameterMap();
		int issuccess = cacheService.cacheAllArticle(pm);
		if (issuccess == 1) {
			map.put("msg", "刷新成功");
			map.put("status", "success");
		} else {
			map.put("msg", "刷新失败");
			map.put("status", "failed");
		}
		return map;
	}
	
	/**
	 * 刷新归档文章刷新
	 * 
	 * @return
	 * @throws Exception
	 * @param article_month
	 */
	@RequestMapping(value = "/month_atc", method = RequestMethod.GET)
	@ResponseBody
	public Object cacheMonthArticle() throws Exception {
		log.info("刷新归档文章刷新");
		Map<String, Object> map = new HashMap<>();
		ParameterMap pm = this.getParameterMap();
		int issuccess = cacheService.cacheMonthArticle(pm);
		if (issuccess == 1) {
			map.put("msg", "刷新成功");
			map.put("status", "success");
		} else {
			map.put("msg", "刷新失败");
			map.put("status", "failed");
		}
		return map;
	}

	/**
	 * 刷新标签文章刷新
	 * 
	 * @return
	 * @throws Exception
	 * @param label_id
	 */
	@RequestMapping(value = "/label_atc", method = RequestMethod.GET)
	@ResponseBody
	public Object cacheLabelArticle() throws Exception {
		log.info("刷新标签文章刷新");
		Map<String, Object> map = new HashMap<>();
		ParameterMap pm = this.getParameterMap();
		int issuccess = cacheService.cacheLabelArticle(pm);
		if (issuccess == 1) {
			map.put("msg", "刷新成功");
			map.put("status", "success");
		} else {
			map.put("msg", "刷新失败");
			map.put("status", "failed");
		}
		return map;
	}

	/**
	 * 刷新推荐文章刷新
	 * 
	 * @return
	 * @throws Exception
	 * @param label_id
	 */
	@RequestMapping(value = "/recommend_atc", method = RequestMethod.GET)
	@ResponseBody
	public Object cacheRecommendArticle() throws Exception {
		log.info("刷新推荐文章刷新");
		Map<String, Object> map = new HashMap<>();
		ParameterMap pm = this.getParameterMap();
		int issuccess = cacheService.cacheRecommendArticle(pm);
		if (issuccess == 1) {
			map.put("msg", "刷新成功");
			map.put("status", "success");
		} else {
			map.put("msg", "刷新失败");
			map.put("status", "failed");
		}
		return map;
	}

	/**
	 * 刷新热门文章刷新
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/hot_atc", method = RequestMethod.GET)
	@ResponseBody
	public Object cacheHotArticle() throws Exception {
		log.info("刷新热门文章刷新");
		Map<String, Object> map = new HashMap<>();
		ParameterMap pm = this.getParameterMap();
		int issuccess = cacheService.cacheHotArticle(pm);
		if (issuccess == 1) {
			map.put("msg", "刷新成功");
			map.put("status", "success");
		} else {
			map.put("msg", "刷新失败");
			map.put("status", "failed");
		}
		return map;
	}
	
	/**
	 * 刷新搜索热值
	 * 
	 * @return
	 * @throws Exception
	 */
	@GetMapping(path="/hot_num")
	public Object reloadArticleHotNum() throws Exception {
		log.info("刷新搜索热值");
		Map<String, Object> map = new HashMap<>();
		int issuccess = cacheService.reloadArticleHotNum();
		if (issuccess == 1) {
			map.put("msg", "刷新成功");
			map.put("status", "success");
		} else {
			map.put("msg", "刷新失败");
			map.put("status", "failed");
		}
		return map;
	}
	
	/**
	 * 刷新音乐刷新
	 * 
	 * @return
	 * @throws Exception
	 * @param label_id
	 */
	@RequestMapping(value = "/music", method = RequestMethod.GET)
	@ResponseBody
	public Object cacheMusic() throws Exception {
		log.info("刷新音乐刷新");
		Map<String, Object> map = new HashMap<>();
		ParameterMap pm = this.getParameterMap();
		int issuccess = cacheService.cacheMusicList(pm);
		if (issuccess == 1) {
			map.put("msg", "刷新成功");
			map.put("status", "success");
		} else {
			map.put("msg", "刷新失败");
			map.put("status", "failed");
		}
		return map;
	}
	
	/**
	 * 刷新笑话
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/joke", method = RequestMethod.GET)
	@ResponseBody
	public Object cacheJoke() throws Exception {
		log.info("刷新句子刷新");
		Map<String, Object> map = new HashMap<>();
		int issuccess = cacheService.cacheJoke();
		if (issuccess == 1) {
			map.put("msg", "刷新成功");
			map.put("status", "success");
		} else {
			map.put("msg", "刷新失败");
			map.put("status", "failed");
		}
		return map;
	}
	
	@RequestMapping(value = "/timeline", method = RequestMethod.GET)
	@ResponseBody
	public Object timeline() throws Exception {
		log.info("刷新时间轴刷新");
		Map<String, Object> map = new HashMap<>();
		int issuccess = cacheService.cacheTimeLineList();
		if (issuccess == 1) {
			map.put("msg", "刷新成功");
			map.put("status", "success");
		} else {
			map.put("msg", "刷新失败");
			map.put("status", "failed");
		}
		return map;
	}
	
	@RequestMapping(value = "/introduce", method = RequestMethod.GET)
	@ResponseBody
	public Object introduce() throws Exception {
		log.info("刷新介绍刷新");
		Map<String, Object> map = new HashMap<>();
		int issuccess = cacheService.cacheIntroduce();
		if (issuccess == 1) {
			map.put("msg", "刷新成功");
			map.put("status", "success");
		} else {
			map.put("msg", "刷新失败");
			map.put("status", "failed");
		}
		return map;
	}
	
	/**
	 * 刷新友链
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/friendlinks", method = RequestMethod.GET)
	@ResponseBody
	public Object links() throws Exception {
		log.info("刷新介绍刷新");
		Map<String, Object> map = new HashMap<>();
		int issuccess = cacheService.cacheFriendLink();
		if (issuccess == 1) {
			map.put("msg", "刷新成功");
			map.put("status", "success");
		} else {
			map.put("msg", "刷新失败");
			map.put("status", "failed");
		}
		return map;
	}
	
	/**
	 * 刷新黑名单
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/blackList", method = RequestMethod.GET)
	@ResponseBody
	public Object blackList() throws Exception {
		log.info("刷新黑名单");
		Map<String, Object> map = new HashMap<>();
		int issuccess = cacheService.cacheBlackIP();
		if (issuccess == 1) {
			map.put("msg", "刷新成功");
			map.put("status", "success");
		} else {
			map.put("msg", "刷新失败");
			map.put("status", "failed");
		}
		return map;
	}
}
