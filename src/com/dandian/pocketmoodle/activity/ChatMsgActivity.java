package com.dandian.pocketmoodle.activity;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.example.androidgifdemo.MyTextViewEx;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.dandian.pocketmoodle.activity.ChatMsgActivity;
import com.dandian.pocketmoodle.activity.ImagesActivity;
import com.dandian.pocketmoodle.activity.ShowPersonInfo;
import com.dandian.pocketmoodle.CampusApplication;
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.adapter.ExpressionGvAdapter;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.db.DatabaseHelper;
import com.dandian.pocketmoodle.db.InitData;
import com.dandian.pocketmoodle.entity.ChatFriend;
import com.dandian.pocketmoodle.entity.ChatMsg;
import com.dandian.pocketmoodle.entity.ChatMsgDetail;
import com.dandian.pocketmoodle.entity.ContactsMember;
import com.dandian.pocketmoodle.entity.Page;
import com.dandian.pocketmoodle.entity.User;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.AsynImageLoader;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.DateHelper;
import com.dandian.pocketmoodle.util.ExpressionUtil;
import com.dandian.pocketmoodle.util.FileUtility;
import com.dandian.pocketmoodle.util.ImageUtility;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.dandian.pocketmoodle.widget.InnerScrollView;
import com.dandian.pocketmoodle.widget.PredicateLayout;
import com.dandian.pocketmoodle.widget.XListView;
import com.dandian.pocketmoodle.widget.XListView.IXListViewListener;

@SuppressLint("NewApi")
public class ChatMsgActivity extends FragmentActivity implements IXListViewListener{
	private static final int PIC_REQUEST_CODE_SELECT_CAMERA = 1;// ����
	private static final int PIC_Select_CODE_ImageFromLoacal = 2;// �ӱ��ػ�ȡͼƬ
	private static final String TAG = "ChatMsgActivity";
	private TextView mBtnSend;
	private EditText mEditTextContent;
	// �������ݵ�������
	private InteractAdapter mAdapter;
	private XListView mListView;
	// ���������
	private List<ChatMsg> chatMsgList;
	private Dao<ChatMsg, Integer> chatMsgDao;
	private Dao<ChatMsgDetail, Integer> chatMsgDetailDao;
	private Dao<ChatFriend, Integer> chatFriendDao;
	private Button bn_back, bn_info;
	private LinearLayout layout_info;
	private TextView tv_title;
	private ImageView cameraImage, faceImage;
	private String toname,userImage,msg_type,ACTION_NAME = "ChatInteract";
	DatabaseHelper database;
	AsynImageLoader asynImageLoader;
	public static boolean isruning = false;
	public static String toid = "";
	private String myPicPath,myHeadPic;// ���յ�ͼƬ·��,�ҵ�ͷ��
	Page page;
	// �������
	private ViewPager viewPager; // ʵ�ֱ���Ļ�����ҳ
	private View viewpager_layout;
	private RelativeLayout express_spot_layout;
	private int imageIds[] = ExpressionUtil.getExpressRcIds(); // �������б�����Դ��id
	
	private Map<String,ProgressBar> imageProgressBars = new HashMap<String,ProgressBar>();
	private List<String> msgIds = new ArrayList<String>();
	private User user;
	AQuery aq;
	Dialog downloadDialog;
	int startRow = 0;
	RelativeLayout flaglayout;
	ProgressBar pb;
	int msgPager = 0;
	private Timer timer; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();
		
		myHeadPic = user.getUserImage();
		
		isruning = true;
		Log.d(TAG, "��������:Create");
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		setContentView(R.layout.view_chat_interact);
		Intent intent = getIntent();
		toid = getIntent().getStringExtra("toid");
		toname = intent.getStringExtra("toname");
		userImage = intent.getStringExtra("userImage");
		msg_type = intent.getStringExtra("type");
		Log.d(TAG, "----------->userImage��" + userImage);
		mListView = (XListView) findViewById(R.id.chat_list_interact);
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false);
		mListView.setXListViewListener(this);
		initTitle();
		initView();
		initData();// ����������Ϣ
		//֪ͨTabHostActivity�ײ�����Ϣͼ�����
		Intent i = new Intent(ACTION_NAME);
		sendBroadcast(i);
		initListener();
		registerBoradcastReceiver();// ע��㲥
		
		timer = new Timer();
		timer.schedule(task,1000,10000);
	}

	TimerTask task = new TimerTask( ) {
		public void run ( ) {
			getMsgState();
		}
	};
		
	@Override
	protected void onStart() {
		super.onStart();
		isruning = true;
		Log.d(TAG, "��������:Start");
	}

	@Override
	protected void onStop() {
		super.onStop();
		isruning = false;
		Log.d(TAG, "��������:Stop");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		/*
		//֪ͨTabHostActivity�ײ�����Ϣͼ�����
		Intent i = new Intent(ACTION_NAME);
		sendBroadcast(i);
		*/
		isruning = true;
		Log.d(TAG, "��������:Restart");
		initData();
	}

	/**
	 * ����ͷ��
	 */
	private void initTitle() {
		layout_info = (LinearLayout)findViewById(R.id.setting_layout_goto);
		tv_title = (TextView) findViewById(R.id.setting_tv_title);
		bn_back = (Button) findViewById(R.id.back);
		bn_info = (Button) findViewById(R.id.setting_btn_goto);
		layout_info.setVisibility(View.VISIBLE);
		bn_info.setVisibility(View.VISIBLE);
//		if(msg_type.equals("Ⱥ��Ϣ")){
//			bn_info.setBackgroundResource(R.drawable.contacts_member);
//			tv_title.setText("Ⱥ��:" + toname);
//		}else{
			//bn_info.setBackgroundResource(R.drawable.shuaizi);
			Drawable drawable= getResources().getDrawable(R.drawable.shuaizi);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			bn_info.setCompoundDrawables(drawable,null,null,null);
			//bn_info.setBackgroundDrawable(drawable);
			//tv_title.setText(toname);
		//}
		flaglayout =(RelativeLayout) findViewById(R.id.flaglayout);
		bn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(bn_back.getWindowToken(), 0);
				finish();
			}
		});
		
		layout_info.setOnClickListener(new MyListener());
	}

	// ��ʼ����ͼ
	private void initView() {
		mBtnSend = (TextView) findViewById(R.id.send);
		mBtnSend.setOnClickListener(new MyListener());
		mEditTextContent = (EditText) findViewById(R.id.edit);
		cameraImage = (ImageView) findViewById(R.id.camera);
		faceImage = (ImageView) findViewById(R.id.face);

		viewpager_layout = findViewById(R.id.viewpager_layout);
		express_spot_layout = (RelativeLayout) findViewById(R.id.express_spot_layout);
		viewPager = (ViewPager) findViewById(R.id.tabpager);
	}

	// ������Ϣ����¼�
	private void initListener() {
		// ѡ��ͼƬ
		cameraImage.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				showGetPicDialog();
			}
		});
		// ��ʾ����
		faceImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showExpressionWindow(v);
			}
		});

		// ���ر����
		mEditTextContent.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				viewpager_layout.setVisibility(View.GONE);
				mListView.setSelection(mAdapter.getCount());
				return false;
			}
		});
	}
	/**
	 * ��������:��ʾ��ȡͼƬ�ķ���
	 *
	 * @author shengguo  2014-6-4 ����10:16:24
	 *
	 */
	private void showGetPicDialog() {
		View localView = getLayoutInflater().inflate(R.layout.view_dialog_schedule, null);
		ListView list = (ListView) localView.findViewById(R.id.list);
		Button close = (Button)localView.findViewById(R.id.close);
		close.setText(R.string.cancel);
		String[] data = getResources().getStringArray(R.array.camera_dialog);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.view_testing_pop, R.id.time, data);
		list.setAdapter(adapter);
		final Dialog dialog = new Dialog(this, R.style.dialog);
		dialog.setContentView(localView);
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				switch(position){
				case 0:
					Log.d(TAG, "get_pic_from_camera");
					getPicByCamera();
					dialog.dismiss();
					break;
				case 1:
					Log.d(TAG, "get_pic_from_location");
					getPicFromLocation();
					dialog.dismiss();
					break;
					default:
						break;
				}
				
			}
		});
		close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
		dialog.show();
	}
	// �����ȡͷ��
	public void getPicByCamera() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����
			AppUtility.showToastMsg(this, getString(R.string.Commons_SDCardErrorTitle));
			return;
		}
		myPicPath =FileUtility.getRandomSDFileName("jpg");
		
		File mCurrentPhotoFile = new File(myPicPath);
		Uri uri = Uri.fromFile(mCurrentPhotoFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, PIC_REQUEST_CODE_SELECT_CAMERA);
		
	}
	//hu
	public void getPicFromLocation() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// �ж��Ƿ���SD��
			/*
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
			*/
			Intent intent; 
			intent = new Intent(Intent.ACTION_PICK, 
			                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); 
			startActivityForResult(intent, PIC_Select_CODE_ImageFromLoacal);
		} else {
			AppUtility.showToastMsg(this, getString(R.string.Commons_SDCardErrorTitle));
		}
		
	}

	// �õ�ǰʱ���ȡ�õ�ͼƬ����
	@SuppressLint("SimpleDateFormat")
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Random random = new Random();
		int num = random.nextInt() * 1000;
		return dateFormat.format(date) + num + ".jpg";
	}

	// ��ʼ��Ҫ��ʾ������
	private void initData() {
		try {
			if(!AppUtility.isApplicationBroughtToBackground(this))
			{
				// �������б��δ����Ϣ��������Ϊ0
				chatFriendDao = getHelper().getChatFriendDao();
				ChatFriend chatFriend = chatFriendDao.queryBuilder().where()
					.eq("toid", toid).and().eq("hostid", user.getUserNumber()).queryForFirst();
				if (chatFriend != null) {
					chatFriend.setUnreadCnt(0);
					chatFriendDao.update(chatFriend);
				}
			}
			// ������Ϣ
			chatMsgDao = getHelper().getChatMsgDao();
			chatMsgDetailDao = getHelper().getChatMsgDetailDao();
			chatMsgList = new ArrayList<ChatMsg>();
			queryData(msgPager);
			mAdapter = new InteractAdapter(this, chatMsgList);
			mListView.setAdapter(mAdapter);
			mListView.setSelection(mListView.getCount());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void queryData(int pager){
		try {
			int num = 10 * (pager + 1);
			chatMsgList = chatMsgDao.queryBuilder().orderBy("time", false).limit(num).where().eq("toid", toid).and().eq("hostid", user.getUserNumber()).query();
			Collections.reverse(chatMsgList);
			updateMsgState();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//�����Ѷ�״̬
	private void updateMsgState()
	{
		if(!isruning) return;
		ArrayList<String> msgidList=new ArrayList<String>();
		for(ChatMsg item:chatMsgList)
		{
			if(item.getMsgFlag()==0 && AppUtility.isNotEmpty(item.getMsg_id()) && !AppUtility.isNotEmpty(item.getSendstate()))
			{
				msgidList.add(item.getMsg_id());
			}
		}
		String msg_ids="";
		for(int i=0;i<msgidList.size();i++)
		{
			if(msg_ids.length()==0)
				msg_ids=msgidList.get(i);
			else
				msg_ids+=","+msgidList.get(i);
		}
		if(msg_ids.length()>0)
		{
			JSONObject jo = new JSONObject();
			String user_code = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
			try {
				String datetime = String.valueOf(new Date().getTime());
				jo.put("ACTION", "SetInfo");
				jo.put("�û�������", user_code);
				jo.put("DATETIME", datetime);
				jo.put("MSG_ID_LIST", msg_ids);
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			CampusParameters params = new CampusParameters();
			params.add(Constants.PARAMS_DATA,
					Base64.encode(jo.toString().getBytes()));
			CampusAPI.updatesmsState(params, new RequestListener() {

				@Override
				public void onIOException(IOException e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onError(CampusException e) {
					
				}

				@Override
				public void onComplete(String response) {
					Message msg = new Message();
					msg.what = 6;
					msg.obj = response;
					mHandler.sendMessage(msg);
				}
			});
		}
	}
	//��ȡ�Ѷ�״̬
	private void getMsgState()
	{
		if(!isruning) return;
		ArrayList<String> msgidList=new ArrayList<String>();
		for(ChatMsg item:chatMsgList)
		{
			if(item.getMsgFlag()==1 && AppUtility.isNotEmpty(item.getMsg_id()) && item.getSendstate().equals("�ʹ�"))
			{
				if(item.getToid().split(",").length>1)
				{
					try {
						List<ChatMsgDetail> detailList=chatMsgDetailDao.queryBuilder().where().eq("mainid", item.getId()).and().eq("sendstate", "�ʹ�").query();
						for(ChatMsgDetail detail:detailList)
							msgidList.add(detail.getMsg_id());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else
					msgidList.add(item.getMsg_id());
			}
		}
		String msg_ids="";
		for(int i=0;i<msgidList.size();i++)
		{
			if(msg_ids.length()==0)
				msg_ids=msgidList.get(i);
			else
				msg_ids+=","+msgidList.get(i);
		}
		if(msg_ids.length()>0)
		{
			JSONObject jo = new JSONObject();
			String user_code = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
			try {
				String datetime = String.valueOf(new Date().getTime());
				jo.put("ACTION", "GetInfo");
				jo.put("�û�������", user_code);
				jo.put("DATETIME", datetime);
				jo.put("MSG_ID_LIST", msg_ids);
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			CampusParameters params = new CampusParameters();
			params.add(Constants.PARAMS_DATA,
					Base64.encode(jo.toString().getBytes()));
			CampusAPI.updatesmsState(params, new RequestListener() {

				@Override
				public void onIOException(IOException e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onError(CampusException e) {
					
				}

				@Override
				public void onComplete(String response) {
					Message msg = new Message();
					msg.what = 6;
					msg.obj = response;
					mHandler.sendMessage(msg);
				}
			});
		}
	}
	public class MyListener implements OnClickListener {
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.send:
				String contString = mEditTextContent.getText().toString();
				send(contString,"txt","",0);
				break;
			case R.id.setting_layout_goto:
				AlertDialog.Builder showbuilder = new AlertDialog.Builder(ChatMsgActivity.this);  
				showbuilder.setTitle("");
				showbuilder.setMessage(R.string.clearmessagehistory);  
				showbuilder.setPositiveButton(R.string.go, new clearChatListener());  
				showbuilder.setNegativeButton(R.string.cancel, new cancelStudentPicListener());
	            AlertDialog ad = showbuilder.create();  
	            ad.show();
				break;
			default:
				break;
			}

		}
	}
	//������  
    private class clearChatListener implements DialogInterface.OnClickListener{  
        @SuppressWarnings("unchecked")
		@Override  
        public void onClick(DialogInterface dialog, int which) {  
        	try {
				chatMsgDao = getHelper().getChatMsgDao();
				chatMsgDao.delete((PreparedDelete<ChatMsg>)chatMsgDao.deleteBuilder().where().eq("toid", toid).prepare());
				Toast.makeText(ChatMsgActivity.this, R.string.messagehistoryclear, Toast.LENGTH_LONG).show();
				mAdapter.coll.clear();
				mAdapter.notifyDataSetChanged();
			} catch (SQLException e) {
				e.printStackTrace();
				Toast.makeText(ChatMsgActivity.this, R.string.failed, Toast.LENGTH_LONG).show();
			}
        	
        	
        }  
    } 
  //������  
    private class cancelStudentPicListener implements DialogInterface.OnClickListener{  
        @Override  
        public void onClick(DialogInterface dialog, int which) {  
        	dialog.dismiss();
        }  
    } 
	/**
	 * ��������:������Ϣ
	 * 
	 * @author yanzy 2013-12-17 ����4:19:16
	 * 
	 * �޸ģ�ChatMsg�����ݿ�д����Ӳ���msg_type��Ϣ����
	 */
	private void send(String contString,String type,String imgpath,int msgid) {

		if (contString.length() > 0) {
			try {
				String content = contString;
				chatFriendDao = getHelper().getChatFriendDao();
				ChatFriend chatList = chatFriendDao.queryBuilder().where()
						.eq("toid", toid).and().eq("hostid", user.getUserNumber()).queryForFirst();
				// ����Ϣ���浽�������ݿ�
				InitData initData = new InitData(this, getHelper(), null, null,null);
				if ("img".equals(type)) {
					contString = imgpath;
				}
				ChatMsg entity = initData.sendChatToDatabase(type,toid, toname, 1,
						contString, chatList,msg_type,userImage,"");
				
				if ("txt".equals(type)) {
					//initData();
					chatMsgList.add(entity);
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(mListView.getCount());
					msgid=entity.getId();
					// ��������UI����
					mEditTextContent.setText("");
				}
				
				String datetime = String.valueOf(new Date().getTime());
				sendSMS(content, toid, datetime,type,msgid,msg_type);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * ��������:��������Ϣ�ύ�����������������ðٶ������ͣ�����Ϣ������toid�û��ֻ�
	 * 
	 * @author yanzy 2013-12-17 ����4:18:35
	 * 
	 * @param content
	 * @param toid
	 * @param time
	 */
	public void sendSMS(String content, String toid, String time,String type,final int msgId,String msg_type) {
		JSONObject jo = new JSONObject();
		String user_code = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		try {
			jo.put("CONTENT", content);
			jo.put("CONTENT_TYPE", type);
			jo.put("action", "DataDeal");
			jo.put("�û�������", user_code);
			jo.put("DATETIME", time);
			jo.put("TOID", toid);
			if(msg_type.equals("Ⱥ��Ϣ")){
				jo.put("MSG_TYPE", "Ⱥ��Ϣ");
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA,
				Base64.encode(jo.toString().getBytes()));
		CampusAPI.smsSend(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = 5;
				msg.obj = e.getMessage();
				msg.arg1 = msgId;
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Message msg = new Message();
				msg.what = 0;
				msg.obj = response;
				msg.arg1 = msgId;
				mHandler.sendMessage(msg);
			}
		});
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				String result = msg.obj.toString();
				int msgId = msg.arg1;
				Log.d(TAG, "------->result:" + result);
				String sendResult="";
				
				try {
					sendResult = new String(Base64.decode(result.getBytes("ISO-8859-1")));
					Log.d(TAG, "---------------->result:" + sendResult);
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				try {
					JSONObject jo = new JSONObject(sendResult);
					
					if (jo.optString("MSG_STATUS").equals("�ɹ�") && msgId > 0) {
						String msgIdStr = String.valueOf(msgId);
						ProgressBar progressBar = imageProgressBars.get(msgIdStr);
						if(progressBar!=null)
						{
							progressBar.setVisibility(View.INVISIBLE);
							msgIds.remove(msgIdStr);
							imageProgressBars.remove(msgIdStr);
						}
						ChatMsg cm=chatMsgDao.queryForId(msgId);
						
						if(cm.getType().equals("img"))
						{
							
							String newPath=jo.optJSONObject("CONTENT").optString("description");
							cm.setRemoteimage(newPath);
							
						}
						cm.setSendstate("�ʹ�");
						String msg_id=(String)jo.optJSONArray("MSG_ID").get(0);
						cm.setMsg_id(msg_id);
						chatMsgDao.update(cm);
						
						JSONArray toidArray=jo.optJSONArray("TO_USERID_UNIQUE");
						if(toidArray.length()>1)
						{
							JSONArray msgidArray=jo.optJSONArray("MSG_ID");
							for(int i=0;i<toidArray.length();i++)
							{
								String toid=(String)toidArray.get(i);
								String msgid=(String)msgidArray.get(i);
								ChatMsgDetail cmd=new ChatMsgDetail(cm.getId(),toid,msgid,"�ʹ�");
								chatMsgDetailDao.create(cmd);
							}
						}
						
						for(ChatMsg item:chatMsgList)
						{
							if(item.getId()==cm.getId())
							{
								item.setRemoteimage(cm.getRemoteimage());
								item.setSendstate(cm.getSendstate());
								item.setMsg_id(cm.getMsg_id());
								break;
							}
						}
						mAdapter.notifyDataSetChanged();
					}
					AppUtility.playSounds(R.raw.sent_chat, ChatMsgActivity.this);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 1:
				int position = mAdapter.getCount();
				mAdapter = new InteractAdapter(ChatMsgActivity.this, chatMsgList);
				mListView.setAdapter(mAdapter);
				mListView.stopRefresh();
				mListView.setSelection(mAdapter.getCount() - position - 1);
				break;
			case 2:
				Bundle bundle = (Bundle) msg.obj;
				String content = bundle.getString("content");
				String type = bundle.getString("type");
				String path = bundle.getString("path");
				int msgid = bundle.getInt("msgidInt");
				send(content,type,path,msgid);
				break;
			case 3:
				mAdapter.notifyDataSetChanged();
				// ���������������Ϣ���͵�toid�û��ֻ�
				break;
			case 4://�ϴ�ͼ��
				final String pathStr = msg.obj.toString();
				ImageUtility.rotatingImageIfNeed(pathStr); 
				ChatMsg entity = new ChatMsg();
				entity.setType("img");
				entity.setHostid(user.getUserNumber());
				entity.setToid(toid);
				entity.setToname(toname);
				entity.setTime(new Date());
				entity.setMsgFlag(1);
				entity.setContent(pathStr);
				
				try {
					chatMsgDao = getHelper().getChatMsgDao();
					chatMsgDao.create(entity);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				chatMsgList.add(entity);
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mListView.getCount());
				
				final int msgidInt = entity.getId();
				msgIds.add(String.valueOf(msgidInt));
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						SystemClock.sleep(100);
						sendImage(pathStr,msgidInt);
					}
				}).start();
				//sendImage(pathStr);
				break;
			case 5 :
				AppUtility.showErrorToast(ChatMsgActivity.this,
						msg.obj.toString());
				
				msgId = msg.arg1;
				String msgIdStr = String.valueOf(msgId);
				ProgressBar progressBar = imageProgressBars.get(msgIdStr);
				if(progressBar!=null)
				{
					progressBar.setVisibility(View.INVISIBLE);
					msgIds.remove(msgIdStr);
					imageProgressBars.remove(msgIdStr);
				}
				
				try {
					ChatMsg cm = chatMsgDao.queryForId(msgId);
					if(cm!=null)
					{
						cm.setSendstate("ʧ��");
						chatMsgDao.update(cm);
						for(ChatMsg item:chatMsgList)
						{
							if(item.getId()==cm.getId())
							{
								item.setSendstate(cm.getSendstate());
								break;
							}
						}
						mAdapter.notifyDataSetChanged();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				break;
			case 6:
				result = msg.obj.toString();
				Log.d(TAG, "------->result:" + result);
				sendResult="";
				try {
					sendResult = new String(Base64.decode(result.getBytes("ISO-8859-1")));
					Log.d(TAG, "---------------->result:" + sendResult);
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				try {
					JSONObject jo1 = new JSONObject(sendResult);
					@SuppressWarnings("unchecked")
					Iterator<String> keyIter=jo1.keys();
					boolean flag=false;
					while (keyIter.hasNext()) 
					{ 

				        String key = (String) keyIter.next(); 
				        String value = (String) jo1.get(key); 
				        if(value.equals("�ɹ�"))
				        	value="�ʹ�";
						for(ChatMsg item:chatMsgList)
						{
							if(item.getMsg_id()!=null)
							{
								if(item.getToid().split(",").length>1 && value.equals("�Ѷ�"))
								{
									ChatMsgDetail detail=chatMsgDetailDao.queryBuilder().where().eq("mainid", item.getId()).and().eq("msg_id", key).queryForFirst();
									if(detail!=null)
									{
										detail.setSendstate(value);
										chatMsgDetailDao.update(detail);
										detail=chatMsgDetailDao.queryBuilder().where().eq("mainid", item.getId()).and().eq("sendstate", "�ʹ�").queryForFirst();
										if(detail==null)
										{
											item.setSendstate("�Ѷ�");
											chatMsgDao.update(item);
											flag=true;
										}
										break;
									}
									
								}
								else
								{
									if(item.getMsg_id().equals(key))
									{
										if(!item.getSendstate().equals(value))
										{
											item.setSendstate(value);
											chatMsgDao.update(item);
											flag=true;
										}
										break;
									}
								}
								
							}
						}
					    

					}
					if(flag)
						mAdapter.notifyDataSetChanged();
					
					
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				break;
			}
		}
	};

	public class InteractAdapter extends BaseAdapter {

		private List<ChatMsg> coll;

		private Context ctx;

		private LayoutInflater mInflater;

		public InteractAdapter(Context context, List<ChatMsg> coll) {
			ctx = context;
			this.coll = coll;
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return coll.size();
		}

		public Object getItem(int position) {
			return coll.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public int getItemViewType(int position) {
			ChatMsg entity = coll.get(position);

			if (entity.getMsgFlag() == 0) {
				return IMsgViewType.IMVT_COM_MSG;
			} else {
				return IMsgViewType.IMVT_TO_MSG;
			}

		}

		public int getViewTypeCount() {
			return 2;
		}

		@SuppressLint("DefaultLocale")
		public View getView(int position, View convertView, ViewGroup parent) {

			final ChatMsg entity = coll.get(position);
			Log.d(TAG,"----->entity.getMsgFlag():"
					+ entity.getMsgFlag());
			Log.d(TAG,"----->entity.getContent():"
					+ entity.getContent());
			ViewHolder viewHolder=null;
			if (convertView == null) {
				if (entity.getMsgFlag() == 0) {
					convertView = mInflater.inflate(
							R.layout.view_list_chat_left, null);
				} else {
					convertView = mInflater.inflate(
							R.layout.view_list_chat_right, null);
				}

				viewHolder = new ViewHolder();
				viewHolder.tvSendTime = (TextView) convertView
						.findViewById(R.id.chat_left_time);
				viewHolder.imgUser = (ImageView) convertView
						.findViewById(R.id.chat_left_photo);
				viewHolder.tvContent = (MyTextViewEx) convertView
						.findViewById(R.id.chat_left_tv);
				viewHolder.msgContent = (RelativeLayout) convertView
						.findViewById(R.id.msgContent);
				viewHolder.tvSendState = (TextView) convertView
						.findViewById(R.id.chat_send_state);
				viewHolder.toid_list = (PredicateLayout) convertView
						.findViewById(R.id.toid_list);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			aq = new AQuery(convertView);
			BitmapAjaxCallback cb = new BitmapAjaxCallback(){
		        @Override
		        public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
		            if(status.getCode()==200) {
		                super.callback(url, iv, bm,status);
		                mListView.setSelection(mListView.getCount());
		            } 
		        }            
		    };
		    String toidStr=entity.getToid();
		    String[] toidArray=toidStr.split(",");
		    if(toidArray.length>1 && viewHolder.toid_list!=null)
		    {
		    	List<String> ReadList=new ArrayList<String>();
		    	
		    	try {
		    		List<ChatMsgDetail> detailList=chatMsgDetailDao.queryBuilder().where().eq("mainid", entity.getId()).and().eq("sendstate", "�Ѷ�").query();
		    		for(ChatMsgDetail detail:detailList)
		    			ReadList.add(detail.getToid());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	viewHolder.toid_list.removeAllViews();
		    	for(int i=0;i<toidArray.length;i++)
		    	{
		    		String toid=toidArray[i];
		    		ContactsMember contactsMember;
		    		
		    		
		    		contactsMember=((CampusApplication)getApplicationContext()).getLinkManDic().get(toid);
		    		if(contactsMember!=null)
		    		{
		    		
		    			TextView btn=new TextView(ChatMsgActivity.this);
		    			if(!ReadList.contains(contactsMember.getUserNumber()))
		    				btn.setBackgroundResource(R.drawable.button_round_corner_blue);
		    			
		    			btn.setTextColor(getResources().getColorStateList(R.color.test_answer_selector));
		    			btn.setText(contactsMember.getName());
		    			btn.setClickable(true);
		    			btn.setFocusable(true);
		    			btn.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size_micro));
		    			
		    			btn.setPadding(5, 5, 5, 5);
		    			ViewGroup.LayoutParams lp=new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		    			
		    			btn.setLayoutParams(lp);
		    			btn.setTag(contactsMember.getUserNumber());
		    			btn.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								
								openLinkmanInfo((String)v.getTag(),0);
							}
		    				
		    			});
		    			viewHolder.toid_list.addView(btn);
		    			
		    		}
		    		
		    	}
		    
		    }
		    
			String lasttime = DateHelper.getDateString(entity.getTime(), "yyyy-MM-dd");
			String nowdate = DateHelper.getDateString(new Date(), "yyyy-MM-dd");
			if (nowdate.equals(lasttime)) {
				viewHolder.tvSendTime.setText(DateHelper.getDateString(entity.getTime(), "HH:mm"));
			}else{
				viewHolder.tvSendTime.setText(DateHelper.getDateString(entity.getTime(), "MM-dd HH:mm"));
			}
			viewHolder.msgContent.removeAllViews();
			if ("img".equals(entity.getType())) {
				viewHolder.msgContent.removeView(viewHolder.tvContent);
				View convertImageView = mInflater.inflate(R.layout.view_list_chat_image, null);
				viewHolder.msgContent.addView(convertImageView);
				viewHolder.showImage = (ImageView) convertImageView.findViewById(R.id.showImage);
				viewHolder.progressBarImage = (ProgressBar) convertImageView.findViewById(R.id.progressBarImage);
				
				String path = entity.getContent();
				if (AppUtility.isNotEmpty(path)) {
					viewHolder.showImage.setTag(path);
					if (entity.getMsgFlag()==1) {
						Bitmap bitmap = ImageUtility.getDiskBitmapByPath(path);
						if (bitmap != null) {
							Bitmap zoombitmap = ImageUtility.zoomBitmap(bitmap, 200);
							aq.id(viewHolder.showImage).image(zoombitmap);
						}
						else
						{
							path=entity.getRemoteimage();
							aq.id(viewHolder.showImage).progress(viewHolder.progressBarImage).image(path,true,true,200,0);
						}
							
						if (msgIds.size() > 0 && msgIds.contains(String.valueOf(entity.getId()))) {
							ProgressBar pb = new ProgressBar(ChatMsgActivity.this);
							pb.setPadding(20, 60, 20, 20);
							FrameLayout frameLayout = (FrameLayout) viewHolder.showImage.getParent();
							frameLayout.addView(pb);
							imageProgressBars.put(String.valueOf(entity.getId()), pb);
						}
						
					}else{
						Log.d(TAG, "------------path"+path);
						Bitmap bitmap = aq.getCachedImage(path);
						if (bitmap != null) {
							Bitmap zoombitmap = ImageUtility.zoomBitmap(bitmap, 200);
							aq.id(viewHolder.showImage).image(zoombitmap);
						}
						else
							aq.id(viewHolder.showImage).progress(viewHolder.progressBarImage).image(path,true,true,200,0,cb);
					}
				}
			}else{
				viewHolder.msgContent.addView(viewHolder.tvContent);
				// ��ʾ�������ı���Ϣ
				/*
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(ctx, entity.getContent());
				viewHolder.tvContent.setText("");
				viewHolder.tvContent.append(spannableString);
				*/
				viewHolder.tvContent.setText("");
				viewHolder.tvContent.insertGif(entity.getContent());
			}
			
			if (entity.getMsgFlag()==1) {
				if(AppUtility.isNotEmpty(entity.getSendstate()))
				{
					viewHolder.tvSendState.setVisibility(View.VISIBLE);
					String state[]=getResources().getStringArray(R.array.send_status);
					ArrayList<String> example=new ArrayList<String>();
					example.add("�ʹ�");
					example.add("�Ѷ�");
					example.add("ʧ��");
					int index=example.indexOf(entity.getSendstate());
					viewHolder.tvSendState.setText(state[index]);
					if(entity.getSendstate().equals("�ʹ�"))
						viewHolder.tvSendState.setBackgroundResource(R.drawable.chat_sendstate_bg_hassend);
					else if(entity.getSendstate().equals("�Ѷ�"))
						viewHolder.tvSendState.setBackgroundResource(R.drawable.chat_sendstate_bg_hasread);
					else if(entity.getSendstate().equals("ʧ��"))
					{
						viewHolder.tvSendState.setBackgroundResource(R.drawable.chat_sendstate_bg_fail);
						viewHolder.tvSendState.setTag(entity);
					}
						
				}
				else
					viewHolder.tvSendState.setVisibility(View.INVISIBLE);
			}
			String path = "";
			if (entity.getMsgFlag() == 0) {
				path = userImage;
				viewHolder.imgUser.setTag(toid);
			} else {
				path = myHeadPic;
				viewHolder.imgUser.setTag(user.getUserNumber());
			}
			Log.d(TAG, "------------------------->path:"+path);
//			ImageOptions options = new ImageOptions();
//		    options.round = 25;
//		    if(AppUtility.isNotEmpty(path)){
//		    	aq.id(viewHolder.imgUser).image(path,options);
//		    }
			Bitmap bitmap = aq.getCachedImage(path);
			viewHolder.imgUser.setImageBitmap(null);
			
			if (bitmap != null) {
				aq.id(viewHolder.imgUser).image(bitmap);
				Log.d(TAG, "----------------------");
			}else{
				aq.id(viewHolder.imgUser).image(path,true,true);
				Log.d(TAG, "======================");
			}
		    
			viewHolder.imgUser.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					openLinkmanInfo((String)v.getTag(),entity.getMsgFlag());
				}
					
			});
			if(viewHolder.tvSendState!=null)
			{
				viewHolder.tvSendState.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						TextView tv=(TextView)v;
						if(tv.getText().equals("ʧ��"))
						{
							
							ChatMsg cm=(ChatMsg)tv.getTag();
							if(cm!=null)
							{
								AppUtility.showToastMsg(ChatMsgActivity.this, getString(R.string.tryresend));
								if(cm.getType().equals("img"))
								{
									Bitmap disckBitmap = null;
									try {
										File file = new File(cm.getContent());
										if (file.exists()) {
											disckBitmap = BitmapFactory.decodeFile(cm.getContent());
											byte[] bytes = ImageUtility.BitmapToBytes(disckBitmap);
											cm.setContent(Base64.encode(bytes));
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								
								String datetime = String.valueOf(new Date().getTime());
								sendSMS(cm.getContent(), toid, datetime,cm.getType(),cm.getId(),cm.getType());
							}
						}
					}
				});
			}
			return convertView;
		}

		public class ViewHolder {
			public TextView tvSendTime;
			public ImageView imgUser,showImage;
			public MyTextViewEx tvContent;
			public ProgressBar progressBarImage;
			public RelativeLayout msgContent;
			public TextView tvSendState;
			public PredicateLayout toid_list;
			public InnerScrollView toid_scroll;
		}

	}
	
	private void openLinkmanInfo(String toid,int flag)
	{

			Intent intent = new Intent(ChatMsgActivity.this,
					ShowPersonInfo.class);
			if (flag== 0) {
				intent.putExtra("studentId", toid);
				intent.putExtra("userImage", userImage);
			} else {
				intent.putExtra("studentId", user.getUserNumber());
				String myPic = user.getUserImage();
				intent.putExtra("userImage", myPic);
			}
			startActivity(intent);
		
	}
	
	public interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		}
		return database;
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_NAME)) {
				Log.d(TAG, "----------->BroadcastReceiver��" + ACTION_NAME);
				initData();
			}
		}
	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_NAME);
		// ע��㲥
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	@Override
	protected void onDestroy() {
		if (timer != null) {
			timer.cancel( );
			timer = null;
		}
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
		
		
	}
	/**
	 * ��ʾ����Ի���
	 * 
	 * @param view
	 */
	@SuppressWarnings("deprecation")
	public void showExpressionWindow(View v) {
		Log.d("showExpressionWindow", "ѡ�����");
		// �ж�������Ƿ��
		if (viewpager_layout.getVisibility() == View.VISIBLE) {
			viewpager_layout.setVisibility(View.GONE);
		} else {
			this.hideOrShowSoftinput(v);
			// ��ʾ����Ի���
			viewpager_layout.setVisibility(View.VISIBLE);
		}
		// ��ȡ��Ļ��ǰ�ֱ���
		Display currDisplay = getWindowManager().getDefaultDisplay();
		int displayWidth = currDisplay.getWidth();
		// ��ñ���ͼƬ�Ŀ��/�߶�
		Bitmap express = BitmapFactory.decodeResource(getResources(),
				R.drawable.f000);
		int headWidth = express.getWidth();
		int headHeight = express.getHeight();
		Log.d("showExpressionWindow", displayWidth + ":" + headWidth);

		final int colmns = displayWidth / headWidth > 7 ? 7 : displayWidth
				/ headWidth; // ÿҳ��ʾ������
		final int rows = 230 / headHeight > 4 ? 4 : 230 / headHeight; // ÿҳ��ʾ������
		final int pageItemCount = colmns * rows; // ÿҳ��ʾ����Ŀ��
		// ������ҳ��
		int totalPage = Constants.express_counts % pageItemCount == 0 ? Constants.express_counts
				/ pageItemCount
				: Constants.express_counts / pageItemCount + 1;

		final List<View> listView = new ArrayList<View>();
		for (int index = 0; index < totalPage; index++) {
			listView.add(getViewPagerItem(index, colmns, pageItemCount));
		}
		express_spot_layout.removeAllViews();
		for (int i = 0; i < totalPage; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setId(i + 1);
			if (i == 0) {
				imageView.setBackgroundResource(R.drawable.d2);
			} else {
				imageView.setBackgroundResource(R.drawable.d1);
			}
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			layoutParams.bottomMargin = 20;
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
			if (i != 0) {
				layoutParams.addRule(RelativeLayout.ALIGN_TOP, i);
				layoutParams.addRule(RelativeLayout.RIGHT_OF, i);
			}
			express_spot_layout.addView(imageView, layoutParams);
		}
		Log.d("showExpressionWindow", express_spot_layout.getChildCount() + "");
		// ���viewPager��������
		viewPager.setAdapter(new PagerAdapter() {
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			public int getCount() {
				return listView.size();
			}

			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(listView.get(position));
			}

			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(listView.get(position));
				return listView.get(position);
			}
		});
		// ע�������
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

	/**
	 * ���������
	 * 
	 * @param view
	 */
	public void hideOrShowSoftinput(View view) {
		InputMethodManager manager = (InputMethodManager) this
				.getSystemService(Service.INPUT_METHOD_SERVICE);
		if (manager.isActive()) {
			manager.hideSoftInputFromWindow(mEditTextContent.getWindowToken(),
					0);
			viewpager_layout.setVisibility(View.GONE);
		} else {
			manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * ��������:���ر���ͼƬ
	 * 
	 * @author shengguo 2013-12-26 ����11:55:01
	 * 
	 * @param index
	 * @param colums
	 * @param pageItemCount
	 * @return
	 */
	private View getViewPagerItem(final int index, int colums,
			final int pageItemCount) {
		LayoutInflater inflater = this.getLayoutInflater();
		View express_view = inflater.inflate(R.layout.express_gv, null);
		GridView gridView = (GridView) express_view
				.findViewById(R.id.gv_express);
		gridView.setNumColumns(colums);
		gridView.setAdapter(new ExpressionGvAdapter(index, pageItemCount,
				imageIds, inflater));
		// ע������¼�
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int positon, long id) {
				Log.d("getViewPagerItem", "imageIds[positon]"
						+ imageIds[positon]);
				
				Bitmap bitmap = null;
				int start = index * pageItemCount; // ��ʼλ��
				positon = positon + start;
				bitmap = BitmapFactory.decodeResource(getResources(),
						imageIds[positon]);
				ImageSpan imageSpan = new ImageSpan(ChatMsgActivity.this,
						bitmap, ImageSpan.ALIGN_BOTTOM);
				
				
				String str = "";
				if (positon < 10) {
					str = "[f00" + positon + "]";
				} else if (positon < 100) {
					str = "[f0" + positon + "]";
				} else {
					str = "[f" + positon + "]";
				}
				SpannableString spannableString = new SpannableString(str);
				spannableString.setSpan(imageSpan, 0, str.length(),
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				mEditTextContent.append(spannableString);
			}

		});
		return express_view;
	}

	/**
	 * 
	 * #(c) ruanyun PocketCampus <br/>
	 * 
	 * �汾˵��: $id:$ <br/>
	 * 
	 * ����˵��: ����viewpager�����¼�
	 * 
	 * <br/>
	 * ����˵��: 2013-12-26 ����1:17:11 shengguo �����ļ�<br/>
	 * 
	 * �޸���ʷ:<br/>
	 * 
	 */
	private final class MyPageChangeListener implements OnPageChangeListener {
		private int curIndex = 0;

		public void onPageSelected(int index) {
			express_spot_layout.getChildAt(curIndex).setBackgroundResource(
					R.drawable.d1);
			express_spot_layout.getChildAt(index).setBackgroundResource(
					R.drawable.d2);
			curIndex = index;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Message msg = new Message();
			msg.what = 4;
			switch (requestCode) {

			// ����������ص�
			case PIC_REQUEST_CODE_SELECT_CAMERA:
				// �ϴ�ͷ��
				msg.obj = myPicPath;
				mHandler.sendMessage(msg);
				break;
			case PIC_Select_CODE_ImageFromLoacal://���ļ���ȡͼƬ
				Uri uri = Uri.parse(data.getData().toString());
				ContentResolver cr = this.getContentResolver();
	        	Cursor cursor = cr.query(uri, null, null, null, null);
	        	cursor.moveToFirst();
	        	String path = cursor.getString(1);
	        	
	        	
				String tempPath =FileUtility.getRandomSDFileName("jpg");
				if(FileUtility.copyFile(path,tempPath))
				{
					msg.obj = tempPath;
					mHandler.sendMessage(msg);
				}
				else
					AppUtility.showErrorToast(this, getString(R.string.failedcopytosdcard));
				
				
				break;
			}
		}
	}
	
	//�ϴ�ͷ��
	public void sendImage(String path,int msgidInt){
		Bitmap disckBitmap = null;
		try {
			File file = new File(path);
			if (file.exists()) {
				disckBitmap = BitmapFactory.decodeFile(path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		int width = disckBitmap.getWidth(); 
		int w=width; //�ж��Ƿ���Ҫѹ��
		if (width > 600) {
			w = 600;
			disckBitmap = ImageUtility.zoomBitmap(disckBitmap, w);
		}
		*/
//		Bitmap zoomBitmap = ImageUtility.zoomBitmap(disckBitmap, w);
		byte[] bytes = ImageUtility.BitmapToBytes(disckBitmap);
		String content = Base64.encode(bytes);
		Bundle bundle = new Bundle();
		bundle.putString("content", content);
		bundle.putString("type", "img");
		bundle.putString("path", path);
		bundle.putInt("msgidInt", msgidInt);
		Message msg = new Message();
		msg.what = 2;
		msg.obj = bundle;
		mHandler.sendMessage(msg);
//		send(content,"img",path);
		
	}
	
	public void showImage(View v){
		/*
		if(v.getTag() != null){
			String imageUrl = v.getTag().toString();
			DialogFragment newFragment = ShowImageDialogFragment
					.newInstance(imageUrl);

			newFragment.show(getSupportFragmentManager(), "dialog");
		}
		*/
		Intent intent = new Intent(this,
				ImagesActivity.class);
		ArrayList<String> picturePaths=new ArrayList<String>();
		for(ChatMsg item:chatMsgList)
		{
			if(item.getType().equals("img"))
			{
				picturePaths.add(item.getContent());
			}
		}
		String imageUrl = v.getTag().toString();
		intent.putStringArrayListExtra("pics",
				(ArrayList<String>) picturePaths);
		for (int i = 0; i < picturePaths.size(); i++) {
			if(picturePaths.get(i).equals(imageUrl)){
				intent.putExtra("position", i);
			}
		}
		startActivity(intent);
		
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onRefresh() {
		msgPager++;
		queryData(msgPager);
		mHandler.sendEmptyMessage(1);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
	}
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("myPicPath", myPicPath);
		
		
	}
	@Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        myPicPath=savedInstanceState.getString("myPicPath");
    }
}
