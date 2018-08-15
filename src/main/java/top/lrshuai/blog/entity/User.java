package top.lrshuai.blog.entity;

/**
 * 用户实体
 * 
 * @author tyro
 *
 */
public class User {
	private String user_id; // 用户ID
	private String username; // 用户名
	private String password;
	private String name; // 用户昵称
	private String img; // 用户头像
	private String sex; // 用户性别
	private String sign; // 用户签名
	private String last_login; // 最后登录时间
	private String ip; // 用户登录ip地址
	private String status; // 状态
	private String email; // 邮箱
	private String locate; // 位置
	private String register_type; // 注册类型
	private String third_uuid; // 第三方注册Id
	private String create_time; // 注册时间
	private String edit_tool;	//常用的编辑器
	
	

	public String getEdit_tool() {
		return edit_tool;
	}

	public void setEdit_tool(String edit_tool) {
		this.edit_tool = edit_tool;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getLast_login() {
		return last_login;
	}

	public void setLast_login(String last_login) {
		this.last_login = last_login;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLocate() {
		return locate;
	}

	public void setLocate(String locate) {
		this.locate = locate;
	}

	public String getRegister_type() {
		return register_type;
	}

	public void setRegister_type(String register_type) {
		this.register_type = register_type;
	}

	public String getThird_uuid() {
		return third_uuid;
	}

	public void setThird_uuid(String third_uuid) {
		this.third_uuid = third_uuid;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", username=" + username + ", password=" + password + ", name=" + name
				+ ", img=" + img + ", sex=" + sex + ", sign=" + sign + ", last_login=" + last_login + ", ip=" + ip
				+ ", status=" + status + ", email=" + email + ", locate=" + locate + ", register_type=" + register_type
				+ ", third_uuid=" + third_uuid + ", create_time=" + create_time + ", edit_tool=" + edit_tool + "]";
	}
	
}
