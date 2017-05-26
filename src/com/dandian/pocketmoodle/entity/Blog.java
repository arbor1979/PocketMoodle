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
		
		this.id = jo.optInt("���");
		this.poster = jo.optString("������");
		this.avater = jo.optString("ͷ��");
		this.posttime = jo.optString("����ʱ��");
		this.title = jo.optString("����");
		this.content = jo.optString("����");
		this.comments = jo.optString("������");
		this.detailUrl=jo.optString("������URL");
		this.mainImage=jo.optString("����ͼƬ");
		this.username=jo.optString("������ID");
		this.courseId=jo.optInt("�γ̱��");
		commentIconUrl=jo.optString("����ͼ��");
		commentTitle=jo.optString("���۱���");
		commentList=new ArrayList<Comment>();
		fujianList=new ArrayList<ImageItem>();
		org.json.JSONArray js=jo.optJSONArray("��������");
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
		org.json.JSONArray fj=jo.optJSONArray("����");
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