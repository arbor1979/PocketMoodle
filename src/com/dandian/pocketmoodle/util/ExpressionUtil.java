package com.dandian.pocketmoodle.util;


import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.base.Constants;


public class ExpressionUtil {
	public static String pattern = "\\[f\\d{3}\\]";

	public static void dealExpression(Context context,
			SpannableString spannableString, Pattern patten)
			throws SecurityException, NoSuchFieldException,
			NumberFormatException, IllegalArgumentException,
			IllegalAccessException {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			// [f000]
			String filedname = key.substring(1, key.length() - 1);
			Field field = R.drawable.class.getDeclaredField(filedname);
			int resId = AppUtility.parseInt(field.get(null).toString()); // 通过上面匹配得到的字符串来生成图片资源id
			if (resId != 0) {
				Bitmap bitmap = BitmapFactory.decodeResource(
						context.getResources(), resId);
				ImageSpan imageSpan = new ImageSpan(context,
						bitmap, ImageSpan.ALIGN_BOTTOM); // 通过图片资源id来得到bitmap，用�?个ImageSpan来包�?
				
				
				int end = matcher.start() + key.length(); // 计算该图片名字的长度，也就是要替换的字符串的长度
				spannableString.setSpan(imageSpan, matcher.start(), end,
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE); // 将该图片替换字符串中规定的位置中
			}
		}
	}

	/**
	 * 得到�?个SpanableString对象，�?�过传入的字符串,并进行正则判�?
	 * 
	 * @param context
	 * @param str
	 * @return SpannableString 带表情的字符�?
	 */
	public static SpannableString getExpressionString(Context context,
			String str) {
		SpannableString spannableString = new SpannableString(str);
		Pattern sinaPatten = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
		try {
			dealExpression(context, spannableString, sinaPatten);
		} catch (Exception e) {
			Log.e("dealExpression", e.getMessage());
		}
		return spannableString;
	}

	/**
	 * 保存�?有表情图片的资源ID
	 */
	public static int[] getExpressRcIds() {
		int imageIds[] = new int[Constants.express_counts];
		int resourceId = 0;
		String fieldName;
		for (int i = 0; i < Constants.express_counts; i++) {
			try {
				if (i < 10) {
					fieldName = "f00" + i;
				} else if (i < 100) {
					fieldName = "f0" + i;
				} else {
					fieldName = "f" + i;
				}
				Field field = R.drawable.class.getDeclaredField(fieldName);
				resourceId = AppUtility.parseInt(field.get(null).toString());
				imageIds[i] = resourceId;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return imageIds;
	}
}
