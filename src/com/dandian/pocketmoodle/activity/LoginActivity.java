package com.dandian.pocketmoodle.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.minidev.json.JSONValue;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TableRow;
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
import com.dandian.pocketmoodle.db.InitData;
import com.dandian.pocketmoodle.entity.AccountInfo;
import com.dandian.pocketmoodle.entity.ContactsInfo;
import com.dandian.pocketmoodle.entity.User;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.BaiduPushUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.DialogUtility;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.dandian.pocketmoodle.util.ZLibUtils;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.dandian.pocketmoodle.activity.WebSiteActivity;


public class LoginActivity extends Activity implements OnClickListener,
		OnDismissListener {
	private static final String TAG = "LoginActivity";
	private EditText mUsernameView, mPasswordView;
	private Button loginButton, experienceButton;
	private TableRow table_item;
	private String mUsername, mPassword;
	private User user;
	private DatabaseHelper database;
	private Dialog mLoadingDialog, experienceDialog,userTypeDialog;
	private TextView mHowtouse;
	private ImageButton login_choose;
	private ListView listView;
	private PopupWindow popupWindow;
	private loginHistoryAdapter adapter;
	private Dao<User, Integer> userDao;
	private Dao<AccountInfo, Integer> accountInfoDao;
	private String[] userTypes;
	private String teacher,student;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		teacher=getString(R.string.teacher);
		student=getString(R.string.student);
		userTypes = new String[]{ teacher,student,getString(R.string.search_cancel)};
		setContentView(R.layout.activity_login);

		buildComponents();
	
		try {
			accountInfoDao = getHelper().getAccountInfoDao();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		PrefUtility.put(Constants.PREF_BAIDU_USERID, "");
		PrefUtility.put(Constants.PREF_CHECK_CODE,"");
		
		mUsername=PrefUtility.get(Constants.PREF_LOGIN_NAME, "");
		mPassword=PrefUtility.get(Constants.PREF_LOGIN_PASS, "");
		
		if(mUsername!=null && mUsername.length()>0)
		{
	
			mUsernameView.setText(mUsername);
			mPasswordView.setText(mPassword);
			doLogin();
		}
		
	}

	private void buildComponents() {
		table_item = (TableRow) findViewById(R.id.table_item);
		mUsernameView = (EditText) findViewById(R.id.login_username);
		mPasswordView = (EditText) findViewById(R.id.login_password);
		loginButton = (Button) findViewById(R.id.btn_login);
		experienceButton = (Button) findViewById(R.id.btn_experience);
		login_choose = (ImageButton) findViewById(R.id.login_choose);
		mLoadingDialog = DialogUtility.createLoadingDialog(this, getString(R.string.logging));
		mHowtouse=(TextView) findViewById(R.id.tv_howtouse);
		mHowtouse.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );
		
		mHowtouse.setOnClickListener(this);
		loginButton.setOnClickListener(this);
		experienceButton.setOnClickListener(this);
		login_choose.setOnClickListener(this);
		
		TextView copyright=(TextView) findViewById(R.id.tv_copyright);
		String thisVersion = CampusApplication.getVersion();
		copyright.append(" Ver:"+thisVersion);
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	
		case R.id.btn_login:
			attemptLogin();
			break;
		case R.id.btn_experience:
			/*
			mUsernameView.setText("1329641087@moodle360.cn");
			mPasswordView.setText("123456");
			attemptLogin();
			*/
			PrefUtility.put(Constants.PREF_CHECK_TEST, true);
			// jumpExperience();
			showUserTypeDialog(userTypes);
			break;
		case R.id.login_choose:
			if (adapter == null) {
				listView = new ListView(this);
				listView.setBackgroundColor(Color.GRAY);
				adapter = new loginHistoryAdapter();
				listView.setAdapter(adapter);
				popupWindow = new PopupWindow(listView, table_item.getWidth(),
						LayoutParams.WRAP_CONTENT);
				popupWindow.setFocusable(true);
		
				popupWindow.setOutsideTouchable(true);
				
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				popupWindow.showAsDropDown(table_item);
				login_choose.setImageResource(R.drawable.login_btn_bg_sel);
			} else {
				adapter.notifyDataSetChanged();
				popupWindow = new PopupWindow(listView, table_item.getWidth(),
						LayoutParams.WRAP_CONTENT);
				popupWindow.setFocusable(true);
			
				popupWindow.setOutsideTouchable(true);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				popupWindow.showAsDropDown(table_item);
				login_choose.setImageResource(R.drawable.login_btn_bg_sel);
			}
			popupWindow.setOnDismissListener(this);
			break;
		case R.id.tv_howtouse:
			Intent aboutusIntent = new Intent(this,WebSiteActivity.class);
			aboutusIntent.putExtra("url", CampusAPI.howtouse);
			aboutusIntent.putExtra("title", getResources().getString(R.string.login_left_bottom));
			startActivity(aboutusIntent);
			break;

		}
	}

	private void showUserTypeDialog(String[] data) {
		userTypeDialog = new Dialog(LoginActivity.this, R.style.dialog);
		View view = LayoutInflater.from(getBaseContext()).inflate(
				R.layout.view_exam_login_dialog, null);
		ListView mList = (ListView) view.findViewById(R.id.list);
		DialogAdapter dialogAdapter = new DialogAdapter(data);
		mList.setAdapter(dialogAdapter);
		Window window = userTypeDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);// 在底部弹出
		window.setWindowAnimations(R.style.CustomDialog);
		window.setGravity(Gravity.CENTER);
		userTypeDialog.setContentView(view);
		userTypeDialog.show();
		
	}
	
	private void attemptLogin() {
		// Reset errors.
		
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
	
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_username_required));
			focusView = mUsernameView;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
		} else {

			doLogin();
		}
	}

	private void doLogin() {
		mLoadingDialog.show();
		String dataResult = "";
		Locale locale = getResources().getConfiguration().locale;
	    String language = locale.getCountry();
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("用户名", mUsername);
			jsonObj.put("密码", mPassword);
			jsonObj.put("language", language);
			dataResult = Base64.encode(jsonObj.toString().getBytes());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, dataResult);
		CampusAPI.loginCheck(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				
				Message msg = new Message();
				msg.what = 0;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				mLoadingDialog.dismiss();
				AppUtility.showErrorToast(LoginActivity.this,
						msg.obj.toString());
				break;
			case 0:
				
				String result = msg.obj.toString();
				try {
					result = new String(Base64.decode(result.getBytes("GBK")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				Log.d(TAG, "--->  " + result);
				try 
				{
					JSONObject jo = new JSONObject(result);
					String loginStatus = jo.optString("结果");
					
					if (!loginStatus.equals("成功")) {
						AppUtility.showToastMsg(LoginActivity.this, loginStatus,1);
						if(mLoadingDialog!=null)
							mLoadingDialog.dismiss();
					} else 
					{
						user = new User(jo.optJSONObject("用户信息"));
						
						String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
						PrefUtility.put(Constants.PREF_CHECK_CODE,user.getCheckCode());
						PrefUtility.put(Constants.PREF_LOGIN_NAME, mUsername);
						PrefUtility.put(Constants.PREF_LOGIN_PASS, mPassword);
						PrefUtility.put(Constants.PREF_CHECK_USERID,user.getId());
						PrefUtility.put(Constants.PREF_SCHOOL_DOMAIN,user.getDomain());
						PrefUtility.put(Constants.PREF_CHECK_SCLASS,user.getsClass());
						PrefUtility.put(Constants.PREF_CHECK_REALNAME,user.getName());
						PrefUtility.put(Constants.PREF_CHECK_HOSTID,user.getUserNumber());
						PrefUtility.put(Constants.PREF_CHECK_USERTYPE,user.getUserType());
						checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
						((CampusApplication)getApplicationContext()).setLoginUserObj(user);
						
						Log.d(TAG, "login_-------------checkCode"+ checkCode);
						Log.d(TAG, "login_-------------user.getDomain()"+ user.getDomain());
						
						getHelper().getEqmDao();
						userDao = getHelper().getUserDao();
						userDao.delete((PreparedDelete<User>) userDao.deleteBuilder().prepare());
						userDao.create(user);
						
						Boolean check_test = PrefUtility.getBoolean(
								Constants.PREF_CHECK_TEST, false);
						if (!check_test) {
							AccountInfo info = accountInfoDao.queryBuilder().where()
									.eq("userName", mUsername).queryForFirst();
							if (info == null) {
								AccountInfo accountInfo = new AccountInfo();
								long time = new Date().getTime();
								accountInfo.setUserName(mUsername);
								accountInfo.setPassWord(mPassword);
								accountInfo.setLoginTime(time);
								accountInfoDao.create(accountInfo);
							} else {
								long time = new Date().getTime();
								info.setUserName(mUsername);
								info.setPassWord(mPassword);
								info.setLoginTime(time);
								accountInfoDao.update(info);
							}
						}
						
						initContactInfo();
						
						String baiduUserId=PrefUtility.get(Constants.PREF_BAIDU_USERID, "");
						if(baiduUserId.length()>0 && !TabHostActivity.ifpostuserid)
						{
							InitData initData = new InitData(LoginActivity.this, getHelper(), null,"postBaiDuUserId",checkCode);
							initData.postBaiduUserId();
						}
					}
				} catch (Exception e) {
					if(mLoadingDialog!=null)
						mLoadingDialog.dismiss();
					e.printStackTrace();
				}
				
				break;
			case 1:
				Date dt=new Date();
				try
				{
					Log.d(TAG, "------------------初始化联系人----------------------");
					result = msg.obj.toString();
					byte[] contact64byte = null;
					String resultContact = "";
					try {
						if (AppUtility.isNotEmpty(result)) {
							contact64byte = Base64.decode(result.getBytes("GBK"));
							Log.d("TAG","---->  "+ contact64byte.toString());
						}
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
						
					}
					// 解密base64字符串，得到压缩字节流，后台为php，采用gzcompress函数压缩，协议为zlib
					resultContact = ZLibUtils.decompress(contact64byte);// 采用zlib协议解压缩
					
					
					if (!AppUtility.isNotEmpty(resultContact)) 
						AppUtility.showToastMsg(LoginActivity.this, resultContact);
					else
					{
						//JSONObject jObject = null;
						//jObject = new JSONObject(resultContact);
						//ContactsInfo contacts = new ContactsInfo(jObject);
						
						Object obj=JSONValue.parseStrict(resultContact);
						ContactsInfo contacts = new ContactsInfo(obj);
						
						if (contacts != null) {
							
							((CampusApplication)getApplicationContext()).setLinkManDic(contacts.getLinkManDic());
							((CampusApplication)getApplicationContext()).setLinkGroupList(contacts.getContactsFriendsList());
							//PrefUtility.putObject("linkManDic", contacts.getLinkManDic());
							//PrefUtility.putObject("linkGroupList", contacts.getContactsFriendsList());
						}
						PrefUtility.put(Constants.PREF_INIT_CONTACT_FLAG, true);
					}
				}  
				
				catch (Exception e) {
					if(mLoadingDialog!=null)
						mLoadingDialog.dismiss();
					e.printStackTrace();
					
				}
				Log.d(TAG, "----------联系人处理耗时:" + (new Date().getTime()-dt.getTime()));
				
				jumpMain();
				
				break;
			case 2:
				experienceDialog.dismiss();
				break;
			}
		};
	};

	public void initContactInfo() {
		Log.d(TAG, "开始初始化联系人信息");
			
		CampusParameters params = new CampusParameters();

		try {
			JSONObject jo = new JSONObject();
			String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE,"");
			jo.put("用户较验码", checkCode);
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
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----------联系人耗时:" + (new Date().getTime()-dt.getTime()));
				Message msg = new Message();
				msg.what = 1;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}

	private void jumpMain() {
		Intent intent = new Intent(this, TabHostActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		if(mLoadingDialog!=null)
			mLoadingDialog.dismiss();
		this.finish();
	}

	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (database != null) {
			OpenHelperManager.releaseHelper();
			database = null;
		}

	}

	/**
     */
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, com.dandian.pocketmoodle.db.DatabaseHelper.class);
		}
		return database;
	}

	/**
	 * 弹出窗口listview适配器
	 */
	public class DialogAdapter extends BaseAdapter {
		String[] arrayData;

		public DialogAdapter(String[] array) {
			this.arrayData = array;
		}

		@Override
		public int getCount() {
			return arrayData == null ? 0 : arrayData.length;
		}

		@Override
		public Object getItem(int position) {
			return arrayData[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.view_testing_pop, null);
				holder.title = (TextView) convertView.findViewById(R.id.time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final String text = arrayData[position];
			holder.title.setText(text);
			holder.title.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					PrefUtility.put(Constants.PREF_CHECK_TEST, false);
					if ("教师".equals(text)) {
						mUsernameView.setText("0335@moodle360.cn");
						mPasswordView.setText("123456");
						attemptLogin();
					} else if ("学生".equals(text)) {
						mUsernameView.setText("1329641087@moodle360.cn");
						mPasswordView.setText("123456");
						attemptLogin();
					}
					else if ("Teacher".equals(text)) {
						mUsernameView.setText("teacher001@md3.moodle360.cn");
						mPasswordView.setText("123001");
						attemptLogin();
					} else if ("Student".equals(text)) {
						mUsernameView.setText("student003@md3.moodle360.cn");
						mPasswordView.setText("321003");
						attemptLogin();
					}
					else if ("教師".equals(text)) {
						mUsernameView.setText("PP241@moodle360.cn");
						mPasswordView.setText("123456");
						attemptLogin();
					} else if ("學生".equals(text)) {
						mUsernameView.setText("PP240@moodle360.cn");
						mPasswordView.setText("123456");
						attemptLogin();
					}
					userTypeDialog.dismiss();
				}
			});
			return convertView;
		}

	}

	class ViewHolder {
		TextView title;
	}
	class loginHistoryAdapter extends BaseAdapter {
		List<AccountInfo> accountInfoList;
		LayoutInflater inflater;

		@SuppressWarnings("deprecation")
		public loginHistoryAdapter() {
			inflater = LayoutInflater.from(getApplicationContext());
			try {
				accountInfoList = accountInfoDao.queryBuilder()
						.orderBy("loginTime", false).limit(4).query();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public int getCount() {
			return accountInfoList == null ? 0 : accountInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return accountInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PopHolder holder = null;
			if (convertView == null) {
				holder = new PopHolder();
				convertView = inflater.inflate(
						R.layout.view_login_poplist_item, null);
				holder.userName = (TextView) convertView
						.findViewById(R.id.account);
				holder.deleteButton = (ImageButton) convertView
						.findViewById(R.id.delete_account);
				convertView.setTag(holder);
			} else {
				holder = (PopHolder) convertView.getTag();
			}

			final AccountInfo info = this.accountInfoList.get(position);
			Log.i(TAG, info.getUserName() + info.getPassWord());
			holder.userName.setText(info.getUserName());
			holder.userName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					mUsernameView.setText(info.getUserName());
					mPasswordView.setText(info.getPassWord());
					attemptLogin();
				}
			});
			holder.deleteButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						accountInfoDao.delete(info);
						accountInfoList.remove(info);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					adapter.notifyDataSetChanged();
					popupWindow.update();
					if (accountInfoList.size() == 0) {
						popupWindow.dismiss();
					}
				}
			});
			return convertView;
		}

	}

	class PopHolder {
		TextView userName;
		ImageButton deleteButton;
	}

	@Override
	public void onDismiss() {
		login_choose.setImageResource(R.drawable.login_btn_bg_nor);
	}
}
