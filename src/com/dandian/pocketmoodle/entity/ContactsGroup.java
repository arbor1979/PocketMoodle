package com.dandian.pocketmoodle.entity;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ContactsGroup")
public class ContactsGroup {
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String groupName;
	@DatabaseField
	private String groupClass;
	@DatabaseField
	private String groupCount;
	@DatabaseField
	private String groupId; 
	@DatabaseField
	private String groupMember;

	public ContactsGroup(){
		
	}
	
	public ContactsGroup(JSONObject jo){
		groupName = jo.optString("群名�?");
		groupClass = jo.optString("级别");
		groupCount = jo.optString("群人�?");
		groupId = jo.optString("群唯�?�?");
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupClass() {
		return groupClass;
	}

	public void setGroupClass(String groupClass) {
		this.groupClass = groupClass;
	}

	public String getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(String groupCount) {
		this.groupCount = groupCount;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupMember() {
		return groupMember;
	}

	public void setGroupMember(String groupMember) {
		this.groupMember = groupMember;
	}

	
}
