package com.dandian.pocketmoodle.entity;

import org.json.JSONObject;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: 考勤规则
 * 
 * <br/>
 * 创建说明: 2014-4-16 下午2:06:15 shengguo 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
public class WorkAttendanceRule {
	private String value;// 分�??
	private String name;// 名称

	public WorkAttendanceRule() {
	}

	public WorkAttendanceRule(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public WorkAttendanceRule(JSONObject jo) {
		value = jo.optString("�?");// 数量
		name = jo.optString("名称");// 名称
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
