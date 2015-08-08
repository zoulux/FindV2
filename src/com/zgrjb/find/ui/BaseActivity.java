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
	// 定义titleBar中间的文字
	private TextView textView;
	// 定义titleBar左边的按钮
	public ImageView leftImageView;
	// 定义titleBar右边的按钮
	public ImageView rightImageView;
	// 定义一个drawable
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
	 * 设置titleBar左边的按钮是否显示
	 * 
	 * @param b
	 */
	@SuppressLint("NewApi")
	public void leftButtonIsVisible(boolean b) {
		if (b) {
			leftImageView = (ImageView) findViewById(R.id.leftImageView);
			leftImageView.setVisibility(0);// 显示
			leftImageView.setImageDrawable(drawable);

		}

	}

	/**
	 * 设置titleBar右边的按钮是否显示
	 * 
	 * @param b
	 */
	@SuppressLint("NewApi")
	public void rightButtonIsVisible(boolean b) {
		if (b) {
			rightImageView = (ImageView) findViewById(R.id.rightImageView);
			rightImageView.setVisibility(0);// 显示
			rightImageView.setImageDrawable(drawable);
			// rightImageView.setImageResource(imageId);
		}

	}

	/*
	 * 设置drawable的路径
	 */
	public void setDrawablePath(Drawable drawable) {
		this.drawable = drawable;
	}

	/**
	 * 显示titleBar中间的文字
	 * 
	 * @param text
	 *            text文字
	 */
	public void showTitleText(String text) {
		textView = (TextView) findViewById(R.id.titleText);
		textView.setText(text);
	}

	/**
	 * 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
	 * 
	 * @Title: updateUserInfos
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void updateUserInfos() {
		// 不需要更新地理位置信息

		// 查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
		// 这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				if (arg0 == BmobConfig.CODE_COMMON_NONE) {
					ShowLog(arg1);
				} else {
					ShowLog("查询好友列表失败：" + arg1);
				}
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				// 保存到application中方便比较
				CustomApplcation.getInstance().setContactList(
						CollectionUtils.list2map(arg0));
			}
		});
	}

}