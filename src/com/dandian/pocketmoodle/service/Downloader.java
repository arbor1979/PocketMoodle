package com.dandian.pocketmoodle.service;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.dandian.pocketmoodle.db.DownloaderDao;
import com.dandian.pocketmoodle.entity.DownloadInfo;
import com.dandian.pocketmoodle.entity.LoadInfo;
import com.dandian.pocketmoodle.util.AppUtility;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Downloader {
	private String urlstr;// 下载的地�?
	private String localfile;// 保存路径
	private int threadcount;// 线程�?
	private Handler mHandler;// 消息处理�?
	private DownloaderDao dao;// 工具�?
	private int fileSize;// �?要下载的文件的大�?
	private List<DownloadInfo> infos;// 存放下载信息类的集合
	private static final int INIT = 1;// 定义三种下载的状态：初始化状态，正在下载状�?�，暂停状�??
	private static final int DOWNLOADING = 2;
	private static final int PAUSE = 3;
	private int state = INIT;
	private Context context;

	public Downloader(String urlstr, String localfile, int threadcount,
			Context context, Handler mHandler) {
		this.urlstr = urlstr;
		this.localfile = localfile;
		this.threadcount = threadcount;
		this.mHandler = mHandler;
		this.context = context;
		dao = new DownloaderDao(context);
	}

	/**
	 * 判断是否正在下载
	 */
	public boolean isdownloading() {
		return state == DOWNLOADING;
	}

	/**
	 * 得到downloader里的信息 首先进行判断是否是第�?次下载，如果是第�?次就要进行初始化，并将下载器的信息保存到数据库中
	 * 如果不是第一次下载，那就要从数据库中读出之前下载的信息（起始位置，结束为止，文件大小等），并将下载信息返回给下载�?
	 */
	public LoadInfo getDownloaderInfors() {
		int totalsize=0;
		int downsize=0;
		boolean isfirst = false;
		int size = 0;
		int compeleteSize = 0;
		if (isFirst(urlstr)) {
			isfirst = true;
			init();
			
			int waittimes=3;
			try {
				while (fileSize == 0 && waittimes > 0) {
					Thread.sleep(1000);
					waittimes--;
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			int range = fileSize / threadcount;
			infos = new ArrayList<DownloadInfo>();
			for (int i = 0; i < threadcount - 1; i++) {
				DownloadInfo info = new DownloadInfo(i, i * range, (i + 1)
						* range - 1, 0, urlstr);
				infos.add(info);
			}
			DownloadInfo info = new DownloadInfo(threadcount - 1,
					(threadcount - 1) * range, fileSize - 1, 0, urlstr);
			infos.add(info);
			// 保存infos中的数据到数据库
			dao.saveInfos(infos);
			totalsize = fileSize;
			downsize = 0;
//			return new LoadInfo(fileSize, 0, urlstr);
			//return loadInfo;
		} 
		if(!isfirst){

			infos = dao.getInfos(urlstr);
			Log.v("TAG", "not isFirst size=" + infos.size());
			
			for (DownloadInfo info : infos) {
				compeleteSize += info.getCompeleteSize();
				size += info.getEndPos() - info.getStartPos() + 1;
			}
			totalsize = size;
			downsize = compeleteSize;
//			return new LoadInfo(size, compeleteSize, urlstr);
		}
//		if (totalsize == 0) {
//			totalsize = fileSize;
//		}
		System.out.println("------------------>totalsize:"+fileSize);
		System.out.println("------------------>size:"+size);
		LoadInfo loadInfo = new LoadInfo(totalsize, downsize, urlstr);
		return loadInfo;
	}

	/**
      */
	private void init() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					URL url = new URL(urlstr);
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					//connection.setConnectTimeout(5000);
					//connection.setRequestMethod("GET");
					if (connection.getResponseCode() != 200) {
						AppUtility.showToastMsg(context, "��Ч�����ص�ַ��");
					}else{
						fileSize = connection.getContentLength();
						File file = new File(localfile);
						if (!file.exists()) {
							file.createNewFile();
						}
						// 本地访问文件
//						RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
//						accessFile.setLength(fileSize);
//						accessFile.close();
						connection.disconnect();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
			
		
	}

	/**
	 * 判断是否是第�?�? 下载
	 */
	private boolean isFirst(String urlstr) {
		return dao.isHasInfors(urlstr);
	}

	/**
	 * 114 * 利用线程�?始下载数�? 115
	 */
	public void download() {
		if (infos != null) {
			if (state == DOWNLOADING)
				return;
			state = DOWNLOADING;
			for (DownloadInfo info : infos) {
				new MyThread(info.getThreadId(), info.getStartPos(),
						info.getEndPos(), info.getCompeleteSize(),
						info.getUrl()).start();
			}
		}
	}

	public class MyThread extends Thread {
		private int threadId;
		private int startPos;
		private int endPos;
		private int compeleteSize;
		private String urlstr;

		public MyThread(int threadId, int startPos, int endPos,
				int compeleteSize, String urlstr) {
			this.threadId = threadId;
			this.startPos = startPos;
			this.endPos = endPos;
			this.compeleteSize = compeleteSize;
			this.urlstr = urlstr;
		}

		@SuppressWarnings({ "deprecation", "resource" })
		@Override
		public void run() {
			HttpURLConnection connection = null;
			RandomAccessFile randomAccessFile = null;
			InputStream is = null;
			try {
				URL url = new URL(urlstr);
				connection = (HttpURLConnection) url.openConnection();
				//connection.setConnectTimeout(5000);
				//connection.setRequestMethod("GET");
				// 设置范围，格式为Range：bytes x-y;
				connection.setRequestProperty("Range", "bytes="
						+ (startPos + compeleteSize) + "-" + endPos);

				randomAccessFile = new RandomAccessFile(localfile, "rwd");
				randomAccessFile.seek(startPos + compeleteSize);
				// 将要下载的文件写到保存在保存路径下的文件�?
				is = connection.getInputStream();
				byte[] buffer = new byte[4096];
				int length = -1;
				while ((length = is.read(buffer)) != -1) {
					if (isFirst(urlstr)) { //判断本地数据库是否有下载消息，无下载消息则停止线程，跳出循环
						this.stop();
						break;
					}
					randomAccessFile.write(buffer, 0, length);
					compeleteSize += length;
					// 更新数据库中的下载信�?
					dao.updataInfos(threadId, compeleteSize, urlstr);
					
//					int progress = (Double.valueOf((compeleteSize * 1.0 / fileSize * 100))).intValue();
//					System.out.println("---------------------------->progress:"+progress+">>>>>:"+compeleteSize+"============"+fileSize);
					// 用消息将下载信息传给进度条，对进度条进行更新
					Bundle bundle = new Bundle();
					bundle.putString("urlstr", urlstr);
					bundle.putInt("compeleteSize", compeleteSize);
					bundle.putString("localFile", localfile);
					Message message = Message.obtain();
					message.what = 1;
					message.obj = bundle;
					mHandler.sendMessage(message);
					if (state == PAUSE) {
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
					randomAccessFile.close();
					connection.disconnect();
//					dao.closeDb();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	// 删除数据库中urlstr对应的下载器信息
	public void delete(String urlstr) {
		dao.delete(urlstr);
	}

	// 设置暂停
	public void pause() {
		state = PAUSE;
	}

	// 重置下载状�??
	public void reset() {
		state = INIT;
	}
}

