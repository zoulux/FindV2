package com.zgrjb.find.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;

public class FileServiceFlag {
	private Context context;

	public FileServiceFlag(Context context) {
		this.context = context;
	}

	/**
	 * 读取data里的file文件
	 * @param fileName
	 * @return
	 */
	public String readContentFromFile(String fileName) {
		FileInputStream fileInputStream = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			fileInputStream = context.openFileInput(fileName);
			int len = 0;
			byte[] data = new byte[1024];
			while ((len = fileInputStream.read(data)) != -1) {
				outputStream.write(data, 0, len);
			}
			return new String(outputStream.toByteArray());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 把文件存储到data里的file文件下
	 * 
	 * @param fileName
	 *            文件名
	 * @param mode
	 *            写入文件的模式
	 * @param data
	 *            存入文件的数据
	 * @return
	 */
	public boolean saveContentToFile(String fileName, int mode, byte[] data) {
		boolean flag = false;
		FileOutputStream outputStream = null;
		try {
			outputStream = context.openFileOutput(fileName, mode);
			outputStream.write(data, 0, data.length);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e2) {
				}
			}
		}

		return flag;
	}
}
