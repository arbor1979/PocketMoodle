package com.dandian.pocketmoodle.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 学生实体�?
 * 
 * @Title Student.java
 * @Description: TODO
 * 
 * @author Zecker
 * @date 2013-11-7 下午4:28:07
 * @version V1.0
 * @remark yanzy 无学生头像字段，学生出勤率字�?
 */
@DatabaseTable(tableName="Student")
public class Student implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(id = true)
	private String id;
	@DatabaseField
	private String studentID;
	@DatabaseField
	private String password;
	@DatabaseField
	private String name;
	@DatabaseField
	private String className;
	@DatabaseField
	private String gender;
	@DatabaseField
	private String phone;
	@DatabaseField
	private String email;
	@DatabaseField
	private String dormitory;
	@DatabaseField
	private String parentName;
	@DatabaseField
	private String parentPhone;
	@DatabaseField
	private String homeAddress;
	@DatabaseField
	private String remark;
	@DatabaseField
	private String status;
	@DatabaseField
	private int isModify=0;

	private String stuLetter;
	
	private String attence = "出勤"; //考勤状�?�，用于记录考勤情况
	@DatabaseField
	private String picImage;
	public Student() {

	}
	public static List<Student> toList(JSONArray ja) {
		List<Student> result = new ArrayList<Student>();
		System.out.println("ja.length"+ja.length()+"List<Student> toListssss");
		Student info = null;
		if(ja.length()==0){
			System.out.println("没有Student数据");
		}else{
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				info = new Student(jo);
				result.add(info);
			}
			return result;
		}
		return null;
	}
	public static List<Student> toList(net.minidev.json.JSONArray ja) {
		List<Student> result = new ArrayList<Student>();
		System.out.println("ja.length"+ja.size()+"List<Student> toListssss");
		Student info = null;
		if(ja.size()==0){
			System.out.println("没有Student数据");
		}else{
			for (int i = 0; i < ja.size(); i++) {
				net.minidev.json.JSONObject jo = (net.minidev.json.JSONObject) ja.get(i);
				info = new Student(jo);
				result.add(info);
			}
			return result;
		}
		return null;
	}
	public Student(JSONObject jo) {
		id = jo.optString("编号");
		studentID = jo.optString("学号");
		password = jo.optString("密码");
		name = jo.optString("姓名");
		className = jo.optString("班级");
		gender = jo.optString("性别");
		phone = jo.optString("学生电话");
		email = jo.optString("学生邮箱");
		dormitory = jo.optString("学生宿舍");
		parentName = jo.optString("家长姓名");
		parentPhone = jo.optString("家长电话");
		homeAddress = jo.optString("家庭住址");
		remark = jo.optString("备注");
		status = jo.optString("学生状�??");
		picImage = jo.optString("头像");
	}
	
	public Student(net.minidev.json.JSONObject jo) {
		id = String.valueOf(jo.get("编号"));
		studentID = String.valueOf(jo.get("学号"));
		password = String.valueOf(jo.get("密码"));
		name = String.valueOf(jo.get("姓名"));
		className = String.valueOf(jo.get("班级"));
		gender = String.valueOf(jo.get("性别"));
		phone = String.valueOf(jo.get("学生电话"));
		email = String.valueOf(jo.get("学生邮箱"));
		dormitory = String.valueOf(jo.get("学生宿舍"));
		parentName = String.valueOf(jo.get("家长姓名"));
		parentPhone = String.valueOf(jo.get("家长电话"));
		homeAddress = String.valueOf(jo.get("家庭住址"));
		remark = String.valueOf(jo.get("备注"));
		status =String.valueOf( jo.get("学生状�??"));
		picImage =String.valueOf( jo.get("头像"));
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
	 * 学号
	 * 
	 * @return
	 */
	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	/**
	 * 密码
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 姓名
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
	 * 班级
	 * 
	 * @return
	 */
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * 性别
	 * 
	 * @return
	 */
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * 学生电话
	 * 
	 * @return
	 */
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 学生邮箱
	 * 
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 学生宿舍
	 * 
	 * @return
	 */
	public String getDormitory() {
		return dormitory;
	}

	public void setDormitory(String dormitory) {
		this.dormitory = dormitory;
	}

	/**
	 * 家长姓名
	 * 
	 * @return
	 */
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		if(parentName!=null)
			this.parentName = parentName;
	}

	/**
	 * 家长电话
	 * 
	 * @return
	 */
	public String getParentPhone() {
		return parentPhone;
	}

	public void setParentPhone(String parentPhone) {
		if(parentPhone!=null)
			this.parentPhone = parentPhone;
	}

	/**
	 * 家庭住址
	 * 
	 * @return
	 */
	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		if(homeAddress!=null)
			this.homeAddress = homeAddress;
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

	/**
	 * 学生状�??
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
	
	public String getStuLetter() {
		return stuLetter;
	}
	public void setStuLetter(String stuLetter) {
		this.stuLetter = stuLetter;
	}
	public String getAttence() {
		return attence;
	}
	public void setAttence(String attence) {
		this.attence = attence;
	}
	public String getPicImage() {
		return picImage;
	}
	public void setPicImage(String picImage) {
		this.picImage = picImage;
	}

	
}
