package com.dandian.pocketmoodle.activity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.provider.MediaStore;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.dandian.pocketmoodle.CampusApplication;
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.db.DatabaseHelper;
import com.dandian.pocketmoodle.entity.ContactsMember;
import com.dandian.pocketmoodle.entity.ImageItem;
import com.dandian.pocketmoodle.entity.User;
import com.dandian.pocketmoodle.fragment.SchoolBlogDetailFragment.MyURLSpan;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.DialogUtility;
import com.dandian.pocketmoodle.util.FileUtility;
import com.dandian.pocketmoodle.util.HttpMultipartPostToMoodle;
import com.dandian.pocketmoodle.util.ImageUtility;
import com.dandian.pocketmoodle.util.IntentUtility;
import com.dandian.pocketmoodle.util.MyImageGetter;
import com.dandian.pocketmoodle.util.MyTagHandler;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;



public class ShowPersonInfo extends Activity {
	public static final int REQUEST_CODE_TAKE_PICTURE = 2;// //设置图片操作的标志
	public static final int REQUEST_CODE_TAKE_CAMERA = 1;// //设置拍照操作的标志
	private String studentId;
	private int courseId;
	private String userImage;
	AQuery aq;
	DatabaseHelper database;
	MyAdapter adapter;
	Button changeheader;
	private String picturePath;
	private String userDomain;
	private Dao<User, Integer> userDao;
	private User user;
	private Button btnSendMsg,btnAddLink;
	private ProgressDialog loadingDlg;
	JSONObject userObj;
	JSONArray keyList;
	
	
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, com.dandian.pocketmoodle.db.DatabaseHelper.class);
		}
		return database;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_info);
		studentId = getIntent().getStringExtra("studentId");
		courseId  = getIntent().getIntExtra("courseId",1);
		userImage = getIntent().getStringExtra("userImage");
		try {
			userDao = getHelper().getUserDao();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RelativeLayout nav_bar=(RelativeLayout) findViewById(R.id.headerlayout);
		int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			nav_bar.setBackgroundColor(color);
		
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();
		userDomain=PrefUtility.get(Constants.PREF_SCHOOL_DOMAIN,"");
		btnSendMsg=(Button) findViewById(R.id.btnSendMsg);
		btnAddLink=(Button) findViewById(R.id.btnAddLink);
		Button page_exit=(Button) findViewById(R.id.page_exit);
		
		if(studentId.equals(user.getId()))
		{
			changeheader= (Button) findViewById(R.id.bt_changeHeader);
			changeheader.setVisibility(View.VISIBLE);
			changeheader.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					showGetPictureDiaLog();
				}
				
			});
			btnSendMsg.setVisibility(View.GONE);
			btnAddLink.setVisibility(View.GONE);
			page_exit.setVisibility(View.VISIBLE);
		}
		else
		{
			btnSendMsg.setVisibility(View.VISIBLE);
			btnAddLink.setVisibility(View.VISIBLE);
			page_exit.setVisibility(View.GONE);
		}
		
		aq = new AQuery(this);
		userObj=new JSONObject();
		keyList=new JSONArray();
		initContent();
		getUserInfo();
	}
	
	
	
	private void initContent() {
		ImageOptions options = new ImageOptions();
        options.memCache=false;
        options.fileCache=false;
        options.fallback=R.drawable.ic_launcher;
		options.targetWidth=200;
		options.round = 100;
		aq.id(R.id.iv_pic).image(userImage,options);
		
		
		aq.id(R.id.tv_name).text(userObj.optString("姓名"));
		if(userObj.optString("角色名称")!=null && userObj.optString("角色名称").length()>0)
		{
			aq.id(R.id.user_type).getTextView().setVisibility(View.VISIBLE);
			aq.id(R.id.user_type).text("("+userObj.optString("角色名称")+")");
		}
		else
			aq.id(R.id.user_type).getTextView().setVisibility(View.GONE);
		
		if(userObj.optBoolean("是否联系人"))
		{
			btnAddLink.setCompoundDrawablesWithIntrinsicBounds(R.drawable.removelink, 0, 0, 0);
			btnAddLink.setText(R.string.removelink);
		}
		else
		{
			btnAddLink.setCompoundDrawablesWithIntrinsicBounds(R.drawable.addlink, 0, 0, 0);
			btnAddLink.setText(R.string.addlink);
		}
		//else
		//	aq.id(R.id.user_type).text(R.string.moodleuser);
		aq.id(R.id.setting_tv_title).text(R.string.userinfo);
		
		btnAddLink.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String action;
				if(userObj.optBoolean("是否联系人"))
					action="removeLinkman";
				else
					action="addLinkman";
				loadingDlg=ProgressDialog.show(ShowPersonInfo.this, null, getString(R.string.data_loading_progress),true);
				loadingDlg.show();
				String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
				JSONObject jo = new JSONObject();
				try {
					jo.put("用户较验码", checkCode);
					jo.put("userid", studentId);
					jo.put("action", action);
					jo.put("function", "getUserInfo");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				CampusAPI.httpPostToDandian(jo, mHandler, 3);
				
			}
			
		});
		aq.id(R.id.back).clicked(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		aq.id(R.id.iv_pic).clicked(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtility.showImageDialog(ShowPersonInfo.this,userImage);
				
			}
			
		});
		btnSendMsg.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ShowPersonInfo.this,ChatMsgActivity.class);
				intent.putExtra("toid", studentId);
				intent.putExtra("type", "消息");
				intent.putExtra("toname", userObj.optString("姓名"));
				intent.putExtra("userImage", userObj.optString("用户头像"));
				startActivity(intent);
			}
			
		});
		aq.id(R.id.page_exit).clicked(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ShowPersonInfo.this);
				builder.setMessage(R.string.confirmlogout)
				       .setCancelable(false)
				       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   ((CampusApplication)getApplicationContext()).setLoginUserObj(null);
								
								PrefUtility.put(Constants.PREF_INIT_BASEDATE_FLAG,
										false);
								
								PrefUtility
										.put(Constants.PREF_INIT_CONTACT_FLAG, false);
								PrefUtility.put(Constants.PREF_SELECTED_WEEK, 0);
								PrefUtility.put(Constants.PREF_SITE_URL, "");
								PrefUtility.put(Constants.PREF_LOGIN_NAME, "");
								PrefUtility.put(Constants.PREF_LOGIN_PASS, "");
								WebSiteActivity.loginDate=null;
								Intent intent = new Intent(ShowPersonInfo.this,
										LoginActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										| Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
								System.exit(0);
								dialog.dismiss();
				           }
				       })
				       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		
		adapter=new MyAdapter(this);
		aq.id(R.id.listView1).adapter(adapter);
		
	}
	
	public class MyAdapter extends BaseAdapter{
		 
        private LayoutInflater mInflater;
        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return keyList.length();
        }
 
        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }
 
        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
             
            ViewHolder holder = null;
            
            if (convertView == null) {
                 
                holder=new ViewHolder();  
                 
                convertView = mInflater.inflate(R.layout.list_left_right, null);
                holder.title = (TextView)convertView.findViewById(R.id.left_title);
                holder.info = (TextView)convertView.findViewById(R.id.right_detail);
                holder.info.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
                holder.private_album = (LinearLayout)convertView.findViewById(R.id.private_album);
                holder.imageViews=new ImageView[4];
                holder.imageViews[0]= (ImageView)convertView.findViewById(R.id.theImage);
                holder.imageViews[1] = (ImageView)convertView.findViewById(R.id.imageView2);
                holder.imageViews[2] = (ImageView)convertView.findViewById(R.id.imageView3);
                holder.imageViews[3] = (ImageView)convertView.findViewById(R.id.imageView4);
                holder.bt_changeNumber= (Button)convertView.findViewById(R.id.bt_changeNumber);
                convertView.setTag(holder);
                 
            }else {
                 
                holder = (ViewHolder)convertView.getTag();
            }
            JSONObject keyItem=null;
			try {
				keyItem = keyList.getJSONObject(position);
		            
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			if(keyItem==null)
				keyItem=new JSONObject();
			String key=keyItem.optString("name");
			holder.title.setText(key);
            String keytype=keyItem.optString("type");
            String content=userObj.optString(key);
            holder.info.setText("");
            if(keytype.equals("course"))
            {
            	
            	if(content!=null && content.length()>0)
        		{
            		JSONArray courseArray=null;
            		try {
						 courseArray=new JSONArray(content);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
        			//contentView.append(getString(R.string.attachment)+"：\r\n");
        			for(int i=0;i<courseArray.length();i++)
        			{
        				try {
        					
        					JSONObject itemObj=courseArray.getJSONObject(i);
        					String courseName=itemObj.optString("fullname");
        					String detailUrl=itemObj.optString("DetailUrl");
        					SpannableString ss = new SpannableString(courseName);
        			        //ss.setSpan(new StyleSpan(Typeface.NORMAL), 0, ss.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        			        ss.setSpan(new MyURLSpan(detailUrl), 0, ss.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        			        
        			        holder.info.append(ss);
        			        if(i<courseArray.length()-1)
        			        	holder.info.append("\r\n\r\n");
        				} catch (Exception e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        			
        			}
        			holder.info.setMovementMethod(LinkMovementMethod.getInstance());

        		}
            }
            else if(keytype.equals("description"))
            {
            	Spanned spanned = Html.fromHtml(content, new MyImageGetter(ShowPersonInfo.this,holder.info), null);
            	holder.info.setText(spanned);
            	holder.info.setMovementMethod(LinkMovementMethod.getInstance());
            }
            else
            	holder.info.setText(content);
            holder.bt_changeNumber.setVisibility(View.GONE);
            holder.private_album.setVisibility(View.GONE);
            return convertView;
        }
        public final class ViewHolder{
          
            public TextView title;
            public TextView info;
            public LinearLayout private_album;
            public ImageView[] imageViews;
            public Button bt_changeNumber;
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
			Intent intent=new Intent(ShowPersonInfo.this,SchoolDetailActivity.class);
			intent.putExtra("templateName", "博客");
			intent.putExtra("interfaceName",mUrl);
			intent.putExtra("title", getString(R.string.course_summary));
			startActivity(intent);
	    }
		
	}
	private void getUserInfo() {
		loadingDlg=ProgressDialog.show(this, null, getString(R.string.data_loading_progress),true);
		loadingDlg.show();
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("userid", studentId);
			jo.put("courseId", courseId);
			jo.put("function", "getUserInfo");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPostToDandian(jo, mHandler, 1);
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@SuppressLint("SimpleDateFormat")
		@Override
		public void handleMessage(Message msg) {
			
			String result = "";
			switch (msg.what) 
			{
				case -1:// 请求失败
					if(loadingDlg!=null)
						loadingDlg.dismiss();
					AppUtility.showErrorToast(ShowPersonInfo.this,
							msg.obj.toString());
					break;
				case 1:
					if(loadingDlg!=null)
						loadingDlg.dismiss();
					result = msg.obj.toString();
					try {
						JSONObject jo = new JSONObject(result);
						if (jo.optString("结果").equals("失败"))
							AppUtility.showErrorToast(ShowPersonInfo.this, jo.optString("error"));
						else
						{
							userObj=jo.getJSONObject("个人资料");
							userImage=userObj.optString("用户头像");
							keyList=jo.getJSONArray("显示字段");
							initContent();
						}
							
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
					
				case 2:
					
					result = msg.obj.toString();
					
					try {
						JSONObject jo = new JSONObject(result);
						if (jo.optString("结果").equals("失败"))
							AppUtility.showErrorToast(ShowPersonInfo.this, jo.optString("error"));
						else
						{
							AppUtility.showToastMsg(ShowPersonInfo.this, getString(R.string.uploadSuccess));
							userImage=jo.getString("newpicurl");
							user.setUserImage(userImage);
							try {
								userDao.update(user);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							initContent();
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case 3:
					if(loadingDlg!=null)
						loadingDlg.dismiss();
					result = msg.obj.toString();
					
					try {
						JSONObject jo = new JSONObject(result);
						if (jo.optString("结果").equals("失败"))
							AppUtility.showErrorToast(ShowPersonInfo.this, jo.optString("error"));
						else
						{
							userObj.put("是否联系人",jo.optBoolean("是否联系人"));
							initContent();
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
					}
					break;	
				case 5:
					
					Bundle	upLoadbundle = (Bundle) msg.obj;
					result = upLoadbundle.getString("result");
					
					try {
						JSONArray ja = new JSONArray(result);
						JSONObject jo=ja.getJSONObject(0);
						if(jo!=null && jo.optString("itemid").length()>0)
						{
							//userImage=userDomain+"/pluginfile.php/"+jo.optString("contextid")+"/user/icon/clean/"+URLEncoder.encode(jo.optString("filename"),"UTF-8");
							updateServerUserInfo(jo);
						}else{
							DialogUtility.showMsg(ShowPersonInfo.this, getString(R.string.failed)+jo.optString("error"));
						}
					}catch (Exception e) {
						AppUtility.showToastMsg(ShowPersonInfo.this, e.getMessage());
						e.printStackTrace();
					}	
					break;
			}
		}
	};
	
	private void showGetPictureDiaLog() {
		View view = getLayoutInflater()
				.inflate(R.layout.view_get_picture, null);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		TextView byCamera = (TextView) view.findViewById(R.id.tv_by_camera);
		TextView byLocation = (TextView) view.findViewById(R.id.tv_by_location);

		final AlertDialog ad = new AlertDialog.Builder(this).setView(view)
				.create();

		Window window = ad.getWindow();
		window.setGravity(Gravity.BOTTOM);// 在底部弹出
		window.setWindowAnimations(R.style.CustomDialog);
		ad.show();
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ad.dismiss();
			}
		});
		byCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getPictureByCamera();
				ad.dismiss();
			}
		});
		byLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getPictureFromLocation();
				ad.dismiss();
			}
		});
	}
	private synchronized void getPictureByCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			AppUtility.showToastMsg(this, getString(R.string.Commons_SDCardErrorTitle));
			return;
		}
		picturePath = FileUtility.getRandomSDFileName("jpg");
		
		File mCurrentPhotoFile = new File(picturePath);

		Uri uri = Uri.fromFile(mCurrentPhotoFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, REQUEST_CODE_TAKE_CAMERA);
	}
	
	public void getPictureFromLocation() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
			/*
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
			*/
			Intent intent; 
			intent = new Intent(Intent.ACTION_PICK, 
			                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); 
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
		} else {
			AppUtility.showToastMsg(this, getString(R.string.Commons_SDCardErrorTitle));
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_TAKE_CAMERA: // 拍照返回
			if (resultCode == RESULT_OK) {
				if(picturePath!=null)
					rotateAndCutImage(new File(picturePath));
				else
					AppUtility.showToastMsg(this, getString(R.string.getcamerafailed));
			}
			break;
		case REQUEST_CODE_TAKE_PICTURE:
			if (data != null) {
				
				//picturePath = data.getStringExtra("filepath");
				//String myImageUrl = data.getDataString();
				//Uri uri = Uri.parse(myImageUrl);
				Uri uri = data.getData();
				String[] pojo  = { MediaStore.Images.Media.DATA };
				CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null,null, null); 
				Cursor cursor = cursorLoader.loadInBackground();
				if(cursor!=null)
				{
					cursor.moveToFirst(); 
					picturePath = cursor.getString(cursor.getColumnIndex(pojo[0])); 
					String tempPath =FileUtility.getRandomSDFileName("jpg");
					if(FileUtility.copyFile(picturePath,tempPath))
						rotateAndCutImage(new File(tempPath));
					else
						AppUtility.showErrorToast(this,getString(R.string.failedcopytosdcard));
				}
				else
					AppUtility.showErrorToast(this, getString(R.string.getcamerafailed));
			}
			break;
		case 3:
			if (resultCode == 200 && data != null) {
				
				String picPath = data.getStringExtra("picPath");
				SubmitUploadFile(picPath);
			}
		default:
			break;
		}
	}
	private void rotateAndCutImage(final File file) {
		if(!file.exists()) return;
		if(AppUtility.formetFileSize(file.length()) > 5242880*2){
			AppUtility.showToastMsg(this, String.format(getString(R.string.maxfilesize),10));
		}else{
			
			ImageUtility.rotatingImageIfNeed(file.getAbsolutePath());
			Intent intent=new Intent(this,CutImageActivity.class);
			intent.putExtra("picPath", file.getAbsolutePath());
			startActivityForResult(intent,3);
		}
	}
	
	public void SubmitUploadFile(String picPath){
		CampusParameters params = new CampusParameters();
		
		final String localfile = picPath;
		
		String token=PrefUtility.get(Constants.PREF_CHECK_TOKEN,"");
	
		params.add("token",token);
		params.add("filearea","draft");
		params.add("pic", picPath);
		
		String url=userDomain+"/webservice/upload.php";
		
		HttpMultipartPostToMoodle post = new HttpMultipartPostToMoodle(this, url, params){
			@Override  
		    protected void onPostExecute(String result) {  
				Bundle bundle = new Bundle();
				bundle.putString("result", result);
				bundle.putString("localfile", localfile);
				Message msg = new Message();
				msg.what = 5;
				msg.obj = bundle; 
				mHandler.sendMessage(msg);	
				this.pd.dismiss();
		    }
		};  
        post.execute();
	}
	private void updateServerUserInfo(JSONObject jo)
	{
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		try {
			jo.put("用户较验码", checkCode);
			jo.put("action", "changeAvatar");
			jo.put("function", "getUserInfo");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPostToDandian(jo, mHandler, 2);

	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("picturePath", picturePath);
        savedInstanceState.putString("studentId", studentId);
        savedInstanceState.putString("userImage", userImage);
        
    }
 
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        picturePath = savedInstanceState.getString("picturePath");
        studentId = savedInstanceState.getString("studentId");
        userImage = savedInstanceState.getString("userImage");
        
    }
}
