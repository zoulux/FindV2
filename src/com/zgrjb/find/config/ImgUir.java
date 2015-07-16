package com.zgrjb.find.config;

import java.io.File;

import android.os.Environment;

public class ImgUir {
	//拍照之后，裁切好后照片存放的路径,用与存放头像
	public static File tempFile=new File(Environment
			.getExternalStorageDirectory() + "/Find/Picture_Regist/cut.jpg/");
	//读取照片的路径
	public final static String ALBUM_PATH = Environment
			.getExternalStorageDirectory() + "/Find/Picture_Regist/";
	
	//拍照之后，裁切好后照片存放的路径,用与存放头像
		public static File sendFile=new File(Environment
				.getExternalStorageDirectory() + "/Find/Picture_Regist/sendCut.jpg");
}
