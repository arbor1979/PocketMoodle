package com.dandian.pocketmoodle.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "DownloadInfo")
public class DownloadInfo {
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private int threadId;// 下载器id
	@DatabaseField
	private int startPos;// �?始点
	@DatabaseField
	private int endPos;// 结束�?
	@DatabaseField
	private int compeleteSize;// 完成�?
	@DatabaseField
	private String url;// 下载器网络标�?
	
	public DownloadInfo(){}
	
	public DownloadInfo(int threadId, int startPos, int endPos,
			int compeleteSize, String url) {
		this.threadId = threadId;
		this.startPos = startPos;
		this.endPos = endPos;
		this.compeleteSize = compeleteSize;
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	public int getCompeleteSize() {
		return compeleteSize;
	}

	public void setCompeleteSize(int compeleteSize) {
		this.compeleteSize = compeleteSize;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	/*@Override
	     public String toString() {
	         return "DownloadInfo [threadId=" + threadId
	                 + ", startPos=" + startPos + ", endPos=" + endPos
	                 + ", compeleteSize=" + compeleteSize +"]";
     }*/


}
