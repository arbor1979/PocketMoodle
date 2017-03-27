package com.dandian.pocketmoodle.util;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.activity.HomeActivity;
import com.dandian.pocketmoodle.activity.LoginActivity;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.service.Alarmreceiver;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;


public class AppUtility {
	private static final String TAG = "AppUtility";
	private static Toast mToast;
	public static DisplayImageOptions headOptions;
	
	public static void setViewHeightBasedOnChildren(ExpandableListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	public static void setListViewHeightBasedOnChildren(
			ExpandableListView listView) {
		// ListAdapter listAdapter = listView.getAdapter();
		ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getGroupCount(); i++) {

			for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
				View listItem = listAdapter.getGroupView(i, true, null,
						listView);
				// getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
				System.out.println("---------totalheight----------"
						+ totalHeight);
			}
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight;
//				+ (listView.getChildCount() * (listAdapter.getGroupCount() - 1));
		// params.height =totalHeight
		// + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
		System.out.println("----------height----------" + params.height);
		listView.setLayoutParams(params);
	}

	/**
	 * 功能描述:检查邮箱格式
	 * 
	 * @author zhuliang 2013-12-11 下午7:26:42
	 * 
	 * @param email
	 * @return
	 */
	public static final boolean checkEmail(String email) {
		Pattern pattern = Pattern
				.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(email);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}
	/**
	 * 功能描述:获取整个文件夹的大小
	 *
	 * @author linrr  2014-1-25 上午11:48:24
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static long getFileSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}
	/**
	 * 功能描述:快速取消Toast提示
	 *
	 * @author linrr  2014-1-25 上午11:32:21
	 * 
	 * @param context
	 * @param msg
	 * @param duration
	 */
	public static void showToast(Context context, String msg, int duration) {
        // if (mToast != null) {
        // mToast.cancel();
        // }
        // mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        if (mToast == null) {
                mToast = Toast.makeText(context, msg, duration);
        } else {
                mToast.setText(msg);
        }
        mToast.show();
}
   public static void cancelToast()
   {
	   if(mToast!=null)
		   mToast.cancel();
   }
	/**
	 * 功能描述:检查手机号码格式
	 * 
	 * @author zhuliang 2013-12-11 下午7:40:38
	 * 
	 * @param phone
	 * @return
	 */
	public static final boolean checkPhone(String phone) {
		Pattern pattern = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher matcher = pattern.matcher(phone);

		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	public static void report(Throwable e) {
		if (e == null)
			return;
		try {
			Log.d("reporting", Log.getStackTraceString(e));

			if (eh != null) {
				eh.uncaughtException(Thread.currentThread(), e);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static UncaughtExceptionHandler eh;

	public static void setExceptionHandler(UncaughtExceptionHandler handler) {
		eh = handler;
	}

	private static Context context;

	public static void setContext(Application app) {
		context = app.getApplicationContext();
	}

	public static Context getContext() {
		if (context == null) {
			Log.w(TAG, "getContext with null");
			Log.d(TAG, "debug", new IllegalStateException());
		}
		return context;
	}

	/**
	 * 获取sd卡的路径
	 * 
	 * @return
	 */
	@SuppressLint("SdCardPath")
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();
	}

	/**
	 * Show a prompt message
	 * 
	 * @param msg
	 *            message content
	 */
	public static void showToastMsg(Context context, String msg) {
		Toast toast=Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	public static void showToastMsg(Context context, String msg,int duration) {
		if(context!=null && msg!=null)
		{
			Toast toast=Toast.makeText(context, msg, duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	/**
	 * 生成UUID
	 * 
	 * @return
	 */
	public static String UUIDGenerator() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * MD5加密
	 * 
	 * @param str
	 * @return
	 */
	public static String MD5Encode(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString().substring(8, 24);
	}

	/**
	 * 判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
	/**
	 * 功能描述:将文件大小转换成MB,KB,GB形式
	 *
	 * @author linrr  2014-1-23 下午3:41:07
	 * 
	 * @param size
	 * @return
	 */
	public static int formetFileSize(long size){
		String length_display = null;
		int b_size = 0;
			if(size < 1024){
				length_display = size + "B";
			}else if(size < 1048576 && size >= 1024){
				if((size/1024+"").contains(".") && (size/1024+"").substring((size/1024+"").lastIndexOf(".")).length() > 3){
					length_display = (size/1024+"").substring(0,(size/1024+"").lastIndexOf(".")+3)  + "KB";
					b_size = Integer.parseInt(length_display.substring(0, length_display.indexOf("B")*1024));
				}else{
					length_display = size/1024*1024 + "B";
				}
			}else if(size <= 1073741824 && size >= 1048576){
				if((size/1048576+"").contains(".") && (size/1048576+"").substring((size/1048576+"").lastIndexOf(".")).length() > 3){
					length_display = (size/1048576+"").substring(0,(size/1048576+"").lastIndexOf(".")+3) + "MB";
				}else{
					length_display = size/1048576*1024*1024 + "B";
				}
				
			}else{
				if((size/1073741824+"").contains(".") && (size/1073741824+"").substring((size/1073741824+"").lastIndexOf(".")).length() > 3){
					length_display = (size/1073741824+"").substring(0,(size/1073741824+"").lastIndexOf(".")+3) + "GB";
				}else{
					length_display = size/1073741824 + "GB";
				}
				
			}
			b_size = Integer.parseInt(length_display.substring(0, length_display.indexOf("B")));
			return b_size;
		}

	/**
	 * 保持屏幕唤醒状态（即背景灯不熄灭）
	 * 
	 * @param context
	 * @param on
	 *            是否唤醒
	 */
	@SuppressLint("Wakelock")
	@SuppressWarnings({ "deprecation" })
	public static void keepScreenOn(Context context, boolean on) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
		if (on) {
			wl.acquire();
		} else {
			wl.release();
			wl = null;
		}
	}

	/**
	 * Check if the network is available. <br/>
	 * need <uses-permission
	 * android:name="android.permission.ACCESS_NETWORK_STATE" />
	 * 
	 * @param context
	 *            The current context.
	 * @return True if the network is available,false otherwise.
	 */
	public static boolean getNetworkIsAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			return false;
		}
		if (info.isRoaming()) {
			return true;
		}
		return true;
	}

	/**
	 * Check if the SD card is available.Display an alert if not.
	 * 
	 * @param context
	 *            The current context.
	 * @param showMessage
	 *            If true, will display a message for the user.
	 * @return True if the SD Card is available, false otherwise.
	 */
	public static boolean checkCardState(Context context, boolean showMessage) {
		// Check to see if we have an SDCard.
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			int messageId;
			// Check to see if the SDCard is busy,same as the music app.
			if (status.equals(Environment.MEDIA_SHARED)) {
				messageId = R.string.Commons_SDCardErrorSDUnavailable;
			} else {
				messageId = R.string.Commons_SDCardErrorNoSDMsg;
			}
			if (showMessage) {
				AppUtility.showErrorDialog(context,
						R.string.Commons_SDCardErrorTitle, messageId);
			}
			return false;
		}
		return true;
	}

	/**
	 * Show an error dialog.
	 * 
	 * @param context
	 *            The current context.
	 * @param title
	 *            The title string id.
	 * @param message
	 *            The message string id.
	 */
	public static void showErrorDialog(Context context, int title, int message) {
		new AlertDialog.Builder(context).setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(message)
				.setPositiveButton(R.string.go, null).show();
	}

	/**
	 * 检查IP地址是否正确
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean checkIPAddress(String ip) {
		Pattern pattern = Pattern
				.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	/**
	 * 功能描述：字符串转换int
	 * 
	 * @author yanzy 2013-11-26 下午12:02:05
	 * 
	 * @param str
	 * @return
	 */
	public static int parseInt(String str) {
		if (!"".equals(str) && !"null".equals(str) && str != null
				&& isNumeric(str)) {
			return Integer.parseInt(str);
		} else {
			return -1;
		}
	}

	public static float parseFloat(String str) {
		if (!"".equals(str) && !"null".equals(str) && str != null
				&& isNumeric(str)) {
			return Float.parseFloat(str);
		} else {
			return (float) 0.0;
		}
	}

	/**
	 * 功能描述:判断字符串不为空
	 * 
	 * @author yanzy 2013-11-26 下午1:02:37
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		if (str != null && !"".equals(str) && !"null".equals(str)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * get the IP address of the device
	 * 
	 * @return IP address add by zhuliang copy from wgz's AfterSale
	 */
	public static String getLocalIpAddress() {
		String deviceIp = null;
		boolean keepLookupOn = true;

		try {
			Enumeration<NetworkInterface> availableNetwork = NetworkInterface
					.getNetworkInterfaces();

			while (availableNetwork.hasMoreElements() && keepLookupOn) {
				NetworkInterface intf = availableNetwork.nextElement();
				Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();

				while (enumIpAddr.hasMoreElements()) {
					InetAddress inetAddress = enumIpAddr.nextElement();

					deviceIp = inetAddress.getHostAddress().toString();

					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(deviceIp)) {
						keepLookupOn = false;
						break;
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return deviceIp;
	}

	public static String getStudentPicPath(String className, String studentId) {
		return "PocketCampus/" + className + "/" + studentId + "_230230.jpg";
	}
	
	public static String getFileSize(long filesize){
		float a = 0;
		String text="";
		if (filesize > 1024*1024) {
			a = (float)filesize/1024/1024;
			text = String.format("%.1f", a)+"M";
		}
		if (1024 <= filesize && filesize < 1024*1024) {
			a = (float)filesize/1024;
			text = String.format("%.1f", a)+"K";
		}
		if (0<= filesize && filesize < 1024) {
			text = String.format("%.1f", a)+"b";
		}
		return text;
	}
	
	/**
	 * 功能描述: 判断是否初始化基础数据
	 *
	 * @author yanzy  2013-12-26 下午12:12:28
	 * 
	 * @return
	 */
	public static boolean isInitBaseData(){
//		String init_flag = PrefUtility.get(Constants.PREF_INIT_BASE_FLAG, "");
//		String userNumber = PrefUtility.get(Constants.PREF_USER_NUNMBER, "");
//		System.out.println("-------------------->init_flag:"+init_flag);
//		System.out.println("-------------------->userNumber:"+userNumber);
//		boolean init = false;
//		if (init_flag.equals(userNumber)) {
//			init = true;
//		}
//		return init;
		return PrefUtility.getBoolean(Constants.PREF_INIT_BASEDATE_FLAG, false);
	}
	
	/**
	 * 功能描述: 判断是否初始化联系人
	 *
	 * @author yanzy  2013-12-26 下午12:12:28
	 * 
	 * @return
	 */
	public static boolean isInitContactData(){
//		String init_flag = PrefUtility.get(Constants.PREF_INIT_CONTACT_FLAG, "");
//		String userNumber = PrefUtility.get(Constants.PREF_USER_NUNMBER, "");
//		System.out.println("-------------------->init_flag:"+init_flag);
//		System.out.println("-------------------->userNumber:"+userNumber);
//		boolean init = false;
//		if (init_flag.equals(userNumber)) {
//			init = true;
//		}
//		return init;
		
		Log.d("TAG", "--->  "+PrefUtility.getBoolean(Constants.PREF_INIT_CONTACT_FLAG, false));
		
		return PrefUtility.getBoolean(Constants.PREF_INIT_CONTACT_FLAG, false);
	}
	
	/**
	 * 功能描述:获取日期和星期
	 *
	 * @author shengguo  2014-5-10 下午1:52:11
	 * 
	 * @param date
	 * @return
	 */
	public static String getWeekAndDate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
		String time = sdf.format(date);
		String[] weekDays = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
		//int day = calendar.DAY_OF_WEEK - 1;
		if(dayOfWeek < 0){
			dayOfWeek = 0;
		}
		return time +" "+ weekDays[dayOfWeek];
	}
	
	/**
	 * 功能描述:获取垂直文本
	 *
	 * @author zhuliang  2014-1-11 下午3:34:58
	 * 
	 * @param text
	 * @return
	 */
	public static String getVerticalText(String text){
		StringBuffer stringBuffer = new StringBuffer();  
        if (text != null && text.length() > 0) {  
            int length = text.length();  
            for (int i = 0; i < length; i++)  
                stringBuffer.append(text.charAt(i) + "\n");
        }
        stringBuffer.deleteCharAt(stringBuffer.lastIndexOf("\n"));
	    return stringBuffer.toString();
	}
	
	/**
	 * 功能描述:打印错误提示
	 *
	 * @author shengguo  2014-4-28 下午2:51:46
	 * 
	 * @param context
	 * @param exception
	 */
	public static void showErrorToast(Context context,String exception){
		String string = exception;
		Log.d(TAG, "e----->" + exception);
		if (exception.indexOf(":") != -1) {
			exception = exception.substring(0, exception.indexOf(":"));
		}
		exception = exception.substring(exception.lastIndexOf(".") + 1,
				exception.length());
		if (exception.equals("ConnectTimeoutException")) {
			string = "连接超时!";
		}
		if (exception.equals("IllegalArgumentException")) {
			string = "服务器地址错误!";
		}
		if (exception.equals("SocketTimeoutException")) {
			string = "服务器未响应!";
		}
		if (exception.equals("HttpHostConnectException")) {
			string = "请检查网络连接!";
		}
		if (exception.equals("UnknownHostException")) {
			string = "服务器无法访问，请检查网络连接";
		}
		if (exception.indexOf("重新登录")>-1 || exception.indexOf("relogin")>-1) {
			string = exception;
			reLogin();
		}
		AppUtility.showToastMsg(context, string,1);
	}
	private static void reLogin()
	{
		Intent intent = new Intent(context,
				LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	//程序是否进入后台
	public static boolean isApplicationBroughtToBackground(final Context context) { 

	    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); 

	    List<RunningTaskInfo> tasks = am.getRunningTasks(1); 

	    if (!tasks.isEmpty()) { 

	        ComponentName topActivity = tasks.get(0).topActivity; 

	        if (!topActivity.getPackageName().equals(context.getPackageName())) { 

	            return true; 

	        } 

	    } 

	    return false; 

	}
	//是否处于锁屏
	public static boolean isLockScreen(final Context context) { 
		KeyguardManager kgm = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if(kgm.inKeyguardRestrictedInputMode())
        	return true;
        else
        	return false;
	}
	public static void playSounds(int sound, final Context context)
	{
		
	  final SoundPool sp = new SoundPool(1,AudioManager.STREAM_MUSIC,0);
	  sp.load(context, sound, 1);
	  sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener(){
		  
		  public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
		  {
			  sp.play(sampleId,     //声音资源
			        1,         //左声道
			        1,         //右声道
			        0,             //优先级，0最低
			        0,         //循环次数，0是不循环，-1是永远循环
			        1);            //回放速度，0.5-2.0之间。1为正常速度
			  
		  }
		  
	  } );
	  
	  
	}
	public static int getPixByDip(Context context,int dip)
	{
		float scale = context.getResources().getDisplayMetrics().density;  
        return (int)(dip * scale); 
	}
	public static float getDipByPix(Context context,int Pix)
	{
		float scale = context.getResources().getDisplayMetrics().density;  
        return Pix/scale; 
	}
	/**
	 * 功能描述:开启课程提醒
	 * 
	 * @author shengguo 2014-5-24 下午5:17:18
	 * 
	 */
	
	public static void beginReminder(Context ct) {
		Intent intent = new Intent(ct, Alarmreceiver.class);
		intent.setAction("reminderMeClass");
		PendingIntent sender = PendingIntent.getBroadcast(ct,
				0, intent, 0);
		AlarmManager am = (AlarmManager) ct.getSystemService(Activity.ALARM_SERVICE);// 24小时一个周期，不停的发送广播
		am.cancel(sender);
		if(PrefUtility.getBoolean("booleanReminderDayClass", true))
		{

			String pattern="yyyy-MM-dd 20:00:00";
			String dtStr=DateHelper.getToday(pattern);
			Date dt=DateHelper.getStringDate(dtStr,null);
			
			if(dt.before(new Date()))
			{
				dtStr=DateHelper.getNextday(pattern);
				dt=DateHelper.getStringDate(dtStr,pattern);
			}
			
			// 开始时间
			long firstime = dt.getTime();
			am.setRepeating(AlarmManager.RTC_WAKEUP, firstime,
					24*60*60*1000,sender);
			
			
		}
		
	}
	
	/*
	public static void beginGPS(Context ct,String userType) {
		Intent intent = new Intent(ct, Alarmreceiver.class);
		intent.setAction("reportLocation");
		PendingIntent sender = PendingIntent.getBroadcast(ct,
				0, intent, 0);
		AlarmManager am = (AlarmManager) ct.getSystemService(Activity.ALARM_SERVICE);
		am.cancel(sender);
		if(userType.equals("学生"))
		{
			String dtStr=DateHelper.getToday("yyyy-MM-dd HH:00:00");
			Date dt=DateHelper.getStringDate(dtStr,null);
			am.setRepeating(AlarmManager.RTC_WAKEUP, dt.getTime(),60*60*1000,sender);
		}
		
	}	
    */
	public static void setListViewHeightBasedOnChildren(ListView listView, int attHeight) {  
        ListAdapter listAdapter = listView.getAdapter();   
        if (listAdapter == null) {  
            // pre-condition  
            return;  
        }  
  
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + attHeight;  
        listView.setLayoutParams(params);  
    }  
	public static List getHtmlTagContent(String html,String tagname) { 
        List resultList = new ArrayList(); 
        Pattern p = Pattern.compile("<"+tagname+">([^</"+tagname+">]*)");//匹配<tagname>开头，</tagname>结尾的文档 
        Matcher m = p.matcher(html );//开始编译 
        while (m.find()) { 
            resultList.add(m.group(1));//获取被匹配的部分 
        } 
        return resultList; 
    } 
	
	public static void iniImageLoader(Context ctx)
	{
        headOptions =
                new DisplayImageOptions.Builder()
                        .cacheOnDisc(true)//图片存本地
                        .cacheInMemory(false)
                        .showImageOnFail(R.drawable.ic_launcher)
                                //.displayer(new FadeInBitmapDisplayer(50))
                        .displayer(new RoundedBitmapDisplayer(45))
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .build();
		//初始化图片加载库
				DisplayImageOptions defaultOptions =
					        new DisplayImageOptions.Builder()
					            .cacheOnDisc(true)//图片存本地
					            .cacheInMemory(false)
					            .showImageOnFail(R.drawable.empty_photo)
					            //.displayer(new FadeInBitmapDisplayer(50))
					           // .decodingOptions(decodingOptions)
					            .bitmapConfig(Bitmap.Config.RGB_565)
					            .imageScaleType(ImageScaleType.EXACTLY ) // default
					            .build();
				
				//DisplayImageOptions defaultOptions = DisplayImageOptions.createSimple();
					    ImageLoaderConfiguration config =
					        new ImageLoaderConfiguration.Builder(ctx)
					    
					    //.memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽  
					    //.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) 
					    //.threadPriority(Thread.NORM_PRIORITY - 2)  
		                //.denyCacheImageMultipleSizesInMemory()  
		                //.memoryCache(new FIFOLimitedMemoryCache(2 * 1024 * 1024))
		                //.memoryCacheSize(2 * 1024 * 1024)    
		                //.discCacheSize(50 * 1024 * 1024) 
		                .defaultDisplayImageOptions(defaultOptions).build();
					    /*
					            .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
					            .memoryCacheSize(2 * 1024 * 1024)  
					            .memoryCacheSizePercentage(13) // default  
					            .denyCacheImageMultipleSizesInMemory()
					     
					            .defaultDisplayImageOptions(defaultOptions).build();
					      */ 
					    ImageLoader.getInstance().init(config);
	}
	public static JSONObject parseQueryStrToJson(String queryStr)
	{
		JSONObject obj=new JSONObject();
		String temp[]=queryStr.split("\\?");
		if(temp.length>1)
			queryStr=temp[1];
		temp=queryStr.split("&");
		for(int i=0;i<temp.length;i++)
		{
			String item[]=temp[i].split("=");
			if(item[1]!=null)
			{
				try {
					obj.put(item[0], item[1]);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return obj;
	}
	public static boolean responseHasError(JSONObject jo)
	{
		String loginStatus = jo.optString("结果");
		if (loginStatus.equals("失败")) 
		{
			AppUtility.showToastMsg(context, jo.optString("errorMsg"),1);
			return true;
		}
		else if(jo.optString("exception")!=null || jo.optString("exception").length()>0)
		{
			AppUtility.showErrorToast(context,jo.optString("errorcode"));
			return true;
		}
		return false;
	}
	public static void closeInputMethod(View view) {
	    InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	    boolean isOpen = imm.isActive();
	    if (isOpen) {
	        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
	        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
}
