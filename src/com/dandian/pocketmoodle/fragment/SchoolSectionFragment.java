package com.dandian.pocketmoodle.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.activity.ChatMsgActivity;
import com.dandian.pocketmoodle.activity.MyCourseActivity;
import com.dandian.pocketmoodle.activity.SchoolActivity;
import com.dandian.pocketmoodle.activity.SchoolDetailActivity;
import com.dandian.pocketmoodle.activity.ShowPersonInfo;
import com.dandian.pocketmoodle.activity.WebSiteActivity;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.entity.AchievementItem;
import com.dandian.pocketmoodle.entity.AchievementItem.Achievement;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.dandian.pocketmoodle.fragment.SchoolSectionFragment.AchieveAdapter;
import com.dandian.pocketmoodle.fragment.SchoolSectionFragment.AchieveAdapter.ViewHolder;

/**
 * 成绩
 */
@SuppressLint("ValidFragment")
public class SchoolSectionFragment extends Fragment {
	private String TAG = "SchoolAchievementFragment";
	private ListView myListview;
	private Button btnLeft;
	private TextView tvTitle,tvRight,tv_summary;
	private LinearLayout lyLeft,lyRight;
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private LinearLayout emptyLayout;
	private AchievementItem achievementItem;
	private String interfaceName,title;
	private LayoutInflater inflater;
	private AchieveAdapter adapter;
	private List<Achievement> achievements = new ArrayList<Achievement>();
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				showFetchFailedView();
				AppUtility.showErrorToast(getActivity(), msg.obj.toString());
				break;
			case 0:
				showProgress(false);
				String result = msg.obj.toString();
				try 
				{
					JSONObject jo = new JSONObject(result);
					if(jo.optString("结果").equals("失败"))
						AppUtility.showErrorToast(getActivity(),
								jo.optString("error"));
					else
					{
						achievementItem = new AchievementItem(jo);
						Log.d(TAG, "--------noticesItem.getNotices().size():"
								+ achievementItem.getAchievements().size());
						
						achievements = achievementItem.getAchievements();
						adapter.notifyDataSetChanged();
						tvTitle.setText(achievementItem.getTitle());
						if(achievementItem.getRightButton()!=null && achievementItem.getRightButton().length()>0)
						{
							tvRight.setText(achievementItem.getRightButton());
							tvRight.setVisibility(View.VISIBLE);
							lyRight.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent =new Intent(getActivity(),SchoolDetailActivity.class);
									intent.putExtra("templateName", "调查问卷");
									intent.putExtra("interfaceName", interfaceName+achievementItem.getRightButtonURL());
									intent.putExtra("title", title);
									intent.putExtra("status", "进行中");
									intent.putExtra("autoClose", "是");
									startActivityForResult(intent,101);
								}
							});
						}
						else
						{
							tvRight.setVisibility(View.GONE);
							lyRight.setOnClickListener(null);
						}
						
					} 
				}
				catch (Exception e) {
					
					AppUtility.showErrorToast(getActivity(),
							e.getLocalizedMessage());
				}
				break;

			}
		}
	};
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		case 1:
			getAchievesItem();
		    break;
		default:
		    break;
		}
	}
	public SchoolSectionFragment() {
		
	}
	public SchoolSectionFragment(String title,String iunterfaceName) {
		this.interfaceName = iunterfaceName;
		this.title = title;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.school_section_listview,
				container, false);
		
		RelativeLayout nav_bar=(RelativeLayout) view.findViewById(R.id.nav_bar);
		int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			nav_bar.setBackgroundColor(color);
		myListview = (ListView) view.findViewById(R.id.my_listview);
		tv_summary=(TextView)view.findViewById(R.id.tv_summary);
		myListview.addHeaderView(tv_summary);
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		tvRight = (TextView) view.findViewById(R.id.tv_right);
		lyRight = (LinearLayout) view.findViewById(R.id.layout_btn_right);
		lyLeft = (LinearLayout) view.findViewById(R.id.layout_btn_left);
		loadingLayout = (LinearLayout) view.findViewById(R.id.data_load);
		contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) view.findViewById(R.id.empty_error);
		emptyLayout = (LinearLayout) view.findViewById(R.id.empty);
		myListview.setEmptyView(emptyLayout);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);
		tvTitle.setText(title);
		adapter = new AchieveAdapter();
		myListview.setAdapter(adapter);
		lyLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		// 重新加载
		failedLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getAchievesItem();
			}
		});
		getAchievesItem();
		return view;
	}


	/**
	 * 显示加载失败提示页
	 */
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

	/**
	 * 功能描述:获取通知内容
	 * 
	 * @author shengguo 2014-4-16 上午11:12:43
	 * 
	 */
	public void getAchievesItem() {
		showProgress(true);
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo=null;
		try {
			jo = new JSONObject(interfaceName);
			jo.put("用户较验码", checkCode);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPostToDandian(jo, mHandler, 0);
	}

	@SuppressLint("NewApi")
	class AchieveAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return achievements.size();
		}

		@Override
		public Object getItem(int position) {
			return achievements.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (null == convertView) {
				convertView = inflater.inflate(
						R.layout.school_section_list_item, parent,
						false);
				holder = new ViewHolder();

				holder.icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.title = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.total = (TextView) convertView
						.findViewById(R.id.thieDescription);
				holder.rank = (TextView) convertView
						.findViewById(R.id.tv_right);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Achievement achievement = (Achievement) getItem(position);
			AQuery aq = new AQuery(convertView);
			String imagurl = achievement.getIcon();
			Log.d(TAG, "----imagurl:" + imagurl);
			if (imagurl != null && !imagurl.equals("")) {
				aq.id(holder.icon).image(imagurl);
				if(achievement.getIcon_link().equals("个人资料"))
				{
					holder.icon.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),
									ShowPersonInfo.class);
							intent.putExtra("studentId", achievement.getId());
							intent.putExtra("userImage", achievement.getIcon());
							startActivity(intent);
						}
						
					});
				}
			}
	
			holder.title.setText(achievement.getTitle());
			holder.total.setText(achievement.getTotal());
			holder.rank.setText(achievement.getRank());
			
			if(achievement.getTheColor()!=null && achievement.getTheColor().length()>0)
			{
				if(achievement.getTheColor().toLowerCase().equals("red"))
					holder.total.setBackground(getResources().getDrawable(R.drawable.school_achievement_red));
				else if(achievement.getTheColor().toLowerCase().equals("blue"))
					holder.total.setBackground(getResources().getDrawable(R.drawable.school_achievement_blue));
				else if(achievement.getTheColor().toLowerCase().equals("brown"))
					holder.total.setBackground(getResources().getDrawable(R.drawable.school_achievement_brown));
				else if(achievement.getTheColor().toLowerCase().equals("pink"))
					holder.total.setBackground(getResources().getDrawable(R.drawable.school_achievement_pink));
				else
					holder.total.setBackground(getResources().getDrawable(R.drawable.school_achievement_bg));
			}
			
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String DetailUrl = achievement.getDetailUrl();
					if (AppUtility.isNotEmpty(DetailUrl)) {
						Log.d(TAG,"----notice.getEndUrl():"+ achievement.getDetailUrl());
						if(DetailUrl.toLowerCase().startsWith("http"))
						{
							Intent contractIntent = new Intent(getActivity(),WebSiteActivity.class);
							contractIntent.putExtra("htmlText","<script>location.href='"+DetailUrl+"';</script>");
							DetailUrl=DetailUrl.replace("\\", "/");
							String[] loginUrl=DetailUrl.split("/");
							contractIntent.putExtra("loginUrl", "http://"+loginUrl[2]+"/login/index.php");
							contractIntent.putExtra("title", title);
							startActivity(contractIntent);
						}
						else if(DetailUrl.equals("发送消息"))
						{
							Intent intent = new Intent(getActivity(),ChatMsgActivity.class);
							intent.putExtra("toid", achievement.getId());
							intent.putExtra("type", "消息");
							intent.putExtra("toname", achievement.getTitle());
							intent.putExtra("userImage", achievement.getIcon());
							getActivity().startActivity(intent);
						}
						else if(DetailUrl.toLowerCase().indexOf(".php")>=0)
						{
							Intent intent =null;
							if(achievement.getTemplateName()==null || achievement.getTemplateName().length()==0)
							{
								intent=new Intent(getActivity(),SchoolDetailActivity.class);
								intent.putExtra("templateName", "成绩");
							}
							else
							{
								if(achievement.getTemplateGrade().equals("main"))
									intent=new Intent(getActivity(),SchoolActivity.class);
								else
									intent=new Intent(getActivity(),SchoolDetailActivity.class);
								intent.putExtra("templateName", achievement.getTemplateName());
							}
							int pos=interfaceName.indexOf("?");
							String preUrl=interfaceName;
							if(pos>-1)
								preUrl=interfaceName.substring(0, pos);
							intent.putExtra("interfaceName", preUrl+DetailUrl+"&newFlag=1");
							intent.putExtra("title", title);
							intent.putExtra("display", title);
							startActivityForResult(intent,101);
						}
						
					}
				}
			});
	
			return convertView;
		}

		class ViewHolder {
			ImageView icon;
			TextView title;
			TextView total;
			TextView rank;
		}
		
	}
}
