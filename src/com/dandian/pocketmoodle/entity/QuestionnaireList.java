package com.dandian.pocketmoodle.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * �汾˵��: $id:$ <br/>
 * 
 * ����˵��: �ʾ���������б�
 * 
 * <br/>
 * ����˵��: 2014-4-17 ����6:04:18 shengguo �����ļ�<br/>
 * 
 * �޸���ʷ:<br/>
 * 
 */
public class QuestionnaireList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7775213654657975509L;
	private String title;
	private String submitTo;
	private String status;
	private ArrayList<Question> questions;

	public QuestionnaireList(JSONObject jo) {
		title = jo.optString("������ʾ");
		submitTo = jo.optString("�ύ��ַ");
		status = jo.optString("�����ʾ�״̬");
		questions = new ArrayList<Question>();
		JSONArray joq = jo.optJSONArray("�����ʾ���ֵ");
		for (int i = 0; i < joq.length(); i++) {
			Question q = new Question(joq.optJSONObject(i));
			questions.add(q);
		}
	}
	

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getSubmitTo() {
		return submitTo;
	}


	public void setSubmitTo(String submitTo) {
		this.submitTo = submitTo;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public ArrayList<Question> getQuestions() {
		return questions;
	}


	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}


	public class Question implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5740781476744525865L;
		private String title;
		private String status;
		private String usersAnswer;
		private String remark;
		private String options[];
		private List<ImageItem> images; 
		private String isRequired;
		public Question(JSONObject jo) {
			title = jo.optString("��Ŀ");
			status = jo.optString("����");
			remark = jo.optString("��ע");
			Log.d("-----", jo.toString());
			JSONArray ja = jo.optJSONArray("ѡ��");
			if(ja!=null){
				options = new String[ja.length()];
				for (int i = 0; i < ja.length(); i++) {
					options[i] = ja.optString(i);
				}
			}
			isRequired = jo.optString("�Ƿ����");
			if(status.equals("ͼƬ")){
				JSONArray jaimages = jo.optJSONArray("�û���");
				if(jaimages!=null){
					setImages(ImageItem.toList(jaimages));
				}else{
					setImages(new ArrayList<ImageItem>());
				}
			}else{
				usersAnswer = jo.optString("�û���");
			}
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getUsersAnswer() {
			return usersAnswer;
		}

		public void setUsersAnswer(String usersAnswer) {
			this.usersAnswer = usersAnswer;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String[] getOptions() {
			return options;
		}

		public void setOptions(String[] options) {
			this.options = options;
		}

		public String getIsRequired() {
			return isRequired;
		}

		public void setIsRequired(String isRequired) {
			this.isRequired = isRequired;
		}

		public List<ImageItem> getImages() {
			return images;
		}

		public void setImages(List<ImageItem> images) {
			this.images = images;
		}
	}
}
