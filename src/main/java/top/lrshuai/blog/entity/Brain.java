package top.lrshuai.blog.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

@Component
public class Brain {
	
	@Id
	private String id;
	private String answer;
	private String question;
	private String user_id;
	private Date create_time;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public Brain() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Brain(String id, String question,String answer, String user_id, Date create_time) {
		super();
		this.id = id;
		this.answer = answer;
		this.question = question;
		this.user_id = user_id;
		this.create_time = create_time;
	}
	@Override
	public String toString() {
		return "Brain [id=" + id + ", answer=" + answer + ", question=" + question + ", user_id=" + user_id
				+ ", create_time=" + create_time + "]";
	}
	
	
	
}
