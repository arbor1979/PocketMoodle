package com.dandian.pocketmoodle.fragment;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dandian.pocketmoodle.R;
import com.dandian.pocketmoodle.activity.SchoolDetailActivity;
import com.dandian.pocketmoodle.api.CampusAPI;
import com.dandian.pocketmoodle.api.CampusException;
import com.dandian.pocketmoodle.api.CampusParameters;
import com.dandian.pocketmoodle.api.RequestListener;
import com.dandian.pocketmoodle.base.Constants;
import com.dandian.pocketmoodle.db.DatabaseHelper;
import com.dandian.pocketmoodle.entity.MyClassSchedule;
import com.dandian.pocketmoodle.entity.Schedule;
import com.dandian.pocketmoodle.entity.TeacherInfo;
import com.dandian.pocketmoodle.lib.BaseTableAdapter;
import com.dandian.pocketmoodle.lib.TableFixHeaders;
import com.dandian.pocketmoodle.util.AppUtility;
import com.dandian.pocketmoodle.util.Base64;
import com.dandian.pocketmoodle.util.DateHelper;
import com.dandian.pocketmoodle.util.PrefUtility;
import com.j256.ormlite.dao.Dao;

public class SubjectFragment extends Fragment {
	public Object[][] table ;
	WindowManager manager;
	Display display;
	DatabaseHelper database;
	Dao<Schedule, Integer> scheduleDao;
	List<TeacherInfo> teacherInfoList = new ArrayList<TeacherInfo>();
	Schedule scheduleInfo;
	BaseTableAdapter baseTableAdapter;
	TableFixHeaders tableFixHeaders;
	String[] colors = { "#ff6666", "#ff9900", "#0099cc"};
	private static final String TAG = "SubjectFragment";
	private Map<String, Button> showButtons = new HashMap<String, Button>();
	private String[] weeks, sections;
	private String yearMonth;
	private JSONObject weekJson;
	
	private String pattern="yyyy-MM";
	LinearLayout initlayout,layout_menu,layoutRefresh;
	Button bn_menu,bn_refresh;
	TextView tv_title;
	DatePickerDialog mdialog;
	private String interfaceName,title;
	
	public SubjectFragment(String title,String interfaceName) {
		this.interfaceName=interfaceName;
		this.title=title;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		weekJson = new JSONObject();
		try {
			weekJson.put("0", getActivity().getString(R.string.sunday));
			weekJson.put("1", getActivity().getString(R.string.monday));
			weekJson.put("2", getActivity().getString(R.string.tuesday));
			weekJson.put("3", getActivity().getString(R.string.wednesday));
			weekJson.put("4", getActivity().getString(R.string.thursday));
			weekJson.put("5", getActivity().getString(R.string.friday));
			weekJson.put("6", getActivity().getString(R.string.saturday));

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View localView = inflater.inflate(R.layout.table, container, false);
		manager = getActivity().getWindowManager();
		display = manager.getDefaultDisplay();
		
		if(yearMonth==null)
			yearMonth=DateHelper.getDateString(new Date(), pattern);
		initTitle(localView);
		
		tableFixHeaders = (TableFixHeaders) localView.findViewById(R.id.table);
		baseTableAdapter = new SubjectAdapter(getActivity()
				.getApplicationContext());
		tableFixHeaders.setAdapter(baseTableAdapter);
		
		weeks = new String[] { getActivity().getString(R.string.sunday),
				getActivity().getString(R.string.monday),
				getActivity().getString(R.string.tuesday),
				getActivity().getString(R.string.wednesday),
				getActivity().getString(R.string.thursday),
				getActivity().getString(R.string.friday),
				getActivity().getString(R.string.saturday)};
		
		return localView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getSchedulInfo();
		
	}
	
	private void initTitle(View view) {
		initlayout = (LinearLayout) view.findViewById(R.id.initlayout);
		layout_menu = (LinearLayout) view.findViewById(R.id.layout_back);
		layoutRefresh = (LinearLayout) view.findViewById(R.id.layout_goto);
		bn_menu = (Button) view.findViewById(R.id.btn_back);
		bn_refresh = (Button) view.findViewById(R.id.btn_goto);
		bn_refresh.setTextColor(getActivity().getResources().getColor(R.color.white));
		bn_refresh.setTextSize(13);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(yearMonth);
		bn_menu.setVisibility(View.VISIBLE);
		bn_menu.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);
		//bn_refresh.setBackgroundResource(R.drawable.bg_title_homepage_go);
		tv_title.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectedWeeks();
			}
			
		});
		//layoutRefresh.setOnClickListener(new MyListener());
	
		layout_menu.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});
		
		
	}
	public void selectedWeeks() {
		Date dt=DateHelper.getStringDate(yearMonth,pattern);
		Calendar cal=Calendar.getInstance();
		cal.setTime(dt);
		
		DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener(){  //
			@Override
			public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			
				yearMonth=DateHelper.getDateString(new Date(arg1-1900,arg2,arg3), pattern);
				getSchedulInfo();
			}
		};
		
		mdialog = new CustomerDatePickerDialog(getActivity(),listener,cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));

		Date minDt=DateHelper.getMonthOffset(dt,-3);
		Date maxDt=DateHelper.getMonthOffset(dt,+3);
		mdialog.getDatePicker().setMinDate(minDt.getTime());
		mdialog.getDatePicker().setMaxDate(maxDt.getTime());
		
		mdialog.setButton2(getString(R.string.cancel), new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		mdialog.show();
		DatePicker dp = findDatePicker((ViewGroup) mdialog.getWindow().getDecorView());  
		if (dp != null) {  
		    ((ViewGroup)((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);  
		} 

	}
	
	private DatePicker findDatePicker(ViewGroup group) {  
        if (group != null) {  
            for (int i = 0, j = group.getChildCount(); i < j; i++) {  
                View child = group.getChildAt(i);  
                if (child instanceof DatePicker) {  
                    return (DatePicker) child;  
                } else if (child instanceof ViewGroup) {  
                    DatePicker result = findDatePicker((ViewGroup) child);  
                    if (result != null)  
                        return result;  
                }  
            }  
        }  
        return null;  
    } 
	class CustomerDatePickerDialog extends DatePickerDialog {  
	    public CustomerDatePickerDialog(Context context,  
	            OnDateSetListener callBack, int year, int monthOfYear,  
	            int dayOfMonth) {  
	        super(context, callBack, year, monthOfYear, dayOfMonth);  
	    }  
	  
	    @Override  
	    public void onDateChanged(DatePicker view, int year, int month, int day) {  
	        super.onDateChanged(view, year, month, day);  
	        mdialog.setTitle(year + "-" + (month + 1));  
	    }  
	}
	
	public void initTable() {
		tableFixHeaders.removeAllViews();
		
		tv_title.setText(yearMonth+"▼");
		Date firstDay=DateHelper.getStringDate(yearMonth, pattern);
		Date lastDay=DateHelper.getLastDayOfMonth(firstDay);
		int weeknum=DateHelper.getWeekOfMonthDefault(lastDay);
		int beginDayIndex=DateHelper.getDayOfWeek(firstDay);
		sections = new String[weeknum];
		for(int i=0;i<weeknum;i++)
		{
			sections[i]=String.valueOf(i+1);
		}
		table = new Object[sections.length][7];
		int maxDay=DateHelper.getDayOfMonth(lastDay);
		for(int i=0;i<maxDay;i++)
		{
			int row=(i+beginDayIndex)/7;
			int col=(i+beginDayIndex)%7;
			TeacherInfo tt=new TeacherInfo();
			tt.setId(String.valueOf(i+1));
			tt.setSection(String.valueOf(row));
			tt.setWeek(col);
			table[row][col]=tt;
		}
		baseTableAdapter.notifyDataSetChanged();
		
	}

	/**
	 * 功能描述:获取课表规则
	 * 
	 * @author shengguo 2014-5-16 上午11:55:22
	 * 
	 */
	private void getSchedulInfo() {
		
		initTable();
		
		initlayout.setVisibility(View.VISIBLE);
		tv_title.setVisibility(View.INVISIBLE);
		
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		Locale locale = getResources().getConfiguration().locale;
	    String language = locale.getCountry();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
			jo.put("language", language);
			jo.put("yearMonth", yearMonth);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getSchoolItem(params, interfaceName, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response" + e.getMessage());
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----response" + response);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
		
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				initlayout.setVisibility(View.INVISIBLE);
				tv_title.setVisibility(View.VISIBLE);
				AppUtility.showErrorToast(getActivity(), msg.obj.toString());
				break;
			case 0:
				initlayout.setVisibility(View.INVISIBLE);
				tv_title.setVisibility(View.VISIBLE);
				String result = msg.obj.toString();
				String resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						if(AppUtility.isNotEmpty(res)){
							AppUtility.showToastMsg(getActivity(), res);
						}
						else{
							JSONArray richeng=jo.optJSONArray("数值"); 
							if(richeng!=null)
							{
								
								for(int i=0;i<richeng.length();i++)
								{
									JSONObject itemJson=richeng.getJSONObject(i);
									MyClassSchedule mcs=new MyClassSchedule(itemJson);
									if(mcs.getName()!=null && mcs.getName().length()>0)
									{
										localTable(mcs);
									}
								}
								baseTableAdapter.notifyDataSetChanged();
								
							}
							String rightButton=jo.optString("右上按钮");
							final String rightButtonURL=jo.optString("右上按钮URL");
							if(rightButton!=null && rightButton.length()>0)
							{
								bn_refresh.setText(rightButton);
								bn_refresh.setVisibility(View.VISIBLE);
								layoutRefresh.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										Intent intent =new Intent(getActivity(),SchoolDetailActivity.class);
										intent.putExtra("templateName", "调查问卷");
										intent.putExtra("interfaceName", interfaceName+rightButtonURL);
										intent.putExtra("title", title);
										intent.putExtra("status", "进行中");
										intent.putExtra("autoClose", "是");
										startActivityForResult(intent,101);
									}
								});
							}
								
						}
					} 
					catch (UnsupportedEncodingException e) {

						e.printStackTrace();
						AppUtility.showErrorToast(getActivity(),e.getLocalizedMessage());
					}
					catch (JSONException e) {
						
						e.printStackTrace();
						AppUtility.showErrorToast(getActivity(), e.getLocalizedMessage());
					}
				}

				break;
			}
		}
	};
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		case 1:
			getSchedulInfo();
		    break;
		default:
		    break;
		}
	}
	public class SubjectAdapter extends BaseTableAdapter {
		private final float density;

		public SubjectAdapter(Context context) {
			density = context.getResources().getDisplayMetrics().density;
		}

		@Override
		public int getRowCount() {
			return sections == null ? 0 : sections.length;
		}

		@Override
		public int getColumnCount() {
			return weeks == null ? 0 : weeks.length;
		}

		@Override
		public View getView(int row, int column, View convertView,
				ViewGroup parent) {
			switch (getItemViewType(row, column)) {
			case 0:
				convertView = getFirstHeader(row, column, convertView, parent);
				break;
			case 1:
				convertView = getHeader(row, column, convertView, parent);
				break;
			case 2:
				convertView = getFirst(row, column, convertView, parent);
				break;
			case 3:
				convertView = getTime(row, column, convertView, parent);
				break;
			case 4:
				convertView = getBody(row, column, convertView, parent);
				break;
			case 5:
				convertView = getTimeNull(row, column, convertView, parent);
				break;
			default:
				throw new RuntimeException("wtf?");
			}
			return convertView;
		}

		private View getFirstHeader(int row, int column, View convertView,
				ViewGroup parent) {
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(getActivity());
				tv.setBackgroundColor(getResources().getColor(
						R.color.subject_single));
				tv.setText("");
			}
			return tv;
		}

		private View getHeader(int row, int column, View convertView,
				ViewGroup parent) {
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(getActivity());
				tv.setText(weeks[column]);
				
				tv.setTextSize(13);
				//tv.setPadding(5, 5, 5, 5);
				//tv.setTextColor(Color.parseColor("#fadf8f"));
				tv.setTextColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER);
				if (column % 2 == 0) {
					tv.setBackgroundColor(getResources().getColor(
							R.color.subject_double));
				} else {
					tv.setBackgroundColor(getResources().getColor(
							R.color.subject_single));
				}
				if(column==DateHelper.getDayOfWeek(new java.util.Date()))
				{
					tv.setBackgroundColor(Color.parseColor("#075c1b"));
				}
			}
			return tv;
		}

		private View getFirst(int row, int column, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.view_subject_sections, parent, false);
			}
			TextView tv = (TextView) convertView.findViewById(R.id.tv_section);
			if (AppUtility.isNumeric(sections[row])) {
				tv.setText(sections[row]);
			} else {
				tv.setText(AppUtility.getVerticalText(sections[row]));
			}
			if (row % 2 == 0) {
				tv.setBackgroundColor(getResources().getColor(
						R.color.subject_double));
			} else {
				tv.setBackgroundColor(getResources().getColor(
						R.color.subject_single));
			}
			return convertView;
		}

		private View getTime(int row, int column, View convertView,
				ViewGroup parent) {
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(getActivity());
				tv.setText(sections[row]);
				tv.setTextColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER);
			}
			return tv;
		}

		private View getTimeNull(int row, int column, View convertView,
				ViewGroup parent) {
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(getActivity());
				tv.setText("");
			}
			return tv;
		}

		@SuppressLint("NewApi")
		private View getBody(int row, int column, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.view_subject_body, parent, false);
			}
			final Button bn = (Button) convertView.findViewById(R.id.button);
			final TextView tv=(TextView)convertView.findViewById(R.id.dayIndex);
			
			if (row < table.length && column < table[row].length) {
				TeacherInfo teacherInfo = (TeacherInfo) table[row][column];
				if (teacherInfo != null) {
					tv.setTag(teacherInfo);
					String teacherInfoStr = teacherInfo.getId();
					
					tv.setTextSize(16);
					tv.setText(teacherInfoStr);
					
					/*
					TeacherInfo lastInfo = null;
					TeacherInfo nextInfo = null;
					if (row - 1 >= 0 && row - 1 <= table.length) {
						lastInfo = (TeacherInfo) table[row - 1][column];
					}
					if (row + 1 > 0 && row + 1 <= table.length) {
						nextInfo = (TeacherInfo) table[row + 1][column];
					}
					if (nextInfo == null && lastInfo == null) {
						teacherInfoStr = teacherInfo.getCourseName() + "("
								+ teacherInfo.getClassroom() + ")"
								+ teacherInfo.getClassGrade();
						bn.setText(teacherInfoStr);
					} else {
						if (nextInfo != null) {
							if (teacherInfo.getSection().equals(
									nextInfo.getSection())) {
								showButtons.put(teacherInfo.getId() + "up", bn);
								
								teacherInfoStr = teacherInfo.getCourseName() + "("
										+ teacherInfo.getClassroom() + ")";
								bn.setText(teacherInfoStr);
								bn.setGravity(Gravity.BOTTOM);
							}
						}

						if (lastInfo != null) {
							if (teacherInfo.getSection().equals(
									lastInfo.getSection())) {
								showButtons.put(teacherInfo.getId() + "down",
										bn);
								
								if (userType.equals("老师"))
									bn.setText(teacherInfo.getClassGrade());
								else
									bn.setText(teacherInfo.getName());
								bn.setGravity(Gravity.TOP);
							}
						}
					}

					bn.setBackgroundColor(color);
					
					
					//bn.getBackground().setAlpha(180);
					bn.setId(color);
					*/
					if(teacherInfo.getName()!=null && teacherInfo.getName().length()>0)
					{
						bn.setTextSize(TypedValue.COMPLEX_UNIT_PX,display.getWidth()/320*10);
						bn.setTag(teacherInfo);
						bn.setText(teacherInfo.getName());
						int color;
						bn.setVisibility(View.VISIBLE);
						if(Integer.parseInt(teacherInfo.getTerm())==1)
						{
							//课程事件
							color=Color.parseColor(colors[0]);
						}
						else if(Integer.parseInt(teacherInfo.getTerm())>1)
						{
							//系统事件
							color=Color.parseColor(colors[1]);
						}
						else
						{
							//用户事件
							color=Color.parseColor(colors[2]);
						}
						bn.setBackgroundColor(color);
						bn.setId(color);
					}
				}

			} else {
				tv.setText("");
			}
			if (!bn.getText().toString().equals("")) {
				bn.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						TeacherInfo ti = (TeacherInfo) v.getTag();
						Button btnDown = showButtons.get(ti.getId() + "down");
						Button btnUp = showButtons.get(ti.getId() + "up");
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							Log.d(TAG, "--------->开始点击");
							if(btnDown!=null && btnUp!=null)
							{
								btnDown.setBackgroundColor(Color
									.parseColor("#eeeeee"));
								btnUp.setBackgroundColor(Color
									.parseColor("#eeeeee"));
							}
							else
								v.setBackgroundColor(Color
									.parseColor("#eeeeee"));
						}
						if (event.getAction() == MotionEvent.ACTION_UP
								|| event.getAction() != MotionEvent.ACTION_DOWN) {
							Log.d(TAG, "--------->结束点击");
							if(btnDown!=null && btnUp!=null)
							{
								btnDown.setBackgroundColor(v.getId());
								btnUp.setBackgroundColor(v.getId());
							//btnDown.getBackground().setAlpha(180);
							//btnUp.getBackground().setAlpha(180);
							}
							else
								v.setBackgroundColor(v.getId());
						}
						return false;
					}
				});

				bn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						TeacherInfo ti = (TeacherInfo) v.getTag();
						// Intent intent = new
						// Intent(getActivity(),ClassRoomActivity.class);
						Intent intent = new Intent(getActivity(),
								SchoolDetailActivity.class);
						intent.putExtra("teacherInfo", (Serializable) ti);
						intent.putExtra("templateName", "成绩");
						intent.putExtra("interfaceName", interfaceName+"?action=pageview&day="+yearMonth+"-"+ti.getId());
						intent.putExtra("title", yearMonth+"-"+ti.getId());
						startActivityForResult(intent,101);
					}
				});
			}
			return convertView;
		}

		@SuppressWarnings("deprecation")
		@Override
		public int getWidth(int column) {
			if (column == -1) {
				return Math.round(0 * density);
			} else {
				if(display.getWidth()>=720)
					return Math.round(display.getWidth() / 7);
				else
					return Math.round(display.getWidth() / 5);
			}
		}

		@SuppressWarnings("deprecation")
		@Override
		public int getHeight(int row) {
			if (row == -1 /* || row == 3 || row == 6 */) {
				return Math.round(35 * density);
			} else {
				if(display.getHeight()>=1280)
					return Math.round((display.getHeight()-110*density)/sections.length);
				else
					return Math.round((display.getHeight()-110*density)/4);
			}
		}

		@Override
		public int getItemViewType(int row, int column) {
			int itemViewType;
			if (row == -1 && column == -1) {
				itemViewType = 0;
			} else if (row == -1) {
				itemViewType = 1;
			} else if (column == -1) {
				itemViewType = 2;
			} else {
				itemViewType = 4;
			}
			return itemViewType;
		}

		@Override
		public int getViewTypeCount() {
			
			return 6;
			
		}

	}

	

	public void localTable(MyClassSchedule li) {

		Date dt=new Date(Long.valueOf(li.getTimestart())*1000);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int beginday=cal.get(Calendar.DAY_OF_MONTH);
		dt=new Date(Long.valueOf(li.getTimesend())*1000);
		cal.setTime(dt);
		int endday=cal.get(Calendar.DAY_OF_MONTH);
		for (int i = 0; i < table.length; i++) {
			for(int j=0;j<table[i].length;j++)
			{
				if(table[i][j]!=null)
				{
					TeacherInfo ti=(TeacherInfo) table[i][j];
					if(Integer.valueOf(ti.getId())>=beginday && Integer.valueOf(ti.getId())<=endday)
					{
						if(ti.getName()==null)
							ti.setName("");
						if(ti.getName().length()>0)
							ti.setName(ti.getName()+"\n");
						//if(li.getName().length()>5)
						//	li.setName(li.getName().substring(0, 5));
						ti.setName(ti.getName()+li.getName());
						ti.setTerm(li.getEventtype());
					}
				}
			}
		}
	}
			
}
