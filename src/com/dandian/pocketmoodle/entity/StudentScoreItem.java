package com.dandian.pocketmoodle.entity;

import java.io.Serializable;
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
 *  功能说明: 科目分数
 * 
 *  <br/>创建说明: 2013-11-30 下午4:11:38 yanzy  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
//@DatabaseTable(tableName = "StudentScoreItem")
public class StudentScoreItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	@DatabaseField(id = true)
	private String id;
//	@DatabaseField
	private String studentID; //学号
//	@DatabaseField
	private String name; //科目名称
//	@DatabaseField
	private String score; //分数
	

	public StudentScoreItem() {
		
	}

	public StudentScoreItem(JSONObject jo) {
		this.studentID = jo.optString("学号");
		this.name = jo.optString("名称");
		this.score = jo.optString("分�??");
	}
	
	public static List<StudentScoreItem> toList(JSONArray ja) {
		List<StudentScoreItem> result = new ArrayList<StudentScoreItem>();
		StudentScoreItem info = null;
		
		if (ja != null && ja.length() > 0) {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				info = new StudentScoreItem(jo);
				result.add(info);
			}
			return result;
		}else{
			System.out.println("没有StudentScoreItem数据");
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	
}
