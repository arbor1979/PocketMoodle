package com.dandian.pocketmoodle.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.DateHelper;
import com.dandian.pocketmoodle.util.FileUtility;
import com.dandian.pocketmoodle.util.IntentUtility;
import com.dandian.pocketmoodle.util.PrefUtility;

/**
 * 信息
 * 
 * @Title NoticeAcitivity.java
 * @Description: TODO
 * 
 * @author Zecker
 * @date 2013-10-23 下午4:47:47
 * @version V1.0
 * 
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class WebSiteActivity extends Activity {
	private static final String TAG = "WebSiteActivity";
	public static Date loginDate;
	
	private WebView mWebView; 
	private WebChromeClient chromeClient = null;
	private View myView = null;
	private WebChromeClient.CustomViewCallback myCallBack = null;
	private LinearLayout frameLayout = null,webNavBar;
	private FrameLayout loading=null;
	private RelativeLayout layoutHead;
	private Button back,forward;
	private String needLoadHtml="";
	private String moodleText="";
	private ValueCallback<Uri> mUploadMessage;
	private ValueCallback<Uri[]> mUploadMessageForAndroid5;
	private final int FILECHOOSER_RESULTCODE=101;
	private final int FILECHOOSER_RESULTCODE_FOR_ANDROID_5=102;
	
	@SuppressLint({ "SetJavaScriptEnabled", "CutPasteId" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_site);
		frameLayout = (LinearLayout)findViewById(R.id.mainLayout);
		loading=(FrameLayout)findViewById(R.id.loading);
		mWebView = (WebView) findViewById(R.id.website);
		layoutHead = (RelativeLayout) findViewById(R.id.headerlayout);
		webNavBar=(LinearLayout)findViewById(R.id.webNavBar);
		back=(Button)findViewById(R.id.navBack);
		forward=(Button)findViewById(R.id.navForword);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				mWebView.goBack();
			}
			
		});
		forward.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				mWebView.goForward();
			}
			
		});
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.getSettings().setJavaScriptEnabled(true);   
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.getSettings().setSupportZoom(false);  
		mWebView.getSettings().setDisplayZoomControls(false);  
		//mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);   
		//mWebView.getSettings().setPluginState(PluginState.ON);
		mWebView.getSettings().setDefaultTextEncodingName("gbk");
		Button back=(Button) findViewById(R.id.back);
		back.setText(getString(R.string.close));
		/*
		mWebView.getSettings().setDatabaseEnabled(true);
		mWebView.getSettings().setAppCacheEnabled(true);
		mWebView.getSettings().setAppCachePath(FileUtility.creatSDDir("webCache"));
		*/
		String url = getIntent().getStringExtra("url");
		moodleText = getIntent().getStringExtra("htmlText");
		String title = getIntent().getStringExtra("title");
		
		TextView tv_title = (TextView) findViewById(R.id.setting_tv_title);
		if(title!=null && title.length()>10)
		{
			tv_title.setTextSize(18);
			/*
			if(title.length()>12)
			title=title.substring(0, 12)+"..";*/
		}
		else 
			title="";
		tv_title.setText(title);
		mWebView.setWebViewClient(new MyWebClient());
		//mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.setVisibility(View.GONE);
		mWebView.setDownloadListener(new MyWebViewDownLoadListener());
		chromeClient = new MyChromeClient();
		mWebView.addJavascriptInterface(chromeClient, "VideoComplete");
		mWebView.setWebChromeClient(chromeClient);
		
		CookieSyncManager.createInstance(this); 
		
		if(url!=null && url.length()>0)
			mWebView.loadUrl(url);
		else if(moodleText!=null && moodleText.length()>0)
		{
			Date now=new Date();
			
			if(loginDate==null || DateHelper.getMinutesDiff(now,loginDate)>20)
			{
				reloginmoodle();
			}
			else
			{
				mWebView.getSettings().setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
				mWebView.loadData(moodleText, "text/html; charset=UTF-8", null);
			}
			webNavBar.setVisibility(View.VISIBLE);
		}
		
		Button btn_back = (Button) findViewById(R.id.back);

		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	public void reloginmoodle()
	{
		String loginUrl =  getIntent().getStringExtra("loginUrl");
		String username=PrefUtility.get(Constants.PREF_LOGIN_NAME, "");
		username=username.split("@")[0];
		String password=PrefUtility.get(Constants.PREF_LOGIN_PASS, "");
		String postDate = "username="+username+"&password="+password;
		mWebView.postUrl(loginUrl, EncodingUtils.getBytes(postDate, "BASE64"));
		needLoadHtml=moodleText;
	}
	protected void onRestart() {
		super.onRestart();
		Date now=new Date();
		if(loginDate==null || DateHelper.getMinutesDiff(now,loginDate)>20)
		{
			reloginmoodle();
		}
	}
	
	final class DemoJavaScriptInterface {

        DemoJavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        public void clickOnAndroid() {
           

        }
    }
	
	public class MyChromeClient extends WebChromeClient implements OnCompletionListener {
		
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			if(myView != null){
				callback.onCustomViewHidden();
				return;
			}
			WebSiteActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			//frameLayout.removeView(mWebView);
			mWebView.setVisibility(View.GONE);
			layoutHead.setVisibility(View.GONE);
			webNavBar.setVisibility(View.GONE);
			frameLayout.setBackgroundColor(Color.BLACK);
			frameLayout.addView(view);
			myView = view;
			myCallBack = callback;
			chromeClient=this;
			
			FrameLayout frameLayout = (FrameLayout) view; 
            View focusedChild = frameLayout.getFocusedChild();
            
            if (focusedChild instanceof VideoView) 
            { 
                // VideoView (typically API level <11) 
                VideoView videoView = (VideoView) focusedChild; 
                // Handle all the required events 
                videoView.setOnCompletionListener(this); 
                
            }
            else // Usually android.webkit.HTML5VideoFullScreen$VideoSurfaceView, sometimes android.webkit.HTML5VideoFullScreen$VideoTextureView 
            { 
                // HTML5VideoFullScreen (typically API level 11+) 
                // Handle HTML5 video ended event 
                    String js = "javascript:"; 
                    js += "_ytrp_html5_video = document.getElementsByTagName('video')[0];"; 
                    js += "if (_ytrp_html5_video !== undefined) {"; 
                    { 
                        js += "function _ytrp_html5_video_ended() {"; 
                        { 
                            js += "_ytrp_html5_video.removeEventListener('ended', _ytrp_html5_video_ended);"; 
                            js += "VideoComplete.playVideoEnd();"; // Must match Javascript interface name and method of VideoEnableWebView 
                        } 
                        js += "}"; 
                        js += "_ytrp_html5_video.addEventListener('ended', _ytrp_html5_video_ended);"; 
                    } 
                    //js += "if(_ytrp_html5_video.paused) _ytrp_html5_video.play();}"; 
                    js += "}";
                    mWebView.loadUrl(js); 
            	
            } 
		}
		public void playVideoEnd() 
		{
			WebSiteActivity.this.frameLayout.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						onHideCustomView();
					}
				});
			}
		@Override 
		public void onCompletion(MediaPlayer mp) // Video finished playing, only called in the case of VideoView (typically API level <11) 
		{ 
		        onHideCustomView(); 
		} 
		@Override
		public void onHideCustomView() {
			if(myView == null){
				return;
			}
			WebSiteActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			frameLayout.removeView(myView);
			layoutHead.setVisibility(View.VISIBLE);
			frameLayout.setBackgroundColor(Color.WHITE);
			myView = null;
			mWebView.setVisibility(View.VISIBLE);
			if(moodleText!=null && moodleText.length()>0)
				webNavBar.setVisibility(View.VISIBLE);
			myCallBack.onCustomViewHidden();
			
		}
		
		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			// TODO Auto-generated method stub
			Log.d("ZR", consoleMessage.message()+" at "+consoleMessage.sourceId()+":"+consoleMessage.lineNumber());
			return super.onConsoleMessage(consoleMessage);
		}
		
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {  
            if (mUploadMessage != null) return;
            mUploadMessage = uploadMsg;   
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
                startActivityForResult( Intent.createChooser( i, getString(R.string.choose_upload)), FILECHOOSER_RESULTCODE);
             }
		
        //   For Android < 3.0
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser( uploadMsg, "" );
		}
 
		// For Android  > 4.1.1
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
           openFileChooser(uploadMsg, acceptType);
		}
		// For Android > 5.0
		public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
             openFileChooserImplForAndroid5(uploadMsg);
             return true;
         }
		private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
			mUploadMessageForAndroid5= uploadMsg;
	        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
	        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
	        contentSelectionIntent.setType("*/*");

	        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
	        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
	        chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.choose_upload));
	        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
	    }
		

	}
	 
	
	private class MyWebClient extends WebViewClient
	{
		 @Override  
	    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {  
	            back.setEnabled(mWebView.canGoBack());  
	            forward.setEnabled(mWebView.canGoForward());  
	            if(back.isEnabled())
	            	back.setBackgroundResource(R.drawable.bg_title_bar_btn_back_enable);
	            else
	            	back.setBackgroundResource(R.drawable.bg_title_bar_btn_back);
	            if(forward.isEnabled())
	            	forward.setBackgroundResource(R.drawable.bg_title_bar_btn_forword_enable);
	            else
	            	forward.setBackgroundResource(R.drawable.bg_title_bar_btn_forword);
	    } 
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if(moodleText!=null && moodleText.length()>0)
			{
				view.loadUrl("javascript:if(document.getElementById('region-main')) "
						+"{var mainHtml=document.getElementById('region-main').innerHTML;"
						//+"var reg=new RegExp('<script type=\"text/javascript\">>([^</script>]*)','g');"
						//+ "document.body.innerHTML=mainHtml+'<script type=\"text/javascript\">'+reg+'</script>;'"
						+ "document.body.innerHTML=mainHtml;"
						+ "var tags=document.getElementsByTagName('a');"
						+ "for(var i=0;i<tags.length;i++)"
						+ "{tags[i].innerHTML=decodeURIComponent(tags[i].innerHTML);}"
						+ "setTimeout('',100);}");
				
			}
			mWebView.setVisibility(View.VISIBLE);
			loading.setVisibility(View.GONE);
			
			if(needLoadHtml.length()>0)
			{
				needLoadHtml=needLoadHtml.replaceAll("pluginfile.php","pluginfile_dandian.php?");
				needLoadHtml="<head><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>"+needLoadHtml;
				
				/*
				needLoadHtml+="<script language=javascript>window.onload=function(){"
				+ "var tables=document.getElementsByTagName('table');"
						+ "for(var i=0;i<tables.length;i++)"
						+ "{tables[i].style.width='';}}</script>";
				*/
				mWebView.getSettings().setDefaultTextEncodingName("UTF-8"); //设置默认为utf-8
				mWebView.loadData(needLoadHtml, "text/html; charset=UTF-8", null);
				needLoadHtml="";
				loginDate=new Date();
			}
			
	    }
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
	        super.onPageStarted(view, url, favicon);
	        mWebView.setVisibility(View.GONE);
			loading.setVisibility(View.VISIBLE);
	    }  
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
		    if (url.startsWith("tel:")) {
		        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
		        return true;
		    } else if (url.startsWith("mailto:")) {
		        url = url.replaceFirst("mailto:", "");
		        url = url.trim();
		        Intent i = new Intent(Intent.ACTION_SEND);
		        i.setType("plain/text").putExtra(Intent.EXTRA_EMAIL, new String[]{url});
		        startActivity(i);
		        return true;
		    } else if (url.startsWith("geo:")) {
		        Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); 
		        startActivity(searchAddress); 
		        return true;
		    }
		    /*
		    else if(url.toLowerCase().endsWith(".swf"))
		    {
		    	
		    	if(!checkFlashplayer())
		    	{
		    		AppUtility.showToastMsg(WebSiteActivity.this, "请先安装flash插件");
		    		Intent installIntent = new Intent(  
                            "android.intent.action.VIEW");  
                    installIntent.setData(Uri  
                            .parse("market://details?id=com.adobe.flashplayer"));  
                    startActivity(installIntent);  
                    return true;
		    	}
		    	
		    	AppUtility.showToastMsg(WebSiteActivity.this,"由于安卓对swf格式文件支持不佳，建议在电脑上打开swf文件",1);
		    	//view.loadUrl(url);
		    	return true;
		    }
		    */
		    else
		    {
		    	//if(moodleText!=null && moodleText.length()>0)
		    	openUrlFile(view,url);
		        return true;
		    	
		    	/*
		    	else
		    	{
			    	String type=intent.getType();
			    	if(iOfficeInstall<3 && (type.equals("application/vnd.ms-powerpoint") || type.equals("application/vnd.ms-excel") || type.equals("application/msword") || type.equals("application/pdf")))
			    	{
			    		String packArray[]={"com.qo.android.am3","com.qo.android.tablet.am","cn.wps.moffice_eng","cn.wps.moffice_i18n_hd"};
			    		IntentUtility.checkAppByPackName(WebSiteActivity.this,intent,packArray,"打开此文件需要安装QuickOffice,是否安装?","http://laoshi.dandian.net/otherTools/QuickOffice.apk");
			    		iOfficeInstall++;
			    	}
			    	else if(iZipInstall<3 && (type.equals("application/x-rar-compressed") || type.equals("application/zip")))
					{
						String packArray[]={"ru.zdevs.zarchiver"};
						IntentUtility.checkAppByPackName(WebSiteActivity.this,intent,packArray,"打开此文件需要安装ZArchiver,是否安装?","http://laoshi.dandian.net/otherTools/ZArchiver.apk");
						iZipInstall++;
					}
					else
					{
						if()
							view.loadUrl(url);
					}
			    	return true;
		    	}
		    	*/
		    	
		    }
		   
		    
		}
		
	}
	@SuppressLint("DefaultLocale")
	private void openUrlFile(WebView view,String url)
	{
		url=url.replace("pluginfile.php", "pluginfile_dandian.php?");
		url=url.replace("draftfile.php", "pluginfile_dandian.php?");
    	String[] tempArray=url.split("\\?");
    	if(tempArray.length>2)
    		url=tempArray[0]+"?"+tempArray[1];
    	String path=FileUtility.creatSDDir("download");
		String fileName=url.substring(url.lastIndexOf("/")+1, url.length());
		if(fileName.toLowerCase().contains(".php"))
		{
			tempArray=fileName.split("=");
			fileName=Uri.decode(tempArray[tempArray.length-1]);
		}
		String filePath=path+fileName;
		//FileUtility.deleteFile(filePath);
		File file = new File(filePath);  
		Intent intent;
        if(file.exists())
        {
        	intent=IntentUtility.openUrl(filePath);
        	IntentUtility.openIntent(WebSiteActivity.this, intent,true);
        }
        else
        {
        	intent=IntentUtility.openUrl(url);
        	if(intent==null && !url.contains("pluginfile_dandian.php?"))
        	{
        		view.loadUrl(url);
        	}
        	else
        	{
        		/*
        		if(!IntentUtility.openIntent(WebSiteActivity.this, intent,false))
        		{
        			TabHostActivity.schoolService.downLoadUpdate(url, 1003);
        			AppUtility.showToastMsg(WebSiteActivity.this, getString(R.string.startdownloadbackground));
        		}
        		*/
        		ArrayList<String> tempList =new ArrayList<String>();
        		tempList.add("audio/*");
        		tempList.add("video/*");
        		tempList.add("image/*");
        		if(tempList.contains(intent.getType()))
        		{
        			if(!IntentUtility.openIntent(WebSiteActivity.this, intent,false))
        			{
        			//TabHostActivity.schoolService.downLoadUpdate(url, 1003);
        				downloadFile(url,file);
        			}
        		}
        		else
        			downloadFile(url,file);
        	}
        }
		
	}
	private void downloadFile(String url,File file)
	{
		 DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);  
	   	 Uri uri = Uri.parse(url);  
	   	 Request request = new Request(uri); 
	   	 request.setDestinationUri(Uri.fromFile(file)); 
	   	 request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
	   	 //request.setDestinationInExternalFilesDir(WebSiteActivity.this, null, "PacketCampus");
	   	 downloadManager.enqueue(request);  
	   	 AppUtility.showToastMsg(WebSiteActivity.this, "已开始后台下载..");
	}
	private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
        	
        	openUrlFile(mWebView,url);
        	/*
        	Intent intent=IntentUtility.openUrl(url);
        	if(intent==null)
	    	{
        		
        		Uri uri = Uri.parse(url);
        		intent = new Intent(Intent.ACTION_VIEW, uri);
        		startActivity(intent);
	    	}
        	else
        	{
        		if(!IntentUtility.openIntent(WebSiteActivity.this,intent,true))
        		{
        			Uri uri = Uri.parse(url);
            		intent = new Intent(Intent.ACTION_VIEW, uri);
            		startActivity(intent);
        		}
        	}
        	*/
        }

    }
	@Override
	public void onPause()
	{
		super.onPause();
		mWebView.onPause();
		mWebView.loadUrl("javascript:var v=document.getElementById('video1');if(v) v.pause();");
		CookieSyncManager.getInstance().stopSync();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		mWebView.onResume();
		CookieSyncManager.getInstance().startSync();
	}
	 @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE )
        {
            Toast.makeText(getApplicationContext(), R.string.openfullscreen, Toast.LENGTH_SHORT).show();
        }else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            Toast.makeText(getApplicationContext(), R.string.exitfullscreen, Toast.LENGTH_SHORT).show();
        }
    }
	 @Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
		 if(keyCode == KeyEvent.KEYCODE_BACK)
		 {
			 if(myView!=null)
			 {
				 chromeClient.onHideCustomView();
				 return true;
			 }
			 else
				 return super.onKeyDown(keyCode, event); 
		 }
		 else
			 return super.onKeyDown(keyCode, event); 
	 }
	 @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 
           if(requestCode==FILECHOOSER_RESULTCODE)
           {  
              if (null == mUploadMessage) return;  
              Uri result = data == null || resultCode != RESULT_OK ? null  
                               : data.getData();  
              if(result!=null)
              {
            	  rebuildUri(result);
              }
              mUploadMessage.onReceiveValue(result);  
               
               mUploadMessage = null;  
                         
             }
           else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5){
               if (null == mUploadMessageForAndroid5)
                   return;
               Uri result = (data == null || resultCode != RESULT_OK) ? null: data.getData();
               if (result != null) {
            	   rebuildUri(result);
                   mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
               } else {
                   mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
               }
               mUploadMessageForAndroid5 = null;
           }
	 }
	 private void rebuildUri(Uri result)
	 {
		 String img_path=FileUtility.getFilePathInSD(this,result);
         if(!img_path.startsWith("file://"))
       	  img_path="file://"+img_path;
          
         result=Uri.parse(Uri.decode(img_path));
         String filename=FileUtility.getUrlRealName(result.getPath());
         String filenameEncode=Uri.encode(filename);
         if(!filename.equals(filenameEncode))
         {
       	  	Uri destUri=Uri.parse(FileUtility.getFileDir(img_path)+"/"+filenameEncode);
       	  	FileUtility.copyFile(result.toString().replace("file://", ""), destUri.toString().replace("file://", ""));
         }
	 }
	 /*
	 private boolean checkFlashplayer() {
			PackageManager pm = getPackageManager();
			List<PackageInfo> infoList = pm
					.getInstalledPackages(PackageManager.GET_SERVICES);
			for (PackageInfo info : infoList) {
				if ("com.adobe.flashplayer".equals(info.packageName)) {
					return true;
				}
			}
			return false;
	}*/
}

