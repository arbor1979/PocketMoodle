package com.dandian.pocketmoodle.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;



public class Blog implements Serializable{
	public List<Comment> getCommentList() {
		return commentList;
	}
	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -4143006483867293979L;

	private int id;

	private String title;

	private String posttime;

	private String avater;

	private String content;
	
	private String detailUrl;
	private String mainImage;
	private String username;
	private List<Comment> commentList;
	private List<ImageItem> fujianList;
	
	private String commentIconUrl;
	private String commentTitle;
	private int courseId;
	public String getCommentIconUrl() {
		return commentIconUrl;
	}
	public void setCommentIconUrl(String commentIconUrl) {
		this.commentIconUrl = commentIconUrl;
	}
	public String getCommentTitle() {
		return commentTitle;
	}
	public void setCommentTitle(String commentTitle) {
		this.commentTitle = commentTitle;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPosttime() {
		return posttime;
	}
	public void setPosttime(String posttime) {
		this.posttime = posttime;
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
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	private String comments;

	private String poster;
	public Blog()
	{
		commentList=new ArrayList<Comment>();
		fujianList=new ArrayList<ImageItem>();
	}
	public Blog(JSONObject jo) {
		
		this.id = jo.optInt("编号");
		this.poster = jo.optString("发布者");
		this.avater = jo.optString("头像");
		this.posttime = jo.optString("发布时间");
		this.title = jo.optString("标题");
		this.content = jo.optString("内容");
		this.comments = jo.optString("评论数");
		this.detailUrl=jo.optString("内容项URL");
		this.mainImage=jo.optString("主题图片");
		this.username=jo.optString("发布者ID");
		this.courseId=jo.optInt("课程编号");
		commentIconUrl=jo.optString("评论图标");
		commentTitle=jo.optString("评论标题");
		commentList=new ArrayList<Comment>();
		fujianList=new ArrayList<ImageItem>();
		org.json.JSONArray js=jo.optJSONArray("评论数据");
		if(js!=null && js.length()>0)
		{
			for(int i=0;i<js.length();i++)
			{
				try {
					Comment cm=new Comment(js.getJSONObject(i));
					if(cm!=null)
						commentList.add(cm);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		org.json.JSONArray fj=jo.optJSONArray("附件");
		if(fj!=null && fj.length()>0)
		{
			for(int i=0;i<fj.length();i++)
			{
				try {
					ImageItem cm=new ImageItem(fj.getJSONObject(i));
					if(cm!=null)
						fujianList.add(cm);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
	public List<ImageItem> getFujianList() {
		return fujianList;
	}
	public void setFujianList(List<ImageItem> fujianList) {
		this.fujianList = fujianList;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMainImage() {
		return mainImage;
	}
	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}
	public String getDetailUrl() {
		return detailUrl;
	}
	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	
}