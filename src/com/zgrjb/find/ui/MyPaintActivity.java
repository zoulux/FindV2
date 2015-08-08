package com.zgrjb.find.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.zgrjb.find.R;
import com.zgrjb.find.view.MyPaintView;

public class MyPaintActivity extends BaseActivity {
	private ImageView rightBt;
	private MyPaintView paintView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_paint);
		paintView = (MyPaintView) findViewById(R.id.id_my_paint_view);
		initActionBar();

	}

	private void initActionBar() {
		showTitleText("Ëæ±Ê»­");
		setDrawablePath(getResources().getDrawable(R.drawable.chat_send_n));
		rightButtonIsVisible(true);
		rightBt = rightImageView;

		rightBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveAndSend();

			}
		});
	}

	protected void saveAndSend() {
		paintView.save();
		sendBroadCast();
		finish();
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	}

	private void sendBroadCast() {
		Intent intent = new Intent("sendPaint");
		intent.putExtra("date", paintView.getDateString());
		this.sendBroadcast(intent);
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
	}

}
