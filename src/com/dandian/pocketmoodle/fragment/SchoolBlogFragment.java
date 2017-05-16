package com.dandian.pocketmoodle.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;

import com.androidquery.AQuery;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.activity.SchoolDetailActivity;
import com.dandian.pocketmoodle.activity.ShowPersonInfo;
import com.dandian.pocketmoodle.activity.WebSiteActivity;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.entity.AchievementItem;
import com.dandian.pocketmoodle.entity.Blog;
import com.dandian.pocketmoodle.entity.BlogsItem;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.MyImageGetter;
import com.dandian.pocketmoodle.util.MyTagHandler;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.dandian.pocketmoodle.util.ZLibUtils;
import com.dandian.pocketmoodle.widget.XListView;
import com.dandian.pocketmoodle.widget.XListView.IXListViewListener;

/**
 * 通知
 */
public class SchoolBlogFragment extends Fragment implements IXListViewListener {
	private String TAG = "SchoolNoticeFragment";
	private XListView myListview;
	private Button btnLeft;
	private TextView tvTitle;
	private LinearLayout lyLeft;
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private LinearLayout emptyLayout;
	
	private String interfaceName,title;
	private LayoutInflater inflater;
	private NoticeAdapter adapter;
	private List<Blog> noticesList=new ArrayList<Blog>();

	private int pagesize=10;
	private int page=0;
	AQuery aq;
	Button btn_right;
	LinearLayout lyRight;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				showProgress(false);
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
						final BlogsItem noticesItem = new BlogsItem(jo);
						Log.d(TAG, "--------noticesItem.getNotices().size():"
								+ noticesItem.getNotices().size());
						List<Blog> notices = noticesItem.getNotices();
						if(noticesItem.getTopLine1()!=null && noticesItem.getTopLine1().length()>0)
						{
							TextView tv_summary1=new TextView(getActivity());
							TextView tv_summary2=new TextView(getActivity());
							LinearLayout layout=new LinearLayout(getActivity());
							layout.setOrientation(LinearLayout.VERTICAL);
							LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT);
							layout.setBackgroundColor(Color.WHITE);
							layout.setLayoutParams(params);
							LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT);
							params1.setMargins(20,20,20,0);
							tv_summary1.setLayoutParams(params1);
							layout.addView(tv_summary1);
							tv_summary2.setLayoutParams(params1);
							layout.addView(tv_summary2);
							tv_summary1.setBackgroundColor(Color.WHITE);
							tv_summary2.setBackgroundColor(Color.WHITE);
							tv_summary2.setTextColor(Color.GRAY);
							tv_summary2.setTextSize(12);
							tv_summary2.setGravity(Gravity.RIGHT);
							Spanned spanned = Html.fromHtml(noticesItem.getTopLine1(), new MyImageGetter(getActivity(),tv_summary1), new MyTagHandler(getActivity()));
							tv_summary1.setText(spanned);
							tv_summary1.setMovementMethod(LinkMovementMethod.getInstance());
							if(noticesItem.getTopLine2()!=null && noticesItem.getTopLine2().length()>0)
								tv_summary2.setText(noticesItem.getTopLine2());
							myListview.setAdapter(null);
							myListview.addHeaderView(layout);
							myListview.setAdapter(adapter);
							
						}
						myListview.stopLoadMore();
						if(noticesItem.getTitle()!=null && noticesItem.getTitle().length()>0)
							tvTitle.setText(noticesItem.getTitle());
						for(Blog item:notices)
							noticesList.add(item);
						//if(notices.size()>0)
						adapter.notifyDataSetChanged();
						if(notices.size()<pagesize)
						{
							//AppUtility.showErrorToast(getActivity(), getString(R.string.loadedalldata));
							myListview.setPullLoadEnable(false);
						}
						if(noticesItem.getRightButton()!=null && noticesItem.getRightButton().length()>0)
						{
							btn_right.setVisibility(View.VISIBLE);
							btn_right.setTextSize(13);
							btn_right.setText(noticesItem.getRightButton());
							lyRight.setOnClickListener(new OnClickListener(){

								@Override
								public void onClick(View v) {
									Intent intent =new Intent(getActivity(),SchoolDetailActivity.class);
									intent.putExtra("templateName", "调查问卷");
									intent.putExtra("interfaceName", noticesItem.getRightButtonUrl());
									intent.putExtra("title", title);
									intent.putExtra("status", "进行中");
									intent.putExtra("autoClose", "是");
									startActivityForResult(intent, 101);
								}
							
							});
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
	public SchoolBlogFragment() {
		
	}
	public static final Fragment newInstance(String title, String interfaceName,String display){
    	Fragment fragment = new SchoolBlogFragment();
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
		
		
		interfaceName=getArguments().getString("interfaceName");
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.school_xlistview_fragment,
				container, false);
		RelativeLayout nav_bar=(RelativeLayout) view.findViewById(R.id.nav_bar);
		int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			nav_bar.setBackgroundColor(color);
		aq= new AQuery(view);
		myListview = (XListView) view.findViewById(R.id.my_listview);
		myListview.setDivider(getResources().getDrawable(R.color.transparent));
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		lyLeft = (LinearLayout) view.findViewById(R.id.layout_btn_left);
		tvTitle=(TextView) view.findViewById(R.id.tv_title);
		loadingLayout = (LinearLayout) view.findViewById(R.id.data_load);
		contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) view.findViewById(R.id.empty_error);
		emptyLayout = (LinearLayout) view.findViewById(R.id.empty);
		myListview.setEmptyView(emptyLayout);
		myListview.setPullRefreshEnable(false);
		myListview.setPullLoadEnable(true);
		myListview.setXListViewListener(this);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);
		adapter = new NoticeAdapter();
		tvTitle.setText(title);
		myListview.setAdapter(adapter);
		lyLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		//重新加载
		failedLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getNoticesList(true);
			}
		});
		btn_right=(Button)view.findViewById(R.id.btn_right);
		lyRight = (LinearLayout) view.findViewById(R.id.layout_btn_right);
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getNoticesList(true);
	}
	
	private void getNoticesList(boolean flag)
	{
		showProgress(flag);
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo=null;
		try {
			jo = new JSONObject(interfaceName);
			jo.put("用户较验码", checkCode);
			jo.put("page", page);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPostToDandian(jo, mHandler, 0);
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
	
	class NoticeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return noticesList.size();
		}

		@Override
		public Object getItem(int position) {
			return noticesList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (null == convertView) {
				convertView = inflater.inflate(R.layout.school_blog_item,
						parent, false);
				holder = new ViewHolder();

				holder.title = (TextView) convertView.findViewById(R.id.tv_title);
				holder.time = (TextView) convertView.findViewById(R.id.tv_time);
				holder.headImage = (ImageView) convertView.findViewById(R.id.iv_head);
				holder.content = (TextView) convertView.findViewById(R.id.tv_content);
				holder.name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.openDetail=(LinearLayout) convertView.findViewById(R.id.rl_detail);
				holder.image = (ImageView) convertView.findViewById(R.id.iv_image);
				holder.replys = (TextView) convertView.findViewById(R.id.tv_replys);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Blog notice = (Blog) getItem(position);
			
			//holder.openDetail.setBackgroundColor(Color.WHITE);
			ImageOptions option=new ImageOptions();
			option.targetWidth=230;
			option.round=115;
			aq.id(holder.headImage).image(notice.getAvater(),option);
			holder.headImage.setTag(notice.getUsername());
			holder.headImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							ShowPersonInfo.class);
					intent.putExtra("studentId", String.valueOf(v.getTag()));
					startActivity(intent);
				}
			});

			holder.name.setText(notice.getPoster());
			holder.title.setText(notice.getTitle());
			holder.time.setText(notice.getPosttime());
			holder.replys.setText(notice.getComments());
			AbstractAjaxCallback.setTimeout(30000);
			String imagurl = notice.getMainImage();
			Log.d(TAG, "----imagurl:" + imagurl);
			if (imagurl != null && !imagurl.equals("") && !imagurl.equals("null")) 
			{
				aq.id(holder.image).visibility(View.VISIBLE);
				aq.id(holder.image).image(imagurl);
			} else {
				aq.id(holder.image).visibility(View.GONE);
			}
			Spanned spanned = Html.fromHtml(notice.getContent(), null,null);
			holder.content.setText(spanned);
			//holder.content.setMovementMethod(LinkMovementMethod.getInstance());
			//holder.content.setText(notice.getContent());
			//查看详细信息
			holder.openDetail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(notice.getDetailUrl()!=null)
					{
						if(notice.getDetailUrl().length()>4 && notice.getDetailUrl().substring(0, 4).equalsIgnoreCase("http"))
						{
							Intent contractIntent = new Intent(getActivity(),WebSiteActivity.class);
							contractIntent.putExtra("url",notice.getDetailUrl());
							contractIntent.putExtra("title",title+"详情");
							getActivity().startActivity(contractIntent);
						}
						else
						{
							Intent intent =new Intent(getActivity(),SchoolDetailActivity.class);
							intent.putExtra("templateName", "博客");
							intent.putExtra("interfaceName", notice.getDetailUrl());
							intent.putExtra("title", title);
							startActivityForResult(intent,101);
						}
					}
				}
			});

			return convertView;
		}

		class ViewHolder {
			TextView title;
			TextView time;
			ImageView headImage;
			ImageView image;
			TextView content;
			TextView name;
			TextView replys;
			LinearLayout openDetail;
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		page++;
		getNoticesList(false);
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		case 1:
			noticesList.clear();
			page=0;
			getNoticesList(true);
		    break;
		default:
		    break;
		}
	}
	
}
