package top.lrshuai.blog.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import top.lrshuai.blog.es.dao.Dao;
import top.lrshuai.blog.plugin.Page;
import top.lrshuai.blog.service.IRobotService;
import top.lrshuai.blog.task.EmailThread;
import top.lrshuai.blog.util.Const;
import top.lrshuai.blog.util.DateUtil;
import top.lrshuai.blog.util.MyUtil;
import top.lrshuai.blog.util.ParameterMap;
import top.lrshuai.blog.util.ReturnModel;

@Service
public class RobotService implements IRobotService {

	private Logger log = Logger.getLogger(getClass());
	
	//索引名称（数据库名）
	private String index = "robot";
	
	//类型名称（表名）
	private String type = "brain"; 
	private String history_type = "history"; 
	
	@Autowired
	private MailService mailService;
	
	
	@Autowired
	private Dao dao;
	
	
	@Override
	public Map<String, Object> save(ParameterMap pm,HttpSession session) {
		String userId = "1";
		String isAuth = "0";
		try {
			ParameterMap user = (ParameterMap) session.getAttribute(Const.SESSION_USER);
			if(user == null){
				return ReturnModel.getModel("大哥你私自请求这个接口是犯法的哦", "failed", null);
			}else{
				isAuth = user.getString("auth_robot");
				if(!"1".equals(isAuth)){
					return ReturnModel.getModel("权限不够,可向管理员申请授权", "failed", null);
				}
				userId = user.getString("user_id");
			}
			String question = pm.getString("question");
			String answer = pm.getString("answer");
			question = HtmlUtils.htmlEscape(question);
			question = QueryParser.escape(question);
			answer = HtmlUtils.htmlEscape(answer);
			if(MyUtil.notEmpty(question) || MyUtil.notEmpty(answer)){
				pm.put("create_time", DateUtil.getTime());
				pm.put("user_id",userId);
				pm.remove("_t");
				pm.remove("rip");
				dao.save(pm, index, type);
			}
		} catch (Exception e) {
			log.error("错误", e);
			e.printStackTrace();
			return ReturnModel.getModel("服务好像出了点故障,麻烦联系管理员", "failed", null);
		}
		return ReturnModel.getModel("ok", "success", null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> answer(ParameterMap pm,HttpSession session) {
		try {
			List<Map<String, Object>> list = (List<Map<String, Object>>) dao.multiMatchQuery(pm, index, type,null);
			Object result = "";
			if(list != null && list.size() > 0){
				result = list.get(0).get("answer");
			}else{
				String question = pm.getString("content");
				result =question;
				//当没有数据的时候，保存进历史记录
				try {
					//特殊符号转义
					question = QueryParser.escape(question);
					String userId = "1";
					ParameterMap user = (ParameterMap) session.getAttribute(Const.SESSION_USER);
					if(user != null){
						userId = user.getString("user_id");
					}
					ParameterMap history = new ParameterMap();
					history.put("question", question);
					history.put("user_id", userId);
					history.put("ip", pm.getString("rip"));
					history.put("is_read", "0");
					history.put("create_time", DateUtil.getTime());
					dao.save(history, index, history_type);
					
					String isSave = (String) session.getAttribute(Const.SESSION_SAVE_QUESTION);
					if(isSave == null || "".equals(isSave)){
						EmailThread et = new EmailThread(mailService,"1006059906@qq.com", "机器人有新问题",question);
						new Thread(et).start();
						session.setAttribute(Const.SESSION_SAVE_QUESTION, "1");
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage(), e);
				}
			}
			result = result.toString().replaceAll("\\[cqname\\]", "我");
			result = result.toString().replaceAll("\\[name\\]", "你");
			result = result.toString().replaceAll("\\[bq[0-9]*\\]", "");
			return ReturnModel.getModel("ok", "success", result);
		} catch (Exception e) {
			log.error("错误", e);
			e.printStackTrace();
			return ReturnModel.getModel("服务好像出了点故障,麻烦联系管理员", "failed", null);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Object> getBrainList(ParameterMap pm) {
		Page page = new Page();
		String pageNo =pm.getString("page_no");
		String pageSize =pm.getString("page_size");
		try {
			if(MyUtil.notEmpty(pageNo)){
				page.setCurrentPage(Integer.parseInt(pageNo));
			}
			if(MyUtil.notEmpty(pageSize)){
				page.setShowCount(Integer.parseInt(pageSize));
			}else{
				page.setShowCount(10);
			}
			String keyword = pm.getString("kw");
			keyword = QueryParser.escape(keyword);
			ParameterMap mm = new ParameterMap();
			if(MyUtil.notEmpty(keyword)){
				mm.put("must_key", "question");
				mm.put("question_value", keyword);
			}else{
				//默认列表按时间排序
				mm.put("sort", "time");
			}
			List<Map<String,Object>> list = null;
			if(pm.getString("type") != null && !pm.getString("type").equals("")){
				list = (List<Map<String, Object>>) dao.query(mm, index, pm.getString("type"), page);
			}else{
				list = (List<Map<String, Object>>) dao.query(mm, index, type, page);
			}
			if(list == null){
				list = new ArrayList<>();
			}
			if(MyUtil.notEmpty(pageNo)){
				page.setCurrentPage(Integer.parseInt(pageNo));
			}
			return ReturnModel.getModel("ok", "success", list, page);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
			return ReturnModel.getErrorModel(e.getMessage());
		}
	}
	
	@Override
	public Map<String, Object> find(ParameterMap pm) {
		try {
			String id = pm.getString("id");
			Map<String,Object> map = null;
			String tagType = pm.getString("type");
			if(tagType != null && !"".equals(tagType)){
				map = dao.find(id, index, tagType);
			}else{
				map = dao.find(id, index, type);
			}
			return ReturnModel.getModel("ok", "success", map);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return ReturnModel.getErrorModel("服务器错误");
	}
	
	@Override
	public Map<String, Object> del(ParameterMap pm,HttpSession session) {
		String isAuth = "0";
		try {
			ParameterMap user = (ParameterMap) session.getAttribute(Const.SESSION_USER);
			if(user == null){
				return ReturnModel.getModel("大哥你私自请求这个接口是犯法的哦", "failed", null);
			}else{
				isAuth = user.getString("auth_robot");
				if(!"1".equals(isAuth)){
					return ReturnModel.getModel("权限不够,可向管理员申请授权", "failed", null);
				}
			}
			String id = pm.getString("id");
			
			String tagType = pm.getString("type");
			int issuccess=0;
			if(tagType != null && !"".equals(tagType)){
				issuccess = dao.deltele(id, index, tagType);
			}else{
				issuccess = dao.deltele(id, index, type);
			}
			return ReturnModel.getModel("ok", "success", issuccess);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return ReturnModel.getErrorModel("服务器错误");
	}
	
	@Override
	public Map<String, Object> update(ParameterMap pm,HttpSession session) {
		String isAuth = "0";
		try {
			ParameterMap user = (ParameterMap) session.getAttribute(Const.SESSION_USER);
			if(user == null){
				return ReturnModel.getModel("大哥你私自请求这个接口是犯法的哦", "failed", null);
			}else{
				isAuth = user.getString("auth_robot");
				if(!"1".equals(isAuth)){
					return ReturnModel.getModel("权限不够,可向管理员申请授权", "failed", null);
				}
			}
			pm.remove("_t");
			pm.remove("rip");
			System.out.println("pm="+pm);
			String question = pm.getString("question");
			question = QueryParser.escape(question);
			pm.put("question", question);
			int issuccess = dao.update(pm, index, type);
			return ReturnModel.getModel("ok", "success", issuccess);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return ReturnModel.getErrorModel("服务器错误");
	}
	

}
