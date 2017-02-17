package com.dandian.pocketmoodle.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 沟�?�聊天界面数�?
 * @author hfthink
 *
 */
@DatabaseTable(tableName="ChatMsgDetail")
public class ChatMsgDetail {
	@DatabaseField(generatedId=true)
	private int id;
	
	/**
	 * 消息发�?�人
	 */
	@DatabaseField(generatedId=false)
	private int mainid; 
	/**
	 * 消息接收�?
	 */
	@DatabaseField
	private String toid; 
	@DatabaseField
	private String msg_id; 
	
	@DatabaseField
	private String sendstate; 
	
	
	public String getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}
	

	public ChatMsgDetail(){
		toid="";
		sendstate="";
		msg_id="";
	}
	public ChatMsgDetail(int mainid,String toid,String msgid,String sendstate){
		this.mainid=mainid;
		this.toid=toid;
		this.msg_id=msgid;
		this.sendstate=sendstate;
	}

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getToid() {
		return toid;
	}
	public void setToid(String toid) {
		this.toid = toid;
	}
	public int getMainid() {
		return mainid;
	}
	public void setMainid(int mainid) {
		this.mainid = mainid;
	}
	public String getSendstate() {
		return sendstate;
	}
	public void setSendstate(String sendstate) {
		this.sendstate = sendstate;
	}
	
	
}
