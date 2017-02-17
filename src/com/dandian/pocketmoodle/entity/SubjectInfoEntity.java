package com.dandian.pocketmoodle.entity;
/**
 * 课程排期�?-课程排期数据
 * @author hfthink
 *
 */
public class SubjectInfoEntity {
	/**
	 * 课程节数安排
	 */
	private String numSubject;
	/**
	 * 每节课时�?
	 */
	private String Time;
	/**
	 * 周一到周�?
	 */
	private String date;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getNumSubject() {
		return numSubject;
	}
	public void setNumSubject(String numSubject) {
		this.numSubject = numSubject;
	}
	public String getTime() {
		return Time;
	}
	public void setTime(String time) {
		Time = time;
	}
}
