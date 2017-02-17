package com.dandian.pocketmoodle.util;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.view.KeyEvent;

import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.HttpManager;
import com.dandian.pocketmoodle.entity.CustomMultipartEntity;
import com.dandian.pocketmoodle.entity.CustomMultipartEntity.ProgressListener;


public class HttpMultipartPostToMoodle extends AsyncTask<String, Integer, String> {
	private Context context;  
    public ProgressDialog pd;  
    private long totalSize;
    private CampusParameters myParams;
    private String url;
    HttpClient httpClient;
    public HttpMultipartPostToMoodle(Context context, String url,CampusParameters params) {  
        this.context = context;  
        this.url=url;
        this.myParams = params;  
        
    }  
  
    private OnKeyListener onKeyListener = new OnKeyListener() {  
     
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {  
				if (null != dialog) {  
					dialog.dismiss();  
		        } 
            } 
			return false;
		}  
    }; 
    @Override  
    protected void onPreExecute() {  
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
        pd.setMessage(context.getString(R.string.isuploading));  
        pd.setCancelable(false);
        pd.setOnKeyListener(onKeyListener);  
        pd.show();  
        pd.setOnDismissListener(new OnDismissListener() {                
        

			@Override
			public void onDismiss(DialogInterface dialog) {
				if(httpClient!=null)
	    			httpClient.getConnectionManager().shutdown();
				
				
			}
        	  }); 
        
    }  
  
    @Override  
    protected String doInBackground(String... params) {  
        String serverResponse = null;  
  
        httpClient =HttpManager.getNewHttpClient();
        HttpContext httpContext = new BasicHttpContext();  
        HttpPost httpPost = new HttpPost(this.url);  
        
        
        try {  
            CustomMultipartEntity multipartContent = new CustomMultipartEntity(
            		new ProgressListener() {  
                        @Override  
                        public void transferred(long num) {  
                            publishProgress((int) ((num / (float) totalSize) * 100));  
                        }  
                    });  
            // We use FileBody to transfer an image  
            
            for(int i=0;i<myParams.size();i++)
            {
            	String key=myParams.getKey(i);
            	String value=myParams.getValue(key);
            	multipartContent.addPart(key, new StringBody(value, Charset.forName("UTF-8")));   
            	
            }  
            multipartContent.addPart("filename", new FileBody(new File(  
            		myParams.getValue("pic"))));  
           
            totalSize = multipartContent.getContentLength();  
  
            // Send it  
            httpPost.setEntity(multipartContent);  
            
            HttpResponse response = httpClient.execute(httpPost, httpContext);  
            serverResponse = EntityUtils.toString(response.getEntity());  
              
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return serverResponse;  
    }  
  
    @Override  
    protected void onProgressUpdate(Integer... progress) {  
        pd.setProgress((int) (progress[0]));  
        if(pd.getProgress()==100)
        	pd.setMessage(context.getString(R.string.waitreturn));
    }  
  
      
  
    @Override  
    protected void onCancelled() {  
        System.out.println("cancle");  
        pd.dismiss(); 
    } 
}
