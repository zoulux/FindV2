package com.zgrjb.find.file_handle;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.R.string;
import android.content.Context;
import android.graphics.Bitmap;

public class HandlePicFile {
	private Context context;
	private String filePath;
	   public HandlePicFile(Context context,String filePath){
		   	this.context = context;
		   	this.filePath = filePath;
	   }
	/** 
	 * ����ͼƬ�ļ� 
	 * @param bm 
	 * @param fileName 
	 * @throws IOException 
	 */  
	public void savePictureFile(Bitmap bm, String fileName) throws IOException {  
	    File dirFile = new File(filePath);  
	    if(!dirFile.exists()){  
	        dirFile.mkdir();  
	    }  
	    File myCaptureFile = new File(filePath + fileName);  
	    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
	    bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);  
	    bos.flush();  
	    bos.close();  
	}
}
