package com.dandian.pocketmoodle.db;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.dandian.pocketmoodle.CampusApplication;
import com.dandian.pocketmoodle.activity.TabHostActivity;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.entity.AllInfo;
import com.dandian.pocketmoodle.entity.ChatFriend;
import com.dandian.pocketmoodle.entity.ChatMsg;
import com.dandian.pocketmoodle.entity.ContactsInfo;
import com.dandian.pocketmoodle.entity.Dictionary;
import com.dandian.pocketmoodle.entity.DownloadInfo;
import com.dandian.pocketmoodle.entity.MyClassSchedule;
import com.dandian.pocketmoodle.entity.Schedule;
import com.dandian.pocketmoodle.entity.StudentAttence;
import com.dandian.pocketmoodle.entity.StudentPic;
import com.dandian.pocketmoodle.entity.StudentScore;
import com.dandian.pocketmoodle.entity.StudentTest;
import com.dandian.pocketmoodle.entity.TeacherInfo;
import com.dandian.pocketmoodle.entity.TestEntity;
import com.dandian.pocketmoodle.entity.TestStartEntity;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.DialogUtility;
import com.dandian.pocketmoodle.util.FileUtility;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.dandian.pocketmoodle.util.SerializableMap;
import com.dandian.pocketmoodle.util.WifiUtility;
import com.dandian.pocketmoodle.util.ZLibUtils;
import com.dandian.pocketmoodle.util.ZipUtility;

public class InitData {
	private static final String TAG = "InitData";
	DatabaseHelper database;
	private Context context;
	private Dialog mLoadingDialog;
	private String ACTION_NAME;

	private Dao<Schedule, Integer> scheduleDao;
	
	private Dao<TeacherInfo, Integer> teacherinfoDao;
	private Dao<TestEntity, Integer> testEntityDao;
	private Dao<TestStartEntity, Integer> startTestDao;

	private Dao<StudentAttence, Integer> studentAttenceDao;
	private Dao<Dictionary, Integer> dictionaryDao;
	private Dao<StudentScore, Integer> studentScoreDao;
	private Dao<StudentTest, Integer> studentTestDao;
	private Dao<StudentPic, Integer> studentPicDao;
	private Dao<MyClassSchedule, Integer> myClassScheduleDao;

	List<StudentPic> studentPicList;
	private int studentClassCnt = 0; // ��Ҫ����ͷ��İ༶����
	private int studentLoadClassCnt = 0; // �Ѿ����ع�ͷ��İ༶����

	private Dao<ChatMsg, Integer> chatMsgDao;
	private Dao<ChatFriend, Integer> chatFriendDao;

	private Dao<DownloadInfo, Integer> downloadInfoDao;

	Date begindate;
	Date enddate;
	String checkCode;

	public InitData(Context context, DatabaseHelper database,
			Dialog mLoadingDialog, String ACTION_NAME, String checkCode) {
		this.context = context;
		this.database = database;
		this.mLoadingDialog = mLoadingDialog;
		this.ACTION_NAME = ACTION_NAME;
		this.checkCode = checkCode;
		Log.d(TAG, "---------------this.mLoadingDialog��" + this.mLoadingDialog);
	}

	/**
	 * ��������:����Dao
	 * 
	 * @author yanzy 2013-12-26 ����12:07:35
	 * 
	 */
	private void initDao() {
		try {
			
			testEntityDao = getHelper().getTestEntityDao();
			teacherinfoDao = getHelper().getTeacherInfoDao();
			myClassScheduleDao= getHelper().getMyClassScheduleDao();
			startTestDao = getHelper().getStartTestDao();
			studentAttenceDao = getHelper().getStudentAttenceDao();
			dictionaryDao = getHelper().getDictionaryDao();
			studentScoreDao = getHelper().getStudentScoreDao();
			studentTestDao = getHelper().getStudentTestDao();
			studentPicDao = getHelper().getStudentPicDao();
			scheduleDao = getHelper().getScheduleDao();
	
			chatMsgDao = getHelper().getChatMsgDao();
			chatFriendDao = getHelper().getChatFriendDao();
			downloadInfoDao = getHelper().getDownloadInfoDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��������:��ջ�������
	 * 
	 * @author yanzy 2013-12-26 ����12:07:23
	 * 
	 */
	private void deleteBaseData() {
		try {
			testEntityDao.delete((PreparedDelete<TestEntity>) testEntityDao
					.deleteBuilder().prepare());
			teacherinfoDao.delete((PreparedDelete<TeacherInfo>) teacherinfoDao
					.deleteBuilder().prepare());
			myClassScheduleDao.delete((PreparedDelete<MyClassSchedule>) myClassScheduleDao
					.deleteBuilder().prepare());
			startTestDao.delete((PreparedDelete<TestStartEntity>) startTestDao
					.deleteBuilder().prepare());
			studentAttenceDao
					.delete((PreparedDelete<StudentAttence>) studentAttenceDao
							.deleteBuilder().prepare());
			dictionaryDao.delete((PreparedDelete<Dictionary>) dictionaryDao
					.deleteBuilder().prepare());
			studentScoreDao
					.delete((PreparedDelete<StudentScore>) studentScoreDao
							.deleteBuilder().prepare());
			studentTestDao.delete((PreparedDelete<StudentTest>) studentTestDao
					.deleteBuilder().prepare());
			dictionaryDao.delete((PreparedDelete<Dictionary>) dictionaryDao
					.deleteBuilder().prepare());
			studentPicDao.delete((PreparedDelete<StudentPic>) studentPicDao
					.deleteBuilder().prepare());
			scheduleDao.delete((PreparedDelete<Schedule>) scheduleDao
					.deleteBuilder().prepare());
			//chatMsgDao.deleteBuilder().delete();
			//chatFriendDao.deleteBuilder().delete();
			downloadInfoDao.deleteBuilder().delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	private DatabaseHelper getHelper() {
		return database;
	}

	/**
	 * ��������:�ֶ����ػ�������
	 * 
	 * @author yanzy 2013-12-12 ����10:26:10
	 * 
	 */
	public void myInitAllInfo() {
		mLoadingDialog = DialogUtility.createLoadingDialog(context,
				"���ݳ�ʼ���У����Ե�...");
		initAllInfo();
	}

	/**
	 * ��������:�ֶ�����ѧ��ͷ��
	 * 
	 * @author yanzy 2013-12-12 ����10:26:20
	 * 
	 */
	public void myInitStudentPicDialog() {
		mLoadingDialog = DialogUtility.createLoadingDialog(context,
				"��ʼ��ͷ���У����Ե�...");
		// ��ȡ��ǰwifi����״̬
		WifiUtility wifi = new WifiUtility(context);
		int wifiState = wifi.checkState();
		Log.d(TAG, "-------------->wifiState:" + wifiState);
		if (wifiState == 3) { // wifi����״̬
			initStudentPic();
		} else {
			if (mLoadingDialog != null) {
				mLoadingDialog.dismiss();
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("��Ϣ��ʾ");
			builder.setMessage("����ǰ��WIFI���磬�������½�����GPRS����");
			builder.setPositiveButton("����", new initStudentPicListener());
			builder.setNegativeButton("ȡ��", new cancelStudentPicListener());
			AlertDialog ad = builder.create();
			ad.show();
		}
	}

	public void myInitContactsDialog() {
		mLoadingDialog = DialogUtility.createLoadingDialog(context,
				"��ϵ�����ݳ�ʼ���У����Ե�...");
		if (mLoadingDialog != null) {
			mLoadingDialog.show();
		}
		initContactInfo();
	}

	public void myInitStudentPic() {
		// ��ȡ��ǰwifi����״̬
		WifiUtility wifi = new WifiUtility(context);
		int wifiState = wifi.checkState();
		Log.d(TAG, "-------------->wifiState:" + wifiState);
		if (wifiState == 3) { // wifi����״̬
			initStudentPic();
		} else {
			if (mLoadingDialog != null) {
				mLoadingDialog.dismiss();
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("��Ϣ��ʾ");
			builder.setMessage("����ǰ��WIFI���磬��ʼ����ϵ��ͷ������3G����");
			builder.setPositiveButton("����", new initStudentPicListener());
			builder.setNegativeButton("ȡ��", new cancelStudentPicListener());
			AlertDialog ad = builder.create();
			ad.show();
		}
	}

	// ������
	private class initStudentPicListener implements
			DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			initStudentPic();
		}

	}

	// ������
	private class cancelStudentPicListener implements
			DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}

	}

	/**
	 * ��������:��ʼ����������
	 * 
	 * @author yanzy 2013-12-11 ����10:57:27
	 * 
	 */
	public void initAllInfo() {
		Log.d(TAG, "��ʼ��ʼ����������");
		initDao(); // ����Dao
		deleteBaseData(); // ��ձ�
		if (mLoadingDialog != null) {
			mLoadingDialog.show();
		}
		Log.d(TAG, "--------------->��ʼ��ʼ�����ݵ��������ݿ⣺" + new Date());
		Log.d(TAG, "--------------->�û�У����" + checkCode);
		String dataResult = "";
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("�û�������", checkCode);
			dataResult = Base64.encode(jsonObj.toString().getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "--->  [���ܲ���] =>" + dataResult);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, dataResult);
		final Date dt=new Date();
		CampusAPI.initInfo(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				Log.d(TAG, "--->  " + e);
			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "--->  " + e);
				Message msg = new Message();
				msg.what = 3;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----------��ʼ����ʱ:" + (new Date().getTime()-dt.getTime()));
				Message msg = new Message();
				msg.what = 0;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * ��������:��ȡ��ʦ�Ͽμ�¼
	 * 
	 * @author shengguo 2014-5-16 ����5:07:07
	 * 
	 */
	public void getTeacherInfos() {
		mLoadingDialog = DialogUtility.createLoadingDialog(context,
				"���ڻ�ȡ���ݣ����Ե�...");
		mLoadingDialog.show();
		try {
			teacherinfoDao = getHelper().getTeacherInfoDao();
			teacherinfoDao.delete((PreparedDelete<TeacherInfo>) teacherinfoDao
					.deleteBuilder().prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String dataResult = null;
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("�û�������", checkCode);
			dataResult = Base64.encode(jsonObj.toString().getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "--->  [���ܲ���] =>" + dataResult);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, dataResult);
		CampusAPI.initInfo(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = 3;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "--->  " + response);
				Message msg = new Message();
				msg.what = 4;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * ��������:��ʼ����ϵ����Ϣ
	 * 
	 * @author yanzy 2013-12-11 ����10:57:40
	 * 
	 */
	public void initContactInfo() {
		Log.d(TAG, "��ʼ��ʼ����ϵ����Ϣ");
		if(mLoadingDialog!=null)
			mLoadingDialog.show();
		initDao();
		
		CampusParameters params = new CampusParameters();

		try {
			JSONObject jo = new JSONObject();
			jo.put("�û�������", checkCode);
			String datetime = String.valueOf(new Date().getTime());
			jo.put("DATETIME", datetime);
			params.add(Constants.PARAMS_DATA,
					Base64.encode(jo.toString().getBytes()));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		final Date dt=new Date();
		CampusAPI.getTeacherInfo(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = 3;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----------��ϵ�˺�ʱ:" + (new Date().getTime()-dt.getTime()));
				Message msg = new Message();
				msg.what = 1;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * ��������:��ȡ���һ�������¼
	 * 
	 * @QiaoLin  2014-6-18 ����10:35:40
	 * 
	 */
	public void initContactLastMsg() {
		Log.d(TAG, "��ʼ��ʼ����ϵ�����һ�������¼");
		
		initDao();
		try {
			chatFriendDao.deleteBuilder().delete();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		CampusParameters params = new CampusParameters();

		try {
			JSONObject jo = new JSONObject();
			jo.put("�û�������", checkCode);
			String datetime = String.valueOf(new Date().getTime());
			jo.put("DATETIME", datetime);
			params.add(Constants.PARAMS_DATA,
					Base64.encode(jo.toString().getBytes()));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.getLast_ATOALL(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = 3;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "--->  " + response);
				Message msg = new Message();
				msg.what = 5;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}
	
	/**
	 * ��������:��ʼ����ϵ��ͷ��
	 * 
	 * @author yanzy 2013-12-11 ����5:16:53
	 * 
	 */
	public void initStudentPic() {
		Log.d(TAG, "��ʼ��ʼ����ϵ��ͷ��");
		initDao(); // ����Dao
		try {
			if (mLoadingDialog != null) {
				mLoadingDialog.show();
			}
			studentPicDao = getHelper().getStudentPicDao();
			studentPicList = studentPicDao.queryForAll();

			if (studentPicList != null && studentPicList.size() > 0) {
				studentClassCnt = studentPicList.size();
				for (StudentPic studentPic : studentPicList) {
					downLoadStudentPic(studentPic);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void postBaiduUserId() {
		
		TabHostActivity.ifpostuserid=true;
		String dataResult = "";
		String baidu_userid = PrefUtility.get(Constants.PREF_BAIDU_USERID, "");
		
		Log.d(TAG, "-------------------->baidu_userid:" + baidu_userid);
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("�û�������", checkCode);
			jsonObj.put("�ٶ�������ID", baidu_userid);
			String datetime = String.valueOf(new Date().getTime());
			jsonObj.put("DATETIME", datetime);
			// ���ϵͳ��Ϣ
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String SerialNumber ="";
			if(tm.getDeviceId()==null)
			{
				SerialNumber = android.os.Build.SERIAL+"-"+Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID); 
			}
			else
				SerialNumber =tm.getDeviceId()+"-"+tm.getSimSerialNumber();
			jsonObj.put("�豸Ψһ��", SerialNumber);
			jsonObj.put("�豸��", android.os.Build.BRAND+" "+android.os.Build.PRODUCT);
			jsonObj.put("�豸����", "Android");
			jsonObj.put("����ģʽ", android.os.Build.VERSION.SDK);
			jsonObj.put("ϵͳ��", android.os.Build.USER);
			jsonObj.put("ϵͳ�汾", android.os.Build.VERSION.RELEASE);
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
	        jsonObj.put("�ֱ���", dm.widthPixels+" * "+dm.heightPixels);
			
			dataResult = Base64.encode(jsonObj.toString().getBytes());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, dataResult);
		final Date dt=new Date();
		CampusAPI.postBaiDuId(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = 3;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----------�ϴ��豸��Ϣ�ɹ�:" + (new Date().getTime()-dt.getTime()));
				Message msg = new Message();
				msg.what = 6;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}
	/**
	 * ��������:����ѧ��ͷ��
	 * 
	 * @author yanzy 2013-12-9 ����10:12:19
	 * 
	 * @param studentPic
	 */
	private void downLoadStudentPic(final StudentPic studentPic) {
		CampusParameters params = new CampusParameters();
		String picUrl = studentPic.getPicUrl();
		params.add("picUrl", picUrl);
		CampusAPI.downLoadStudentPic(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = 3;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Bundle bundle = new Bundle();
				bundle.putString("className", studentPic.getClassName());
				bundle.putString("result", response.toString());
				Message msg = new Message();
				msg.what = 2;
				msg.obj = bundle;
				mHandler.sendMessage(msg);

			}
		});
	}

	// "���ò���""�μ�����":"ѧ��������Ϣ��""����0901��_�����б�"
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (mLoadingDialog!=null) {
					mLoadingDialog.dismiss();
				}
				Thread thread=new saveInitData(msg.obj.toString());
				thread.start();
			        
				break;
			case 1:
				if (mLoadingDialog!=null) {
					mLoadingDialog.dismiss();
				}
				
				thread=new saveContractsData(msg.obj.toString());
				thread.start();
				
				break;
			case 2:
				Log.d(TAG, "-----------����ѧ��ͷ��------------");
				Bundle bundle = (Bundle) msg.obj;
				String className = bundle.getString("className");
				String resultPic = bundle.getString("result");
				if (!"".equals(resultPic) && resultPic != null) {
					byte[] base64bytePic = null;
					try {
						base64bytePic = Base64
								.decode(resultPic.getBytes("GBK"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} // ����base64�ַ����õ�ͷ��zip����������
					String path = "PocketCampus/";
					String fileName = className + ".zip";
					if (base64bytePic != null) {
						// ��zip�����ص�����
						File file = FileUtility.writeSDFromByte(path, fileName,
								base64bytePic);
						if (file != null) {
							path = file.getPath();
							fileName = path.substring(0, path.indexOf("."))
									+ "/"; // ��ѹĿ��·��
							boolean isUnZip = ZipUtility.unZipFile(file,
									fileName); // ��ѹ
							if (isUnZip) { // ��ѹ�ɹ�����ɾ��zip��
								FileUtility.deleteFile(path);
							}
						}
					}

				}
				studentLoadClassCnt++;
				if (studentClassCnt == studentLoadClassCnt
						&& studentClassCnt > 0) {
					if (mLoadingDialog != null) {
						mLoadingDialog.dismiss();
					}

					if (!"".equals(ACTION_NAME) && ACTION_NAME != null) {
						Intent intent = new Intent(ACTION_NAME);
						intent.putExtra("initResult", "studentPic");
						context.sendBroadcast(intent);
					}
				}
				break;
			case 3:
				if (mLoadingDialog != null) {
					mLoadingDialog.dismiss();
				}
				AppUtility.showErrorToast(context, msg.obj.toString());
				if (!"".equals(ACTION_NAME) && ACTION_NAME != null) {
					Intent intent = new Intent(ACTION_NAME);
					context.sendBroadcast(intent);
				}
				break;
			case 4:// ��ʦ�Ͽμ�¼�б�
				Log.d(TAG, "-------------��ȡ��ʦ�Ͽμ�¼�б�---------");
				String resultStr = msg.obj.toString(); // ���������ص�base64���ܺ���ַ���
				Log.d(TAG, "--------------resultStr:" + resultStr);
				byte[] base64byte = null;
				try {
					base64byte = Base64.decode(resultStr.getBytes("GBK"));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} // ����base64�ַ������õ�ѹ���ֽ�������̨Ϊphp������gzcompress����ѹ����Э��Ϊzlib
				String unZlibStr1 = ZLibUtils.decompress(base64byte); // ����zlibЭ���ѹ��
				Log.d(TAG, "--------------unZlibStr:" + unZlibStr1);
				try {
					JSONObject jothacher = new JSONObject(unZlibStr1);
					String result = jothacher.optString("���");
					if (AppUtility.isNotEmpty(result)) {
						AppUtility.showToastMsg(context, result);
						if (mLoadingDialog != null) {
							mLoadingDialog.dismiss();
						}
						break;
					}
					AllInfo allInfo = new AllInfo(jothacher);

					List<TeacherInfo> teacherInfosList = allInfo
							.getTeacherInfos();
					if (teacherInfosList != null && teacherInfosList.size() > 0) {
						for (TeacherInfo tt : teacherInfosList) {
							if (tt != null) {
								teacherinfoDao.create(tt);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (!"".equals(ACTION_NAME) && ACTION_NAME != null) {
					Intent intent = new Intent(ACTION_NAME);
					context.sendBroadcast(intent);
				}
				break;
			case 5:// ���һ�������¼
				Log.d(TAG, "-------------��ȡ���һ�������¼---------");
				resultStr = msg.obj.toString(); // ���������ص�base64���ܺ���ַ���
				Log.d(TAG, "--------------resultStr:" + resultStr);
				base64byte = null;
				
				Map<String,String> lastMsgMap = new HashMap<String,String>();
				try {
					resultStr = new String(
							Base64.decode(resultStr.getBytes("GBK")));
					
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} 
				try {
					JSONObject jowasr = new JSONObject(resultStr);
					Iterator<?> keys =jowasr.keys();
					String name;
					JSONObject values;
					while (keys.hasNext()) {
						name = String.valueOf(keys.next());
						values = jowasr.getJSONObject(name);
						values=values.optJSONObject("���һ�������¼");
						String content=values.getString("CONTENT");
						if(!values.getString("TYPE").equals("txt"))
						{
							content="[ͼƬ]";
						}
						lastMsgMap.put(name,content);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				} 
				
				if (!"".equals(ACTION_NAME) && ACTION_NAME != null) {
					Intent intent = new Intent(ACTION_NAME);
					Bundle bdl=new Bundle();
					SerializableMap myMap=new SerializableMap();
					myMap.setMap(lastMsgMap);
					bdl.putSerializable("result", myMap);					
					intent.putExtras(bdl);
					context.sendBroadcast(intent);
				}
				break;
			case 6:// �ϴ��豸��Ϣ
				
				resultStr = msg.obj.toString(); // ���������ص�base64���ܺ���ַ���
				
				base64byte = null;
				
				try {
					resultStr = new String(
							Base64.decode(resultStr.getBytes("GBK")));
					
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} 
				try {
					JSONObject jowasr = new JSONObject(resultStr);
					if(!jowasr.optString("���").equals("�ɹ�"))
					{
						AppUtility.showErrorToast(context,"�����޷����ռ�ʱ��Ϣ");
					}
					JSONArray richeng=jowasr.optJSONArray("δ���ճ�"); 
					if(richeng!=null)
					{
						try {
							if(myClassScheduleDao==null)
								myClassScheduleDao= getHelper().getMyClassScheduleDao();
							myClassScheduleDao.delete((PreparedDelete<MyClassSchedule>) myClassScheduleDao
									.deleteBuilder().prepare());
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						for(int i=0;i<richeng.length();i++)
						{
							JSONObject itemJson=richeng.getJSONObject(i);
							MyClassSchedule mcs=new MyClassSchedule(itemJson);
							if(mcs.getName()!=null && mcs.getName().length()>0)
								try {
									myClassScheduleDao.create(mcs);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						}
						AppUtility.beginReminder(context);
						
					}
					
				}
				catch (JSONException e) {
					e.printStackTrace();
				} 
				break;
				
			default:
				break;
			}
			;
		}
	};

	/**
	 * ��������:
	 * 
	 * @author zhuliang 2014-1-15 ����11:41:22
	 * 
	 * @param type
	 * @param toid
	 * @param toname
	 * @param msgFlag
	 * @param content
	 * @param chatFriend
	 * @param msg_type
	 *            ��Ӵ��ֶΣ���Ϣ����
	 * @return
	 */
	public ChatMsg sendChatToDatabase(String type, String toid, String toname,
			int msgFlag, String content, ChatFriend chatFriend, String msg_type,String userImage,String msg_id) {
		try {
			chatMsgDao = getHelper().getChatMsgDao();
			chatFriendDao = getHelper().getChatFriendDao();
			String hostid=PrefUtility.get(Constants.PREF_CHECK_HOSTID,"");

			// �ж��û��Ƿ��������б���
			if (chatFriend != null) { // �������б��У���������������ݣ��������ʱ��
				chatFriend.setLastTime(new Date());
				chatFriend.setLastContent(content);
				chatFriend.setType(type);
				chatFriend.setMsgType(msg_type);
				chatFriend.setUserImage(userImage);
				chatFriendDao.update(chatFriend);
			} else { // ���������б��У�����ӵ������б���
				
				chatFriend = new ChatFriend();
				chatFriend.setHostid(hostid);
				chatFriend.setToid(toid);
				chatFriend.setLastTime(new Date());
				chatFriend.setLastContent(content);
				if(msgFlag==0)
					chatFriend.setUnreadCnt(1);
				chatFriend.setType(type);
				chatFriend.setUserImage(userImage);
				chatFriend.setMsgType(msg_type);
				chatFriend.setToname(toname);
				chatFriendDao.create(chatFriend);
			}
	
			// ���챣�����ݵ���������
			ChatMsg entity = null;
			if ("txt".equals(type) || msgFlag == 0) {
				entity = new ChatMsg();
				entity.setType(type);
				entity.setHostid(hostid);
				entity.setToid(toid);
				entity.setToname(toname);
				entity.setTime(new Date());
				entity.setMsgFlag(msgFlag);
				entity.setContent(content);
				entity.setMsg_id(msg_id);
				chatMsgDao.create(entity);
			}
			return entity;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	private class saveInitData extends Thread
	{
		private String str;
		public saveInitData(String str)
		{
			this.str = str;
		}
		public void run()
		{
			Log.d(TAG, "-----------��ʼ����������------------");
			Date dt=new Date();
			byte[] base64byte = null;
			try {
				base64byte = Base64.decode(str.getBytes("GBK"));
				PrefUtility.put(Constants.PREF_INIT_DATA_STR, str);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				
			} // ����base64�ַ������õ�ѹ���ֽ�������̨Ϊphp������gzcompress����ѹ����Э��Ϊzlib
			String unZlibStr = ZLibUtils.decompress(base64byte); // ����zlibЭ���ѹ��
			Log.d(TAG, "--------------unZlibStr:" + unZlibStr);
    		
			try {
				/*
				JSONObject jo = null;
				jo = new JSONObject(unZlibStr);
				String result = jo.optString("���");
				*/
				net.minidev.json.JSONObject obj=(net.minidev.json.JSONObject) JSONValue.parseStrict(unZlibStr);
				if (AppUtility.isNotEmpty(String.valueOf(obj.get("���")))) {
					
				}
				else
				{
        		
	            	AllInfo allInfo = new AllInfo(obj);
	            	int currentWeek = allInfo.getCurrentWeek();// ��ǰ�ܴΣ�
					int selectedWeek = allInfo.getSelectedWeek();// ѡ���ܴ�
					int maxWeek = allInfo.getMaxWeek();// "����ܴ�
					Log.d(TAG, "currentWeek:" + currentWeek + ",selectedWeek:"
							+ selectedWeek + ",maxWeek:" + maxWeek);
					
					Schedule schedule = allInfo.getSchedule();
					// Log.d(TAG, "----------------------->" +
					// schedule.getSections() + schedule.getWeeks());
					/*
					AttendanceOfStudent attendanceOfStudent = new AttendanceOfStudent(
							jo);
					QueryTheMarkOfStudent markOfStudent = new QueryTheMarkOfStudent(
							jo);
					StatisticsScoreOfStudents statisticsScoreOfStudents = new StatisticsScoreOfStudents(
							jo);
					statisticsScoreOfStudentsDao
							.create(statisticsScoreOfStudents);
					*/
					// ���ò�������б�����
					Log.d(TAG,
							"-----------------���ò�������б�����-----------------------");
					List<TestEntity> testinglist = allInfo.getTestEntitys();
					if (testinglist != null && testinglist.size() > 0) {
						Log.d(TAG, "-----------------testinglist.size():"
								+ testinglist.size());
						for (TestEntity testEntity : testinglist) {
							testEntityDao.create(testEntity);
						}
					}
					// ��ʦ�Ͽμ�¼�б�
					Log.d(TAG,
							"-----------------��ʦ�Ͽμ�¼�б�-----------------------");
					List<TeacherInfo> teacherInfosList = allInfo
							.getTeacherInfos();
					if (teacherInfosList != null && teacherInfosList.size() > 0) {
						Log.d(TAG, "-----------------teacherInfosList.size():"
								+ teacherInfosList.size());
						for (TeacherInfo tt : teacherInfosList) {
							teacherinfoDao.create(tt);
						}
					} 
					
					// ѧ���б�
					((CampusApplication)context.getApplicationContext()).setStudentDic(allInfo.getStudentList());
					//PrefUtility.putObject("studentDic", allInfo.getStudentList());
					
					/**
					 * ��ʼ�����ʱ by zhuliang
					 */
					Log.d(TAG, "----------��ʼ�����ʱ-------- ");
					List<TestStartEntity> startTestList = allInfo
							.getStartTestList();
					if (startTestList != null && startTestList.size() > 0) {
						Log.d(TAG,
								"-------------------------startTestList.size():"
										+ startTestList.size());
						for (TestStartEntity testStartEntity : startTestList) {
							startTestDao.create(testStartEntity);
						}
					}
	
					
	
					// ������ɫ����
					Log.d(TAG, "---------������ɫ����--------");
					List<Dictionary> studentAttenceColorList = allInfo
							.getStudentAttenceColorList();
					if (studentAttenceColorList != null
							&& studentAttenceColorList.size() > 0) {
						Log.d(TAG,
								"-------------------studentAttenceColorList.size():"
										+ studentAttenceColorList.size());
						for (Dictionary dictionary : studentAttenceColorList) {
							dictionaryDao.create(dictionary);
						}
					}
					/**
					 * ѧ���ɼ���ѯ
					 */
					Log.d(TAG, "--------ѧ���ɼ���ѯ--------");
					List<StudentScore> studentScoreList = allInfo
							.getStudentScoreList();
					if (studentScoreList != null && studentScoreList.size() > 0) {
						Log.d(TAG,
								"-------------------studentScoreList.size():"
										+ studentScoreList.size());
						for (StudentScore studentScore : studentScoreList) {
							studentScoreDao.create(studentScore);
						}
					}
					/**
					 * ѧ�������ѯ
					 */
					Log.d(TAG, "----------ѧ�������ѯ--------");
					List<StudentTest> studentTestList = allInfo
							.getStudentTestList();
					if (studentTestList != null && studentTestList.size() > 0) {
						Log.d(TAG, "-------------------studentTestList.size():"
								+ studentTestList.size());
						for (StudentTest studentTest : studentTestList) {
							studentTestDao.create(studentTest);
						}
					}
					// ����ͳ����ɫ
					Log.d(TAG, "-----------����ͳ����ɫ--------");
					List<Dictionary> studentTestColorList = allInfo
							.getStudentTestColorList();
					if (studentTestColorList != null
							&& studentTestColorList.size() > 0) {
						Log.d(TAG,
								"-------------------studentTestColorList.size():"
										+ studentTestColorList.size());
						for (Dictionary dictionary : studentTestColorList) {
							dictionaryDao.create(dictionary);
						}
					}
	
					// ѧ��������ʾ
					Log.d(TAG, "-------------------------ѧ��������Ϣ��");
					List<Dictionary> studentInfoList = allInfo
							.getStudentInfoList();
					if (studentInfoList != null && studentInfoList.size() > 0) {
						Log.d(TAG, "-------------------studentInfoList.size():"
								+ studentInfoList.size());
						for (Dictionary dictionary : studentInfoList) {
							dictionaryDao.create(dictionary);
						}
					}
	
					// ѧ��������Ϣ��
					Log.d(TAG, "-------------------------ѧ��������Ϣ��");
					Dictionary studentTab = allInfo.getStudentTab();
					if (studentTab != null) {
						dictionaryDao.create(studentTab);
					}
	
					// ѧ��ͷ��
	
					// List<StudentPic> studentPicList = allInfo
					// .getStudentPicList();
					// if (studentPicList != null && studentPicList.size() > 0)
					// {
					// for (StudentPic studentPic : studentPicList) {
					// studentPicDao.create(studentPic);
					// }
					// }
					/*
					queryTheMarkOfStudentDao = getHelper().getQueryTheMarkOfStudentDao();
					queryTheMarkOfStudentDao.create(markOfStudent);
	
					attendanceOfStudentDao = getHelper().geAttendanceOfStudentDao();
					attendanceOfStudentDao.create(attendanceOfStudent);
					*/
					// /**
					// * �������ر� #����ǰ��Ҫɾ�� by zhuliang
					// */
					//
					// List<DownloadSubject> downloadSubjects = allInfo
					// .getDownloadSubjects();
					// if (downloadSubjects != null && downloadSubjects.size() >
					// 0) {
					//
					// for (DownloadSubject subject : downloadSubjects) {
					// downloadSubjectDao.create(subject);
					// }
					// }
	
					scheduleDao.create(schedule);
					
					
					// δ�����ܿγ�
					Log.d(TAG,
							"-----------------δ�����ܿγ�-----------------------");
					List<MyClassSchedule> classScheduleList = allInfo.getFutureClassSchedule();
					if (classScheduleList != null && classScheduleList.size() > 0) {
						Log.d(TAG, "-----------------classScheduleList.size():"
								+ classScheduleList.size());
						for (MyClassSchedule tt : classScheduleList) {
							myClassScheduleDao.create(tt);
						}
					}
					
					PrefUtility.put(Constants.PREF_INIT_BASEDATE_FLAG,true);
				}
				
        	}
			
        	catch (SQLException e) {
				e.printStackTrace();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// �������ݳ�ʼ����Ϲ㲥֪ͨ
			Log.d(TAG, "----------------------->�������ݳ�ʼ����Ϲ㲥֪ͨ");
			if (!"".equals(ACTION_NAME) && ACTION_NAME != null) {
				Log.d(TAG, "----------------------->ACTION_NAME:"+ ACTION_NAME);
				Intent intent = new Intent(ACTION_NAME);
				context.sendBroadcast(intent);
			}
			//AppUtility.beginReminder(context);
			Log.d(TAG, "----------��ʼ�������ʱ:" + (new Date().getTime()-dt.getTime()));
		}
	}
	private class saveContractsData extends Thread
	{
		private String str;
		public saveContractsData(String str)
		{
			this.str = str;
		}
		public void run()
		{
			Date dt=new Date();
			try
			{
				Log.d(TAG, "------------------��ʼ����ϵ��----------------------");
				
				byte[] contact64byte = null;
				String resultContact = "";
				try {
					if (AppUtility.isNotEmpty(str)) {
						contact64byte = Base64.decode(str.getBytes("GBK"));
						PrefUtility.put(Constants.PREF_INIT_CONTACT_STR, str);
						Log.d("TAG","---->  "+ contact64byte.toString());
					}
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
					
				}
				// ����base64�ַ������õ�ѹ���ֽ�������̨Ϊphp������gzcompress����ѹ����Э��Ϊzlib
				resultContact = ZLibUtils.decompress(contact64byte);// ����zlibЭ���ѹ��
				
				
				if (!AppUtility.isNotEmpty(resultContact)) 
					AppUtility.showToastMsg(context, resultContact);
				else
				{
					//JSONObject jObject = null;
					//jObject = new JSONObject(resultContact);
					//ContactsInfo contacts = new ContactsInfo(jObject);
					
					Object obj=JSONValue.parseStrict(resultContact);
					ContactsInfo contacts = new ContactsInfo(obj);
					
					if (contacts != null) {
						
						((CampusApplication)context.getApplicationContext()).setLinkManDic(contacts.getLinkManDic());
						((CampusApplication)context.getApplicationContext()).setLinkGroupList(contacts.getContactsFriendsList());
						//PrefUtility.putObject("linkManDic", contacts.getLinkManDic());
						//PrefUtility.putObject("linkGroupList", contacts.getContactsFriendsList());
					}
					PrefUtility.put(Constants.PREF_INIT_CONTACT_FLAG, true);
				}
			}  
			
			catch (Exception e) {
				e.printStackTrace();
				
			}
		
			// �������ݳ�ʼ����Ϲ㲥֪ͨ
			Log.d(TAG, "----------------------->�������ݳ�ʼ����Ϲ㲥֪ͨ");
			if (!"".equals(ACTION_NAME) && ACTION_NAME != null) {
				Intent intent = new Intent(ACTION_NAME);
				context.sendBroadcast(intent);
			}
			Log.d(TAG, "----------��ϵ�˴����ʱ:" + (new Date().getTime()-dt.getTime()));
		}
	}
}
