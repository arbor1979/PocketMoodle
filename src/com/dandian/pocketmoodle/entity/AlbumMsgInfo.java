package com.dandian.pocketmoodle.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable(tableName="AlbumMsgInfo")
public class AlbumMsgInfo implements Serializable {
	
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 489477630867568173L;
	/**
	 * 
	 */
	@DatabaseField(generatedId=true)
	private int id;
	private AlbumImageInfo image;
	@DatabaseField
	private String fromId;
	@DatabaseField
	private String toId;
	@DatabaseField
	private String msg;
	@DatabaseField
	private String type;
	@DatabaseField
	private String fromHeadUrl;
	@DatabaseField
	private String fromName;
	@DatabaseField
	private String time;
	@DatabaseField
	private String imageObject;
	@DatabaseField
	private String toName;
	
	public int getIfRead() {
		return ifRead;
	}
	public void setIfRead(int ifRead) {
		this.ifRead = ifRead;
	}
	@DatabaseField
	private int ifRead;
	public AlbumMsgInfo() {
		
	}
	public AlbumMsgInfo(JSONObject jo) {
		try {
			
			this.image=new AlbumImageInfo(jo.getJSONObject("相片信息"));
			this.imageObject=jo.getJSONObject("相片信息").toString();
			
		} catch (JSONException e) {
			this.image=new AlbumImageInfo();
		}
		if(jo.optString("点赞�?")!=null && !jo.optString("点赞�?").equals("null") && jo.optString("点赞�?").length()>0)
		{
			
			this.fromId=jo.optString("点赞�?");
			this.time=jo.optString("时间");
			this.fromHeadUrl=jo.optString("点赞人头�?");
			this.fromName=jo.optString("点赞人姓�?");
			this.toId=this.image.getHostId();
			this.type="点赞";
		}
		else
		{
			
			this.fromId=jo.optString("评论�?");
			this.msg=jo.optString("评论内容");
			this.time=jo.optString("时间");
			this.fromHeadUrl=jo.optString("评论人头�?");
			this.fromName=jo.optString("评论人姓�?");
			this.toId=jo.optString("回复目标");
			this.toName=jo.optString("回复目标姓名");
			this.type="评论";
		}
		this.ifRead=0;
		
	}
	public String getToName() {
		return toName;
	}
	public void setToName(String toName) {
		this.toName = toName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFromId() {
		return fromId;
	}
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}
	public String getToId() {
		return toId;
	}
	public void setToId(String toId) {
		this.toId = toId;
	}
	
	public AlbumImageInfo getImage() {
		return image;
	}
	public void setImage(AlbumImageInfo image) {
		this.image = image;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFromHeadUrl() {
		return fromHeadUrl;
	}
	public void setFromHeadUrl(String fromHeadUrl) {
		this.fromHeadUrl = fromHeadUrl;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getImageObject() {
		return imageObject;
	}
	public void setImageObject(String imageObject) {
		this.imageObject = imageObject;
	}
}
