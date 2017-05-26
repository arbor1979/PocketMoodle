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
import com.dandian.pocketmoodle.util.PrefUtility;

/**
 * 成绩
 */
@SuppressLint("ValidFragment")
public class SchoolQuizFragment extends Fragment {
	private String TAG = "SchoolAchievementFragment";
	private ListView myListview;
	private Button btnLeft,bt_bottom;
	private TextView tvTitle,tvRight,tv_summary,tv_rules,tv_grade;
	private LinearLayout lyLeft,lyRight;
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private LinearLayout emptyLayout;
	private LinearLayout ll_summary,ll_bottom;
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
						ll_summary.setVisibility(View.GONE);
						if(achievementItem.getSummary()!=null && achievementItem.getSummary().length()>0)
						{
							ll_summary.setVisibility(View.VISIBLE);
							Spanned spanned = Html.fromHtml(achievementItem.getSummary(), new MyImageGetter(getActivity(),tv_summary), null);
							tv_summary.setText(spanned);
						}
						if(achievementItem.getRules()!=null && achievementItem.getRules().length()>0)
						{
							ll_summary.setVisibility(View.VISIBLE);
							tv_rules.setText(achievementItem.getRules());
						}
						if(achievementItem.getGrade()!=null && achievementItem.getGrade().length()>0)
						{
							ll_summary.setVisibility(View.VISIBLE);
							tv_grade.setText(achievementItem.getGrade());
						}
						ll_bottom.setVisibility(View.GONE);
						if(achievementItem.getBottomButton()!=null && achievementItem.getBottomButton().length()>0)
						{
							ll_bottom.setVisibility(View.VISIBLE);
							bt_bottom.setText(achievementItem.getBottomButton());
							if(achievementItem.getBottomButtonURL()!=null && achievementItem.getBottomButtonURL().length()>0)
							{
								bt_bottom.setEnabled(true);
								bt_bottom.setBackgroundResource(R.drawable.button_round_corner_green);
								bt_bottom.setOnClickListener(new OnClickListener(){

									@Override
									public void onClick(View v) {
										Intent intent =new Intent(getActivity(),SchoolDetailActivity.class);
										intent.putExtra("templateName", "测验");
										intent.putExtra("interfaceName", achievementItem.getBottomButtonURL());
										intent.putExtra("title", title);
										intent.putExtra("status", "进行中");
										intent.putExtra("autoClose", "是");
										startActivityForResult(intent,101);
									}
									
								});
							}
							else
							{
								bt_bottom.setEnabled(false);
								bt_bottom.setBackgroundResource(R.drawable.button_round_corner_gray);
							}
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
									intent.putExtra("templateName", "测验");
									intent.putExtra("interfaceName", achievementItem.getRightButtonURL());
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
	public SchoolQuizFragment() {
		
	}
	public SchoolQuizFragment(String title,String iunterfaceName) {
		this.interfaceName = iunterfaceName;
		this.title = title;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.school_quiz_listview,
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
		ll_summary= (LinearLayout) view.findViewById(R.id.ll_summary);
		ll_bottom= (LinearLayout) view.findViewById(R.id.ll_bottom);
		ll_summary.setVisibility(View.GONE);
		ll_bottom.setVisibility(View.GONE);
		tv_summary =(TextView) view.findViewById(R.id.tv_summary);
		tv_rules =(TextView) view.findViewById(R.id.tv_rules);
		tv_grade =(TextView) view.findViewById(R.id.tv_grade);
		bt_bottom = (Button) view.findViewById(R.id.bt_bottom);
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
						R.layout.school_quiz_list_item, parent,
						false);
				holder = new ViewHolder();

				holder.left = (TextView) convertView
						.findViewById(R.id.tv_left);
				holder.center = (TextView) convertView
						.findViewById(R.id.tv_center);
				holder.right = (TextView) convertView
						.findViewById(R.id.tv_right);
				
				holder.iv_right = (ImageView) convertView
						.findViewById(R.id.iv_right);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Achievement achievement = (Achievement) getItem(position);
			if((achievement.getDetailUrl()==null || achievement.getDetailUrl().length()==0) && position==0)
			{
				convertView.setBackgroundColor(Color.LTGRAY);
				holder.iv_right.setVisibility(View.GONE);
			}
			else
			{
				convertView.setBackgroundColor(Color.WHITE);
				holder.iv_right.setVisibility(View.VISIBLE);
			}
			holder.left.setText(achievement.getLeft());
			holder.center.setText(achievement.getCenter());
			holder.right.setText(achievement.getRight());
			
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String DetailUrl = achievement.getDetailUrl();
					if (AppUtility.isNotEmpty(DetailUrl)) {
						Log.d(TAG,"----notice.getEndUrl():"+ achievement.getDetailUrl());
						if(DetailUrl.toLowerCase().startsWith("http"))
						{
							Intent contractIntent = new Intent(getActivity(),WebSiteActivity.class);
							contractIntent.putExtra("url", DetailUrl);
							contractIntent.putExtra("title", title);
							startActivity(contractIntent);
						}
						
						else
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
							intent.putExtra("title", title);
							startActivityForResult(intent,101);
						}
						
					}
				}
			});
	
			return convertView;
		}

		class ViewHolder {
			
			TextView left;
			TextView center;
			TextView right;
			ImageView iv_right;
		}
		
	}
}
