package com.zgrjb.find.ui;

import com.zgrjb.find.R;
import com.zgrjb.find.ui.dialog.MyDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class ChoiceThemeActivity extends BaseActivity implements
		OnClickListener {
	private Button defualtTheme;
	// 定义黑色主题按钮
	private Button blackThemeBt;
	// 定义绿色主题按钮
	private Button greenThemeBt;
	// 定义蓝色主题按钮
	private Button blueThemeBt;
	// 定义粉红色主题按钮
	private Button pinkThemeBt;
	// 定义一个是否设置主题的dialog
	private MyDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choice_theme);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		blackThemeBt = (Button) this.findViewById(R.id.black_theme_bt);
		blackThemeBt.setOnClickListener(this);
		greenThemeBt = (Button) this.findViewById(R.id.green_theme_bt);
		greenThemeBt.setOnClickListener(this);
		blueThemeBt = (Button) this.findViewById(R.id.blue_theme_bt);
		blueThemeBt.setOnClickListener(this);
		pinkThemeBt = (Button) this.findViewById(R.id.red_theme_bt);
		pinkThemeBt.setOnClickListener(this);
		dialog = new MyDialog(this);
		defualtTheme = (Button) this.findViewById(R.id.default_theme_bt);
		defualtTheme.setOnClickListener(this);
	}

	/**
	 * 监听的设置
	 */
	@Override
	public void onClick(View v) {
		if (v == blackThemeBt) {
			dialog.showChoiceThemeDialog(1);
		} else if (v == greenThemeBt) {
			dialog.showChoiceThemeDialog(2);
		} else if (v == blueThemeBt) {
			dialog.showChoiceThemeDialog(3);
		} else if (v == pinkThemeBt) {
			dialog.showChoiceThemeDialog(4);
		} else if (v == defualtTheme) {
			dialog.showChoiceThemeDialog(5);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
	}
}
