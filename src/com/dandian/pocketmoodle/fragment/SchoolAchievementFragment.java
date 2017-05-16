package com.dandian.pocketmoodle.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
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
import com.dandian.pocketmoodle.activity.SchoolActivity;
import com.dandian.pocketmoodle.activity.SchoolDetailActivity;
import com.dandian.pocketmoodle.activity.ShowPersonInfo;
import com.dandian.pocketmoodle.activity.WebSiteActivity;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.entity.AchievementItem;
import com.dandian.pocketmoodle.entity.AchievementItem.Achievement;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.MyImageGetter;
import com.dandian.pocketmoodle.util.MyTagHandler;
import com.dandian.pocketmoodle.util.PrefUtility;

/**
 * 成绩
 */
@SuppressLint("ValidFragment")
public class SchoolAchievementFragment extends Fragment {
	private String TAG = "SchoolAchievementFragment";
	private ListView myListview;
	private Button btnLeft;
	private TextView tvTitle,tvRight;
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
						if(achievementItem.getSummary()!=null && achievementItem.getSummary().length()>0)
						{
							TextView tv_summary=new TextView(getActivity());
							LinearLayout layout=new LinearLayout(getActivity());
							layout.setOrientation(LinearLayout.VERTICAL);
							LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT);
							layout.setBackgroundColor(Color.WHITE);
							layout.setLayoutParams(params);
							LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT);
							params1.setMargins(20,20,20,20);
							tv_summary.setLayoutParams(params1);
							layout.addView(tv_summary);
							tv_summary.setBackgroundColor(Color.WHITE);
							Spanned spanned = Html.fromHtml(achievementItem.getSummary(), new MyImageGetter(getActivity(),tv_summary), new MyTagHandler(getActivity()));
							tv_summary.setText(spanned);
							tv_summary.setMovementMethod(LinkMovementMethod.getInstance());
							myListview.setAdapter(null);
							myListview.addHeaderView(layout);
							myListview.setAdapter(adapter);
							
						}
						achievements = achievementItem.getAchievements();
						adapter.notifyDataSetChanged();
						if(achievementItem.getTitle()!=null && achievementItem.getTitle().length()>0)
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
	public SchoolAchievementFragment() {
		
	}
	public SchoolAchievementFragment(String title,String iunterfaceName) {
		this.interfaceName = iunterfaceName;
		this.title = title;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.school_listview_fragment,
				container, false);
		
		RelativeLayout nav_bar=(RelativeLayout) view.findViewById(R.id.nav_bar);
		int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			nav_bar.setBackgroundColor(color);
		myListview = (ListView) view.findViewById(R.id.my_listview);
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
						R.layout.school_achievement_or_question_item, parent,
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
				holder.ll_bottom = (LinearLayout) convertView
						.findViewById(R.id.ll_bottom);
				holder.iv_right = (ImageView) convertView
						.findViewById(R.id.iv_right);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Achievement achievement = (Achievement) getItem(position);
			AQuery aq = new AQuery(convertView);
			String imagurl = achievement.getIcon();
			Log.d(TAG, "----imagurl:" + imagurl);
			if (imagurl != null && !imagurl.equals("")) {
				holder.icon.setVisibility(View.VISIBLE);
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
			else
				holder.icon.setVisibility(View.GONE);
	
			holder.title.setText(achievement.getTitle());
			if(achievement.getTotal().length()==0 && achievement.getRank().length()==0)
			{
				holder.ll_bottom.setVisibility(View.GONE);
				LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(50, 50);
				holder.icon.setLayoutParams(params);
			}
			else
				holder.ll_bottom.setVisibility(View.VISIBLE);
			if(achievement.getTotal().length()==0)
				holder.total.setVisibility(View.GONE);
			else
				holder.total.setVisibility(View.VISIBLE);
			holder.total.setText(achievement.getTotal());
			holder.rank.setText(achievement.getRank());
			if(achievement.getDetailUrl().length()==0)
				holder.iv_right.setVisibility(View.GONE);
			else
				holder.iv_right.setVisibility(View.VISIBLE);
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
						else if(DetailUrl.length()>0)
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
							intent.putExtra("interfaceName", DetailUrl);
							intent.putExtra("title", achievement.getDetailTitle());
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
			LinearLayout ll_bottom;
			ImageView iv_right;
		}
		
	}
}
