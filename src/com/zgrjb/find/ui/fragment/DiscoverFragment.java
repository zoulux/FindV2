package com.zgrjb.find.ui.fragment;

import java.io.File;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.zgrjb.find.R;
import com.zgrjb.find.ui.ChoiceThemeActivity;
import com.zgrjb.find.ui.ShackActivity;
import com.zgrjb.find.ui.puzzle_game.TryToPrepareGame;
import com.zgrjb.find.utils.FileServiceFlag;

public class DiscoverFragment extends Fragment implements OnClickListener {
	// 定义摇一摇选项
	private LinearLayout shakeHandlLayout;
	// 定义炫主题选项
	private LinearLayout goodThemeLayout;
	// 定义玩游戏选项
	private LinearLayout playGameLayout;
	// 定义rootView
	private View rootView;
	// 定义游戏难度系数
	private int whatDiff;
	// 定义dialog
	private AlertDialog.Builder builder;
	// 定义广播接收者来接收主题设置的广播
	private BroadcastReceiver broadcastReceiver;

	private final int COLOR_BLACK = 1;
	private final int COLOR_GREEN = 2;
	private final int COLOR_BLUE = 3;
	private final int COLOR_PINK = 4;
	private FileServiceFlag serviceFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 初始话id和设置点击事件和接收到的广播来跟换主题
	 */
	private void init() {
		shakeHandlLayout = (LinearLayout) rootView.findViewById(R.id.shackhand);
		goodThemeLayout = (LinearLayout) rootView.findViewById(R.id.goodTheme);
		playGameLayout = (LinearLayout) rootView.findViewById(R.id.playGame);
		shakeHandlLayout.setOnClickListener(DiscoverFragment.this);
		goodThemeLayout.setOnClickListener(DiscoverFragment.this);
		playGameLayout.setOnClickListener(DiscoverFragment.this);
		itemColorSet();
		builder = new AlertDialog.Builder(DiscoverFragment.this.getActivity());
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int value = intent.getIntExtra("theme", 0);
				switch (value) {
				case 1:
					shakeHandlLayout
							.setBackgroundResource(R.color.theme_bg_black);
					goodThemeLayout
							.setBackgroundResource(R.color.theme_bg_black);
					playGameLayout
							.setBackgroundResource(R.color.theme_bg_black);
					break;
				case 2:
					shakeHandlLayout
							.setBackgroundResource(R.color.theme_bg_green);
					goodThemeLayout
							.setBackgroundResource(R.color.theme_bg_green);
					playGameLayout
							.setBackgroundResource(R.color.theme_bg_green);
					break;
				case 3:
					shakeHandlLayout
							.setBackgroundResource(R.color.theme_bg_blue);
					goodThemeLayout
							.setBackgroundResource(R.color.theme_bg_blue);
					playGameLayout
							.setBackgroundResource(R.color.theme_bg_blue);
					break;
				case 4:
					shakeHandlLayout
							.setBackgroundResource(R.color.theme_bg_pink);
					goodThemeLayout
							.setBackgroundResource(R.color.theme_bg_pink);
					playGameLayout
							.setBackgroundResource(R.color.theme_bg_pink);
					break;
				case 5:
					shakeHandlLayout
							.setBackgroundResource(R.color.theme_bg_defualt);
					goodThemeLayout
							.setBackgroundResource(R.color.theme_bg_defualt);
					playGameLayout
							.setBackgroundResource(R.color.theme_bg_defualt);
					break;
				}
			}
		};
	}

	/**
	 * 选项颜色设置
	 */
	private void itemColorSet() {
		File colorFile = new File(
				"data/data/com.zgrjb.find/files/colorSave.txt");
		if (colorFile.exists()) {
			int color = Integer.parseInt(serviceFlag
					.readContentFromFile("colorSave.txt"));
			switch (color) {
			case COLOR_BLACK:
				shakeHandlLayout
						.setBackgroundResource(R.color.theme_bg_black);
				goodThemeLayout
						.setBackgroundResource(R.color.theme_bg_black);
				playGameLayout
						.setBackgroundResource(R.color.theme_bg_black);
				break;
			case COLOR_GREEN:
				shakeHandlLayout
						.setBackgroundResource(R.color.theme_bg_green);
				goodThemeLayout
						.setBackgroundResource(R.color.theme_bg_green);
				playGameLayout
						.setBackgroundResource(R.color.theme_bg_green);
				break;
			case COLOR_BLUE:
				shakeHandlLayout
						.setBackgroundResource(R.color.theme_bg_blue);
				goodThemeLayout
						.setBackgroundResource(R.color.theme_bg_blue);
				playGameLayout
						.setBackgroundResource(R.color.theme_bg_blue);
				break;
			case COLOR_PINK:
				shakeHandlLayout
						.setBackgroundResource(R.color.theme_bg_pink);
				goodThemeLayout
						.setBackgroundResource(R.color.theme_bg_pink);
				playGameLayout.setBackgroundResource(R.color.theme_bg_pink);
				break;

			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.main_ui_discover_fragment,
				container, false);
		serviceFlag = new FileServiceFlag(DiscoverFragment.this.getActivity());
		init();

		return rootView;
	}

	@Override
	public void onClick(View v) {
		if (v == shakeHandlLayout) {
			Intent intent = new Intent(DiscoverFragment.this.getActivity(),
					ShackActivity.class);
			startActivity(intent);
		} else if (v == goodThemeLayout) {
			Intent intent = new Intent(DiscoverFragment.this.getActivity(),
					ChoiceThemeActivity.class);
			startActivity(intent);
		} else if (v == playGameLayout) {
			showDialogToChoiceDifficult();
		}
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
		// 注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("choiceIcon");
		DiscoverFragment.this.getActivity().registerReceiver(broadcastReceiver,
				intentFilter);
	}

}
