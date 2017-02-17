package com.dandian.pocketmoodle.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 课堂测试题库列表数据
 * 
 * @author hfthink
 * 
 */
@DatabaseTable(tableName = "TestEntity")
public class TestEntity {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String subjectId; //上课记录编号
	@DatabaseField
	private String testName; //测验名称
	@DatabaseField
	private String topicName; //题目名称
	@DatabaseField
	private String answerStatus;//答题状�?? 
	@DatabaseField
	private String answer; //答案
	@DatabaseField
	private String remark; //备注
	@DatabaseField
	private String studentAnswer; //学生答题
	@DatabaseField
	private String testOption;
	
	public TestEntity() {
		
	}

	private TestEntity(JSONObject jo) {
		this.subjectId = jo.optString("老师上课记录编号");
		this.testName = jo.optString("测验名称");
		this.topicName = jo.optString("题目名称");
		this.answerStatus = jo.optString("答题状�??");
		this.answer = jo.optString("正确答案");
		this.remark = jo.optString("备注");
		this.studentAnswer = jo.optString("学生答题");
		this.testOption = jo.optString("题目");
	}
	private TestEntity(net.minidev.json.JSONObject jo) {
		this.subjectId = String.valueOf(jo.get("老师上课记录编号"));
		this.testName = String.valueOf(jo.get("测验名称"));
		this.topicName = String.valueOf(jo.get("题目名称"));
		this.answerStatus = String.valueOf(jo.get("答题状�??"));
		this.answer = String.valueOf(jo.get("正确答案"));
		this.remark = String.valueOf(jo.get("备注"));
		this.studentAnswer = String.valueOf(jo.get("学生答题"));
		this.testOption = String.valueOf(jo.get("题目"));
	}

	public static List<TestEntity> toList(JSONArray ja) {
		List<TestEntity> result = new ArrayList<TestEntity>();
		TestEntity info = null;

		for (int i = 0; i < ja.length(); i++) {
			JSONObject jo = ja.optJSONObject(i);
			info = new TestEntity(jo);
			result.add(info);
		}

		return result;

	}
	public static List<TestEntity> toList(net.minidev.json.JSONArray ja) {
		List<TestEntity> result = new ArrayList<TestEntity>();
		TestEntity info = null;

		for (int i = 0; i < ja.size(); i++) {
			net.minidev.json.JSONObject jo = (net.minidev.json.JSONObject)ja.get(i);
			info = new TestEntity(jo);
			result.add(info);
		}

		return result;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getAnswerStatus() {
		return answerStatus;
	}

	public void setAnswerStatus(String answerStatus) {
		this.answerStatus = answerStatus;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStudentAnswer() {
		return studentAnswer;
	}

	public void setStudentAnswer(String studentAnswer) {
		this.studentAnswer = studentAnswer;
	}

	public String getTestOption() {
		return testOption;
	}

	public void setTestOption(String testOption) {
		this.testOption = testOption;
	}

	
}
