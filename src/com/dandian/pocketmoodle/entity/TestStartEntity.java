package com.dandian.pocketmoodle.entity;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: 收卷时间
 * 
 * <br/>
 * 创建说明: 2013-11-29 下午4:35:31 zhuliang 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
@DatabaseTable(tableName = "TestStartEntity")
public class TestStartEntity {
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String timeKey;
	@DatabaseField
	private int timeValue;

	public TestStartEntity(){
		
	}
	public TestStartEntity(JSONObject jo){
		timeKey=jo.optString("名称");
		timeValue = jo.optInt("�?");
	}
	public TestStartEntity(String timeKey,int timeValue){
		this.timeKey = timeKey;
		this.timeValue = timeValue;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTimeKey() {
		return timeKey;
	}

	public void setTimeKey(String timeKey) {
		this.timeKey = timeKey;
	}

	public int getTimeValues() {
		return timeValue;
	}

	public void setTimeValues(int timeValue) {
		this.timeValue = timeValue;
	}

}
