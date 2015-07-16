package com.zgrjb.find.ui.puzzle_game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.ui.BaseActivity;

public class TryToPrepareGame extends BaseActivity {
	// 定义一个按钮isPrepared
	private Button isPrepared;
	// 定义一个给用户准备的图片
	private ImageView prepareImageView;
	// 定义一个意图
	private Intent temIntent;
	// 定义游戏的难度
	private int diff;
	// 定义游戏难度的值
	String diffValue = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prepare_game);
		getIntentValue();
		init();
	}

	/**
	 * 获取Intet的值
	 */
	private void getIntentValue() {
		temIntent = getIntent();
		diff = temIntent.getIntExtra("diff", -1);
		diffValue = temIntent.getStringExtra("diffValue");
	}

	/**
	 * 初始化
	 */
	private void init() {
		prepareImageView = (ImageView) this.findViewById(R.id.prepareImage);
		ChoiceDifferPictureToPlay.getRandomPicture(prepareImageView, this);
		isPrepared = (Button) this.findViewById(R.id.isPrepared);
		isPrepared.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TryToPrepareGame.this,
						TryToPlayGameActivity.class);
				intent.putExtra("diff", diff);
				intent.putExtra("random",
						ChoiceDifferPictureToPlay.getRandomNumber());
				intent.putExtra("diffValue", diffValue);
				startActivity(intent);
//				TryToPrepareGame.this.finish();
			}
		});
	}

}
