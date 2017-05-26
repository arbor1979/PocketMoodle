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
 *  ����˵��: �ɼ�
 * 
 *  <br/>����˵��: 2014-4-16 ����6:41:34 shengguo  �����ļ�<br/>
 * 
 *  �޸���ʷ:<br/>
 *
 */
public class AchievementItem {
	private String templateName;
	private String title;
	private List<Achievement> achievements;
	private String rightButton;
	private String rightButtonURL;
	private String summary;
	private String rules;
	private String grade;
	private String bottomButton;
	private String bottomButtonURL;
	public AchievementItem(JSONObject jo) {
		templateName = jo.optString("����ģ��");
		title = jo.optString("������ʾ");
		achievements = new ArrayList<Achievement>();
		JSONArray joa = jo.optJSONArray("�ɼ���ֵ");
		if(joa!=null)
		{
			for (int i = 0; i < joa.length(); i++) {
				Achievement a = new Achievement(joa.optJSONObject(i));
				achievements.add(a);
			}
		}
		rightButton=jo.optString("���ϰ�ť");
		rightButtonURL=jo.optString("���ϰ�ťURL");
		summary=jo.optString("���");
		rules=jo.optString("����").replaceAll("<br>", "\n");
		grade=jo.optString("����").replaceAll("<br>", "\n");
		bottomButton=jo.optString("�ײ���ť");
		bottomButtonURL=jo.optString("�ײ���ťURL");
	}

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules.replaceAll("<br>", "\n");;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade.replaceAll("<br>", "\n");
	}

	public String getBottomButton() {
		return bottomButton;
	}

	public void setBottomButton(String bottomButton) {
		this.bottomButton = bottomButton;
	}

	public String getBottomButtonURL() {
		return bottomButtonURL;
	}

	public void setBottomButtonURL(String bottomButtonURL) {
		this.bottomButtonURL = bottomButtonURL;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getRightButton() {
		return rightButton;
	}

	public void setRightButton(String rightButton) {
		this.rightButton = rightButton;
	}

	public String getRightButtonURL() {
		return rightButtonURL;
	}

	public void setRightButtonURL(String rightButtonURL) {
		this.rightButtonURL = rightButtonURL;
	}

	public class Achievement {
		private String id;// ���
		private String icon;// ͼ��
		private String icon_link;// ͼ��
		private String title;// ����
		private String total;// �ܷ�
		private String rank;// ����
		private String detailUrl;// �����ַ
	    private String templateName;
	    private String templateGrade;
	    private String theColor;
	    private String detailTitle;
	    private String left,center,right;
		public Achievement(JSONObject jo) {
			id = jo.optString("���");
			icon = jo.optString("ͼ��");
			icon_link=jo.optString("ͼ������");
			title = jo.optString("��һ��");
			total = jo.optString("�ڶ�����");
			rank = jo.optString("�ڶ�����");
			detailUrl = jo.optString("������URL");
			templateName = jo.optString("ģ��");
			templateGrade = jo.optString("ģ�弶��");
			theColor= jo.optString("��ɫ");
			detailTitle=jo.optString("Ŀ�����");
			left=jo.optString("���");
			center=jo.optString("�м�").replaceAll("<br>", "\n");
			right=jo.optString("�ұ�");
		}

		public String getLeft() {
			return left;
		}

		public void setLeft(String left) {
			this.left = left;
		}

		public String getCenter() {
			return center;
		}

		public void setCenter(String center) {
			this.center = center.replaceAll("<br>", "\n");
		}

		public String getRight() {
			return right;
		}

		public void setRight(String right) {
			this.right = right;
		}

		public String getDetailTitle() {
			return detailTitle;
		}

		public void setDetailTitle(String detailTitle) {
			this.detailTitle = detailTitle;
		}

		public String getIcon_link() {
			return icon_link;
		}

		public void setIcon_link(String icon_link) {
			this.icon_link = icon_link;
		}

		public String getTheColor() {
			return theColor;
		}

		public void setTheColor(String theColor) {
			this.theColor = theColor;
		}

		public String getTemplateName() {
			return templateName;
		}

		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}

		public String getTemplateGrade() {
			return templateGrade;
		}

		public void setTemplateGrade(String templateGrade) {
			this.templateGrade = templateGrade;
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

		public String getTotal() {
			return total;
		}

		public void setTotal(String total) {
			this.total = total;
		}

		public String getRank() {
			return rank;
		}

		public void setRank(String rank) {
			this.rank = rank;
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

	public List<Achievement> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements) {
		this.achievements = achievements;
	}
	
}
