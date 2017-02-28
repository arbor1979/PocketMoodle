package com.dandian.pocketmoodle.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.dandian.pocketmoodle.R;

public class DialogUtility {

	static private AQuery aq=null;
	/**
	 * 得到自定义的progressDialog
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	@SuppressLint("InlinedApi")
	public static Dialog createLoadingDialog(Context context, String msg) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView
		//ProgressBar spaceshipImage = (ProgressBar) v.findViewById(R.id.progress);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		// 加载动画
//		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
//				context, R.anim.loading_animation);
		// 使用ImageView显示动画
		//spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);// 设置加载信息

		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		//loadingDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消�?
		loadingDialog.setCancelable(false);// 不可以用“返回键”取�?
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		return loadingDialog;

	}

	/**
	 * 功能描述:显示提示
	 *
	 * @author yanzy  2013-12-19 下午1:34:13
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showMsg(Context context, String msg){
		Toast toast=Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	public static void showImageDialog(Activity ct,String imagePath) {
		View view = ct.getLayoutInflater().inflate(R.layout.view_image, null);
		aq = new AQuery(view);
		final Dialog dialog=createLoadingDialog(ct, "show_image_dialog");
		dialog.setContentView(view);
	//	final AlertDialog dialog=new AlertDialog.Builder(getActivity()).setView(view).create();
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		ct.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		android.view.WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();   
		lp.width = mDisplayMetrics.widthPixels;
		lp.height = mDisplayMetrics.heightPixels;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		
		aq.id(R.id.iv_img).image(imagePath,true,true,0,R.drawable.ic_launcher).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
	}
	public static void showImageDialog(Activity ct,String imagePath,String msg) {
		
		showImageDialog(ct,imagePath);
		
		if(aq!=null)
		{
			if(msg!=null && msg.length()>0)
			{
				aq.id(R.id.tv_description).visibility(View.VISIBLE);
				aq.id(R.id.tv_description).text(msg);
			}
			else
				aq.id(R.id.tv_description).visibility(View.GONE);
		}
		
	}
}
