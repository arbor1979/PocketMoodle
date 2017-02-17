package com.dandian.pocketmoodle.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class DownloadUtil {
	/** 下载文件总长�? */
	private int totalSize;
	/** 下载文件进度 */
	private int progress;
	/** 下载文件 */
	private File downFile = null;
	private String downloadPath;

	/**
	 * 文件下载
	 * 
	 * @param downUrl
	 *            下载链接
	 * @return 下载的文�?
	 */
	public File downloadFile(String downUrl,String saveFilePath) {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		downloadPath=saveFilePath;
			Log.d("url", "download - " + downUrl);
			try {
				URL url = new URL(downUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(1000 * 5);
				totalSize = conn.getContentLength();
				if (totalSize <= 0) {
					return null;
				}
				progress = 0;
				InputStream is = conn.getInputStream();
				String filename = downUrl.substring(downUrl.lastIndexOf("/") + 1);// 获得文件�?
				Log.d("url", "filename - " + filename);
				FileUtility.createFilePath(downloadPath);
				downFile = new File(downloadPath,filename);
				
				FileOutputStream fos = new FileOutputStream(downFile);
				BufferedInputStream bis = new BufferedInputStream(is);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = bis.read(buffer))!=-1) {
					fos.write(buffer, 0, len);
					progress += len;
					// System.out.println("progress = " + progress);
				}
				fos.flush();
				fos.close();
				bis.close();
				is.close();
				conn.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		return downFile;
	}

	/**
	 * 安装APK文件
	 * 
	 * @param apkfile
	 *            APK文件�?
	 * @param mContext
	 */
	public void installApk(Context mContext, File apkFile) {
		if (!apkFile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkFile.toString()),
				"application/vnd.android.package-archive");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(i);
	}

	/** 监听下载进度 */
	public void setOnDownloadListener(IOnDownloadListener listener) {
		listener.updateNotification(progress, totalSize, downFile);
	}

	/**
	 * 监听接口
	 */
	public interface IOnDownloadListener {
		/**
		 * 更新下载进度
		 * 
		 * @param progress
		 *            下载进度�?
		 * @param totalSize
		 *            文件总大�?
		 * @param downFile
		 *            下载的文�?
		 */
		public void updateNotification(int progress, int totalSize,
				File downFile);
	}

}
