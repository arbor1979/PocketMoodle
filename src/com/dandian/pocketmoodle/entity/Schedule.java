package com.dandian.pocketmoodle.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * �α����
 * 
 * @Title Schedule.java
 * @Description: TODO
 * 
 * @author Zecker
 * @date 2013-11-7 ����4:53:59
 * @version V1.0
 * 
 */
@DatabaseTable(tableName = "Schedule")
public class Schedule {
	@DatabaseField
	private String weeks;
	@DatabaseField
	private String sections;
	@DatabaseField
	private String rests;
	@DatabaseField
	private String sectionsTime;
	@DatabaseField
	private String WeekBeginDay;
	@DatabaseField
	private String WeekEndDay;

	public Schedule() {
	}
	
	public Schedule(JSONObject jo) {
//		weeks = getResult(jo, "������ʾ");
//		sections = getResult(jo, "�ڴ���ʾ");
//		rests = getResult(jo, "������Ϣʱ��");
		weeks = jo.optString("������ʾ");
		sections = jo.optString("�ڴ���ʾ");
		rests = jo.optString("������Ϣʱ��");
		sectionsTime = jo.optString("�ڴ�ʱ��");
		WeekBeginDay = jo.optString("�ܿ�ʼ����");
		WeekEndDay = jo.optString("�ܽ�������");
	}
	
	public Schedule(net.minidev.json.JSONObject jo) {
//		weeks = getResult(jo, "������ʾ");
//		sections = getResult(jo, "�ڴ���ʾ");
//		rests = getResult(jo, "������Ϣʱ��");
		weeks = jo.get("������ʾ").toString();
		sections = jo.get("�ڴ���ʾ").toString();
		rests = jo.get("������Ϣʱ��").toString();
		sectionsTime = jo.get("�ڴ�ʱ��").toString();
		if(jo.get("�ܿ�ʼ����")!=null)
			WeekBeginDay = jo.get("�ܿ�ʼ����").toString();
		if(jo.get("�ܽ�������")!=null)
			WeekEndDay = jo.get("�ܽ�������").toString();
	}
	

	public String getWeekBeginDay() {
		return WeekBeginDay;
	}

	public void setWeekBeginDay(String weekBeginDay) {
		WeekBeginDay = weekBeginDay;
	}

	public String getWeekEndDay() {
		return WeekEndDay;
	}

	public void setWeekEndDay(String weekEndDay) {
		WeekEndDay = weekEndDay;
	}

	private String getResult(JSONObject jo, String key) {
		JSONArray ja = jo.optJSONArray(key);
		String[] result = null;
		if (ja != null) {
			result = toStrArray(ja);
		}
		StringBuffer strbuff = new StringBuffer();

		for (int i = 0; i < result.length; i++) {
			strbuff.append(",").append(result[i]);
		}

		String str = strbuff.deleteCharAt(0).toString();
		return str;
	}

	private String[] toStrArray(JSONArray ja) {
		String[] strArray = new String[ja.length()];
		for (int i = 0; i < ja.length(); i++) {
			strArray[i] = ja.optString(i);
		}
		return strArray;
	}

	public String getWeeks() {
		return weeks;
	}

	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}

	public String getSections() {
		return sections;
	}

	public void setSections(String sections) {
		this.sections = sections;
	}

	public String getRests() {
		return rests;
	}

	public void setRests(String rests) {
		this.rests = rests;
	}

	public String getSectionsTime() {
		return sectionsTime;
	}

	public void setSectionsTime(String sectionsTime) {
		this.sectionsTime = sectionsTime;
	}

}
