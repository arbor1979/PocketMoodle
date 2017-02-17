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
 *  ����˵��: �ɼ�����
 * 
 *  <br/>����˵��: 2014-4-17 ����5:07:31 shengguo  �����ļ�<br/>
 * 
 *  �޸���ʷ:<br/>
 *
 */
public class AchievementDetail {
	private String title;
	private String submitBtn;
	private String submitBtnUrl;
	private String submitTarget;
	private int leftWeight;//ռ�ݿ�ȵİٷֱ�
	private int rightWeight;//ռ�ݿ�ȵİٷֱ�
	private String loginUrl;
	private String submitConfirm;
	private List<Achievement> achievements;

	public AchievementDetail(JSONObject jo) {
		title = jo.optString("������ʾ");
		leftWeight = jo.optInt("��߿��");
		rightWeight = jo.optInt("�ұ߿��");
		submitBtn=jo.optString("���ϰ�ť");
		submitBtnUrl=jo.optString("���ϰ�ťURL");
		submitTarget=jo.optString("���ϰ�ťSubmit");
		submitConfirm=jo.optString("���ϰ�ťConfirm");
		loginUrl=jo.optString("��¼��ַ");
		achievements = new ArrayList<Achievement>();
		JSONArray joa = jo.optJSONArray("�ɼ���ֵ");
		for (int i = 0; i < joa.length(); i++) {
			Achievement a = new Achievement(joa.optJSONObject(i));
			achievements.add(a);
		}
	}

	

	public String getSubmitConfirm() {
		return submitConfirm;
	}



	public void setSubmitConfirm(String submitConfirm) {
		this.submitConfirm = submitConfirm;
	}



	public String getLoginUrl() {
		return loginUrl;
	}



	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}



	public String getSubmitTarget() {
		return submitTarget;
	}



	public void setSubmitTarget(String submitTarget) {
		this.submitTarget = submitTarget;
	}



	public String getSubmitBtn() {
		return submitBtn;
	}



	public void setSubmitBtn(String submitBtn) {
		this.submitBtn = submitBtn;
	}



	public String getSubmitBtnUrl() {
		return submitBtnUrl;
	}



	public void setSubmitBtnUrl(String submitBtnUrl) {
		this.submitBtnUrl = submitBtnUrl;
	}



	public int getLeftWeight() {
		return leftWeight;
	}

	public void setLeftWeight(int leftWeight) {
		this.leftWeight = leftWeight;
	}

	public int getRightWeight() {
		return rightWeight;
	}

	public void setRightWeight(int rightWeight) {
		this.rightWeight = rightWeight;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public List<Achievement> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements) {
		this.achievements = achievements;
	}

	public class Achievement {
		public String getHiddenBtn() {
			return hiddenBtn;
		}

		public void setHiddenBtn(String hiddenBtn) {
			this.hiddenBtn = hiddenBtn;
		}

		public String getHiddenBtnURL() {
			return hiddenBtnURL;
		}

		public void setHiddenBtnURL(String hiddenBtnURL) {
			this.hiddenBtnURL = hiddenBtnURL;
		}

		private double lat;
		private double lon;
		private String subject;
		private String hiddenBtn;
		private String hiddenBtnURL;
		private String htmlText;
		private String url;
		private String templateName;
		private String templateGrade;
		public double getLat() {
			return lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public double getLon() {
			return lon;
		}

		public void setLon(double lon) {
			this.lon = lon;
		}

		private String fraction;

		public Achievement(JSONObject jo) {
			subject = jo.optString("���");
			fraction = jo.optString("�ұ�");
			lat=jo.optDouble("lat");
			lon=jo.optDouble("lon");
			hiddenBtn=jo.optString("���ذ�ť");
			hiddenBtnURL=jo.optString("���ذ�ťURL");
			htmlText=jo.optString("htmlText");
			url=jo.optString("URL");
			templateName=jo.optString("ģ��");
			templateGrade=jo.optString("ģ�弶��");
		}

		public String getTemplateName() {
			return templateName;
		}

		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}

		public String getUrl() {
			return url;
		}

		public String getTemplateGrade() {
			return templateGrade;
		}

		public void setTemplateGrade(String templateGrade) {
			this.templateGrade = templateGrade;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getHtmlText() {
			return htmlText;
		}

		public void setHtmlText(String htmlText) {
			this.htmlText = htmlText;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getFraction() {
			return fraction;
		}

		public void setFraction(String fraction) {
			this.fraction = fraction;
		}
	}
}
