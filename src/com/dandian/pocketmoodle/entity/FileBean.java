package com.dandian.pocketmoodle.entity;

import com.zhy.tree.bean.TreeNodeDesc;
import com.zhy.tree.bean.TreeNodeId;
import com.zhy.tree.bean.TreeNodeImage;
import com.zhy.tree.bean.TreeNodeLabel;
import com.zhy.tree.bean.TreeNodePid;

public class FileBean
{
	@TreeNodeId
	private int _id;
	@TreeNodePid
	private int parentId;
	@TreeNodeLabel
	private String name;
	@TreeNodeDesc
	private String desc;
	@TreeNodeImage
	private String image;
	private long length;
	public FileBean(int _id, int parentId, String name,String desc,String image)
	{
		super();
		this._id = _id;
		this.parentId = parentId;
		this.name = name;
		this.desc=desc;
		this.image=image;
	}

}
