package com.dandian.pocketmoodle.fragment;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.activity.ImagesActivity;
import com.dandian.pocketmoodle.adapter.MyPictureAdapter;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.entity.DownloadSubject;
import com.dandian.pocketmoodle.entity.ImageItem;
import com.dandian.pocketmoodle.entity.QuestionnaireList;
import com.dandian.pocketmoodle.entity.QuestionnaireList.Question;
import com.dandian.pocketmoodle.lib.DateTimePickDialogUtil;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.DateHelper;
import com.dandian.pocketmoodle.util.FileUtility;
import com.dandian.pocketmoodle.util.ImageUtility;
import com.dandian.pocketmoodle.util.MyImageGetter;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.dandian.pocketmoodle.widget.NonScrollableGridView;
import com.dandian.pocketmoodle.widget.NonScrollableListView;




@SuppressLint("ValidFragment")
public class SchoolQuizDetailFragment extends Fragment {
	private final String TAG = "SchoolQuizDetailFragment";
	private ListView myListview;
	private Button btnLeft;
	private TextView tvTitle, tvRight;
	private LinearLayout lyLeft, lyRight,loadingLayout,contentLayout,failedLayout,emptyLayout;
	private QuestionnaireList questionnaireList;
	private String title,status, interfaceName,picturePath,delImagePath,autoClose;
	private LayoutInflater inflater;
	private QuestionAdapter adapter;
	private boolean isEnable = true;
	private Dialog dialog, getPictureDiaLog;
	private MyPictureAdapter myPictureAdapter;
	//List<String> picturePaths = new ArrayList<String>();
	private ArrayList<Question> questions = new ArrayList<Question>();
	//private List<ImageItem> images = new ArrayList<ImageItem>();
	private static final int REQUEST_CODE_TAKE_PICTURE = 2;// //����ͼƬ�����ı�־
	private static final int REQUEST_CODE_TAKE_CAMERA = 1;// //�������ղ����ı�־
	private static final int REQUEST_CODE_TAKE_DOCUMENT = 3;// //����ͼƬ�����ı�־
	//private int size = 5;//���ύͼƬ����;size:ͼƬ�������
	private int curIndex;
	private ProgressDialog progressDlg;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressLint("NewApi")
		public void handleMessage(Message msg) {
			String result = "";
			switch (msg.what) {
			case -1:
				if(dialog != null){
					dialog.dismiss();
				}
				if(progressDlg!=null)
					progressDlg.dismiss();
				AppUtility.showErrorToast(getActivity(), msg.obj.toString());
				
				break;
			case 0://��ȡ����
				showProgress(false);
				result = msg.obj.toString();
				
				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("���");
						if (AppUtility.isNotEmpty(res)) {
							AppUtility.showToastMsg(getActivity(), res);
						} else {
							questionnaireList = new QuestionnaireList(jo);
							if(questionnaireList.getTitle()!=null && questionnaireList.getTitle().length()>0)
								tvTitle.setText(questionnaireList.getTitle());
							questions = questionnaireList.getQuestions();
							status=questionnaireList.getStatus();
							autoClose=questionnaireList.getAutoClose();
							if (status.equals("������")) {
								tvRight.setVisibility(View.VISIBLE);
								isEnable = true;

							} else {
								lyRight.setVisibility(View.INVISIBLE);
								isEnable = false;
							}
							
							adapter.notifyDataSetChanged();
						}
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			case 1://����ɹ�
				if(progressDlg!=null) progressDlg.dismiss();
				result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) {
					try {
					
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("���");
						if(!AppUtility.isNotEmpty(res))
							res=jo.optString("״̬");
						if(res.equals("�ɹ�"))
						{
							AppUtility.showToastMsg(getActivity(),getString(R.string.savesuc));
							if(autoClose!=null && autoClose.equals("��"))
							{
								
								Intent aintent = new Intent();
								getActivity().setResult(1,aintent); 
								getActivity().finish();
							}
						}
						else
							AppUtility.showErrorToast(getActivity(), res);
							
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						AppUtility.showErrorToast(getActivity(), e.getMessage());
						
					}
					
				}
				
				break;
			case 2://ɾ��ͼƬ
				result = msg.obj.toString();
				int type=msg.getData().getInt("type");
				
				if (AppUtility.isNotEmpty(result)) {
					
					try {
						JSONObject	jo = new JSONObject(result);
						if("�ɹ�".equals(jo.optString("STATUS"))){
							if(type==1)
							{
								List<ImageItem> images=questions.get(curIndex).getImages();
								for (int i = 0; i < images.size(); i++) {
									if(images.get(i).getDownAddress().equals(delImagePath)){
										images.remove(i);
									}
								}
								questions.get(curIndex).setImages(images);
								File cacheFile=FileUtility.getCacheFile(delImagePath);
								if(cacheFile.exists())
									cacheFile.delete();
								myPictureAdapter.setPicPathsByImages(images);
							}
							else if(type==2)
							{
								JSONArray fujianArray=questions.get(curIndex).getFujianArray();
								for (int i = 0; i < fujianArray.length(); i++) {
									JSONObject item=(JSONObject) fujianArray.get(i);
									if(item.optString("newname").equals(FileUtility.getFileRealName(jo.optString("path")))){
										fujianArray.remove(i);
									}
								}
								questions.get(curIndex).setFujianArray(fujianArray);
								View view=(View) myListview.getChildAt(curIndex);
								NonScrollableListView listview=(NonScrollableListView) view.findViewById(R.id.lv_choose);
								SimpleAdapter fujianAdapter=setupFujianAdpter(questions.get(curIndex));
								listview.setAdapter(fujianAdapter);
							}
							//myPictureAdapter.notifyDataSetChanged();
						}else{
							AppUtility.showToastMsg(getActivity(), jo.optString("STATUS"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case 3://ͼƬ�ϴ�
				result = msg.obj.toString();
				String resultStr = "";
				Bundle data=msg.getData();
				String oldFileName=data.getString("oldFileName");
				type=data.getInt("type");
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
					JSONObject jo = new JSONObject(resultStr);
					if("OK".equals(jo.optString("STATUS"))){
						
						String newFileName=jo.getString("�ļ���");
						if(type==1)
						{
							List<ImageItem> images=questions.get(curIndex).getImages();
							FileUtility.fileRename(oldFileName, newFileName);
							ImageItem ds = new ImageItem(jo);
							images.add(ds);
							questions.get(curIndex).setImages(images);
							myPictureAdapter.setPicPathsByImages(images);
						}
						else if(type==2)
						{
							if(progressDlg!=null) progressDlg.dismiss();
							Question question=questions.get(curIndex);
							if(question!=null)
							{
								JSONArray fujianArray=question.getFujianArray();
								JSONObject newItem=new JSONObject();
								newItem.put("name", oldFileName);
								newItem.put("newname", newFileName);
								newItem.put("url", jo.optString("�ļ���ַ"));
								fujianArray.put(newItem);
								question.setFujianArray(fujianArray);
								questions.set(curIndex, question);
								View view=(View) myListview.getChildAt(curIndex);
								NonScrollableListView listview=(NonScrollableListView) view.findViewById(R.id.lv_choose);
								SimpleAdapter fujianAdapter=setupFujianAdpter(questions.get(curIndex));
								listview.setAdapter(fujianAdapter);
							}
							
							
						}
						//myPictureAdapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	};
	public SchoolQuizDetailFragment() {

	}
	public SchoolQuizDetailFragment(String title,String status,
			String iunterfaceName,String autoClose) {
		if (status==null || status.equals("�ѽ���") || status.equals("δ��ʼ")) {
			isEnable = false;
		}
		if(status==null)
			status="�ѽ���";
		this.title = title;
		this.status = status;
		this.interfaceName = iunterfaceName;
		if(autoClose==null)
			autoClose="��";
		this.autoClose=autoClose;
	}

	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerBroastcastReceiver();
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.school_listview_fragment,
				container, false);
		myListview = (ListView) view.findViewById(R.id.my_listview);
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		tvRight = (TextView) view.findViewById(R.id.tv_right);
		lyLeft = (LinearLayout) view.findViewById(R.id.layout_btn_left);
		lyRight = (LinearLayout) view.findViewById(R.id.layout_btn_right);
		loadingLayout = (LinearLayout) view.findViewById(R.id.data_load);
		contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) view.findViewById(R.id.empty_error);
		emptyLayout = (LinearLayout) view.findViewById(R.id.empty);
		RelativeLayout nav_bar=(RelativeLayout) view.findViewById(R.id.nav_bar);
		int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			nav_bar.setBackgroundColor(color);
		myListview.setEmptyView(emptyLayout);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);
		tvRight.setText(getString(R.string.save));
		tvTitle.setText(title);
		
		adapter = new QuestionAdapter();
		myListview.setAdapter(adapter);
		getPictureDiaLog = new Dialog(getActivity(), R.style.dialog);
		//�˳�
		lyLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		//��������
		lyRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "-----����");
				if (status.equals("�ѽ���") || status.equals("δ��ʼ")) {
					return;
				} else {
					Log.d(TAG, "-----����");
					saveQuestionAnswer();
				}
			}
		});
		// ���¼���
		failedLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getQuestionsItem();
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//AppUtility.showToastMsg(getActivity(), "���ڻ�ȡ����");
		getQuestionsItem();
	}

	private void registerBroastcastReceiver() {
		IntentFilter mFilter = new IntentFilter(Constants.GET_PICTURE);
		mFilter.addAction(Constants.DEL_OR_LOOK_PICTURE);
		getActivity().registerReceiver(mBroadcastReceiver, mFilter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String fromTag=intent.getStringExtra("TAG");
			curIndex=intent.getIntExtra("position",0);
			Log.d(TAG, "--------action:" + action);
			Log.d(TAG, "--------fromTag:" + fromTag);
			if (action.equals(Constants.GET_PICTURE)&&fromTag.equals(TAG)) {
				showGetPictureDiaLog();
			}else if(action.equals(Constants.DEL_OR_LOOK_PICTURE)&&fromTag.equals(TAG)){
				//�鿴��ͼ��ɾ��ͼƬ
				delImagePath = intent.getStringExtra("imagePath");
				showDelOrShowPictureDiaLog(delImagePath);
			}
		}
	};
	
	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	};
	/**
	 * ��������:��ȡͼƬ
	 * 
	 * @author shengguo 2014-5-5 ����3:45:04
	 * 
	 */
	private void showGetPictureDiaLog() {
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.view_get_picture, null);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		TextView byCamera = (TextView) view.findViewById(R.id.tv_by_camera);
		TextView byLocation = (TextView) view.findViewById(R.id.tv_by_location);
		getPictureDiaLog.setContentView(view);
		getPictureDiaLog.show();
		Window window = getPictureDiaLog.getWindow();
		window.setGravity(Gravity.BOTTOM);// �ڵײ�����
		window.setWindowAnimations(R.style.CustomDialog);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getPictureDiaLog.dismiss();
			}
		});
		//����ϵͳ�������
		byCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				getPictureByCamera();
				getPictureDiaLog.dismiss();
			}
		});
		//ѡ�񱾵�ͼƬ
		byLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				getPictureFromLocation();
				getPictureDiaLog.dismiss();
			}
		});
	}
	/**
	 * ��������:ɾ����鿴ͼƬ
	 *
	 * @author shengguo  2014-5-8 ����6:32:49
	 *
	 */
	private void showDelOrShowPictureDiaLog(final String imageName) {
		
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.view_show_or_del_picture, null);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		TextView delPicture = (TextView) view.findViewById(R.id.tv_delete);
		TextView showPicture = (TextView) view.findViewById(R.id.tv_show);
		View v = view.findViewById(R.id.view_dividing_line);
		final AlertDialog ad=new AlertDialog.Builder(getActivity()).setView(view).create();
		if(isEnable){
			delPicture.setVisibility(View.VISIBLE);
			v.setVisibility(View.VISIBLE);
		}else{
			delPicture.setVisibility(View.GONE);
			v.setVisibility(View.GONE);
		}
		
		
		Window window = ad.getWindow();
		
		window.setGravity(Gravity.BOTTOM);// �ڵײ�����
		window.setWindowAnimations(R.style.CustomDialog);
		ad.show();
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ad.dismiss();
			}
		});
		//ɾ��ͼƬ
		delPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String fileName=FileUtility.getFileRealName(imageName);
				SubmitDeleteinfo(fileName,1);
				ad.dismiss();
			}
		});
		//��ʾ��ͼ
		showPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				List<String> picturePaths = new ArrayList<String>();// ѡ�е�ͼƬ·��
				List<ImageItem> images=questions.get(curIndex).getImages();
				if(images != null)
				{
					for (int i = 0; i < images.size(); i++) {
						picturePaths.add(images.get(i).getDownAddress());
					}
				}
				Intent intent = new Intent(getActivity(), ImagesActivity.class);
				intent.putStringArrayListExtra("pics", (ArrayList<String>) picturePaths);
				
				for (int i = 0; i < picturePaths.size(); i++) {
					if(picturePaths.get(i).equals(imageName)){
						intent.putExtra("position", i);
					}
				}
				startActivity(intent);
				ad.dismiss();
			}
		});
	}


	/**
	 * ����ϵͳ������ջ�ȡͼƬ
	 * 
	 * @param
	 */
	private void getPictureByCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// ����android�Դ��������
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����
			AppUtility.showToastMsg(getActivity(), getString(R.string.Commons_SDCardErrorTitle));
			return;
		}
		picturePath =FileUtility.getRandomSDFileName("jpg");
		
		File mCurrentPhotoFile = new File(picturePath);
		Uri uri = Uri.fromFile(mCurrentPhotoFile);
		
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, REQUEST_CODE_TAKE_CAMERA);
	}

	/**
	 * ��������:�ӱ��ػ�ȡͼƬ
	 * 
	 * @author shengguo 2014-5-8 ����10:58:45
	 * 
	 */
	private void getPictureFromLocation() {
		/*
		picturePaths.remove("");
		Intent intent = new Intent(getActivity(),AlbumActivity.class);
		intent.putStringArrayListExtra("picturePaths",
				(ArrayList<String>) picturePaths);
		intent.putExtra("size", 5);
		startActivityForResult(intent,
				SchoolDetailActivity.REQUEST_CODE_TAKE_PICTURE);
		*/
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// �ж��Ƿ���SD��
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
			AppUtility.showToastMsg(getActivity(), "SD��������");
		}
		
	}
	/**
	 * ��ʾ����ʧ����ʾҳ
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
	 * ��������:��ȡ�ʾ�����
	 * 
	 * @author shengguo 2014-4-16 ����11:12:43
	 * 
	 */
	public void getQuestionsItem() {
		showProgress(true);
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jsonObj=null;
		try {
			jsonObj = new JSONObject(interfaceName);
			jsonObj.put("�û�������", checkCode);
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPostToDandian(jsonObj, mHandler, 0);
	}

	/**
	 * ��������:�����ʾ��
	 * 
	 * @author shengguo 2014-5-5 ����5:29:30
	 * 
	 */
	private void saveQuestionAnswer() {
		JSONArray joarr = getAnswers();
		if(joarr==null){
			return ;
		}
		progressDlg=ProgressDialog.show(getActivity(), "", getString(R.string.saving),true,false);
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jsonObj=null;
		try {
			jsonObj = new JSONObject(questionnaireList.getSubmitTo());
			jsonObj.put("�û�������", checkCode);
			jsonObj.put("ѡ���¼��", joarr.toString());
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPostToDandian(jsonObj, mHandler, 1);
	}
	
	/**
	 * ��������:�����ļ��ϴ�
	 * 
	 * @author shengguo 2013-12-26 ����4:36:51
	 * 
	 * @param mCurrentFile
	 *
	 */
	public void uploadFile(File file,int type)  {
		if(!file.exists()) return;
		if(AppUtility.formetFileSize(file.length()) > 5242880*2){
			AppUtility.showToastMsg(getActivity(), "�Բ������ϴ����ļ�̫���ˣ���ѡ��С��10M���ļ���");
		}else{
			 
	        try
	        {
	        	if(type==1)
	        		ImageUtility.rotatingImageIfNeed(file.getAbsolutePath());
	        	DownloadSubject	downloadSubject = new DownloadSubject();
				String filebase64Str = FileUtility.fileupload(file);
				downloadSubject.setFilecontent(filebase64Str);
				String filename = file.getName();
				downloadSubject.setFileName(filename);
				downloadSubject.setLocalfile(file.getAbsolutePath());
				downloadSubject.setFilesize(file.length());
				SubmitUploadFile(downloadSubject,type);
				
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
		}
	}
	/**
	 * ��������:�ϴ��ļ�
	 *
	 * @author shengguo  2013-12-18 ����11:48:59
	 * 
	 * @param base64Str
	 * @param action
	 */
	public void SubmitUploadFile(final DownloadSubject downloadSubject,final int type){
		
		final CampusParameters params = new CampusParameters();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");// ��ȡ�û�У����
		params.add("�û�������", checkCode);
		params.add("�γ�����", questionnaireList.getTitle());
		params.add("��ʦ�Ͽμ�¼���", questionnaireList.getTitle());
		params.add("ͼƬ���", "�ʾ����");
		params.add("�ļ�����", downloadSubject.getFileName());
		params.add("WenJianMing", downloadSubject.getFileName());
		params.add("�ļ�����", downloadSubject.getFilecontent());
		if(type==1)
		{
			List<String> picturePaths=myPictureAdapter.getPicPaths();
			picturePaths.remove("");
			picturePaths.add("loading");
			myPictureAdapter.setPicPaths(picturePaths);
		}
		else if(type==2)
		{
			progressDlg=ProgressDialog.show(getActivity(), "", "�ϴ���...",true,false);
		}
		//myPictureAdapter.notifyDataSetChanged();
		
		CampusAPI.uploadFiles(params, new RequestListener(){

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "------------------response"+response);
				
				Message msg = new Message();
				msg.what = 3;
				msg.obj = response;
				Bundle data=new Bundle();
				data.putString("oldFileName", params.getValue("�ļ�����"));
				data.putInt("type",type);
				msg.setData(data);
				mHandler.sendMessage(msg);	
				
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "ͼƬ�ϴ�ʧ��");
				
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);	
			}
		});
	}
	/**
	 * ��������:��ȡ��
	 * 
	 * @author shengguo 2014-5-5 ����5:37:56
	 * 
	 * @return
	 */
	private JSONArray getAnswers() {
		JSONArray joarr = new JSONArray();
		for (int i = 0; i < questions.size(); i++) {
			String mStatus = questions.get(i).getStatus();
			if(mStatus.equals("ͼƬ"))
			{
				JSONArray joimages = new JSONArray();
				List<ImageItem> images=questions.get(i).getImages();
				for (ImageItem imageItem :images) {
					JSONObject joimgs = new JSONObject();
					try {
						joimgs.put("�ļ���", imageItem.getFileName());
						joimgs.put("�ļ���ַ", imageItem.getDownAddress());
						joimgs.put("�γ�����", imageItem.getCurriculumName());
						joimgs.put("���ش���", imageItem.getLoadCount());
						joimgs.put("�Ͽμ�¼���", imageItem.getSubjectId());
						joimgs.put("���һ������", imageItem.getLastDown());
						joimgs.put("����", imageItem.getName());
						joimgs.put("STATUS", "OK");
						joimages.put(joimgs);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				joarr.put(joimages);
			}
			else if(mStatus.equals("����"))
			{
				
				JSONArray fujianArray=questions.get(i).getFujianArray();
				for (int j=0;i<fujianArray.length();j++) {
					JSONObject joimgs = null;
					try {
						joimgs = fujianArray.getJSONObject(j);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						joimgs.put("�ļ���", joimgs.optString("newname"));
						joimgs.put("�ļ���ַ",joimgs.optString("url"));
						joimgs.put("�γ�����", questionnaireList.getTitle());
						joimgs.put("���ش���","0");
						joimgs.put("�Ͽμ�¼���", questionnaireList.getTitle());
						joimgs.put("���һ������", "0");
						joimgs.put("����", joimgs.optString("name"));
						joimgs.put("STATUS", "OK");
						fujianArray.put(joimgs);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				joarr.put(fujianArray);
			}
			else
			{
				String usersAnswer = questions.get(i).getUsersAnswer();
				String isRequired = questions.get(i).getIsRequired();//�Ƿ����
				if(AppUtility.isNotEmpty(isRequired)){
					if(isRequired.equals("��")){
						if(AppUtility.isNotEmpty(usersAnswer)){
							joarr.put(usersAnswer);
						}else{
							AppUtility.showToastMsg(getActivity(),"������ʾ����ύ");
							myListview.setSelection(i);
							
							return null;
						}
					}else{
						joarr.put(usersAnswer);
					}
				}else{
					if(AppUtility.isNotEmpty(usersAnswer)){
						joarr.put(usersAnswer);
					}else{
						Log.d(TAG, "222"+questions.get(i).getTitle());
						AppUtility.showToastMsg(getActivity(),"������ʾ����ύ");
						myListview.setSelection(i);
						return null;
					}
				}
			}
		}
		Log.d(TAG, joarr.toString());
		return joarr;
	}
	/**
	 * ��������:ɾ��ͼƬ
	 *
	 * @author shengguo  2014-5-9 ����12:05:03
	 * 
	 * @param fileName
	 */
	public void SubmitDeleteinfo(String fileName,final int type) {
		JSONObject jo = new JSONObject();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "--------------filename----------" + fileName);
		try {
			jo.put("�û�������", checkCode);
			jo.put("DATETIME", String.valueOf(new Date().getTime()));
			jo.put("�μ�����", fileName);
			jo.put("ͼƬ���", "�ʾ����");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString()
				.getBytes());
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
				Message msg = new Message();
				msg.what = 2;
				msg.obj = response;
				Bundle data=new Bundle();
				data.putInt("type",type);
				msg.setData(data);
				mHandler.sendMessage(msg);
			}
		});
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
			case REQUEST_CODE_TAKE_CAMERA: // ���շ���
				//Bundle bundle = data.getExtras();
				//Bitmap bitmap = (Bitmap) bundle.get("data");// ��ȡ������ص����ݣ���ת��ΪBitmapͼƬ��ʽ
				//ImageUtility.writeTofiles(bitmap, picturePath);
				uploadFile(new File(picturePath),1);
				
				break;
			case REQUEST_CODE_TAKE_PICTURE:
				
				if (data != null) {
					Uri uri = data.getData();
					String[] pojo  = { MediaStore.Images.Media.DATA };
					CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, pojo, null,null, null); 
					Cursor cursor = cursorLoader.loadInBackground();
					cursor.moveToFirst(); 
				    picturePath = cursor.getString(cursor.getColumnIndex(pojo[0])); 
					
					String tempPath =FileUtility.getRandomSDFileName("jpg");
					if(FileUtility.copyFile(picturePath,tempPath))
						uploadFile(new File(tempPath),1);
					else
						AppUtility.showErrorToast(getActivity(), "��SD�������ļ�����");
				}
				break;
			case REQUEST_CODE_TAKE_DOCUMENT:
				if (resultCode == Activity.RESULT_OK) 
				{
					Uri uri = data.getData();
					String filepath=FileUtility.getFilePathInSD(getActivity(),uri);
					if(filepath!=null)
						uploadFile(new File(filepath),2);
					
				}
		}
	};

	

	class QuestionAdapter extends BaseAdapter {
	
		int mFocusPosition = -1;
		OnFocusChangeListener mListener = new OnFocusChangeListener() {

	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	            int position = (Integer) v.getTag();
	            if (hasFocus) {
	                mFocusPosition = position;
	            }
	            Log.e("test", "onFocusChange:" + position + " " + hasFocus);
	        }
	    };
		@Override
		public int getCount() {
			return questions.size();
		}

		@Override
		public Object getItem(int position) {
			return questions.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	
		@SuppressWarnings("deprecation")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
						
			final Question question = (Question) getItem(position);
			convertView = inflater.inflate(R.layout.school_quiz_question_item, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.tv_qno=(TextView) convertView.findViewById(R.id.tv_qno);
			holder.tv_qstate=(TextView) convertView.findViewById(R.id.tv_qstate);
			holder.title = (TextView) convertView.findViewById(R.id.tv_questionnaire_name);
			holder.prompt = (TextView) convertView.findViewById(R.id.tv_prompt);
			holder.radioGroup = (RadioGroup) convertView.findViewById(R.id.rg_choose);
			holder.multipleChoice = (NonScrollableListView) convertView.findViewById(R.id.lv_choose);
			holder.etAnswer = (EditText) convertView.findViewById(R.id.et_answer);
			holder.tvAnswer = (TextView) convertView.findViewById(R.id.tv_answer);
			holder.imageGridView = (NonScrollableGridView) convertView.findViewById(R.id.grid_picture);
			holder.tvRemark = (TextView) convertView.findViewById(R.id.tv_remark);
			holder.bt_date=(Button)convertView.findViewById(R.id.bt_date);
			holder.bt_datetime=(Button)convertView.findViewById(R.id.bt_datetime);
			holder.sp_select=(Spinner)convertView.findViewById(R.id.sp_select);
			holder.etAnswer.setOnFocusChangeListener(mListener);
			holder.etAnswer.setTag(position);
			
			String mStatus = question.getStatus();
			holder.tv_qno.setText(getString(R.string.question)+question.getSlot());
			holder.tv_qstate.setText(question.getState()+'\n'+question.getGrade());
			Spanned spanned = Html.fromHtml(question.getTitle(), new MyImageGetter(getActivity(),holder.title), null);
			holder.title.setText(spanned);
			holder.prompt.setText(question.getPrompt());
			if(!isEnable){
				String remark = question.getRemark();
				Log.d(TAG, "-----------remark:"+remark);
				
				holder.tvRemark.setVisibility(View.INVISIBLE);
				if(AppUtility.isNotEmpty(remark) && !mStatus.equals("�����ı������") && !mStatus.equals("ͼƬ")){
					holder.tvRemark.setText(remark);
					holder.tvRemark.setVisibility(View.VISIBLE);
					
					if(remark.length()>7 && (remark.substring(0, 7).equals("����״̬:����") || remark.indexOf("error")>0)){
						holder.tvRemark.setTextColor(getActivity().getResources().getColor(R.color.red_color));
					}else if(remark.length()>7 && (remark.substring(0, 7).equals("����״̬:��ȷ") || remark.indexOf("right")>0)){
						holder.tvRemark.setTextColor(getActivity().getResources().getColor(R.color.title_nor));
					}
					else
						holder.tvRemark.setTextColor(Color.BLUE);
				}
			}
			if (mStatus.equals("��ѡ")) {
				holder.prompt.setVisibility(View.GONE);
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.VISIBLE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				final String[] answers = question.getOptions();
				holder.radioGroup.removeAllViews();
				int checkIndex = -1;
				holder.radioGroup.setOnCheckedChangeListener(null);
				for (int i = 0; i < answers.length; i++) {
					View v= inflater.inflate(R.layout.my_radiobutton, parent, false);
					RadioButton radioButton = (RadioButton) v.findViewById(R.id.rb_chenck);
					radioButton.setText(answers[i].toString());
					radioButton.setTextSize(12.0f);
					radioButton.setId(i);
					radioButton.setEnabled(isEnable);
					if (answers[i].equals(question.getUsersAnswer())) {
						checkIndex = i;
					}
					holder.radioGroup.addView(radioButton);
				}
				if (checkIndex != -1) {
					holder.radioGroup.clearCheck();
					holder.radioGroup.check(checkIndex);
				} else {
					holder.radioGroup.clearCheck();
				}
				holder.radioGroup
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(RadioGroup group,
									int checkedId) {
								Log.d(TAG, "ѡ����" + answers[checkedId]);
								question.setUsersAnswer(answers[checkedId]);
								questions.set(position, question);
								questionnaireList.setQuestions(questions);
							}
						});
			} else if (mStatus.equals("��ѡ")) {
				holder.prompt.setVisibility(View.GONE);
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.VISIBLE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(
						getActivity(), position, question);
				holder.multipleChoice.setAdapter(checkBoxAdapter);
				
			} else if (mStatus.equals("�����ı������")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				
				if (status.equals("�ѽ���") || status.equals("δ��ʼ")) {
					holder.etAnswer.setVisibility(View.GONE);
					holder.tvAnswer.setVisibility(View.VISIBLE);
					holder.tvAnswer.setText(question.getUsersAnswer());
				} else {
					holder.etAnswer.setVisibility(View.VISIBLE);
					holder.tvAnswer.setVisibility(View.GONE);
					holder.etAnswer.setText(question.getUsersAnswer());
					if(question.getLines()==1)
						holder.etAnswer.setSingleLine(true);
					else if(question.getLines()>1)
					{
						holder.etAnswer.setLines(question.getLines());
						holder.etAnswer.setSingleLine(false);
					}
					
					if (mFocusPosition == position) {
						holder.etAnswer.requestFocus();
			        } else {
			        	holder.etAnswer.clearFocus();
			        }
					holder.etAnswer.addTextChangedListener(new TextWatcher() {
						
					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						Log.d(TAG, "-------------" + s);
						
					}
	
					@Override
					public void beforeTextChanged(CharSequence s,
							int start, int count, int after) {
						// TODO Auto-generated method stub
	
					}
	
					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub
						question.setUsersAnswer(s.toString());
						questions.set(position, question);
						questionnaireList.setQuestions(questions);
					}
				});
					
				}
			} else if (mStatus.equals("ͼƬ")) {
				holder.imageGridView.setVisibility(View.VISIBLE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				
				int size=5;
				if(question.getLines()>0)
					size=question.getLines();
				myPictureAdapter = new MyPictureAdapter(getActivity(),isEnable,new ArrayList<String>(),size,"�����ʾ�");
				myPictureAdapter.setFrom(TAG);
				myPictureAdapter.setCurIndex(position);
				myPictureAdapter.setPicPathsByImages(question.getImages());
				holder.imageGridView.setAdapter(myPictureAdapter);
			}
			else if (mStatus.equals("����")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.VISIBLE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				if(!AppUtility.isNotEmpty(question.getUsersAnswer()))
				{
					question.setUsersAnswer(DateHelper.getToday());
				}
				holder.bt_date.setText(question.getUsersAnswer());

				holder.bt_date.setOnClickListener(new OnClickListener(){
					
					private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener(){  //
						@Override
						public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
						
							question.setUsersAnswer(DateHelper.getDateString(new Date(arg1-1900,arg2,arg3), "yyyy-MM-dd"));
							Button bt=(Button)arg0.getTag();
							bt.setText(question.getUsersAnswer());
						}
					};
						
					@Override
					public void onClick(View v) {
						Date dt=DateHelper.getStringDate(question.getUsersAnswer(), "yyyy-MM-dd");
						Calendar cal=Calendar.getInstance();
						cal.setTime(dt);
						DatePickerDialog dialog = new DatePickerDialog(getActivity(),listener,cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
						dialog.getDatePicker().setTag(v);
						if(question.getOptions().length==2)
						{
							Date minDt=DateHelper.getStringDate(question.getOptions()[0], "yyyy-MM-dd");
							Date maxDt=DateHelper.getStringDate(question.getOptions()[1], "yyyy-MM-dd");
							dialog.getDatePicker().setMinDate(minDt.getTime());
							dialog.getDatePicker().setMaxDate(maxDt.getTime());
						}
						dialog.setButton2("ȡ��", new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						dialog.show();
					}
					
				});
	
			}
			else if (mStatus.equals("����ʱ��")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.VISIBLE);
				holder.sp_select.setVisibility(View.GONE);
				if(!AppUtility.isNotEmpty(question.getUsersAnswer()))
				{
					question.setUsersAnswer(DateHelper.getToday());
				}
				holder.bt_datetime.setText(question.getUsersAnswer());

				holder.bt_datetime.setOnClickListener(new OnClickListener(){
				
					@Override
					public void onClick(View v) {
						
						DateTimePickDialogUtil dialog = new DateTimePickDialogUtil(getActivity(),question.getUsersAnswer(),"yyyy-MM-dd HH:mm");
						Button bt=(Button)v;
						bt.setTag(question);
						dialog.dateTimePicKDialog(bt);
						
					}
					
				});
	
			}
			else if (mStatus.equals("����")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.VISIBLE);
				ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,question.getOptions());
				holder.sp_select.setAdapter(aa);
				int pos=0;
				for(int i=0;i<question.getOptions().length;i++)
				{
					if(question.getOptions()[i].equalsIgnoreCase(question.getUsersAnswer()))
						pos=i;
				}
				holder.sp_select.setSelection(pos);
				holder.sp_select.setOnItemSelectedListener(new OnItemSelectedListener() {
					
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						question.setUsersAnswer(question.getOptions()[position]);
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
						
					}
				});
				
			}
			else if (mStatus.equals("����")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.VISIBLE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				SimpleAdapter fujianAdapter=setupFujianAdpter(question);
				holder.multipleChoice.setAdapter(fujianAdapter);
				holder.multipleChoice.setTag(position);
				holder.multipleChoice.setOnItemClickListener(new OnItemClickListener(){  
					
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						curIndex=(int)parent.getTag();
						final HashMap<String, Object> item=(HashMap<String, Object>) parent.getAdapter().getItem(position);;
						if(item.get("url").toString().length()>0)
						{
							new AlertDialog.Builder(view.getContext())
						    .setMessage("�Ƿ�ɾ���˸���?")
						    .setPositiveButton("��", new DialogInterface.OnClickListener()
						    {
						    @Override
						    public void onClick(DialogInterface dialog, int which) {
						      
						    	SubmitDeleteinfo(item.get("newname").toString(),2);
						    }})
						    .setNegativeButton("��", null)
						    .show();
						}
						else
						{
							Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
			                intent.setType("*/*");
			                intent.addCategory(Intent.CATEGORY_OPENABLE);
			                startActivityForResult(Intent.createChooser(intent, "��ѡ��һ��Ҫ�ϴ����ļ�"), REQUEST_CODE_TAKE_DOCUMENT); 
						}
					}     
				});
	
			}
			return convertView;
		}

		class ViewHolder {
			TextView tv_qno;
			TextView tv_qstate;
			TextView title;
			TextView prompt;
			EditText etAnswer;
			TextView remark;
			TextView tvAnswer;
			TextView tvRemark;
			RadioGroup radioGroup;
			NonScrollableListView multipleChoice;
			NonScrollableGridView imageGridView;
			Spinner sp_select;
			Button bt_date;
			Button bt_datetime;
		}
		
	}
	private SimpleAdapter setupFujianAdpter(Question question)
	{
		final ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String,Object>>();  
		for(int i=0;i<question.getFujianArray().length();i++){  
        	JSONObject item = null;
			try {
				item = (JSONObject) question.getFujianArray().get(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(item!=null)
			{
	            HashMap<String, Object> tempHashMap = new HashMap<String, Object>();  
	            tempHashMap.put("name", item.optString("name"));
	            tempHashMap.put("url", item.optString("url"));
	            tempHashMap.put("newname", item.optString("newname"));
	            arrayList.add(tempHashMap);  
			}
              
        } 
		if(arrayList.size()<question.getLines())
		{
			HashMap<String, Object> tempHashMap = new HashMap<String, Object>();  
            tempHashMap.put("name", "��Ӹ���");
            tempHashMap.put("url", "");
            arrayList.add(tempHashMap);  
		}
		SimpleAdapter fujianAdapter = new SimpleAdapter(getActivity(), arrayList, R.layout.list_item_simple,  
                new String[]{"name"}, new int[]{R.id.item_textView});
		return fujianAdapter;
	}
	/**
	 * 
	 *  #(c) ruanyun PocketCampus <br/>
	 *
	 *  �汾˵��: $id:$ <br/>
	 *
	 *  ����˵��: ���ض�ѡ�б�
	 * 
	 *  <br/>����˵��: 2014-5-17 ����9:44:52 shengguo  �����ļ�<br/>
	 * 
	 *  �޸���ʷ:<br/>
	 *
	 */
	@SuppressWarnings("unused")
	private class CheckBoxAdapter extends BaseAdapter {

		private Context context;
		private String[] anwsers;
		private String anwser;
		private int questionIndex;// question ��list�е��±�
		private Question question;
		public Map<String, Boolean> isChecked = new HashMap<String, Boolean>();

		public CheckBoxAdapter(Context context, int questionIndex,
				Question question) {
			super();
			this.context = context;
			this.questionIndex = questionIndex;
			this.question = question;
			anwsers = question.getOptions();
			anwser = question.getUsersAnswer();
			initDate();
		}

		private void initDate() {

			String[] arr = anwser.split("@");
			List<String> list = Arrays.asList(arr);
			for (int i = 0; i < anwsers.length; i++) {
				if (list.contains(anwsers[i])) {
					isChecked.put(anwsers[i], true);
				} else {
					isChecked.put(anwsers[i], false);
				}
			}
		}

		public String getAnwser() {
			StringBuffer str = new StringBuffer();
			for (int i = 0; i < anwsers.length; i++) {
				if (isChecked.get(anwsers[i])) {
					str.append(anwsers[i]).append("@");
				}
			}
			if (str.indexOf(",") > -1) {
				str.deleteCharAt(str.lastIndexOf("@"));
			}
			return str.toString();
		}

		public void setAnwser(String anwser) {
			this.anwser = anwser;
		}

		@Override
		public int getCount() {
			return anwsers.length;
		}

		@Override
		public Object getItem(int position) {
			return anwsers[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			view = inflater.inflate(R.layout.checkbox_item, parent, false);
			final CheckBox cb = (CheckBox) view.findViewById(R.id.cb_chenck);

			cb.setText(anwsers[position]);
			if (isChecked.get(anwsers[position])) {
				cb.setChecked(true);
			} else {
				cb.setChecked(false);
			}
			cb.setEnabled(isEnable);
			cb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Boolean flag = cb.isChecked();
					isChecked.put(anwsers[position], flag);
					String answer = getAnwser();
					Log.d(TAG, "---------" + answer +question.getStatus()+"ss" +question.getTitle());
					question.setUsersAnswer(answer);
					questions.set(questionIndex, question);
					questionnaireList.setQuestions(questions);
				}
			});
			return view;
		}
	}
	

}
