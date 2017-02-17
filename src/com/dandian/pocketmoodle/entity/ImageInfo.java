package com.dandian.pocketmoodle.entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
/**
 * 发动态中本地图片信息
 * @author shengguo
 */
public class ImageInfo {

	public int id;
	public Bitmap icon;//路径下的�?张图�?
	public String displayName;//
	public String path;//文件路径
	public int picturecount;//图片数量
	public List<String> tag=new ArrayList<String>();//路径下图片路径集�?
}
