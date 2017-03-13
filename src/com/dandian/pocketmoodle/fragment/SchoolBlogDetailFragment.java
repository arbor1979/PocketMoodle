package com.dandian.pocketmoodle.fragment;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.support.v4.app.Fragment;
import android.text.Html;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.activity.SchoolDetailActivity;
import com.dandian.pocketmoodle.activity.ShowPersonInfo;
import com.dandian.pocketmoodle.activity.TabHostActivity;
import com.dandian.pocketmoodle.activity.WebSiteActivity;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.entity.Blog;
import com.dandian.pocketmoodle.entity.Comment;
import com.dandian.pocketmoodle.entity.ImageItem;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.DialogUtility;
import com.dandian.pocketmoodle.util.FileUtility;
import com.dandian.pocketmoodle.util.IntentUtility;
import com.dandian.pocketmoodle.util.MyImageGetter;
import com.dandian.pocketmoodle.util.MyTagHandler;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.dandian.pocketmoodle.util.TimeUtility;
import com.dandian.pocketmoodle.widget.NonScrollableListView;

/**
 * 通知
 */
public class SchoolBlogDetailFragment extends Fragment {
	private String TAG = "SchoolNoticeDetailFragment";
	private Button btnLeft,btnDel;
	private String title,interfaceName,display;
	private LinearLayout loadingLayout;
	private ScrollView contentLayout;
	private LinearLayout failedLayout,commentLayout;
	private TextView tvRight;
	private LinearLayout lyRight;
	private AQuery aq;
	private Blog blog;
	private String rightBtn,rightBtnUrl,deleteBtn,deleteBtnUrl,rightSubmit;
	private CommentAdapter commentAdapter;
	private NonScrollableListView listview1;
	private TextView contentView;
	private Dialog dialog;
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
							rightBtn=jo.optString("右上按钮");
							rightBtnUrl=jo.optString("右上按钮URL");
							rightSubmit=jo.optString("右上按钮Submit");
							deleteBtn=jo.optString("删除按钮");
							deleteBtnUrl=jo.optString("删除按钮URL");
							blog = new Blog(jo);
							initData();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			case 1:
				showProgress(false);
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
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						if(res.equals("成功")){
							int id=jo.optInt("编号");
							for(Comment item:blog.getCommentList())
							{
								if(item.getId()==id)
								{
									blog.getCommentList().remove(item);
									break;
								}
							}
							commentAdapter.notifyDataSetChanged();
							if(blog.getCommentList().size()==0)
								commentLayout.setVisibility(View.GONE);
							else
								commentLayout.setVisibility(View.VISIBLE);
								
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case 2:
				showProgress(false);
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
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						if(res.equals("成功")){
							Intent aintent = new Intent();
							getActivity().setResult(1,aintent);
							getActivity().finish();
						}
						else
							AppUtility.showErrorToast(getActivity(), res);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			
			}
		
		}
	};

	
	public SchoolBlogDetailFragment() {

	}
	public static final Fragment newInstance(String title, String interfaceName,String display){
    	Fragment fragment = new SchoolBlogDetailFragment();
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
		View view = inflater.inflate(R.layout.school_blog_detail_fragment,
				container, false);
				
		aq = new AQuery(view);
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		btnDel=(Button)view.findViewById(R.id.delButton);
		
		loadingLayout = (LinearLayout) view.findViewById(R.id.data_load);
		contentLayout = (ScrollView) view.findViewById(R.id.content_layout);
		commentLayout= (LinearLayout) view.findViewById(R.id.commentLayout);
		failedLayout = (LinearLayout) view.findViewById(R.id.empty_error);
		listview1=(NonScrollableListView) view.findViewById(R.id.listView1);
		contentView=(TextView)view.findViewById(R.id.tv_content);
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
				getNoticeDetail(true);
			}
		});
		
		lyRight = (LinearLayout) view.findViewById(R.id.layout_btn_right);
		tvRight=(TextView)view.findViewById(R.id.tv_right);
		if(blog==null)
			blog=new Blog();
		commentAdapter=new CommentAdapter();
		listview1.setAdapter(commentAdapter);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//AppUtility.showToastMsg(getActivity(), "正在获取数据");
		getNoticeDetail(true);
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
		
		aq.id(R.id.tv_notice_title).text(blog.getTitle());
		aq.id(R.id.tv_time).text(blog.getPosttime());
		String content = blog.getContent();
		//aq.id(R.id.tv_content).text(Html.fromHtml(content,imgGetter,null));
		Spanned spanned = Html.fromHtml(content, new MyImageGetter(getActivity(),contentView), new MyTagHandler(getActivity()));		
		contentView.setText(spanned);		
		
		
		if(blog.getFujianList()!=null && blog.getFujianList().size()>0)
		{
			contentView.append(getString(R.string.attachment)+"：\r\n");
			for(int i=0;i<blog.getFujianList().size();i++)
			{
				ImageItem jo;
				try {
					jo = blog.getFujianList().get(i);
					
					SpannableString ss = new SpannableString(jo.getName());
			        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, ss.length(),
			                   Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			        ss.setSpan(new MyURLSpan(jo.getDownAddress()), 0, ss.length(),
			                   Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			        
			        contentView.append(ss);
			        contentView.append("\r\n\r\n");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				
			}

		}
		aq.id(R.id.tv_content).getTextView().setMovementMethod(LinkMovementMethod.getInstance());
		if(rightBtn!=null && rightBtn.length()>0 && rightBtn!="null")
		{
			tvRight.setText(rightBtn);
			tvRight.setVisibility(View.VISIBLE);
			lyRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(rightBtnUrl.substring(0, 4).equalsIgnoreCase("http"))
					{
						Intent contractIntent = new Intent(getActivity(),WebSiteActivity.class);
						String username=PrefUtility.get(Constants.PREF_LOGIN_NAME, "");
						username=username.split("@")[0];
						String password=PrefUtility.get(Constants.PREF_LOGIN_PASS, "");
						String url=rightBtnUrl+"&username="+username+"&password="+password;
						contractIntent.putExtra("url",url);
						contractIntent.putExtra("title",title);
						getActivity().startActivity(contractIntent);
					}
					else
					{
						if(rightSubmit!=null && rightSubmit.equals("是"))
						{
							submitButtonClick(rightBtnUrl);
						}
						else
							openDiaoChaWenJuan(rightBtnUrl);
					}
					
				}
			});
		}
		else
		{
			tvRight.setVisibility(View.GONE);
			lyRight.setOnClickListener(null);
		}
		if(blog.getCommentList().size()>0)
		{
			commentLayout.setVisibility(View.VISIBLE);
			listview1.setVisibility(View.VISIBLE);
			commentAdapter.notifyDataSetChanged();
			//AppUtility.setListViewHeightBasedOnChildren(listview1, 50);
			/*
			new Handler().postDelayed(new Runnable(){   
			    public void run() {   
			    	
			    }   
			 }, 50);
			 */  
			aq.id(R.id.comments_count).text(String.valueOf(blog.getCommentList().size()));
		}
		else
		{
			commentLayout.setVisibility(View.GONE);
			listview1.setVisibility(View.GONE);
		}
		
		
		if(deleteBtn!=null && deleteBtn.length()>0)
		{
			btnDel.setVisibility(View.VISIBLE);
			btnDel.setText(deleteBtn);
			btnDel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.ifconfirm)+deleteBtn+"?") 
				    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { 
				 
				        @Override 
				        public void onClick(DialogInterface dialog, int which) { 
				        
				        	deleteBlog();
				 
				        } 
				    }) 
				    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { 
				 
				        @Override 
				        public void onClick(DialogInterface dialog, int which) { 

				        } 
				    }).show(); 
					
				}
			});
		}
	}
	private void openDiaoChaWenJuan(String url)
	{
		Intent intent =new Intent(getActivity(),SchoolDetailActivity.class);
		intent.putExtra("templateName", "调查问卷");
		int pos=interfaceName.indexOf("?");
		String preUrl=interfaceName;
		if(pos>-1)
			preUrl=interfaceName.substring(0, pos);
		intent.putExtra("interfaceName", preUrl+url);
		intent.putExtra("title", title);
		intent.putExtra("status", "进行中");
		intent.putExtra("autoClose", "是");
		startActivityForResult(intent, 101);
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
							msg.what = 2;
							msg.obj = response;
							mHandler.sendMessage(msg);
						}
					});
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
	public void getNoticeDetail(boolean flag) {
		showProgress(flag);
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
	
	public class CommentAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return blog.getCommentList().size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return blog.getCommentList().get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh;
			if (convertView == null) 
			{		
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_album_comment, null);
				
				vh=new ViewHolder();
				vh.iv_icon=(ImageView)convertView.findViewById(R.id.iv_icon);
				vh.tv_title=(TextView)convertView.findViewById(R.id.tv_title);
				vh.tv_left=(TextView)convertView.findViewById(R.id.thieDescription);
				convertView.setTag(vh);

			}
			else
			{
				vh = (ViewHolder) convertView.getTag();
			}
			/*
			ImageOptions options = new ImageOptions();
			options.memCache=false;
			options.targetWidth=100;
			options.round = 50;
			aq.id(vh.iv_icon).image(commentList.get(position).getFromHeadUrl(), options);
			*/
			Comment comment=blog.getCommentList().get(position);
			ImageOptions option=new ImageOptions();
			option.targetWidth=230;
			option.round=115;
			aq.id(vh.iv_icon).image(comment.getAvater(),option);
			
			String timeStr=TimeUtility.cleverTimeString(comment.getPosttime(),getActivity());
			if(comment.getReply()!=null && comment.getReply().length()>0)
				timeStr+=" "+comment.getReply();
			vh.tv_left.setText(timeStr);
			vh.iv_icon.setTag(comment);
			vh.iv_icon.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),
							ShowPersonInfo.class);
					Comment cm=(Comment)v.getTag();
					intent.putExtra("studentId", cm.getUsercode());
					startActivity(intent);
				}
				
			});
			
			vh.tv_title.setText(comment.getUsername()+":"+comment.getContent());
			
			
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ImageView iv=(ImageView) v.findViewById(R.id.iv_icon);
					final Comment cm=(Comment) iv.getTag();
					String hostId=PrefUtility.get(Constants.PREF_CHECK_HOSTID, "");
					final String[] moreAction;
					if(hostId.equals(cm.getUsercode()))
					{
						moreAction=new String[]{getString(R.string.delete),getString(R.string.cancel)};
						Builder builder = new AlertDialog.Builder(getActivity());  
						builder.setCancelable(true);
				        builder.setTitle("");  
				        //builder.setIcon(R.drawable.dialog);  
				        DialogInterface.OnClickListener listener =   
				            new DialogInterface.OnClickListener() {  
				                  
				                @Override  
				                public void onClick(DialogInterface dialogInterface,   
				                        int which) {  
				                	if(moreAction[which].equals(getString(R.string.delete)))
				                	{
				                		sendCommentAction(cm,"删除评论");
				                	}
				                	dialogInterface.dismiss();
				                		
				                }  
				            };  
				        builder.setItems(moreAction, listener);  
				        AlertDialog dialog = builder.create();  
				        dialog.show();
					}
					else
					{
						if(cm.getReplyUrl()!=null && cm.getReplyUrl().length()>0)
						{
							moreAction=new String[]{getString(R.string.reply),getString(R.string.cancel)};
							Builder builder = new AlertDialog.Builder(getActivity());  
							builder.setCancelable(true);
					        builder.setTitle("");  
					        //builder.setIcon(R.drawable.dialog);  
					        DialogInterface.OnClickListener listener =   
					            new DialogInterface.OnClickListener() {  
					                  
					                @Override  
					                public void onClick(DialogInterface dialogInterface,   
					                        int which) {  
					                	if(moreAction[which].equals(getString(R.string.reply)))
					                	{
					                		//sendCommentAction(cm,"回复评论");
					                		openDiaoChaWenJuan(cm.getReplyUrl());
					                	}
					                	dialogInterface.dismiss();
					                		
					                }  
					            };  
					        builder.setItems(moreAction, listener);  
					        AlertDialog dialog = builder.create();  
					        dialog.show();
						}
					}
					
				}
				
			});
			return convertView;
		}
		public class ViewHolder {
			public TextView tv_title;
			public ImageView iv_icon;
			public TextView tv_left;
			
			
		}
		
	}
	private void sendCommentAction(Comment ami,String action)
	{
		
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
		String[] urlarray=interfaceName.split("\\?");
		String url=urlarray[0];
		CampusAPI.getSchoolItem(params, url+ami.getDeleteUrl(),new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
				
			}

			@Override
			public void onComplete(String response) {
				Message msg = new Message();
				msg.what = 1;
				msg.obj = response;
				mHandler.sendMessage(msg);
				
			}
		});
	}
	private void deleteBlog()
	{
		showProgress(true);
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
		String[] urlarray=interfaceName.split("\\?");
		String url=urlarray[0];
		CampusAPI.getSchoolItem(params, url+deleteBtnUrl,new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
				
			}

			@Override
			public void onComplete(String response) {
				Message msg = new Message();
				msg.what = 2;
				msg.obj = response;
				mHandler.sendMessage(msg);
				
			}
		});
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		case 1:
			getNoticeDetail(true);
		    break;
		default:
		    break;
		}
	}
}
