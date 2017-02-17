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
		className = jo.optString("班级名称");
		seatNumber = jo.optString("座号");
		gender = jo.optString("性别");
		stuPhone = jo.optString("学生电话");
		stuEmail = jo.optString("电子邮件");
		dormitory = jo.optString("院系");
		relativeName = jo.optString("家长姓名");
		relativePhone = jo.optString("家长电话");
		address = jo.optString("城市");
		remark = jo.optString("国家");
		if(remark.endsWith("null"))
			remark="";
		stuStatus = jo.optString("学生状态");
		userNumber = jo.optString("用户唯一码");
		userImage = jo.optString("用户头像");
		//userType=jo.optString("用户类型");
		XingMing=jo.optString("XingMing");
		userGrade=jo.optString("用户评级");
		loginTime=jo.optString("登录时间");
		/*
		if (userNumber.indexOf("老师") > -1) {
			studentID = jo.optString("用户名");
			virtualClass = jo.optString("虚拟班级");
			seatNumber = jo.optString("排序号");
			className = jo.optString("部门");
			stuPhone = jo.optString("手机");
			chargeClass = jo.optString("所带班级");
			chargeKeCheng= jo.optString("所带课程");
		}
		if (userNumber.indexOf("家长") > -1) {
			stuPhone = jo.optString("手机");
		}
		*/
		chargeClass="";
		chargeKeCheng="";
		userType="";
		JSONArray kecheng=jo.optJSONArray("选修课程");
		JSONArray jiaose=jo.optJSONArray("选修角色");
		if(kecheng!=null)
		{
			for(int i=0;i<kecheng.length();i++)
			{

				try {
					String role=jiaose.getString(i);
					kechengArray.add(kecheng.getString(i));
					roleArray.add(role);
					if(role.equals("学生"))
					{
						if(chargeClass.length()>0)
							chargeClass+=";";
						chargeClass+=kecheng.getString(i);
					}
					else if(role.equals("无编辑权教师") || role.equals("有编辑权限教师") || role.equals("教师") || role.equals("课程创建者") || role.equals("管理员"))
					{
						if(chargeKeCheng.length()>0)
							chargeKeCheng+=";";
						chargeKeCheng+=kecheng.getString(i);
					}
					if(role.equals("无编辑权教师") || role.equals("有编辑权限教师"))
						role="教师";
					if(userType.indexOf(role)==-1)
					{
						if(userType.length()>0)
							userType+=",";
						userType+=role;
					}
						
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		schoolName=jo.optString("单位名称");
		
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

	public ContactsMember(net.minidev.json.JSONObject jo){
		number = String.valueOf(jo.get("编号"));
		studentID = String.valueOf(jo.get("学号"));
		password = String.valueOf(jo.get("密码"));
		name = String.valueOf(jo.get("姓名"));
		className = String.valueOf(jo.get("班级"));
		seatNumber = String.valueOf(jo.get("座号"));
		gender = String.valueOf(jo.get("性别"));
		stuPhone = String.valueOf(jo.get("学生电话"));
		stuEmail = String.valueOf(jo.get("学生邮箱")==null?"":jo.get("学生邮箱"));
		
		dormitory = String.valueOf(jo.get("学生宿舍"));
		relativeName = String.valueOf(jo.get("家长姓名")==null?"":jo.get("家长姓名"));
		relativePhone = String.valueOf(jo.get("家长电话")==null?"":jo.get("家长电话"));
		address = String.valueOf(jo.get("家庭住址")==null?"":jo.get("家长电话"));
		remark = String.valueOf(jo.get("备注")==null?"":jo.get("备注"));
		stuStatus = String.valueOf(jo.get("学生状态"));
		userNumber = String.valueOf(jo.get("用户唯一码"));
		userImage = String.valueOf(jo.get("用户头像"));
		userType=String.valueOf(jo.get("用户类型"));
		XingMing=String.valueOf(jo.get("XingMing"));
		userGrade=String.valueOf(jo.get("用户评级"));
		loginTime=String.valueOf(jo.get("登录时间")==null?"":jo.get("登录时间"));
		if (userNumber.indexOf("老师") > -1) {
			studentID = String.valueOf(jo.get("用户名"));
			virtualClass = String.valueOf(jo.get("虚拟班级"));
			seatNumber = String.valueOf(jo.get("排序号"));
			className = String.valueOf(jo.get("部门"));
			stuPhone = String.valueOf(jo.get("手机"));
			chargeClass = String.valueOf(jo.get("所带班级"));
			chargeKeCheng= String.valueOf(jo.get("所带课程"));
		}
		if (userNumber.indexOf("家长") > -1) {
			stuPhone = String.valueOf(jo.get("手机"));
		}
		
		schoolName=String.valueOf(jo.get("单位名称"));
		
		userType="";
		net.minidev.json.JSONArray kecheng=(net.minidev.json.JSONArray)jo.get("选修课程");
		net.minidev.json.JSONArray jiaose=(net.minidev.json.JSONArray)jo.get("选修角色");
		if(kecheng!=null)
		{
			for(int i=0;i<kecheng.size();i++)
			{
				String role=String.valueOf(jiaose.get(i));
				kechengArray.add(String.valueOf(kecheng.get(i)));
				roleArray.add(role);
				
				if(role.equals("无编辑权教师") || role.equals("有编辑权限教师"))
					role="教师";
				if(userType.indexOf(role)==-1)
				{
					if(userType.length()>0)
						userType+=",";
					userType+=role;
				}
			}
		}
					
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
