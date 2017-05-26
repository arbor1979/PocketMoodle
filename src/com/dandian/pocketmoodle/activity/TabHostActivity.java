package com.dandian.pocketmoodle.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.dandian.pocketmoodle.CampusApplication;
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.db.DatabaseHelper;
import com.dandian.pocketmoodle.entity.ChatFriend;
import com.dandian.pocketmoodle.entity.User;
import com.dandian.pocketmoodle.service.SchoolService;
import com.dandian.pocketmoodle.service.SchoolService.MyIBinder;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.BaiduPushUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.dandian.pocketmoodle.widget.BottomTabLayout;
import com.dandian.pocketmoodle.widget.BottomTabLayout.OnCheckedChangeListener;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.ImageLoader;


@SuppressWarnings("deprecation")
public class TabHostActivity extends TabActivity   {
	private String TAG = "TabHostActivity";
	private BottomTabLayout mainTab;
	private TabHost tabHost;
	private Intent messageIntent;
	private Intent communicationIntent;
	private Intent schoolIntent,homeIntent;
	

	private Dao<ChatFriend,Integer> chatFriendDao;
	private List<ChatFriend> chatFriendList;
	
	private final static String TAB_TAG_HOME = "tab_tag_home";
	private final static String TAB_TAG_MESSAGE = "tab_tag_message";
	private final static String TAB_TAG_COMMUNICATION = "tab_tag_communication";
	// private final static String TAB_TAG_SUMMARY = "tab_tag_summary";
	private final static String TAB_TAG_SCHOOL = "tab_tag_school";
	
	
	// public static int currentWeek = 0,selectedWeek = 0,maxWeek =
	// 0;//当前周次,选择周次,选择周次
	DatabaseHelper database;
	private final String ACTION_NAME_REMIND = "remindSubject";
	private final String ACTION_CHATINTERACT =  "ChatInteract";
	private final String ACTION_CHANGEHEAD =  "ChangeHead";
	public final String STitle = "showmsg_title";
	public final String SMessage = "showmsg_message";
	public final String BAThumbData = "showmsg_thumb_data";
	private User user;
	private boolean isIntoBack;
	public static boolean ifpostuserid=false;
	
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				AppUtility.showErrorToast(TabHostActivity.this, msg.obj.toString());
				break;
			
			case 1:
				String result = msg.obj.toString();
				String resultStr = "";
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
						String tips = jo.optString("功能更新");
						String downLoadPath = jo.optString("下载地址");
						String newVer=jo.optString("最新版本号");
						if(AppUtility.isNotEmpty(tips)&&AppUtility.isNotEmpty(downLoadPath)){
							showUpdateTips(tips,downLoadPath,newVer);
						}
					}
				}
				break;
			
			default:
				break;
			}
		}
	};
	
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(!ImageLoader.getInstance().isInited())
			AppUtility.iniImageLoader(getApplicationContext());
		isIntoBack=true;
	
		user=((CampusApplication)getApplicationContext()).getLoginUserObjAllowNull();
		if(user==null)
		{
			finish();
			return;
		}
		
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                BaiduPushUtility.getMetaValue(this, "api_key"));
		/*
		String contentText = getIntent().getStringExtra("contentText");
		if (AppUtility.isNotEmpty(contentText)) {
			showDialog(contentText);
		}
		*/
		setContentView(R.layout.activity_tabhost);
		mainTab = (BottomTabLayout) findViewById(R.id.bottom_tab_layout);
		mainTab.setOnCheckedChangeListener(changeListener);
		int color=PrefUtility.getInt(Constants.PREF_THEME_TABBARCOLOR, 0);
		if(color!=0)
			mainTab.setBackgroundColor(color);
		try {
			chatFriendDao = getHelper().getChatFriendDao();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		prepareIntent();
		setupIntent();
		
		showUnreadCnt();
		
		//版本检测
		versionDetection();
			
		//regToWx(); // 注册微信
		registerBoradcastReceiver();
		
		String toTag = getIntent().getStringExtra("tab");
		if(toTag==null)
			findView();
		
		Log.d(TAG,"生命周期:onCreate");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		//showUnreadCnt();
		if(isIntoBack)
		{
			isIntoBack=false;
			//getNetLocation();
		}
		
		Log.d(TAG,"生命周期:onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();
	
		if(AppUtility.isApplicationBroughtToBackground(this))
			isIntoBack=true;
		Log.d(TAG,"生命周期:onStop");
	}

	/**
	 * 准备tab的内容Intent
	 */
	private void prepareIntent() {
		
		homeIntent = new Intent(this, HomeActivity.class);
		schoolIntent = new Intent(this, MyCourseActivity.class);
		communicationIntent = new Intent(this, ContactsActivity.class);
		messageIntent = new Intent(this, ChatFriendActivity.class);
		// summaryIntent = new Intent(this, SummaryActivity.class);
		
	}

	private void setupIntent() {
		this.tabHost = getTabHost();
		TabHost localTabHost = this.tabHost;
		localTabHost.addTab(buildTabSpec(TAB_TAG_HOME, R.string.school,
				R.drawable.ic_launcher, homeIntent));
		
		localTabHost.addTab(buildTabSpec(TAB_TAG_SCHOOL, R.string.course,
				R.drawable.ic_launcher, schoolIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_MESSAGE, R.string.message,
				R.drawable.ic_launcher, messageIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_COMMUNICATION,
				R.string.curriculum, R.drawable.ic_launcher,
				communicationIntent));
		// localTabHost.addTab(buildTabSpec(TAB_TAG_SUMMARY, R.string.summary,
		// R.drawable.ic_launcher, summaryIntent));

	}

	/**
	 * 构建TabHost的Tab页
	 * 
	 * @param tag
	 *            标记
	 * @param resLabel
	 *            标签
	 * @param resIcon
	 *            图标
	 * @param content
	 *            该tab展示的内容
	 * @return 一个tab
	 */
	private TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.tabHost
				.newTabSpec(tag)
				.setIndicator(getString(resLabel),
						getResources().getDrawable(resIcon))
				.setContent(content);
	}

	// 设置默认选中项
	private void findView() {
		View nearBtn = mainTab.findViewById(R.id.bottom_tab_home);
		nearBtn.setSelected(true);
	}

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {
		@Override
		public void OnCheckedChange(View checkview) {
			switch (checkview.getId()) {
			
			case R.id.bottom_tab_home:
				tabHost.setCurrentTabByTag(TAB_TAG_HOME);
			
				break;
			case R.id.bottom_tab_message:
				tabHost.setCurrentTabByTag(TAB_TAG_MESSAGE);
				
				break;
			case R.id.bottom_tab_communication:
				tabHost.setCurrentTabByTag(TAB_TAG_COMMUNICATION);
				
				break;
			/*
			 * case R.id.bottom_tab_summary:
			 * tabHost.setCurrentTabByTag(TAB_TAG_SUMMARY);
			 * SummaryActivity.layout_menu.setOnClickListener(new
			 * MenuListener()); break;
			 */
			case R.id.bottom_tab_school:
				tabHost.setCurrentTabByTag(TAB_TAG_SCHOOL);
				
				break;
			
			
			}
			
		}

		@Override
		public void OnCheckedClick(View checkview) {

		}
	};


	/**
	 * 功能描述:显示头像大图
	 * 
	 * @author shengguo 2014-5-9 下午3:04:49
	 * 
	 * @param imagePath
	 */
	/*
	private void showImageDialog(String imagePath) {
		View view = getLayoutInflater().inflate(R.layout.view_image, null);
		AQuery aq = new AQuery(view);
		final Dialog dialog = DialogUtility.createLoadingDialog(
				TabHostActivity.this, "show_image_dialog");
		dialog.setContentView(view);
		dialog.setCancelable(true);// 可以点返回键取消
		dialog.show();
		aq.id(R.id.iv_img).image(imagePath).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	*/

	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return database;
	}

	public void showDialog(String contentText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				TabHostActivity.this);
		builder.setTitle("课程提醒");
		builder.setMessage(contentText);
		builder.setNegativeButton("知道了", new cancelStudentPicListener());
		AlertDialog ad = builder.create();
		ad.show();
	}

	

	/**
	 * 功能描述:显示消息数量
	 *
	 * @author shengguo  2014-5-29 下午3:07:35
	 *
	 */
	private void showUnreadCnt() {
		int count = 0;
		try {
			chatFriendList = chatFriendDao.queryBuilder().where().eq("hostid", user.getUserNumber()).query();
			for (ChatFriend chatFriend:chatFriendList) {
				count += chatFriend.getUnreadCnt();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TextView unreadCnt = (TextView) mainTab.findViewById(R.id.unreadCnt);
		if(count!=0){
			unreadCnt.setText(String.valueOf(count));
			unreadCnt.setVisibility(View.VISIBLE);
		}else{
			unreadCnt.setVisibility(View.INVISIBLE);
		}
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "--------------注销广播/关闭服务-------------");
		try
		{
			unregisterReceiver(mBroadcastReceiver);
		}
		catch(IllegalArgumentException e)
		{
			
		}
		
		Log.d(TAG,"生命周期:onDestroy");
	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_NAME_REMIND);
		myIntentFilter.addAction(ACTION_CHATINTERACT);
		myIntentFilter.addAction(ACTION_CHANGEHEAD);
		
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private class cancelStudentPicListener implements
			DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}

	}

	
	
	
	/**
	 * 功能描述:版本检测
	 *
	 * @author shengguo  2014-6-3 下午4:05:05
	 *
	 */
	private void versionDetection() {
		String thisVersion = CampusApplication.getVersion();
		String check=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		long datatime = System.currentTimeMillis();
		String base64Str = null;
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("当前版本号", thisVersion);
			jsonObj.put("用户较验码", check);
			jsonObj.put("DATETIME", datatime);

			base64Str = Base64.encode(jsonObj.toString().getBytes());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		Log.d(TAG, "---------------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.versionDetection(params, new RequestListener() {
			
			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response"+e.getMessage());
				Message msg=new Message();
				msg.what = -1;
				msg.obj= e.getMessage();
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----response"+response);
				
				Message msg=new Message();
				msg.what = 1;
				msg.obj= response;
				mHandler.sendMessage(msg);
			}
		});
	}
	
	/**
	 * 功能描述:询问是否更新
	 *
	 * @author shengguo  2014-6-3 下午4:31:55
	 *
	 */
	private void showUpdateTips(String tips,final String downLoadPath,String newVer) {
		View view = LayoutInflater.from(TabHostActivity.this).inflate(
				R.layout.view_textview, null);
		TextView tvTip = (TextView) view.findViewById(R.id.tv_text);
		tvTip.setText(tips);
		AlertDialog dialog_UpdateTips = new AlertDialog.Builder(TabHostActivity.this)
				.setView(view)
				.setTitle(newVer+getString(R.string.updatetip))
				.setPositiveButton(getString(R.string.go), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "-------------downLoadPath:" + downLoadPath);
						//schoolService.downLoadUpdate(downLoadPath, 1001);
						AppUtility.downloadUrl(downLoadPath, null, TabHostActivity.this);
						dialog.dismiss();
					}
				})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		dialog_UpdateTips.show();
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_NAME_REMIND)) {
				Log.d(TAG, "----------->BroadcastReceiver："
						+ ACTION_NAME_REMIND);
				String contentText = intent.getStringExtra("contentText");
				showDialog(contentText);
			}else if(action.equals(ACTION_CHATINTERACT)){
				showUnreadCnt();
			}
			
		}
	};
	
}
