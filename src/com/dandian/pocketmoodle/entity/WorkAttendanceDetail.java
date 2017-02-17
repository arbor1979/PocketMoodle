package com.dandian.pocketmoodle.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: 考勤详情
 * 
 * <br/>
 * 创建说明: 2014-4-19 上午11:12:52 shengguo 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
public class WorkAttendanceDetail {

	private String[] type;
	private String title;
	private List<AttendanceValue> AttendanceValues;

	public WorkAttendanceDetail(JSONObject jo) {
		JSONArray jot = jo.optJSONArray("考勤类型");
		type = new String[jot.length()];
		for (int i = 0; i < jot.length(); i++) {
			type[i] = jot.optString(i);
		}
		title = jo.optString("标题显示");
		JSONArray joa = jo.optJSONArray("考勤数�??");
		AttendanceValues = new ArrayList<AttendanceValue>();
		for (int i = 0; i < joa.length(); i++) {
			AttendanceValue attValue = new AttendanceValue(joa.optJSONObject(i));
			AttendanceValues.add(attValue);
		}
	}

	public String[] getType() {
		return type;
	}

	public void setType(String[] type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<AttendanceValue> getAttendanceValues() {
		return AttendanceValues;
	}

	public void setAttendanceValues(List<AttendanceValue> attendanceValues) {
		AttendanceValues = attendanceValues;
	}

	public class AttendanceValue {
		private String type;
		private String background;
		private String title;
		private String timeAndAddress;
		private String rightType;// 图片和文�?
		private String rightContent;

		public AttendanceValue(JSONObject jo) {
			type = jo.optString("类别");
			background = jo.optString("图片背景");
			title = jo.optString("第一�?");
			timeAndAddress = jo.optString("第二�?");
			rightType = jo.optString("右边显示类型");
			rightContent = jo.optString("右边显示内容");
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getBackground() {
			return background;
		}

		public void setBackground(String background) {
			this.background = background;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getTimeAndAddress() {
			return timeAndAddress;
		}

		public void setTimeAndAddress(String timeAndAddress) {
			this.timeAndAddress = timeAndAddress;
		}

		public String getRightType() {
			return rightType;
		}

		public void setRightType(String rightType) {
			this.rightType = rightType;
		}

		public String getRightContent() {
			return rightContent;
		}

		public void setRightContent(String rightContent) {
			this.rightContent = rightContent;
		}
	}
}
