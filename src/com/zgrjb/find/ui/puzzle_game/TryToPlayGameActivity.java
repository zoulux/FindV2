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
	// ����һ��GamePuzzleLayout
	private GamePuzzleLayout mGamePintuLayout;
	// ����ȼ�������
	private TextView mLevel;
	// ����ʱ�������
	private TextView mTime;
	// ������Ϸ���Ѷ�
	private int diff2;
	// ����һ�������
	private int randomNumber;
	// ����һ�����سɹ�������
	private MediaPlayer playMp3Win;
	// ����һ������ʧ�ܵĵ�����
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
		//���intent��ֵ
		Intent intent = getIntent();
		diff2 = intent.getIntExtra("diff", -1);
		randomNumber = intent.getIntExtra("random", -1);
		mLevel.setText(intent.getStringExtra("diffValue"));
		// ������Ϸ���Ѷ�
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
						.setTitle("��Ϣ")
						.setMessage("��ϲ�㣬����������")
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int swich) {
										playMp3Win.stop();
										TryToPlayGameActivity.this.finish();
										overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
									}
								}).show();
			}

			@Override
			public void gameOver() {
				playMp3Failue.start();
				new AlertDialog.Builder(TryToPlayGameActivity.this)
						.setTitle("��Ϣ")
						.setMessage("���ź�����δ�ܹ��أ���")
						.setPositiveButton("����һ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int swich) {
										playMp3Failue.stop();
										mGamePintuLayout.reStart();
									}
								})
						.setNegativeButton("�˳�",
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
	// ������Ϸ���Ѷ�
	private void set() {
		mGamePintuLayout.setdifficult(diff2);
		mGamePintuLayout.setRandomNuber(randomNumber);
	}

	/**
	 * �����·��ؼ���ʱ����Ϸ�᲻����������Ϊ�˱�֤����������У�ִ����һ����ֹͣ��Ϣ�ķ��ͣ���ʱ��ֹͣ
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		mGamePintuLayout.stopAndRemove();
		Intent intent2 = new Intent("send");
		sendBroadcast(intent2);
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
//		Intent intent = new Intent(TryToPlayGameActivity.this,MainUIActivity.class);
//		startActivity(intent);
//		TryToPlayGameActivity.this.finish();

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
