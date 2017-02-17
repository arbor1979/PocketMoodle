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
		
		this.id = jo.optInt("编号");
		this.username=jo.optString("用户姓名");
		this.avater = jo.optString("头像");
		this.posttime = jo.optString("时间");
		this.content = jo.optString("内容");
		this.deleteUrl=jo.optString("删除URL");
		this.usercode=jo.optString("用户唯一码");
		this.reply=jo.optString("回复");
		this.replyUrl=jo.optString("回复URL");
		
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