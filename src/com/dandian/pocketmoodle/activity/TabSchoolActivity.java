package com.dandian.pocketmoodle.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.adapter.SchoolWorkAdapter;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.db.DatabaseHelper;
import com.dandian.pocketmoodle.entity.Notice;
import com.dandian.pocketmoodle.entity.NoticesItem;
import com.dandian.pocketmoodle.entity.SchoolWorkItem;
import com.dandian.pocketmoodle.entity.User;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.dandian.pocketmoodle.CampusApplication;

public class TabSchoolActivity extends Activity {

	
	private User user;
	
	private String TAG = "TabSchoolActivtiy";
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private LinearLayout emptyLayout;
	private LinearLayout leftlayout;
	private Button btnLeft;
	private GridView myGridView;
	private SchoolWorkAdapter adapter;
	private List<SchoolWorkItem> schoolWorkItems = new ArrayList<SchoolWorkItem>();
	private List<Notice> notices = new ArrayList<Notice>();
	private Dao<Notice, Integer> noticeInfoDao;
	private DatabaseHelper database;
	//private boolean needCount;
	static LinearLayout layout_menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerBoradcastReceiver();
		
		
		setContentView(R.layout.tab_activity_school);
		TextView title = (TextView) findViewById(R.id.tv_title);
		title.setVisibility(View.VISIBLE);
		title.setText(getString(R.string.school));
		myGridView = (GridView) findViewById(R.id.mygridview);
		btnLeft = (Button) findViewById(R.id.btn_back);
		//btnRight = (Button) findViewById(R.id.btn_right);
		leftlayout=(LinearLayout) findViewById(R.id.layout_back);
		loadingLayout = (LinearLayout) findViewById(R.id.data_load);
		contentLayout = (LinearLayout) findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) findViewById(R.id.empty_error);
		emptyLayout = (LinearLayout) findViewById(R.id.empty);
		myGridView.setEmptyView(emptyLayout);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setBackgroundResource(R.drawable.personalinfo);
		//btnRight.setBackgroundResource(R.drawable.loginout);
		//btnRight.setVisibility(View.VISIBLE);
		adapter=new SchoolWorkAdapter(this, schoolWorkItems);
		myGridView.setAdapter(adapter);
		getSchool();
		//���¼���
		failedLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getSchool();
			}
		});
		emptyLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getSchool();
			}
		});
		leftlayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TabSchoolActivity.this,
						ShowPersonInfo.class);
				intent.putExtra("studentId", user.getUserNumber());
				intent.putExtra("userImage", user.getUserImage());
				startActivity(intent);
			}
		});
		
		

		try {
			noticeInfoDao = getHelper().getNoticeInfoDao();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();
		
		
	}
	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("refreshUnread");
	
		// ע��㲥
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("refreshUnread")) {
				
				String refreshTitle = intent.getStringExtra("title");
				getUnreadByTitle(refreshTitle);
				
			}
		}
	};
	private boolean getUnreadByTitle(String title)
	{
		List<Notice> unreadList=null;
		boolean flag=false;
		try {
			unreadList = noticeInfoDao.queryBuilder().where().eq("newsType",title).and().eq("userNumber", user.getUserNumber()).and().eq("ifread","0").query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(unreadList!=null)
		{
			for(SchoolWorkItem item:schoolWorkItems)
			{
				if(item.getWorkText().equals(title))
				{
					item.setUnread(unreadList.size());
					flag=true;
					break;
				}
			}
		}
		if(flag)
			adapter.notifyDataSetChanged();
		return flag;
	}
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		}
		return database;
	}
	//��ȡУ��itemѡ������
	public void getSchool() {
		showProgress(true);
		Log.d(TAG, "--------"+String.valueOf(new Date().getTime()));
		long datatime =System.currentTimeMillis();
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "----------datatime:"+datatime);
		Log.d(TAG, "----------checkCode:"+checkCode+"++");
		JSONObject jo = new JSONObject();
		Locale locale = getResources().getConfiguration().locale;
	    String language = locale.getCountry();
	    String thisVersion = CampusApplication.getVersion();
		try {
			jo.put("�û�������", checkCode);
			jo.put("DATETIME", datatime);
			jo.put("language", language);
			jo.put("��ǰ�汾", thisVersion);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "---------------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getSchool(params, new RequestListener() {
			
			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response"+e.getMessage());
				Message msg=new Message();
				msg.what=-1;
				msg.obj= e.getMessage();
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----response"+response);
				
				Message msg=new Message();
				msg.what=0;
				msg.obj= response;
				mHandler.sendMessage(msg);
			}
		});
	}
	private void showProgress(boolean progress) {
		if (progress) {
			loadingLayout.setVisibility(View.VISIBLE);
			contentLayout.setVisibility(View.GONE);
			failedLayout.setVisibility(View.GONE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			contentLayout.setVisibility(View.VISIBLE);
			failedLayout.setVisibility(View.GONE);
		}
	}

	
	private void showFetchFailedView() {
		loadingLayout.setVisibility(View.GONE);
		contentLayout.setVisibility(View.GONE);
		failedLayout.setVisibility(View.VISIBLE);
	}
	
	public void getUnreadCount() 
	{
		

		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
	
		JSONObject jo = new JSONObject();
		try {
			jo.put("�û�������", checkCode);
			jo.put("DATETIME", datatime);
	
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getSchoolItem(params, "count.php", new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response" + e.getMessage());
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----response" + response);
				Message msg = new Message();
				msg.what = 3;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}
	public void getNoticesItem(String interfaceName,String showName) {
		
		Log.d(TAG, "--------" + String.valueOf(new Date().getTime()));
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "----------datatime:" + datatime);
		Log.d(TAG, "----------checkCode:" + checkCode + "++");
		int lastId=0;
		try {
			Notice nt=noticeInfoDao.queryBuilder().orderBy("id", false).where().eq("newsType", showName).and().eq("userNumber", user.getUserNumber()).queryForFirst();
			if(nt!=null)
				lastId=nt.getId();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Locale locale = getResources().getConfiguration().locale;
	    String language = locale.getCountry();
		JSONObject jo = new JSONObject();
		try {
			jo.put("�û�������", checkCode);
			jo.put("DATETIME", datatime);
			jo.put("LASTID", lastId);
			jo.put("language", language);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getSchoolItem(params, interfaceName, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response" + e.getMessage());
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----response" + response);
				Message msg = new Message();
				msg.what = 2;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				showFetchFailedView();
				AppUtility.showErrorToast(TabSchoolActivity.this, msg.obj.toString());
				break;

			case 0:
				showProgress(false);
				String result = msg.obj.toString();
				String resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result.getBytes("GBK")));
						Log.d(TAG, "----resultStr:"+resultStr);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					schoolWorkItems.clear();
					try {
						JSONArray jo = new JSONArray(resultStr);
						for (int i = 0; i < jo.length(); i++) {
							SchoolWorkItem swItem=new SchoolWorkItem(jo.getJSONObject(i));
							schoolWorkItems.add(swItem);
						}
						adapter.setSchoolWorkItems(schoolWorkItems);
						adapter.notifyDataSetChanged();
						
						/*
						for(int i=0;i<schoolWorkItems.size();i++)
						{
							SchoolWorkItem item=(SchoolWorkItem)schoolWorkItems.get(i);
							if(item.getTemplateName().equals("�����"))
							{
								needCount=true;
								break;
							}
						}
						
						if(needCount)
							getUnreadCount();
						*/
						for(SchoolWorkItem item:schoolWorkItems)
						{
							if(item.getTemplateName().equals("֪ͨ"))
							{
								getNoticesItem(item.getInterfaceName(),item.getWorkText());
							}
						}
						
						
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}
				break;
			case 2:
				
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}
	
				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("���");
						if(AppUtility.isNotEmpty(res)){
							AppUtility.showToastMsg(TabSchoolActivity.this, res);
						}else{
							NoticesItem noticesItem = new NoticesItem(jo);
							Log.d(TAG, "--------noticesItem.getNotices().size():"
									+ noticesItem.getNotices().size());
							notices = noticesItem.getNotices();
							
							for(Notice item:notices)
							{
								item.setIfread("0");
								item.setNewsType(noticesItem.getTitle());
								item.setUserNumber(user.getUserNumber());
								Notice nt=noticeInfoDao.queryBuilder().where().eq("id",item.getId()).and().eq("newsType", item.getNewsType()).and().eq("userNumber",user.getUserNumber()).queryForFirst();
								if(nt==null)
									noticeInfoDao.create(item);
							}
							getUnreadByTitle(noticesItem.getTitle());
							
							
						}
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
				
			case 3:
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
	
				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						if(jo!=null)
						{
								for(SchoolWorkItem item:schoolWorkItems)
								{
									if(item.getTemplateName().equals("�����"))
										item.setUnread(jo.optInt(item.getWorkText()));
									
								}
								adapter.notifyDataSetChanged();
							
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} 
				}
				break;
			
			
			default:
				break;
			}
		}
	};
	@Override
	protected void onDestroy() {
	
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
		
		Log.d(TAG,"��������:onDestroy");
	}
	@Override
	protected void onStart() {
		super.onStart();
		
		Log.d(TAG, "��������:Start");
		/*
		if(needCount)
			getUnreadCount();
		*/
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "��������:Stop");
	}
}
