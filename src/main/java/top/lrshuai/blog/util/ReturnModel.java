package top.lrshuai.blog.util;

import java.util.HashMap;


public class ReturnModel{

	private static HashMap<String,Object> model=null;
	
	private ReturnModel() {}
	
	public static HashMap<String, Object> getModel(String msg,String status,Object data){
		if(model == null){
			model = new HashMap<>();
		}
		if(Tools.notEmpty(msg)){
			model.put("msg", msg);
		}
		if(Tools.notEmpty(status)){
			model.put("status", status);
		}
		if(data != null){
			model.put("data", data);
		}
		return model;
	}
	public static HashMap<String, Object> getModel(String msg,String status,Object data,Object page){
		if(model == null){
			model = new HashMap<>();
		}
		if(Tools.notEmpty(msg)){
			model.put("msg", msg);
		}
		if(Tools.notEmpty(status)){
			model.put("status", status);
		}
		if(data != null){
			model.put("data", data);
		}
		if(page != null){
			model.put("page", page);
		}
		return model;
	}
	
	public static HashMap<String, Object> getErrorModel(String... error){
		if(model == null){
			model = new HashMap<>();
		}
		model.put("status", "failed");
		model.put("msg", "你请求的是一个冒牌接口");
		if(error != null){
			model.put("msg", error);
		}
		return model;
	}
	public static HashMap<String, Object> getNotAuthModel(){
		if(model == null){
			model = new HashMap<>();
		}
		model.put("status", "notauth");
		model.put("msg", "你权限不足");
		return model;
	}
}
