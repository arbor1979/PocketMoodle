package com.dandian.pocketmoodle.entity;

import org.json.JSONObject;

/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 获取测验状�??
 * 
 *  <br/>创建说明: 2014-4-29 上午10:00:34 shengguo  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class TestStatus {
	private String id;
	private String testStatus;
	private long TotalTime;
	private long remainingTime;
	private long expiryTime;
	
	public TestStatus() {
	}
	
	public TestStatus(JSONObject jo) {
		id = jo.optString("唯一码SEND");
		//remainingTime = jo.optLong("剩余时间");
		JSONObject joarr = jo.optJSONObject("GET_ARRAY2");
		testStatus = joarr.optString("答题状�??");
		remainingTime= joarr.optLong("剩余时间");
		expiryTime = joarr.optLong("到期时间");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTestStatus() {
		return testStatus;
	}

	public void setTestStatus(String testStatus) {
		this.testStatus = testStatus;
	}
	/**
	 * @return 剩余时间(/s)
	 */
	public long getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(long remainingTime) {
		this.remainingTime = remainingTime;
	}

	public long getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(long expiryTime) {
		this.expiryTime = expiryTime;
	}

	/**
	 * @return 总时�?(/s)
	 */
	public long getTotalTime() {
		return TotalTime;
	}

	public void setTotalTime(long totalTime) {
		TotalTime = totalTime;
	}
	
}
