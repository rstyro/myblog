package top.lrshuai.blog.intercept;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import top.lrshuai.blog.service.impl.MailService;
import top.lrshuai.blog.service.impl.PublicService;
import top.lrshuai.blog.task.EmailThread;
import top.lrshuai.blog.util.Const;
import top.lrshuai.blog.util.DateUtil;
import top.lrshuai.blog.util.ParameterMap;

public class UrlInterceptor implements HandlerInterceptor {

	@Autowired
	private PublicService publicService;
	
	@Autowired
	private MailService mailService;

	private Logger log = Logger.getLogger(getClass());
	
	@Autowired
	private RedisTemplate<String, Integer> redis;
	
	private RedisTemplate<String,List<ParameterMap>> lredis;
	
	private RedisTemplate<String, Object> oRedis;
	

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj, ModelAndView mv)
			throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		try {
			String path = request.getServletPath();
			BeanFactory factory = WebApplicationContextUtils
					.getRequiredWebApplicationContext(request.getServletContext());
			HttpSession session = request.getSession();
			redis = (RedisTemplate<String, Integer>) factory.getBean("redisTemplate");
			lredis = (RedisTemplate<String, List<ParameterMap>>) factory.getBean("redisTemplate");
			oRedis = (RedisTemplate<String, Object>) factory.getBean("redisTemplate");
			List<ParameterMap> blackIpList = lredis.opsForValue().get(Const.BLACK_IP_LIST);
			String ip = getIp(request);
			if(blackIpList != null){
				for(ParameterMap ippm:blackIpList) {
					if(ip.equals(ippm.getString("ip"))) {
						log.info("黑名单用户访问:"+path+" ,ip:" + ip);
						response.sendRedirect(request.getContextPath() + "/error/302");
						session.setAttribute(Const.AUTH_PAGE, "1");
						return false;
					}
				}
			}
			if (publicService == null) {// 解决service为null无法注入问题
				publicService = (PublicService) factory.getBean("publicService");
			}
			if (mailService == null) {// 解决service为null无法注入问题
				mailService =  (MailService) factory.getBean("mailService");
			}
			ParameterMap user = (ParameterMap) session.getAttribute(Const.SESSION_USER);
//			Integer num = redis.opsForValue().get(Const.VISIT_+path+"_"+ip);
//			if(num == null) {
//				redis.opsForValue().set(Const.VISIT_+path+"_"+ip, 1,10,TimeUnit.SECONDS);
//			}else if(num >= 50){
//				EmailThread et = new EmailThread(mailService,"1006059906@qq.com", "黑名单","添加黑名单ip:"+ip);
//				new Thread(et).start();
//				log.info("ip:"+ip+",访问异常");
//				ParameterMap pm = new ParameterMap();
//				pm.put("ip", ip);
//				pm.put("create_time", DateUtil.getTime());
//				publicService.saveBlackIp(pm);
//				blackIpList.add(pm);
//				lredis.opsForValue().set(Const.BLACK_IP_LIST, blackIpList);
//				session.setAttribute(Const.AUTH_PAGE, "1");
//				response.sendRedirect(request.getContextPath() + "/error/302");
//				return false;
//			}else {
//				redis.opsForValue().set(Const.VISIT_+path+"_"+ip, ++num,10,TimeUnit.SECONDS);
//			}
			
			Long count = incr(ip, 1, oRedis.getConnectionFactory());
			System.out.println("count="+count);
			System.out.println("path="+path);
			if (count > 5) {
				System.out.println("当前请求大于5，ip为="+ip);
				log.info("当前请求大于10，ip为="+ip);
				EmailThread et = new EmailThread(mailService,"1006059906@qq.com", "黑名单","添加黑名单ip:"+ip);
				new Thread(et).start();
				log.info("ip:"+ip+",访问异常");
				ParameterMap pm = new ParameterMap();
				pm.put("ip", ip);
				pm.put("create_time", DateUtil.getTime());
				publicService.saveBlackIp(pm);
				blackIpList.add(pm);
				lredis.opsForValue().set(Const.BLACK_IP_LIST, blackIpList);
				session.setAttribute(Const.AUTH_PAGE, "1");
				response.sendRedirect(request.getContextPath() + "/error/302");
				return false;
			}
			
			
			//文章匹配
			if (path.matches(".*/atc/.*")) {
				if (path.matches(".*/atc/show/.*")) {
					System.out.println("匹配成功");
					ParameterMap pm = new ParameterMap();
					if (user != null) {
						pm.put("user_id", user.getString("user_id"));
					}
					String id = path.substring(path.lastIndexOf("/") + 1, path.length());
					System.out.println("id=" + id);
					pm.put("table_id", id);
					pm.put("ip", getIp(request));
					System.out.println("publicService=" + publicService);
					String browseIndex = (String) session.getAttribute(Const.BLOG_BROWSE_INDEX + id);
					if (browseIndex == null) {
						publicService.saveBrowse(pm);
						session.setAttribute(Const.BLOG_BROWSE_INDEX + id, "1");
					}
					return true;
				}
				if (null == user || "".equals(user)) {
					response.sendRedirect(request.getContextPath() + "/login");
					return false;
				} else if (!user.getString("user_id").equals("1") && !"1".equals(user.getString("auth_blog"))) {
					log.info("用户没有权限 from ip:" + getIp(request));
					response.sendRedirect(request.getContextPath() + "/error/403");
					return false;
				}
			}
			
			//每日人访问量统计
			Integer browsePeopleNum =(Integer) session.getAttribute(Const.BLOG_BROWSE_PEOPLE_NUMBER);
			Integer browseTotalNum =(Integer) redis.opsForValue().get(Const.BLOG_BROWSE_TOTAL_NUMBER);
			Integer newNum = 1;
			if(browsePeopleNum == null){
				Integer nowNum = redis.opsForValue().get(Const.BLOG_BROWSE_PEOPLE_NUMBER);
				if(nowNum != null){
					newNum = nowNum+1;
				}
				redis.opsForValue().set(Const.BLOG_BROWSE_PEOPLE_NUMBER, newNum);
				session.setAttribute(Const.BLOG_BROWSE_PEOPLE_NUMBER, 1);
			}
			//每日总的点击量
			if(browseTotalNum == null){
				redis.opsForValue().set(Const.BLOG_BROWSE_TOTAL_NUMBER, 1);
			}else{
				redis.opsForValue().set(Const.BLOG_BROWSE_TOTAL_NUMBER, (browseTotalNum+1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public String getIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	/**
	 * 自己实现的自增方法
	 * @param key
	 * @param liveTime
	 * @param factory
	 * @return
	 */
	public Long incr(String key, long liveTime,RedisConnectionFactory factory) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, factory);
        Long increment = entityIdCounter.getAndIncrement();

        if ((null == increment || increment.longValue() == 0) && liveTime > 0) {//初始设置过期时间
            entityIdCounter.expire(liveTime, TimeUnit.SECONDS);
        }
        return increment;
    }

}
