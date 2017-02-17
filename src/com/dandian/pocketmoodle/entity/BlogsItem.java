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
public class BlogsItem {
	private String TemplateName;
	private String title;
	private List<Blog> notices;
	private String rightButton;
	private String rightButtonUrl;
	public BlogsItem() {
		super();
	}
	
	public BlogsItem(JSONObject jo) {
		TemplateName = jo.optString("����ģ��");
		title = jo.optString("������ʾ");
		JSONArray joArr = jo.optJSONArray("֪ͨ��");
		rightButton=jo.optString("���ϰ�ť");
		rightButtonUrl=jo.optString("���ϰ�ťURL");
		notices = getNotices(joArr);
	}

	public String getRightButton() {
		return rightButton;
	}

	public void setRightButton(String rightButton) {
		this.rightButton = rightButton;
	}

	public String getRightButtonUrl() {
		return rightButtonUrl;
	}

	public void setRightButtonUrl(String rightButtonUrl) {
		this.rightButtonUrl = rightButtonUrl;
	}

	private List<Blog> getNotices(JSONArray joArr) {
		List<Blog> notices = new ArrayList<Blog>();
		for (int i = 0; i < joArr.length(); i++) {
			Blog notice = new Blog(joArr.optJSONObject(i));
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

	public List<Blog> getNotices() {
		return notices;
	}

	public void setNotices(List<Blog> notices) {
		this.notices = notices;
	}

}
