package com.dandian.pocketmoodle.fragment;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.activity.ImagesActivity;
import com.dandian.pocketmoodle.activity.TabHostActivity;
import com.dandian.pocketmoodle.activity.WebSiteActivity;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.entity.NoticesDetail;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.FileUtility;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.dandian.pocketmoodle.util.IntentUtility;

/**
 * 通知
 */
public class SchoolNoticeDetailFragment extends Fragment {
	private String TAG = "SchoolNoticeDetailFragment";
	private Button btnLeft;
	private String title, interfaceName,display;
	private NoticesDetail noticesDetail;
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private TextView tvRight;
	private LinearLayout lyRight;
	private AQuery aq;
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
						if(AppUtility.isNotEmpty(res)){
							AppUtility.showToastMsg(getActivity(), res);
						}else{
							noticesDetail = new NoticesDetail(jo);
							initData();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			}
		}
	};

	
	public SchoolNoticeDetailFragment() {

	}
	public static final Fragment newInstance(String title, String interfaceName,String display){
    	Fragment fragment = new SchoolNoticeDetailFragment();
    	Bundle bundle = new Bundle();
    	bundle.putString("title", title);
    	bundle.putString("display", display);
    	bundle.putString("interfaceName", interfaceName);
    	fragment.setArguments(bundle);
    	return fragment;
    }
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		title=getArguments().getString("title");
		display=getArguments().getString("display");
		
		interfaceName=getArguments().getString("interfaceName");
		View view = inflater.inflate(R.layout.school_notice_detail_fragment,
				container, false);
		aq = new AQuery(view);
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		loadingLayout = (LinearLayout) view.findViewById(R.id.data_load);
		contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) view.findViewById(R.id.empty_error);
		
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);

		
		aq.id(R.id.tv_title).text(display+" "+getString(R.string.details));
		aq.id(R.id.layout_btn_left).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		// 重新加载
		failedLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getNoticeDetail();
			}
		});
		
		lyRight = (LinearLayout) view.findViewById(R.id.layout_btn_right);
		tvRight=(TextView)view.findViewById(R.id.tv_right);
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//AppUtility.showToastMsg(getActivity(), "正在获取数据");
		getNoticeDetail();
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

	
    
	private void initData() {
		//aq.id(R.id.tv_title).text(noticesDetail.getTitle());
		final String imagurl = noticesDetail.getImageUrl();
		
		
		Log.d(TAG, "----imagurl:" + imagurl);
		if (imagurl != null && !imagurl.equals("")) {
			aq.id(R.id.iv_image).image(imagurl,true,true,0,0);
			aq.id(R.id.iv_image).clicked(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 查看大图
					Intent intent = new Intent(getActivity(),ImagesActivity.class);
					ArrayList<String> picPaths=new ArrayList<String>();
					picPaths.add(imagurl);
					JSONArray ja=noticesDetail.getTupian();
					if(ja!=null && ja.length()>0)
					{
						for(int index=0;index<ja.length();index++)
						{
							try {
								String url=(String)ja.get(index);
								if(!picPaths.contains(url))
									picPaths.add(url);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}
					intent.putStringArrayListExtra("pics",
							(ArrayList<String>) picPaths);
					getActivity().startActivity(intent);
				}
			});
			
		} else {
			aq.id(R.id.iv_image).visibility(View.GONE);
		}
		aq.id(R.id.tv_notice_title).text(noticesDetail.getTitle());
		aq.id(R.id.tv_time).text(noticesDetail.getTime());
		String content = noticesDetail.getContent();
		Log.d(TAG, "content:"+content);
		aq.id(R.id.tv_content).text(content);
		JSONArray ja=noticesDetail.getFujian();
		if(ja!=null && ja.length()>0)
		{
			aq.id(R.id.tv_content).getTextView().append(getString(R.string.attachment)+"：\r\n");
			for(int i=0;i<ja.length();i++)
			{
				JSONObject jo;
				try {
					jo = (JSONObject) ja.get(i);
					
					SpannableString ss = new SpannableString(jo.optString("name"));
			        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, ss.length(),
			                   Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			        ss.setSpan(new MyURLSpan(jo.optString("url")), 0, ss.length(),
			                   Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			        
			        aq.id(R.id.tv_content).getTextView().append(ss);
			        aq.id(R.id.tv_content).getTextView().append("\r\n\r\n");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				
			}
			aq.id(R.id.tv_content).getTextView().setMovementMethod(LinkMovementMethod.getInstance());
			
			
			
		}
		ja=noticesDetail.getTupian();
		if(ja!=null && ja.length()>0)
		{
			aq.id(R.id.image_text).text(R.string.clicktoviewpic);
		}
		
		if(noticesDetail.getRightBtn()!=null && noticesDetail.getRightBtn().length()>0)
		{
			tvRight.setText(noticesDetail.getRightBtn());
			tvRight.setVisibility(View.VISIBLE);
			lyRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(noticesDetail.getRightBtnUrl().substring(0, 4).equalsIgnoreCase("http"))
					{
						Intent contractIntent = new Intent(getActivity(),WebSiteActivity.class);
						String username=PrefUtility.get(Constants.PREF_LOGIN_NAME, "");
						username=username.split("@")[0];
						String password=PrefUtility.get(Constants.PREF_LOGIN_PASS, "");
						String url=noticesDetail.getRightBtnUrl()+"&username="+username+"&password="+password;
						contractIntent.putExtra("url",url);
						contractIntent.putExtra("title",noticesDetail.getNewWindowTitle());
						getActivity().startActivity(contractIntent);
					}
					
				}
			});
		}
		else
		{
			tvRight.setVisibility(View.GONE);
			lyRight.setOnClickListener(null);
		}
	}

	public class MyURLSpan extends URLSpan
	{

		public MyURLSpan(String url) {
			super(url);
			// TODO Auto-generated constructor stub
		}
		@Override
	    public void onClick(View widget) {
			
			String mUrl=getURL();
			
			String path=FileUtility.creatCacheDir("download");
			String fileName=mUrl.substring(mUrl.lastIndexOf("/")+1, mUrl.length());
			String filePath=path+fileName;
			//FileUtility.deleteFile(filePath);
			File file = new File(filePath);  
			Intent intent;
	        if(file.exists())
	        {
	        	intent=IntentUtility.openUrl(filePath);
	        	IntentUtility.openIntent(widget.getContext(), intent,true);
	        }
	        else
	        {
	        	intent=IntentUtility.openUrl(mUrl);
	        	if(intent==null)
	        	{
		    		Uri uri = Uri.parse(mUrl);
			        Context context = widget.getContext();
			        intent = new Intent(Intent.ACTION_VIEW, uri);
			        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
			        context.startActivity(intent);
		    	}
		    	else
		    	{
		    		TabHostActivity.schoolService.downLoadUpdate(mUrl, 1003);
		    		
		    	}
	        }
	        
	        
	    }
		
	}
	/**
	 * 功能描述:获取通知内容
	 * 
	 * @author shengguo 2014-4-16 上午11:12:43
	 * 
	 */
	public void getNoticeDetail() {
		showProgress(true);
		Log.d(TAG, "--------" + String.valueOf(new Date().getTime()));
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "----------datatime:" + datatime);
		Log.d(TAG, "----------checkCode:" + checkCode + "++");
		Locale locale = getResources().getConfiguration().locale;
	    String language = locale.getCountry();
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
			jo.put("language", language);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getSchoolItem(params, interfaceName, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response" + e.getMessage());
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
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
}
