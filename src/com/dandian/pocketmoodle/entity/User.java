package com.dandian.pocketmoodle.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dandian.pocketmoodle.util.DateHelper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "User")
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9203715820163398998L;
	/**
	 * 
	 */
	
	@DatabaseField(id = true)
	private String id;
	@DatabaseField
	private String username;
	@DatabaseField
	private String name;
	@DatabaseField
	private String nickname;
	@DatabaseField
	private String department;
	@DatabaseField
	private String gender;
	@DatabaseField
	private String birthday;
	@DatabaseField
	private String phone;
	@DatabaseField
	private String email;
	@DatabaseField
	private String withClass;
	@DatabaseField
	private String withCourse;
	@DatabaseField
	private String companyName;
	@DatabaseField
	private String loginStatus;
	@DatabaseField
	private String loginTime;
	@DatabaseField
	private int isModify;
	@DatabaseField
	private String allowModifyField;
	@DatabaseField
	private String checkCode;
	@DatabaseField
	private String userImage;
	@DatabaseField
	private String virtualClass;
	@DatabaseField
	private String userNumber;
	@DatabaseField
	private String domain;
	@DatabaseField
	private String userType;
	@DatabaseField
	private String recentlyUsedEquipment;
	@DatabaseField
	private String iosDeviceToken;
	@DatabaseField
	private String certificationPath;
	@DatabaseField
	private String userRating;
	@DatabaseField
	private String mainRole;
	@DatabaseField
	private String secondaryRole;
	@DatabaseField
	private String sortNumber;
	@DatabaseField
	private String banLogin;
	@DatabaseField
	private String password;
	@DatabaseField
	private String sClass;
	@DatabaseField
	private String sPhone;
	@DatabaseField
	private String sDormitory;
	@DatabaseField
	private String sEmail;
	@DatabaseField
	private String sStatus;
	@DatabaseField
	private String pName;
	@DatabaseField
	private String pPhone;
	@DatabaseField
	private String homeAddress;
	@DatabaseField
	private String remark;
	@DatabaseField
	private String rootDomain;
	@DatabaseField
	private String company;
	
	private String latestAddress;
	private JSONArray functions;
	private boolean downloadfiles;
	private boolean uploadfiles;
	private String release;
	private String version;
	private JSONArray advancedfeatures;
	private boolean usercanmanageownfiles;
	private int userquota;
	private int usermaxuploadfilesize;
	private int userhomepage;
	private String token;
	public String getLatestAddress() {
		return latestAddress;
	}

	public void setLatestAddress(String latestAddress) {
		this.latestAddress = latestAddress;
	}

	public void setLoginEquipments(Collection<Equipment> loginEquipments) {
		this.loginEquipments = loginEquipments;
	}

	// private List<Equipment> loginEquipments;
	@ForeignCollectionField
	/** 
	 * 这里需要注意的是：属性类型只能是ForeignCollection<T>或者Collection<T> 
	 * 如果需要懒加载（延迟加载）可以在@ForeignCollectionField加上参数eager=false 
	 * 这个属性也就说明一个用户对应着多个设备
	 */
	private Collection<Equipment> loginEquipments;

	public User() {
		userType="";
		userNumber="";
		username="";
		name="";
		userImage="";
		latestAddress="";
	}

	@SuppressWarnings("unchecked")
	public User(JSONObject jo) {
		//userType = jo.optString("用户类型");
		id = jo.optString("userid");
		username = jo.optString("username");
		name = jo.optString("firstname");
		//password = jo.optString("密码");
		//nickname = jo.optString("呢称");
		department = jo.optString("院系");
		//gender = jo.optString("性别");
		//birthday = jo.optString("出生日期");
		phone = jo.optString("手机");
		email = jo.optString("电子邮件");
		//withClass = jo.optString("所带班级");
		//withCourse = jo.optString("所带课程");
		//loginStatus = jo.optString("登录状态");
		loginTime = jo.optString("登录时间");
		//isModify = jo.optInt("是否修改");
		//allowModifyField = jo.optString("允许用户修改自身字段列表");
		checkCode = jo.optString("用户较验码");
		userImage = jo.optString("userpictureurl");
		virtualClass = jo.optString("lastname");
		userNumber = jo.optString("用户唯一码");
		domain = jo.optString("siteurl");
		functions=jo.optJSONArray("functions");
		downloadfiles=jo.optBoolean("downloadfiles");
		uploadfiles=jo.optBoolean("uploadfiles");
		release = jo.optString("release");
		version = jo.optString("version");
		advancedfeatures=jo.optJSONArray("advancedfeatures");
		usercanmanageownfiles=jo.optBoolean("usercanmanageownfiles");
		userquota=jo.optInt("userquota");
		usermaxuploadfilesize=jo.optInt("usermaxuploadfilesize");
		userhomepage=jo.optInt("userhomepage");
		token=jo.optString("token");
		//banLogin = jo.optString("禁止登录");
		//sortNumber = jo.optString("排序号");
		//sDormitory = jo.optJSONArray("选修课程").toString();
		//mainRole = jo.optJSONArray("选修角色").toString();
		sDormitory="";
		mainRole="";
		userType="";
		JSONArray kecheng=jo.optJSONArray("选修课程");
		JSONArray jiaose=jo.optJSONArray("选修角色");
		if(kecheng!=null)
		{
		for(int i=0;i<kecheng.length();i++)
		{
			try {
				String role=jiaose.getString(i);
				if(role.equals("学生"))
				{
					if(sDormitory.length()>0)
						sDormitory+=";";
					sDormitory+=kecheng.getString(i);
				}
				else if(role.equals("无编辑权教师") || role.equals("有编辑权限教师") || role.equals("教师") || role.equals("课程创建者") || role.equals("管理员"))
				{
					if(mainRole.length()>0)
						mainRole+=";";
					mainRole+=kecheng.getString(i);
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
		
		//secondaryRole = jo.optString("辅助角色");
		//userRating = jo.optString("用户评级");
		//certificationPath = jo.optString("认证路径");
		//iosDeviceToken = jo.optString("IosDeviceToken");
		//recentlyUsedEquipment = jo.optString("最近使用设备");
		//loginEquipments = new ArrayList<Equipment>();
		/*
		JSONObject jole = jo.optJSONObject("用户使用设备");
		if (jole != null) {
			Iterator<String> keys = jole.keys();
			while (keys.hasNext()) {
				String str = keys.next();
				Equipment eq = new Equipment(jole.optJSONObject(str));
				eq.setUser(this);
				loginEquipments.add(eq);
			}
		}
		*/
		companyName = jo.optString("单位名称");
		company = jo.optString("国家");
		if(company.equals("null"))
			company="";
		sClass = jo.optString("班级名称");
		//sPhone = jo.optString("学生电话");
		
		//sEmail = jo.optString("学生邮箱");
		//sStatus = jo.optString("学生状态");
		//pName = jo.optString("家长姓名");
		//pPhone = jo.optString("家长电话");
		homeAddress = jo.optString("城市");
		//remark = jo.optString("备注");
		//rootDomain = jo.optString("更域名");
		//latestAddress="";
	}

	public JSONArray getFunctions() {
		return functions;
	}

	public void setFunctions(JSONArray functions) {
		this.functions = functions;
	}

	public boolean isDownloadfiles() {
		return downloadfiles;
	}

	public void setDownloadfiles(boolean downloadfiles) {
		this.downloadfiles = downloadfiles;
	}

	public boolean isUploadfiles() {
		return uploadfiles;
	}

	public void setUploadfiles(boolean uploadfiles) {
		this.uploadfiles = uploadfiles;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public JSONArray getAdvancedfeatures() {
		return advancedfeatures;
	}

	public void setAdvancedfeatures(JSONArray advancedfeatures) {
		this.advancedfeatures = advancedfeatures;
	}

	public boolean isUsercanmanageownfiles() {
		return usercanmanageownfiles;
	}

	public void setUsercanmanageownfiles(boolean usercanmanageownfiles) {
		this.usercanmanageownfiles = usercanmanageownfiles;
	}

	public int getUserquota() {
		return userquota;
	}

	public void setUserquota(int userquota) {
		this.userquota = userquota;
	}

	public int getUsermaxuploadfilesize() {
		return usermaxuploadfilesize;
	}

	public void setUsermaxuploadfilesize(int usermaxuploadfilesize) {
		this.usermaxuploadfilesize = usermaxuploadfilesize;
	}

	public int getUserhomepage() {
		return userhomepage;
	}

	public void setUserhomepage(int userhomepage) {
		this.userhomepage = userhomepage;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User(net.minidev.json.JSONObject jo) {
		userType = String.valueOf(jo.get("用户类型"));
		id = String.valueOf(jo.get("编号"));
		username = String.valueOf(jo.get("用户名"));
		name = String.valueOf(jo.get("姓名"));
		password = String.valueOf(jo.get("密码"));
		nickname = String.valueOf(jo.get("呢称"));
		department = String.valueOf(jo.get("部门"));
		gender = String.valueOf(jo.get("性别"));
		birthday = String.valueOf(jo.get("出生日期"));
		phone = String.valueOf(jo.get("手机"));
		email = String.valueOf(jo.get("电邮"));
		withClass = String.valueOf(jo.get("所带班级"));
		withCourse = String.valueOf(jo.get("所带课程"));
		loginStatus = String.valueOf(jo.get("登录状态"));
		loginTime = String.valueOf(jo.get("登录时间"));
		isModify = Integer.parseInt(String.valueOf(jo.get("是否修改")==null?"0":jo.get("是否修改")));
		allowModifyField = String.valueOf(jo.get("允许用户修改自身字段列表"));
		checkCode = String.valueOf(jo.get("用户较验码"));
		userImage = String.valueOf(jo.get("用户头像"));
		virtualClass = String.valueOf(jo.get("虚拟班级"));
		userNumber = String.valueOf(jo.get("用户唯一码"));
		domain = String.valueOf(jo.get("域名"));
		banLogin = String.valueOf(jo.get("禁止登录"));
		sortNumber = String.valueOf(jo.get("排序号"));
		mainRole = String.valueOf(jo.get("主要角色"));
		secondaryRole = String.valueOf(jo.get("辅助角色"));
		userRating = String.valueOf(jo.get("用户评级"));
		certificationPath = String.valueOf(jo.get("认证路径"));
		iosDeviceToken = String.valueOf(jo.get("IosDeviceToken"));
		recentlyUsedEquipment = String.valueOf(jo.get("最近使用设备"));
		loginEquipments = new ArrayList<Equipment>();
		net.minidev.json.JSONObject jole = (net.minidev.json.JSONObject)jo.get("用户使用设备");
		if (jole != null) {
			Set<String> keyset=jole.keySet();
			Iterator<String> keys = keyset.iterator();
			while (keys.hasNext()) {
				String str = keys.next();
				Equipment eq = new Equipment((net.minidev.json.JSONObject)jole.get(str));
				eq.setUser(this);
				loginEquipments.add(eq);
			}
		}

		companyName = String.valueOf(jo.get("单位名称"));
		company = String.valueOf(jo.get("单位"));
		sClass = String.valueOf(jo.get("班级"));
		sPhone = String.valueOf(jo.get("学生电话"));
		sDormitory = String.valueOf(jo.get("学生宿舍"));
		sEmail = String.valueOf(jo.get("学生邮箱"));
		sStatus = String.valueOf(jo.get("学生状态"));
		pName = String.valueOf(jo.get("家长姓名"));
		pPhone = String.valueOf(jo.get("家长电话"));
		homeAddress = String.valueOf(jo.get("家庭住址"));
		remark = String.valueOf(jo.get("备注"));
		rootDomain = String.valueOf(jo.get("更域名"));
		latestAddress="";
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
	 * 用户名
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
	 * 昵称
	 * 
	 * @return
	 */
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * 部门
	 * 
	 * @return
	 */
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
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
	 * 出生日期
	 * 
	 * @return
	 */
	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	/**
	 * 手机
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
	 * 电邮
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
	 * 所带班级
	 * 
	 * @return
	 */
	public String getWithClass() {
		return withClass;
	}

	public void setWithClass(String withClass) {
		this.withClass = withClass;
	}

	/**
	 * 所带课程
	 * 
	 * @return
	 */
	public String getWithCourse() {
		return withCourse;
	}

	public void setWithCourse(String withCourse) {
		this.withCourse = withCourse;
	}

	/**
	 * 单位名称
	 * 
	 * @return
	 */
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * 登录状态
	 * 
	 * @return
	 */
	public String getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}

	/**
	 * 登录时间
	 * 
	 * @return
	 */
	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
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

	/**
	 * 允许修改字段
	 * 
	 * @return
	 */
	public String getAllowModifyField() {
		return allowModifyField;
	}

	public void setAllowModifyField(String allowModifyField) {
		this.allowModifyField = allowModifyField;
	}

	/**
	 * 用户校验码
	 * 
	 * @return
	 */
	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	/**
	 * 用户头像
	 * 
	 * @return
	 */
	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	/**
	 * 虚拟班级
	 * 
	 * @return
	 */
	public String getVirtualClass() {
		return virtualClass;
	}

	public void setVirtualClass(String virtualClass) {
		this.virtualClass = virtualClass;
	}

	/**
	 * 用户唯一码
	 * 
	 * @return
	 */
	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	/**
	 * 域名
	 * 
	 * @return
	 */
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * 用户类型
	 * 
	 * @return
	 */
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	/**
	 * 用户使用设备
	 * 
	 * @return
	 */
	public Collection<Equipment> getLoginEquipments() {
		return loginEquipments;
	}

	public void setLoginEquipments(List<Equipment> loginEquipments) {
		this.loginEquipments = loginEquipments;
	}

	/**
	 * 用户最经使用设备
	 * 
	 * @return
	 */
	public String getRecentlyUsedEquipment() {
		return recentlyUsedEquipment;
	}

	public void setRecentlyUsedEquipment(String recentlyUsedEquipment) {
		this.recentlyUsedEquipment = recentlyUsedEquipment;
	}

	/**
	 * 功能描述:
	 * 
	 * @return
	 */
	public String getIosDeviceToken() {
		return iosDeviceToken;
	}

	public void setIosDeviceToken(String iosDeviceToken) {
		this.iosDeviceToken = iosDeviceToken;
	}

	/**
	 * 认证路径
	 * 
	 * @return
	 */
	public String getCertificationPath() {
		return certificationPath;
	}

	public void setCertificationPath(String certificationPath) {
		this.certificationPath = certificationPath;
	}

	/**
	 * 用户评级
	 * 
	 * @return
	 */
	public String getUserRating() {
		return userRating;
	}

	public void setUserRating(String userRating) {
		this.userRating = userRating;
	}

	/**
	 * 主要角色
	 * 
	 * @return
	 */
	public String getMainRole() {
		return mainRole;
	}

	public void setMainRole(String mainRole) {
		this.mainRole = mainRole;
	}

	/**
	 * 辅助角色
	 * 
	 * @return
	 */
	public String getSecondaryRole() {
		return secondaryRole;
	}

	public void setSecondaryRole(String secondaryRole) {
		this.secondaryRole = secondaryRole;
	}

	/**
	 * 排序号
	 * 
	 * @return
	 */
	public String getSortNumber() {
		return sortNumber;
	}

	public void setSortNumber(String sortNumber) {
		this.sortNumber = sortNumber;
	}

	/**
	 * 禁止登录
	 * 
	 * @return
	 */
	public String getBanLogin() {
		return banLogin;
	}

	public void setBanLogin(String banLogin) {
		this.banLogin = banLogin;
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
	 * 学生班级
	 * 
	 * @return
	 */
	public String getsClass() {
		return sClass;
	}

	public void setsClass(String sClass) {
		this.sClass = sClass;
	}

	/**
	 * 学生电话
	 * 
	 * @return
	 */
	public String getsPhone() {
		return sPhone;
	}

	public void setsPhone(String sPhone) {
		this.sPhone = sPhone;
	}

	/**
	 * 学生宿舍
	 * 
	 * @return
	 */
	public String getsDormitory() {
		return sDormitory;
	}

	public void setsDormitory(String sDormitory) {
		this.sDormitory = sDormitory;
	}

	/**
	 * 学生邮箱
	 * 
	 * @return
	 */
	public String getsEmail() {
		return sEmail;
	}

	public void setsEmail(String sEmail) {
		this.sEmail = sEmail;
	}

	/**
	 * 学生状态
	 * 
	 * @return
	 */
	public String getsStatus() {
		return sStatus;
	}

	public void setsStatus(String sStatus) {
		this.sStatus = sStatus;
	}

	/**
	 * 家长姓名
	 * 
	 * @return
	 */
	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	/**
	 * 家长电话
	 * 
	 * @return
	 */
	public String getpPhone() {
		return pPhone;
	}

	public void setpPhone(String pPhone) {
		this.pPhone = pPhone;
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
	 * 根域名
	 * 
	 * @return
	 */
	public String getRootDomain() {
		return rootDomain;
	}

	public void setRootDomain(String rootDomain) {
		this.rootDomain = rootDomain;
	}

	/**
	 * 单位
	 * 
	 * @return
	 */
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
}
