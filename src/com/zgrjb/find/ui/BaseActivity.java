package com.zgrjb.find.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import com.zgrjb.find.R;
import com.zgrjb.find.utils.CollectionUtils;
import com.zgrjb.find.utils.CustomApplcation;

public class BaseActivity extends FragmentActivity {

	BmobUserManager userManager;
	BmobChatManager chatManager;
	protected CustomApplcation mApplication;
	// ����titleBar�м������
	private TextView textView;
	// ����titleBar��ߵİ�ť
	public ImageView leftImageView;
	// ����titleBar�ұߵİ�ť
	public ImageView rightImageView;
	// ����һ��drawable
	private Drawable drawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initService();
	}

	private void initService() {
		userManager = BmobUserManager.getInstance(this);
		chatManager = BmobChatManager.getInstance(this);
		mApplication = CustomApplcation.getInstance();

	}

	public void ShowLog(String msg) {
		Log.i(">>>>", msg);
	}

	public void ShowLog(int msg) {
		Log.i(">>>>", msg + "");
	}

	Toast mToast;

	public void ShowToast(final int resId) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mToast == null) {
					mToast = Toast.makeText(
							BaseActivity.this.getApplicationContext(), resId,
							Toast.LENGTH_LONG);
				} else {
					mToast.setText(resId);
				}
				mToast.show();
			}
		});
	}

	public void ShowToast(final String string) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mToast == null) {
					mToast = Toast.makeText(
							BaseActivity.this.getApplicationContext(), string,
							Toast.LENGTH_LONG);
				} else {
					mToast.setText(string);
				}
				mToast.show();
			}
		});
	}

	/**
	 * ����titleBar��ߵİ�ť�Ƿ���ʾ
	 * 
	 * @param b
	 */
	@SuppressLint("NewApi")
	public void leftButtonIsVisible(boolean b) {
		if (b) {
			leftImageView = (ImageView) findViewById(R.id.leftImageView);
			leftImageView.setVisibility(0);// ��ʾ
			leftImageView.setImageDrawable(drawable);

		}

	}

	/**
	 * ����titleBar�ұߵİ�ť�Ƿ���ʾ
	 * 
	 * @param b
	 */
	@SuppressLint("NewApi")
	public void rightButtonIsVisible(boolean b) {
		if (b) {
			rightImageView = (ImageView) findViewById(R.id.rightImageView);
			rightImageView.setVisibility(0);// ��ʾ
			rightImageView.setImageDrawable(drawable);
			// rightImageView.setImageResource(imageId);
		}

	}

	/*
	 * ����drawable��·��
	 */
	public void setDrawablePath(Drawable drawable) {
		this.drawable = drawable;
	}

	/**
	 * ��ʾtitleBar�м������
	 * 
	 * @param text
	 *            text����
	 */
	public void showTitleText(String text) {
		textView = (TextView) findViewById(R.id.titleText);
		textView.setText(text);
	}

	/**
	 * ���ڵ�½�����Զ���½����µ��û����ϼ��������ϵļ�����
	 * 
	 * @Title: updateUserInfos
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void updateUserInfos() {
		// ����Ҫ���µ���λ����Ϣ

		// ��ѯ���û��ĺ����б�(��������б���ȥ���������û���Ŷ),Ŀǰ֧�ֵĲ�ѯ���Ѹ���Ϊ100�������޸����ڵ����������ǰ����BmobConfig.LIMIT_CONTACTS���ɡ�
		// ����Ĭ�ϲ�ȡ���ǵ�½�ɹ�֮�󼴽������б�洢�����ݿ��У������µ���ǰ�ڴ���,
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				if (arg0 == BmobConfig.CODE_COMMON_NONE) {
					ShowLog(arg1);
				} else {
					ShowLog("��ѯ�����б�ʧ�ܣ�" + arg1);
				}
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				// ���浽application�з���Ƚ�
				CustomApplcation.getInstance().setContactList(
						CollectionUtils.list2map(arg0));
			}
		});
	}

}