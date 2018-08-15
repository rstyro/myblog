package top.lrshuai.blog.service.impl;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import top.lrshuai.blog.util.HttpUtils;
import top.lrshuai.blog.util.ImgUtil;
import top.lrshuai.blog.util.MyUtil;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import top.lrshuai.blog.dao.UserDao;
import top.lrshuai.blog.service.IUserService;
import top.lrshuai.blog.util.Const;
import top.lrshuai.blog.util.ParameterMap;
import top.lrshuai.blog.util.ReturnModel;
import top.lrshuai.blog.util.SHA;
import top.lrshuai.blog.util.Tools;

@Service
public class UserService implements IUserService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private RedisTemplate<String, Object> redis;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	//图片上次根目录
	@Value("${upload.root.folder}")
	public String root_fold;
	//用户头像存储路径
	@Value("${user.folder}")
	public String user_folder;
	
	//qq第三方相关链接
	@Value("${qq_open_url}")
	private String qq_open_url;
	
	@Value("${qq_token_url}")
	private String qq_token_url;
	
	@Value("${qq_user_url}")
	private String qq_user_url;
	
	@Value("${qq_app_key}")
	private String qq_app_key;
	
	@Value("${qq_app_secret}")
	private String qq_app_secret;

	@Value("${qq_redirect_url}")
	private String qq_redirect_url;
	
	@Override
	public Map<String, Object> login(ParameterMap pm,HttpSession session) {
		System.out.println("pm=" + pm);
		try {
			String psw = pm.getString("password");
			String userName = pm.getString("username");
			if(Tools.isEmpty(userName) || Tools.isEmpty(psw)){
				return ReturnModel.getModel("你请求的是冒牌接口", "falied", null);
			}
			String ip = pm.getString("rip");
			psw = SHA.encryptSHA(psw);
			System.out.println("encode psw=" + psw);
			System.out.println("ip=" + ip);
			Integer lockIpNum = (Integer) redis.opsForValue().get("LOGIN_"+ip);
			if(lockIpNum != null && lockIpNum >= 10){
				return ReturnModel.getModel("此ip登录错误次数过多，目前已锁定。请联系管理员", "failed", null);
			}else if(lockIpNum == null){
				lockIpNum=0;
			}
			pm.put("password", psw);
			ParameterMap user = userDao.getUserInfo(pm);
			if (user == null) {
				checkAccount(lockIpNum, ip, userName);
				return ReturnModel.getModel("用户名或密码错误", "failed", null);
			}
			session.setAttribute(Const.SESSION_USER, user);
			
		}catch (RedisConnectionFailureException e) {
			return ReturnModel.getModel("连接redis失败，请检查redis是否开启", "failed", null);
		}
		catch (Exception e) {
			e.printStackTrace();
			log.error("login error :" + e.getMessage(), e);
			return ReturnModel.getModel("登录错误，请稍后重试", "failed", null);
		}
		return ReturnModel.getModel("ok", "success", null);
	}
	
	/**
	 * 检测账号
	 * @param lockIpNum
	 * @param errorNum
	 * @param ip
	 * @param userName
	 */
	public void checkAccount(Integer lockIpNum,String ip,String userName){
		try {
			lockIpNum += 1;
			if(lockIpNum >= 10){
				redis.opsForValue().set("LOGIN_"+ip, lockIpNum, 12, TimeUnit.HOURS);
				log.info("ip受限，来自用户:"+userName+",ip="+ip);
			}else{
				redis.opsForValue().set("LOGIN_"+ip, lockIpNum);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("checkAccount error", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> qqredirect(ParameterMap pm,HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		String code = pm.getString("code");
		String state = pm.getString("state");
		String grant_type = "authorization_code";
		ParameterMap param = new ParameterMap();
		param.put("code", code);
		param.put("state", state);
		param.put("grant_type", grant_type);
		param.put("client_id", qq_app_key);
		param.put("client_secret", qq_app_secret);
		param.put("redirect_uri", qq_redirect_url);
		try {
			// 获取access_token
			System.out.println("param=" + param);
			String url = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id="+qq_app_key
					+"&client_secret="+qq_app_secret+"&code="+code+"&state="+state+"&redirect_uri="+qq_redirect_url;
			String result = HttpUtils.getInstance().sendGetMethod(url, "UTF-8");
			System.out.println("result=" + result);
			ParameterMap acToken = MyUtil.getMapByUrlString(result);
			String access_token = acToken.getString("access_token");
//			String expires_in = acToken.getString("expires_in");
//			String refresh_token = acToken.getString("refresh_token");
			// 权限自动续期
			// pm.put("grant_type", "refresh_token");
			// pm.put("client_id", client_id);
			// pm.put("client_secret", client_secret);
			// pm.put("refresh_token", refresh_token);
			// HttpUtils.getInstance().sendGetMethod(tokenUrl, pm, "UTF-8");

			// 获取open_id
			param.put("access_token", access_token);
			String openString = HttpUtils.getInstance().sendGetMethod(qq_open_url, param, "UTF-8");
			System.out.println("openString=" + openString);
			openString = openString.replaceAll("callback", "");
			openString = openString.replaceAll("[(]", "");
			openString = openString.replaceAll("[)]", "");
			System.out.println("openString=" + openString);
			log.info("openString=" + openString);
			JSONObject open = new JSONObject(openString);
			String openId = open.getString("openid");
			// 获取用户信息
			param.put("register_type", "qq");
			param.put("third_uuid", openId);
			if(!checkIsExist(param,session)){
				param.put("access_token", access_token);
				param.put("oauth_consumer_key", qq_app_key);
				param.put("openid", openId);
				String userString = HttpUtils.getInstance().sendGetMethod(qq_user_url, param, "UTF-8");
				System.out.println("userString=" + userString);
				JSONObject user = new JSONObject(userString);
				System.out.println("user=" + user);
				saveQQUser(user,pm.getString("ip"),openId,session);
			}
			map.put("msg", "ok");
			map.put("status", "success");
		} catch (Exception e) {
			log.error("error:" + e.getMessage(), e);
			map.put("status", "failed");
			map.put("msg", "回调错误");
		}
		return map;
	}
	private void saveQQUser(JSONObject user,String ip,String openId,HttpSession session) {
		ParameterMap userpm = new ParameterMap();
		String sex = user.getString("gender");
		if("女".equalsIgnoreCase(sex)){
			sex="girl";
		}else{
			sex="boy";
		}
		String userPath = user.getString("figureurl_qq_2");//100x100 图片
		if(Tools.isEmpty(userPath)){
			userPath=user.getString("figureurl_qq_1");
		}
		try {
			userpm.put("username", "blog_" + MyUtil.random(8));
			userpm.put("password", "password_" + MyUtil.random(8));
			userpm.put("name", user.getString("nickname"));
			userpm.put("third_uuid", openId);
			userpm.put("register_type", "qq");
			userpm.put("sex", sex);
			userpm.put("locate", user.getString("city"));
			userpm.put("sign", "人懒连个性签名都没有!");
			userpm.put("ip", ip);
			userpm.put("status", "unlock");
			userpm.put("create_time", top.lrshuai.blog.util.DateUtil.getTime());
			String path = user_folder + MyUtil.random(8) + ".png";
			try {
				ImgUtil.saveImgByUrl(userPath, root_fold+ path);
				path = "/upload" + path;
			} catch (Exception e) {
				path = "/images/niming.png";
				log.error("errpt=" + e.getMessage(), e);
			}
			userpm.put("img", path);
			userDao.saveUser(userpm);
			session.setAttribute(Const.SESSION_USER, userpm);
		} catch (Exception e) {
			log.error("error", e);
		}
	}

	/**
	 * 检测是否存在了
	 * @param pm
	 * @return
	 */
	private boolean checkIsExist(ParameterMap pm,HttpSession session){
		ParameterMap userInfo = userDao.getUserInfo(pm);
		if (userInfo != null && userInfo.size() > 0) {
			session.setAttribute(Const.SESSION_USER, userInfo);
			return true;
		}
		return false;
	}
	

}
