package com.zgrjb.find.ui.fragment;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.zgrjb.find.R;
import com.zgrjb.find.ui.ChoiceThemeActivity;
import com.zgrjb.find.ui.MainUIActivity;
import com.zgrjb.find.ui.RobotActivity;
import com.zgrjb.find.ui.ScannActivity;
import com.zgrjb.find.ui.ShackActivity;
import com.zgrjb.find.ui.puzzle_game.TryToPrepareGame;
import com.zgrjb.find.utils.FileServiceFlag;
import com.zgrjb.find.view.InitView;
import com.zgrjb.find.view.MenuView;
import com.zgrjb.find.view.MenuView.onMenuItemClickListener;

public class DiscoverFragment extends Fragment {
	private InitView initView;
	private MenuView menuView;
	private BroadcastReceiver broadcastReceiver2;

	// 定义游戏难度系数
	private int whatDiff;
	// 定义dialog
	private AlertDialog.Builder builder;
	// 定义四个选项的值
	private final int SHACK_HAND = 0;
	private final int GOOD_THEME = 1;
	private final int PLAY_GAME = 2;
	private final int TALK_TIME = 3;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.main_ui_discover_fragment,
				container, false);
		initView = (InitView) rootView.findViewById(R.id.id_initView);
		menuView = (MenuView) rootView.findViewById(R.id.id_spacialView);
		builder = new AlertDialog.Builder(DiscoverFragment.this.getActivity());
		menuView.GetValue(initView);
		setItemListener();
		broadcastReceiver2 = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				whatDiff = 0;
			}
		};
		return rootView;
	}

	/**
	 * 设置每个选项的监听事件
	 */
	private void setItemListener() {
		menuView.setOnMenuItemClickListener(new onMenuItemClickListener() {

			@Override
			public void onClick(View view, int position) {
				switch (position) {
				case SHACK_HAND:
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							Intent intent1 = new Intent(DiscoverFragment.this
									.getActivity(), ShackActivity.class);
							startActivity(intent1);
							DiscoverFragment.this
									.getActivity()
									.overridePendingTransition(
											R.anim.zoom_enter, R.anim.zoom_exit);

						}
					}, 200);

					break;
				case GOOD_THEME:
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							Intent intent2 = new Intent(DiscoverFragment.this
									.getActivity(), ChoiceThemeActivity.class);
							startActivity(intent2);
							DiscoverFragment.this
									.getActivity()
									.overridePendingTransition(
											R.anim.zoom_enter, R.anim.zoom_exit);

						}
					}, 200);

					break;
				case PLAY_GAME:
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							showDialogToChoiceDifficult();
						}
					}, 200);

					break;
				case TALK_TIME:
					new Handler().postDelayed(new Runnable() {
						public void run() {
							startActivity(new Intent(DiscoverFragment.this
									.getActivity(), RobotActivity.class));
							DiscoverFragment.this
									.getActivity()
									.overridePendingTransition(
											R.anim.zoom_enter, R.anim.zoom_exit);
						}
					}, 200);

					break;

				}
			}
		});
	}

	/**
	 * 对游戏选择的dialog的显示
	 */
	private void showDialogToChoiceDifficult() {
		builder.setTitle("拼图游戏难度选择");
		final String[] hobby = { "容易", "正常", "艰难" };
		builder.setSingleChoiceItems(hobby, 0,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						whatDiff = which;

					}
				});

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				Intent intent = new Intent(DiscoverFragment.this.getActivity(),
						TryToPrepareGame.class);
				intent.putExtra("diff", whatDiff + 6);
				intent.putExtra("diffValue", hobby[whatDiff]);
				DiscoverFragment.this.startActivity(intent);
				DiscoverFragment.this.getActivity().overridePendingTransition(
						R.anim.zoom_enter, R.anim.zoom_exit);

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter intentFilter2 = new IntentFilter();
		intentFilter2.addAction("send");
		DiscoverFragment.this.getActivity().registerReceiver(
				broadcastReceiver2, intentFilter2);
	}

}
