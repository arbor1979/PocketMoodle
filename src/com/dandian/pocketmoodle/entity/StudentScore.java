package com.dandian.pocketmoodle.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 成绩查询
 * 
 *  <br/>创建说明: 2013-11-30 下午4:04:56 yanzy  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
@DatabaseTable(tableName = "StudentScore")
public class StudentScore implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(id = true)
	private String id;
	@DatabaseField
	private String studentID; //学号
	@DatabaseField
	private String scoreTitle; //成绩标题
	@DatabaseField
	private String avgScore; //平均�?
	@DatabaseField
	private String totalScore; //总分
	@DatabaseField
	private String scoreItem;
	
	
//	private List<StudentScoreItem> studentScoreItemList;
	
	public StudentScore() {

	}

	public StudentScore(JSONObject jo) {
		this.studentID = jo.optString("学号");
		this.scoreTitle = jo.optString("标题");
		this.avgScore = jo.optString("平均�?");
		this.totalScore = jo.optString("总分");
		this.scoreItem = jo.optString("成绩");
//		studentScoreItemList = StudentScoreItem.toList(jo.optJSONArray("成绩"));
	}
	public StudentScore(net.minidev.json.JSONObject jo) {
		this.studentID = String.valueOf(jo.get("学号"));
		this.scoreTitle = String.valueOf(jo.get("标题"));
		this.avgScore = String.valueOf(jo.get("平均�?"));
		this.totalScore = String.valueOf(jo.get("总分"));
		this.scoreItem = String.valueOf(jo.get("成绩"));
//		studentScoreItemList = StudentScoreItem.toList(jo.optJSONArray("成绩"));
	}
	
	public static List<StudentScore> toList(JSONArray ja) {
		List<StudentScore> result = new ArrayList<StudentScore>();
		StudentScore info = null;
		
		if (ja != null && ja.length() > 0) {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				info = new StudentScore(jo);
				result.add(info);
			}
			return result;
		}else{
			System.out.println("没有StudentScore数据");
			return null;
		}
	}
	
	public static List<StudentScore> toList(net.minidev.json.JSONArray ja) {
		List<StudentScore> result = new ArrayList<StudentScore>();
		StudentScore info = null;
		
		if (ja != null && ja.size() > 0) {
			for (int i = 0; i < ja.size(); i++) {
				net.minidev.json.JSONObject jo = (net.minidev.json.JSONObject)ja.get(i);
				info = new StudentScore(jo);
				result.add(info);
			}
			return result;
		}else{
			System.out.println("没有StudentScore数据");
			return null;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	public String getScoreTitle() {
		return scoreTitle;
	}

	public void setScoreTitle(String scoreTitle) {
		this.scoreTitle = scoreTitle;
	}

	public String getAvgScore() {
		return avgScore;
	}

	public void setAvgScore(String avgScore) {
		this.avgScore = avgScore;
	}

	public String getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(String totalScore) {
		this.totalScore = totalScore;
	}

//	public List<StudentScoreItem> getStudentScoreItemList() {
//		return studentScoreItemList;
//	}
//
//	public void setStudentScoreItemList(List<StudentScoreItem> studentScoreItemList) {
//		this.studentScoreItemList = studentScoreItemList;
//	}

	public String getScoreItem() {
		return scoreItem;
	}

	public void setScoreItem(String scoreItem) {
		this.scoreItem = scoreItem;
	}

	
}
