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
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import com.zgrjb.find.R;
import com.zgrjb.find.utils.ShakeListener;
import com.zgrjb.find.utils.ShakeListener.OnShakeListener;

public class ShackActivity extends Activity {
	// 定义一个摇一摇监听
	ShakeListener mShakeListener = null;
	// 定义一个震动类
	private Vibrator mVibrator;
	// 定义一个向上的layout
	private RelativeLayout mImgUp;
	// 定义一个向下的layout
	private RelativeLayout mImgDn;
	// 定义一个播放音乐的类
	private MediaPlayer player;
	// 在mShakeListener停止的时候，获取当前的时间
	long currentTimestop;
	// 在mShakeListener开启的时候，获取当前的时间
	long currentTimestart;
	// 判断是否可摇，初始化为可摇状态
	private boolean isOnshake = true;
	// 定义一个时间
	private long time = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_ui_discover_shack);

		mVibrator = (Vibrator) getApplication().getSystemService(
				VIBRATOR_SERVICE);

		mImgUp = (RelativeLayout) findViewById(R.id.shakeImgUp);
		mImgDn = (RelativeLayout) findViewById(R.id.shakeImgDown);
		mShakeListener = new ShakeListener(ShackActivity.this);
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				startAnim(); // 开始 摇一摇手掌动画
				startVibrato(); // 开始 震动
				controlTheShake();
			}
		});
	}

	/**
	 * 通过多线程对摇一摇的控制
	 */
	private void controlTheShake() {
		mShakeListener.stop();
		isOnshake = true;
		currentTimestop = System.currentTimeMillis();
		time = currentTimestop;

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				currentTimestart = System.currentTimeMillis();
				mShakeListener.start();
				isOnshake = false;
			}
		}, 2000);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!isOnshake) {
					mShakeListener.stop();
					ProgressDialog dialog2 = ProgressDialog.show(
							ShackActivity.this, "提示", "查找好友中");
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							startActivity(new Intent(ShackActivity.this,
									MapActivity.class));
							finish();
						}
					}, 2000);
				}
			}
		}, 3000);
	}

	/**
	 * 定义摇一摇动画动画
	 */
	private void startAnim() {
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimup0.setDuration(1000);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimup1.setDuration(1000);
		mytranslateanimup1.setStartOffset(1000);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		mImgUp.startAnimation(animup);

		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimdn0.setDuration(1000);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimdn1.setDuration(1000);
		mytranslateanimdn1.setStartOffset(1000);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		mImgDn.startAnimation(animdn);
	}

	/**
	 * 开启震动
	 */
	private void startVibrato() {

		player = MediaPlayer.create(this, R.raw.notify);

		player.setLooping(false);
		player.start();

		// 定义震动
		mVibrator.vibrate(new long[] { 600, 300, 600, 300 }, -1); // 第一个｛｝里面是节奏数组，
																	// 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		mShakeListener.start();

	}

	@Override
	protected void onPause() {

		super.onPause();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}
}