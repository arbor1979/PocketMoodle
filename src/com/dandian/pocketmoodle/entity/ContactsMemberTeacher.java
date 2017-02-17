package com.dandian.pocketmoodle.entity;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="ContactsMemberTeacher")
public class ContactsMemberTeacher {
	@DatabaseField(generatedId= true)
	private int id;
	@DatabaseField
	private String number;
	@DatabaseField
	private String loginName;
	@DatabaseField
	private String name;
	@DatabaseField
	private String nickname;
	@DatabaseField
	private String department;
	@DatabaseField
	private String password;
	@DatabaseField
	private String orderNum;
	@DatabaseField
	private String sex;
	@DatabaseField
	private String birthday;
	@DatabaseField
	private String phone;
	@DatabaseField
	private String email;
	@DatabaseField
	private String mainRole;
	@DatabaseField
	private String aidRole;
	@DatabaseField
	private String prohibitLogin;
	@DatabaseField
	private String userNumber;
	@DatabaseField
	private String virtualClass;
	@DatabaseField
	private String XingMing;
	@DatabaseField
	private String chargeClass;
	@DatabaseField
	private String chargeKeCheng;
	public ContactsMemberTeacher(){
		
	}
	
	public ContactsMemberTeacher(JSONObject jo){
		number = jo.optString("编号");               
		loginName = jo.optString("用户�?");          
		name = jo.optString("姓名");                 
		nickname = jo.optString("呢称");             
		department = jo.optString("部门");           
		password = jo.optString("密码");             
		orderNum = jo.optString("排序�?");           
		sex = jo.optString("性别");                  
		birthday = jo.optString("出生日期");         
		phone = jo.optString("手机");                
		email = jo.optString("电邮");                
		mainRole = jo.optString("主要角色");         
		aidRole = jo.optString("辅助角色");          
		prohibitLogin = jo.optString("禁止登录");    
		userNumber = jo.optString("用户唯一�?");
		virtualClass = jo.optString("虚拟班级");
		XingMing= jo.optString("XingMing");
		chargeClass= jo.optString("�?带班�?");
		chargeKeCheng= jo.optString("�?带课�?");
	}
	
	
	
	public String getXingMing() {
		return XingMing;
	}

	public void setXingMing(String xingMing) {
		XingMing = xingMing;
	}

	public String getChargeClass() {
		return chargeClass;
	}

	public void setChargeClass(String chargeClass) {
		this.chargeClass = chargeClass;
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

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMainRole() {
		return mainRole;
	}

	public void setMainRole(String mainRole) {
		this.mainRole = mainRole;
	}

	public String getAidRole() {
		return aidRole;
	}

	public void setAidRole(String aidRole) {
		this.aidRole = aidRole;
	}

	public String getProhibitLogin() {
		return prohibitLogin;
	}

	public void setProhibitLogin(String prohibitLogin) {
		this.prohibitLogin = prohibitLogin;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public String getVirtualClass() {
		return virtualClass;
	}

	public void setVirtualClass(String virtualClass) {
		this.virtualClass = virtualClass;
	}
	
}
