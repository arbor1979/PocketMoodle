package com.dandian.pocketmoodle.activity;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.dandian.pocketmoodle.CampusApplication;
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.entity.User;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.PrefUtility;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * �汾˵��: $id:$ <br/>
 * 
 * ����˵��: ��Ϣ�б����
 * 
 * <br/>
 * ����˵��: 2013-12-9 ����12:51:50 zhuliang �����ļ�<br/>
 * 
 * �޸���ʷ:<br/>
 * 
 */
public class MyCourseActivity extends Activity {
	private String TAG= "HomeActivity";
	private ListView mList;
	private ListAdapter mAdapter;
	static Button menu;
	public static boolean isruning = false;
	
	private JSONArray myCourseArray;
	AQuery aq;
	private LinearLayout loadingLayout,leftlayout,failedLayout;
	//private ScrollView contentLayout;
	private User user;
	private Button btnLeft;
	private RelativeLayout nav_bar;
	//private MaterialCalendarView calendarView1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "----------------onCreate-----------------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycourse);
		loadingLayout = (LinearLayout) findViewById(R.id.data_load);
		leftlayout=(LinearLayout) findViewById(R.id.layout_back);
		btnLeft = (Button) findViewById(R.id.btn_back);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setBackgroundResource(R.drawable.personalinfo);
		mList = (ListView) findViewById(R.id.lv_category);
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();
		int color=PrefUtility.getInt(Constants.PREF_THEME_LISTCOLOR, 0);
		if(color!=0)
			mList.setBackgroundColor(color);
		nav_bar=(RelativeLayout) findViewById(R.id.nav_bar);
		color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			nav_bar.setBackgroundColor(color);
		TextView tv_title=(TextView)findViewById(R.id.tv_title);
		tv_title.setText(R.string.mycourse);
		failedLayout = (LinearLayout) findViewById(R.id.empty_error);
		//calendarView1= (MaterialCalendarView) findViewById(R.id.calendarView1);
		//mList = (NonScrollableListView) findViewById(R.id.lv_category);
		
		aq = new AQuery(this);
		failedLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getMyCourseData();
			}
		});
		leftlayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyCourseActivity.this,
						ShowPersonInfo.class);
				intent.putExtra("studentId", user.getId());
				intent.putExtra("userImage", user.getUserImage());
				startActivity(intent);
			}
		});
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();
		myCourseArray=new JSONArray();
		mAdapter=new ListAdapter(this);
		mList.setAdapter(mAdapter);
		getMyCourseData();
	}
	private void getMyCourseData()
	{
		showProgress(true);
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("�û�������", checkCode);
			jo.put("function", "getMyCourseData");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPostToDandian(jo, mHandler, 0);

	}
	private void showFetchFailedView() {
		loadingLayout.setVisibility(View.GONE);
		mList.setVisibility(View.GONE);
		failedLayout.setVisibility(View.VISIBLE);
	}

	private void showProgress(boolean progress) {
		if (progress) {
			loadingLayout.setVisibility(View.VISIBLE);
			mList.setVisibility(View.GONE);
			failedLayout.setVisibility(View.GONE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			mList.setVisibility(View.VISIBLE);
			failedLayout.setVisibility(View.GONE);
		}
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				showFetchFailedView();
				AppUtility.showErrorToast(MyCourseActivity.this,
						msg.obj.toString());
				break;
			case 0:
				showProgress(false);
				String result = msg.obj.toString();
				try 
				{
					JSONObject jo = new JSONObject(result);
					if(jo.optString("���").equals("ʧ��"))
						AppUtility.showErrorToast(MyCourseActivity.this,
								jo.optString("error"));
					else
					{
						myCourseArray=jo.optJSONArray("�ҵĿγ�");
						mAdapter.notifyDataSetChanged();
					}
				}
				catch (Exception e) {
					
					AppUtility.showErrorToast(MyCourseActivity.this,
							e.getLocalizedMessage());
				}
				break;
			}
		}
	};
	
	class ListAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater mInflater;

		public ListAdapter(Context context) {
			this.context = context;
			this.mInflater = LayoutInflater.from(this.context);
		}

		@Override
		public int getCount() {
			return myCourseArray.length();
		}

		@Override
		public Object getItem(int position) {
		JSONObject item=null;
			try {
				item=(JSONObject) myCourseArray.get(position);
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item_mycourse, null);
				
				holder.courseName = (TextView) convertView.findViewById(R.id.tv_courseName);
				holder.summary = (TextView) convertView.findViewById(R.id.tv_summary);
				holder.photo = (ImageView) convertView.findViewById(R.id.iv_course);
				holder.button1 = (Button) convertView.findViewById(R.id.nav_button1);
				holder.button2 = (Button) convertView.findViewById(R.id.nav_button2);
				holder.button3 = (Button) convertView.findViewById(R.id.nav_button3);
				holder.button4 = (Button) convertView.findViewById(R.id.nav_button4);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			JSONObject courseItem = null;
			try {
				courseItem=(JSONObject)myCourseArray.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			aq.id(holder.photo).image(courseItem.optString("photo"),true, true, 0, R.drawable.default_photo);
			
			holder.courseName.setText(courseItem.optString("fullname"));
			if(courseItem.optString("summary").length()>0)
			{
				holder.summary.setText(courseItem.optString("summary"));
				holder.summary.setVisibility(View.VISIBLE);
			}
			else
				holder.summary.setVisibility(View.GONE);
			JSONObject options=courseItem.optJSONObject("navOptions");
			if(options==null || options.optBoolean("competencies"))
				holder.button1.setVisibility(View.VISIBLE);
			else
				holder.button1.setVisibility(View.INVISIBLE);
			if(options==null || options.optBoolean("participants"))
			{
				holder.button2.setText(getString(R.string.participants)+"("+courseItem.optString("enrolledusercount")+")");
				holder.button2.setVisibility(View.VISIBLE);
			}
			else
				holder.button2.setVisibility(View.INVISIBLE);
			if(options==null || options.optBoolean("grades"))
				holder.button3.setVisibility(View.VISIBLE);
			else
				holder.button3.setVisibility(View.INVISIBLE);
			if(options!=null && options.optBoolean("notes"))
				holder.button4.setVisibility(View.VISIBLE);
			else
				holder.button4.setVisibility(View.INVISIBLE);
			
			holder.button1.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
						JSONObject courseItem = null;
						try {
							courseItem=(JSONObject)myCourseArray.get(position);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent intent=new Intent(MyCourseActivity.this,SchoolActivity.class);
						intent.putExtra("templateName", "�ɼ�");
						JSONObject params=new JSONObject();
						try {
							params.put("function", "getMyCourseData");
							params.put("action", "�γ�����");
							params.put("courseid", courseItem.optString("id"));
							params.put("theUserid", user.getId());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						intent.putExtra("interfaceName", params.toString());
						intent.putExtra("title", getString(R.string.competencies));
						startActivity(intent);
				}
				
			});
			
			holder.button2.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
						JSONObject courseItem = null;
						try {
							courseItem=(JSONObject)myCourseArray.get(position);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent intent=new Intent(MyCourseActivity.this,SchoolActivity.class);
						intent.putExtra("templateName", "�ɼ�");
						JSONObject params=new JSONObject();
						try {
							params.put("function", "getMyCourseData");
							params.put("action", "�γ̳�Ա");
							params.put("courseid", courseItem.optString("id"));
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						intent.putExtra("interfaceName", params.toString());
						intent.putExtra("title", getString(R.string.participants));
						startActivity(intent);
				}
				
			});
			
			holder.button3.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
						JSONObject courseItem = null;
						try {
							courseItem=(JSONObject)myCourseArray.get(position);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent intent=new Intent(MyCourseActivity.this,SchoolActivity.class);
						intent.putExtra("templateName", "��״�б�");
						JSONObject params=new JSONObject();
						try {
							params.put("function", "getMyCourseData");
							params.put("action", "�γ̳ɼ�");
							params.put("courseid", courseItem.optString("id"));
							params.put("theUserid", user.getId());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						intent.putExtra("interfaceName", params.toString());
						intent.putExtra("title", getString(R.string.grades));
						startActivity(intent);
				}
				
			});
			
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					JSONObject courseItem = null;
					try {
						courseItem=(JSONObject)myCourseArray.get(position);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Intent intent=new Intent(MyCourseActivity.this,SchoolDetailActivity.class);
					intent.putExtra("templateName", "�ɼ�");
					JSONObject params=new JSONObject();
					try {
						params.put("function", "getCourseContent");
						params.put("action", "�γ��½�");
						params.put("courseid", courseItem.optString("id"));
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					intent.putExtra("interfaceName", params.toString());
					intent.putExtra("title", courseItem.optString("shortname"));
					startActivity(intent);
					
				}
				
			});
			return convertView;
		}
	}

	class ViewHolder {
		ImageView photo;
		TextView courseName;
		TextView summary;
		Button button1;
		Button button2;
		Button button3;
		Button button4;
	}
	
	
}
