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
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: 学生考勤统计
 * 
 * <br/>
 * 创建说明: 2013-11-30 下午12:01:14 yanzy 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
@DatabaseTable(tableName = "StudentAttence")
public class StudentAttence implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(id = true)
	private String id;
	@DatabaseField
	private String studentID; //学号
	@DatabaseField
	private String attenceTitle; //考勤标题
	@DatabaseField
	private String attendance; //出勤
	@DatabaseField
	private String absence; //缺勤
	@DatabaseField
	private String late; //迟到
	@DatabaseField
	private String leave; //请假
	@DatabaseField
	private String attendanceRate; //出勤�?

	public StudentAttence() {

	}

	public StudentAttence(JSONObject jo) {
		this.studentID = jo.optString("学号");
		this.attenceTitle = jo.optString("标题");
		this.attendance = jo.optString("出勤");
		this.absence = jo.optString("缺勤");
		this.late = jo.optString("迟到");
		this.leave = jo.optString("请假");
		this.attendanceRate = jo.optString("出勤�?");
	}
	
	public StudentAttence(net.minidev.json.JSONObject jo) {
		this.studentID = String.valueOf(jo.get("学号"));
		this.attenceTitle = String.valueOf(jo.get("标题"));
		this.attendance = String.valueOf(jo.get("出勤"));
		this.absence = String.valueOf(jo.get("缺勤"));
		this.late = String.valueOf(jo.get("迟到"));
		this.leave = String.valueOf(jo.get("请假"));
		this.attendanceRate = String.valueOf(jo.get("出勤�?"));
	}
	
	public static List<StudentAttence> toList(JSONArray ja) {
		List<StudentAttence> result = new ArrayList<StudentAttence>();
		StudentAttence info = null;
		
		if (ja != null && ja.length() > 0) {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				info = new StudentAttence(jo);
				result.add(info);
			}
			return result;
		}else{
			System.out.println("没有StudentAttence数据");
			return null;
		}
	}
	
	public static List<StudentAttence> toList(net.minidev.json.JSONArray ja) {
		List<StudentAttence> result = new ArrayList<StudentAttence>();
		StudentAttence info = null;
		
		if (ja != null && ja.size() > 0) {
			for (int i = 0; i < ja.size(); i++) {
				net.minidev.json.JSONObject jo = (net.minidev.json.JSONObject)ja.get(i);
				info = new StudentAttence(jo);
				result.add(info);
			}
			return result;
		}else{
			System.out.println("没有StudentAttence数据");
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

	public String getAttenceTitle() {
		return attenceTitle;
	}

	public void setAttenceTitle(String attenceTitle) {
		this.attenceTitle = attenceTitle;
	}

	public String getAttendance() {
		return attendance;
	}

	public void setAttendance(String attendance) {
		this.attendance = attendance;
	}

	public String getAbsence() {
		return absence;
	}

	public void setAbsence(String absence) {
		this.absence = absence;
	}

	public String getLate() {
		return late;
	}

	public void setLate(String late) {
		this.late = late;
	}

	public String getLeave() {
		return leave;
	}

	public void setLeave(String leave) {
		this.leave = leave;
	}

	public String getAttendanceRate() {
		return attendanceRate;
	}

	public void setAttendanceRate(String attendanceRate) {
		this.attendanceRate = attendanceRate;
	}

	
}
