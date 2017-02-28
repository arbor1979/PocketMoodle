package com.dandian.pocketmoodle.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidquery.AQuery;
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
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * �汾˵��: $id:$ <br/>
 * 
 * ����˵��: ��Ϣ�б�����
 * 
 * <br/>
 * ����˵��: 2013-12-9 ����12:51:50 zhuliang �����ļ�<br/>
 * 
 * �޸���ʷ:<br/>
 * 
 */
public class HomeActivity extends Activity implements OnItemClickListener {
	private String TAG= "HomeActivity";
	private ListView mList;
	private ListAdapter mAdapter;
	static Button menu;
	private DatabaseHelper database;
	private String ACTION_NAME = "ChatInteract";
	public static boolean isruning = false;
	
	private ViewPager mViewPager;
	private JSONArray imageArray,excellentArray,popularArray,categoryArray;
	private GridView gv_popular,gv_excellent;
	private SamplePagerAdapter imagePageDapter;
	AQuery aq;
	private LinearLayout loadingLayout,leftlayout,failedLayout;
	private ScrollView contentLayout;
	private MyGridAdapter excellentAdapter,popularAdapter;
	private User user;
	private Button btnLeft;
	private RelativeLayout nav_bar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "----------------onCreate-----------------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		loadingLayout = (LinearLayout) findViewById(R.id.data_load);
		leftlayout=(LinearLayout) findViewById(R.id.layout_back);
		btnLeft = (Button) findViewById(R.id.btn_back);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setBackgroundResource(R.drawable.personalinfo);
		contentLayout = (ScrollView) findViewById(R.id.scrollView1);
		int color=PrefUtility.getInt(Constants.PREF_THEME_LISTCOLOR, 0);
		if(color!=0)
			contentLayout.setBackgroundColor(color);
		nav_bar=(RelativeLayout) findViewById(R.id.nav_bar);
		color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			nav_bar.setBackgroundColor(color);
		failedLayout = (LinearLayout) findViewById(R.id.empty_error);
		mViewPager = ((ViewPager) findViewById(R.id.zoom_imags));
		mList = (ListView) findViewById(R.id.lv_category);
		gv_excellent = (GridView) findViewById(R.id.gv_excellent);
		gv_popular = (GridView) findViewById(R.id.gv_popular);
		failedLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getHomeData();
			}
		});
		leftlayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this,
						ShowPersonInfo.class);
				intent.putExtra("studentId", user.getUserNumber());
				intent.putExtra("userImage", user.getUserImage());
				startActivity(intent);
			}
		});
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();
		registerBoradcastReceiver();
		getHomeData();
	}
	private void getHomeData()
	{
		showProgress(true);
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("�û�������", checkCode);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPostToDandian("getHomeData", jo, mHandler, 0);

	}
	private void showFetchFailedView() {
		loadingLayout.setVisibility(View.GONE);
		contentLayout.setVisibility(View.GONE);
		failedLayout.setVisibility(View.VISIBLE);
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
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				showFetchFailedView();
				AppUtility.showErrorToast(HomeActivity.this,
						msg.obj.toString());
				break;
			case 0:
				showProgress(false);
				String result = msg.obj.toString();
				try 
				{
					JSONObject jo = new JSONObject(result);
					if(jo.optString("���").equals("ʧ��"))
						AppUtility.showErrorToast(HomeActivity.this,
								jo.optString("error"));
					else
					{
						imageArray=jo.optJSONArray("����ͼƬ");
						excellentArray=jo.optJSONArray("��Ʒ�γ�");
						popularArray=jo.optJSONArray("���ſγ�");
						categoryArray=jo.optJSONArray("�γ����");
						initContent();
					}
				}
				catch (Exception e) {
					
					AppUtility.showErrorToast(HomeActivity.this,
							e.getLocalizedMessage());
				}
				break;
			}
		}
	};
	private void initContent() {
		imagePageDapter = new SamplePagerAdapter(this);
		mViewPager.setAdapter(imagePageDapter);
		excellentAdapter= new MyGridAdapter(this,excellentArray);
		gv_excellent.setAdapter(excellentAdapter);
		popularAdapter= new MyGridAdapter(this,popularArray);
		gv_popular.setAdapter(popularAdapter);
		mAdapter=new ListAdapter(this,categoryArray);
		mList.setAdapter(mAdapter);
	}
	
	@SuppressLint("ResourceAsColor")
	class SamplePagerAdapter extends PagerAdapter {
		
		private Context context;
		public SamplePagerAdapter(Context context) {
			this.context=context;
		}

		public int getCount() {
			return imageArray.length();
		}

		public int getItemPosition() {
			return POSITION_NONE;
		}

		public View instantiateItem(ViewGroup container, final int position) {
			ImageView iv_image=new ImageView(context);
			
			JSONObject imageObj = null;
			try {
				imageObj = (JSONObject) imageArray.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final String imageUrl = imageObj.optString("ͼƬ��ַ");
			aq.id(iv_image).image(imageUrl, true, true, 0, R.drawable.default_photo);
			
			iv_image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});

			container.addView(iv_image, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

			return iv_image;
		}

		public void destroyItem(ViewGroup paramViewGroup, int position,
				Object object) {
			Log.d(TAG, "destroyItem-------position" + position);
			paramViewGroup.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}
	
	class MyGridAdapter extends BaseAdapter{ 
        //�����Ķ��� 
        private Context context; 
        private JSONArray courseArray; 
        MyGridAdapter(Context context,JSONArray courseArray){ 
            this.context = context; 
            this.courseArray=courseArray;
        } 
        public int getCount() { 
            return courseArray.length(); 
        } 
 
        public Object getItem(int item) { 
            return item; 
        } 
 
        public long getItemId(int id) { 
            return id; 
        } 
         
        //����View���� 
        public View getView(int position, View convertView, ViewGroup parent) { 
        	LayoutInflater inflater = LayoutInflater.from(context);
        	View view = inflater.inflate(R.layout.grid_item_course, null);
			final ImageView zoomImageView = (ImageView) view
					.findViewById(R.id.iv_course);
			final TextView tv_courseName = (TextView) view
					.findViewById(R.id.tv_courseName);
			final TextView tv_teacherName = (TextView) view
					.findViewById(R.id.tv_teacherName);
			final TextView tv_userNum = (TextView) view
					.findViewById(R.id.tv_userNum);
			JSONObject courseObj = null;
			try {
				courseObj = (JSONObject) imageArray.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			final String imageUrl = courseObj.optString("ͼƬ��ַ");
			aq.id(zoomImageView).image(imageUrl, true, true, 0, R.drawable.default_photo);
			tv_courseName.setText(courseObj.optString("�γ�����"));
			tv_teacherName.setText(courseObj.optString("��ʦ����"));
			tv_userNum.setText(courseObj.optString("ѡ���û���"));
			view.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
				
			});

			return view;
        } 
} 
	/**
	 * 
	 * #(c) ruanyun PocketCampus <br/>
	 * 
	 * �汾˵��: $id:$ <br/>
	 * 
	 * ����˵��: ��Ϣlist������
	 * 
	 * <br/>
	 * ����˵��: 2013-12-9 ����1:10:31 zhuliang �����ļ�<br/>
	 * 
	 * �޸���ʷ:<br/>
	 * 
	 */
	class ListAdapter extends BaseAdapter {
		private JSONArray list = new JSONArray();
		private Context context;
		private LayoutInflater mInflater;

		public ListAdapter(Context context, JSONArray list) {
			this.context = context;
			this.list = list;
			this.mInflater = LayoutInflater.from(this.context);
		}

		@Override
		public int getCount() {
			return list.length();
		}

		@Override
		public Object getItem(int position) {
		JSONObject item=null;
			try {
				item=(JSONObject) list.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return item;
			
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item_course_category, null);
				
				holder.name = (TextView) convertView.findViewById(R.id.tv_name);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final JSONObject cateObj = (JSONObject) getItem(position);
			holder.name.setText(cateObj.optString("�������"));
			
			/*
			if(chatFriend.getMsgType().equals("Ⱥ��Ϣ")){
				holder.photo.setImageResource(R.drawable.contacts_group);
			}*/
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					
					/*
						Intent intent = new Intent(HomeActivity.this,
								ShowPersonInfo.class);
						intent.putExtra("studentId", toid);
						intent.putExtra("userImage",chatFriend.getUserImage());
						startActivity(intent);
					*/
				}
				
			});
			return convertView;
		}
	}

	class ViewHolder {
		ImageView photo;
		TextView name;
		TextView content;
		TextView time;
		TextView unreadCnt;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		ChatFriend chatFriend = (ChatFriend) mList.getItemAtPosition(position);
		Intent intent = new Intent(this, ChatMsgActivity.class);
		intent.putExtra("toid", chatFriend.getToid());
		intent.putExtra("toname", chatFriend.getToname());
		intent.putExtra("type", chatFriend.getMsgType());
		intent.putExtra("userImage", chatFriend.getUserImage());
		startActivity(intent);
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
				Log.d(TAG,"----------->BroadcastReceiver��"
						+ ACTION_NAME);
				initContent();
			}
		}
	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_NAME);
		// ע��㲥
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	public void getLastChatMsg(final String toid, final int position) {
		JSONObject jo = new JSONObject();
		try {
			jo.put("�û�������", PrefUtility.get(Constants.PREF_CHECK_CODE, ""));
			jo.put("DATETIME", String.valueOf(new Date().getTime()));
			jo.put("TOID", toid);
			Log.d(TAG,"----------------->toid:" + toid);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG,"---------------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getLastChatMsg(params, new RequestListener() {

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
				bundle.putInt("position", position);
				bundle.putString("response", response);
				bundle.putString("toid", toid);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = bundle;
				mHandler.sendMessage(msg);
			}
		});
	}

	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
		
		
	}
}