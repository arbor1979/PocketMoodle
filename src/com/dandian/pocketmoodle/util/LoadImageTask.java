package com.dandian.pocketmoodle.util;

import java.io.InputStream;

import com.androidquery.util.Progress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.TextView;

public class LoadImageTask extends AsyncTask<String,Progress,Bitmap> {

	private String filename;
	private TextView tv;
	public LoadImageTask(String filename,TextView tv)
	{
		this.filename=filename;
		this.tv=tv;
	}
	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bitmap = null;
        try{
            InputStream in = new java.net.URL(params[0]).openStream();
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
	}
	@Override
    protected void onPostExecute(Bitmap result) {
      
        if (result != null) {
        	ImageUtility.writeTofiles(result, filename);
        	tv.requestLayout();
        }
    }
	

}
