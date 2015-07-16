package com.zgrjb.find.ui.puzzle_game;

import com.zgrjb.find.R;
import com.zgrjb.find.ui.BaseActivity;
import com.zgrjb.find.ui.ChatActivity;
import com.zgrjb.find.ui.MainUIActivity;
import com.zgrjb.find.ui.puzzle_game.GamePuzzleLayout.GamePintuListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

public class TryToPlayGameActivity extends BaseActivity {
	// 定义一个GamePuzzleLayout
	private GamePuzzleLayout mGamePintuLayout;
	// 定义等级的文字
	private TextView mLevel;
	// 定义时间的数字
	private TextView mTime;
	// 定义游戏的难度
	private int diff2;
	// 定义一个随机数
	private int randomNumber;
	// 定义一个闯关成功的音乐
	private MediaPlayer playMp3Win;
	// 定义一个闯关失败的的音乐
	private MediaPlayer playMp3Failue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_game);
		mLevel = (TextView) this.findViewById(R.id.id_level);
		mTime = (TextView) this.findViewById(R.id.id_time);
		mGamePintuLayout = (GamePuzzleLayout) this.findViewById(R.id.gamePintu);
		mGamePintuLayout.setTimeEnabled(true);

		playMp3Win = MediaPlayer.create(this, R.raw.win);
		playMp3Failue = MediaPlayer.create(this, R.raw.failue);
		// 获得intent的值
		Intent intent = getIntent();
		diff2 = intent.getIntExtra("diff", -1);
		randomNumber = intent.getIntExtra("random", -1);
		mLevel.setText(intent.getStringExtra("diffValue"));
		// 设置游戏的难度
		set();

		mGamePintuLayout.setOnGamePintuListener(new GamePintuListener() {

			@Override
			public void timeChaneged(int currentTime) {
				mTime.setText("" + currentTime);

			}

			@Override
			public void youWin(int level) {
				playMp3Win.start();
				new AlertDialog.Builder(TryToPlayGameActivity.this)
						.setTitle("信息")
						.setMessage("恭喜你，过关啦！！")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int swich) {
										playMp3Win.stop();
										TryToPlayGameActivity.this.finish();
									}
								}).show();
			}

			@Override
			public void gameOver() {
				playMp3Failue.start();
				new AlertDialog.Builder(TryToPlayGameActivity.this)
						.setTitle("信息")
						.setMessage("很遗憾，您未能过关！！")
						.setPositiveButton("再来一次",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int swich) {
										playMp3Failue.stop();
										mGamePintuLayout.reStart();
									}
								})
						.setNegativeButton("退出",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										playMp3Failue.stop();
										finish();
									}
								}).show();

			}

		});
	}

	// 设置游戏的难度
	private void set() {
		mGamePintuLayout.setdifficult(diff2);
		mGamePintuLayout.setRandomNuber(randomNumber);
	}

	/**
	 * 当按下返回键的时候，游戏会不正常结束，为了保证软件正常运行，执行这一步，停止消息的发送，让时间停止
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mGamePintuLayout.stopAndRemove();
		Intent intent2 = new Intent("send");
		sendBroadcast(intent2);
		Intent intent = new Intent(TryToPlayGameActivity.this,
				MainUIActivity.class);
		startActivity(intent);
		TryToPlayGameActivity.this.finish();

	}

	@Override
	protected void onPause() {
		super.onPause();
		mGamePintuLayout.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGamePintuLayout.resume();
	}

}
