package com.dandian.pocketmoodle.entity;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="ContactsMember")
public class ContactsMember implements Serializable,Cloneable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(generatedId= true)
	private int id;
	@DatabaseField
	private String number;
	@DatabaseField
	private String studentID;
	@DatabaseField
	private String password;
	@DatabaseField
	private String name;
	@DatabaseField
	private String className;
	@DatabaseField
	private String seatNumber;
	@DatabaseField
	private String gender;
	@DatabaseField
	private String stuPhone;
	@DatabaseField
	private String stuEmail;
	@DatabaseField
	private String dormitory;
	@DatabaseField
	private String relativeName;
	@DatabaseField
	private String relativePhone;
	@DatabaseField
	private String address;
	@DatabaseField
	private String remark;
	@DatabaseField
	private String stuStatus;
	@DatabaseField
	private String userNumber;
	@DatabaseField
	private String userImage;
	
	@DatabaseField
	private String userType;
	@DatabaseField
	private String chargeClass;
	@DatabaseField
	private String XingMing;
	@DatabaseField
	private String virtualClass;
	@DatabaseField
	private String userGrade;
	@DatabaseField
	private String loginTime;
	@DatabaseField
	private String chargeKeCheng;
	private String schoolName;
	private ArrayList<String> kechengArray=new ArrayList<String>();
	private ArrayList<String> roleArray=new ArrayList<String>();
	private String firstloginTime;
	private String description;
	private JSONArray courseTeacher,courseStudent;
	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public ContactsMember(){
		super();
	}
	
	public ContactsMember(JSONObject jo){
		number = jo.optString("编号");
		studentID = jo.optString("学号");
		password = jo.optString("密码");
		name = jo.optString("姓名");
		seatNumber = jo.optString("座号");
		gender = jo.optString("性别");
		stuPhone = jo.optString("学生电话");
		stuEmail = jo.optString("电子邮件");
		dormitory = jo.optString("部门");
		relativeName = jo.optString("家长姓名");
		relativePhone = jo.optString("家长电话");
		address = jo.optString("城市");
		remark = jo.optString("国家");
		if(remark.endsWith("null"))
			remark="";
		stuStatus = jo.optString("学生状态");
		userNumber = jo.optString("用户唯一码");
		userImage = jo.optString("用户头像");
		XingMing=jo.optString("XingMing");
		userGrade=jo.optString("用户评级");
		firstloginTime=jo.optString("首次登录");
		loginTime=jo.optString("最近登录");
		chargeKeCheng=jo.optString("管理课程");
		chargeClass=jo.optString("选修课程");
		userType=jo.optString("角色");
		schoolName=jo.optString("学校");
		description=jo.optString("描述");
		
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFirstloginTime() {
		return firstloginTime;
	}

	public void setFirstloginTime(String firstloginTime) {
		this.firstloginTime = firstloginTime;
	}

	public ArrayList<String> getKechengArray() {
		return kechengArray;
	}

	public void setKechengArray(ArrayList<String> kechengArray) {
		this.kechengArray = kechengArray;
	}

	public ArrayList<String> getRoleArray() {
		return roleArray;
	}

	public void setRoleArray(ArrayList<String> roleArray) {
		this.roleArray = roleArray;
	}

	
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getChargeClass() {
		return chargeClass;
	}

	public void setChargeClass(String chargeClass) {
		this.chargeClass = chargeClass;
	}

	public String getXingMing() {
		return XingMing;
	}

	public void setXingMing(String xingMing) {
		XingMing = xingMing;
	}

	public String getVirtualClass() {
		return virtualClass;
	}

	public void setVirtualClass(String virtualClass) {
		this.virtualClass = virtualClass;
	}

	public String getUserGrade() {
		return userGrade;
	}

	public void setUserGrade(String userGrade) {
		this.userGrade = userGrade;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSeatNumber() {
		return seatNumber;
	}
	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getStuPhone() {
		return stuPhone;
	}
	public void setStuPhone(String stuPhone) {
		this.stuPhone = stuPhone;
	}
	public String getStuEmail() {
		return stuEmail;
	}
	public void setStuEmail(String stuEmail) {
		this.stuEmail = stuEmail;
	}
	public String getDormitory() {
		return dormitory;
	}
	public void setDormitory(String dormitory) {
		this.dormitory = dormitory;
	}
	public String getRelativeName() {
		return relativeName;
	}
	public void setRelativeName(String relativeName) {
		this.relativeName = relativeName;
	}
	public String getRelativePhone() {
		return relativePhone;
	}
	public void setRelativePhone(String relativePhone) {
		this.relativePhone = relativePhone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStuStatus() {
		return stuStatus;
	}
	public void setStuStatus(String stuStatus) {
		this.stuStatus = stuStatus;
	}
	public String getUserNumber() {
		return userNumber;
	}
	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	public String getChargeKeCheng() {
		return chargeKeCheng;
	}

	public void setChargeKeCheng(String chargeKeCheng) {
		this.chargeKeCheng = chargeKeCheng;
	}
	public ContactsMember clone() throws CloneNotSupportedException {
		  return (ContactsMember) super.clone();
		 }

	
}
