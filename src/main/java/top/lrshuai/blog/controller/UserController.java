package top.lrshuai.blog.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import top.lrshuai.blog.controller.base.BaseController;
import top.lrshuai.blog.service.impl.UserService;
import top.lrshuai.blog.util.Const;
import top.lrshuai.blog.util.MyUtil;
import top.lrshuai.blog.util.ParameterMap;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
	
	@Autowired
	private UserService userService;
	
	/**
	 * 登陆
	 * 
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Object login() {
		ParameterMap pm = this.getParameterMap();
		System.out.println("pm=" + pm);
		pm.put("ip", this.getIpAddr());
		log.info("登录，ip="+this.getIpAddr());
		Map<String, Object> map = userService.login(pm,this.getSession());
		return MyUtil.returnObject(pm, map);
	}
	
	/**
	 * qq第三方回调
	 * 
	 * @return
	 */
	@RequestMapping(value = "/qqredirect", method = RequestMethod.GET)
	public ModelAndView redirect() {
		ModelAndView view = this.getModelAndView();
		ParameterMap pm = this.getParameterMap();
		pm.put("ip", this.getIpAddr());
		System.out.println("pm=" + pm);
		userService.qqredirect(pm,this.getSession());
		view.setViewName("redirect");
		return view;
	}
	
	@GetMapping(path="/logout")
	@ResponseBody
	public String logout(HttpSession session) {
		session.removeAttribute(Const.SESSION_USER);
		return "logout success";
	}

}
