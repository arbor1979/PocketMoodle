package com.dandian.pocketmoodle.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  �汾˵��: $id:$ <br/>
 *
 *  ����˵��: У��֪ͨ
 * 
 *  <br/>����˵��: 2014-4-16 ����2:05:53 shengguo  �����ļ�<br/>
 * 
 *  �޸���ʷ:<br/>
 *
 */
public class NoticesItem {
	private String TemplateName;
	private String title;
	private List<Notice> notices;
	
	public NoticesItem() {
		super();
	}
	
	public NoticesItem(JSONObject jo) {
		TemplateName = jo.optString("����ģ��");
		title = jo.optString("������ʾ");
		JSONArray joArr = jo.optJSONArray("֪ͨ��");
		notices = getNotices(joArr);
	}

	private List<Notice> getNotices(JSONArray joArr) {
		List<Notice> notices = new ArrayList<Notice>();
		for (int i = 0; i < joArr.length(); i++) {
			Notice notice = new Notice(joArr.optJSONObject(i));
			notices.add(notice);
		}
		return notices;
	}
	

	public String getTemplateName() {
		return TemplateName;
	}

	public void setTemplateName(String templateName) {
		TemplateName = templateName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Notice> getNotices() {
		return notices;
	}

	public void setNotices(List<Notice> notices) {
		this.notices = notices;
	}

}
