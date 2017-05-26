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
 *  功能说明: 成绩
 * 
 *  <br/>创建说明: 2014-4-16 下午6:41:34 shengguo  创建文件<br/>
 * 
 *  修改历史:<br/>
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
		templateName = jo.optString("适用模板");
		title = jo.optString("标题显示");
		achievements = new ArrayList<Achievement>();
		JSONArray joa = jo.optJSONArray("成绩数值");
		if(joa!=null)
		{
			for (int i = 0; i < joa.length(); i++) {
				Achievement a = new Achievement(joa.optJSONObject(i));
				achievements.add(a);
			}
		}
		rightButton=jo.optString("右上按钮");
		rightButtonURL=jo.optString("右上按钮URL");
		summary=jo.optString("简介");
		rules=jo.optString("规则").replaceAll("<br>", "\n");
		grade=jo.optString("评分").replaceAll("<br>", "\n");
		bottomButton=jo.optString("底部按钮");
		bottomButtonURL=jo.optString("底部按钮URL");
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
		private String id;// 编号
		private String icon;// 图标
		private String icon_link;// 图标
		private String title;// 标题
		private String total;// 总分
		private String rank;// 排名
		private String detailUrl;// 详情地址
	    private String templateName;
	    private String templateGrade;
	    private String theColor;
	    private String detailTitle;
	    private String left,center,right;
		public Achievement(JSONObject jo) {
			id = jo.optString("编号");
			icon = jo.optString("图标");
			icon_link=jo.optString("图标链接");
			title = jo.optString("第一行");
			total = jo.optString("第二行左");
			rank = jo.optString("第二行右");
			detailUrl = jo.optString("内容项URL");
			templateName = jo.optString("模板");
			templateGrade = jo.optString("模板级别");
			theColor= jo.optString("颜色");
			detailTitle=jo.optString("目标标题");
			left=jo.optString("左边");
			center=jo.optString("中间").replaceAll("<br>", "\n");
			right=jo.optString("右边");
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
