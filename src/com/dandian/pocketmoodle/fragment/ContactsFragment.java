package com.dandian.pocketmoodle.fragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.activity.ChatMsgActivity;
import com.dandian.pocketmoodle.activity.ShowPersonInfo;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.entity.ContactsMember;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.ExpressionUtil;
import com.dandian.pocketmoodle.util.PrefUtility;
/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 联系人详细信息
 * 
 *  <br/>创建说明: 2013-12-16 下午5:45:42 zhuliang  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class ContactsFragment extends Fragment {
	
	
	private ExpandableListView expandableListView;
	public ExpandableAdapter expandableAdapter;
	public List<String> groupList;
	public List<List<ContactsMember>> childList;
	private LinearLayout initLayout;
	private AQuery aq;
	private static final String TAG = "ContactsFragment";
	
	public List<ContactsMember> memberList;
	public Map<String,String> chatFriendMap;
	static Dialog mLoadingDialog = null;

	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0 :
				showProgress(false);
	
				String result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) 
				{
					try 
					{
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("结果");
						if (res.equals("失败")) {
							AppUtility.showToastMsg(getActivity(), jo.optString("error"));
						} 
						else 
						{
							groupList.clear();
							childList.clear();
							memberList.clear();
							JSONArray ja=jo.optJSONArray("groupName");
							for(int i=0;i<ja.length();i++)
							{
								String groupName=ja.getString(i);
								groupList.add(groupName);
								JSONArray memberArray=jo.optJSONArray(groupName);
								List<ContactsMember> listMember = new ArrayList<ContactsMember>();
								for(int j=0;j<memberArray.length();j++)
								{
									JSONObject memberItem=memberArray.getJSONObject(j);
									ContactsMember contactsMember=new ContactsMember(memberItem);
									listMember.add(contactsMember);
									memberList.add(contactsMember);
								}
								childList.add(listMember);
								
							}
							initContent();
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
				
				default:
					break;
			}
		}
		
	};

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "--------------refresh is running--------------");
		View localView = inflater.inflate(R.layout.view_contacts, container, false);
		expandableListView = (ExpandableListView)localView.findViewById(R.id.contacts);
		initLayout = (LinearLayout) localView.findViewById(R.id.initlayout);
		
		groupList = new ArrayList<String>();
		childList = new ArrayList<List<ContactsMember>>();
		memberList = new ArrayList<ContactsMember>();
		getContracts();
		initContent();
		return localView;
	}
	
	private void showProgress(boolean progress) {
		if (progress) {
			expandableListView.setVisibility(View.GONE);
			initLayout.setVisibility(View.VISIBLE);
		} else {
			expandableListView.setVisibility(View.VISIBLE);
			initLayout.setVisibility(View.GONE);
		}
	}
	public void getContracts()
	{
		showProgress(true);
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("function", "getContacts");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPostToDandian(jo, mHandler, 0);
			
		
	}
	
	public class PinyinComparator implements Comparator<ContactsMember> {

		public int compare(ContactsMember o1, ContactsMember o2) {
			// 这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
			String o1Name=o1.getName().trim();
			String o2Name=o2.getName().trim();
			if(o1.getXingMing().trim().length()>0)
				o1Name=o1.getXingMing().trim().substring(0,1)+o1Name;
			if(o2.getXingMing().trim().length()>0)
				o2Name=o2.getXingMing().trim().substring(0,1)+o2Name;
			return o1Name.compareTo(o2Name);
			
		}

	
	}
	
	private void initContent(){
		Log.d(TAG, "--------------initContent is rinning-------------");
		System.out.println(groupList.size() + "/" + childList.size());
		
		expandableAdapter = new ExpandableAdapter(groupList, childList,chatFriendMap);
		expandableListView.setAdapter(expandableAdapter);
		if( groupList.size() == 1){
			expandableListView.expandGroup(0);
		}
		expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {
				for(int i = 0; i < expandableAdapter.getGroupCount(); i++){
					if(groupPosition != i && expandableListView.isGroupExpanded(i)){
						expandableListView.collapseGroup(i);
					}
				}
			}
		});
		
	}
	
	// 联系人数据适配器
		public class ExpandableAdapter extends BaseExpandableListAdapter {
			List<String> groupList = new ArrayList<String>();
			List<List<ContactsMember>> childList = new ArrayList<List<ContactsMember>>();
			Map<String,String> map = new HashMap<String, String>();
			
			public ExpandableAdapter(List<String> group,
					List<List<ContactsMember>> child,Map<String,String> map) {
				this.groupList = group;
				this.childList = child;
				this.map = map;
			}

			public void refresh(List<String> group,
					List<List<ContactsMember>> child,Map<String,String> map){
				this.groupList = group;
				this.childList = child;
				this.map = map;
				notifyDataSetChanged();
			}
			@Override
			public Object getChild(int groupPosition, int childPosition) {
				return this.childList.get(groupPosition).get(childPosition);
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				return childPosition;
			}

			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				ViewHolder holder = null;
				if (convertView == null) {
					holder = new ViewHolder();
					convertView = LayoutInflater.from(getActivity())
							.inflate(R.layout.view_expandablelist_child, null);
					holder.group = (LinearLayout)convertView.findViewById(R.id.contacts_info1);
					holder.photo = (ImageView) convertView.findViewById(R.id.photo);
					holder.name = (TextView) convertView.findViewById(R.id.child);
					holder.lastContentTV = (TextView)convertView.findViewById(R.id.signature);
					holder.callIV = (ImageView) convertView.findViewById(R.id.callIV);
					
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				final ContactsMember contactsMember = childList.get(groupPosition)
						.get(childPosition);
				aq = new AQuery(getActivity());
				if (contactsMember != null) {
					String toid = contactsMember.getNumber();
					String url = contactsMember.getUserImage();
					
					if(toid != null && !toid.trim().equals("") && map!=null && map.containsKey(toid)){
						holder.lastContentTV.setVisibility(View.VISIBLE);
						String msgContent = map.get(toid);
						SpannableString spannableString = ExpressionUtil
								.getExpressionString(getActivity(), msgContent);
						holder.lastContentTV.setText(spannableString);
						
					}else{
						holder.lastContentTV.setVisibility(View.GONE);
						holder.lastContentTV.setText("");
					}
					holder.callIV.setVisibility(View.GONE);
					
					//Log.d(TAG,"---------------------->contactsMember.getUserImage():"+url);
					ImageOptions options = new ImageOptions();
					options.round = 20;
					options.fallback = R.drawable.man;
					aq.id(holder.photo).image(url, options);
					holder.name.setText(contactsMember.getName().trim());
					
					holder.group.setOnClickListener(new OnClickListener() {
	
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),ChatMsgActivity.class);
							intent.putExtra("toid", contactsMember.getNumber());
							intent.putExtra("type", "消息");
							intent.putExtra("toname", contactsMember.getName());
							intent.putExtra("userImage", contactsMember.getUserImage());
							getActivity().startActivity(intent);
						}
					});
					holder.photo.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							
							
								Intent intent = new Intent(getActivity(),
										ShowPersonInfo.class);
								intent.putExtra("studentId", contactsMember.getNumber());
								intent.putExtra("userImage", contactsMember.getUserImage());
								startActivity(intent);
							
						}
						
					});
				}
				return convertView;
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				return this.childList.get(groupPosition).size();
			}

			@Override
			public Object getGroup(int groupPosition) {
				return this.groupList.get(groupPosition);
			}

			@Override
			public int getGroupCount() {
				return this.groupList.size();
			}

			@Override
			public long getGroupId(int groupPosition) {
				return groupPosition;
			}

			@Override
			public View getGroupView(final int groupPosition, final boolean isExpanded,
					View convertView, ViewGroup parent) {
				ViewHolder holder;
				if(convertView == null){
					holder = new ViewHolder();
					convertView = LayoutInflater.from(getActivity()).inflate(
							R.layout.view_expandablelist_group, null);
					holder.groupTV = (TextView) convertView
							.findViewById(R.id.group_name);
					holder.countTV = (TextView) convertView
							.findViewById(R.id.group_count);
					holder.groupIV = (ImageView) convertView.findViewById(R.id.group_image);
					holder.showMemberBT = (TextView)convertView.findViewById(R.id.show_member);
					
					convertView.setTag(holder);
				}else{
					holder = (ViewHolder) convertView.getTag();
				}
				
					holder.showMemberBT.setVisibility(View.GONE);
					holder.groupIV.setVisibility(View.GONE);
				
				holder.groupTV.setText(this.groupList.get(groupPosition));
				holder.countTV.setText(String.valueOf(this.childList.get(groupPosition)
						.size()) + getString(R.string.people));
				return convertView;
			}

			@Override
			public boolean hasStableIds() {
				return false;
			}

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition) {
				return false;
			}

		}
		
		class ViewHolder {
			LinearLayout group;
			ImageView photo,groupIV,callIV;
			TextView name,groupTV,countTV,lastContentTV;
			TextView showMemberBT;
		}
		
		/*
		private void searchChatContent(){
			chatFriendMap = new ConcurrentHashMap<String, ChatFriend>();
			try {
				chatFriendDao = getHelper().getChatFriendDao();
				List<ChatFriend> chatFriendList = chatFriendDao.queryForAll();
				ChatFriend chatFriend;
				String toid;
				for(int i = 0; i < chatFriendList.size(); i++){
					chatFriend = chatFriendList.get(i);
					toid = chatFriend.getToid();
					chatFriendMap.put(toid, chatFriend);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		*/
}
