package top.lrshuai.blog.controller;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import top.lrshuai.blog.controller.base.BaseController;
import top.lrshuai.blog.dao.PublicDao;
import top.lrshuai.blog.plugin.Page;
import top.lrshuai.blog.service.IArticleService;
import top.lrshuai.blog.service.ICacheService;
import top.lrshuai.blog.service.ILabelService;
import top.lrshuai.blog.util.HTMLUtils;
import top.lrshuai.blog.util.MyUtil;
import top.lrshuai.blog.util.ParameterMap;

@Controller
public class PageController extends BaseController{
	@Autowired
	private IArticleService articleService;
	
	@Autowired
	private ILabelService labelService;
	
	@Autowired
	private ICacheService cacheService;
	
	@Autowired
	private PublicDao publicDao;
	
	
	@GetMapping(path={"/","/index"})
	public String index(){
		log.info("comment in index ip:"+this.getIpAddr());
		return "index";
	}
	
	@GetMapping(path="/login")
	public String login(){
		log.info("comment in login ip:"+this.getIpAddr());
		return "login";
	}
	
	@GetMapping(path="/otherLogin")
	public String otherLogin(){
		log.info("comment in other login ip:"+this.getIpAddr());
		return "otherlogin";
	}
	
	@GetMapping("/include/{pageName}")
	public String includePage(@PathVariable("pageName") String pageName){
		return "include/"+pageName;
	}
	
	@GetMapping(path="/leaveword")
	public String leaveword(Model model){
		log.info("comment in leaveword ip:"+this.getIpAddr());
		ParameterMap pm = this.getParameterMap();
		List<ParameterMap> leaves = publicDao.getleaveWordByTime(pm);
		for(ParameterMap mm:leaves){
			String content = mm.getString("content");
			content = HTMLUtils.replayFace(content);
			mm.put("content", content);
		}
		ParameterMap joke = cacheService.getCacheJoke(pm);
		model.addAttribute("joke", joke);
		model.addAttribute("leaves", leaves);
		return "leaveword";
	}
	
	@GetMapping(path="/about")
	public String about(Model model){
		log.info("comming in aboutMe ip:"+this.getIpAddr());
		try {
			model.addAttribute("timelines", cacheService.getTimeLineList());
			model.addAttribute("introduces", cacheService.getCacheIntroduce());
		} catch (Exception e) {
			log.error("error",e);
			e.printStackTrace();
		}
		return "about";
	}
	
	@GetMapping(path="/music")
	public String music(Model model){
		
		return "music";
	}
	@GetMapping(path="/twocode")
	public String twocode(Model model){
		return "twocode";
	}
	
	@GetMapping(path="/share")
	public String share(Model model){
		
		return "share";
	}
	
	@GetMapping(path="/robot")
	public String rebot(Model model){
		
		return "robot/robot";
	}
	
	@GetMapping(path="/resume")
	public String resume(Model model){
		
		return "resume/index";
	}
	
	
	@GetMapping(path="/links")
	public String links(Model model){
		log.info("comming in links ip:"+this.getIpAddr());
		try {
			model.addAttribute("links", cacheService.getCacheLinks(getParameterMap()));
		} catch (Exception e) {
			log.error("error",e);
			e.printStackTrace();
		}
		return "links";
	}
	
	
	@GetMapping(path="/blog")
	public String blog(Model model){
		log.info("comming in blog ip:"+this.getIpAddr());
		ParameterMap pm = this.getParameterMap();
		List<ParameterMap> articles =null;
		List<ParameterMap> months = null;
		List<ParameterMap> labels = null;
		List<ParameterMap> recommends = null;
		List<ParameterMap> hots = null;
		List<ParameterMap> randoms = null;
		Page page = new Page();
		try {
			//按时间排序，要5条
			articles = cacheService.getCacheAllArticle(pm);
			page.setCurrentPage(1);
			page.setShowCount(5);
			page.setTotalResult(articles.size());
			articles = MyUtil.subList(articles, 1, 5);
			
			//所有的文章归档
			months = cacheService.getCacheMonthArticle(new ParameterMap());
			if(months == null)
			months=articleService.getArticleMonthNum(pm);
			
			//文章的所有标签
			labels = cacheService.getCacheLabelArticle(new ParameterMap());
			if(labels == null)
			labels=labelService.getArticleLabels(pm);
			
			//推荐文章列表
			recommends = cacheService.getCacheRecommendArticle(pm);
			if(recommends == null)
			recommends=articleService.getRecommendArticle(pm);
			recommends = MyUtil.getRandomList(recommends, 5);
			
			//热门文章列表
			hots = cacheService.getCacheHotArticle(pm);
			if(hots == null)
			hots=articleService.gethotArticle(pm);
			hots = MyUtil.getRandomList(hots, 5);
			
			//随机文章
			randoms = MyUtil.getRandomList(cacheService.getCacheAllArticle(pm),5);
		} catch (Exception e) {
			log.error("异常", e);
			e.printStackTrace();
		}
		model.addAttribute("title", "帅大叔的博客");
		model.addAttribute("articles", articles);
		model.addAttribute("months",months );
		model.addAttribute("labels", labels);
		model.addAttribute("recommends",recommends );
		model.addAttribute("hots", hots);
		model.addAttribute("randoms", randoms);
		model.addAttribute("page", new ParameterMap(page));
		model.addAttribute("page_url", "/blog/page");
		return "blog/blog";
	}
	
	@GetMapping(path="/search")
	public String search(Model model){
		ParameterMap pm = this.getParameterMap();
		String keyword = pm.getString("keyword");
		log.info("comming in search keyword="+keyword);
		List<ParameterMap> articles =null;
		List<ParameterMap> months = null;
		List<ParameterMap> labels = null;
		List<ParameterMap> recommends = null;
		List<ParameterMap> hots = null;
		List<ParameterMap> randoms = null;
		Page page = new Page();
		try {
			//按时间排序，要5条
			page.setCurrentPage(1);
			page.setShowCount(5);
			System.out.println("keyword="+keyword);
			System.out.println("p,m="+pm);
			page.setPm(pm);
			articles = articleService.getArticlelistPage(page);
			System.out.println("搜索结果articles="+articles.size());
//			if(articles == null || articles.size() < 1) {
//				System.out.println("走分词list");
//				ParameterMap mm = new ParameterMap();
//				List<String> list = new ArrayList<>();
//				list.addAll(MyUtil.setMentList(keyword));
//				System.out.println("set="+MyUtil.setMentList(keyword));
//				System.out.println("list="+list);
//				mm.put("keywords", list);
//				page.setPm(mm);
//				articles = articleService.getArticlelistPage(page);
//			}
			//所有的文章归档
			months = cacheService.getCacheMonthArticle(new ParameterMap());
			if(months == null)
				months=articleService.getArticleMonthNum(pm);
			
			//文章的所有标签
			labels = cacheService.getCacheLabelArticle(new ParameterMap());
			if(labels == null)
				labels=labelService.getArticleLabels(pm);
			
			//推荐文章列表
			recommends = cacheService.getCacheRecommendArticle(pm);
			if(recommends == null)
				recommends=articleService.getRecommendArticle(pm);
			recommends = MyUtil.getRandomList(recommends, 5);
			
			//热门文章列表
			hots = cacheService.getCacheHotArticle(pm);
			if(hots == null)
				hots=articleService.gethotArticle(pm);
			hots = MyUtil.getRandomList(hots, 5);
			
			//随机文章
			randoms = MyUtil.getRandomList(cacheService.getCacheAllArticle(pm),5);
		} catch (Exception e) {
			log.error("异常", e);
			e.printStackTrace();
		}
		model.addAttribute("title", "搜索"+keyword+"的文章");
		model.addAttribute("articles", articles);
		model.addAttribute("keyword",keyword );
		model.addAttribute("months",months );
		model.addAttribute("labels", labels);
		model.addAttribute("recommends",recommends );
		model.addAttribute("hots", hots);
		model.addAttribute("randoms", randoms);
		model.addAttribute("page", new ParameterMap(page));
		model.addAttribute("page_url", "/blog/search");
		return "blog/blog";
	}
	
	/**
	 * 获取音乐列表
	 * @return
	 */
	@GetMapping(path="/getMusicList")
	@ResponseBody
	public Object getMusicList(){
		List<ParameterMap> musiclist = null;
		try {
			musiclist = cacheService.findMusiclist(getParameterMap());
		} catch (Exception e) {
			e.printStackTrace();
			musiclist = new ArrayList<>();
		}
		return musiclist;
	}
	
}
