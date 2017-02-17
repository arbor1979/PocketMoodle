package com.dandian.pocketmoodle.entity;

import org.json.JSONObject;

//У��item
public class SchoolWorkItem {
	private String workPicPath;// ����ͼ��
	private String workText;// �������
	private String interfaceName;// �ӿ�����
	private String TemplateName;// ģ������
	private int unread;//δ��
	private String displayText;// �������
	public SchoolWorkItem() {

	}

	public SchoolWorkItem(JSONObject jo) {
		workPicPath = jo.optString("ͼ��");
		workText = jo.optString("����");
		displayText=jo.optString("����");
		interfaceName = jo.optString("�ӿڵ�ַ");
		TemplateName = jo.optString("ģ������");
		unread=0;
	}

	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public int getUnread() {
		return unread;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}

	public String getWorkPicPath() {
		return workPicPath;
	}

	public void setWorkPicPath(String workPicPath) {
		this.workPicPath = workPicPath;
	}

	public String getWorkText() {
		return workText;
	}

	public void setWorkText(String workText) {
		this.workText = workText;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getTemplateName() {
		return TemplateName;
	}

	public void setTemplateName(String templateName) {
		TemplateName = templateName;
	}
}
