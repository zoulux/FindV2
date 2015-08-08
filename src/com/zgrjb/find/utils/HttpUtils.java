package com.zgrjb.find.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zgrjb.find.bean.ChatMessage;
import com.zgrjb.find.bean.ChatMessage.Type;
import com.zgrjb.find.bean.Result;

public class HttpUtils {

	private static final String URL = "http://www.tuling123.com/openapi/api";
	private static final String API_KEY = "65dceb266e67b766cf90568eae508d13";

	/**
	 * 发送一个消息
	 * 返回一个消息
	 * 
	 * */
	public static ChatMessage sendMessage(String msg) {
		ChatMessage message = new ChatMessage();

		String jsonRes = doGet(msg);
		Gson gson = new Gson();
		Result result = null;
		try {
			result = gson.fromJson(jsonRes, Result.class);
			System.out.println(result.getText()+">>>>");
			message.setMsg(
					result.getText());
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			message.setMsg("服务器繁忙，稍后重试！");
		}
		message.setDate(new Date());
		message.setType(Type.INCOMING);

		return message;

	}

	public static String doGet(String msg) {
		String result = "";
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		String url = setParams(msg);
		try {
			java.net.URL urlNet = new java.net.URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlNet
					.openConnection();
			conn.setReadTimeout(5 * 1000);
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");

			is = conn.getInputStream();
			int len = -1;
			byte[] buf = new byte[128];
			baos = new ByteArrayOutputStream();

			while ((len = is.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			baos.flush();
			result = new String(baos.toByteArray());

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;

	}

	private static String setParams(String msg) {

		String url = "";
		try {
			url = URL + "?key=" + API_KEY + "&info="
					+ URLEncoder.encode(msg, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return url;
	}
}
