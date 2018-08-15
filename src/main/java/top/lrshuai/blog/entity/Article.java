package top.lrshuai.blog.entity;

import java.io.Serializable;

public class Article implements Serializable{
	private static final long serialVersionUID = 1L;
	private String article_id;
	private String user_id;
	private String title;
	private String content;
	private String text;
	private long browse_num;
	private long comment_num;
	private long praise_num;
	private long isrecommend;
	private long isdel;
	private long hot_num;
	private String create_time;
	public String getArticle_id() {
		return article_id;
	}
	public void setArticle_id(String article_id) {
		this.article_id = article_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public long getBrowse_num() {
		return browse_num;
	}
	public void setBrowse_num(long browse_num) {
		this.browse_num = browse_num;
	}
	public long getComment_num() {
		return comment_num;
	}
	public void setComment_num(long comment_num) {
		this.comment_num = comment_num;
	}
	public long getPraise_num() {
		return praise_num;
	}
	public void setPraise_num(long praise_num) {
		this.praise_num = praise_num;
	}
	public long getIsrecommend() {
		return isrecommend;
	}
	public void setIsrecommend(long isrecommend) {
		this.isrecommend = isrecommend;
	}
	public long getIsdel() {
		return isdel;
	}
	public void setIsdel(long isdel) {
		this.isdel = isdel;
	}
	public long getHot_num() {
		return hot_num;
	}
	public void setHot_num(long hot_num) {
		this.hot_num = hot_num;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public Article() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Article(String article_id, String user_id, String title, String content, String text, long browse_num,
			long comment_num, long praise_num, long isrecommend, long isdel, long hot_num, String create_time) {
		super();
		this.article_id = article_id;
		this.user_id = user_id;
		this.title = title;
		this.content = content;
		this.text = text;
		this.browse_num = browse_num;
		this.comment_num = comment_num;
		this.praise_num = praise_num;
		this.isrecommend = isrecommend;
		this.isdel = isdel;
		this.hot_num = hot_num;
		this.create_time = create_time;
	}
	@Override
	public String toString() {
		return "Article [article_id=" + article_id + ", user_id=" + user_id + ", title=" + title + ", content="
				+ content + ", text=" + text + ", browse_num=" + browse_num + ", comment_num=" + comment_num
				+ ", praise_num=" + praise_num + ", isrecommend=" + isrecommend + ", isdel=" + isdel + ", hot_num="
				+ hot_num + ", create_time=" + create_time + "]";
	}
	
	
}
