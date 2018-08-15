package top.lrshuai.blog.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import top.lrshuai.blog.controller.base.BaseController;
import top.lrshuai.blog.service.impl.ArticleService;
import top.lrshuai.blog.service.impl.CacheService;
import top.lrshuai.blog.service.impl.LabelService;
import top.lrshuai.blog.util.Const;
import top.lrshuai.blog.util.MyUtil;
import top.lrshuai.blog.util.ParameterMap;
import top.lrshuai.blog.util.Tools;

/**
 * 文章控制器
 *
 */
@Controller
@RequestMapping("/atc")
public class ArticleController extends BaseController {

	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private LabelService labelService;
	
	@Autowired
	private RedisTemplate<String, List<ParameterMap>> redis;
	
	/**
	 * 去新增文章页面
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@GetMapping(path="/add")
	public String add(Model model) throws Exception{
		List<ParameterMap> articleLabels = null;
		articleLabels = cacheService.getCacheLabelArticle(new ParameterMap());
		if (articleLabels == null || articleLabels.size() < 1) {
			// 所有文章的标签
			articleLabels = labelService.getArticleLabels(new ParameterMap());
			log.info("不是缓存获取 所有文章的标签");
		} else {
			log.info("缓存获取 所有文章的标签");
		}
		MyUtil.checkLabelStatue(articleLabels, new ArrayList<>());
		model.addAttribute("labels", articleLabels);
		model.addAttribute("action", "/atc/save");
		model.addAttribute("btntext", "发布文章");
		return "blog/add";
	}
	
	/**
	 * 保存文章
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping(path="/save")
	public String save(Model model) throws Exception{
		ParameterMap pm = articleService.saveArticle(this.getParameterMap(), this.getSession());
		model.addAttribute("id", pm.getString("id"));
		model.addAttribute("title", pm.getString("title"));
		model.addAttribute("msg", pm.getString("msg"));
		model.addAttribute("page_title","成功保存");
		return "blog/success";
	}
	
	/**
	 * 获取文章详情
	 * 
	 * @param articleId
	 * @return
	 * @throws Exception
	 */
	@GetMapping(path = "/show/{articleId}")
	public String articleDetail(@PathVariable("articleId") String articleId,Model model) throws Exception {
		ParameterMap pm = this.getParameterMap();
		//为了返回点赞状态
		ParameterMap user = (ParameterMap) this.getSession().getAttribute(Const.SESSION_USER);
		if(user != null) {
			pm.put("user_id", user.getString("user_id"));
		}
		pm.put("article_id", articleId);
		ParameterMap article = articleService.getArticleDetail(pm);
		if(article == null) {
			return "/error/404";
		}
		try {
			//添加点赞状态
			int index = (int) this.getSession().getAttribute(Const.BLOG_ARTICLE_PRAISE_INDEX_+articleId);
			if(index > 0) {
				article.put("praise_flag", 1);
			}
		} catch (Exception e) {
		}
		
		String KEY = (String) this.getSession().getAttribute(Const.USER_LAST_ARTICLE_LIST);
		int tarIndex=0;
		int total =0;
		List<ParameterMap> tarList = null;
		System.out.println("KEY="+KEY);
		if(Tools.notEmpty(KEY)){
			tarList = redis.opsForValue().get(KEY);
		}else{
			tarList =  redis.opsForValue().get(Const.ARTICLE_HOME);
		}
		ParameterMap lastArticle = new ParameterMap();
		ParameterMap nextArticle = new ParameterMap();
		if(tarList != null && tarList.size() > 0){
			total = tarList.size();
			tarIndex = findTarIndex(tarList, articleId);
			int last = tarIndex-1 < 0?total-1:tarIndex-1;
			int next = tarIndex+1 > total-1?0:tarIndex+1;
			
			lastArticle = tarList.get(last);
			nextArticle = tarList.get(next);
		}else {
			lastArticle=article;
			nextArticle=article;
		}
		//上一篇 和下一篇
		model.addAttribute("lArticle", lastArticle);
		model.addAttribute("nArticle", nextArticle);
		
		String text = article.getString("text");
		model.addAttribute("article", article);
		model.addAttribute("description", text.substring(0, text.length() > 100 ?100:text.length()));
		model.addAttribute("title", article.getString("title"));
		
		return "blog/detail";
	}
	
	/**
	 * 获取当前文章在目标集合中的索引
	 * @param tarList
	 * @param articleId
	 * @return
	 */
	public int findTarIndex(List<ParameterMap> tarList,String articleId){
		if(tarList != null && tarList.size() > 0){
			for(int i=0;i<tarList.size();i++){
				String artId = tarList.get(i).getString("article_id");
				if(articleId.equals(artId)){
					return i;
				}
			}
		}
		return 0;
	}
	
	/**
	 * 去更新页面
	 * @param articleId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(path = "/update/{articleId}")
	public String updateArticle(@PathVariable("articleId") String articleId,Model model) throws Exception {
		ParameterMap pm = this.getParameterMap();
		pm.put("article_id", articleId);
		ParameterMap article = articleService.getArticleDetail(pm);
		model.addAttribute("article", article);
		if(article == null) {
			return "/error/404";
		}
		ParameterMap user = (ParameterMap) this.getSession().getAttribute(Const.SESSION_USER);
		if(!article.getString("user_id").equals(user.getString("user_id"))){
			return "/error/403";
		}
		
		List<ParameterMap> articleLabels = cacheService.getCacheLabelArticle(new ParameterMap());
		if (articleLabels == null || articleLabels.size() < 1) {
			// 所有文章的标签
			articleLabels = labelService.getArticleLabels(new ParameterMap());
			log.info("不是缓存获取 所有文章的标签");
		} else {
			log.info("缓存获取 所有文章的标签");
		}
		MyUtil.checkLabelStatue(articleLabels, (List<ParameterMap>)article.get("labels"));
		model.addAttribute("labels", articleLabels);
		model.addAttribute("action", "/atc/update");
		model.addAttribute("btntext", "更新文章");
		return "blog/add";
	}
	
	@PostMapping(path="/update")
	public String update(Model model) throws Exception{
		ParameterMap pm = articleService.updateArticle(this.getParameterMap(), this.getSession());
		model.addAttribute("id", pm.getString("id"));
		model.addAttribute("title", pm.getString("title"));
		model.addAttribute("msg", pm.getString("msg"));
		model.addAttribute("page_title","成功更新");
		return "blog/success";
	}

	@GetMapping(path= "/del/{articleId}")
	public String del(Model model,@PathVariable("articleId") String articleId) throws Exception {
		ParameterMap pm = this.getParameterMap();
		pm.put("article_id", articleId);
		ParameterMap article = articleService.getArticleDetail(pm);
		if(article == null) {
			return "error/404";
		}
		articleService.delArticle(pm);
		return "redirect";
	}

}
