package top.lrshuai.blog.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import top.lrshuai.blog.controller.base.BaseController;
import top.lrshuai.blog.service.IPublicService;
import top.lrshuai.blog.util.Const;
import top.lrshuai.blog.util.MyUtil;
import top.lrshuai.blog.util.ParameterMap;
import top.lrshuai.blog.util.ReturnModel;


@Controller
@RequestMapping("/public")
public class PublicController extends BaseController {

	@Autowired
	private IPublicService publicService;

	/**
	 * 点赞
	 * 
	 * @return
	 */
	@RequestMapping(value = "/praise", method = RequestMethod.POST)
	@ResponseBody
	public Object praise() {
		log.info("点赞");
		ParameterMap pm = this.getParameterMap();
		Map<String, Object> map = publicService.savePraise(pm,this.getSession());
		return MyUtil.returnObject(pm, map);
	}

//	/**
//	 * 评论
//	 * 
//	 * @return
//	 */
//	@PostMapping(path = "/comment")
//	@ResponseBody
//	public Object comment() {
//		log.info("评论");
//		ParameterMap pm = this.getParameterMap();
//		String ip = this.getIpAddr();
//		pm.put("ip", ip);
//		Map<String, Object> map = publicService.comment(pm,this.getSession());
//		return MyUtil.returnObject(pm, map);
//	}
//
//
//	/**
//	 * 获取评论
//	 * 
//	 * @return
//	 */
//	@RequestMapping(value = "/getComment", method = RequestMethod.GET)
//	@ResponseBody
//	public Object getComment() {
//		log.info("获取评论");
//		ParameterMap pm = this.getParameterMap();
//		String ip = this.getIpAddr();
//		pm.put("ip", ip);
//		Map<String, Object> map = publicService.getComment(pm,this.getSession());
//		return MyUtil.returnObject(pm, map);
//	}

	/**
	 * 删除评论
	 * 
	 * @return
	 */
	@RequestMapping(value = "/delComment", method = RequestMethod.GET)
	@ResponseBody
	public Object delComment() {
		log.info("删除评论");
		ParameterMap pm = this.getParameterMap();
		String ip = this.getIpAddr();
		pm.put("ip", ip);
		Map<String, Object> map = publicService.delComment(pm,this.getSession());
		return MyUtil.returnObject(pm, map);
	}

	/**
	 * 留言
	 * 
	 * @return
	 */
	@RequestMapping(value = "/leaveword", method = RequestMethod.POST)
	@ResponseBody
	public Object leaveword() {
		log.info("留言");
		ParameterMap pm = this.getParameterMap();
		String ip = this.getIpAddr();
		pm.put("ip", ip);
		Map<String, Object> map = publicService.leaveword(pm,this.getSession());
		return MyUtil.returnObject(pm, map);
	}
	
	/**
	 * 获取留言
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getLeaveWord", method = RequestMethod.GET)
	@ResponseBody
	public Object getLeaveWord() {
		log.info("获取留言");
		ParameterMap pm = this.getParameterMap();
		String ip = this.getIpAddr();
		pm.put("ip", ip);
		Map<String, Object> map = publicService.getLeaveWord(pm,this.getSession());
		return MyUtil.returnObject(pm, map);
	}
	
	
	/**
	 * 解除黑名单
	 * @return
	 */
	@RequestMapping(value = "/delBlack", method = RequestMethod.POST)
	@ResponseBody
	public Object removeBlackIp() {
		log.info("修改留言");
		String isAuth = (String) this.getSession().getAttribute(Const.AUTH_PAGE);
		if(isAuth == null) {
			return ReturnModel.getErrorModel();
		}
		
		ParameterMap pm = this.getParameterMap();
		String ip = this.getIpAddr();
		pm.put("ip", ip);
		Map<String, Object> map = publicService.editBlackIpList(pm,this.getSession());
		this.getSession().removeAttribute(Const.AUTH_PAGE);
		return MyUtil.returnObject(pm, map);
	}
	
	
	
	/**
	 * 添加友链
	 * @return
	 */
	@RequestMapping(value = "/saveLink", method = RequestMethod.POST)
	@ResponseBody
	public Object saveLinks() {
		log.info("添加友链");
		ParameterMap pm = this.getParameterMap();
		String ip = this.getIpAddr();
		pm.put("ip", ip);
		Map<String, Object> map = publicService.saveFriendLink(pm,this.getSession());
		return MyUtil.returnObject(pm, map);
	}
	
	/**
	 * 评论插件接口
	 * @return
	 */
	@RequestMapping(value = "/talk", method = RequestMethod.POST)
	@ResponseBody
	public Object talk() {
		log.info("讨论");
		ParameterMap pm = this.getParameterMap();
		String ip = this.getIpAddr();
		pm.put("ip", ip);
		Map<String, Object> map = publicService.talk(pm,this.getSession());
		return MyUtil.returnObject(pm, map);
	}
	
	/**
	 * 获取内容
	 * @return
	 */
	@RequestMapping(value = "/getTalk", method = RequestMethod.GET)
	@ResponseBody
	public Object getTalk() {
		log.info("获取讨论");
		ParameterMap pm = this.getParameterMap();
		String ip = this.getIpAddr();
		pm.put("ip", ip);
		Map<String, Object> map = publicService.gettalk(pm, this.getSession());
		return MyUtil.returnObject(pm, map);
	}
	
	/**
	 * 提交二维码
	 * @return
	 */
	@RequestMapping(value = "/qrcode", method = RequestMethod.POST)
	@ResponseBody
	public Object qrcode() {
		log.info("二维码");
		ParameterMap pm = this.getParameterMap();
		String ip = this.getIpAddr();
		pm.put("ip", ip);
		Map<String, Object> map = publicService.qrcode(pm, this.getSession());
		return MyUtil.returnObject(pm, map);
	}

}
