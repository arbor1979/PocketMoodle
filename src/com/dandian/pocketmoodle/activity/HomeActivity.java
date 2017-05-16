package com.dandian.pocketmoodle.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.dandian.pocketmoodle.CampusApplication;
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.entity.User;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.dandian.pocketmoodle.widget.NonScrollableGridView;
import com.dandian.pocketmoodle.widget.NonScrollableListView;

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
	public static boolean isruning = false;
	
	private ViewPager mViewPager;
	private JSONArray imageArray,recommendedArray,categoryArray;
	private JSONObject subCategoryArray;
	private NonScrollableGridView gv_excellent;
	private SamplePagerAdapter imagePageDapter;
	AQuery aq;
	private LinearLayout loadingLayout,leftlayout,failedLayout;
	private ScrollView contentLayout;
	private MyGridAdapter excellentAdapter;
	private User user;
	private Button btnLeft;
	private RelativeLayout nav_bar;
	private final int MSG_UPDATE_IMAGE=10;
	private long MSG_DELAY=5000;
	private int currentItem;//当前viewpage选中页
	private boolean isAutoPlay = false;
	private ImageView tips[];//图片轮播下面的小点
	private TextView tv_excellent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "----------------onCreate-----------------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		loadingLayout = (LinearLayout) findViewById(R.id.data_load);
		tv_excellent=(TextView) findViewById(R.id.tv_excellent);
		tv_excellent.setVisibility(View.GONE);
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
				getHomeData("标题图片",0);
				getHomeData("推荐课程",1);
				getHomeData("课程类别",2);
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
		getHomeData("标题图片",0);
		getHomeData("推荐课程",1);
		getHomeData("课程类别",2);
	}
	private void getHomeData(String $action,int handleId)
	{
		showProgress(true);
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("action", $action);
			jo.put("function", "getHomeData");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPostToDandian(jo, mHandler, handleId);

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
						initContent(msg.what);
					}
				}
				catch (Exception e) {
					
					AppUtility.showErrorToast(HomeActivity.this,
							e.getLocalizedMessage());
				}
				break;
			case 1:
				showProgress(false);
				result = msg.obj.toString();
				try 
				{
					JSONObject jo = new JSONObject(result);
					if(jo.optString("结果").equals("失败"))
						AppUtility.showErrorToast(HomeActivity.this,
								jo.optString("error"));
					else
					{
						recommendedArray=jo.optJSONArray("推荐课程");
						initContent(msg.what);
					}
				}
				catch (Exception e) {
					
					AppUtility.showErrorToast(HomeActivity.this,
							e.getLocalizedMessage());
				}
				break;
			case 2:
				showProgress(false);
				result = msg.obj.toString();
				try 
				{
					JSONObject jo = new JSONObject(result);
					if(jo.optString("结果").equals("失败"))
						AppUtility.showErrorToast(HomeActivity.this,
								jo.optString("error"));
					else
					{
						categoryArray=jo.optJSONArray("课程大类");
						subCategoryArray=jo.optJSONObject("课程子类");
						if(subCategoryArray==null)
							subCategoryArray=new JSONObject();
						initContent(msg.what);
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
	private void initContent(int actionid) {
		if(actionid==0)
		{
			imagePageDapter = new SamplePagerAdapter(this);
			mViewPager.setAdapter(imagePageDapter);
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
		else if(actionid==1)
		{
			tv_excellent.setVisibility(View.VISIBLE);
			excellentAdapter= new MyGridAdapter(this);
			gv_excellent.setAdapter(excellentAdapter);
			
		}
		else if(actionid==2)
		{
			if(categoryArray!=null)
			{
				mAdapter=new ListAdapter(this);
				mList.setAdapter(mAdapter);
			}
		}
		contentLayout.scrollTo(0, 0);

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
			ImageView zoomImageView = (ImageView) view
					.findViewById(R.id.iv_course);
			TextView tv_courseName = (TextView) view
					.findViewById(R.id.tv_courseName);
			TextView tv_teacherName = (TextView) view
					.findViewById(R.id.tv_teacherName);
			JSONObject courseObj = null;
			try {
				courseObj = (JSONObject) recommendedArray.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String imageUrl = courseObj.optString("图片地址");
			aq.id(zoomImageView).image(imageUrl, true, true, 0, R.drawable.default_photo);
			tv_courseName.setText(courseObj.optString("课程名称"));
			tv_teacherName.setText(courseObj.optString("教师名称"));
			view.setTag(courseObj);
			view.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					JSONObject courseObj=(JSONObject) v.getTag();
					Intent intent=new Intent(HomeActivity.this,SchoolDetailActivity.class);
					intent.putExtra("templateName", "博客");
					intent.putExtra("interfaceName",courseObj.optString("DetailUrl"));
					intent.putExtra("title", courseObj.optString("课程名称"));
					intent.putExtra("display", getString(R.string.course_summary));
					startActivity(intent);
				}
				
			});

			return view;
        } 
	} 

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
				holder.count = (TextView) convertView.findViewById(R.id.tv_right);
				holder.subGates = (NonScrollableGridView) convertView.findViewById(R.id.gv_subGates);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final JSONObject cateObj = (JSONObject) getItem(position);
			holder.name.setText(cateObj.optString("name"));
			holder.count.setText(cateObj.optString("coursecount"));
			JSONArray subCatesArray=subCategoryArray.optJSONArray(cateObj.optString("name"));
			
			if(subCatesArray!=null && subCatesArray.length()>0)
			{
				holder.subGates.setVisibility(View.VISIBLE);
				final ArrayList<HashMap<String,Object>> data_list = new ArrayList<HashMap<String,Object>>();
				for(int i=0;i<subCatesArray.length();i++)
				{
					JSONObject item=null;
					try {
						item = subCatesArray.getJSONObject(i);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(item!=null)
					{
						HashMap<String,Object> map1 = new HashMap<String,Object>();
						map1.put("name", item.optString("name"));
						map1.put("id", item.optString("id"));
						data_list.add(map1);
					}
				}
				if(data_list.size()%2==1)
				{
					HashMap<String,Object> map1 = new HashMap<String,Object>();
					map1.put("name", "");
					map1.put("id", "");
					data_list.add(map1);
				}
				SimpleAdapter sim_adapter = new SimpleAdapter(context, data_list, R.layout.grid_item, new String[] {"name"}, new int[]{R.id.item_textView});
				holder.subGates.setAdapter(sim_adapter);
				holder.subGates.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						HashMap<String,Object> map1=data_list.get(position);
						if(map1.get("id").toString().length()>0)
						{
							Intent intent = new Intent(HomeActivity.this,
									CoursesSearchActivity.class);
							intent.putExtra("cateId", map1.get("id").toString());
							startActivity(intent);
						}
					}
					
				});
			
			}
			else
				holder.subGates.setVisibility(View.GONE);
			/*
			if(chatFriend.getMsgType().equals("群消息")){
				holder.photo.setImageResource(R.drawable.contacts_group);
			}*/
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					
						Intent intent = new Intent(HomeActivity.this,
								CoursesSearchActivity.class);
						intent.putExtra("cateId", cateObj.optString("id"));
						startActivity(intent);
					
				}
				
			});
			return convertView;
		}
	}

	class ViewHolder {
		ImageView photo;
		TextView name;
		TextView count;
		NonScrollableGridView subGates;
	}

	
}
