package com.dandian.pocketmoodle.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 成绩详情
 * 
 *  <br/>创建说明: 2014-4-17 下午5:07:31 shengguo  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class AchievementDetail {
	private String title;
	private String submitBtn;
	private String submitBtnUrl;
	private String submitTarget;
	private int leftWeight;//占据宽度的百分比
	private int rightWeight;//占据宽度的百分比
	private String loginUrl;
	private String submitConfirm;
	private List<Achievement> achievements;

	public AchievementDetail(JSONObject jo) {
		title = jo.optString("标题显示");
		leftWeight = jo.optInt("左边宽度");
		rightWeight = jo.optInt("右边宽度");
		submitBtn=jo.optString("右上按钮");
		submitBtnUrl=jo.optString("右上按钮URL");
		submitTarget=jo.optString("右上按钮Submit");
		submitConfirm=jo.optString("右上按钮Confirm");
		loginUrl=jo.optString("登录地址");
		achievements = new ArrayList<Achievement>();
		JSONArray joa = jo.optJSONArray("成绩数值");
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
			subject = jo.optString("左边");
			fraction = jo.optString("右边");
			lat=jo.optDouble("lat");
			lon=jo.optDouble("lon");
			hiddenBtn=jo.optString("隐藏按钮");
			hiddenBtnURL=jo.optString("隐藏按钮URL");
			htmlText=jo.optString("htmlText");
			url=jo.optString("URL");
			templateName=jo.optString("模板");
			templateGrade=jo.optString("模板级别");
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
