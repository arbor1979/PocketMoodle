package com.dandian.pocketmoodle.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.entity.ContactsMember;
import com.dandian.pocketmoodle.fragment.ContactsSelectFragment;
import com.dandian.pocketmoodle.fragment.ContactsSelectSearchFragment;
import com.dandian.pocketmoodle.fragment.ContactsSelectSearchFragment.MyListener;
import com.dandian.pocketmoodle.util.DialogUtility;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * �汾˵��: $id:$ <br/>
 * 
 * ����˵��: ��ϵ�˽���
 * 
 * <br/>
 * ����˵��: 2014-07-15 15:14:26 QiaoLin �����ļ�<br/>
 * 
 * 
 */
public class ContactsSelectActivity extends FragmentActivity implements MyListener  {
	static Button menu;
	static LinearLayout layout_menu;
	public static LinearLayout layout_refresh;
	private TextView cancel;
	private ViewGroup search_head;

	public static EditText search;
	private LinearLayout contacts;

	public static int STATUS = 0;
	private static final String TAG = "ContactsActivity";
	public static MyHandler mHandler;
	static ContactsSelectSearchFragment contactsSearchFragment;
	private DisplayMetrics dm;
	public static Dialog mLoadingDialog;
	ContactsSelectFragment mContactsFragment;
	RelativeLayout contactlayout;
	LinearLayout initlayout;
	Button selectOk;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "----------------onCreate-----------------------");
		dm = getResources().getDisplayMetrics();
		setContentView(R.layout.activity_contacts_select);
		
		contacts = (LinearLayout) findViewById(R.id.content);
		search = (EditText) findViewById(R.id.edit_search);
		mContactsFragment=(ContactsSelectFragment)getSupportFragmentManager().findFragmentById(R.id.contacts_list);

		mLoadingDialog = DialogUtility.createLoadingDialog(ContactsSelectActivity.this, getString(R.string.data_loading_progress));
		mHandler = new MyHandler();
		initViews();
		
		initSearch();

	
	}
	


	
	/**
	 * ��������: ��������
	 *
	 * @author zhuliang  2013-12-13 ����5:03:12
	 *
	 */
	@SuppressLint("NewApi")
	private void initSearch() {
		search.setFocusable(false);
		search.setFocusableInTouchMode(false);
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//�������ƶ���
				AnimationSet animationSet = new AnimationSet(true);
				TranslateAnimation translateAnimation = new TranslateAnimation(
						0, 0, contacts.getY(), contacts.getY() - 44 * dm.densityDpi/160);
				animationSet.addAnimation(translateAnimation);
				animationSet.setDuration(300);
				animationSet.setFillAfter(true);
				animationSet.setFillBefore(false);
				contacts.startAnimation(animationSet);
				
				List<ContactsMember> list=new ArrayList<ContactsMember>();
				for(List<ContactsMember> sublist:mContactsFragment.childSelectedList)
				{
					for(ContactsMember item:sublist)
						list.add(item);
				}
				
				contactsSearchFragment = ContactsSelectSearchFragment.newInstance(1, mContactsFragment.memberList,list);
				
				Message msg = new Message();
				msg.what = 0;
				mHandler.sendMessageDelayed(msg, 300);
			}
		});
	}
	
	// ��ʼ��Views
	private void initViews() {
		
		search_head = (ViewGroup) findViewById(R.id.search_head);
		
		search_head.getBackground().setAlpha(50);
		cancel = (TextView) findViewById(R.id.chat_btn_cancel);
		cancel.setVisibility(View.GONE);
		
		TextView tv_title = (TextView) findViewById(R.id.setting_tv_title);
		final Button bn_back = (Button) findViewById(R.id.back);
		tv_title.setText(R.string.selectlinkman);
		
		bn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(bn_back.getWindowToken(), 0);
				finish();
			}
		});
		selectOk=(Button)findViewById(R.id.confirm_sel);
		selectOk.setEnabled(false);
		selectOk.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mContactsFragment.selectedlist!=null)
				{
					String toid="";
					String toname="";
					
					for(ContactsMember item:mContactsFragment.selectedlist)
					{
						if(toid.length()==0)
						{
							toid=item.getUserNumber();
							toname=item.getName()+String.format(getString(R.string.etcpeople),mContactsFragment.selectedlist.size());
						}
						else
							toid+=","+item.getUserNumber();
					}
					Intent intent = new Intent(ContactsSelectActivity.this,ChatMsgActivity.class);
					intent.putExtra("toid", toid);
					intent.putExtra("type", "��Ϣ");
					intent.putExtra("toname", toname);
					intent.putExtra("userImage", "group");
					startActivity(intent);
				}
			}
			
		});
	}

	//	��Ϣ����
	@SuppressLint("HandlerLeak") 
	public class MyHandler extends Handler {
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				//������������
				Log.d(TAG, "-------isAdded----------" + contactsSearchFragment.isAdded());
				if (!contactsSearchFragment.isAdded()) {
					contactsSearchFragment.show(getSupportFragmentManager(),
							"search");
					getSupportFragmentManager().executePendingTransactions();
					Dialog dialog = contactsSearchFragment.getDialog();
					WindowManager wm = getWindowManager();
					Display display = wm.getDefaultDisplay();
					LayoutParams lp = dialog.getWindow().getAttributes();
					dialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
					lp.width = display.getWidth();
					Log.d(TAG, "----------height----------" + lp.height);
					dialog.getWindow().setGravity(Gravity.TOP);
					dialog.getWindow().setAttributes(lp);
					
					//���searchʱ���������������
					
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
					
					search.setInputType(0);
				}
				break;
			case 1:
				//��������
				Log.d(TAG, "--->  ִ�н������ط���...");
				
				AnimationSet animationSet1 = new AnimationSet(true);
				TranslateAnimation translateAnimation1 = new TranslateAnimation(
						0, 0, contacts.getY() + 44 * dm.densityDpi/160, contacts.getY());
				animationSet1.addAnimation(translateAnimation1);
				animationSet1.setFillAfter(true);
				animationSet1.setFillBefore(false);
				contacts.startAnimation(animationSet1);
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(search.getWindowToken(), 0); //ǿ�����ؼ���  
				
				break;
			
			default:
				break;
			}
		}

	}

	
	class ViewHolder {
		ImageView photo;
		TextView name;
	}


	@Override
	public void updateSelectedList(List<ContactsMember> selectList) {
		// TODO Auto-generated method stub
		
		for(int i=0;i<mContactsFragment.childSelectedList.size();i++)
			mContactsFragment.childSelectedList.get(i).clear();
		for(ContactsMember item:selectList)
		{
			
			String group=item.getVirtualClass();
			if(group==null)
				group=item.getClassName();
			for(int i=0;i<mContactsFragment.groupList.size();i++)
			{
				if(mContactsFragment.groupList.get(i).equals(group))
				{
					List<ContactsMember> list=mContactsFragment.childSelectedList.get(i);
					if(!list.contains(item))
						list.add(item);
					break;
				}
			}
			
			
		}

		mContactsFragment.updateViewBySelected();
	}
	
	
}
