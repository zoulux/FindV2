package com.zgrjb.find.utils;

import android.graphics.Bitmap;

/**
 * 设置和获取图片碎片的位置以及设置和获取位图
 * 
 * @author ly
 * 
 */
public class ImagePiece {
	private int index;
	private Bitmap bitmap;

	public ImagePiece() {

	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public String toString() {
		return "ImagePiece [index=" + index + ", bitmap=" + bitmap + "]";
	}
}
