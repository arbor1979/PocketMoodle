package com.dandian.pocketmoodle.api;

import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.util.PrefUtility;

public class CampusAPI {
	

	public static final String HTTP_METHOD = "POST";
	public static final String HTTP_METHOD2 = "GET";

	public static String commonQuestionUrl = "http://www.dandian.net/company/ICampus-faq.php"; // 甯歌闂�?�?
	public static String contractUrl = "http://www.dandian.net/company/ICampus-contract.php"; // 甯歌闂�?�?
	public static String aboutusUrl = "http://www.dandian.net/company/ICampus-aboutus.php"; // 甯歌闂�?�?
	public static String howtouse = "http://www.moodle360.cn/howtouse.php";


	public static void request(final String url, final CampusParameters params,
			final String HTTP_METHOD, RequestListener listener) {
		String domain=PrefUtility.get(Constants.PREF_SCHOOL_DOMAIN,"");
		
		AsyncFoodSafeRunner.request("http://" + domain + "appserver.php" + url,
				params, HTTP_METHOD, listener);
	}


	public static void loginCheck(CampusParameters params,
			RequestListener listener) {
		// request("?action=logincheck", params, HTTP_METHOD, listener);
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/GetUserPwdIsRight_Moodle.php", params,
				HTTP_METHOD, listener);
	}
	/**
	 * 鐢ㄦ埛鎻愪氦璁惧淇℃伅鍙婄櫨搴D
	 * 
	 * @param params
	 * @param listener
	 */
	public static void postBaiDuId(CampusParameters params,
			RequestListener listener) {
		// request("?action=logincheck", params, HTTP_METHOD, listener);
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/BaiDuSdk_Input.php", params,
				HTTP_METHOD, listener);
	}
	/**
	 * 杩斿洖鐢ㄦ埛鎵�鏈変俊鎭�?,鏍规嵁鍛ㄦ鑾峰彇鐢ㄦ埛涓婅璁板綍
	 * 
	 * @param params
	 * @param listener
	 */
	public static void initInfo(CampusParameters params,
			RequestListener listener) {
		int week = PrefUtility.getInt(Constants.PREF_SELECTED_WEEK, 0);
		if(week==0)
			request("?action=initinfo&zip=1", params, HTTP_METHOD,
					listener);
		else
			request("?action=initinfo&zip=1&WEEK=" + week, params, HTTP_METHOD,
				listener);
	}

	/***
	 * 鍔熻兘鎻忚堪:淇敼鎰忚鍙嶉淇℃伅
	 * 
	 * @author linrr 2013-12-16 涓嬪�?2:11:20
	 * 
	 * @param params
	 * @param listener
	 */
	public static void feedback(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/SendSMS_GUESTBOOK_ALL.php", params,
				HTTP_METHOD, listener);
	}

	/**
	 * 鍔熻兘鎻忚堪:鐝骇閫氱煡淇℃�?
	 * 
	 * @author linrr 2013-12-17 涓婂�?10:22:03
	 * 
	 * @param params
	 * @param listener
	 */
	public static void noticeClass(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/SendSMS_CLASS_NOTIFY.php", params,
				HTTP_METHOD, listener);
	}

	/**
	 * 鍔熻兘鎻忚堪:涓婁紶鏂囦欢
	 * 
	 * @author linrr 2013-12-17 涓婂�?10:23:00
	 * 
	 * @param params
	 * @param listener
	 */
	public static void uploadFiles(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/upload.php?action=base64", params,
				HTTP_METHOD, listener);
	}
	
	public static void uploadFilesNoBase64(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/upload.php", params,
				HTTP_METHOD, listener);
	}
	/**
	 * 鍔熻兘鎻忚堪:淇敼�?�︾敓鑰冨嫟淇℃�?
	 * 
	 * @author yanzy 2013-12-4 涓婂�?9:47:08
	 * 
	 * @param params
	 * @param listener
	 */
	public static void Changeinfo(CampusParameters params,
			RequestListener listener) {
		String action = params.getValue("action");
		request("?action=" + action, params, HTTP_METHOD, listener);
	}

	/**
	 * 鍔熻兘鎻忚堪:鍔犺浇�?�︾敓澶村儚
	 * 
	 * @author yanzy 2013-12-9 涓婂�?10:09:57
	 * 
	 * @param params
	 * @param listener
	 */
	public static void downLoadStudentPic(CampusParameters params,
			RequestListener listener) {
		String picUrl = params.getValue("picUrl");
		System.out.println("---------------------ApiPicUrl:" + picUrl);
		AsyncFoodSafeRunner.request(picUrl, params, HTTP_METHOD, listener);
	}

	public static void getTeacherInfo(CampusParameters params,
			RequestListener listener) {
		// String user_code = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		// requestData("?JSON=1&DATA="+user_code, params, HTTP_METHOD,
		// listener);
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/GetTeacherInfo_Moodle.php?IsZip=1", params,
				HTTP_METHOD, listener);
	}
	//鏈�杩戜竴娆¤亰澶╄褰�
	public static void getLast_ATOALL(CampusParameters params,
			RequestListener listener) {
		// String user_code = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		// requestData("?JSON=1&DATA="+user_code, params, HTTP_METHOD,
		// listener);
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/SendSMS_GETLast_ATOALL.php", params,
				HTTP_METHOD, listener);
	}
	/**
	 * 鍔熻兘鎻忚堪: 鑱婂ぉ鍙戦�佹秷鎭�
	 * 
	 * @author yanzy 2013-12-16 涓婂�?10:02:20
	 * 
	 * @param params
	 * @param listener
	 */
	public static void smsSend(CampusParameters params, RequestListener listener) {
		String url = "http://laoshi.dandian.net/SendSMS_MSG_ATOB.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}
	/**
	 * 鍔熻兘鎻忚堪: 鑱婂ぉ鏇存柊宸茶鐘舵��?
	 * 
	 * @author QiaoLin 2014-7-9 涓嬪�?22:36:20
	 * 
	 * @param params
	 * @param listener
	 */
	public static void updatesmsState(CampusParameters params, RequestListener listener) {
		String url = "http://laoshi.dandian.net/GeSmsStatus.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}
	/**
	 * 鍔熻兘鎻忚堪: 鑱婂ぉ鏇存柊宸茶鐘舵��?
	 * 
	 * @author QiaoLin 2014-7-14 涓嬪�?14:21:20
	 * 
	 * @param params
	 * @param listener
	 */
	public static void postGPS(CampusParameters params, RequestListener listener) {
		String url = "http://laoshi.dandian.net/IOSLData_Input.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}
	/**
	 * 鍔熻兘鎻忚堪:涓嬭浇鑱婂ぉ璁板�?
	 * 
	 * @author yanzy 2013-12-23 涓嬪�?5:15:56
	 * 
	 * @param params
	 * @param listener
	 */
	public static void smsDownLoad(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/SendSMS_LIST_ATOB.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}

	

	/**
	 * 鍔熻兘鎻忚堪:鎻愪氦鍒犻櫎涓嬭浇鏂囦欢鐨勬暟鎹�?
	 * 
	 * @author zhuliang 2013-12-24 涓婂�?11:18:23
	 * 
	 * @param params
	 * @param listener
	 */
	public static void sendDownloadDeleteData(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/MoodleFilesDownload.php?action=delete";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD,
				listener);
	}

	/**
	 * 鍔熻兘鎻忚堪:鑾峰彇鏈�鍚庝竴鏉¤亰澶╄褰�?
	 * 
	 * @author yanzy 2013-12-23 涓嬪�?5:16:09
	 * 
	 * @param params
	 * @param listener
	 */
	public static void getLastChatMsg(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/SendSMS_GetLast_ATOB.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}

	// /**
	// * 鍔熻兘鎻忚堪:鍙戦�佺兢娑堟伅
	// *
	// * @author zhuliang 2014-1-14 涓嬪�?3:59:57
	// *
	// * @param params
	// * @param listener
	// */
	// public static void sendGroupMsg(CampusParameters params,RequestListener
	// listener){
	// String url = "http://laoshi.dandian.net/SendSMS_QUN.php";
	// AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	// }
	/**
	 * 鍔熻兘鎻忚堪:鑾峰彇鏍″唴鍐呭椤�?
	 * 
	 * @author shengguo 2014-4-14 涓嬪�?5:25:18
	 * 
	 * @param params
	 *            datetime //:1397112337 鐢ㄦ埛鏍￠獙鐮�
	 * @param listener
	 */
	public static void getSchool(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/InterfaceStudent/XUESHENG-Moodle.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}

	/**
	 * 鍔熻兘鎻忚堪:鑾峰彇鏍″唴璇︽儏鍒楄�??
	 * 
	 * @author shengguo 2014-4-26 涓婂�?10:07:12
	 * 
	 * @param params
	 * @param Interface
	 * @param listener
	 */
	public static void getSchoolItem(CampusParameters params, String Interface,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/InterfaceStudent/" + Interface;
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}
	
	public static void getSchoolItemZip(CampusParameters params, String Interface,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/InterfaceStudent/" + Interface;
		if(url.indexOf("?")>=0)
			url=url+"&IsZip=1";
		else
			url=url+"?IsZip=1";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}
	
	/**
	 * 鍔熻兘鎻忚堪:鑾峰彇鏍″唴璇︽儏鍒楄�?�鐨勮鎯�
	 * 
	 * @author shengguo 2014-4-26 涓婂�?10:07:55
	 * 
	 * @param params
	 * @param Interface
	 * @param listener
	 */
	public static void getSchoolChild(CampusParameters params,
			String Interface, RequestListener listener) {
		String url = "http://laoshi.dandian.net/InterfaceStudent/" + Interface;
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD2, listener);
	}

	/**
	 * 鍔熻兘鎻忚堪:瀛︾敓韬唤锛氱偣鏌愪竴鑺傝鍚庣殑鎺ュ彛鏁版嵁鏉ユ�?
	 * 
	 * @author shengguo 2014-4-26 涓婂�?10:07:55
	 * 
	 * @param params
	 * @param listener
	 */
	public static void getCourseAndTeacherInfo(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/GetCourseAndTeacherInfo.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD2, listener);
	}

	/**
	 * 鍔熻兘鎻忚堪:鑾峰彇璇句欢鍒楄�?
	 * 
	 * @author shengguo 2014-4-28 涓婂�?11:11:42
	 * 
	 * @param params
	 * @param Interface
	 *            鎺ュ彛鍚�?
	 * @param listener
	 */
	public static void getDownloadSubject(CampusParameters params,
			String Interface, RequestListener listener) {
		String url = "http://laoshi.dandian.net/" + Interface;
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}

	/**
	 * 鍔熻兘鎻忚堪:鑾峰彇娴嬮獙鐘舵��
	 * 
	 * @author shengguo 2014-4-28 涓嬪�?5:39:06
	 * 
	 * @param params
	 * @param Interface
	 * @param listener
	 */
	public static void getCeyanStatus(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/GetCeyanStatus.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD2, listener);
	}

	/**
	 * 鍔熻兘鎻忚堪:鑾峰彇娴嬮獙鏁版�?,淇濆瓨娴嬮獙缁撴�?
	 * 
	 * @author shengguo 2014-4-28 涓嬪�?5:39:06
	 * 
	 * @param params
	 * @param Interface
	 * @param listener
	 */
	public static void getCeyanInfo(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/GetCeyanInfo.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD2, listener);
	}

	/**
	 * 鍔熻兘鎻忚堪:瀛︾敓鑾峰彇,淇濆瓨璇勪环淇℃�?
	 * 
	 * @author shengguo 2014-4-30 涓婂�?11:31:05
	 * 
	 * @param params
	 * @param listener
	 */
	public static void getPingjiaByStudent(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/GetPingjiaByStudent.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD2, listener);
	}

	/**
	 * 鍔熻兘鎻忚堪:瀛︾敓鑾峰彇,淇濆瓨璇勪环淇℃�?
	 * 
	 * @author shengguo 2014-4-30 涓婂�?11:31:05
	 * 
	 * @param params
	 * @param listener
	 */
	public static void saveTeacherZongJie(CampusParameters params,
			RequestListener listener) {
		request("?action=changezongjieinfo", params, HTTP_METHOD, listener);
	}

	/**
	 * 鍔熻兘鎻忚堪:淇濆瓨�?�︾敓鎬荤粨
	 * 
	 * @author shengguo 2014-5-15 涓嬪�?12:02:46
	 * 
	 * @param params
	 * @param listener
	 */
	public static void saveStudentZongJie(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/GetPingjiaByStudent.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}

	/**
	 * 鍔熻兘鎻忚堪:妫�娴嬫洿鏂�?
	 * 
	 * @author shengguo 2014-6-3 涓嬪�?3:54:36
	 * 
	 */
	public static void versionDetection(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/update-moodle.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}
	
	public static void getAddressFromBaidu(double latitude, double longitude, RequestListener listener) {
		String url = String.format("http://api.map.baidu.com/geocoder/v2/?ak=cR269G15Gov4OaRZ1Tko1Hu4&coordtype=wgs84ll&callback=renderReverse&location=%s,%s&output=json&pois=1", latitude, longitude);
		AsyncFoodSafeRunner.request(url, new CampusParameters(), HTTP_METHOD2, listener);
	}
	
	public static void getAlbumList(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/AlbumDownload.php?IsZip=1", params,
				HTTP_METHOD, listener);
	}
	public static void getAlbumDetailList(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/AlbumDownloadDetail.php?IsZip=1", params,
				HTTP_METHOD, listener);
	}
}
