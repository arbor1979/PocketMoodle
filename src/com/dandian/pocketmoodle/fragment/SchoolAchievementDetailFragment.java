package com.dandian.pocketmoodle.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.dandian.pocketmoodle.CampusApplication;
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.activity.SchoolActivity;
import com.dandian.pocketmoodle.activity.SchoolDetailActivity;
import com.dandian.pocketmoodle.activity.WebSiteActivity;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.entity.AchievementDetail;
import com.dandian.pocketmoodle.entity.AchievementDetail.Achievement;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.DialogUtility;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.dandian.pocketmoodle.util.ZLibUtils;


/**
 * 成绩详情
 */
@SuppressLint("ValidFragment")
public class SchoolAchievementDetailFragment extends Fragment {
	private String TAG = "SchoolAchievementDetailFragment";
	private ListView myListview;
	private Button btnLeft;
	private TextView tvTitle,tvRight;
	private LinearLayout lyLeft,lyRight;
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private LinearLayout emptyLayout;
	private AchievementDetail achievementDetail;
	private String title, interfaceName;
	private LayoutInflater inflater;
	private AchieveAdapter adapter;
	private LayoutParams leftParams, rightParams;
	private Dialog dialog;
	private List<Achievement> achievements = new ArrayList<Achievement>();
	private boolean refreshParent=false;
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
				String resultStr = "";
				byte[] contact64byte = null;
				if (AppUtility.isNotEmpty(result)) {
					try {
						contact64byte = Base64.decode(result.getBytes("GBK"));
						
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				resultStr=ZLibUtils.decompress(contact64byte);
				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						if(AppUtility.isNotEmpty(res)){
							AppUtility.showToastMsg(getActivity(), res);
						}else{
							achievementDetail = new AchievementDetail(jo);
							Log.d(TAG, "--------noticesItem.getNotices().size():"
									+ achievementDetail.getAchievements().size());
							initDate();
						}
					} catch (JSONException e) {
						//showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			case 1:
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						AppUtility.showToastMsg(getActivity(), res);
						if(res.equals("成功"))
						{
							getAchievesItem();
							refreshParent=true;
						}
						
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			case 2:
				LinearLayout v=(LinearLayout)msg.obj;
				v.setVisibility(View.GONE);
				break;
			case 3:
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						
						if(res.equals("成功"))
						{
							AppUtility.showToastMsg(getActivity(), res);
							String autoClose=jo.optString("自动关闭");
							if(autoClose!=null && autoClose.equals("是"))
							{
								Intent aintent = new Intent();
								getActivity().setResult(1,aintent); 
								getActivity().finish();
							}
						}
						else
							AppUtility.showErrorToast(getActivity(), res);
						
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
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
	public SchoolAchievementDetailFragment() {
		
	}
	public SchoolAchievementDetailFragment(String title, String iunterfaceName) {
		this.title = title;
		this.interfaceName = iunterfaceName;
		leftParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, 1.0f);
		rightParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, 1.0f);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.school_listview_fragment,
				container, false);
		myListview = (ListView) view.findViewById(R.id.my_listview);
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		tvRight = (TextView) view.findViewById(R.id.tv_right);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		lyLeft = (LinearLayout) view.findViewById(R.id.layout_btn_left);
		lyRight = (LinearLayout) view.findViewById(R.id.layout_btn_right);
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
				Intent aintent = new Intent();
				if(refreshParent)
					getActivity().setResult(1,aintent);
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
	 * 功能描述:初始化数据
	 * 
	 * @author shengguo 2014-4-17 下午5:18:06
	 * 
	 */
	private void initDate() {
		tvTitle.setText(achievementDetail.getTitle());
		achievements = achievementDetail.getAchievements();
		//设置Weight值
		float leftWeight = achievementDetail.getLeftWeight() / 10.0f;
		float rightWeight = achievementDetail.getRightWeight() / 10.0f;
		Log.d(TAG, "leftWeight:" + leftWeight + ",rightWeight:" + rightWeight);
		leftParams = new LayoutParams(0,LayoutParams.WRAP_CONTENT, leftWeight);
		rightParams = new LayoutParams(0,LayoutParams.WRAP_CONTENT, rightWeight);
		if(achievementDetail.getSubmitBtn()!=null && achievementDetail.getSubmitBtn().length()>0)
		{
			tvRight.setText(achievementDetail.getSubmitBtn());
			tvRight.setVisibility(View.VISIBLE);
			lyRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(achievementDetail.getSubmitTarget().equals("是"))
					{
						if(achievementDetail.getSubmitConfirm()!=null && achievementDetail.getSubmitConfirm().length()>0)
						{
						new AlertDialog.Builder(getActivity()).setTitle(achievementDetail.getSubmitConfirm()) 
					    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { 
					 
					        @Override 
					        public void onClick(DialogInterface dialog, int which) { 
					        // 点击“确认”后的操作 
					        	submitButtonClick(achievementDetail.getSubmitBtnUrl());
					 
					        } 
					    }) 
					    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { 
					 
					        @Override 
					        public void onClick(DialogInterface dialog, int which) { 
					        // 点击“返回”后的操作,这里不设置没有任何操作 
					        } 
					    }).show(); 
						}
						else
							submitButtonClick(achievementDetail.getSubmitBtnUrl());
						
					}
					else
					{
						Intent intent =new Intent(getActivity(),SchoolDetailActivity.class);
						intent.putExtra("templateName", "调查问卷");
						int pos=interfaceName.indexOf("?");
						String preUrl=interfaceName;
						if(pos>-1)
							preUrl=interfaceName.substring(0, pos);
						intent.putExtra("interfaceName", preUrl+achievementDetail.getSubmitBtnUrl());
						intent.putExtra("title", title);
						intent.putExtra("status", "进行中");
						intent.putExtra("autoClose", "是");
						startActivityForResult(intent, 101);
					}
				}
			});
		}
		else
		{
			tvRight.setVisibility(View.GONE);
			lyRight.setOnClickListener(null);
		}
		adapter.notifyDataSetChanged();
	}
	//submit按钮
	private void submitButtonClick(String url) {

		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");

		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		dialog = DialogUtility.createLoadingDialog(getActivity(),
				getString(R.string.dataprocessing));
		dialog.show();

		String base64Str = Base64.encode(jo.toString().getBytes());
	
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		int pos=interfaceName.indexOf("?");
		String preUrl=interfaceName;
		if(pos>-1)
			preUrl=interfaceName.substring(0, pos);
		CampusAPI.getSchoolItem(params,
				preUrl + url,
				new RequestListener() {

					@Override
					public void onIOException(IOException e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onError(CampusException e) {
						Log.d(TAG, "----response" + e.getMessage());
						if(dialog != null){
							dialog.dismiss();
						}
						Message msg = new Message();
						msg.what = -1;
						msg.obj = e.getMessage();
						mHandler.sendMessage(msg);
					}

					@Override
					public void onComplete(String response) {
						Log.d(TAG, "----response" + response);
						if(dialog != null){
							dialog.dismiss();
						}
						Message msg = new Message();
						msg.what = 3;
						msg.obj = response;
						mHandler.sendMessage(msg);
					}
				});
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
		Log.d(TAG, "--------" + String.valueOf(new Date().getTime()));
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "----------datatime:" + datatime);
		Log.d(TAG, "----------checkCode:" + checkCode + "++");
		JSONObject jo = new JSONObject();
		Locale locale = getResources().getConfiguration().locale;
	    String language = locale.getCountry();
	    String thisVersion = CampusApplication.getVersion();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
			jo.put("language", language);
			jo.put("当前版本", thisVersion);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getSchoolItemZip(params, interfaceName, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response" + e.getMessage());

			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----response" + response);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}

	
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
						R.layout.school_achievement_detail_item, parent, false);
				holder = new ViewHolder();
				holder.celllayout=(LinearLayout)convertView.findViewById(R.id.cell_layout);
				holder.left = (TextView) convertView.findViewById(R.id.thieDescription);
				holder.right = (TextView) convertView.findViewById(R.id.tv_right);
				holder.hiddenBtn=(ImageView)convertView.findViewById(R.id.hiddenBtn);
				holder.ly_hidden=(LinearLayout)convertView.findViewById(R.id.ly_hidden);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			AQuery aq = new AQuery(convertView);
			final Achievement achievement = (Achievement) getItem(position);
			holder.left.setText(achievement.getSubject());
			holder.right.setText(achievement.getFraction());
			if(achievement.getHiddenBtn()!=null && achievement.getHiddenBtn().length()>0)
			{
				
				aq.id(holder.hiddenBtn).image(achievement.getHiddenBtn(),true,true);
				holder.ly_hidden.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						doRequestUrl(achievement.getHiddenBtnURL());
					}
					
				});
			}
			holder.left.setLayoutParams(leftParams);
			holder.right.setLayoutParams(rightParams);
			
			convertView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if(achievement.getHiddenBtn()!=null && achievement.getHiddenBtn().length()>0)
					{
						ViewHolder holder = (ViewHolder) v.getTag();
						if(holder.ly_hidden.getVisibility()==View.GONE)
						{
							holder.ly_hidden.setVisibility(View.VISIBLE);
							Timer timer = new Timer(); 
							timer.schedule(new Task(holder), 3 * 1000);
						}
					}
					
					if(achievement.getLat()!=0 && !String.valueOf(achievement.getLat()).equals("NaN"))
					{
						Intent contractIntent = new Intent(getActivity(),WebSiteActivity.class);
						String address=achievement.getFraction().split("\n")[0];
						String url=String.format("http://mo.amap.com/?q=%.10f,%.10f&name=%s&dev=1", achievement.getLat(),achievement.getLon(),address);
						contractIntent.putExtra("url",url);
						contractIntent.putExtra("title", achievement.getSubject());
						startActivity(contractIntent);
					}
					if(achievement.getHtmlText().length()>0 && !achievement.getHtmlText().equals("null"))
					{
						Intent contractIntent = new Intent(getActivity(),WebSiteActivity.class);
						contractIntent.putExtra("htmlText",achievement.getHtmlText());
						contractIntent.putExtra("title", achievementDetail.getTitle());
						contractIntent.putExtra("loginUrl", achievementDetail.getLoginUrl());
						startActivity(contractIntent);
					}
					
					if(achievement.getUrl().length()>0 && !achievement.getUrl().equals("null"))
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
						intent.putExtra("interfaceName", preUrl+achievement.getUrl());
						intent.putExtra("title", achievement.getFraction());
						intent.putExtra("display", achievement.getFraction());
						startActivityForResult(intent,101);
					}
					
				}
				
			});
			return convertView;
		}
		class Task extends TimerTask {
			private ViewHolder holder;
			public Task(ViewHolder h)
			{
				holder=h;
			}
			public void run()
			{
				Message msg = new Message();
				msg.what = 2;
				msg.obj = holder.ly_hidden;
				mHandler.sendMessage(msg);   
			}
		}
		class ViewHolder {
			LinearLayout celllayout;
			TextView left;
			TextView right;
			ImageView hiddenBtn;
			LinearLayout ly_hidden;
		}
	}
	private void doRequestUrl(String url)
	{
		dialog = DialogUtility.createLoadingDialog(getActivity(),
				getString(R.string.dataprocessing));
		dialog.show();
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		int pos=interfaceName.indexOf("?");
		String preUrl=interfaceName;
		if(pos>-1)
			preUrl=interfaceName.substring(0, pos);
		CampusAPI.getSchoolItem(params,preUrl+ url,new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response" + e.getMessage());
				if(dialog != null){
					dialog.dismiss();
				}
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----response" + response);
				if(dialog != null){
					dialog.dismiss();
				}
				Message msg = new Message();
				msg.what = 1;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}
}
