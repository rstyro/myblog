package top.lrshuai.blog.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.util.HtmlUtils;

import top.lrshuai.blog.dao.PublicDao;
import top.lrshuai.blog.dao.UserDao;
import top.lrshuai.blog.entity.User;
import top.lrshuai.blog.plugin.Page;
import top.lrshuai.blog.service.IPublicService;
import top.lrshuai.blog.task.EmailThread;
import top.lrshuai.blog.util.Const;
import top.lrshuai.blog.util.DateUtil;
import top.lrshuai.blog.util.HTMLUtils;
import top.lrshuai.blog.util.MyUtil;
import top.lrshuai.blog.util.ParameterMap;
import top.lrshuai.blog.util.ReturnModel;
import top.lrshuai.blog.util.Tools;


@Service
public class PublicService implements IPublicService {
	@Autowired
	private PublicDao publicDao;
	
	@Autowired
	private UserDao userDao;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private RedisTemplate<String, Integer> redis;

	@Override
	public int saveBrowse(ParameterMap pm) {
		try {
			int issuccess = publicDao.updateBrowseNum(pm);
			if (issuccess > 0) {// 存在这个id，就保存浏览记录
				publicDao.saveBrowse(pm);
			} else {
				return 0;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}
		return 1;
	}


	@Override
	public Map<String, Object> comment(ParameterMap pm,HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		try {
			System.out.println("pm=" + pm);
			String tableId = pm.getString("table_id");
			String content = pm.getString("content");
			String uid = pm.getString("reply_user_id");
			String ip = pm.getString("ip");
			Integer commentIndex= null;
			if (MyUtil.isEmpty(tableId) || MyUtil.isEmpty(content) || MyUtil.isEmpty(uid)) {
				map.put("msg", "你请求的是一个冒牌接口");
				map.put("status", "failed");
				return map;
			}
			//防止html注入
			content = HtmlUtils.htmlEscape(content);
			//替换表情
			content = HTMLUtils.replayFace(content);
			pm.put("content", content);
			pm.put("create_time", DateUtil.getTime());
			ParameterMap user = (ParameterMap) session.getAttribute(Const.SESSION_USER);
			if(user != null){
				pm.put("user_id", user.getString("user_id"));
			}else{
				//匿名的用户
				pm.put("user_id", "2");
				redis.opsForValue().get(Const.LEAVEWORD_NUMBER+ip);
				commentIndex = redis.opsForValue().get(Const.LEAVEWORD_NUMBER+ip);
				if(commentIndex != null && commentIndex >= 5){
					return ReturnModel.getModel("你明天再来刷", "failed", null);
				}
				if(commentIndex == null){
					commentIndex=1;
				}else{
					++commentIndex;
				}
			}
			publicDao.saveComment(pm);
			// 更改评论数
			pm.put("comment_type", "add");
			publicDao.updateCommentNum(pm);

			System.out.println("comment_id="+pm.getString("comment_id"));
			System.out.println("pm="+pm);
			//返回数据
			List<ParameterMap> resultMap = null;
			if(pm.getString("parent_id") != null && !"".equals(pm.getString("parent_id"))){
				resultMap = publicDao.getCommentlist(pm);
			}else{
				Page page = new Page();
				page.setPm(pm);
				resultMap = publicDao.getCommentlistPage(page);
			}
			redis.opsForValue().set(Const.COMMENT_NUMBER+ip, commentIndex, 12, TimeUnit.HOURS);
			resultMap.get(0).put("replybody", new ArrayList<>());
			map.put("data", resultMap);
			map.put("status", "success");
			map.put("msg", "ok");
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			log.error("评论异常:" + e.getMessage(), e);
			map.put("status", "failed");
			map.put("msg", "评论失败");
		}
		return map;
	}

	@Override
	public Map<String, Object> getComment(ParameterMap pm,HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		List<ParameterMap> floorlist = null;
		List<ParameterMap> replyfloorlist = null;
		try {
			String tableId = pm.getString("table_id");
			if (MyUtil.isEmpty(tableId)) {
				map.put("msg", "你请求的是一个冒牌接口");
				map.put("status", "failed");
				return map;
			}
			int pageNo = 1;
			int pageSize = 10;
			if (MyUtil.notEmpty(pm.getString("page_no"))) {
				pageNo = Integer.parseInt(pm.getString("page_no"));
			}
			if (MyUtil.notEmpty(pm.getString("page_size"))) {
				pageSize = Integer.parseInt(pm.getString("page_size"));
			}
			Page page = new Page();
			page.setCurrentPage(pageNo);
			page.setShowCount(pageSize);
			page.setPm(pm);
			// 楼主评论
			floorlist = publicDao.getCommentlistPage(page);
			if (floorlist != null && floorlist.size() > 0) {
				// 回复楼主的评论
				replyfloorlist = publicDao.getCommentlist(pm);
				Map<String, List<ParameterMap>> commentMap = new HashMap<>();
				if (replyfloorlist != null && replyfloorlist.size() > 0) {
					// 把同一个楼层回复的放到一个数组里
					for (ParameterMap ptm : replyfloorlist) {
						if (commentMap.containsKey(ptm.getString("parent_id"))) {
							commentMap.get(ptm.getString("parent_id")).add(ptm);
						} else {
							List<ParameterMap> list = new ArrayList<>();
							list.add(ptm);
							commentMap.put(ptm.getString("parent_id"), list);
						}
					}
				}
				// 给每层的楼主添加回复到replybody中
				for (ParameterMap ptm : floorlist) {
					System.out.println("==============????==================");
					if (commentMap.containsKey(ptm.getString("comment_id"))) {
						ptm.put("replybody", commentMap.get(ptm.getString("comment_id")));
					} else {
						ptm.put("replybody", new ArrayList<>());
					}
				}
			} else {
				floorlist = new ArrayList<>();
			}
			ParameterMap pmpage = new ParameterMap(page);
			map.put("page", pmpage);
			map.put("msg", "ok");
			map.put("status", "success");
			map.put("data", floorlist);
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			log.error("获取评论异常:" + e.getMessage(), e);
			map.put("status", "failed");
			map.put("msg", "获取评论失败");
		}
		return map;
	}

	@Override
	public Map<String, Object> delComment(ParameterMap pm,HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		try {
			String commendId = pm.getString("comment_id");
			if (MyUtil.isEmpty(commendId)) {
				map.put("msg", "你请求的是一个错误的接口");
				map.put("status", "failed");
				return map;
			}
			User user = (User) session.getAttribute(Const.SESSION_USER);
			if (user != null) {
				pm.put("user_id", user.getUser_id());
			} else {
				map.put("status", "auth");
				map.put("msg", "auth failed");
				return map;
			}
			ParameterMap isFloorC = publicDao.checkCommentId(pm);
			System.out.println("isFloorC=" + isFloorC);
			if (isFloorC != null && isFloorC.size() > 0) {
				String parentId = isFloorC.getString("parent_id");
				// 是楼层评论，删除所有子评论
				if (MyUtil.isEmpty(parentId)) {
					pm.put("parent_id", commendId);
				} else {
					pm.put("parent_id", "");
				}
				System.out.println("111pm=" + pm);
				int iss = publicDao.delComment(pm);
				System.out.println("iss=" + iss);
			}
			map.put("status", "success");
			map.put("msg", "ok");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error("点赞异常:" + e.getMessage(), e);
			map.put("status", "failed");
			map.put("msg", "点赞失败");
		}
		return map;
	}

	@Override
	public Map<String, Object> savePraise(ParameterMap pm,HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		try {
			String tableId = pm.getString("table_id");
			String tableType = pm.getString("table_type");
			System.out.println("table_id=" + tableId + ", table_type=" + tableType);
			if (Tools.isEmpty(tableId) || Tools.isEmpty(tableType)) {
				map.put("msg", "你请求的是一个冒牌接口");
				map.put("status", "failed");
				return map;
			}
			ParameterMap user =  (ParameterMap) session.getAttribute(Const.SESSION_USER);
			if (user != null) {
				pm.put("user_id", user.getString("user_id"));
				ParameterMap repeatPraise = publicDao.repeatPraise(pm);
				if (repeatPraise != null && repeatPraise.size() > 0) {
					pm.put("praise_type", "sub");
					System.out.println("pm=" + pm);
					publicDao.updatePraiseNum(pm);
					publicDao.delPraise(pm);
				} else {
					pm.put("praise_type", "add");
					System.out.println("pm=" + pm);
					int issuccess = publicDao.updatePraiseNum(pm);
					if (issuccess > 0) {// 有修改记录，就保存点赞记录
						publicDao.savePraise(pm);
					}
				}
			} else {
				int index = 0;
				try {
					if ("article".equalsIgnoreCase(tableType)) {
						index = (int) session.getAttribute(Const.BLOG_ARTICLE_PRAISE_INDEX_ + tableId);
					} else {
						index = (int) session.getAttribute(Const.BLOG_COMMENT_PRAISE_INDEX_ + tableId);
					}
				} catch (Exception e) {
					System.out.println("为空报错不理它");
				}
				if (index > 0) {
					pm.put("praise_type", "sub");
					System.out.println("pm=" + pm);
					publicDao.updatePraiseNum(pm);
					if ("article".equalsIgnoreCase(tableType)) {
						session.setAttribute(Const.BLOG_ARTICLE_PRAISE_INDEX_ + tableId, 0);
					} else {
						session.setAttribute(Const.BLOG_COMMENT_PRAISE_INDEX_ + tableId, 0);
					}
				}else {
					pm.put("praise_type", "add");
					System.out.println("pm=" + pm);
					publicDao.updatePraiseNum(pm);
					if ("article".equalsIgnoreCase(tableType)) {
						session.setAttribute(Const.BLOG_ARTICLE_PRAISE_INDEX_ + tableId, 1);
					} else {
						session.setAttribute(Const.BLOG_COMMENT_PRAISE_INDEX_ + tableId, 1);
					}
				}
			}
			map.put("status", "success");
			map.put("msg", "ok");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			log.error("点赞异常:" + e.getMessage(), e);
			map.put("status", "failed");
			map.put("msg", "点赞失败");
		}
		return map;
	}

	@Override
	public Map<String, Object> leaveword(ParameterMap pm, HttpSession session) {
		try {
			String content = pm.getString("content");
			if(Tools.isEmpty(content)){
				return ReturnModel.getErrorModel();
			}
			String ip = pm.getString("ip");
			redis.opsForValue().get(Const.LEAVEWORD_NUMBER+ip);
			Integer leaveIndex = redis.opsForValue().get(Const.LEAVEWORD_NUMBER+ip);
			if(leaveIndex != null && leaveIndex >= 5){
				return ReturnModel.getModel("你明天再来刷", "failed", null);
			}
			
			ParameterMap user = (ParameterMap) session.getAttribute(Const.SESSION_USER);
			if(user != null){
				pm.put("user_id", user.getString("user_id"));
			}else{
				//匿名的用户
				pm.put("user_id", "2");
			}
			content = HtmlUtils.htmlEscape(content);
			//替换表情
			content = HTMLUtils.replayFace(content);
			pm.put("content", content);
			pm.put("create_time", DateUtil.getTime());
			publicDao.saveLeaveWord(pm);
			if(leaveIndex == null){
				leaveIndex=1;
			}else{
				++leaveIndex;
			}
			redis.opsForValue().set(Const.LEAVEWORD_NUMBER+ip, leaveIndex, 12, TimeUnit.HOURS);
			Page page = new Page();
			page.setPm(pm);
			List<ParameterMap> lists = publicDao.getleaveWordlistPage(page);
			return ReturnModel.getModel("ok", "success", lists);
		} catch (Exception e) {
			log.info("error", e);
			e.printStackTrace();
		}
		return ReturnModel.getModel("error", "failed", new ArrayList<>());
	}
	
	@Override
	public Map<String, Object> getLeaveWord(ParameterMap pm, HttpSession session) {
		try {
			int pageNo = 1;
			int pageSize = 10;
			if (MyUtil.notEmpty(pm.getString("page_no"))) {
				pageNo = Integer.parseInt(pm.getString("page_no"));
			}
			if (MyUtil.notEmpty(pm.getString("page_size"))) {
				pageSize = Integer.parseInt(pm.getString("page_size"));
			}
			Page page = new Page();
			page.setCurrentPage(pageNo);
			page.setShowCount(pageSize);
			page.setPm(pm);
			// 楼主评论
			List<ParameterMap> lists = publicDao.getleaveWordlistPage(page);
			ParameterMap pmpage = new ParameterMap(page);
			return ReturnModel.getModel("ok", "success", lists,pmpage);
		} catch (Exception e) {
			log.info("error", e);
			e.printStackTrace();
		}
		return ReturnModel.getModel("error", "failed", new ArrayList<>());
	}
	
	@Override
	public Map<String, Object> saveFriendLink(ParameterMap pm, HttpSession session) {
		try {
			String ip = pm.getString("ip");
			Integer index = redis.opsForValue().get(Const.ADD_LINKS_INDEX+ip);
			if(index != null && index > 5){
				return ReturnModel.getModel("朋友你是来捣乱的吧！", "failed", null);
			}
			String link = pm.getString("link");
			String description = pm.getString("description");
			String link_name = pm.getString("link_name");
			link = HtmlUtils.htmlEscape(link);
			description = HtmlUtils.htmlEscape(description);
			link_name = HtmlUtils.htmlEscape(link_name);
			pm.put("link", link);
			pm.put("description", description);
			pm.put("link_name", link_name);
			publicDao.saveFriendLink(pm);
			String content = "来自ip:"+ip+"<table><tr><th>网站名称</th><th>网站链接</th><th>网站描述</th></tr><tr><td>"+link_name
							+"</td><td>"+link+"</td><td>"+description+"</td></tr></table>";
			EmailThread et = new EmailThread(mailService,"1006059906@qq.com", "添加友链",content);
			new Thread(et).start();
			if(index == null){
				index = 1;
			}else{
				++index;
			}
			redis.opsForValue().set(Const.ADD_LINKS_INDEX+ip, index, 24*60*60, TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error("error", e);
			e.printStackTrace();
			return ReturnModel.getModel("申请失败,重试失败请联系博主", "failed", null);
		}
		return ReturnModel.getModel("ok", "success", null);
	}
	
	@Override
	public int saveBlackIp(ParameterMap pm) {
		try {
			publicDao.addBlackIP(pm);
		} catch (Exception e) {
			log.error("save black ip err", e);
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	@Override
	public Map<String, Object> editBlackIpList(ParameterMap pm, HttpSession session) {
		try {
			String auth_code = pm.getString("auth_code");
			String sessionCode = (String) session.getAttribute(Const.AUTH_CODE);
			if(sessionCode == null || !sessionCode.equalsIgnoreCase(auth_code)) {
				return ReturnModel.getModel("验证码不匹配", "failed", null);
			}
			publicDao.updateBlackIP(pm);
			cacheService.cacheBlackIP();
		} catch (Exception e) {
			log.error("error", e);
			e.printStackTrace();
			return ReturnModel.getModel("解除黑名单错误,可给博主留言", "failed", null);
		}
		return ReturnModel.getModel("ok", "success", null);
	}
	
	@Override
	public int saveDayBrowseNum(ParameterMap pm) {
		int issuccess = 0;
		try {
			if(pm == null){
				pm = new ParameterMap();
			}
			Integer browse_people_num = redis.opsForValue().get(Const.BLOG_BROWSE_PEOPLE_NUMBER);
			Integer browse_total_num = redis.opsForValue().get(Const.BLOG_BROWSE_TOTAL_NUMBER);
			if(browse_people_num == null){
				browse_people_num=0;
			}
			if(browse_total_num == null){
				browse_total_num=0;
			}
			pm.put("browse_people_num", browse_people_num);
			pm.put("browse_total_num", browse_total_num);
			pm.put("create_time", DateUtil.fomatDate(DateUtil.getBeginDayOfYesterday(), "yyyy-MM-dd"));
			issuccess = publicDao.saveEveryDayBrowseNum(pm);
			redis.opsForValue().set(Const.BLOG_BROWSE_PEOPLE_NUMBER, 0);
			redis.opsForValue().set(Const.BLOG_BROWSE_TOTAL_NUMBER, 0);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("saveDayBrowseNumError", e);
		}
		return issuccess;
	}
	
	@Override
	public Map<String, Object> talk(ParameterMap pm, HttpSession session) {
		System.out.println("pm="+pm);
		try {
			String tableName = pm.getString("table_name");
			ParameterMap user = (ParameterMap) session.getAttribute(Const.SESSION_USER);
			if(user != null){
				pm.put("user_id", user.getString("user_id"));
			}else{
				String ip = pm.getString("ip");
				redis.opsForValue().get(Const.LEAVEWORD_NUMBER+ip);
				Integer leaveIndex = redis.opsForValue().get(Const.LEAVEWORD_NUMBER+ip);
				if(leaveIndex != null && leaveIndex >= 5){
					return ReturnModel.getModel("匿名次数限制", "failed", null);
				}
				if(leaveIndex == null){
					leaveIndex=1;
				}else{
					++leaveIndex;
				}
				redis.opsForValue().set(Const.LEAVEWORD_NUMBER+ip, leaveIndex, 12, TimeUnit.HOURS);
				
				String userName = pm.getString("userName");
				if(userName == null || "".equals(userName)){
					//匿名的用户
					pm.put("user_id", "2");
				}else{
					ParameterMap haveUser = new ParameterMap();
					haveUser.put("userName", userName);
					haveUser=userDao.getUserInfo(haveUser);
					if(haveUser != null){
						pm.put("user_id", haveUser.getString("user_id"));
					}else{
						haveUser = new ParameterMap();
						haveUser.put("username", "blog_" + MyUtil.random(8));
						haveUser.put("password", "password_" + MyUtil.random(8));
						haveUser.put("name", userName);
						haveUser.put("register_type", "niming");
						haveUser.put("ip", ip);
						haveUser.put("img",pm.getString("userPath"));
						haveUser.put("status", "unlock");
						haveUser.put("create_time", DateUtil.getTime());
						userDao.saveUser(haveUser);
						pm.put("user_id", haveUser.getString("user_id"));
					}
				}
			}
			String content = pm.getString("content");
			content = HtmlUtils.htmlEscape(content);
			pm.put("content", content);
			pm.put("create_time", DateUtil.getTime());
			EmailThread et = new EmailThread(mailService,"1006059906@qq.com", tableName + "有信息,By table_id="+pm.getString("table_id"),content);
			new Thread(et).start();
			if(tableName.equalsIgnoreCase("leaveword")){
				publicDao.saveLeaveWord(pm);
				System.out.println("pmmm="+pm);
				List<ParameterMap> lists = publicDao.getleaveWordlist(pm);
				System.out.println("lists="+lists.get(0));
				return ReturnModel.getModel("ok", "success", lists.get(0));
			}else if(tableName.equalsIgnoreCase("comment")){
				publicDao.saveComment(pm);
				// 更改评论数
				pm.put("comment_type", "add");
				publicDao.updateCommentNum(pm);
				List<ParameterMap> lists = publicDao.getCommentlist(pm);
				System.out.println("lists="+lists.get(0));
				return ReturnModel.getModel("ok", "success", lists.get(0));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Map<String, Object> gettalk(ParameterMap pm, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		List<ParameterMap> floorlist = null;
		List<ParameterMap> replyfloorlist = null;
		try {
			int pageNo = 1;
			int pageSize = 10;
			if (MyUtil.notEmpty(pm.getString("page_no"))) {
				pageNo = Integer.parseInt(pm.getString("page_no"));
			}
			if (MyUtil.notEmpty(pm.getString("page_size"))) {
				pageSize = Integer.parseInt(pm.getString("page_size"));
			}
			Page page = new Page();
			page.setCurrentPage(pageNo);
			page.setShowCount(pageSize);
			page.setPm(pm);
			String tableType = pm.getString("table_type");
			// 楼主评论
			if("comment".equalsIgnoreCase(tableType)){
				floorlist = publicDao.getCommentlistPage(page);
			}else if("leaveword".equalsIgnoreCase(tableType)){
				floorlist = publicDao.getleaveWordslistPage(page);
			}
			if (floorlist != null && floorlist.size() > 0) {
				// 回复楼主的评论
				if("comment".equalsIgnoreCase(tableType)){
					replyfloorlist = publicDao.getCommentlist(pm);
				}else if("leaveword".equalsIgnoreCase(tableType)){
					replyfloorlist = publicDao.getleaveWordlist(pm);
				}
				Map<String, List<ParameterMap>> commentMap = new HashMap<>();
				if (replyfloorlist != null && replyfloorlist.size() > 0) {
					// 把同一个楼层回复的放到一个数组里
					for (ParameterMap ptm : replyfloorlist) {
						if (commentMap.containsKey(ptm.getString("parent_id"))) {
							commentMap.get(ptm.getString("parent_id")).add(ptm);
						} else {
							List<ParameterMap> list = new ArrayList<>();
							list.add(ptm);
							commentMap.put(ptm.getString("parent_id"), list);
						}
					}
				}
				// 给每层的楼主添加回复到replybody中
				for (ParameterMap ptm : floorlist) {
					if (commentMap.containsKey(ptm.getString("comment_id"))) {
						ptm.put("replybody", commentMap.get(ptm.getString("comment_id")));
					} else {
						ptm.put("replybody", new ArrayList<>());
					}
				}
			} else {
				floorlist = new ArrayList<>();
			}
			ParameterMap pmpage = new ParameterMap(page);
			map.put("page", pmpage);
			map.put("msg", "ok");
			map.put("status", "success");
			map.put("data", floorlist);
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			log.error("获取评论异常:" + e.getMessage(), e);
			map.put("status", "failed");
			map.put("msg", "获取评论失败");
		}
		return map;
	}
	
	@Override
	public Map<String, Object> qrcode(ParameterMap pm, HttpSession session) {
		try {
			if(pm.getString("content") == null || "".equals(pm.getString("content"))){
				return ReturnModel.getModel("ok", "success","http://share.lrshuai.top");
			}
			try {
				publicDao.saveQrcode(pm);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			}
			String content = pm.getString("content");
			String result = "";
			String[] arr = content.split(",");
			StringBuffer sb = new StringBuffer();
			for(String key:arr){
				sb.append(key).append("||");
			}
			result = sb.toString();
			result = result.substring(0,result.length() - 2);
			return ReturnModel.getModel("ok", "success","http://share.lrshuai.top?kw="+result);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
			return ReturnModel.getModel("服务器异常","failed",null);
		}
	}
}
