package com.zgrjb.find.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.ui.fragment.ContactsFragment;
import com.zgrjb.find.ui.fragment.DiscoverFragment;
import com.zgrjb.find.ui.fragment.RecentFragment;
import com.zgrjb.find.utils.CustomApplcation;
import com.zgrjb.find.utils.FileServiceFlag;
import com.zgrjb.find.utils.ImageLoadOptions;
import com.zgrjb.find.utils.MyMessageReceiver;
import com.zgrjb.find.utils.SharePreferenceUtil;
import com.zgrjb.find.view.AddAndScanView;
import com.zgrjb.find.view.AddAndScanView.onRightTopBarMenuItemClickListener;

public class MainUIActivity extends BaseActivity implements EventListener,
		OnClickListener {
	// 定义一个侧滑左菜单
	private static SlidingMenu mLeftmenu;
	// 定义一个ArrayList来接收每个pageritems的id
	private ArrayList<PagerItem> pagerItems;
	private ViewPager viewPager;
	// 定义一个主界面的适配器
	private MainAdapter adapter;
	private RadioGroup radioGroup;
	public int position;

	private RecentFragment recentFragment;
	public static final int TAB_RECENT = 0;
	public static final int TAB_FIND = 1;
	public static final int TAB_CONTACT = 2;
	// 定义当前的页面
	private int currentPage;
	private RelativeLayout userSetLayout;// 用户资料设置的相对布局
	private RelativeLayout messageSetLayout;// 接受消息通知的相对布局
	private ImageView myAvartar;// 自己的头像
	private RelativeLayout voiceSetLayout;// 声音的相对布局
	private RelativeLayout vibrateSetLayout;// 震动的相对布局
	private ImageView messageSetTrue;// 消息菜单打开图片
	private ImageView messageSetFalse;// 消息菜单关闭图片
	private ImageView voiceSetTrue;// 声音菜单打开图片
	private ImageView voiceSetFalse;// 声音菜单关闭图片
	private ImageView vibrateSetTrue;// 震动菜单打开图片
	private ImageView vibrateSetFalse;// 震动菜单关闭图片
	private View viewVoice;// 指声音菜单底下的那根白边，在此设置影藏和显示
	private View viewVibrate;// 指震动菜单底下的那根白边，在此设置影藏和显示
	private Button quitBt;
	private boolean isOpen = true;// 判断接受新消息的底下的菜单是否打开
	private boolean isVoiceOpen = true;// 判断声音是否打开
	private boolean isVibrateOpen = true;// 判断震动是否打开
	private MediaPlayer mediaPlayer = new MediaPlayer();// 定义一个播放器，并分配空间
	private AlertDialog.Builder builder;// 定义一个对话框
	private ImageView LeftMenuTitleBar;// 定义一个TitleBar上的左边的按钮
	private ImageView rightMenuTitleBar;// 定义一个TitleBar上的左边的按钮
	private LinearLayout acionbarBgLayout;// 定义整个的TitleBar
	private static AddAndScanView menu;
	private RelativeLayout mainBg;// 为主界面设置背景

	private BroadcastReceiver broadcastReceiver;// 定义一个广播接收者
	// private BroadcastReceiver broadcastReceiver2;

	private SharePreferenceUtil shPreferenceUtil;// 定义一个共享工具类
	private CustomApplcation mApplication;

	private final int COLOR_BLACK = 1;
	private final int COLOR_GREEN = 2;
	private final int COLOR_BLUE = 3;
	private final int COLOR_PINK = 4;
	private final int COLOR_DEFUALT = 5;

	private int messageValue = 0;
	private int voiceValue = 0;
	private int vibrateValue = 0;
	// 定义一个文件处理标记类，并分配空间
	private FileServiceFlag serviceFlag = new FileServiceFlag(this);

	// 做边显示的用户昵称
	private TextView tv_nick;
	// 定义当前的user
	private MyUser user;

	// 初始化handler来接收当前的user
	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.what == 100) {

				MyUser user = (MyUser) msg.obj;
				Intent i = new Intent(MainUIActivity.this, ChatActivity.class);
				i.putExtra("user", user);
				startActivity(i);

			} else {

				user = (MyUser) msg.obj;
				showYourAvatar();

			}
		};
	};

	private myRightBtHandler rightBtHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_ui_activity);

		initBmob();
		rightBtHandler = new myRightBtHandler();
		mLeftmenu = (SlidingMenu) this.findViewById(R.id.id_menu);
		menu = (AddAndScanView) findViewById(R.id.id_addAndScanView);
		builder = new AlertDialog.Builder(this);
		setDrawablePath(getResources().getDrawable(R.drawable.set));
		setRightDrawablePath(getResources().getDrawable(R.drawable.set));
		rightButtonIsVisible(true);
		rightMenuTitleBar = rightImageView;
		setRightTitleBarListener();
		setItemListener();

		leftButtonIsVisible(true);
		LeftMenuTitleBar = leftImageView;
		mApplication = CustomApplcation.getInstance();
		shPreferenceUtil = mApplication.getSharePreferenceUtil();

		initPageItem();
		initRadioGroup();
		initPageList();
		init();
		initColorSave();
		initMenuSet();

		rotateLeftMenuTitleBar();

		new Thread(new myRightBtThread()).start();

	}

	private void setRightTitleBarListener() {
		rightMenuTitleBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// ShowToast(">>");
				menu.toggleMenu(300);
			}
		});
	}

	/**
	 * 设置每个选项的监听事件
	 */
	private void setItemListener() {
		menu.setRightTopBarMenuItemClickListener(new onRightTopBarMenuItemClickListener() {

			@Override
			public void onClick(View v, int position) {
				switch (position) {
				case 0:
					System.out.println(0 + ">>>>>");
					Intent intent = new Intent(MainUIActivity.this,
							CreateQrCodeActivity.class);
					intent.putExtra("QrStringValue", user.getObjectId());

					startActivity(intent);
					overridePendingTransition(R.anim.fade, R.anim.hold);
					break;
				case 1:
					System.out.println(1 + ">>>>>");
					// ShowToast("1>>>>>");

					Intent intent2 = new Intent(MainUIActivity.this,
							CaptureActivity.class);
					startActivityForResult(intent2, 0);
					overridePendingTransition(R.anim.fade, R.anim.hold);
					break;

				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String result = data.getExtras().getString("result");
			System.out.println(result);
			query(result);
		}
	}

	ProgressDialog mProgressDialog = null;

	private void query(String userId) {
		mProgressDialog = new ProgressDialog(MainUIActivity.this);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage("正在努力添加...");
		mProgressDialog.setTitle("提示");
		mProgressDialog.show();
		BmobQuery<MyUser> bmobQuery = new BmobQuery<MyUser>();
		bmobQuery.getObject(MainUIActivity.this, userId,
				new GetListener<MyUser>() {

					@Override
					public void onSuccess(final MyUser user) {
						mProgressDialog.dismiss();
						new Thread() {
							public void run() {
								Message msg = new Message();
								msg.obj = user;
								msg.what = 100;
								mHandler.sendMessage(msg);
							};

						}.start();
					}

					@Override
					public void onFailure(int arg0, String arg1) {

					}
				});

	}

	private void initBmob() {
		BmobChat.getInstance(this).init(InitializeActivity.KEY);
		BmobChat.getInstance(this).startPollService(30);
		SDKInitializer.initialize(getApplicationContext());

	}

	/**
	 * 点击TitleBar上的左边的按钮会触发一个动作
	 * 
	 * @param v
	 *            按钮
	 * @param start
	 *            开始角度
	 * @param end
	 *            结束角度
	 * @param duration
	 *            持续时间
	 */
	public void setLeftMenuAnimation(ImageView v, float start, float end,
			int duration) {
		RotateAnimation animation = new RotateAnimation(start, end,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		v.startAnimation(animation);
	}

	/**
	 * 当侧滑菜单时，齿轮顺时针或逆时针旋转，此方法只是传递参数的作用
	 */
	public void rotateLeftMenuTitleBar() {
		mLeftmenu.rotating(LeftMenuTitleBar);
	}

	public class myRightBtThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				// 从消息池中获取消息，如果没有消息，创建一个消息，如果有，则取出来消息携带数据，由handler发送
				Message message = Message.obtain();
				rightBtHandler.sendMessage(message);
			}
		}

	}

	public static class myRightBtHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			// System.out.println(menu.isOPenMenu()+"  "+mLeftmenu.isMove+">>>>>>>>");
			if ((menu.isOPenMenu()) && (mLeftmenu.isMove)) {
				menu.closeMenu();

			}
			mLeftmenu.isMove = false;
		}
	}

	/**
	 * 关闭或打开左菜单
	 * 
	 * @param view
	 */
	public void toggleMenu(View view) {
		setLeftMenuAnimation(LeftMenuTitleBar, 0f, 180f, 500);
		mLeftmenu.toggle();
	}

	/**
	 * 初始化设置界面和监听
	 */
	private void init() {

		tv_nick = (TextView) findViewById(R.id.nickText);

		userSetLayout = (RelativeLayout) this
				.findViewById(R.id.main_ui_user_set_layout);
		quitBt = (Button) this.findViewById(R.id.main_ui_quit_bt);
		myAvartar = (ImageView) findViewById(R.id.id_frontAvartar);
		messageSetTrue = (ImageView) this
				.findViewById(R.id.main_ui_new_message_set_true);
		voiceSetTrue = (ImageView) this
				.findViewById(R.id.main_ui_voice_set_true);
		vibrateSetTrue = (ImageView) this
				.findViewById(R.id.main_ui_vibrate_set_true);
		messageSetFalse = (ImageView) this
				.findViewById(R.id.main_ui_new_message_set_false);
		voiceSetFalse = (ImageView) this
				.findViewById(R.id.main_ui_voice_set_false);
		vibrateSetFalse = (ImageView) this
				.findViewById(R.id.main_ui_vibrate_set_false);
		messageSetLayout = (RelativeLayout) this
				.findViewById(R.id.main_ui_new_message_set_layout);
		voiceSetLayout = (RelativeLayout) this
				.findViewById(R.id.main_ui_voice_set_layout);
		vibrateSetLayout = (RelativeLayout) this
				.findViewById(R.id.main_ui_vibrate_set_layout);

		viewVoice = (View) this.findViewById(R.id.viewVoice);
		viewVibrate = (View) this.findViewById(R.id.viewVibrate);

		mainBg = (RelativeLayout) this.findViewById(R.id.main_bg);
		acionbarBgLayout = (LinearLayout) this.findViewById(R.id.action_bar);
		userSetLayout.setOnClickListener(this);

		quitBt.setOnClickListener(this);

		messageSetLayout.setOnClickListener(this);

		voiceSetLayout.setOnClickListener(this);

		vibrateSetLayout.setOnClickListener(this);

		serviceFlag = new FileServiceFlag(this);

		// broadcastReceiver2 = new BroadcastReceiver() {
		//
		// @Override
		// public void onReceive(Context arg0, Intent arg1) {
		// MainUIActivity.this.finish();
		// }
		// };

		broadcastReceiver = new BroadcastReceiver() {

			@SuppressLint("NewApi")
			@Override
			public void onReceive(Context context, Intent intent) {
				int value = intent.getIntExtra("theme", 0);
				switch (value) {
				case 1:
					radioGroup.setBackgroundResource(R.color.theme_bg_black);
					acionbarBgLayout
							.setBackgroundResource(R.color.theme_bg_black);
					mainBg.setBackgroundResource(R.drawable.mainbg1);
					// 保存颜色
					saveThemeSet(COLOR_BLACK);
					break;
				case 2:
					radioGroup.setBackgroundResource(R.color.theme_bg_green);
					acionbarBgLayout
							.setBackgroundResource(R.color.theme_bg_green);
					mainBg.setBackgroundResource(R.drawable.mainbg2);
					// 保存颜色
					saveThemeSet(COLOR_GREEN);
					break;
				case 3:
					radioGroup.setBackgroundResource(R.color.theme_bg_blue);
					acionbarBgLayout
							.setBackgroundResource(R.color.theme_bg_blue);
					mainBg.setBackgroundResource(R.drawable.mainbg3);
					// 保存颜色
					saveThemeSet(COLOR_BLUE);
					break;
				case 4:
					radioGroup.setBackgroundResource(R.color.theme_bg_pink);
					acionbarBgLayout
							.setBackgroundResource(R.color.theme_bg_pink);
					mainBg.setBackgroundResource(R.drawable.mainbg4);
					// 保存颜色
					saveThemeSet(COLOR_PINK);
					break;
				case 5:
					radioGroup.setBackgroundResource(R.color.theme_bg_defualt);
					acionbarBgLayout
							.setBackgroundResource(R.color.theme_bg_titleBar);
					mainBg.setBackgroundResource(R.drawable.mainbg5);
					// 保存颜色
					saveThemeSet(COLOR_DEFUALT);
					break;
				}

			}
		};
	}

	private void showYourAvatar() {
		String avatarPath = user.getAvatar();
		setAvatar(avatarPath);
	}

	private void setAvatar(String avatarPath) {
		if (avatarPath != null && !avatarPath.equals("")) {
			ImageLoader.getInstance().displayImage(avatarPath, myAvartar,
					ImageLoadOptions.getOptions());
		} else {
			myAvartar.setImageResource(R.drawable.default_head);
		}
	}

	/**
	 * 将以前保存的主题用文件记下来，下次直接读取上次设置的颜色
	 */
	private void initColorSave() {
		File colorFile = new File(
				"data/data/com.zgrjb.find/files/colorSave.txt");
		if (colorFile.exists()) {
			int color = Integer.parseInt(serviceFlag
					.readContentFromFile("colorSave.txt"));
			switch (color) {
			case COLOR_BLACK:
				radioGroup.setBackgroundResource(R.color.theme_bg_black);
				acionbarBgLayout.setBackgroundResource(R.color.theme_bg_black);
				mainBg.setBackgroundResource(R.drawable.mainbg1);
				break;
			case COLOR_GREEN:
				radioGroup.setBackgroundResource(R.color.theme_bg_green);
				acionbarBgLayout.setBackgroundResource(R.color.theme_bg_green);
				mainBg.setBackgroundResource(R.drawable.mainbg2);
				break;
			case COLOR_BLUE:
				radioGroup.setBackgroundResource(R.color.theme_bg_blue);
				acionbarBgLayout.setBackgroundResource(R.color.theme_bg_blue);
				mainBg.setBackgroundResource(R.drawable.mainbg3);
				break;
			case COLOR_PINK:
				radioGroup.setBackgroundResource(R.color.theme_bg_pink);
				acionbarBgLayout.setBackgroundResource(R.color.theme_bg_pink);
				mainBg.setBackgroundResource(R.drawable.mainbg4);
				break;

			case COLOR_DEFUALT:
				radioGroup.setBackgroundResource(R.color.theme_bg_defualt);
				acionbarBgLayout
						.setBackgroundResource(R.color.theme_bg_titleBar);
				mainBg.setBackgroundResource(R.drawable.mainbg5);
				break;

			}
		}

	}

	/**
	 * 查询是当前的哪一个的用户
	 */
	private void queryCurrentUser() {

		BmobQuery<MyUser> query = new BmobQuery<MyUser>();
		query.addWhereEqualTo("objectId", userManager.getCurrentUserObjectId());
		query.findObjects(this, new FindListener<MyUser>() {

			@Override
			public void onSuccess(List<MyUser> arg0) {

				if (arg0.size() != 0) {
					Message message = new Message();
					message.obj = arg0.get(0);
					mHandler.sendMessage(message);
				}

			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});

	};

	@Override
	protected void onResume() {
		super.onResume();
		// 创建一个线程来查询用户
		new Thread() {
			public void run() {

				queryCurrentUser();
			}
		}.start();

		// 注册广播

		tv_nick.setText(userManager.getCurrentUser().getNick());

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("choiceIcon");
		registerReceiver(broadcastReceiver, intentFilter);

		// IntentFilter intentFilter2 = new IntentFilter();
		// intentFilter2.addAction("send");
		// registerReceiver(broadcastReceiver2, intentFilter2);

		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 清空
		MyMessageReceiver.mNewNum = 0;
		if (recentFragment != null) {

			recentFragment.refresh();
		}

	}

	// 保存颜色
	private void saveThemeSet(int whatColor) {

		serviceFlag.saveContentToFile("colorSave.txt", Context.MODE_PRIVATE,
				(whatColor + "").getBytes());
	}

	/**
	 * 对左侧的菜单设置点击事件
	 */
	@Override
	public void onClick(View v) {

		if (v == userSetLayout) {
			Intent intent = new Intent(MainUIActivity.this,
					LeftMenuPersonalData.class);
			startActivity(intent);
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		} else if (v == quitBt) {
			showLogoutBtDialog();
		} else if (v == messageSetLayout) {
			if (isOpen) {
				forbidRecieveMessage();
				serviceFlag.saveContentToFile("Message.txt",
						Context.MODE_PRIVATE, (1 + "").getBytes());
				isOpen = false;
			} else {
				recieveMessage();
				serviceFlag.saveContentToFile("Message.txt",
						Context.MODE_PRIVATE, (0 + "").getBytes());
				isOpen = true;
			}
		} else if (v == voiceSetLayout) {
			if (isVoiceOpen) {
				forbidRecieveVoice();
				serviceFlag.saveContentToFile("Voice.txt",
						Context.MODE_PRIVATE, (1 + "").getBytes());
				isVoiceOpen = false;
			} else {
				recieveVoice();
				serviceFlag.saveContentToFile("Voice.txt",
						Context.MODE_PRIVATE, (0 + "").getBytes());
				isVoiceOpen = true;
			}

		} else if (v == vibrateSetLayout) {
			if (isVibrateOpen) {
				forbidRecieveVibrate();
				serviceFlag.saveContentToFile("Vibrate.txt",
						Context.MODE_PRIVATE, (1 + "").getBytes());
				isVibrateOpen = false;
			} else {
				recieveVibrate();
				serviceFlag.saveContentToFile("Vibrate.txt",
						Context.MODE_PRIVATE, (0 + "").getBytes());
				isVibrateOpen = true;
			}
		}
	}

	/**
	 * 初始化菜单设置
	 */
	private void initMenuSet() {

		File messageFile = new File(
				"data/data/com.zgrjb.find/files/Message.txt");
		File voiceFile = new File("data/data/com.zgrjb.find/files/Voice.txt");
		File vibrateFile = new File(
				"data/data/com.zgrjb.find/files/Vibrate.txt");
		if (messageFile.exists()) {
			messageValue = Integer.parseInt(serviceFlag
					.readContentFromFile("Message.txt"));
		}
		if (voiceFile.exists()) {
			voiceValue = Integer.parseInt(serviceFlag
					.readContentFromFile("Voice.txt"));
		}
		if (vibrateFile.exists()) {
			vibrateValue = Integer.parseInt(serviceFlag
					.readContentFromFile("Vibrate.txt"));
		}

		if (messageValue == 1) {
			forbidRecieveMessage();
		} else {
			recieveMessage();
		}
		if (voiceValue == 1) {
			forbidRecieveVoice();
		} else {
			recieveVoice();
		}
		if (vibrateValue == 1) {
			forbidRecieveVibrate();
		} else {
			recieveVibrate();
		}
	}

	/**
	 * 禁止聊天消息提示
	 */
	private void forbidRecieveMessage() {
		messageSetTrue.setVisibility(View.INVISIBLE);
		messageSetFalse.setVisibility(View.VISIBLE);
		voiceSetLayout.setVisibility(View.GONE);
		vibrateSetLayout.setVisibility(View.GONE);
		viewVoice.setVisibility(View.GONE);
		viewVibrate.setVisibility(View.GONE);
		shPreferenceUtil.setPushNotifyEnable(false);
		isOpen = false;
	}

	/**
	 * 接受聊天消息
	 */
	private void recieveMessage() {
		messageSetTrue.setVisibility(View.VISIBLE);
		messageSetFalse.setVisibility(View.INVISIBLE);
		voiceSetLayout.setVisibility(View.VISIBLE);
		vibrateSetLayout.setVisibility(View.VISIBLE);
		viewVoice.setVisibility(View.VISIBLE);
		viewVibrate.setVisibility(View.VISIBLE);
		shPreferenceUtil.setPushNotifyEnable(true);
		isOpen = true;
	}

	/**
	 * 关闭聊天声音
	 */
	private void forbidRecieveVoice() {
		voiceSetTrue.setVisibility(View.INVISIBLE);
		voiceSetFalse.setVisibility(View.VISIBLE);
		shPreferenceUtil.setAllowVoiceEnable(false);
		isVoiceOpen = false;
	}

	/**
	 * 开启聊天声音
	 */
	private void recieveVoice() {
		voiceSetTrue.setVisibility(View.VISIBLE);
		voiceSetFalse.setVisibility(View.INVISIBLE);
		shPreferenceUtil.setAllowVoiceEnable(true);
		isVoiceOpen = true;
	}

	/**
	 * 关闭聊天震动
	 */
	private void forbidRecieveVibrate() {
		vibrateSetTrue.setVisibility(View.INVISIBLE);
		vibrateSetFalse.setVisibility(View.VISIBLE);
		shPreferenceUtil.setAllowVibrateEnable(false);
		isVibrateOpen = false;
	}

	/**
	 * 开启聊天震动
	 */
	private void recieveVibrate() {
		vibrateSetTrue.setVisibility(View.VISIBLE);
		vibrateSetFalse.setVisibility(View.INVISIBLE);
		shPreferenceUtil.setAllowVibrateEnable(true);
		isVibrateOpen = true;
	}

	/**
	 * 退出登录的dialog
	 */
	private void showLogoutBtDialog() {
		builder.setTitle("提示");
		builder.setMessage("确定重新登录吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				logOut();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});
		builder.show();

	}

	/**
	 * 退出登录
	 */
	private void logOut() {
		CustomApplcation.getInstance().logout();
		Intent intent = new Intent(MainUIActivity.this, LogInActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
		MainUIActivity.this.finish();
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
	}

	// 初始化PageItem
	private void initPageItem() {
		pagerItems = new ArrayList<MainUIActivity.PagerItem>();
		pagerItems.add(new PagerItem(
				(RadioButton) findViewById(R.id.bt_main_recent),
				new RecentFragment()));
		pagerItems.add(new PagerItem(
				(RadioButton) findViewById(R.id.bt_main_discover),
				new DiscoverFragment()));
		pagerItems.add(new PagerItem(
				(RadioButton) findViewById(R.id.bt_main_contact),
				new ContactsFragment()));
	}

	// 初始化RadioGroup
	private void initRadioGroup() {
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				for (int i = 0; i < pagerItems.size(); i++) {

					pagerItems.get(i).radioButton.setBackgroundColor(Color
							.alpha(0));
					if (pagerItems.get(i).radioButton.getId() == checkedId) {

						pagerItems.get(i).radioButton
								.setBackgroundColor(Color.BLACK);
						pagerItems.get(i).radioButton.getBackground().setAlpha(
								40);
						viewPager.setCurrentItem(i);
						switch (i) {
						case 0:
							showTitleText("消息");
							mLeftmenu.closeMore();
							break;
						case 1:
							showTitleText("发现");
							mLeftmenu.closeMore();
							break;
						case 2:
							showTitleText("通讯录");
							mLeftmenu.closeMore();
							break;
						}

					}
				}
			}
		});
	}

	// 初始化PageList
	public void initPageList() {
		viewPager = (ViewPager) findViewById(R.id.viewPage);
		adapter = new MainAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	/**
	 * 新消息广播接收者
	 * 
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 刷新界面
			refreshNewMsg(null);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	public void refreshNewMsg(BmobMsg message) {
		// 声音提示
		boolean isAllow = CustomApplcation.getInstance()
				.getSharePreferenceUtil().isAllowVoice();
		if (isAllow && message != null) {

			// play(R.raw.notify);
			mApplication.getMediaPlayer().start();

		}
		// 也要存储起来
		if (message != null) {
			BmobChatManager.getInstance(MainUIActivity.this)
					.saveReceiveMessage(true, message);
		}

		ShowLog(113);
		if (currentPage == 0) {
			// 当前页面如果为会话页面，刷新此页面
			if (recentFragment != null) {
				recentFragment.refresh();
			}
		}

	}

	/**
	 * 播放消息提示的声音
	 * 
	 * @param res
	 *            资源文件id
	 */
	private void play(int res) {

		mediaPlayer = MediaPlayer.create(MainUIActivity.this, res);
		mediaPlayer.start();
		if (!mediaPlayer.isPlaying()) {
			mediaPlayer.release();
		}

	}

	public class MainAdapter extends FragmentPagerAdapter {

		public MainAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int id) {
			switch (id) {

			case MainUIActivity.TAB_RECENT:
				recentFragment = new RecentFragment();
				return recentFragment;
			case MainUIActivity.TAB_FIND:
				DiscoverFragment discoverFragment = new DiscoverFragment();
				discoverFragment.getView();
				return discoverFragment;
			case MainUIActivity.TAB_CONTACT:
				ContactsFragment contactsFragment = new ContactsFragment();
				return contactsFragment;
			}

			return null;
		}

		@Override
		public int getCount() {
			return pagerItems.size();

		}

	}

	private static long firstTime;

	/**
	 * 连续按两次返回键就退出
	 */
	@Override
	public void onBackPressed() {
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();

			overridePendingTransition(R.anim.quit_zoom_enter,
					R.anim.quit_zoom_exit);
		} else {
			ShowToast("再按一次退出程序");
		}
		firstTime = System.currentTimeMillis();

	}

	public class PagerItem {
		public RadioButton radioButton;
		public android.support.v4.app.Fragment fragment;

		public PagerItem(RadioButton radioButton,
				android.support.v4.app.Fragment fragment) {
			this.radioButton = radioButton;
			this.fragment = fragment;
		}

	}

	@Override
	public void onAddUser(BmobInvitation arg0) {

	}

	@Override
	public void onOffline() {

	}

	@Override
	public void onReaded(String arg0, String arg1) {
		refreshNewMsg(null);
	}

	@Override
	public void onMessage(BmobMsg message) {
		refreshNewMsg(message);
	}

	@Override
	public void onNetChange(boolean net) {
		if (net) {
			ShowToast("请检查网络");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}

}
