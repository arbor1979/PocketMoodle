package com.dandian.pocketmoodle.entity;

import java.io.Serializable;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="ContactsMember")
public class Notice implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4143006483867293979L;
	@DatabaseField(generatedId= true)
	private int autoid;
	@DatabaseField(generatedId= false)
	private int id;
	@DatabaseField
	private String title;
	@DatabaseField
	private String time;
	@DatabaseField
	private String imageUrl;
	@DatabaseField
	private String content;
	@DatabaseField
	private String endText;
	@DatabaseField
	private String endUrl;
	@DatabaseField
	private String newsType;
	@DatabaseField
	private String ifread;
	@DatabaseField
	private String userNumber;
	public Notice()
	{
		
	}
	public Notice(JSONObject jo) {
		
		this.id = jo.optInt("���");
		this.title = jo.optString("��һ������");
		this.time = jo.optString("��һ���ұ�");
		this.imageUrl = jo.optString("�ڶ���ͼƬ��URL");
		this.content = jo.optString("֪ͨ����");
		this.endText = jo.optString("���±�һ���ı�");
		this.endUrl = jo.optString("���±�һ��URL");
		
	}

	public String getNewsType() {
		return newsType;
	}

	public void setNewsType(String newsType) {
		this.newsType = newsType;
	}

	public String getIfread() {
		return ifread;
	}

	public void setIfread(String ifread) {
		this.ifread = ifread;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getEndText() {
		return endText;
	}

	public void setEndText(String endText) {
		this.endText = endText;
	}

	public String getEndUrl() {
		return endUrl;
	}

	public void setEndUrl(String endUrl) {
		this.endUrl = endUrl;
	}
}