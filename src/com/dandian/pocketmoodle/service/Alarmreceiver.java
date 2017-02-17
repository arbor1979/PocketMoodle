package com.dandian.pocketmoodle.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.dandian.pocketmoodle.CampusApplication;
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.activity.TabHostActivity;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.db.DatabaseHelper;
import com.dandian.pocketmoodle.db.InitData;
import com.dandian.pocketmoodle.entity.DownloadSubject;
import com.dandian.pocketmoodle.entity.MyClassSchedule;
import com.dandian.pocketmoodle.entity.NoticeClass;
import com.dandian.pocketmoodle.entity.Student;
import com.dandian.pocketmoodle.entity.StudentPic;
import com.dandian.pocketmoodle.entity.Suggestions;
import com.dandian.pocketmoodle.entity.TeacherInfo;
import com.dandian.pocketmoodle.entity.User;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.DateHelper;
import com.dandian.pocketmoodle.util.DialogUtility;
import com.dandian.pocketmoodle.util.PrefUtility;

public class Alarmreceiver extends BroadcastReceiver {
	private String TAG = "Alarmreceiver";
	DatabaseHelper database;
	SQLiteDatabase sdb;
	private Dao<MyClassSchedule, Integer> myClassScheduleDao;
	private Dao<TeacherInfo, Integer> teacherInfoDao;
	
	private Dao<Suggestions,Integer> suggestDao;
	private Dao<NoticeClass,Integer> noticeDao;
	private Dao<User, Integer> userDao;
	private Dao<DownloadSubject,Integer> downloadSubjectDao;
	List<TeacherInfo> teacherInfoList;
	List<Student> studentList;
	List<StudentPic> studentPicList;
	User userInfo;
	Suggestions suggestions;
	NoticeClass notices;
	DownloadSubject downloadSubject;
	public static Boolean isdialog=false;
	private String actionName,checkCode;;
	private Context context;
	private Location myLocation;
	private LocationManager locationManager;
	
	public static NotificationManager notificationManager;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		
		database = OpenHelperManager.getHelper(context, DatabaseHelper.class);
		sdb = database.getWritableDatabase();
		checkCode = intent.getStringExtra(Constants.PREF_CHECK_CODE);
		Log.d(TAG, "Alarmreceiver-------------checkCode"+checkCode);
		actionName = intent.getAction();
		Log.d(TAG, "Alarmreceiver-------------actionName"+actionName);
		try {
			myClassScheduleDao = database.getMyClassScheduleDao();
			teacherInfoDao=database.getTeacherInfoDao();
			userDao = database.getUserDao();
			suggestDao = database.getSuggestionsDao();
			noticeDao = database.getNoticeClassDao();
			downloadSubjectDao = database.getDownloadSubjectDao();
			
			if("initBaseData".equals(actionName)){
				InitData initData = new InitData(context,database, null,"refreshSubject",checkCode);
				initData.initAllInfo();
			}else if("initContactData".equals(actionName)){
				InitData initData = new InitData(context,database, null,"refreshContact",checkCode);
				initData.initContactInfo();
			}else if ("reportLocation".equals(actionName))
			{
				locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
				getNetLocation();
			}else if ("reminderMeClass".equals(actionName)) {
				/****************************** �γ����� begin ********************************************/
				String contentTitle = context.getString(R.string.Moodle_reminder);
				String contentText = "";
				//��ǰ����
				//boolean booleanReminderBeforeClass = PrefUtility.getBoolean("booleanReminderBeforeClass", false);
				//ÿ�տγ�����
				boolean booleanReminderDayClass = PrefUtility.getBoolean("booleanReminderDayClass", true);
				//ÿ������ʱ��
				
				//������ǰ����
				/*
				if (booleanReminderBeforeClass) {
					Date now10 = new Date(new Date().getTime() + 600000); //10���Ӻ��ʱ��
					String begintime = DateHelper.getDateString(now10, "HH:mm");
					TeacherInfo teacherInfo = teacherInfoDao.queryBuilder().where().eq("beginTime", begintime).queryForFirst();
					if (teacherInfo != null) {
						contentText = "����"+teacherInfo.getBeginTime()+"��"+teacherInfo.getCourseName()+"��,�༶��"+teacherInfo.getClassGrade()+" ���ң�"+teacherInfo.getClassroom();
						showDialog(intent, contentTitle, contentText);
					}
					
				}
				*/
				//����ÿ�տγ�����
				if (booleanReminderDayClass) {
					
					String preDay="ǰһ��";
					String theDay=""; 
					String dayStr="";
					if(preDay.equals("ǰһ��"))
					{
						theDay=DateHelper.getNextday("yyyy-MM-dd");
						dayStr=context.getString(R.string.tomorrow);
					}
					else
					{
						theDay=DateHelper.getToday("yyyy-MM-dd");
						dayStr="����";
					}
					Log.d("alarm", dayStr);
					Date dt=DateHelper.getStringDate(theDay,"yyyy-MM-dd");
					long starttime=dt.getTime()/1000;
					long endtime=starttime+24*60*60;
					List<MyClassSchedule> dayclassList = myClassScheduleDao.queryBuilder().where().le("timestart", endtime).and().ge("timesend", starttime).query();
					String hostid=PrefUtility.get(Constants.PREF_CHECK_HOSTID,"");
					if(hostid.split("_")[1].equals("��ʦ"))
					{
						List<String> banjiList=new ArrayList<String>();
						for (MyClassSchedule teacherInfo : dayclassList) {
							if(!banjiList.contains(teacherInfo.getClassGrade()))
								banjiList.add(teacherInfo.getClassGrade());
							
						}
						if(banjiList.size()>0)
						{
							contentText = "��"+dayStr+"��"+banjiList.size()+"����Ŀ�Ҫ��";
							for(String item:banjiList)
								contentText+="\r\n"+item;
						}
						else
							contentText="��"+dayStr+"û�п�Ŷ";
					}
					else
					{
						List<String> banjiList=new ArrayList<String>();
						for (MyClassSchedule teacherInfo : dayclassList) {
							Date dt1 = new Date(teacherInfo.getTimestart() * 1000);
							SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
							String sDateTime = sdf.format(dt1); 
							sDateTime+=" "+teacherInfo.getName();
							if(!banjiList.contains(sDateTime))
								banjiList.add(sDateTime);
						}
						if(hostid.split("_")[1].equals("�ҳ�"))
						{
							if(banjiList.size()>0)
								contentText = "���ĺ���"+dayStr+"��"+banjiList.size()+"�ſ�Ҫ��";
							else
								contentText = "���ĺ���"+dayStr+"û�п�";
						}
							
						else
						{
							if(banjiList.size()>0)
								contentText =dayStr+context.getString(R.string.youhave)+banjiList.size()+context.getString(R.string.schedules);
							else
								contentText = dayStr+context.getString(R.string.noschedule);
						}
						for(String item:banjiList)
							contentText+="\r\n"+item;
					}
					Log.d("alarm", contentText);
					showDialog(intent, contentTitle, contentText);
					
				}
				/****************************** �γ����� end ********************************************/
			}else{
				/************************** �޸�ѧ��������Ϣ begin ************************************************/
				if ("submitdata".equals(actionName)  || "changekaoqininfo".equals(actionName)) { //�ύ�ֻ��������ݵ�������
					
					// ���ݱ�ʶisModify=1��ѯ��Ҫ�ύ������������
					teacherInfoList = teacherInfoDao.queryBuilder().where()
							.eq("isModify", 1).query();

					/**
					 * ��ȡѧ�������޸�����
					 */
					String kaoqinJsonStr = getChangekaoqininfo(teacherInfoList);
					Log.d(TAG, "kaoqinJsonStr:" + kaoqinJsonStr);
					// base64���ܴ���
					if (!"".equals(kaoqinJsonStr) && kaoqinJsonStr != null) {
						String kaoqinBase64 = Base64.encode(kaoqinJsonStr
								.getBytes());
						SubmitChangeinfo(kaoqinBase64, "changekaoqininfo");
					}

				}
				
				/************* �޸�ѧ��������Ϣ end ************/
				
				/************************** �޸�ѧ����Ϣ begin ************************************************/
				/*
				if ("submitdata".equals(actionName)  || "changestudentinfo".equals(actionName)) {
					// ���ݱ�ʶisModify=1��ѯ��Ҫ�ύ������������
					studentList = studentDao.queryBuilder().where()
							.eq("isModify", 1).query();
					
					String studentJsonStr = getChangestudentinfo(studentList);
					Log.d(TAG, "studentJsonStr:" + studentJsonStr);
					if (!"".equals(studentJsonStr) && studentJsonStr != null) {
						String studentBase64 = Base64.encode(studentJsonStr
								.getBytes());
						SubmitChangeinfo(studentBase64, "changestudentinfo");
					}
				}
				*/
				/************* �޸�ѧ����Ϣ end ************/
				/****************************** �޸Ľ�ʦ��Ϣ begin ********************************************/
				if ("submitdata".equals(actionName)  || "changeuser".equals(actionName)) {
					// ���ݱ�ʶisModify=1��ѯ��Ҫ�ύ������������
					userInfo = userDao.queryBuilder().where().eq("isModify", 1)
							.queryForFirst();
					
					/**
					 * ��ȡ��ʦ��Ϣ�޸�����
					 */
					String teacherJsonStr = getChangeUserInfo(userInfo);
					Log.d(TAG, "teacherJsonStr:" + teacherJsonStr);
					// base64���ܴ���
					if (!"".equals(teacherJsonStr) && teacherJsonStr != null) {
						String teacherBase64 = Base64.encode(teacherJsonStr
								.getBytes());
						SubmitChangeinfo(teacherBase64, "changeuser");
					}
				}
				/************* �޸Ľ�ʦ��Ϣ end ************/
				/****************************** �޸Ŀ��ò���״̬ begin ********************************************/
				if("submitdata".equals(actionName) || "changeceyan".equals(actionName)){
					teacherInfoList = teacherInfoDao.queryBuilder().where().eq("isModify", 1).query();
					Log.d(TAG, "--------------testList-------------" + teacherInfoList.size());
					String ceyanJsonStr = getChangeceyanStatus(teacherInfoList);
					System.out.println("ceyanJsonStr:" + ceyanJsonStr);
					if(!"".equals(ceyanJsonStr) && ceyanJsonStr != null){
						String ceyanBase64 = Base64.encode(ceyanJsonStr.getBytes());
						SubmitChangeinfo(ceyanBase64, "changeceyanzhuangtai");
					}
				}
				/************* �޸Ŀ��ò���״̬ end ************/
				/****************************** �ύ������� begin ********************************************/
				if ("submitdata".equals(actionName)  || "changeSuggestion".equals(actionName)) {
					suggestions = suggestDao.queryBuilder().where().eq("isModify", 1)
							.queryForFirst();
					String suggJsonString =  getChangeSuggestInfo(suggestions);
					if (!"".equals(suggJsonString) && suggJsonString != null) {
						String SuggBase64 = Base64.encode(suggJsonString
								.getBytes());
						SubmitFeedback(SuggBase64, "changeSuggestion");
					}
				}
				/************* �ύ������� end ************/
				/****************************** ���Ͱ༶֪ͨ begin ********************************************/
				if ("submitdata".equals(actionName)  || "changeNoticeClass".equals(actionName)) {
					notices = noticeDao.queryBuilder().where().eq("isModify", 1)
							.queryForFirst();
					String noticeJsonString =  getChangeNoticeClassInfo(notices);
					if (!"".equals(noticeJsonString) && noticeJsonString != null) {
						String NoticeBase64 = Base64.encode(noticeJsonString
								.getBytes());
						SubmitNoticeClass(NoticeBase64, "changeNoticeClass");
					}
				}
				/************* ���Ͱ༶֪ͨ end ************/
				/****************************** �ϴ��μ� begin ********************************************/
				if ("submitdata".equals(actionName)  || "changeDownloadSubject".equals(actionName)) {
					downloadSubject = downloadSubjectDao.queryBuilder().where().eq("isModify", 1)
							.queryForFirst();
					if (downloadSubject!=null) {
						SubmitUploadFile("changeDownloadSubject");
					}
				}
				/************* �ϴ��μ� end ************/
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	public void showDialog(Intent intent,CharSequence contentTitle,CharSequence contentText){
		/*
		String isrunflag = intent.getStringExtra("isStartTabHostActivity");
		if ("true".equals(isrunflag) || "false".equals(isrunflag)) {
			PrefUtility.put("isrunflag", isrunflag);
		}
		String isStartTabHostActivity = PrefUtility.get("isrunflag", "true");
		if ("true".equals(isStartTabHostActivity)) {
			Intent remindintent = new Intent("remindSubject");
			remindintent.putExtra("contentText", contentText);
			context.sendBroadcast(remindintent);
		}else{
			setNotification(contentTitle, contentText,TabHostActivity.class);
		}
		*/
		setNotification(contentTitle, contentText,TabHostActivity.class);
	}
	
	@SuppressWarnings("deprecation")
	public void setNotification(CharSequence contentTitle,CharSequence contentText,Class activity){
		//��Ϣ֪ͨ��
		//����NotificationManager
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		//����֪ͨ��չ�ֵ�������Ϣ
		int icon = R.drawable.ic_logo1;
		CharSequence tickerText = context.getString(R.string.mycampusnotify);
		long when = System.currentTimeMillis();
		
		Intent notificationIntent = new Intent(context, activity);
		notificationIntent.putExtra("contentText", contentText);
		notificationIntent.putExtra("tab", "1");
		notificationIntent.putExtra("module", "����");
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
		notificationIntent, 0);
		
		Notification.Builder builder = new Notification.Builder(context);
		builder.setWhen(when);
        builder.setAutoCancel(true);
        builder.setTicker(tickerText);
        builder.setContentTitle(contentTitle);               
        builder.setContentText(contentText);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentIntent(contentIntent);
        builder.setOngoing(false);
        //builder.setSubText("This is subtext...");   //API level 16
        builder.setNumber(100);
        builder.build();
		
        Notification notification = builder.getNotification();
        mNotificationManager.cancel(2);
        mNotificationManager.notify(2, notification);
        /*
		Notification notification = new Notification(icon, tickerText, when);
		notification.defaults=Notification.DEFAULT_SOUND;  
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		//��������֪ͨ��ʱҪչ�ֵ�������Ϣ
		//Context context = getApplicationContext();
		//notification.setLatestEventInfo(context, contentTitle, contentText,contentIntent);
		//��mNotificationManager��notify����֪ͨ�û����ɱ�������Ϣ֪ͨ
		mNotificationManager.notify(2, notification);
		*/
		Log.d("alarm", notificationIntent.getExtras().toString());
	}

	/**
	 * ��������:�ӹ���Ҫ�޸ĵĿ�������
	 * 
	 * @author yanzy 2013-12-3 ����2:51:37
	 * 
	 * @param subjectIdList
	 */
	public String getChangekaoqininfo(List<TeacherInfo> teacherInfoList) {
		try {
			if (teacherInfoList != null && teacherInfoList.size() > 0) {
				Log.d(TAG, "teacherInfoList.size():" + teacherInfoList.size());
				JSONArray jsonArray = new JSONArray();

				for (TeacherInfo teacherInfo : teacherInfoList) {
					Log.d(TAG, "------------------->teacherInfo.getAbsenceJson():"+teacherInfo.getAbsenceJson());
					String absenceJson = teacherInfo.getAbsenceJson();
					JSONObject joAbsence = new JSONObject();
					if (AppUtility.isNotEmpty(absenceJson)) {
						JSONArray ja = new JSONArray(teacherInfo.getAbsenceJson());
						if (ja != null && ja.length() > 0) {
							for (int i = 0; i < ja.length(); i++) {
								JSONObject jo = ja.getJSONObject(i);
								joAbsence.put(jo.optString("ѧ��"), jo.optString("��������"));
							}
						}
					}
					
					JSONObject jo = new JSONObject();
					jo.put("�û�������", checkCode);
					jo.put("���", teacherInfo.getId() + "");
					jo.put("�ڿ�����", teacherInfo.getCourseContent() + "");
					jo.put("��ҵ����", teacherInfo.getHomework() + "");
					jo.put("�������", "");
					jo.put("���ü���", teacherInfo.getClassroomDiscipline() + "");
					jo.put("��������", teacherInfo.getClassroomHealth() + "");
					jo.put("�༶����", teacherInfo.getClassSize() + "");
					jo.put("ʵ������", teacherInfo.getRealNumber() + "");
					jo.put("ȱ������Ǽ�", "");
					jo.put("��дʱ��", "");
					jo.put("ȱ������Ǽ�JSON", joAbsence);
					jsonArray.put(jo);
				}
				Log.d(TAG, "----------------------json:"+ jsonArray.toString());
				return jsonArray.toString();

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	/**
	 * ��������:�ӹ���Ҫ�޸ĵ�ѧ������
	 * 
	 * @author yanzy 2013-12-5 ����10:17:27
	 * 
	 * @param studentList
	 * @return
	 */
	public String getChangestudentinfo(List<Student> studentList) {
		if (studentList != null && studentList.size() > 0) {
			Log.d(TAG, "studentList.size():" + studentList.size());
			JSONArray jsonArray = new JSONArray();
			try {
				for (Student student : studentList) {
					JSONObject jo = new JSONObject();
					jo.put("�û�������", checkCode);
					jo.put("���", student.getStudentID());
					jo.put("�Ա�", student.getGender());
					jo.put("ѧ���绰", student.getPhone());
					jo.put("ѧ������", student.getEmail());
					jo.put("�ҳ�����", student.getParentName());
					jo.put("�ҳ��绰", student.getParentPhone());
					jo.put("��ͥסַ", student.getHomeAddress());
					jo.put("��ע", student.getRemark());
					jsonArray.put(jo);
				}
				return jsonArray.toString();
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * ��������:�ӹ���Ҫ�޸ĵĽ�ʦ��Ϣ
	 * 
	 * @author zhuliang 2013-12-6 ����4:31:06
	 * 
	 * @param userInfo
	 * @return
	 */
	public String getChangeUserInfo(User userInfo) {
		if (userInfo != null) {
			JSONObject jo = new JSONObject();
			try {
				jo.put("�û�������", checkCode);
				jo.put("���", userInfo.getId());
				jo.put("�Ա�", userInfo.getGender());
				jo.put("�س�", userInfo.getNickname());
				jo.put("��������", userInfo.getBirthday());
				jo.put("�ֻ�", userInfo.getPhone());
				jo.put("����", userInfo.getEmail());
				return jo.toString();
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
		
	}
	/**
	 * ��������:�ӹ���Ҫ�޸ĵİ༶֪ͨ��Ϣ
	 *
	 * @author linrr  2013-12-16 ����5:57:23
	 * 
	 * @param suggestion
	 * @return
	 */
	public String getChangeNoticeClassInfo(NoticeClass notices) {
		if (notices != null) {
			JSONObject jo = new JSONObject();
			try {
				jo.put("�û�������", checkCode);
				jo.put("action",  "DataDeal");
				jo.put("CONTENT", notices.getNotice());
				jo.put("DATETIME", String.valueOf(new Date().getTime()));
				jo.put("�༶����",notices.getClassName());//�ҵ��༶������������
				return jo.toString();
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
		
	}
	/***
	 * ��������:�ӹ���Ҫ�޸ĵ����������Ϣ
	 *
	 * @author linrr  2013-12-16 ����11:14:57
	 * 
	 * @param userInfo
	 * @return
	 */
	public String getChangeSuggestInfo(Suggestions suggestion) {
		if (suggestion != null) {
			JSONObject jo = new JSONObject();
			try {
				jo.put("�û�������", checkCode);
				jo.put("action",  "DataDeal");
				jo.put("CONTENT", suggestion.getSuggest());
				jo.put("DATETIME", String.valueOf(new Date().getTime()));
				return jo.toString();
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
		
	}
	private String getChangeceyanStatus(List<TeacherInfo> teacherInfoList) {
		if (teacherInfoList != null && teacherInfoList.size() > 0) {
			JSONArray jaTest = new JSONArray();
			for (TeacherInfo teacherInfo : teacherInfoList) {
				JSONObject jo = new JSONObject();
				try {
					jo.put("�û�������", checkCode);
					jo.put("��ʦ�Ͽμ�¼���", teacherInfo.getId());
					jo.put("���ò���״̬", teacherInfo.getTestStatus());
					jaTest.put(jo);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return jaTest.toString();
		}
		return null;
	}
	/**
	 * 
	 * ��������:�ύ�༶֪ͨ
	 *
	 * @author linrr  2013-12-16 ����5:52:47
	 * 
	 * @param base64Str
	 * @param action
	 */
	public void SubmitNoticeClass(String base64Str,final String action){
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.noticeClass(params, new RequestListener(){

			@Override
			public void onComplete(String response) {
				Bundle bundle = new Bundle();
				bundle.putString("action", action);
				bundle.putString("result", response.toString());
				Log.d(TAG, "response"+response);
				Message msg = new Message();
				msg.what = 2;
				msg.obj = bundle;
				mHandler.sendMessage(msg);	
				
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(CampusException e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	/**
	 * ��������:�ϴ��ļ�
	 *
	 * @author linrr  2013-12-18 ����11:48:59
	 * 
	 * @param base64Str
	 * @param action
	 */
	public void SubmitUploadFile(final String action){
		CampusParameters params = new CampusParameters();
		params.add("�û�������", checkCode);
		params.add("�ļ�����", downloadSubject.getFileName());
		params.add("�ļ�����", downloadSubject.getFilecontent());
		CampusAPI.uploadFiles(params, new RequestListener(){

			@Override
			public void onComplete(String response) {
				Bundle bundle = new Bundle();
				bundle.putString("action", action);
				bundle.putString("result", response.toString());
				Log.d(TAG, "response"+response);
				Message msg = new Message();
				msg.what = 3;
				msg.obj = bundle;
				mHandler.sendMessage(msg);	
				
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(CampusException e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	/**
	 * ��������:��������:�ύ������
	 *
	 * @author linrr  2013-12-16 ����2:18:41
	 * 
	 * @param base64Str
	 * @param action
	 */
	public void SubmitFeedback(String base64Str,final String action){
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.feedback(params, new RequestListener(){

			@Override
			public void onComplete(String response) {
				Bundle bundle = new Bundle();
				bundle.putString("action", action);
				bundle.putString("result", response.toString());
				Log.d(TAG, "response"+response);
				Message msg = new Message();
				msg.what = 1;
				msg.obj = bundle;
				mHandler.sendMessage(msg);	
				
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(CampusException e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	/**
	 * ��������:�ύ������
	 * 
	 * @author yanzy 2013-12-4 ����10:10:42
	 * 
	 * @param base64Str
	 */
	public void SubmitChangeinfo(String base64Str, final String action) {
		CampusParameters params = new CampusParameters();
		params.add("action", action);
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.Changeinfo(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete(String response) {
				Bundle bundle = new Bundle();
				bundle.putString("action", action);
				bundle.putString("result", response.toString());
				System.out.println("response.toString()"+response.toString());
				Message msg = new Message();
				msg.what = 0;
				msg.obj = bundle;
				mHandler.sendMessage(msg);
			}
		});
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			Bundle bundle = new Bundle();
			switch (msg.what) {
			case 0:
				bundle = (Bundle) msg.obj;
				String action = bundle.getString("action");
				String result = bundle.getString("result");
				String resultStr = new String(Base64.decode(result));
				Log.d(TAG, "action:" + action);
				Log.d(TAG, "result"+result);
				Log.d(TAG, "resultStr:" + resultStr);
				
				try {
					JSONObject jo = new JSONObject(resultStr);
					System.out.println("jo.optString(�ɹ�):"+jo.optString("�ɹ�"));
					if ("1".equals(jo.optString("�ɹ�"))) {
						if ("changekaoqininfo".equals(action)) {
							//����ʶ����ΪisModify=0
							PreparedUpdate<TeacherInfo> preparedUpdateStudentSubject = (PreparedUpdate<TeacherInfo>) teacherInfoDao.updateBuilder().updateColumnValue("isModify", 0).where().eq("isModify", 1).prepare();
							teacherInfoDao.update(preparedUpdateStudentSubject);
						}
						
						if("changeceyanzhuangtai".equals(action)){
							// ����ʶ����ΪisModify=0
							PreparedUpdate<TeacherInfo> preparedUpdateStudentSubject = (PreparedUpdate<TeacherInfo>) teacherInfoDao.updateBuilder().updateColumnValue("isModify", 0).where().eq("isModify", 1).prepare();
							teacherInfoDao.update(preparedUpdateStudentSubject);
						}
						if ("changeuser".equals(action)) {
							// ����ʶ����ΪisModify=0
							PreparedUpdate<User> preparedUpdateUser = (PreparedUpdate<User>) userDao
									.updateBuilder().updateColumnValue("isModify", 0)
									.where().eq("isModify", 1).prepare();
							userDao.update(preparedUpdateUser);
						}
						
						if ("submitdata".equals(actionName)) {
							DialogUtility.showMsg(context, "����ɹ���");
						}
					}else{
						if (!"submitdata".equals(actionName)) {
							DialogUtility.showMsg(context, "����ʧ�ܣ�");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				break;
			case 1:
				bundle = (Bundle) msg.obj;
				String action1 = bundle.getString("action");
				String result1 = bundle.getString("result");
				Log.d(TAG, "action:" + action1);
				Log.d(TAG, "result2"+result1);
				String resultStr1 = new String(Base64.decode(result1));
				Log.d(TAG, "resultStr2:" + resultStr1);
				try {
					JSONObject jo = new JSONObject(resultStr1);
					System.out.println("jo.optString(��Ϣ):"+jo.optString("MSG_STATUS"));
					if("�ɹ�".equals(jo.optString("MSG_STATUS"))){
						//����ʶ����ΪisModify=0
						PreparedUpdate<Suggestions> preparedUpdateSuggestion = (PreparedUpdate<Suggestions>) suggestDao.updateBuilder().updateColumnValue("isModify", 0).where().eq("isModify", 1).prepare();
						suggestDao.update(preparedUpdateSuggestion);
						if ("submitdata".equals(actionName)) {
							DialogUtility.showMsg(context, "���ͳɹ���");
						}
					}else{
						if (!"submitdata".equals(actionName)) {
							DialogUtility.showMsg(context, "����ʧ�ܣ�");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 2:
				bundle = (Bundle) msg.obj;
				String action2 = bundle.getString("action");
				String result2 = bundle.getString("result");
				try {
					PreparedUpdate<NoticeClass> preparedUpdateNoticeClass = (PreparedUpdate<NoticeClass>) noticeDao.updateBuilder().updateColumnValue("isModify", 0).where().eq("isModify", 1).prepare();
					noticeDao.update(preparedUpdateNoticeClass);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			case 3:
				bundle = (Bundle) msg.obj;
				String action3 = bundle.getString("action");
				String result3 = bundle.getString("result");
				String resultStr3 = "";
				try {
					resultStr3 = new String(Base64.decode(result3.getBytes("GBK")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				Log.d(TAG, "action3:" + action3);
				Log.d(TAG, "resultStr3:" + resultStr3);
				isdialog = true;
				try {
					JSONObject jo = new JSONObject(resultStr3);
					Log.d(TAG, "jo.optString(STATUS):"+jo.optString("STATUS"));
					if("OK".equals(jo.optString("STATUS"))){
						//����ʶ����ΪisModify=0
						PreparedUpdate<DownloadSubject> preparedUpdateDownloadSubject = (PreparedUpdate<DownloadSubject>) downloadSubjectDao.updateBuilder().updateColumnValue("isModify", 0).where().eq("isModify", 1).prepare();
						downloadSubjectDao.update(preparedUpdateDownloadSubject);
						DialogUtility.showMsg(context, "�ϴ��ɹ���");
					}else{
						DialogUtility.showMsg(context, "�ϴ�ʧ�ܣ�");
					}
				}catch (Exception e) {
					e.printStackTrace();
				}	
				break;
			case 4:
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result.getBytes("GBK")));
						Log.d(TAG, "----resultStr:"+resultStr);
						
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					JSONObject jo = null;
					try {
						jo = new JSONObject(resultStr);
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if(jo!=null){
						
						
					}
				}
				break;
			case 5:
				resultStr = msg.obj.toString();
				
				resultStr=resultStr.substring(resultStr.indexOf("({")+1);
				resultStr=resultStr.substring(0, resultStr.length()-1);
				String cityStr="";
				String addressStr="";
				Bundle bdl=msg.getData();
				double lat=bdl.getDouble("lat");
				double lon=bdl.getDouble("lon");
				
				JSONObject jo = null;
				try {
					jo = new JSONObject(resultStr);
					
					if(jo!=null && jo.getInt("status")==0)
					{
						jo=jo.getJSONObject("result");
						cityStr=jo.getJSONObject("addressComponent").getString("city");
						addressStr=jo.getString("formatted_address");
						JSONArray pois=jo.getJSONArray("pois");
						for(int i=0;i<pois.length();i++)
						{
							JSONObject item=pois.getJSONObject(i);
							addressStr+="\n"+item.getString("name");
							if(i>2) break;
						}
						String user_type = PrefUtility.get(Constants.PREF_CHECK_USERTYPE, "");
						if(user_type.equals("ѧ��"))
							postGPS(cityStr,addressStr,lat,lon);
						User user=((CampusApplication)context.getApplicationContext()).getLoginUserObj();
						if(user!=null)
							user.setLatestAddress(addressStr);
					}
					
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
				
				break;

			}
		};
	};
	
	@SuppressWarnings("unused")
	private class cancelStudentPicListener implements DialogInterface.OnClickListener{  
		   
        @Override  
        public void onClick(DialogInterface dialog, int which) {  
        	dialog.dismiss();
        }  
    }
	
	LocationListener locationListener = new LocationListener() {
		
		// Provider��״̬�ڿ��á���ʱ�����ú��޷�������״ֱ̬���л�ʱ�����˺���
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		// Provider��enableʱ�����˺���������GPS����
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		
		// Provider��disableʱ�����˺���������GPS���ر� 
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
		//������ı�ʱ�����˺��������Provider������ͬ�����꣬���Ͳ��ᱻ���� 
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {   
				
				myLocation=location;
				locationManager.removeUpdates(locationListener);
		    	getRealAddress();
				
				Log.d("Map", "Location changed : Lat: "  
				+ location.getLatitude() + " Lng: "  
				+ location.getLongitude());   
				
			}
		}
	};
	
	private void getGPSLocation()
	{
		myLocation=null;
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
 		{
			locationManager.removeUpdates(locationListener);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0,locationListener);
			
			new Handler().postDelayed(new Runnable(){  
				public void run() {  
					locationManager.removeUpdates(locationListener);
					if(myLocation==null)
					{
						myLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if(myLocation!=null)
							getRealAddress();
					}	
					
				}  
			}, 10000);
 		}
		
	}
	
	private void getNetLocation()
	{
		myLocation=null;
		if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
 		{
			locationManager.removeUpdates(locationListener);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0,locationListener);
			
			new Handler().postDelayed(new Runnable(){  
				public void run() {  
					locationManager.removeUpdates(locationListener);
					if(myLocation==null)
					{
						myLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if(myLocation!=null)
							getRealAddress();
						else
							getGPSLocation();
					}	
					
				}  
			}, 5000);
			
 		}
	
	}
	
	private void postGPS(String city,String address,double latitude,double longitude )
	{
		JSONObject jo = new JSONObject();
		String user_code = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		try {
			String datetime = String.valueOf(new Date().getTime());
			jo.put("ACTION", "");
			jo.put("�û�������", user_code);
			jo.put("DATETIME", datetime);
			jo.put("����", city);
			jo.put("��ϸ��ַ", address);
			jo.put("γ��", latitude);
			jo.put("����", longitude);
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA,
				Base64.encode(jo.toString().getBytes()));
		CampusAPI.postGPS(params, new RequestListener() {
			@Override
			public void onComplete(String response) {
				Message msg = new Message();
				msg.what = 4;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(CampusException e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	private void getRealAddress()
	{
		if(myLocation==null) return;
		
		final double latitude=myLocation.getLatitude();
		final double longitude=myLocation.getLongitude();
		
		CampusAPI.getAddressFromBaidu(latitude,longitude, new RequestListener() {
			@Override
			public void onComplete(String response) {
				Message msg = new Message();
				msg.what = 5;
				msg.obj = response;
				Bundle bundle=new Bundle();
				bundle.putDouble("lat", latitude);
				bundle.putDouble("lon", longitude);
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(CampusException e) {
				// TODO Auto-generated method stub
				
			}
		});
		/*
		 * Geocoder geocoder=new Geocoder(TabHostActivity.this, Locale.getDefault()); 
		try {  
			
            List<Address> addresses=geocoder.getFromLocation(latitude, longitude, 5);  
            StringBuilder stringBuilder=new StringBuilder();                      
            if(addresses.size()>0){  
                Address address=addresses.get(0);  
                for(int i=0;i<address.getMaxAddressLineIndex();i++){  
                    stringBuilder.append(address.getAddressLine(i)).append("\n");   
                    if(i>3) break;
                }  
                String cityStr=address.getLocality();
                String addressStr=stringBuilder.toString();
                Log.d(TAG,"��������:"+addressStr);
                postGPS(cityStr,addressStr,latitude,longitude);
            }  
            
			
			
        } catch (IOException e) {  
        	getRealAddressFromBaidu(latitude,longitude);
            e.printStackTrace();  
        }  
        */
	}
	
	
	
}