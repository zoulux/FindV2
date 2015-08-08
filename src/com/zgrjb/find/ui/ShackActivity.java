package com.zgrjb.find.ui;

import com.zgrjb.find.R;
import com.zgrjb.find.utils.ShakeListener;
import com.zgrjb.find.utils.ShakeListener.OnShakeListener;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ShackActivity extends BaseActivity {
	// ����һ��ҡһҡ����
	ShakeListener mShakeListener = null;
	// ����һ������
	private Vibrator mVibrator;
	// ����һ���������ֵ���
	private MediaPlayer player;
	// ����һ�����ϵ�ImageView
	private RelativeLayout mImgUp;
	// ����һ�����µ�ImageView
	private RelativeLayout mImgDn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shack);
		init();

	}

	private void init() {
		showTitleText("ҡһҡ");
		mImgUp = (RelativeLayout) findViewById(R.id.shakeImgUp);
		mImgDn = (RelativeLayout) findViewById(R.id.shakeImgDown);
		initListener();
	}

	private void initListener() {
		mVibrator = (Vibrator) getApplication().getSystemService(
				VIBRATOR_SERVICE);

		mShakeListener = new ShakeListener(ShackActivity.this);
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				startAnim(); // ��ʼ ҡһҡ���ƶ���
				startVibrato(); // ��ʼ ��
				controlTheShake();
			}
		});
	}

	protected void startAnim() {
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-1.0f);
		mytranslateanimup0.setDuration(2000);

		animup.addAnimation(mytranslateanimup0);
		mImgUp.startAnimation(animup);

		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+1.0f);
		mytranslateanimdn0.setDuration(2000);
		animdn.addAnimation(mytranslateanimdn0);
		mImgDn.startAnimation(animdn);

	}

	protected void controlTheShake() {
		mShakeListener.stop();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(ShackActivity.this,
						ScannActivity.class));
				ShackActivity.this.finish();
				overridePendingTransition(R.anim.fade, R.anim.hold);

			}
		}, 1600);

	}

	protected void startVibrato() {
		player = MediaPlayer.create(this, R.raw.notify);
		player.setLooping(false);
		player.start();
		// ������
		mVibrator.vibrate(new long[] { 600, 300, 600, 300 }, -1); // ��һ�����������ǽ������飬
																	// �ڶ����������ظ�������-1Ϊ���ظ�����-1���մ�pattern��ָ���±꿪ʼ�ظ�
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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
	}
}
