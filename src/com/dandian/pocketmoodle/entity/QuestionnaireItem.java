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
 *  ����˵��: �����ʾ�
 * 
 *  <br/>����˵��: 2014-4-16 ����6:55:15 shengguo  �����ļ�<br/>
 * 
 *  �޸���ʷ:<br/>
 *
 */
public class QuestionnaireItem {
	private String templateName;
	private String title;
	private List<Question> questions;
	public QuestionnaireItem(JSONObject jo){
		templateName=jo.optString("����ģ��");
		title=jo.optString("������ʾ");
		questions=new ArrayList<Question>();
		JSONArray joq=jo.optJSONArray("�����ʾ���ֵ");
		for (int i = 0; i < joq.length(); i++) {
			Question q=new Question(joq.optJSONObject(i));
			questions.add(q);
		}
	}
	public class Question {
		private String id;
		private String icon;
		private String title;
		private String status;
		private String date;
		private String detailUrl;
		private String autoClose;
		public String getAutoClose() {
			return autoClose;
		}

		public void setAutoClose(String autoClose) {
			this.autoClose = autoClose;
		}

		public Question(JSONObject jo) {
			id = jo.optString("���");
			icon = jo.optString("ͼ��");
			title = jo.optString("��һ��");
			status = jo.optString("�ڶ���֮״̬");
			date = jo.optString("�ڶ���֮����");
			detailUrl = jo.optString("������URL");
			autoClose=jo.optString("�����ر�");
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getDetailUrl() {
			return detailUrl;
		}

		public void setDetailUrl(String detailUrl) {
			this.detailUrl = detailUrl;
		}
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Question> getQuestions() {
		return questions;
	}
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
}

