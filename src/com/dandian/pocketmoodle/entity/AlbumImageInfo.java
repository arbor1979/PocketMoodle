package com.dandian.pocketmoodle.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class AlbumImageInfo implements Serializable {
	public int getPraiseCount() {
		return praiseCount;
	}
	public void setPraiseCount(int praiseCount) {
		this.praiseCount = praiseCount;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -393029336801316019L;
	private String id;
	private String url;
	private String localPath;
	private String name;
	private String hostName;
	private String hostId;
	
	private String hostBanji;
	private String headUrl;
	private int browerCount;
	private int praiseCount;
	private int commentCount;
	private String address;
	private String time;
	
	private String description;
	private int filesize;
	private String showLimit;
	private String device;
	public AlbumImageInfo() {
		
	}
	public AlbumImageInfo(JSONObject jo) {
		this.name = jo.optString("文件�?");
		this.url = jo.optString("文件地址");
		this.hostName = jo.optString("发布�?");
		this.filesize = jo.optInt("文件大小");
		this.hostId = jo.optString("发布人唯�?�?");
		this.hostBanji = jo.optString("班级");
		this.browerCount = jo.optInt("浏览次数");
		this.address = jo.optString("位置");
		this.time = jo.optString("时间");
		this.description = jo.optString("描述");
		if(this.hostBanji.equals("null"))
			this.hostBanji="未知";
		if(this.hostName.equals("null"))
			this.hostName="未知";
		this.headUrl=jo.optString("发布人头�?");
		this.showLimit=jo.optString("可见范围");
		this.praiseCount=jo.optInt("被赞次数");
		this.commentCount=jo.optInt("评论次数");
		this.praiseList=new ArrayList<AlbumMsgInfo>();
		this.commentsList=new ArrayList<AlbumMsgInfo>();
		this.device=jo.optString("当前设备");
	}
	
	public AlbumImageInfo(net.minidev.json.JSONObject jo) {
		this.name = String.valueOf(jo.get("文件�?"));
		this.url = String.valueOf(jo.get("文件地址"));
		this.hostName = String.valueOf(jo.get("发布�?"));
		this.filesize = Integer.parseInt(jo.get("文件大小").toString());
		this.hostId = String.valueOf(jo.get("发布人唯�?�?"));
		this.hostBanji = String.valueOf(jo.get("班级"));
		this.browerCount =  Integer.parseInt(jo.get("浏览次数").toString());
		this.address = String.valueOf(jo.get("位置"));
		this.time = String.valueOf(jo.get("时间"));
		this.description = String.valueOf(jo.get("描述"));
		if(this.hostBanji.equals("null"))
			this.hostBanji="未知";
		if(this.hostName.equals("null"))
			this.hostName="未知";
		this.headUrl=String.valueOf(jo.get("发布人头�?"));
		this.showLimit=String.valueOf(jo.get("可见范围"));
		this.praiseCount=Integer.parseInt(jo.get("被赞次数").toString());
		this.commentCount=Integer.parseInt(jo.get("评论次数").toString());
		this.praiseList=new ArrayList<AlbumMsgInfo>();
		this.commentsList=new ArrayList<AlbumMsgInfo>();
		this.device=String.valueOf(jo.get("当前设备"));
	}
	
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getShowLimit() {
		return showLimit;
	}
	public void setShowLimit(String showLimit) {
		this.showLimit = showLimit;
	}
	public static List<AlbumImageInfo> toList(JSONArray ja) {
		List<AlbumImageInfo> result = new ArrayList<AlbumImageInfo>();
		AlbumImageInfo info = null;
		if (ja.length() == 0) {
			return result;
		} else {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				if(jo != null){
					info = new AlbumImageInfo(jo);
					result.add(info);
				}	
			}
			return result;
		}
	}
	public static List<AlbumImageInfo> toList(net.minidev.json.JSONArray ja) {
		List<AlbumImageInfo> result = new ArrayList<AlbumImageInfo>();
		AlbumImageInfo info = null;
		if (ja.size() == 0) {
			return result;
		} else {
			for (int i = 0; i < ja.size(); i++) {
				net.minidev.json.JSONObject jo = (net.minidev.json.JSONObject)ja.get(i);
				if(jo != null){
					info = new AlbumImageInfo(jo);
					result.add(info);
				}	
			}
			return result;
		}
	}
	
	public int getFilesize() {
		return filesize;
	}

	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}

	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getHostBanji() {
		return hostBanji;
	}
	public void setHostBanji(String hostBanji) {
		this.hostBanji = hostBanji;
	}
	private ArrayList<AlbumMsgInfo> praiseList;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHeadUrl() {
		return headUrl;
	}
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}
	public int getBrowerCount() {
		return browerCount;
	}
	public void setBrowerCount(int browerCount) {
		this.browerCount = browerCount;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList<AlbumMsgInfo> getPraiseList() {
		return praiseList;
	}
	public void setPraiseList(ArrayList<AlbumMsgInfo> praiseList) {
		this.praiseList = praiseList;
	}
	public ArrayList<AlbumMsgInfo> getCommentsList() {
		return commentsList;
	}
	public void setCommentsList(ArrayList<AlbumMsgInfo> commentsList) {
		this.commentsList = commentsList;
	}
	private ArrayList<AlbumMsgInfo> commentsList;
}
