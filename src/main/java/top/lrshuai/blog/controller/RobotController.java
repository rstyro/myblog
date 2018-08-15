package top.lrshuai.blog.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import top.lrshuai.blog.controller.base.BaseController;
import top.lrshuai.blog.plugin.Page;
import top.lrshuai.blog.service.IRobotService;
import top.lrshuai.blog.util.Const;
import top.lrshuai.blog.util.ParameterMap;

@Controller
@RequestMapping("/robot")
public class RobotController extends BaseController{

	@Autowired
	private IRobotService iRobotService;
	
	/**
	 * 保存提问机制
	 * @return
	 */
	@PostMapping("/question")
	@ResponseBody
	public Object save(){
		return iRobotService.save(getParameterMap(),getSession());
	}
	
	/**
	 * 回答机制
	 * @return
	 */
	@PostMapping("/answer")
	@ResponseBody
	public Object answer(){
		return iRobotService.answer(getParameterMap(),getSession());
	}
	
	/**
	 * 去机器人的大脑列表
	 * @param model
	 * @return
	 */
	@GetMapping(path="/brain/{page_no}")
	public String brain(Model model,@PathVariable("page_no") String pageNo){
		ParameterMap pm = this.getParameterMap();
		pm.put("page_no", pageNo);
		ParameterMap user = (ParameterMap) this.getSession().getAttribute(Const.SESSION_USER);
		if(user != null && ("1".equals(user.getString("auth_robot")) || "1".equals(user.getString("user_id")))){
			Map<String,Object> map = iRobotService.getBrainList(pm);
			model.addAttribute("msg", map.get("msg"));
			model.addAttribute("data", map.get("data"));
			model.addAttribute("status", map.get("status"));
			model.addAttribute("page", map.get("page"));
		}else{
			model.addAttribute("msg", "你没有权限访问");
			model.addAttribute("status", "failed");
			Page page = new Page();
			page.setCurrentPage(1);
			page.setShowCount(10);
			page.getCurrentPage();
			page.getCurrentResult();
			model.addAttribute("page", page);
		}
		model.addAttribute("kw", pm.getString("kw"));
		return "robot/brain";
	}
	
	
	/**
	 * 去机器人的大脑列表
	 * @param model
	 * @return
	 */
	@GetMapping(path="/history/{page_no}")
	public String history(Model model,@PathVariable("page_no") String pageNo){
		ParameterMap pm = this.getParameterMap();
		pm.put("page_no", pageNo);
		pm.put("type", "history");
		Map<String,Object> map = iRobotService.getBrainList(pm);
		System.out.println("map="+map);
		model.addAttribute("msg", map.get("msg"));
		model.addAttribute("data", map.get("data"));
		model.addAttribute("status", map.get("status"));
		model.addAttribute("page", map.get("page"));
		model.addAttribute("kw", pm.getString("kw"));
		return "robot/history";
	}
	
	/**
	 * 通过id获取数据
	 * @return
	 */
	@GetMapping("/find")
	@ResponseBody
	public Object get(){
		return iRobotService.find(getParameterMap());
	}
	
	/**
	 * 删除
	 * @return
	 */
	@PostMapping("/del")
	@ResponseBody
	public Object del(){
		return iRobotService.del(getParameterMap(),getSession());
	}
	
	/**
	 * 更新
	 * @return
	 */
	@PostMapping("/update")
	@ResponseBody
	public Object update(){
		return iRobotService.update(getParameterMap(),getSession());
	}
}
