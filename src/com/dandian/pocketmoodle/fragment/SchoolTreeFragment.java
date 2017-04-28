package com.dandian.pocketmoodle.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.activity.MyCourseActivity;
import com.dandian.pocketmoodle.activity.SchoolActivity;
import com.dandian.pocketmoodle.activity.SchoolDetailActivity;
import com.dandian.pocketmoodle.adapter.SimpleTreeAdapter;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.entity.FileBean;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.zhy.tree.bean.Node;
import com.zhy.tree.bean.TreeListViewAdapter;
import com.zhy.tree.bean.TreeListViewAdapter.OnTreeNodeClickListener;

/**
 * 成绩
 */
@SuppressLint("ValidFragment")
public class SchoolTreeFragment extends Fragment {
	private String TAG = "SchoolAchievementFragment";
	private ListView myListview;
	private Button btnLeft;
	private TextView tvTitle,tvRight;
	private LinearLayout lyLeft,lyRight;
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private LinearLayout emptyLayout;
	private String interfaceName,title;
	private LayoutInflater inflater;
	private TreeListViewAdapter mAdapter;
	private List<FileBean> mDatas = new ArrayList<FileBean>();
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
						JSONObject tables=jo.optJSONObject("成绩数值");
						tvTitle.setText(jo.optString("标题显示"));
						Iterator<String> iterator = tables.keys();
						while(iterator.hasNext()){
						        String key = (String) iterator.next();
						        JSONObject item = tables.optJSONObject(key);
						        mDatas.add(new FileBean(item.optInt("id"), item.optInt("parent"), item.optString("name"),item.optString("desc"),item.optString("image")));
						}
						
						mAdapter = new SimpleTreeAdapter<FileBean>(myListview, getActivity(), mDatas, 10);
						myListview.setAdapter(mAdapter);
						mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener()
						{
							@Override
							public void onClick(Node node, int position)
							{
								if(node.getDesc()!=null && node.getDesc().length()>0)
								{
									JSONObject jo=null;
									try {
										jo = new JSONObject(interfaceName);
									} catch (JSONException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									String theUserid=jo.optString("theUserid");
									String courseid=jo.optString("courseid");
									Intent intent=new Intent(getActivity(),SchoolDetailActivity.class);
									intent.putExtra("templateName", "成绩");
									JSONObject params=new JSONObject();
									try {
										params.put("function", "getMyCourseData");
										params.put("action", "成绩比重");
										params.put("courseid", courseid);
										params.put("nodeid", node.getId());
										params.put("theUserid", theUserid);
										
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									intent.putExtra("interfaceName", params.toString());
									intent.putExtra("title", getString(R.string.details));
									startActivity(intent);
								}
							}

						});
						/*
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
						*/
						
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
	public SchoolTreeFragment() {
		
	}
	public SchoolTreeFragment(String title,String iunterfaceName) {
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

	
}
