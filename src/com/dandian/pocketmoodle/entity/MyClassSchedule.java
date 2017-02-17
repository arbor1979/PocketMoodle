package com.dandian.pocketmoodle.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * δ�����ܵĿα�
 * 
 * @Title MyClassSchedule.java
 * @Description: TODO
 * 
 * @author Zecker
 * @date 2013-11-8 ����10:09:26
 * @version V1.0
 * 
 */
@DatabaseTable(tableName = "MyClassSchedule")
public class MyClassSchedule implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5024822673519093237L;
	/**
	 * 
	 */
	
	@DatabaseField(id = true)
	private String id; // ���
	@DatabaseField
	private String term; // ѧ��
	@DatabaseField
	private String name; // ��ʦ����
	@DatabaseField
	private String username; // ��ʦ�û���
	@DatabaseField
	private String courseDate; // �Ͽ�����
	@DatabaseField
	private String classroom; // ����
	@DatabaseField
	private String courseName; // �γ�
	@DatabaseField
	private String classGrade; // �༶
	@DatabaseField
	private String weekly; // �ܴ�
	@DatabaseField
	private int week; // ����
	@DatabaseField
	private String section;// �ڴ�
	@DatabaseField
	private String courseContent; // �ڿ�����
	@DatabaseField
	private String homework; // ��ҵ����
	@DatabaseField
	private String classroomSituation; // �������
	@DatabaseField
	private String classroomDiscipline; // ���ü���
	@DatabaseField
	private String classroomHealth; // ��������
	@DatabaseField
	private String classSize; // �༶����
	@DatabaseField
	private String realNumber; // ʵ������
	@DatabaseField
	private String absences; // ȱ������Ǽ�
	@DatabaseField
	private String absenceJson; // ȱ������Ǽ�JSON
	@DatabaseField
	private String shouldTime; // Ӧ����дʱ��
	@DatabaseField
	private String latestTime; // �����дʱ��
	@DatabaseField
	private String fillTime; // ��дʱ��
	@DatabaseField
	private String remark; // ��ע
	@DatabaseField
	private String compositeScoreText; //�����ڿ��ۺ�����_�ı�
	@DatabaseField
	private String compositeScoreValue; //�����ڿ��ۺ�����_��ֵ
	@DatabaseField
	private int isModify = 0; // �Ƿ��޸�
	@DatabaseField
	private String testStatus;//���ò���״̬
	@DatabaseField
	private String beginTime; //��ʼ�Ͽ�ʱ��
	@DatabaseField
	private String eventtype;
	@DatabaseField
	private long timestart;
	public String getEventtype() {
		return eventtype;
	}

	public void setEventtype(String eventtype) {
		this.eventtype = eventtype;
	}

	public long getTimestart() {
		return timestart;
	}

	public void setTimestart(long timestart) {
		this.timestart = timestart;
	}

	public long getTimeduration() {
		return timeduration;
	}

	public void setTimeduration(long timeduration) {
		this.timeduration = timeduration;
	}

	public long getTimesend() {
		return timesend;
	}

	public void setTimesend(long timesend) {
		this.timesend = timesend;
	}

	@DatabaseField
	private long timeduration;
	@DatabaseField
	private long timesend;
	
	public MyClassSchedule() {
	}

	public static List<MyClassSchedule> toList(JSONArray ja) {
		List<MyClassSchedule> result = new ArrayList<MyClassSchedule>();
		MyClassSchedule info = null;
		if (ja != null && ja.length() > 0) {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				info = new MyClassSchedule(jo);
				result.add(info);
			}
			return result;
		}
		return null;
	}
	public static List<MyClassSchedule> toList(net.minidev.json.JSONArray ja) {
		List<MyClassSchedule> result = new ArrayList<MyClassSchedule>();
		MyClassSchedule info = null;
		if (ja != null && ja.size() > 0) {
			for (int i = 0; i < ja.size(); i++) {
				net.minidev.json.JSONObject jo = (net.minidev.json.JSONObject)ja.get(i);
				info = new MyClassSchedule(jo);
				result.add(info);
			}
			return result;
		}
		return null;
	}

	public MyClassSchedule(JSONObject jo) {
		id = jo.optString("id");
		term = jo.optString("ѧ��");
		name = jo.optString("name");
		eventtype= jo.optString("courseid");
		timestart= jo.optLong("timestart");
		timeduration= jo.optLong("timeduration");
		timesend = timestart+timeduration;
		
		/*
		username = jo.optString("��ʦ�û���");
		courseDate = jo.optString("�Ͽ�����");
		classroom = jo.optString("����");
		courseName = jo.optString("�γ�");
		classGrade = jo.optString("�༶");
		weekly = jo.optString("�ܴ�");
		week = Integer.parseInt(jo.optString("����"));
		section = jo.optString("�ڴ�");
		courseContent = jo.optString("�ڿ�����");
		homework = jo.optString("��ҵ����");
		classroomSituation = jo.optString("�������");
		classroomDiscipline = jo.optString("���ü���");
		classroomHealth = jo.optString("��������");
		classSize = jo.optString("�༶����");
		realNumber = jo.optString("ʵ������");
		absences = jo.optString("ȱ������Ǽ�");
		absenceJson = jo.optString("ȱ������Ǽ�JSON");
		shouldTime = jo.optString("Ӧ����дʱ��");
		latestTime = jo.optString("�����дʱ��");
		fillTime = jo.optString("��дʱ��");
		remark = jo.optString("��ע");
		compositeScoreText = jo.optString("�����ڿ��ۺ�����_�ı�");
		compositeScoreValue = jo.optString("�����ڿ��ۺ�����_��ֵ");
		testStatus = jo.optString("���ò���״̬");
		beginTime = jo.optString("�Ͽο�ʼʱ��");
		
		*/
	}

	private MyClassSchedule(net.minidev.json.JSONObject jo) {
		id = String.valueOf(jo.get("���"));
		term = String.valueOf(jo.get("ѧ��"));
		name = String.valueOf(jo.get("��ʦ����"));
		username = String.valueOf(jo.get("��ʦ�û���"));
		courseDate = String.valueOf(jo.get("�Ͽ�����"));
		classroom = String.valueOf(jo.get("����"));
		courseName = String.valueOf(jo.get("�γ�"));
		classGrade = String.valueOf(jo.get("�༶"));
		weekly = String.valueOf(jo.get("�ܴ�"));
		week = Integer.parseInt(String.valueOf(jo.get("����")));
		section = String.valueOf(jo.get("�ڴ�"));
		courseContent = String.valueOf(jo.get("�ڿ�����"));
		homework = String.valueOf(jo.get("��ҵ����"));
		classroomSituation = String.valueOf(jo.get("�������"));
		classroomDiscipline = String.valueOf(jo.get("���ü���"));
		classroomHealth = String.valueOf(jo.get("��������"));
		classSize = String.valueOf(jo.get("�༶����"));
		realNumber = String.valueOf(jo.get("ʵ������"));
		absences = String.valueOf(jo.get("ȱ������Ǽ�"));
		absenceJson = String.valueOf(jo.get("ȱ������Ǽ�JSON"));
		shouldTime = String.valueOf(jo.get("Ӧ����дʱ��"));
		latestTime = String.valueOf(jo.get("�����дʱ��"));
		fillTime = String.valueOf(jo.get("��дʱ��"));
		remark = String.valueOf(jo.get("��ע"));
		compositeScoreText = String.valueOf(jo.get("�����ڿ��ۺ�����_�ı�"));
		compositeScoreValue = String.valueOf(jo.get("�����ڿ��ۺ�����_��ֵ"));
		testStatus = String.valueOf(jo.get("���ò���״̬"));
		beginTime = String.valueOf(jo.get("�Ͽο�ʼʱ��"));
	}
	/**
	 * ���
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
	 * ѧ��
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
	 * ��ʦ����
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
	 * ��ʦ�û���
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
	 * ��дʱ��
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
	 * ��ע
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
	 * �Ƿ��޸�
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
