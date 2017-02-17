package com.dandian.pocketmoodle.entity;

import java.io.Serializable;

import org.json.JSONObject;



public class Comment implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4143006483867293979L;

	private int id;
	private String username;
	private String avater;
	private String content;
	private String posttime;
	private String deleteUrl;
	private String usercode;
	private String reply;
	private String replyUrl;
	public Comment()
	{
		
	}
	public Comment(JSONObject jo) {
		
		this.id = jo.optInt("���");
		this.username=jo.optString("�û�����");
		this.avater = jo.optString("ͷ��");
		this.posttime = jo.optString("ʱ��");
		this.content = jo.optString("����");
		this.deleteUrl=jo.optString("ɾ��URL");
		this.usercode=jo.optString("�û�Ψһ��");
		this.reply=jo.optString("�ظ�");
		this.replyUrl=jo.optString("�ظ�URL");
		
	}
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	public String getReplyUrl() {
		return replyUrl;
	}
	public void setReplyUrl(String replyUrl) {
		this.replyUrl = replyUrl;
	}
	public String getUsercode() {
		return usercode;
	}
	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAvater() {
		return avater;
	}
	public void setAvater(String avater) {
		this.avater = avater;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPosttime() {
		return posttime;
	}
	public void setPosttime(String posttime) {
		this.posttime = posttime;
	}
	public String getDeleteUrl() {
		return deleteUrl;
	}
	public void setDeleteUrl(String deleteUrl) {
		this.deleteUrl = deleteUrl;
	}
	
}