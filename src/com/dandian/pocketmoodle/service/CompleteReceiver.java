package com.dandian.pocketmoodle.service;


import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.activity.WebSiteActivity;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.FileUtility;
import com.dandian.pocketmoodle.util.IntentUtility;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.DownloadManager.Query; 
public class CompleteReceiver extends BroadcastReceiver {

	private DownloadManager downloadManager; 
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();  
        if(action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {  
        	AppUtility.showToastMsg(context, context.getString(R.string.downloadcomplete));
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);                                                                                      //TODO 判断这个id与之前的id是否相等，如果相等说明是之前的那个要下载的文�?  
            Query query = new Query();  
            query.setFilterById(id);  
            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);  
            Cursor cursor = downloadManager.query(query);  
              
            int columnCount = cursor.getColumnCount();  
            String path = null;                                                                                                                                       //TODO 这里把所有的列都打印�?下，有什么需求，就�?�么处理,文件的本地路径就是path  
            while(cursor.moveToNext()) {  
                for (int j = 0; j < columnCount; j++) {  
                    String columnName = cursor.getColumnName(j);  
                    String string = cursor.getString(j);  
                    if(columnName.equals("local_uri")) {  
                        path =string;  
                    }  
                    if(string != null) {  
                    	
                        System.out.println(columnName+": "+ string);  
                    }else {  
                        System.out.println(columnName+": null");  
                    }  
                }  
            }  
            cursor.close();  
            if(path!=null)
            {
        
	            if(path.startsWith("content:")) 
	            {  
	            	cursor = context.getContentResolver().query(Uri.parse(path), null, null, null, null);  
	                columnCount = cursor.getColumnCount();  
	                while(cursor.moveToNext())
	                {  
	                                    for (int j = 0; j < columnCount; j++) {  
	                                                String columnName = cursor.getColumnName(j);  
	                                                String string = cursor.getString(j);  
	                                                if(string != null) {  
	                                                     System.out.println(columnName+": "+ string);  
	                        }else {  
	                            System.out.println(columnName+": null");  
	                        }  
	                    }  
	                }  
	                cursor.close();  
	            }  
	            Intent aintent=IntentUtility.openUrl(Uri.decode(path).replace("file://", ""));
	            if(aintent!=null)
	            	IntentUtility.openIntent(context, aintent,true);
	            else 
	            {
	            	IntentUtility.openWebIntent(context,Uri.decode(path));
	            }
            }
              
        }else if(action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {  
              
        }  
	}

}
