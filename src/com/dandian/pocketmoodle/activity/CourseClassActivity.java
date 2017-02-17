package com.dandian.pocketmoodle.activity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.db.DatabaseHelper;
import com.dandian.pocketmoodle.entity.DownloadSubject;
import com.dandian.pocketmoodle.entity.LoadInfo;
import com.dandian.pocketmoodle.service.Downloader;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.DialogUtility;
import com.dandian.pocketmoodle.util.FileUtility;
import com.dandian.pocketmoodle.util.HttpMultipartPostToMoodle;
import com.dandian.pocketmoodle.util.ImageUtility;
import com.dandian.pocketmoodle.util.IntentUtility;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class CourseClassActivity extends Activity {
	private ListView mList;
	private CourseAdapter adapter;
	private Button btnLeft;
	private TextView tvRight;
	private ProgressBar proRight;
	private List<DownloadSubject> list = new ArrayList<DownloadSubject>();
	private DatabaseHelper database;
	private LayoutInflater inflater;
	private Dialog upLoadingDialog,dialog;
	private File mCurrentPhotoFile,mCurrentVideoFile;// ��������յõ���ͼƬ
	private AQuery aq;
	private JSONObject filenames = new JSONObject();
	private static final String TAG = "CourseClassActivity";
	private static final int CAMERA_WITH_DATA = 3023;
	private static final int PIC_Select_CODE_ImageFromLoacal = 3;
	private static final int PIC_Select_CODE_VideoFromLoacal = 4;
	private static final int PIC_Select_CODE_AudioFromLoacal = 5;
	private static final int PIC_Select_CODE_DocumentFromLoacal = 6;
	private static final int REQUEST_CODE_TAKE_VIDEO = 2;
	private DownloadSubject downloadSubject;
	private Dialog downloadDialog;
	
	// �̶�������ص����ֵ�·����SD��Ŀ¼��
	private String SD_PATH;
	private String curPath="/";
	// ��Ÿ���������
	private Map<String, Downloader> downloaders = new HashMap<String, Downloader>();
	// �������������Ӧ�Ľ�����
	private Map<String, ProgressBar> ProgressBars = new HashMap<String, ProgressBar>();
	// �������������Ӧ�Ľ�����
	private Map<String, TextView> progressesTexts = new HashMap<String, TextView>();
	private Map<String, ImageView> progressImages = new HashMap<String, ImageView>();
	private Map<String, Integer> downstates = new HashMap<String, Integer>(); // -1ֹͣ����
	private LinearLayout loadingLayout;
	
	private LinearLayout failedLayout;																			// 0��ͣ����
	private String userId;
	private String userDomain;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@SuppressLint("SimpleDateFormat")
		@Override
		public void handleMessage(Message msg) {
			String url = "";
			String result = "";
			String resultStr = "";
			switch (msg.what) {
			case -1:// ����ʧ��
				if (dialog != null) {
					dialog.dismiss();
				}
				AppUtility.showErrorToast(CourseClassActivity.this,
						msg.obj.toString());
				if(proRight.getVisibility()==View.VISIBLE){
					proRight.setVisibility(View.GONE);
					tvRight.setVisibility(View.VISIBLE);
				}
				break;
			case 1:// ���������Ϣ
				Bundle b = (Bundle) msg.obj;
				url = b.getString("urlstr");
				int length = b.getInt("compeleteSize");
				String localFile = b.getString("localFile");
				boolean canceldown = b.getBoolean("canceldown", false); // ȡ�����ر��
				ProgressBar bar = ProgressBars.get(url);
				TextView textProgress = progressesTexts.get(url);
				ImageView imageView = progressImages.get(url);
				if (bar != null) {
					// ���ý���������ȡ��length���ȸ���
					// bar.incrementProgressBy(length);
					bar.setProgress(length);
					int max = bar.getMax();
					float a = 0;
					String text = "";
					if (length > 1024 * 1024) {
						a = (float) length / 1024 / 1024;
						text = String.format("%.1f", a) + "M";
					}
					if (1024 <= length && length < 1024 * 1024) {
						a = (float) length / 1024;
						text = String.format("%.1f", a) + "K";
					}
					if (0 <= length && length < 1024) {
						text = String.format("%.1f", a) + "b";
					}
					String[] texts = textProgress.getText().toString()
							.split("/");
					textProgress.setText(text + "/" + texts[1]);
					if (length == max || canceldown) {
						// ������ɺ��������������map�е��������
						RelativeLayout progresslayout = (RelativeLayout) bar
								.getParent();
						ProgressBars.remove(url);
						downloaders.get(url).delete(url);
						downloaders.get(url).reset();
						downloaders.remove(url);

						FrameLayout layout = (FrameLayout) progresslayout
								.getParent().getParent();
						LinearLayout downloadInfo = (LinearLayout) layout
								.findViewById(R.id.downloadInfo);
						downloadInfo.setVisibility(View.VISIBLE);
						LinearLayout downloadProgress = (LinearLayout) layout
								.findViewById(R.id.downloadProgress);
						downloadProgress.setVisibility(View.INVISIBLE);

						if (!canceldown) {
							AppUtility.showToastMsg(CourseClassActivity.this,
									getString(R.string.downloadcomplete));
							downstates.put(url, 2);
							imageView
									.setImageResource(R.drawable.downloaded_course);
							/*
							String doneJsonStr = getDownloadDoneData(localFile);
							Log.d(TAG, "doneJsonStr:" + doneJsonStr);
							if (!"".equals(doneJsonStr) && doneJsonStr != null) {
								String doneBase64 = Base64.encode(doneJsonStr
										.getBytes());
								SubmitDownloadInfo(doneBase64, url);
							
							}
							*/
						} else {
							// ������״̬����ֹͣ
							downstates.put(url, -1);
						}
					}
				}
				break;
			case 2:// ɾ���������ļ�
				Bundle localBundle = (Bundle) msg.obj;
				url = localBundle.getString("url");
				localFile = localBundle.getString("pathfile");
				result = localBundle.getString("result");
				resultStr = "";
				try {
					resultStr = new String(
							Base64.decode(result.getBytes("GBK")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				Log.d(TAG, "url:" + url);
				Log.d(TAG, "resultStr:" + resultStr);

				try {
					JSONObject jo = new JSONObject(resultStr);
					if ("�ɹ�".equals(jo.opt("���"))) {
						AppUtility.showToastMsg(getApplicationContext(),
								getString(R.string.deleteRemoteFileSuc));
						File file = new File(localFile);
						if (file.exists()) {
							file.delete();
						}
						/*
						DownloadSubject downloadSubject = downloadSubjectDao
								.queryBuilder().where().eq("downAddress", url)
								.queryForFirst();
						downloadSubjectDao.delete(downloadSubject);
						try {
							downloadSubjectDao = getHelper()
									.getDownloadSubjectDao();
							list = downloadSubjectDao.queryForAll();
							
						} catch (SQLException e) {
							e.printStackTrace();
						}
						*/
						for(DownloadSubject item:list)
						{
							if(item.getType().equals("�ļ�") && item.getDownAddress().equals(url))
							{
								list.remove(item);
								break;
							}
						}
						adapter.setList(list);
						adapter.notifyDataSetChanged();
					}
					else
						AppUtility.showToastMsg(CourseClassActivity.this, jo.optString("���"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 3:// ��ȡ�μ��б�
				showProgress(false); 
				proRight.setVisibility(View.GONE);
				tvRight.setVisibility(View.VISIBLE);
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				try {
					JSONArray jo = new JSONArray(resultStr);
					list = DownloadSubject.toList(jo);
					if(!curPath.equals("/"))
					{
						DownloadSubject backitem=new DownloadSubject();
						backitem.setFileName(getString(R.string.parentFold));
						backitem.setType("��һ��");
						list.add(0, backitem);
					}
					if (list.size()>0) {
						Log.d(TAG, "list.size():" + list.size());
						
						adapter.setList(list);
						adapter.notifyDataSetChanged();
						
						Log.d(TAG, "adapter.getCount():" + adapter.getCount());
					}
					else
					{
						
						AppUtility.showToastMsg(CourseClassActivity.this, (String) getResources().getText(R.string.myfileisnull));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			
			case 5:
				if (dialog != null) {
					dialog.dismiss();
				}
				Bundle	upLoadbundle = (Bundle) msg.obj;
				result = upLoadbundle.getString("result");
				String localfile = upLoadbundle.getString("localfile");
				long filesize = upLoadbundle.getLong("filesize");
				Log.d(TAG, "result:"+result);
				/*
				try {
					resultStr = new String(Base64.decode(result.getBytes("GBK")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				Log.d(TAG, "resultStr:" + resultStr);
				*/
				try {
					JSONArray ja = new JSONArray(result);
					JSONObject jo=ja.getJSONObject(0);
					Log.d(TAG, "jo.optString(component):"+jo.optString("component"));
					if(jo!=null && "user".equals(jo.optString("component"))){
						DownloadSubject ds = new DownloadSubject();
						ds.setFileName(jo.optString("filename"));
						ds.setType("�ļ�");
						ds.setDownAddress(userDomain+"/pluginfile_dandian.php?/"+jo.optString("contextid")+"/user/private/0/"+URLEncoder.encode(ds.getFileName(),"UTF-8"));
						ds.setFilepath(jo.optString("filepath"));
						ds.setContextid(jo.optInt("contextid"));
						String newFileName=FileUtility.creatSDDir(SD_PATH)+jo.optString("filename");
						FileUtility.copyFile(localfile,newFileName);
						FileUtility.deleteFile(localfile);
						ds.setLocalfile(newFileName);
						ds.setFilesize(filesize);
						//downloadSubjectDao.create(ds);
						//����ʶ����ΪisModify=0
						//PreparedUpdate<DownloadSubject> preparedUpdateDownloadSubject = (PreparedUpdate<DownloadSubject>) downloadSubjectDao.updateBuilder().updateColumnValue("isModify", 0).where().eq("isModify", 1).prepare();
						//downloadSubjectDao.update(preparedUpdateDownloadSubject);
					
						DialogUtility.showMsg(CourseClassActivity.this, getString(R.string.uploadSuccess));
						//getDownloadSubject();
						list.add(ds);
						adapter.setList(list);
						adapter.notifyDataSetChanged();
						mList.setSelection(list.size());
					}else{
						String error=getString(R.string.uploadfail);
						if(jo.optString("error")!=null && jo.optString("error").length()>0)
							error=jo.optString("error");
						DialogUtility.showMsg(CourseClassActivity.this, error);
						
					}
				}catch (Exception e) {
					AppUtility.showToastMsg(CourseClassActivity.this, e.getMessage());
					e.printStackTrace();
				}	
				break;
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userId=PrefUtility.get(Constants.PREF_CHECK_USERID,"0");
		userDomain=PrefUtility.get(Constants.PREF_SCHOOL_DOMAIN,"");
		setContentView(R.layout.activity_view_classroom_course);
		Log.d(TAG, "--------------onCreate is running----------");
		aq = new AQuery(this);
		inflater = getLayoutInflater();
		downloadDialog = new Dialog(CourseClassActivity.this, R.style.dialog);
		mList = (ListView) findViewById(R.id.course_list);
		btnLeft = (Button) findViewById(R.id.btn_left);
		tvRight = (TextView) findViewById(R.id.tv_right);
		proRight = (ProgressBar) findViewById(R.id.pro_right);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);

		loadingLayout = (LinearLayout) findViewById(R.id.data_load);
		failedLayout = (LinearLayout) findViewById(R.id.empty_error);
		
		String title=getIntent().getStringExtra("title");
		String display=getIntent().getStringExtra("display");
		aq.id(R.id.tv_title).text(display);
		aq.id(R.id.tv_right).text(R.string.uploading);
		aq.id(R.id.tv_right).visibility(View.VISIBLE);
		//aq.id(R.id.tv_title).textSize(getResources().getDimensionPixelSize(R.dimen.text_size_micro));
		
		initListener();
		adapter = new CourseAdapter(list);
		mList.setAdapter(adapter);
		try {
			filenames.put("avi", R.drawable.ic_file_avi);
			filenames.put("mov", R.drawable.ic_file_avi);
			filenames.put("mp4", R.drawable.ic_file_avi);
			filenames.put("3gp", R.drawable.ic_file_avi);
			filenames.put("wmv", R.drawable.ic_file_avi);
			filenames.put("doc", R.drawable.ic_file_doc);
			filenames.put("gif", R.drawable.ic_file_gif);
			filenames.put("jpg", R.drawable.ic_file_jpg);
			filenames.put("jpeg", R.drawable.ic_file_jpg);
			filenames.put("png", R.drawable.ic_file_png);
			filenames.put("ppt", R.drawable.ic_file_ppt);
			filenames.put("rar", R.drawable.ic_file_rar);
			filenames.put("rmvb", R.drawable.ic_file_rmvb);
			filenames.put("rmvp", R.drawable.ic_file_rmvb);
			filenames.put("txt", R.drawable.ic_file_txt);
			filenames.put("xls", R.drawable.ic_file_xls);
			filenames.put("xlsx", R.drawable.ic_file_xls);
			filenames.put("zip", R.drawable.ic_file_zip);
			filenames.put("pdf", R.drawable.ic_file_pdf);
			filenames.put("mp3", R.drawable.ic_file_mp3);
			filenames.put("amr", R.drawable.ic_file_amr);
			filenames.put("ogg", R.drawable.ic_file_amr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String username=PrefUtility.get(Constants.PREF_LOGIN_NAME, "");
		username=username.split("@")[0];
		SD_PATH="�ļ���/"+username;
		SD_PATH=SD_PATH+curPath;
		if(SD_PATH.substring(SD_PATH.length()-1).equals("/"))
			SD_PATH=SD_PATH.substring(0, SD_PATH.length()-1);
		getDownloadSubject();
	}

	private void initListener() {
		aq.id(R.id.layout_btn_left).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
		//�μ��б�
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				startDownload(view);
			}
		});
		//�ϴ��μ�
		aq.id(R.id.layout_btn_right).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//getDownloadSubject();
				//uploadDialog().show();
				uploadDialog().show();
				
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (database != null) {
			OpenHelperManager.releaseHelper();
		}
	}

	public Dialog uploadDialog() {
		View localView = getLayoutInflater().inflate(
				R.layout.view_dialog_schedule, null);
		ListView list = (ListView) localView.findViewById(R.id.list);
		Button close = (Button) localView.findViewById(R.id.close);
		String[] data = getResources().getStringArray(R.array.upload_dialog);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.view_testing_pop, R.id.time, data);
		list.setAdapter(adapter);
		upLoadingDialog = new Dialog(this, R.style.dialog);
		upLoadingDialog.setContentView(localView);
		upLoadingDialog.getWindow().setGravity(Gravity.BOTTOM);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent();
				switch (position) {
				case 0:
					photoCamera();
					upLoadingDialog.dismiss();
					break;
				case 1:
					videoCamera();
					upLoadingDialog.dismiss();
					break;
				case 2:
					getPicFromLocation();
					upLoadingDialog.dismiss();
					break;
				case 3:
					getvideoFromLocation();
					/*
					intent.setClass(CourseClassActivity.this, LocalVideos.class);
					startActivityForResult(intent, PIC_Select_CODE_VideoFromLoacal);
					*/
					upLoadingDialog.dismiss();
					break;
				case 4:
					getAudioFromLocation();
					upLoadingDialog.dismiss();
					break;
				case 5:
					intent.setClass(CourseClassActivity.this, LocalDocument.class);
					startActivityForResult(intent,PIC_Select_CODE_DocumentFromLoacal);
					upLoadingDialog.dismiss();
					break;
				}
				
			}
		});
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.close:
					upLoadingDialog.dismiss();
					break;
				default:
					break;
				}

			}
		});
		return upLoadingDialog;
	}
	/***
	 * ��������:����ϵͳ�����
	 * 
	 * @author linrr 2013-12-26 ����4:35:33
	 * 
	 */
	private void photoCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// ����android�Դ��������
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����
			Log.v("TestFile", "SD card is not avaiable/writeable right now.");
			return;
		}

		mCurrentPhotoFile = new File(FileUtility.getRandomSDFileName(FileUtility.SDPATH,"jpg"));
		Uri uri = Uri.fromFile(mCurrentPhotoFile);
		intent.putExtra("return-data", false);
		intent.putExtra("android.intent.extra.screenOrientation", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, CAMERA_WITH_DATA);
	}
	/**
	 * ��������:����ϵͳ�����
	 * 
	 * @author linrr 2013-12-26 ����4:35:54
	 * 
	 */
	private void videoCamera() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);// ����android�Դ��������
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����
			Log.v("TestFile", "SD card is not avaiable/writeable right now.");
			return;
		}
		mCurrentVideoFile = new File(FileUtility.getRandomSDFileName(FileUtility.SDPATH,"3gp"));
		
		Uri uri = Uri.fromFile(mCurrentVideoFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
		intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, Constants.kejianMaxSize);
		startActivityForResult(intent, REQUEST_CODE_TAKE_VIDEO);
	}
	
	/**
	 * ��������:�õ�����ͼƬ������ͼ
	 * 
	 * @author linrr 2013-12-26 ����4:37:13
	 * 
	 */
	public void getPicFromLocation() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// �ж��Ƿ���SD��
			/*
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, PIC_Select_CODE_ImageFromLoacal);
			*/
			Intent intent; 
			intent = new Intent(Intent.ACTION_PICK, 
			                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); 
			intent.setType("image/*"); 
			startActivityForResult(intent, PIC_Select_CODE_ImageFromLoacal);
		} else {
			AppUtility.showToastMsg(CourseClassActivity.this, "û��SD��");
		}
	}
	public void getvideoFromLocation() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// �ж��Ƿ���SD��
			/*
			Intent intent = new Intent();
			intent.setType("video/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, PIC_Select_CODE_VideoFromLoacal);
			*/
			Intent intent; 
			intent = new Intent(Intent.ACTION_PICK, 
			                    android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI); 
			intent.setType("video/*");
			startActivityForResult(intent, PIC_Select_CODE_VideoFromLoacal);
		} else {
			AppUtility.showToastMsg(CourseClassActivity.this,getString(R.string.NoSDCard));
		}
	}
	public void getAudioFromLocation() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// �ж��Ƿ���SD��
			Intent intent = new Intent();
			intent.setType("audio/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, PIC_Select_CODE_AudioFromLoacal);
		} else {
			AppUtility.showToastMsg(CourseClassActivity.this, getString(R.string.NoSDCard));
		}
	}
	private void showProgress(boolean progress) {
		if (progress) {
			
			mList.setVisibility(View.GONE);
			failedLayout.setVisibility(View.GONE);
			loadingLayout.setVisibility(View.VISIBLE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			failedLayout.setVisibility(View.GONE);
			mList.setVisibility(View.VISIBLE);
		}
	}
	/*
	private void showFetchFailedView() {
		loadingLayout.setVisibility(View.GONE);
		mList.setVisibility(View.GONE);
		failedLayout.setVisibility(View.VISIBLE);
	}
	*/
	/**
	 * ��������:��ȡ�μ��б�
	 * 
	 * @author shengguo 2014-4-28 ����11:08:29
	 * 
	 */
	private void getDownloadSubject() {
		showProgress(true);
		
		String mInterface = null;
		
		mInterface = "MoodleFilesDownload.php";
		
		Log.d(TAG, "--------" + String.valueOf(new Date().getTime()));
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "----------datatime:" + datatime);
		Log.d(TAG, "----------checkCode:" + checkCode + "++");
		JSONObject jo = new JSONObject();
		try {
			jo.put("�û�������", checkCode);
			jo.put("��ǰ·��", curPath);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getDownloadSubject(params, mInterface, new RequestListener() {

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
				msg.what = 3;
				msg.obj = response;
				mHandler.sendMessage(msg);
				
			}
		});
	}

	public class CourseAdapter extends BaseAdapter {
		private List<DownloadSubject> listData = new ArrayList<DownloadSubject>();
		LayoutInflater inflater;

		public CourseAdapter(List<DownloadSubject> list) {
			this.listData = list;
			inflater = LayoutInflater.from(CourseClassActivity.this);
		}

		@Override
		public int getCount() {
			return listData == null ? 0 : listData.size();
		}

		@Override
		public Object getItem(int position) {
			return listData.get(position);
		}

		public void setList(List<DownloadSubject> list) {
			listData = list;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("DefaultLocale")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.view_list_classroom_course, null);
				holder.download = (ImageView) convertView
						.findViewById(R.id.course_download);
				holder.image = (ImageView) convertView
						.findViewById(R.id.course_image);
				holder.courseName = (TextView) convertView
						.findViewById(R.id.course_name);
				holder.downCount = (TextView) convertView
						.findViewById(R.id.count);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.progressBar = (ProgressBar) convertView
						.findViewById(R.id.bar);
				holder.tvprogress = (TextView) convertView
						.findViewById(R.id.tvprogress);

				holder.downloadInfo = (LinearLayout) convertView
						.findViewById(R.id.downloadInfo);
				holder.downloadProgress = (LinearLayout) convertView
						.findViewById(R.id.downloadProgress);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			DownloadSubject downloadSubject = listData.get(position);
			holder.download.setTag(downloadSubject);
			DecimalFormat df1 = new DecimalFormat("###0.00"); 
			holder.downCount.setText("��С:" + df1.format((double)downloadSubject.getFilesize()/(1024*1024))+"M");
			
			String filename = downloadSubject.getFileName();
			holder.courseName.setText(filename);
			downloadSubject.setIndex(position);
			
			if(downloadSubject.getType().equals("�ļ���"))
			{
				holder.time.setVisibility(View.VISIBLE);
				holder.download.setVisibility(View.GONE);
				holder.downCount.setVisibility(View.GONE);
				holder.time.setText(getString(R.string.filenum) + downloadSubject.getLoadCount());
				holder.image.setImageResource(R.drawable.icon_folder);
			}
			else if(downloadSubject.getType().equals("��һ��"))
			{
				holder.time.setVisibility(View.GONE);
				holder.download.setVisibility(View.GONE);
				holder.downCount.setVisibility(View.GONE);
				holder.image.setImageResource(R.drawable.backdir);
			}
			else
			{
				holder.time.setVisibility(View.GONE);
				holder.download.setVisibility(View.VISIBLE);
				holder.downCount.setVisibility(View.VISIBLE);
				if (AppUtility.isNotEmpty(filename)) {
					String filetype = filename
							.substring(filename.lastIndexOf(".") + 1);
					int resId = filenames.optInt(filetype.toLowerCase());
					if (resId > 0) {
						holder.image.setImageResource(resId);
					} else {
						holder.image.setImageResource(R.drawable.ic_file_default);
					}
	
				} else {
					holder.image.setImageResource(R.drawable.ic_file_default);
				}
				holder.download.setImageResource(R.drawable.download_course);
				holder.downloadInfo.setVisibility(View.VISIBLE);
				holder.downloadProgress.setVisibility(View.INVISIBLE);
				
				String localfile = FileUtility.creatSDDir(SD_PATH) + downloadSubject.getFileName();
				File file = new File(localfile);
				long filesize = downloadSubject.getFilesize();
				
				if (file.exists() && localfile.indexOf(".") > -1) {
					
					
					long filelength = file.length();
					if (filelength == filesize) {
						holder.download
								.setImageResource(R.drawable.downloaded_course);
						//holder.download.setSelected(true);
					}
					if (filelength < filesize) {
						holder.progressBar.setMax((int) filesize);
						holder.progressBar.setProgress((int) filelength);

						String downsizestr = AppUtility.getFileSize(filelength);
						String filesizestr = AppUtility.getFileSize(filesize);
						holder.tvprogress.setText(downsizestr + "/" + filesizestr);

						holder.downloadInfo.setVisibility(View.INVISIBLE);
						holder.downloadProgress.setVisibility(View.VISIBLE);
					}
					
				}
				else
					holder.download.setImageResource(R.drawable.download_course);
			}
			
			return convertView;
		}
	}

	static class ViewHolder {
		ImageView image, download;
		TextView courseName;
		TextView downCount;
		TextView time;
		ProgressBar progressBar;
		TextView tvprogress;
		LinearLayout downloadInfo;
		LinearLayout downloadProgress;
	}

	/**
	 * ��������:��Ӧ��ʼ���ذ�ť�ĵ���¼�
	 * 
	 * @author yanzy 2013-12-25 ����3:51:41
	 * 
	 * @param v
	 */
	@SuppressLint("DefaultLocale")
	public void startDownload(View v) {
		// ��ȡ�μ����󣬴˴�����Ϊ�����¸�ֵ������λ�ã�CourseClassFragment.java>>CourseAdapter>>getView>>holder.download.setTag(downloadSubject);
		RelativeLayout rl = (RelativeLayout) v;
		v = rl.findViewById(R.id.course_download);
		DownloadSubject ds = (DownloadSubject) v.getTag();
		if(ds.getType().equals("�ļ���"))
		{
			curPath+=ds.getFileName()+"/";
			getDownloadSubject();
			return;
		}
		else if(ds.getType().equals("��һ��"))
		{
			
			String[] pathArray=curPath.split("/");
			if(pathArray.length>1)
			{
				curPath="";
				for(int i=0;i<pathArray.length-1;i++)
				{
					curPath+=pathArray[i]+"/";
				}
				getDownloadSubject();
				
			}
			return;
		}
		String urlstr = ds.getDownAddress();
		String localfile = FileUtility.creatSDDir(SD_PATH) + ds.getFileName();
		long filesize = ds.getFilesize(); // ���������������ļ���С
		long localsize = 0; // �����ļ��Ѿ����ش�С
		String localFilePath = ds.getLocalfile();
		String[] data;
		
		data=new String[] { getString(R.string.openFile),getString(R.string.delFile), getString(R.string.delRemoteFile)};
		
		
		ImageView image = (ImageView)v;
		progressImages.put(urlstr, image);
		
		if (AppUtility.isNotEmpty(localFilePath)) {
			localfile = ds.getLocalfile();
			Log.d(TAG, "----------------->filename:" + localfile);
			
			showDownloadDialog(urlstr, localfile, data);
			return;
		}
		File file = new File(localfile);
		int downstate = downstates.get(urlstr) == null ? -1 : downstates
				.get(urlstr); // ����״̬

		// �ڶ��ν�����������صĻ�ɫ��ť����ҪΪprogressImages���imageView��
		//RelativeLayout layout = (RelativeLayout) v.getParent();
		//ImageView image = (ImageView) layout.findViewById(R.id.course_download);
		

		if (file.exists() && localfile.indexOf(".") > -1) {// �����ļ�����
			localsize = file.length(); // ��ȡ�����ļ��Ѿ����صĳ���
		}
		Log.d(TAG, localsize + "localsize == filesize" + filesize);
		/*
		if (filesize == 0) {
			AppUtility.showToastMsg(CourseClassActivity.this, "����˷����ļ���СΪ0");
		}
		*/
		if (localsize == filesize && filesize >= 0) { // ���������ļ���С�����ļ��Ĵ�С ˵���Ѿ��������
			downstate = 2;// �������
			// Toast.makeText(ClassRoomActivity.this, "���ļ��Ѿ�������ɣ�", 0).show();
			showDownloadDialog(urlstr, localfile, data);
		}
		if (localsize < filesize && filesize > 0 && downstate != 2) { // �ļ�δ�������
			if (localsize >= 0) {
				if (downstate == 1) { // ��������
					String[] data1 = { getString(R.string.pauseDownload), getString(R.string.cancelDownload)};
					showDownloadDialog(urlstr, localfile, data1);
				}
			}

			boolean ischeck = true; // ������ص�ַ������
			boolean success = false; // ���wifi����
			boolean successall = false; // ���wifi��GPRS����
			/**
			 * ������ص�ַ�Ƿ���ȷ
			 */
			if (!AppUtility.isNotEmpty(urlstr)) { // ��ַΪ��
				ischeck = false;
			} else { // ��ַ��Ϊ��
				if (urlstr.toLowerCase().indexOf("http://") <= -1
						|| urlstr.indexOf(".") <= -1) { // ��ַ����ȷ
					AppUtility.showToastMsg(CourseClassActivity.this,
							getString(R.string.IncompleteAddress));
					ischeck = false;
				}
			}

			/**
			 * �����ص�ַ��ȷ������ֻ�����
			 */
			if (ischeck) {
				ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				State state = connManager.getNetworkInfo(
						ConnectivityManager.TYPE_WIFI).getState(); // ��ȡ��������״̬
				if (State.CONNECTED == state) { // �ж��Ƿ�����ʹ��WIFI����
					success = true;
					successall = true;
				}

				TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
					// return "Tablet";
				} else {
					state = connManager.getNetworkInfo(
							ConnectivityManager.TYPE_MOBILE).getState();
					// ��ȡ��������״̬
					if (State.CONNECTED == state) { // �ж��Ƿ�����ʹ��GPRS����
						AlertDialog.Builder builder = new AlertDialog.Builder(
								CourseClassActivity.this);
						builder.setTitle("");
						builder.setMessage(getString(R.string.isnotWifi));
						builder.setPositiveButton(getString(R.string.continue1), new downloadListener(rl,
								urlstr, localfile, filesize));
						builder.setNegativeButton(getString(R.string.search_cancel),
								new cancelStudentPicListener());
						AlertDialog ad = builder.create();
						ad.show();
						success = false;
						successall = true;
					}
				}
			}

			if (!successall) {
				AppUtility.showToastMsg(CourseClassActivity.this, getString(R.string.networkinterrupted));
			}

			/**
			 * �����ϼ���û���⣬���������
			 */
			if (ischeck && success) {
				downloadFile(rl, urlstr, localfile, filesize);
			}
		}
	}

	// ������
	private class cancelStudentPicListener implements
			DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	}
	/**
	 * ��������:�����ļ��ϴ�
	 * 
	 * @author linrr 2013-12-26 ����4:36:51
	 * 
	 * @param mCurrentFile
	 */
	@SuppressLint("SimpleDateFormat")
	private void fileUploadWay(final File mCurrentFile) {
		if(AppUtility.formetFileSize(mCurrentFile.length())>Constants.kejianMaxSize){
			AppUtility.showToastMsg(this, String.format(getString(R.string.maxfilesize), Constants.kejianMaxSize/1024/1024));
		}else{
			downloadSubject = new DownloadSubject();
			//String filebase64Str = FileUtility.fileupload(mCurrentFile);
			//downloadSubject.setFilecontent(filebase64Str);
			//String filename = mCurrentFile.getName();
			String name = mCurrentFile.getName();
			
			final String type=name.substring(name.lastIndexOf(".")+1, name.length());
			name=name.substring(0, name.lastIndexOf("."));
			//Date date = new Date(System.currentTimeMillis());
			//SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
			
			
			final EditText et=new EditText(this);
			//et.setText(ClassDetailActivity.teacherInfo.getName()+"["+dateFormat.format(date)+"]."+type);
			et.setText(name);
			new AlertDialog.Builder(this).setTitle(getString(R.string.nameuploadfile))
			.setView(et)
			.setCancelable(false)
			.setPositiveButton(R.string.go, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(et.getText().length()>0)
					{
						
						downloadSubject.setFileName(et.getText().toString()+"."+type);
						downloadSubject.setLocalfile(mCurrentFile.getAbsolutePath());
						downloadSubject.setFilesize(mCurrentFile.length());
						SubmitUploadFile(downloadSubject);
					}
					else
					{
						AppUtility.showToastMsg(CourseClassActivity.this, getString(R.string.filenamenotempty));
						return;
					}
			
				}})
			.setNegativeButton(getString(R.string.search_cancel),new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mCurrentFile.delete();
					dialog.dismiss();
				}
			
				}).show();

			
		}
	}
	/**
	 * ��������:�ϴ��ļ�
	 *
	 * @author linrr  2013-12-18 ����11:48:59
	 * 
	 * @param base64Str
	 * @param action
	 */
	/*
	public void SubmitUploadFile(DownloadSubject downloadSubject){
		dialog = DialogUtility.createLoadingDialog(CourseClassActivity.this, "�����ϴ������Ե�...");
		if (dialog != null) {
			dialog.show();
		}
		CampusParameters params = new CampusParameters();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");// ��ȡ�û�У����
		final String localfile = downloadSubject.getLocalfile();
		final long filesize = downloadSubject.getFilesize();
		params.add("�û�������", checkCode);
		params.add("�γ�����", downloadSubject.getCourseName());
		params.add("��ʦ����", downloadSubject.getUserName());
		params.add("�ļ���", downloadSubject.getFileName());
		params.add("pic", localfile);
		//params.add("�ļ�����", downloadSubject.getFilecontent());
		CampusAPI.uploadFilesNoBase64(params, new RequestListener(){

			@Override
			public void onComplete(String response) {
				Bundle bundle = new Bundle();
				bundle.putString("result", response.toString());
				bundle.putString("localfile", localfile);
				bundle.putLong("filesize", filesize);
				Log.d(TAG, "response.toString()"+response.toString());
				Message msg = new Message();
				msg.what = 5;
				msg.obj = bundle;
				mHandler.sendMessage(msg);	
			}

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
		});
	}
	*/
	public void SubmitUploadFile(final DownloadSubject downloadSubject){
		CampusParameters params = new CampusParameters();
		//String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");// ��ȡ�û�У����
		final String localfile = downloadSubject.getLocalfile();
		/*
		params.add("�û�������", checkCode);
		params.add("�γ�����", downloadSubject.getCourseName());
		params.add("��ʦ����", downloadSubject.getUserName());
		params.add("�ļ���", downloadSubject.getFileName());
		*/
		
		params.add("action","upload");
		params.add("filepath",curPath);
		params.add("userId",userId);
		String sclass=PrefUtility.get(Constants.PREF_CHECK_SCLASS,"");
		String realname=PrefUtility.get(Constants.PREF_CHECK_REALNAME,"");
		params.add("author",sclass+realname);
		/*
		params.add("JiaoYanMa", checkCode);
		*/
		params.add("newFileName", downloadSubject.getFileName());
		params.add("pic", localfile);
		String url=userDomain+"/dandian/uploadDelete.php";
		
		HttpMultipartPostToMoodle post = new HttpMultipartPostToMoodle(this, url,params){
			@Override  
		    protected void onPostExecute(String result) {  
				Bundle bundle = new Bundle();
				bundle.putString("result", result);
				bundle.putString("localfile", localfile);
				bundle.putLong("filesize", downloadSubject.getFilesize());
				Message msg = new Message();
				msg.what = 5;
				msg.obj = bundle; 
				mHandler.sendMessage(msg);	
				this.pd.dismiss();
		    }
		};  
        post.execute();
	}
	/**
	 * 
	 * #(c) ruanyun PocketCampus <br/>
	 * 
	 * �汾˵��: $id:$ <br/>
	 * 
	 * ����˵��: ����GRRS������������
	 * 
	 * <br/>
	 * ����˵��: 2013-12-25 ����4:03:21 yanzy �����ļ�<br/>
	 * 
	 * �޸���ʷ:<br/>
	 * 
	 */
	private class downloadListener implements DialogInterface.OnClickListener {
		View v;
		String urlstr;
		String localfile;
		long filesize;

		public downloadListener(View v, String urlstr, String localfile,
				long filesize) {
			this.v = v;
			this.urlstr = urlstr;
			this.localfile = localfile;
			this.filesize = filesize;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			downloadFile(v, urlstr, localfile, filesize);
		}
	}

	/**
	 * ��������:���ص�����
	 * 
	 * @author zhuliang 2013-12-25 ����1:24:26
	 * 
	 */
	private void showDownloadDialog(String urlstr, String localfile,
			String[] data) {
		View view = inflater.inflate(R.layout.view_exam_dialog, null);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		ListView mList = (ListView) view.findViewById(R.id.list);
		DialogAdapter dialogAdapter = new DialogAdapter(urlstr, localfile, data);
		mList.setAdapter(dialogAdapter);
		downloadDialog.setContentView(view);
		downloadDialog.show();
		downloadDialog.getWindow().setGravity(Gravity.BOTTOM);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				downloadDialog.dismiss();
			}
		});
	}

	/**
	 * ��������listview������
	 */
	public class DialogAdapter extends BaseAdapter {
		String[] arrayData;
		String urlstr;
		String localfile;

		public DialogAdapter(String urlstr, String localfile, String[] array) {
			this.arrayData = array;
			this.urlstr = urlstr;
			this.localfile = localfile;
		}

		@Override
		public int getCount() {
			return arrayData == null ? 0 : arrayData.length;
		}

		@Override
		public Object getItem(int position) {
			return arrayData[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			mViewHolder holder = null;
			if (convertView == null) {
				holder = new mViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.view_testing_pop, null);
				holder.title = (TextView) convertView.findViewById(R.id.time);

				convertView.setTag(holder);
			} else {
				holder = (mViewHolder) convertView.getTag();
			}
			final String text = arrayData[position];
			holder.title.setText(text);
			holder.title.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (getString(R.string.pauseDownload).equals(text)) {
						downloaders.get(urlstr).pause();
						downstates.put(urlstr, 0); // ������״̬������ͣ
					}
					if (getString(R.string.cancelDownload).equals(text)) {
						// ɾ�������ļ�
						File file = new File(localfile);
						if (file.exists()) {
							file.delete();
						}

						// ������Ϣ ���������Ϣ
						Bundle bundle = new Bundle();
						bundle.putString("urlstr", urlstr);
						bundle.putBoolean("canceldown", true);
						Message message = Message.obtain();
						message.what = 1;
						message.obj = bundle;
						mHandler.sendMessage(message);
					}
					if (getString(R.string.delFile).equals(text)) {
						// ɾ�������ļ�
						File file = new File(localfile);
						if (file.exists()) {
							file.delete();
							
							
						}
						if (!file.exists()) {
							if (progressImages.get(urlstr) != null) {
								progressImages.get(urlstr).setImageResource(
										R.drawable.download_course);
								DownloadSubject ds=(DownloadSubject)progressImages.get(urlstr).getTag();
								ds.setLocalfile("");
							}
							AppUtility.showToastMsg(getApplicationContext(),
									getString(R.string.deleteSuc));
						}
					}
					if (getString(R.string.delRemoteFile).equals(text)) {
						DownloadSubject ds=(DownloadSubject)progressImages.get(urlstr).getTag();
						
						String deleteJsonStr = getDownloadDeleteData(ds);
						Log.d(TAG, "deleteJsonStr:" + deleteJsonStr);
						if (!"".equals(deleteJsonStr) && deleteJsonStr != null) {
							String deleteBase64 = Base64.encode(deleteJsonStr
									.getBytes());
							SubmitDeleteinfo(deleteBase64, urlstr, localfile);
						}
					}
					if (getString(R.string.openFile).equals(text)) {
						File file = new File(localfile);
						if (!file.exists() || file.length()==0) {
							AppUtility.showToastMsg(CourseClassActivity.this,getString(R.string.filenotexist));
						}
						else
						{
							Intent intent=IntentUtility.openFile(localfile);
							/*
							String type=intent.getType();
							
							if(!bOfficeInstall && (type.equals("application/vnd.ms-powerpoint") || type.equals("application/vnd.ms-excel") || type.equals("application/msword") || type.equals("application/pdf")))
							{
								String packArray[]={"com.qo.android.am3","com.qo.android.tablet.am","cn.wps.moffice_eng","cn.wps.moffice_i18n_hd"};
								IntentUtility.checkAppByPackName(CourseClassActivity.this,intent,packArray,"�򿪴��ļ���Ҫ��װQuickOffice,�Ƿ�װ?","http://laoshi.dandian.net/otherTools/QuickOffice.apk");
								bOfficeInstall=true;
							}
							if(!bZipInstall && (type.equals("application/x-rar-compressed") || type.equals("application/zip")))
							{
								String packArray[]={"ru.zdevs.zarchiver"};
								IntentUtility.checkAppByPackName(CourseClassActivity.this,intent,packArray,"�򿪴��ļ���Ҫ��װZArchiver,�Ƿ�װ?","http://laoshi.dandian.net/otherTools/ZArchiver.apk");
								bZipInstall=true;
							}
							else
							*/
								IntentUtility.openIntent(CourseClassActivity.this,intent,true);
						}
						
					}
					
					// �ر�̷������
					downloadDialog.dismiss();
				}
			});
			return convertView;
		}

	}

	class mViewHolder {
		TextView title;
	}
	
	/**
	 * ��������:�ύɾ���ļ�����Ϣ
	 * 
	 * @author zhuliang 2013-12-25 ����4:41:12
	 * 
	 * @param base64Str
	 * @param action
	 */
	public void SubmitDeleteinfo(String base64Str, final String url,
			final String pathfile) {
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.sendDownloadDeleteData(params, new RequestListener() {

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
				Bundle bundle = new Bundle();
				bundle.putString("url", url);
				bundle.putString("pathfile", pathfile);
				bundle.putString("result", response.toString());
				Message msg = new Message();
				msg.what = 2;
				msg.obj = bundle;
				mHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * ��������:��ȡ�����ؿμ�ɾ�������ύ������
	 * 
	 * @author zhuliang 2013-12-25 ����4:37:34
	 * 
	 * @param localFile
	 * @return
	 */
	private String getDownloadDeleteData(DownloadSubject ds) {
		JSONObject jo = new JSONObject();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		
		try {
			jo.put("�û�������", checkCode);
			jo.put("DATETIME", String.valueOf(new Date().getTime()));
			jo.put("contextid", ds.getContextid());
			jo.put("filename", ds.getFileName());
			jo.put("filepath", ds.getFilepath());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jo.toString();
	}

	/**
	 * ��������:��ʼ�������ļ�������������
	 * 
	 * @author yanzy 2013-12-25 ����3:59:39
	 * 
	 * @param v
	 * @param urlstr
	 * @param localfile
	 */
	public void downloadFile(View v, String urlstr, String localfile,
			long filesize) {

		File file = new File(FileUtility.creatSDDir(SD_PATH));
		if (!file.exists()) {
			file.mkdirs();
		}
		// ���������߳���Ϊ4����������Ϊ�˷������̶���
		int threadcount = 1;
		// ��ʼ��һ��downloader������
		Downloader downloader = downloaders.get(urlstr);
		if (downloader == null) {
			downloader = new Downloader(urlstr, localfile, threadcount,
					CourseClassActivity.this, mHandler);
			downloaders.put(urlstr, downloader);
		}
		if (downloader.isdownloading())
			return;
		// �õ�������Ϣ��ĸ�����ɼ���
		LoadInfo loadInfo = downloader.getDownloaderInfors();
		loadInfo.setFileSize((int) filesize);
		// ���ļ������ǲ�����
		if (loadInfo.getFileSize() > 0) {
			// ����������Ϣ
			RelativeLayout layout=(RelativeLayout)v;
			LinearLayout downloadInfo = (LinearLayout) layout
					.findViewById(R.id.downloadInfo);
			
			downloadInfo.setVisibility(View.INVISIBLE);
			// ��ʾ���ؽ�������Ϣ
			LinearLayout downloadProgress = (LinearLayout) layout
					.findViewById(R.id.downloadProgress);
			downloadProgress.setVisibility(View.VISIBLE);

			downstates.put(urlstr, 1);
			AppUtility.showToastMsg(CourseClassActivity.this, getString(R.string.Startdownloading));
			// ��ʾ������
			showProgress(loadInfo, urlstr, v);
			// ���÷�����ʼ����
			downloader.download();
		}
	}

	/**
	 * ��������:��ʾ������
	 * 
	 * @author yanzy 2013-12-25 ����4:01:32
	 * 
	 * @param loadInfo
	 * @param url
	 * @param v
	 */
	private void showProgress(LoadInfo loadInfo, String url, View v) {
		ProgressBar bar = ProgressBars.get(url);
		TextView textView = progressesTexts.get(url);
		ImageView imageView = progressImages.get(url);
		if (bar == null) {
			RelativeLayout layout=(RelativeLayout)v;
			bar = (ProgressBar) layout.findViewById(R.id.bar); // ��ȡUI�н�����
			int max = loadInfo.getFileSize();
			bar.setMax(max); // �������ֵ
			bar.setProgress(loadInfo.getComplete()); // �����Ѿ����صĽ���
			ProgressBars.put(url, bar);

			textView = (TextView) layout.findViewById(R.id.tvprogress); // ��ʾ�ļ���С
			imageView = (ImageView) layout.findViewById(R.id.course_download); // �������ذ�ť
			float a = 0;
			String text = "";
			if (max > 1024 * 1024) {
				a = (float) max / 1024 / 1024;
				text = "0M/" + String.format("%.1f", a) + "M";
			}
			if (1024 <= max && max < 1024 * 1024) {
				a = (float) max / 1024;
				text = "0K/" + String.format("%.1f", a) + "K";
			}
			if (0 <= max && max < 1024) {
				text = "0b/" + String.format("%.1f", a) + "b";
			}

			textView.setText(text);
			progressesTexts.put(url, textView);
			progressImages.put(url, imageView);

		}
	}

	/**
	 * ��������:�ύ�μ�������Ϣ
	 * 
	 * @author zhuliang 2013-12-25 ����6:01:09
	 * 
	 * @param base64Str
	 
	public void SubmitDownloadInfo(String base64Str, final String url) {
		CampusParameters params = new CampusParameters();
		params.add("url", url);
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.sendDownloadDoneData(params, new RequestListener() {

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
				Bundle bundle = new Bundle();
				bundle.putString("url", url);
				bundle.putString("result", response.toString());
				Message msg = new Message();
				msg.what = 4;
				msg.obj = bundle;
				mHandler.sendMessage(msg);
			}
		});
	}
*/
	
	
	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		File file = null;
		Boolean b = false;
		try {
			if (resultCode == Activity.RESULT_OK) {
				if (requestCode == CAMERA_WITH_DATA) {
					ImageUtility.rotatingImageIfNeed(mCurrentPhotoFile.getAbsolutePath()); 
					file = mCurrentPhotoFile;
					b = true;
				}
				if (requestCode == REQUEST_CODE_TAKE_VIDEO) {
					file = mCurrentVideoFile;
					b = true;
				}
				if (requestCode == PIC_Select_CODE_ImageFromLoacal) {
					
					/*
					String myImageUrl = data.getDataString();
					Uri uri = Uri.parse(myImageUrl);
					String[] photos = { MediaStore.Images.Media.DATA };
					Cursor actualimagecursor = this.managedQuery(uri, photos,
							null, null, null);
					int actual_image_column_index = actualimagecursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					actualimagecursor.moveToFirst();
					String img_path = actualimagecursor
							.getString(actual_image_column_index);
					*/
					
					Uri uri = data.getData();
					String img_path=FileUtility.getFilePathInSD(this,uri);
					//String extName=FileUtility.getFileExtName(img_path);
					//String tempPath =FileUtility.getRandomSDFileName(FileUtility.SDPATH,extName);
					String tempPath =FileUtility.getCacheDir()+FileUtility.getFileRealName(img_path);
					FileUtility.copyFile(img_path,tempPath);
					ImageUtility.rotatingImageIfNeed(tempPath);
					File file1 = new File(tempPath);
					Uri fileUri = Uri.fromFile(file1);
					Log.d(TAG, "fileuri" + fileUri);
					file = file1;
					b = true;
				}
				if (requestCode == PIC_Select_CODE_VideoFromLoacal) {
					
					/*
					String myImageUrl = data.getDataString();
					Uri uri = Uri.parse(myImageUrl);
					String[] photos = { MediaStore.Video.Media.DATA };
					Cursor actualimagecursor = this.managedQuery(uri, photos,
							null, null, null);
					int actual_image_column_index = actualimagecursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					actualimagecursor.moveToFirst();
					String img_path = actualimagecursor
							.getString(actual_image_column_index);
					*/
					Uri uri = data.getData();
					String img_path=FileUtility.getFilePathInSD(this,uri);
					//String extName=FileUtility.getFileExtName(img_path);
					//String tempPath =FileUtility.getRandomSDFileName(FileUtility.SDPATH,extName);
					String tempPath =FileUtility.getCacheDir()+FileUtility.getFileRealName(img_path);
					FileUtility.copyFile(img_path,tempPath);
					File file1 = new File(tempPath);
					if(!file1.exists())
					{
						AppUtility.showToastMsg(this, getString(R.string.selectFilenotexist));
						return;
					}
					Uri fileUri = Uri.fromFile(file1);
					Log.d(TAG, "fileuri" + fileUri);
					file = file1;
					b = true;
				}
				if (requestCode == PIC_Select_CODE_AudioFromLoacal) {
					
					
					String myImageUrl = data.getDataString();
					Uri uri = Uri.parse(myImageUrl);
					String img_path=FileUtility.getFilePathInSD(this,uri);
					
					//String extName=FileUtility.getFileExtName(img_path);
					//String tempPath =FileUtility.getRandomSDFileName(FileUtility.SDPATH,extName);
					String tempPath =FileUtility.getCacheDir()+FileUtility.getFileRealName(img_path);
					FileUtility.copyFile(img_path,tempPath);
					File file1 = new File(tempPath);
					if(!file1.exists())
					{
						AppUtility.showToastMsg(this, getString(R.string.selectFilenotexist));
						return;
					}
					Uri fileUri = Uri.fromFile(file1);
					Log.d(TAG, "fileuri" + fileUri);
					file = file1;
					b = true;
				}
			}
			
			if (requestCode==PIC_Select_CODE_DocumentFromLoacal) {
				String p = data.getStringExtra("paths");
				if (!"".equals(p)&&p != null) {
					//String tempPath =FileUtility.getRandomSDFileName(FileUtility.SDPATH,FileUtility.getFileExtName(p));
					String tempPath =FileUtility.getCacheDir()+FileUtility.getFileRealName(p);
					FileUtility.copyFile(p,tempPath);

					File f = new File(tempPath);
					file = f;
					b = true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (b) {
//			dialog = DialogUtility.createLoadingDialog(this, "�����ϴ������Ե�...");
			fileUploadWay(file);
		}
	}

}
