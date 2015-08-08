package com.zgrjb.find.ui.puzzle_game;

import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.ui.BaseActivity;
import com.zgrjb.find.ui.ChatActivity;
import com.zgrjb.find.ui.puzzle_game.GamePuzzleLayout.GamePintuListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

public class PlayPuzzleGameActivity extends BaseActivity {
	// 定义一个GamePuzzleLayout
	private GamePuzzleLayout mGamePintuLayout;
	// 定义等级的文字
	private TextView mLevel;
	// 定义时间的数字
	private TextView mTime;
	// 定义游戏的难度
	private int diff;
	// 定义一个随机数
	private int randomNumber;
	// 定义一个Myuser类
	private MyUser user;
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
		playMp3Win = MediaPlayer.create(this, R.raw.win);
		playMp3Failue = MediaPlayer.create(this, R.raw.failue);
		mGamePintuLayout.setTimeEnabled(true);
        //获得intent的值
		user = (MyUser) getIntent().getSerializableExtra("user");
		Intent intent = getIntent();
		diff = intent.getIntExtra("diff", -1);
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
				new AlertDialog.Builder(PlayPuzzleGameActivity.this)
						.setTitle("信息")
						.setMessage("恭喜你，过关啦！！跟好友聊天吧！")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int swich) {
										Intent intent = new Intent(
												PlayPuzzleGameActivity.this,
												ChatActivity.class);
										intent.putExtra("user", user);
										startActivity(intent);
										playMp3Win.stop();
										PlayPuzzleGameActivity.this.finish();
										overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
									}
								}).show();
			}

			@Override
			public void gameOver() {
				playMp3Failue.start();
				new AlertDialog.Builder(PlayPuzzleGameActivity.this)
						.setTitle("信息")
						.setMessage("很遗憾，您未能过关！！")
						.setPositiveButton("再来一次",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int swich) {
										mGamePintuLayout.reStart();
										playMp3Failue.stop();
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
		mGamePintuLayout.setdifficult(diff);
		mGamePintuLayout.setRandomNuber(randomNumber);
	}

	/**
	 * 当按下返回键的时候，游戏会不正常结束，为了保证软件正常运行，执行这一步，停止消息的发送，让时间停止
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mGamePintuLayout.stopAndRemove();
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
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
