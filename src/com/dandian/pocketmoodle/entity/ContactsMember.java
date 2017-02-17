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
		number = jo.optString("���");
		studentID = jo.optString("ѧ��");
		password = jo.optString("����");
		name = jo.optString("����");
		className = jo.optString("�༶����");
		seatNumber = jo.optString("����");
		gender = jo.optString("�Ա�");
		stuPhone = jo.optString("ѧ���绰");
		stuEmail = jo.optString("�����ʼ�");
		dormitory = jo.optString("Ժϵ");
		relativeName = jo.optString("�ҳ�����");
		relativePhone = jo.optString("�ҳ��绰");
		address = jo.optString("����");
		remark = jo.optString("����");
		if(remark.endsWith("null"))
			remark="";
		stuStatus = jo.optString("ѧ��״̬");
		userNumber = jo.optString("�û�Ψһ��");
		userImage = jo.optString("�û�ͷ��");
		//userType=jo.optString("�û�����");
		XingMing=jo.optString("XingMing");
		userGrade=jo.optString("�û�����");
		loginTime=jo.optString("��¼ʱ��");
		/*
		if (userNumber.indexOf("��ʦ") > -1) {
			studentID = jo.optString("�û���");
			virtualClass = jo.optString("����༶");
			seatNumber = jo.optString("�����");
			className = jo.optString("����");
			stuPhone = jo.optString("�ֻ�");
			chargeClass = jo.optString("�����༶");
			chargeKeCheng= jo.optString("�����γ�");
		}
		if (userNumber.indexOf("�ҳ�") > -1) {
			stuPhone = jo.optString("�ֻ�");
		}
		*/
		chargeClass="";
		chargeKeCheng="";
		userType="";
		JSONArray kecheng=jo.optJSONArray("ѡ�޿γ�");
		JSONArray jiaose=jo.optJSONArray("ѡ�޽�ɫ");
		if(kecheng!=null)
		{
			for(int i=0;i<kecheng.length();i++)
			{

				try {
					String role=jiaose.getString(i);
					kechengArray.add(kecheng.getString(i));
					roleArray.add(role);
					if(role.equals("ѧ��"))
					{
						if(chargeClass.length()>0)
							chargeClass+=";";
						chargeClass+=kecheng.getString(i);
					}
					else if(role.equals("�ޱ༭Ȩ��ʦ") || role.equals("�б༭Ȩ�޽�ʦ") || role.equals("��ʦ") || role.equals("�γ̴�����") || role.equals("����Ա"))
					{
						if(chargeKeCheng.length()>0)
							chargeKeCheng+=";";
						chargeKeCheng+=kecheng.getString(i);
					}
					if(role.equals("�ޱ༭Ȩ��ʦ") || role.equals("�б༭Ȩ�޽�ʦ"))
						role="��ʦ";
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
		schoolName=jo.optString("��λ����");
		
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
		number = String.valueOf(jo.get("���"));
		studentID = String.valueOf(jo.get("ѧ��"));
		password = String.valueOf(jo.get("����"));
		name = String.valueOf(jo.get("����"));
		className = String.valueOf(jo.get("�༶"));
		seatNumber = String.valueOf(jo.get("����"));
		gender = String.valueOf(jo.get("�Ա�"));
		stuPhone = String.valueOf(jo.get("ѧ���绰"));
		stuEmail = String.valueOf(jo.get("ѧ������")==null?"":jo.get("ѧ������"));
		
		dormitory = String.valueOf(jo.get("ѧ������"));
		relativeName = String.valueOf(jo.get("�ҳ�����")==null?"":jo.get("�ҳ�����"));
		relativePhone = String.valueOf(jo.get("�ҳ��绰")==null?"":jo.get("�ҳ��绰"));
		address = String.valueOf(jo.get("��ͥסַ")==null?"":jo.get("�ҳ��绰"));
		remark = String.valueOf(jo.get("��ע")==null?"":jo.get("��ע"));
		stuStatus = String.valueOf(jo.get("ѧ��״̬"));
		userNumber = String.valueOf(jo.get("�û�Ψһ��"));
		userImage = String.valueOf(jo.get("�û�ͷ��"));
		userType=String.valueOf(jo.get("�û�����"));
		XingMing=String.valueOf(jo.get("XingMing"));
		userGrade=String.valueOf(jo.get("�û�����"));
		loginTime=String.valueOf(jo.get("��¼ʱ��")==null?"":jo.get("��¼ʱ��"));
		if (userNumber.indexOf("��ʦ") > -1) {
			studentID = String.valueOf(jo.get("�û���"));
			virtualClass = String.valueOf(jo.get("����༶"));
			seatNumber = String.valueOf(jo.get("�����"));
			className = String.valueOf(jo.get("����"));
			stuPhone = String.valueOf(jo.get("�ֻ�"));
			chargeClass = String.valueOf(jo.get("�����༶"));
			chargeKeCheng= String.valueOf(jo.get("�����γ�"));
		}
		if (userNumber.indexOf("�ҳ�") > -1) {
			stuPhone = String.valueOf(jo.get("�ֻ�"));
		}
		
		schoolName=String.valueOf(jo.get("��λ����"));
		
		userType="";
		net.minidev.json.JSONArray kecheng=(net.minidev.json.JSONArray)jo.get("ѡ�޿γ�");
		net.minidev.json.JSONArray jiaose=(net.minidev.json.JSONArray)jo.get("ѡ�޽�ɫ");
		if(kecheng!=null)
		{
			for(int i=0;i<kecheng.size();i++)
			{
				String role=String.valueOf(jiaose.get(i));
				kechengArray.add(String.valueOf(kecheng.get(i)));
				roleArray.add(role);
				
				if(role.equals("�ޱ༭Ȩ��ʦ") || role.equals("�б༭Ȩ�޽�ʦ"))
					role="��ʦ";
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
