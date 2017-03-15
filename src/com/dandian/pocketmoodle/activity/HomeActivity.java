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
import android.widget.ImageView.ScaleType;
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
import com.dandian.pocketmoodle.widget.NonScrollableGridView;
import com.dandian.pocketmoodle.widget.NonScrollableListView;
import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: 消息列表界面
 * 
 * <br/>
 * 创建说明: 2013-12-9 下午12:51:50 zhuliang 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
public class HomeActivity extends Activity {
	private String TAG= "HomeActivity";
	private NonScrollableListView mList;
	private ListAdapter mAdapter;
	static Button menu;
	private DatabaseHelper database;
	private String ACTION_NAME = "ChatInteract";
	public static boolean isruning = false;
	
	private ViewPager mViewPager;
	private JSONArray imageArray,recommendedArray,categoryArray;
	private JSONObject subCategoryArray;
	private NonScrollableGridView gv_excellent;
	private SamplePagerAdapter imagePageDapter;
	AQuery aq;
	private LinearLayout loadingLayout,leftlayout,failedLayout;
	private ScrollView contentLayout;
	private MyGridAdapter excellentAdapter,popularAdapter;
	private User user;
	private Button btnLeft;
	private RelativeLayout nav_bar;
	private final int MSG_UPDATE_IMAGE=1;
	private long MSG_DELAY=5000;
	private int currentItem;//当前viewpage选中页
	private boolean isAutoPlay = false;
	private ImageView tips[];//图片轮播下面的小点
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
		TextView tv_title=(TextView)findViewById(R.id.tv_title);
		tv_title.setText(R.string.school);
		failedLayout = (LinearLayout) findViewById(R.id.empty_error);
		mViewPager = ((ViewPager) findViewById(R.id.zoom_imags));
		mList = (NonScrollableListView) findViewById(R.id.lv_category);
		gv_excellent = (NonScrollableGridView) findViewById(R.id.gv_excellent);
		aq = new AQuery(this);
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
				intent.putExtra("studentId", user.getId());
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
			jo.put("用户较验码", checkCode);
			jo.put("function", "getHomeData");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPostToDandian(jo, mHandler, 0);

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
					if(jo.optString("结果").equals("失败"))
						AppUtility.showErrorToast(HomeActivity.this,
								jo.optString("error"));
					else
					{
						imageArray=jo.optJSONArray("标题图片");
						recommendedArray=jo.optJSONArray("推荐课程");
						categoryArray=jo.optJSONArray("课程大类");
						subCategoryArray=jo.optJSONObject("课程子类");
						initContent();
					}
				}
				catch (Exception e) {
					
					AppUtility.showErrorToast(HomeActivity.this,
							e.getLocalizedMessage());
				}
				break;
			case MSG_UPDATE_IMAGE:
				if(isAutoPlay)
				{
					currentItem++;
					if(currentItem==imagePageDapter.getCount())
	                	currentItem=0;
	                mViewPager.setCurrentItem(currentItem);
	                //准备下次播放
	                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
				}
                break;
			  
			}
		}
	};
	@SuppressWarnings("deprecation")
	private void initContent() {
		imagePageDapter = new SamplePagerAdapter(this);
		mViewPager.setAdapter(imagePageDapter);
		excellentAdapter= new MyGridAdapter(this);
		gv_excellent.setAdapter(excellentAdapter);
		mAdapter=new ListAdapter(this);
		mList.setAdapter(mAdapter);
		
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			  
            //配合Adapter的currentItem字段进行设置。
            @Override
            public void onPageSelected(int arg0) {
            	currentItem = arg0;
                setImageBackground(currentItem);
            }
              
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
              
            //覆写该方法实现轮播效果的暂停和恢复
            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                	isAutoPlay = false;  
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                	if(!isAutoPlay)
                	{
                		mHandler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                		isAutoPlay=true;
                	}
                    break;
                default:
                    break;
                }
            }
        });
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.viewGroup);
		tips = new ImageView[imageArray.length()];
		for(int i=0; i<imageArray.length(); i++){
			ImageView mImageView = new ImageView(this);
			tips[i] = mImageView;
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,    
                    LayoutParams.WRAP_CONTENT));  
			layoutParams.rightMargin = 3;
			layoutParams.leftMargin = 3;
			layoutParams.bottomMargin=15;
			mImageView.setBackgroundResource(R.drawable.page_indicator_unfocused);
			linearLayout.addView(mImageView, layoutParams);
		}
		setImageBackground(0);
		isAutoPlay=true;
		mHandler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
		
	}
	private void setImageBackground(int selectItems){  
        for(int i=0; i<tips.length; i++){  
            if(i == selectItems){  
            	tips[i].setBackgroundResource(R.drawable.page_indicator_focused);  
            }else{  
            	tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);  
            }  
        }  
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
			//iv_image.setAdjustViewBounds(true);
			iv_image.setScaleType(ScaleType.CENTER_CROP);
			JSONObject imageObj = null;
			try {
				imageObj = (JSONObject) imageArray.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final String imageUrl = imageObj.optString("image");
			final String link=imageObj.optString("link");
			aq.id(iv_image).image(imageUrl, true, true, 0, R.drawable.default_photo);
			
			iv_image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(link!=null && link.length()>0)
					{
						Intent aboutusIntent = new Intent(HomeActivity.this,WebSiteActivity.class);
						aboutusIntent.putExtra("url", link);
						startActivity(aboutusIntent);
					}
				}
			});

			container.addView(iv_image, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

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
        //上下文对象 
        private Context context; 
        MyGridAdapter(Context context){ 
            this.context = context; 
            
        } 
        public int getCount() { 
            return recommendedArray.length(); 
        } 
 
        public Object getItem(int item) { 
            return item; 
        } 
 
        public long getItemId(int id) { 
            return id; 
        } 
         
        //创建View方法 
        public View getView(int position, View convertView, ViewGroup parent) { 
        	LayoutInflater inflater = LayoutInflater.from(context);
        	View view = inflater.inflate(R.layout.grid_item_course, null);
			final ImageView zoomImageView = (ImageView) view
					.findViewById(R.id.iv_course);
			final TextView tv_courseName = (TextView) view
					.findViewById(R.id.tv_courseName);
			final TextView tv_teacherName = (TextView) view
					.findViewById(R.id.tv_teacherName);
			JSONObject courseObj = null;
			try {
				courseObj = (JSONObject) recommendedArray.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			final String imageUrl = courseObj.optString("图片地址");
			aq.id(zoomImageView).image(imageUrl, true, true, 0, R.drawable.default_photo);
			tv_courseName.setText(courseObj.optString("课程名称"));
			tv_teacherName.setText(courseObj.optString("教师名称"));
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
	 * 版本说明: $id:$ <br/>
	 * 
	 * 功能说明: 消息list适配器
	 * 
	 * <br/>
	 * 创建说明: 2013-12-9 下午1:10:31 zhuliang 创建文件<br/>
	 * 
	 * 修改历史:<br/>
	 * 
	 */
	class ListAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater mInflater;

		public ListAdapter(Context context) {
			this.context = context;
			this.mInflater = LayoutInflater.from(this.context);
		}

		@Override
		public int getCount() {
			return categoryArray.length();
		}

		@Override
		public Object getItem(int position) {
		JSONObject item=null;
			try {
				item=(JSONObject) categoryArray.get(position);
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
			holder.name.setText(cateObj.optString("name"));
			
			/*
			if(chatFriend.getMsgType().equals("群消息")){
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
				Log.d(TAG,"----------->BroadcastReceiver："
						+ ACTION_NAME);
				initContent();
			}
		}
	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_NAME);
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	public void getLastChatMsg(final String toid, final int position) {
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", PrefUtility.get(Constants.PREF_CHECK_CODE, ""));
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
