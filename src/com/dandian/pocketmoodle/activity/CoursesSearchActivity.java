package com.dandian.pocketmoodle.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.fragment.ContactsSearchFragment;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.PrefUtility;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: 联系人界面
 * 
 * <br/>
 * 创建说明: 2013-12-9 上午10:04:26 zhuliang 创建文件<br/>
 * 
 * 修改历史: 正在修改。。。。expandablelistview<br/>
 * 
 */
@SuppressLint({ "NewApi", "HandlerLeak" })
public class CoursesSearchActivity extends FragmentActivity {
	static Button menu;
	public static LinearLayout layout_refresh;
	
	private ViewGroup search_head;
	public static EditText search;
	
	static ContactsSearchFragment contactsSearchFragment;
	
	private ProgressDialog loadingDlg;
	private String cateId;
	private JSONArray coursesArray;
	private ListAdapter adapter;
	private ListView courseListView;
	AQuery aq;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_course_search);
		search = (EditText) findViewById(R.id.edit_search);
		
		search.setFocusable(false);
		search.setFocusableInTouchMode(false);
		aq = new AQuery(this);
		initViews();
		cateId = getIntent().getStringExtra("cateId");
		getCourseList();
	}
	
	private void getCourseList() {
		loadingDlg=ProgressDialog.show(this, null, getString(R.string.data_loading_progress),true);
		loadingDlg.show();
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("cateId", cateId);
			jo.put("function", "searchCourse");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPostToDandian(jo, mHandler, 0);
	}
	
	
	// 初始化Views
	private void initViews() {
		
		aq.id(R.id.back).clicked(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		aq.id(R.id.setting_tv_title).text(R.string.search_course);
		
		search_head = (ViewGroup) findViewById(R.id.search_head);
		search_head.getBackground().setAlpha(50);
		
		RelativeLayout nav_bar=(RelativeLayout) findViewById(R.id.headerlayout);
		int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			nav_bar.setBackgroundColor(color);
		
		courseListView = (ListView) findViewById(R.id.courseListView);
		color=PrefUtility.getInt(Constants.PREF_THEME_LISTCOLOR, 0);
		if(color!=0)
			courseListView.setBackgroundColor(color);
		coursesArray=new JSONArray();
		adapter=new ListAdapter(this);
		courseListView.setAdapter(adapter);
	}
	
	//	消息处理
	private Handler mHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				if(loadingDlg!=null)
					loadingDlg.dismiss();
				AppUtility.showErrorToast(CoursesSearchActivity.this,
						msg.obj.toString());
				break;
			case 0:
				if(loadingDlg!=null)
					loadingDlg.dismiss();
				String result = msg.obj.toString();
				try 
				{
					JSONObject jo = new JSONObject(result);
					if(jo.optString("结果").equals("失败"))
						AppUtility.showErrorToast(CoursesSearchActivity.this,
								jo.optString("error"));
					else
					{
						coursesArray=jo.optJSONArray("课程数组");
						adapter.notifyDataSetChanged();
					}
				}
				catch (Exception e) {
					
					AppUtility.showErrorToast(CoursesSearchActivity.this,
							e.getLocalizedMessage());
				}
				break;
			
			default:
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
			return coursesArray.length();
		}

		@Override
		public Object getItem(int position) {
		JSONObject item=null;
			try {
				item=(JSONObject) coursesArray.get(position);
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
				convertView = mInflater.inflate(R.layout.list_item_course, null);
				
				holder.iv_course_logo = (ImageView) convertView.findViewById(R.id.iv_course_logo);
				holder.tv_course_name = (TextView) convertView.findViewById(R.id.tv_course_name);
				holder.tv_teacher_name = (TextView) convertView.findViewById(R.id.tv_teacher_name);
				holder.iv_enrol1 = (ImageView) convertView.findViewById(R.id.iv_enrol1);
				holder.tv_enrol1 = (TextView) convertView.findViewById(R.id.tv_enrol1);
				holder.iv_enrol2 = (ImageView) convertView.findViewById(R.id.iv_enrol2);
				holder.tv_enrol2 = (TextView) convertView.findViewById(R.id.tv_enrol2);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final JSONObject cateObj = (JSONObject) getItem(position);
			aq.id(holder.iv_course_logo).image(cateObj.optString("图片地址"), true, true, 0, R.drawable.default_photo);
			holder.tv_course_name.setText(cateObj.optString("课程名称"));
			holder.tv_teacher_name.setText(cateObj.optString("教师名称"));
			if(cateObj.optString("方式1图片").length()>0)
			{
				holder.iv_enrol1.setVisibility(View.VISIBLE);
				holder.tv_enrol1.setVisibility(View.VISIBLE);
				aq.id(holder.iv_enrol1).image(cateObj.optString("方式1图片"), true, true);
				holder.tv_enrol1.setText(cateObj.optString("方式1文字"));
			}
			else
			{
				holder.iv_enrol1.setVisibility(View.GONE);
				holder.tv_enrol1.setVisibility(View.GONE);
			}
			if(cateObj.optString("方式2图片").length()>0)
			{
				holder.iv_enrol2.setVisibility(View.VISIBLE);
				holder.tv_enrol2.setVisibility(View.VISIBLE);
				aq.id(holder.iv_enrol2).image(cateObj.optString("方式2图片"), true, true);
				holder.tv_enrol2.setText(cateObj.optString("方式2文字"));
			}
			else
			{
				holder.iv_enrol2.setVisibility(View.GONE);
				holder.tv_enrol2.setVisibility(View.GONE);
			}
			
			/*
			if(chatFriend.getMsgType().equals("群消息")){
				holder.photo.setImageResource(R.drawable.contacts_group);
			}*/
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent intent=new Intent(CoursesSearchActivity.this,SchoolDetailActivity.class);
					intent.putExtra("templateName", "博客");
					intent.putExtra("interfaceName","?function=getUserInfo&action=courseSummary&courseId="+cateObj.optString("id"));
					intent.putExtra("title", cateObj.optString("name"));
					intent.putExtra("display", getString(R.string.course_summary));
					startActivity(intent);
				}
				
			});
			return convertView;
		}
	}
	class ViewHolder {
		ImageView iv_course_logo;
		TextView tv_course_name;
		TextView tv_teacher_name;
		ImageView iv_enrol1;
		TextView tv_enrol1;
		ImageView iv_enrol2;
		TextView tv_enrol2;
		
	}
}
