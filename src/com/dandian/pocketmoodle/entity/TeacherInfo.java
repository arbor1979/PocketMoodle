package com.dandian.pocketmoodle.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 教师上课记录
 * 
 * @Title TeacherInfo.java
 * @Description: TODO
 * 
 * @author Zecker
 * @date 2013-11-8 上午10:09:26
 * @version V1.0
 * 
 */
@DatabaseTable(tableName = "TeacherInfo")
public class TeacherInfo implements Serializable{// "教师上课记录"
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(id = true)
	private String id; // 编号
	@DatabaseField
	private String term; // 学期
	@DatabaseField
	private String name; // 教师姓名
	@DatabaseField
	private String username; // 教师用户�?
	@DatabaseField
	private String courseDate; // 上课日期
	@DatabaseField
	private String classroom; // 教室
	@DatabaseField
	private String courseName; // 课程
	@DatabaseField
	private String classGrade; // 班级
	@DatabaseField
	private String weekly; // 周次
	@DatabaseField
	private int week; // 星期
	@DatabaseField
	private String section;// 节次
	@DatabaseField
	private String courseContent; // 授课内容
	@DatabaseField
	private String homework; // 作业布置
	@DatabaseField
	private String classroomSituation; // 课堂情况
	@DatabaseField
	private String classroomDiscipline; // 课堂纪律
	@DatabaseField
	private String classroomHealth; // 教室卫生
	@DatabaseField
	private String classSize; // 班级人数
	@DatabaseField
	private String realNumber; // 实到人数
	@DatabaseField
	private String absences; // 缺勤情况登记
	@DatabaseField
	private String absenceJson; // 缺勤情况登记JSON
	@DatabaseField
	private String shouldTime; // 应该填写时间
	@DatabaseField
	private String latestTime; // �?迟填写时�?
	@DatabaseField
	private String fillTime; // 填写时间
	@DatabaseField
	private String remark; // 备注
	@DatabaseField
	private String compositeScoreText; //本次授课综合评分_文本
	@DatabaseField
	private String compositeScoreValue; //本次授课综合评分_分�??
	@DatabaseField
	private int isModify = 0; // 是否修改
	@DatabaseField
	private String testStatus;//课堂测验状�??
	@DatabaseField
	private String beginTime; //�?始上课时�?
	public TeacherInfo() {
	}

	public static List<TeacherInfo> toList(JSONArray ja) {
		List<TeacherInfo> result = new ArrayList<TeacherInfo>();
		TeacherInfo info = null;
		if (ja != null && ja.length() > 0) {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				info = new TeacherInfo(jo);
				result.add(info);
			}
			return result;
		}
		return null;
	}
	public static List<TeacherInfo> toList(net.minidev.json.JSONArray ja) {
		List<TeacherInfo> result = new ArrayList<TeacherInfo>();
		TeacherInfo info = null;
		if (ja != null && ja.size() > 0) {
			for (int i = 0; i < ja.size(); i++) {
				net.minidev.json.JSONObject jo = (net.minidev.json.JSONObject)ja.get(i);
				info = new TeacherInfo(jo);
				result.add(info);
			}
			return result;
		}
		return null;
	}

	private TeacherInfo(JSONObject jo) {
		id = jo.optString("编号");
		term = jo.optString("学期");
		name = jo.optString("教师姓名");
		username = jo.optString("教师用户�?");
		courseDate = jo.optString("上课日期");
		classroom = jo.optString("教室");
		courseName = jo.optString("课程");
		classGrade = jo.optString("班级");
		weekly = jo.optString("周次");
		week = Integer.parseInt(jo.optString("星期"));
		section = jo.optString("节次");
		courseContent = jo.optString("授课内容");
		homework = jo.optString("作业布置");
		classroomSituation = jo.optString("课堂情况");
		classroomDiscipline = jo.optString("课堂纪律");
		classroomHealth = jo.optString("教室卫生");
		classSize = jo.optString("班级人数");
		realNumber = jo.optString("实到人数");
		absences = jo.optString("缺勤情况登记");
		absenceJson = jo.optString("缺勤情况登记JSON");
		shouldTime = jo.optString("应该填写时间");
		latestTime = jo.optString("�?迟填写时�?");
		fillTime = jo.optString("填写时间");
		remark = jo.optString("备注");
		compositeScoreText = jo.optString("本次授课综合评分_文本");
		compositeScoreValue = jo.optString("本次授课综合评分_分�??");
		testStatus = jo.optString("课堂测验状�??");
		beginTime = jo.optString("上课�?始时�?");
	}

	private TeacherInfo(net.minidev.json.JSONObject jo) {
		id = String.valueOf(jo.get("编号"));
		term = String.valueOf(jo.get("学期"));
		name = String.valueOf(jo.get("教师姓名"));
		username = String.valueOf(jo.get("教师用户�?"));
		courseDate = String.valueOf(jo.get("上课日期"));
		classroom = String.valueOf(jo.get("教室"));
		courseName = String.valueOf(jo.get("课程"));
		classGrade = String.valueOf(jo.get("班级"));
		weekly = String.valueOf(jo.get("周次"));
		week = Integer.parseInt(String.valueOf(jo.get("星期")));
		section = String.valueOf(jo.get("节次"));
		courseContent = String.valueOf(jo.get("授课内容"));
		homework = String.valueOf(jo.get("作业布置"));
		classroomSituation = String.valueOf(jo.get("课堂情况"));
		classroomDiscipline = String.valueOf(jo.get("课堂纪律"));
		classroomHealth = String.valueOf(jo.get("教室卫生"));
		classSize = String.valueOf(jo.get("班级人数"));
		realNumber = String.valueOf(jo.get("实到人数"));
		absences = String.valueOf(jo.get("缺勤情况登记"));
		absenceJson = String.valueOf(jo.get("缺勤情况登记JSON"));
		shouldTime = String.valueOf(jo.get("应该填写时间"));
		latestTime = String.valueOf(jo.get("�?迟填写时�?"));
		fillTime = String.valueOf(jo.get("填写时间"));
		remark = String.valueOf(jo.get("备注"));
		compositeScoreText = String.valueOf(jo.get("本次授课综合评分_文本"));
		compositeScoreValue = String.valueOf(jo.get("本次授课综合评分_分�??"));
		testStatus = String.valueOf(jo.get("课堂测验状�??"));
		beginTime = String.valueOf(jo.get("上课�?始时�?"));
	}
	/**
	 * 编号
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 学期
	 * 
	 * @return
	 */
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * 教师姓名
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 教师用户�?
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCourseDate() {
		return courseDate;
	}

	public void setCourseDate(String courseDate) {
		this.courseDate = courseDate;
	}

	public String getClassroom() {
		return classroom;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getClassGrade() {
		return classGrade;
	}

	public void setClassGrade(String classGrade) {
		this.classGrade = classGrade;
	}

	public String getWeekly() {
		return weekly;
	}

	public void setWeekly(String weekly) {
		this.weekly = weekly;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getCourseContent() {
		return courseContent;
	}

	public void setCourseContent(String courseContent) {
		this.courseContent = courseContent;
	}

	public String getHomework() {
		return homework;
	}

	public void setHomework(String homework) {
		this.homework = homework;
	}

	public String getClassroomSituation() {
		return classroomSituation;
	}

	public void setClassroomSituation(String classroomSituation) {
		this.classroomSituation = classroomSituation;
	}

	public String getClassroomDiscipline() {
		return classroomDiscipline;
	}

	public void setClassroomDiscipline(String classroomDiscipline) {
		this.classroomDiscipline = classroomDiscipline;
	}

	public String getClassroomHealth() {
		return classroomHealth;
	}

	public void setClassroomHealth(String classroomHealth) {
		this.classroomHealth = classroomHealth;
	}

	public String getClassSize() {
		return classSize;
	}

	public void setClassSize(String classSize) {
		this.classSize = classSize;
	}

	public String getRealNumber() {
		return realNumber;
	}

	public void setRealNumber(String realNumber) {
		this.realNumber = realNumber;
	}

	public String getAbsences() {
		return absences;
	}

	public void setAbsences(String absences) {
		this.absences = absences;
	}

	public String getAbsenceJson() {
		return absenceJson;
	}

	public void setAbsenceJson(String absenceJson) {
		this.absenceJson = absenceJson;
	}

	public String getShouldTime() {
		return shouldTime;
	}

	public void setShouldTime(String shouldTime) {
		this.shouldTime = shouldTime;
	}

	public String getLatestTime() {
		return latestTime;
	}

	public void setLatestTime(String latestTime) {
		this.latestTime = latestTime;
	}

	/**
	 * 填写时间
	 * 
	 * @return
	 */
	public String getFillTime() {
		return fillTime;
	}

	public void setFillTime(String fillTime) {
		this.fillTime = fillTime;
	}

	/**
	 * 备注
	 * 
	 * @return
	 */
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCompositeScoreText() {
		return compositeScoreText;
	}

	public void setCompositeScoreText(String compositeScoreText) {
		this.compositeScoreText = compositeScoreText;
	}

	public String getCompositeScoreValue() {
		return compositeScoreValue;
	}

	public void setCompositeScoreValue(String compositeScoreValue) {
		this.compositeScoreValue = compositeScoreValue;
	}

	public String getTestStatus() {
		return testStatus;
	}

	public void setTestStatus(String testStatus) {
		this.testStatus = testStatus;
	}

	/**
	 * 是否修改
	 * 
	 * @return
	 */
	public int getIsModify() {
		return isModify;
	}

	public void setIsModify(int isModify) {
		this.isModify = isModify;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

}
