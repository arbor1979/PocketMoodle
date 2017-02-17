package com.dandian.pocketmoodle.entity;

import org.json.JSONArray;
import org.json.JSONObject;

public class NoticesDetail {
	private String title;
	private String time;
	private String imageUrl;
	private String content;
	private JSONArray fujian;
	private JSONArray tupian;
	private String rightBtn;
	private String rightBtnUrl;
	private String newWindowTitle;
	public NoticesDetail(JSONObject jo) {
		this.title = jo.optString("����");
		this.time = jo.optString("�ڶ���");
		this.imageUrl = jo.optString("�ڶ���ͼƬ��URL");
		this.content = jo.optString("֪ͨ����");
		fujian=jo.optJSONArray("����");
		tupian=jo.optJSONArray("ͼƬ����");
		rightBtn=jo.optString("���ϰ�ť");
		rightBtnUrl=jo.optString("���ϰ�ťURL");
		newWindowTitle=jo.optString("�´��ڱ���");
	}

	public String getNewWindowTitle() {
		return newWindowTitle;
	}

	public void setNewWindowTitle(String newWindowTitle) {
		this.newWindowTitle = newWindowTitle;
	}

	public String getRightBtn() {
		return rightBtn;
	}

	public void setRightBtn(String rightBtn) {
		this.rightBtn = rightBtn;
	}

	public String getRightBtnUrl() {
		return rightBtnUrl;
	}

	public void setRightBtnUrl(String rightBtnUrl) {
		this.rightBtnUrl = rightBtnUrl;
	}

	public JSONArray getTupian() {
		return tupian;
	}

	public void setTupian(JSONArray tupian) {
		this.tupian = tupian;
	}

	public JSONArray getFujian() {
		return fujian;
	}

	public void setFujian(JSONArray fujian) {
		this.fujian = fujian;
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

}
