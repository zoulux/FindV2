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
	// ����һ��GamePuzzleLayout
	private GamePuzzleLayout mGamePintuLayout;
	// ����ȼ�������
	private TextView mLevel;
	// ����ʱ�������
	private TextView mTime;
	// ������Ϸ���Ѷ�
	private int diff;
	// ����һ�������
	private int randomNumber;
	// ����һ��Myuser��
	private MyUser user;
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
		playMp3Win = MediaPlayer.create(this, R.raw.win);
		playMp3Failue = MediaPlayer.create(this, R.raw.failue);
		mGamePintuLayout.setTimeEnabled(true);
        //���intent��ֵ
		user = (MyUser) getIntent().getSerializableExtra("user");
		Intent intent = getIntent();
		diff = intent.getIntExtra("diff", -1);
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
				new AlertDialog.Builder(PlayPuzzleGameActivity.this)
						.setTitle("��Ϣ")
						.setMessage("��ϲ�㣬��������������������ɣ�")
						.setPositiveButton("ȷ��",
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
						.setTitle("��Ϣ")
						.setMessage("���ź�����δ�ܹ��أ���")
						.setPositiveButton("����һ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int swich) {
										mGamePintuLayout.reStart();
										playMp3Failue.stop();
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
		mGamePintuLayout.setdifficult(diff);
		mGamePintuLayout.setRandomNuber(randomNumber);
	}

	/**
	 * �����·��ؼ���ʱ����Ϸ�᲻����������Ϊ�˱�֤����������У�ִ����һ����ֹͣ��Ϣ�ķ��ͣ���ʱ��ֹͣ
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
