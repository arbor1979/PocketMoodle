package com.dandian.pocketmoodle.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

@SuppressLint("NewApi")
public class FileUtility {
	
	private static final String TAG = "FileUtility";
	private static Context context;
	
	public static void setContext(Application app) {
		context = app.getApplicationContext();
	}
    public static String getDiskCacheDir() {  
        String cachePath = null;  
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())  
                || !Environment.isExternalStorageRemovable()) {  
            cachePath = context.getExternalCacheDir().getPath();  
        } else {  
            cachePath = context.getCacheDir().getPath();  
        }  
        return cachePath;  
    } 
    public static String getDiskFileDir() {  
        String cachePath = null;  
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())  
                || !Environment.isExternalStorageRemovable()) {  
            cachePath = context.getExternalFilesDir(null).getPath();  
        } else {  
            cachePath = context.getFilesDir().getPath();  
        }  
        return cachePath;  
    }
	public static File creatSDFile(String fileName) throws IOException {  
		
    	String path = getDiskCacheDir() + fileName;
    	System.out.println("creatSDFilePath:"+path);
        File file = new File(path);  
        file.createNewFile();  
        return file;  
    }  
      
    /** 
     * 在SD卡上创建目录 
     *  
     * @param dirName 
     */  
    public static String creatFileDir(String dirName) {
    	String newDir;
    	newDir=getDiskFileDir()+"/"+dirName+"/";
    	File dir = new File(newDir);  
        if(!dir.exists())
        	dir.mkdirs();
       
        return newDir;
    }  
    public static String creatCacheDir(String dirName) {
    	String newDir;
    	newDir=getDiskCacheDir()+"/"+dirName+"/";
    	File dir = new File(newDir);  
        if(!dir.exists())
        	dir.mkdirs();
       
        return newDir;
    }
  
    /** 
     * 判断SD卡上的文件夹是否存在 
     */  
    public static boolean isFileExist(String fileName){  
        File file = new File(getDiskCacheDir() + fileName);  
        return file.exists();  
    }  
      
    /** 
     * 将一个InputStream里面的数据写入到SD卡中 
     */  
    public static File write2SDFromInput(String path,String fileName,InputStream input){  
        File file = null;  
        OutputStream output = null;  
        try{  
            
            file = creatSDFile(path + fileName);  
            output = new FileOutputStream(file);  
            byte buffer [] = new byte[4 * 1024];  
            while((input.read(buffer)) != -1){  
                output.write(buffer);  
            }  
            output.flush();  
        }  
        catch(Exception e){  
            e.printStackTrace();  
        }  
        finally{  
            try{  
                output.close();  
            }  
            catch(Exception e){  
                e.printStackTrace();  
            }  
        }  
        return file;  
    } 
    
    public static File writeSDFromByte(String path,String fileName,byte[] buffer){
    	File file = null;  
        OutputStream output = null;  
        try{  
            
            file = FileUtility.creatSDFile(path + fileName);  
            output = new FileOutputStream(file);  
            output.write(buffer);
            output.flush();  
        }  
        catch(Exception e){  
            e.printStackTrace();  
        }  
        finally{  
            try{  
                output.close();  
            }  
            catch(Exception e){  
                e.printStackTrace();  
            }  
        }
        
        return file;
    }
    
    public static boolean deleteFile(String path){
    	File file = new File(path);
    	if (file.exists())
			return file.delete();
    	return false;
    }
    public static boolean deleteFileFolder(String path){
    	File dirFile = new File(path); 
    	if (!dirFile.exists() || !dirFile.isDirectory()) {  
            return false;  
        } 
    	File[] files = dirFile.listFiles();  
        for (int i = 0; i < files.length; i++) {  
            //删除子文�?  
            if (files[i].isFile()) {  
            	files[i].delete();
            } //删除子目�?  
            else {  
            	deleteFileFolder(files[i].getAbsolutePath());  
            }  
        }  
        return true;  
    }
	// 创建文件�?
	public static void createFilePath(String path) {
		String filepath = path.substring(0, path.lastIndexOf("/"));
		File file = new File(path);
		File filesPath = new File(filepath);
		// 如果目标文件已经存在，则删除，产生覆盖旧文件的效果（此处以后可以扩展为已经存在图片不再重新下载功能）
		Log.d(TAG, "-->!filesPath.exists()" + !filesPath.exists());
		if (!filesPath.exists()) {
			createFilePath(filepath);
			file.mkdir();
		} else {
			file.mkdir();
		}
	}
    public static File getCacheFile(String imageUri){  
        File cacheFile = null;        
		String fileName = getFileRealName(imageUri);    
		cacheFile = new File(getDiskCacheDir(), fileName);   
        return cacheFile;  
    }  
      
    public static String getFileRealName(String path) {  
        int index = path.lastIndexOf("/");  
        String subPath=path.substring(index + 1); 
        index=subPath.indexOf("?");
        String fileName;
        if(index>-1)
        	fileName=subPath.substring(0, index);
        else
        	fileName=subPath;
        return  fileName;
    } 
    
    public static String getUrlRealName(String path) {  
        int index = path.lastIndexOf("/");  
        String fileName=path.substring(index + 1); 
        index=fileName.indexOf("?");
        if(index>-1)
        {
        	fileName=fileName.substring(index+1);
        	index=fileName.indexOf("=");
        	if(index>-1)
        		fileName=fileName.substring(index+1);
        }
        return  fileName;
    } 
    
    public static String getFileExtName(String path)
    {
    	String filename=getFileRealName(path);
    	int index=filename.lastIndexOf(".");
        String extName;
        if(index>-1)
        	extName=filename.substring(index+1);
        else
        	extName="";
        return  extName;
    }
    public static String getFileDir(String path)
    {
    	
    	int index=path.lastIndexOf("/");
        String extName;
        if(index>-1)
        	extName=path.substring(0,index);
        else
        	extName=path;
        return  extName;
    }

	/**
	 * 功能描述: 用当前时间给取得的图�?,视频命名
	 * 
	 * @author linrr 2013-12-26 下午4:36:17
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	 public static String getFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Random random = new Random(System.currentTimeMillis());
		int num = random.nextInt(1000000);
		return dateFormat.format(date) +"_"+ num;
	}
	public static String getRandomSDFileName(String fileExt) {
		return getDiskCacheDir()+getFileName()+"."+fileExt;
	}
	
	public static String getRandomSDFileName(String dir,String fileExt) {
		return creatCacheDir(dir)+getFileName()+"."+fileExt;
	}
	
	@SuppressWarnings("resource")
	public static String fileupload(File file) {
		ByteArrayOutputStream content = new ByteArrayOutputStream();
		FileInputStream fStream;
		try {
			fStream = new FileInputStream(file);
			/* 设定每次写入1024bytes */
			int bufferSize = 8192;
			int readBytes = 0;
			byte[] buffer = new byte[bufferSize];
			// 从文件读取数据到缓冲�?
			while ((readBytes = fStream.read(buffer)) != -1) {
				content.write(buffer, 0, readBytes);
			}
			byte[] bytes = content.toByteArray();
			return Base64.encode(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static boolean copyFile(String oldPath, String newPath) 
	{
		boolean result=false;
		try {            
			         
			int byteread = 0;            
			File oldfile = new File(oldPath);            
			if (oldfile.exists()) { 
				//文件存在�?               
				InputStream inStream = new FileInputStream(oldPath); 
				//读入原文�?                
				FileOutputStream fs = new FileOutputStream(newPath);                
				byte[] buffer = new byte[1024*5];                
				               
				while ( (byteread = inStream.read(buffer)) != -1) {                    
					fs.write(buffer, 0, byteread);                
					}                
				inStream.close();  
				fs.close();
				result=true;
				}        
			}        
		catch (Exception e) {            
		           
			e.printStackTrace();        
			}    
		return result;
	}
	public static void fileRename(String oldName,String newName)
	{
		File file=new File(getDiskCacheDir()+oldName);  
		if(file.exists())
		{
			file.renameTo(new File(getDiskCacheDir()+newName));
		}
	}
	public static void fileUrlRename(String oldName,String newName)
	{
		File file=new File(oldName);  
		if(file.exists())
		{
			file.renameTo(new File(newName));
		}
	}

	public static String getFilePathInSD(Activity context,Uri uri)
	{
		String filepath=uri.getPath();
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT; 
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) 
		{  
	        // ExternalStorageProvider  
	        if (isExternalStorageDocument(uri)) {  
	            final String docId = DocumentsContract.getDocumentId(uri);  
	            final String[] split = docId.split(":");  
	            final String type = split[0];  
	  
	            if ("primary".equalsIgnoreCase(type)) {  
	            	filepath=Environment.getExternalStorageDirectory() + "/" + split[1];  
	            }  
	        }  
	        // DownloadsProvider  
	        else if (isDownloadsDocument(uri)) {  
	  
	            final String id = DocumentsContract.getDocumentId(uri);  
	            final Uri contentUri = ContentUris.withAppendedId(  
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));  
	  
	            filepath=getDataColumn(context, contentUri, null, null);  
	        }  
	        // MediaProvider  
	        else if (isMediaDocument(uri)) {  
	            final String docId = DocumentsContract.getDocumentId(uri);  
	            final String[] split = docId.split(":");  
	            final String type = split[0];  
	  
	            Uri contentUri = null;  
	            if ("image".equals(type)) {  
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("video".equals(type)) {  
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("audio".equals(type)) {  
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  
	            }  
	  
	            final String selection = "_id=?";  
	            final String[] selectionArgs = new String[] { split[1] };  
	  
	            filepath=getDataColumn(context, contentUri, selection, selectionArgs);  
	        }  
	    }  
	    // MediaStore (and general)  
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {  
	  
	        // Return the remote address  
	        if (isGooglePhotosUri(uri))  
	            return uri.getLastPathSegment();  
	  
	        filepath=getDataColumn(context, uri, null, null);  
	    }  
	    // File  
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {  
	    	filepath= uri.getPath();  
	    } 
	    return filepath;
		
		
	}
	public static String getDataColumn(Context context, Uri uri, String selection,  
	        String[] selectionArgs) {  
	  
	    Cursor cursor = null;  
	    final String column = "_data";  
	    final String[] projection = { column };  
	  
	    try {  
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,  
	                null);  
	        if (cursor != null && cursor.moveToFirst()) {  
	            final int index = cursor.getColumnIndexOrThrow(column);  
	            return cursor.getString(index);  
	        }  
	    } finally {  
	        if (cursor != null)  
	            cursor.close();  
	    }  
	    return null;  
	}  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is ExternalStorageProvider. 
	 */  
	public static boolean isExternalStorageDocument(Uri uri) {  
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is DownloadsProvider. 
	 */  
	public static boolean isDownloadsDocument(Uri uri) {  
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is MediaProvider. 
	 */  
	public static boolean isMediaDocument(Uri uri) {  
	    return "com.android.providers.media.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is Google Photos. 
	 */  
	public static boolean isGooglePhotosUri(Uri uri) {  
	    return "com.google.android.apps.photos.content".equals(uri.getAuthority());  
	}
}