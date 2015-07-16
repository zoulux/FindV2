package com.zgrjb.find.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommonUtils {

	/** 检查是否有网络 */
	public static boolean isNetworkAvailable(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}

	/** 检查是否是WIFI */
	public static boolean isWifi(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI)
				return true;
		}
		return false;
	}

	/** 检查是否是移动网络 */
	public static boolean isMobile(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE)
				return true;
		}
		return false;
	}

	private static NetworkInfo getNetworkInfo(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	/** 检查SD卡是否存在 */
	public static boolean checkSdCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
	
	/**
	 * 范围在1~5之间
	 * @return
	 */
	public int getRandomNumber(){
		return (int) (Math.random()*5+1);
	}
	
	public static Bitmap createBitmapRepeate(int width,Bitmap src){
		 	int count = (width + src.getWidth()-1)/src.getWidth();
		 	Bitmap bitmap = Bitmap.createBitmap(width,src.getHeight(),Config.ARGB_8888);
		 	Canvas canvas = new Canvas(bitmap);
		 	for(int i =0;i<count;i++)
		 	{
		 		canvas.drawBitmap(src,i*src.getWidth(),0, null);
		 	}
		 	return bitmap;
	}

}
