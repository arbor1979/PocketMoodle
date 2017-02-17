package com.dandian.pocketmoodle.entity;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable(tableName="QueryTheMarkOfStudent")
/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 学生成绩查询
 * 
 *  <br/>创建说明: 2013-11-22 下午3:58:11 linrr  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class QueryTheMarkOfStudent {
	@DatabaseField
	private String data;//数据
		@DatabaseField
	private String title;//标题
		@DatabaseField
	private String average;//平均�?
		@DatabaseField
	private String totalscore;//总分
		public QueryTheMarkOfStudent(){}
		public QueryTheMarkOfStudent(JSONObject jo) {
			data = jo.optString("数据");
			title = jo.optString("标题");
			average = jo.optString("平均�?");
			totalscore= jo.optString("总分");
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getAverage() {
			return average;
		}
		public void setAverage(String average) {
			this.average = average;
		}
		public String getTotalscore() {
			return totalscore;
		}
		public void setTotalscore(String totalscore) {
			this.totalscore = totalscore;
		}
	
}
