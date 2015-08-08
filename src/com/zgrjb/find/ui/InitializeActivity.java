package com.zgrjb.find.ui;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;

import com.baidu.mapapi.SDKInitializer;
import com.zgrjb.find.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class InitializeActivity extends BaseActivity {
	final static String KEY = "ef5110f989ced9713a3840ab32154d05";
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				startActivity(new Intent(InitializeActivity.this,
						LogInActivity.class));
				finish();
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

			} else if (msg.what == 1) {
				startActivity(new Intent(InitializeActivity.this,
						MainUIActivity.class));
				finish();
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initialize);
		BmobChat.getInstance(this).init(KEY);
		BmobChat.getInstance(this).startPollService(30);
		SDKInitializer.initialize(getApplicationContext());
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (BmobUserManager.getInstance(this).getCurrentUser() == null) {
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			mHandler.sendEmptyMessageDelayed(1, 2000);
		}

	}
}
