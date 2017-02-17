package com.dandian.pocketmoodle.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.adapter.MyLocalFileAdapter;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.util.AppUtility;

/**
 * ��ȡͼƬ����Ƶ������ͼ ����������������2.2�����ϰ汾ʹ�ã���Ϊ����ʹ����ThumbnailUtils�����
 */
public class LocalDocument extends Activity {
	private MyLocalFileAdapter adapter;
	
	private String filepath;
	private LinearLayout initLayout;
	private List<File> listFiles = new ArrayList<File>();
	private String videoPath;
	private File root;
	private TextView fileText;
	private List<String> videopaths = new ArrayList<String>();
	private ListView grid;
	private static ArrayList<String> VIDEO_SUFFIX = new ArrayList<String>();
	static {
		VIDEO_SUFFIX.add(".ppt");
		VIDEO_SUFFIX.add(".doc");
		VIDEO_SUFFIX.add(".docx");
		VIDEO_SUFFIX.add(".xls");
		VIDEO_SUFFIX.add(".xlsx");
		VIDEO_SUFFIX.add(".zip");
		VIDEO_SUFFIX.add(".rar");
		VIDEO_SUFFIX.add(".pdf");
		//VIDEO_SUFFIX.add(".txt");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localdocuments);
		grid = (ListView) findViewById(R.id.grid);
		initLayout = (LinearLayout)findViewById(R.id.initlayout);
		fileText = (TextView) findViewById(R.id.fileText);
		initLayout.setVisibility(View.VISIBLE);
		grid.setVisibility(View.GONE);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			filepath = "/mnt";
			root = new File(filepath);
		} else {
			Toast.makeText(this, "û��SD��", Toast.LENGTH_LONG).show();
			finish();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				listFiles = getAllFiles(root);
				Message msg = mHandler.obtainMessage();
				msg.what = 0;
				mHandler.sendMessage(msg);

			}
		}).start();

	}

	/**
	 * ��������:�ж��Ƿ�����Ƶ�ļ�
	 * 
	 * @author linrr 2013-12-26 ����4:05:45
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isVideoFile(String fileName) {

		if (fileName.lastIndexOf(".") < 0) // Don't have the suffix
			return false;
		String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
		if (VIDEO_SUFFIX.contains(fileSuffix)) {
			System.out.println(fileName + ".........................."
					+ fileSuffix);
			return true;
		} else {
			return false;
		}

	}
	

	/**
	 * ��������:�����ļ�
	 * 
	 * @author linrr 2013-12-26 ����4:04:10
	 * 
	 * @param root
	 * @return
	 */
	private List<File> getAllFiles(File root) {
		List<File> list = new ArrayList<File>();
		File files[] = root.listFiles();
		if (files != null&&files.length>0) {
			for (File f : files) {
				if(f.getName().equals("PocKetCampus")) continue;
				if(f.getPath().equals("/mnt/shell")) continue;
				if (f.isDirectory()&&f.canRead()&&!f.isHidden()&&!f.getName().contains(".")&&f.list().length>0) {
					list.addAll(getAllFiles(f));
					Message msg = mHandler.obtainMessage();
					msg.what = 1;
					msg.obj = f.getName();
					mHandler.sendMessage(msg);
				} else if(isVideoFile(f.getName())){
					list.add(f);
					Message msg = mHandler.obtainMessage();
					msg.what = 2;
					msg.obj = f.getName();
					mHandler.sendMessage(msg);
					videoPath = f.getAbsolutePath();
					videopaths.add(videoPath);
					
				}
			}
		}
		return list;
	}
	/**
	 * 
	 * #(c) ruanyun PocketCampus <br/>
	 * 
	 * �汾˵��: $id:$ <br/>
	 * 
	 * ����˵��:
	 * 
	 * <br/>
	 * ����˵��: 2013-12-26 ����4:04:56 linrr �����ļ�<br/>
	 * 
	 * �޸���ʷ:<br/>
	 * 
	 * @param parent
	 *            ����ǰ��gridview
	 * @param ��������item
	 * @param ��ǰ�����item�������е�λ��
	 * @param id
	 *            The row id of the item that was clicked.
	 */
	private class MainItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent;
			intent = getIntent();
			File file=new File(videopaths.get(position));
			if(file.length()>Constants.kejianMaxSize)
			{
				AppUtility.showToastMsg(LocalDocument.this, "ѡ����ļ���С���ܳ���"+Constants.kejianMaxSize/1024/1024+"M");
				return;
			}
			
			intent.putExtra("paths", videopaths.get(position));
			System.out.println(videopaths.get(position) + ".....");
			setResult(5, intent);
			finish();
		}
	}
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if(listFiles.size() == 0){
					Toast.makeText(LocalDocument.this, "����û���κ��ĵ�Ŷ", Toast.LENGTH_SHORT).show();
					finish();
				}else{
					initLayout.setVisibility(View.GONE);
					grid.setVisibility(View.VISIBLE);
					adapter = new MyLocalFileAdapter(LocalDocument.this, listFiles);
					grid.setAdapter(adapter);
					grid.setOnItemClickListener(new MainItemClickListener());
				}
				break;
			case 1:
				fileText.setText("����ɨ�裺"+msg.obj.toString()+"�ļ���...");
				break;
			case 2:
				AppUtility.showToast(LocalDocument.this, "��ɨ�赽"+msg.obj.toString()+"�ĵ��ļ�", 10);
				break;
			}
		}

	};
}
