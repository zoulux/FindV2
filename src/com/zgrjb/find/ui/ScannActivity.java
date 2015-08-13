package com.zgrjb.find.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.zgrjb.find.R;
import com.zgrjb.find.utils.ShakeListener;
import com.zgrjb.find.utils.ShakeListener.OnShakeListener;

public class ScannActivity extends BaseActivity {
	private ImageView quanquan;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_ui_discover_scanning);
		init();
	}

	private void init() {

		showTitleText("ËÑË÷ÖÐ.....");
		quanquan = (ImageView) findViewById(R.id.id_quanquan);
		rotateQuan();
		new Handler().postDelayed(new Runnable() {
			public void run() {
				startActivity(new Intent(ScannActivity.this, MapActivity.class));
				ScannActivity.this.finish();
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
		}, 3000);
	}

	private void rotateQuan() {
		RotateAnimation ani = new RotateAnimation(0, -360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ani.setDuration(3000);
		quanquan.startAnimation(ani);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		System.out.println("");
		//overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
	}

}