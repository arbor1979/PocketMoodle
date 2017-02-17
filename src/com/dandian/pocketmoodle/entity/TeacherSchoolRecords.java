package com.dandian.pocketmoodle.entity;

import org.json.JSONObject;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: 教师上课记录
 * 
 * <br/>
 * 创建说明: 2014-4-23 下午6:21:20 shengguo 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
public class TeacherSchoolRecords {
	private String id;//
	private String schoolTerm;//
	private String name;//
	private String userName;//
	private String courseDate;
	private String classRoom;//
	private String className;//
	private String curriculum;// 课程
	private String numberOfWeek;// 周次
	private String weeks;// 星期
	private String sections;// 节次
	private String Lectures;// 授课内容
	private String jobLayout;// 作业布置
	private String classDetails;// 课堂详情
	private String classroomDiscipline;// 课堂纪律
	private String numberOfPeople;// 班级人数
	private String realToNumberOfPeople;// 实到人数
	private String absenceStatus;// 缺勤情况登记
	private String shouldFillTime;// 应该填写时间
	private String latestFillTime;// �?迟填写时�?
	private String fillTime;// 填写时间
	private String quizzesStatus;// 课堂测验状�??
	private String remark;//
	private String classStartTime;// 上课�?始时�?
	private String absenceStatusJSON;// 缺勤情况登记JSON
	private String compositeScoreText;// 本次授课综合评分_文本
	private String compositeScoreValues;// 本次授课综合评分_分�??
	//private String[] compositeIds;// 课堂测验_编号对照�?

	public TeacherSchoolRecords() {

	}

	public TeacherSchoolRecords(JSONObject jo) {
		id =jo.optString("编号");
		schoolTerm = jo.optString("学期");
		name = jo.optString("教师姓名");
		userName = jo.optString("教师用户�?");
		courseDate = jo.optString("上课日期");
		classRoom = jo.optString("教室");//
		className = jo.optString("班级");//
		curriculum = jo.optString("课程");//
		numberOfWeek = jo.optString("周次");//
		weeks = jo.optString("星期");//
		sections = jo.optString("节次");//
		Lectures = jo.optString("授课内容");//
		jobLayout = jo.optString("作业布置");//
		classDetails = jo.optString("课堂详情");//
		classroomDiscipline = jo.optString("课堂纪律");//
		numberOfPeople = jo.optString("班级人数");//
		realToNumberOfPeople = jo.optString("实到人数");//
		absenceStatus = jo.optString("缺勤情况登记");//
		shouldFillTime = jo.optString("应该填写时间");//
		latestFillTime = jo.optString("�?迟填写时�?");//
		fillTime = jo.optString("填写时间");//
		quizzesStatus = jo.optString("课堂测验状�??");//
		remark = jo.optString("备注");//
		classStartTime = jo.optString("上课�?始时�?");//
		absenceStatusJSON = jo.optString("缺勤情况登记JSON");//
		compositeScoreText = jo.optString("本次授课综合评分_文本");//
		compositeScoreValues = jo.optString("本次授课综合评分_分�??");//
		//JSONArray joids = jo.optJSONArray("课堂测验_编号对照�?");//
//		compositeIds = new String[joids.length()];
//		for (int i = 0; i < joids.length(); i++) {
//			compositeIds[i] = joids.optString(i);
//		}
	}

	public TeacherSchoolRecords(net.minidev.json.JSONObject jo) {
		id = String.valueOf(jo.get("编号"));
		schoolTerm =  String.valueOf(jo.get("学期"));
		name =  String.valueOf(jo.get("教师姓名"));
		userName =  String.valueOf(jo.get("教师用户�?"));
		courseDate =  String.valueOf(jo.get("上课日期"));
		classRoom =  String.valueOf(jo.get("教室"));//
		className =  String.valueOf(jo.get("班级"));//
		curriculum =  String.valueOf(jo.get("课程"));//
		numberOfWeek =  String.valueOf(jo.get("周次"));//
		weeks =  String.valueOf(jo.get("星期"));//
		sections =  String.valueOf(jo.get("节次"));//
		Lectures =  String.valueOf(jo.get("授课内容"));//
		jobLayout =  String.valueOf(jo.get("作业布置"));//
		classDetails =  String.valueOf(jo.get("课堂详情"));//
		classroomDiscipline =  String.valueOf(jo.get("课堂纪律"));//
		numberOfPeople =  String.valueOf(jo.get("班级人数"));//
		realToNumberOfPeople =  String.valueOf(jo.get("实到人数"));//
		absenceStatus =  String.valueOf(jo.get("缺勤情况登记"));//
		shouldFillTime =  String.valueOf(jo.get("应该填写时间"));//
		latestFillTime =  String.valueOf(jo.get("�?迟填写时�?"));//
		fillTime =  String.valueOf(jo.get("填写时间"));//
		quizzesStatus =  String.valueOf(jo.get("课堂测验状�??"));//
		remark = String.valueOf( jo.get("备注"));//
		classStartTime =  String.valueOf(jo.get("上课�?始时�?"));//
		absenceStatusJSON = String.valueOf(jo.get("缺勤情况登记JSON"));//
		compositeScoreText =String.valueOf( jo.get("本次授课综合评分_文本"));//
		compositeScoreValues = String.valueOf(jo.get("本次授课综合评分_分�??"));//
		
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSchoolTerm() {
		return schoolTerm;
	}

	public void setSchoolTerm(String schoolTerm) {
		this.schoolTerm = schoolTerm;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCourseDate() {
		return courseDate;
	}

	public void setCourseDate(String courseDate) {
		this.courseDate = courseDate;
	}

	public String getClassRoom() {
		return classRoom;
	}

	public void setClassRoom(String classRoom) {
		this.classRoom = classRoom;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(String curriculum) {
		this.curriculum = curriculum;
	}

	public String getNumberOfWeek() {
		return numberOfWeek;
	}

	public void setNumberOfWeek(String numberOfWeek) {
		this.numberOfWeek = numberOfWeek;
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

	public String getLectures() {
		return Lectures;
	}

	public void setLectures(String lectures) {
		Lectures = lectures;
	}

	public String getJobLayout() {
		return jobLayout;
	}

	public void setJobLayout(String jobLayout) {
		this.jobLayout = jobLayout;
	}

	public String getClassDetails() {
		return classDetails;
	}

	public void setClassDetails(String classDetails) {
		this.classDetails = classDetails;
	}

	public String getClassroomDiscipline() {
		return classroomDiscipline;
	}

	public void setClassroomDiscipline(String classroomDiscipline) {
		this.classroomDiscipline = classroomDiscipline;
	}

	public String getNumberOfPeople() {
		return numberOfPeople;
	}

	public void setNumberOfPeople(String numberOfPeople) {
		this.numberOfPeople = numberOfPeople;
	}

	public String getRealToNumberOfPeople() {
		return realToNumberOfPeople;
	}

	public void setRealToNumberOfPeople(String realToNumberOfPeople) {
		this.realToNumberOfPeople = realToNumberOfPeople;
	}

	public String getAbsenceStatus() {
		return absenceStatus;
	}

	public void setAbsenceStatus(String absenceStatus) {
		this.absenceStatus = absenceStatus;
	}

	public String getShouldFillTime() {
		return shouldFillTime;
	}

	public void setShouldFillTime(String shouldFillTime) {
		this.shouldFillTime = shouldFillTime;
	}

	public String getLatestFillTime() {
		return latestFillTime;
	}

	public void setLatestFillTime(String latestFillTime) {
		this.latestFillTime = latestFillTime;
	}

	public String getFillTime() {
		return fillTime;
	}

	public void setFillTime(String fillTime) {
		this.fillTime = fillTime;
	}

	public String getQuizzesStatus() {
		return quizzesStatus;
	}

	public void setQuizzesStatus(String quizzesStatus) {
		this.quizzesStatus = quizzesStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getClassStartTime() {
		return classStartTime;
	}

	public void setClassStartTime(String classStartTime) {
		this.classStartTime = classStartTime;
	}

	public String getAbsenceStatusJSON() {
		return absenceStatusJSON;
	}

	public void setAbsenceStatusJSON(String absenceStatusJSON) {
		this.absenceStatusJSON = absenceStatusJSON;
	}

	public String getCompositeScoreText() {
		return compositeScoreText;
	}

	public void setCompositeScoreText(String compositeScoreText) {
		this.compositeScoreText = compositeScoreText;
	}

	public String getCompositeScoreValues() {
		return compositeScoreValues;
	}

	public void setCompositeScoreValues(String compositeScoreValues) {
		this.compositeScoreValues = compositeScoreValues;
	}

//	public String[] getCompositeIds() {
//		return compositeIds;
//	}
//
//	public void setCompositeIds(String[] compositeIds) {
//		this.compositeIds = compositeIds;
//	}
}
