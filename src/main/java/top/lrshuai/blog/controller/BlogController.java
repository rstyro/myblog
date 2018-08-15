package top.lrshuai.blog.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import top.lrshuai.blog.controller.base.BaseController;
import top.lrshuai.blog.plugin.Page;
import top.lrshuai.blog.service.IArticleService;
import top.lrshuai.blog.service.ICacheService;
import top.lrshuai.blog.service.ILabelService;
import top.lrshuai.blog.util.Const;
import top.lrshuai.blog.util.MyUtil;
import top.lrshuai.blog.util.ParameterMap;
import top.lrshuai.blog.util.Tools;

@Controller
@RequestMapping("/blog")
public class BlogController extends BaseController{
	
	@Autowired
	private IArticleService articleService;
	
	@Autowired
	private ILabelService labelService;
	
	@Autowired
	private ICacheService cacheService;
	
	@GetMapping(path="/label/{id}")
	public String labelBlog(Model model,@PathVariable("id") String labelId){
		ParameterMap pm = this.getParameterMap();
		List<ParameterMap> articles =null;
		List<ParameterMap> months = null;
		List<ParameterMap> labels = null;
		List<ParameterMap> recommends = null;
		List<ParameterMap> hots = null;
		List<ParameterMap> randoms = null;
		String title="";
		Page page = new Page();
		try {
			//按时间排序，要5条
			pm.put("label_id", labelId);
			articles = cacheService.getCacheLabelArticle(pm);
			page.setCurrentPage(1);
			page.setShowCount(5);
			page.setTotalResult(articles.size());
			articles = MyUtil.subList(articles, 1, 5);
			
			//所有的文章归档
			months = cacheService.getCacheMonthArticle(pm);
			if(months == null)
			months=articleService.getArticleMonthNum(pm);
			
			//文章的所有标签
			pm.remove("label_id");
			labels = cacheService.getCacheLabelArticle(pm);
			
			
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
			//换标题
			for(ParameterMap lab:labels){
				if(lab.getString("label_id").equals(labelId)) {
					title = lab.getString("label_name")+"标签的文章";
					break;
				}
			}
			//设置最后浏览的大分类
			this.getSession().setAttribute(Const.USER_LAST_ARTICLE_LIST, Const.ARTICLE_LABEL_ + labelId);
		} catch (Exception e) {
			log.error("异常", e);
			e.printStackTrace();
		}
		model.addAttribute("title", title);
		model.addAttribute("articles", articles);
		model.addAttribute("months",months );
		model.addAttribute("labels", labels);
		model.addAttribute("recommends",recommends );
		model.addAttribute("hots", hots);
		model.addAttribute("randoms", randoms);
		model.addAttribute("page", new ParameterMap(page));
		model.addAttribute("page_url", "/blog/label/"+labelId);
		return "blog/blog";
	}
	
	@GetMapping(path="/month/{month}")
	public String monthblog(Model model,@PathVariable("month") String month){
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
			pm.put("article_month", month);
			articles = cacheService.getCacheMonthArticle(pm);
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
			
			//设置最后浏览的大分类
			this.getSession().setAttribute(Const.USER_LAST_ARTICLE_LIST, Const.ARTICLE_MONTH_ + month);
		} catch (Exception e) {
			log.error("异常", e);
			e.printStackTrace();
		}
		model.addAttribute("title", month+"的文章");
		model.addAttribute("articles", articles);
		model.addAttribute("months",months );
		model.addAttribute("labels", labels);
		model.addAttribute("recommends",recommends );
		model.addAttribute("hots", hots);
		model.addAttribute("randoms", randoms);
		model.addAttribute("page", new ParameterMap(page));
		model.addAttribute("page_url", "/blog/month/"+month);
		return "blog/blog";
	}
	
	/**
	 * 分页获取首页文章
	 * @param pageNo
	 * @return
	 * @throws Exception
	 */
	@GetMapping(path="/page/{pageNo}")
	@ResponseBody
	public Object gethomePage(@PathVariable("pageNo") String pageNo) throws Exception{
		System.out.println("分页获取博客分页");
		Map<String, Object> data = new HashMap<>();
		int currentPage = Integer.parseInt(pageNo);
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setShowCount(5);
		List<ParameterMap> articles = cacheService.getCacheAllArticle(this.getParameterMap());
		if(articles != null){
			page.setTotalResult(articles.size());
		}
		ParameterMap pmPage = new ParameterMap(page);
		data.put("page", pmPage);
		data.put("data", MyUtil.subList(articles, currentPage, page.getShowCount()));
		data.put("page_url", "/blog/page");
		return data;
	}
	
	/**
	 * 分页获取标签文章
	 * @param id
	 * @param pageNo
	 * @return
	 * @throws Exception
	 */
	@GetMapping(path="/label/{id}/{pageNo}")
	@ResponseBody
	public Object getLabelPage(@PathVariable("id") String id,@PathVariable("pageNo") String pageNo) throws Exception{
		Map<String, Object> data = new HashMap<>();
		int currentPage = Integer.parseInt(pageNo);
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setShowCount(5);
		ParameterMap pm = this.getParameterMap();
		pm.put("label_id", id);
		List<ParameterMap> articles = cacheService.getCacheLabelArticle(pm);
		if(articles != null){
			page.setTotalResult(articles.size());
		}
		ParameterMap pmPage = new ParameterMap(page);
		data.put("page", pmPage);
		data.put("data", MyUtil.subList(articles, currentPage, page.getShowCount()));
		data.put("page_url", "/blog/label/"+id);
		return data;
	}
	
	@GetMapping(path="/month/{id}/{pageNo}")
	@ResponseBody
	public Object getMonthPage(@PathVariable("id") String id,@PathVariable("pageNo") String pageNo) throws Exception{
		Map<String, Object> data = new HashMap<>();
		int currentPage = Integer.parseInt(pageNo);
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setShowCount(5);
		ParameterMap pm = this.getParameterMap();
		pm.put("article_month", id);
		List<ParameterMap> articles = cacheService.getCacheMonthArticle(pm);
		System.out.println("articles="+articles);
		if(articles != null){
			page.setTotalResult(articles.size());
		}
		ParameterMap pmPage = new ParameterMap(page);
		data.put("page", pmPage);
		data.put("data", MyUtil.subList(articles, currentPage, page.getShowCount()));
		data.put("page_url", "/blog/month/"+id);
		return data;
	}
	
	
	@GetMapping(path="/search/{pageNo}")
	@ResponseBody
	public Object getMonthPage(@PathVariable("pageNo") String pageNo) throws Exception{
		Map<String, Object> data = new HashMap<>();
		int currentPage = Integer.parseInt(pageNo);
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setShowCount(5);
		ParameterMap pm = this.getParameterMap();
		String keyword = pm.getString("keyword");
		
		System.out.println("keyword="+keyword+",currentPage="+currentPage);
		List<ParameterMap> articles=null;
		page.setPm(pm);
		if(Tools.isEmpty(keyword)) {
			articles = cacheService.getCacheAllArticle(pm);
			if(articles != null){
				page.setTotalResult(articles.size());
			}
			articles=MyUtil.subList(articles, currentPage, page.getShowCount());
		}else {
			articles = articleService.getArticlelistPage(page);
			System.out.println("分页搜索结果articles="+articles.size());
			//重新分词后查询
//			if(articles ==null || articles.size() == 0) {
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
		}
		ParameterMap pmPage = new ParameterMap(page);
		System.out.println("pmPage="+pmPage);
		data.put("keyword", keyword);
		data.put("page", pmPage);
		data.put("data", articles);
		data.put("page_url", "/blog/search");
		return data;
	}
	
}
