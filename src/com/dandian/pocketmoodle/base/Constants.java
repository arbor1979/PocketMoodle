package com.dandian.pocketmoodle.base;



public class Constants {

	// 向服务器提交参数�?
	public static final String PARAMS_DATA = "DATA";
	/**
	 * 学校服务器地�?
	 */
	public static final String PREF_SCHOOL_DOMAIN = "pref.school_domain";
	/**
	 * 用户
	 */
	public static final String PREF_SITE_URL = "pref.check_site_url";
	public static final String PREF_LOGIN_NAME = "pref.check_login_name";
	/**
	 * 密码
	 */
	public static final String PREF_LOGIN_PASS = "pref.check_login_pass";
	/**
	 * 判断是否是先行体�?
	 */
	public static final String PREF_CHECK_TEST = "pref.check_test";

	/**
	 * 判断软件是否运行�?
	 */
	public static final String PREF_CHECK_RUN = "pref.check_run";
	// 保存参数
	/**
	 * 用户校验�?
	 */
	public static final String PREF_CHECK_CODE = "pref.check_code";
	
	public static final String PREF_CHECK_USERID = "pref.check_userid";
	public static final String PREF_CHECK_SCLASS = "pref.check_sclass";
	public static final String PREF_CHECK_REALNAME = "pref.check_realname";
	/**
	 * 用户唯一�?
	 */
	public static final String PREF_CHECK_HOSTID = "pref.check_hostid";
	/**
	 * 用户类型
	 */
	public static final String PREF_CHECK_USERTYPE = "pref.check_usertype";
	public static final String PREF_CHECK_TOKEN = "pref.check_token";
	/**
	 * 初始化基�?数据标记
	 */
	public static final String PREF_INIT_BASEDATE_FLAG = "pref.init_basedate_flag";

	
	/**
	 * ����ɫ
	 */
	public static final String PREF_THEME_BACKGROUNDCOLOR = "pref.theme_backgroundcolor";
	public static final String PREF_THEME_TABBARCOLOR = "pref.theme_tabbarcolor";
	public static final String PREF_THEME_NAVBARCOLOR = "pref.theme_navibarcolor";
	public static final String PREF_THEME_MENUCOLOR = "pref.theme_menucolor";
	public static final String PREF_THEME_LISTCOLOR = "pref.theme_listcolor";
	public static final String PREF_THEME_TITLE = "pref.theme_title";
	/**
	 * 初始化联系人标记
	 */
	public static final String PREF_INIT_CONTACT_FLAG = "pref.init_contact_flag";
	public static final String PREF_INIT_CONTACT_STR = "pref.init_contact_str";
	public static final String PREF_INIT_DATA_STR = "pref.init_data_str";
	
	/**
	 * 初始化标�?
	 */
	public static final String PREF_BAIDU_USERID = "pref.baidu_userid";
	/**
	 * 表情数量
	 */
	public static final int express_counts = 107;

	/**
	 * �?带课�?
	 */
	public static final String PREF_CURRICULUMS = "pref.curriculums";
	/**
	 * �?带班�?
	 */
	public static final String PREF_CLASSES = "pref.classes";
	/**
	 * 单位名称
	 */
	public static final String PREF_COMPANY_NAME = "pref.company_name";
	/**
	 * 考勤名称
	 */
	public static final String PREF_WORK_ATTENDANCES = "pref.work_attendances";
	/**
	 * 考勤分�??
	 */
	public static final String PREF_WORK_ATTENDANCE_VALUES = "pref.work_attendance_values";
	/**
	 * 允许教师修改教师上课记录信息字段
	 */
	public static final String PREF_ALLOW_SCHOOL_RECORDKEYS_STR = "pref.allow_school_recordkeys_str";
	/**
	 * 允许教师修改教师上课记录信息字段_总结
	 */
	public static final String PREF_ALLOW_SCHOOL_RECORD_SUMMARYKEYS_STR = "pref.allow_school_record_summarykeys_str";
	/**
	 * 允许教师修改教师上课记录信息字段_考勤
	 */
	public static final String PREF_ALLOW_SCHOOL_RECORDWORK_ATTENDANCEKEYS_STR = "pref.allow_school_record_work_attendancekeys_str";
	/**
	 * 当前周次
	 */
	public static final String PREF_CURRENT_WEEK = "pref.current_week";
	/**
	 * 选择周次
	 */
	public static final String PREF_SELECTED_WEEK = "pref.selected_week";
	/**
	 * �?大周�?
	 */
	public static final String PREF_MAX_WEEK = "pref.max_week";
	/**
	 * 当前学期
	 */
	public static final String PREF_CUR_XUEQI = "pref.cur_xueqi";

	/**
	 * 获取图片
	 */
	public static final String GET_PICTURE = "get_picture";

	/**
	 * 查看详图或删除图�?
	 */
	public static final String DEL_OR_LOOK_PICTURE = "del_or_look_picture";
	/**
	 * 当前周次
	 */
	public static int currentWeek = 0;
	/**
	 * 选择周次
	 */
	public static int selectedWeek = 0;
	/**
	 * 选择周次
	 */
	public static int maxWeek = 0;
	
	//课件�?大文件大�?
	public static int kejianMaxSize =1024*1024*100;
	// public static int getCurrentWeek() {
	// return currentWeek;
	// }
	// public static void setCurrentWeek(int currentWeek) {
	// System.out.println("Constants.currentWeek:"+Constants.currentWeek);
	// Constants.currentWeek = currentWeek;
	// System.out.println("Constants.currentWeek:"+Constants.currentWeek);
	// }
	// public static int getSelectedWeek() {
	// return selectedWeek;
	// }
	// public static void setSelectedWeek(int selectedWeek) {
	// System.out.println("Constants.selectedWeek:"+Constants.selectedWeek);
	// Constants.selectedWeek = selectedWeek;
	// System.out.println("Constants.selectedWeek:"+Constants.selectedWeek);
	// }
	// public static int getMaxWeek() {
	// return maxWeek;
	// }
	// public static void setMaxWeek(int maxWeek) {
	// System.out.println("Constants.maxWeek:"+Constants.maxWeek);
	// Constants.maxWeek = maxWeek;
	// System.out.println("Constants.maxWeek:"+Constants.maxWeek);
	// }
}
