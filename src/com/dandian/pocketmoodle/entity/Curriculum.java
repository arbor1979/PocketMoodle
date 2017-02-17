package com.dandian.pocketmoodle.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: classdetail中的课程
 * 
 * <br/>
 * 创建说明: 2014-4-26 上午11:06:31 shengguo 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
public class Curriculum {
	private String teacherRank;
	private String courseRating;
	private String summaryContent;
	private String homeWork;
	private String attendanceValues;
	private String curriculums;
	private String classes;
	private ArrayList<DownloadSubject> imagePaths;

	public Curriculum() {
	}

	public Curriculum(JSONObject jo) throws JSONException {
		teacherRank = jo.optString("老师评分");
		courseRating = jo.optString("课程评分");
		summaryContent = jo.optString("授课内容");
		homeWork = jo.optString("课后作业");
		attendanceValues = jo.optString("个人出勤");
		curriculums = jo.optString("�?带课�?");
		classes = jo.optString("�?带班�?");
		JSONArray joimg = jo.optJSONArray("课堂笔记图片");
		imagePaths = new ArrayList<DownloadSubject>();
		for (int i = 0; i < joimg.length(); i++) {
			DownloadSubject downsub=new DownloadSubject(joimg.getJSONObject(i));
			imagePaths.add(downsub);
			
		}
	}

	public String getTeacherRank() {
		return teacherRank;
	}

	public void setTeacherRank(String teacherRank) {
		this.teacherRank = teacherRank;
	}

	public String getCourseRating() {
		return courseRating;
	}

	public void setCourseRating(String courseRating) {
		this.courseRating = courseRating;
	}

	public String getSummaryContent() {
		return summaryContent;
	}

	public void setSummaryContent(String summaryContent) {
		this.summaryContent = summaryContent;
	}

	public String getHomeWork() {
		return homeWork;
	}

	public void setHomeWork(String homeWork) {
		this.homeWork = homeWork;
	}

	public String getAttendanceValues() {
		return attendanceValues;
	}

	public void setAttendanceValues(String attendanceValues) {
		this.attendanceValues = attendanceValues;
	}

	public String getCurriculums() {
		return curriculums;
	}

	public void setCurriculums(String curriculums) {
		this.curriculums = curriculums;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	public ArrayList<DownloadSubject> getImagePaths() {
		return imagePaths;
	}

	public void setImagePaths(ArrayList<DownloadSubject> imagePaths) {
		this.imagePaths = imagePaths;
	}

	
}
