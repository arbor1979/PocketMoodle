package com.dandian.pocketmoodle.activity;

import java.util.ArrayList;
import java.util.List;

import com.dandian.pocketmoodle.fragment.SchoolAchievementFragment;
import com.dandian.pocketmoodle.fragment.SchoolBlogFragment;
import com.dandian.pocketmoodle.fragment.SchoolNoticeFragment;
import com.dandian.pocketmoodle.fragment.SchoolQuestionnaireFragment;
import com.dandian.pocketmoodle.fragment.SchoolWorkAttendanceFragment;
import com.dandian.pocketmoodle.fragment.SubjectFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;


public class SchoolActivity extends FragmentActivity {
	private String TAG = "SchoolActivity";
	private static List<String> TemplateNameS = new ArrayList<String>();
	private int templateType;
	private String interfaceName, templateName,title,display;
	Fragment fragment;
	static {
		TemplateNameS.add("通知");
		TemplateNameS.add("考勤");
		TemplateNameS.add("成绩");
		TemplateNameS.add("调查问卷");
		TemplateNameS.add("日历");
		TemplateNameS.add("博客");
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Check what fragment is shown, replace if needed.
		fragment = getSupportFragmentManager().findFragmentById(
				android.R.id.content);
		Intent intent =getIntent();
		templateName = intent.getStringExtra("templateName");
		interfaceName = intent.getStringExtra("interfaceName");
		title = intent.getStringExtra("title");
		display= intent.getStringExtra("display");
		
		Log.d(TAG, "---templateName" + templateName);
		Log.d(TAG, "---interfaceName" + interfaceName);
		for (int i = 0; i < TemplateNameS.size(); i++) {
			if (TemplateNameS.get(i).equals(templateName)) {
				templateType = i;
			}
		}
		switch (templateType) {
		case 0:
			fragment = SchoolNoticeFragment.newInstance(title,interfaceName,display);
			break;
		case 1:
			fragment = new SchoolWorkAttendanceFragment(title,interfaceName);
			break;
		case 2:
			fragment = new SchoolAchievementFragment(title,interfaceName,display);
			break;
		case 3:
			fragment = new SchoolQuestionnaireFragment(title,interfaceName);
			break;
		case 4:
			fragment = new SubjectFragment(title,interfaceName);
			break;
		case 5:
			fragment = SchoolBlogFragment.newInstance(title,interfaceName,display);
			break;
		}
		if(getSupportFragmentManager().findFragmentById(
				android.R.id.content)!=null)
		{
			getSupportFragmentManager().beginTransaction()
			.replace(android.R.id.content, fragment).commit();
		}
		else
		{
			getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, fragment).commit();
		}
	}

	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("title", title);
        savedInstanceState.putString("interfaceName", interfaceName);
    }
 
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        title = savedInstanceState.getString("title");
        interfaceName = savedInstanceState.getString("interfaceName");
    }
}
