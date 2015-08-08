package com.zgrjb.find.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import com.zgrjb.find.R;
import com.zgrjb.find.adapter.ChartAdapter;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.config.ImgUir;
import com.zgrjb.find.file_handle.HandlePicFile;
import com.zgrjb.find.utils.CollectionUtils;
import com.zgrjb.find.utils.CommonUtils;
import com.zgrjb.find.utils.CustomApplcation;
import com.zgrjb.find.utils.MyMessageReceiver;
import com.zgrjb.find.view.EmoticonsEditText;
import com.zgrjb.find.view.xlist.XListView;
import com.zgrjb.find.view.xlist.XListView.IXListViewListener;

public class ChatActivity extends BaseActivity implements OnClickListener,
		IXListViewListener, EventListener {
	private ListView mMsgs;
	private ChartAdapter mAdapter;
	private List<BmobMsg> mDatas;

	private XListView mListView;

	// private NewBroadcastReceiver receiver; // 新消息接收器
	private MyUser targetUser; // 当前对话的用户
	private String targetId;
	private String targetUserName;
	private int MsgPagerNum;
	private String currentUserId;
	private String currentUserName;
	private int hashCode;

	private Button btn_chat_add, btn_chat_keyboard, btn_chat_voice,
			btn_chat_send, btn_speak;
	private LinearLayout layout_more, layout_add;

	private RelativeLayout layout_record;
	private TextView tv_voice_tips;
	private ImageView iv_record;
	private BmobRecordManager recordManager;
	private int time;

	private EmoticonsEditText edit_user_comment;

	public static final int NEW_MESSAGE = 0x001;// 收到消息
	public static final int CLICK_UP = 0x002;// 抬起手指
	private int a = 0;// 手指是否抬起

	private boolean isOpen;// 判断加号菜单是否打开

	private ImageView imgPhoto, imgGetAlbum, imgMyPaint;

	private Drawable[] drawable_Anims;// 话筒动画

	private BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		init();

		showTitleText("与" + targetUser.getNick() + "聊天中");
	}

	private void addFriend() {

		List<BmobChatUser> contactList = BmobDB.create(this, currentUserId)
				.getContactList();
		for (BmobChatUser bmobChatUser : contactList) {
			if (bmobChatUser.getUsername().equals(targetUserName)) {
				System.out.println("他是你好友"); // 当前这个对象是这个联系人的好友
				return;
			}
		}
		BmobUserManager.getInstance(this).addContactAfterAgree(targetUserName,
				new FindListener<BmobChatUser>() {

					@Override
					public void onError(int arg0, final String arg1) {
						System.out.println("加他好友失败");

					}

					@Override
					public void onSuccess(List<BmobChatUser> arg0) {
						System.out.println("添加新朋友");
						// 保存到内存中
						CustomApplcation.getInstance().setContactList(
								CollectionUtils.list2map(BmobDB.create(
										ChatActivity.this).getContactList()));
						String txt = currentUserName + "加你为好友了，很高兴认识你";

						sendTxt(txt, 1);

					}
				});

	}

	private void init() {

		targetUser = (MyUser) getIntent().getSerializableExtra("user");
		targetId = targetUser.getObjectId();
		targetUserName = targetUser.getUsername();
		MsgPagerNum = 0;
		currentUserId = BmobChatUser.getCurrentUser(ChatActivity.this)
				.getObjectId();
		currentUserName = BmobChatUser.getCurrentUser(ChatActivity.this)
				.getUsername();

		initBottomView();
		initXListView();
		initVoiceView();
		getBroadcastFromPaintActivity();
	}

	/**
	 * 初始化语音布局
	 * 
	 * @Title: initVoiceView
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initVoiceView() {
		layout_record = (RelativeLayout) findViewById(R.id.layout_record);
		tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
		iv_record = (ImageView) findViewById(R.id.iv_record);
		initRecordManager();
		initVoiceAnimRes();
		btn_speak.setOnTouchListener(new VoiceTouchListen());
	}

	/**
	 * 初始化录音
	 */
	private void initRecordManager() {
		// 语音相关管理器
		recordManager = BmobRecordManager.getInstance(this);
		// 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
		recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

			@Override
			public void onVolumnChanged(int value) {

				iv_record.setImageDrawable(drawable_Anims[value]);

			}

			@Override
			public void onTimeChanged(final int recordTime,
					final String localPath) {
				time = recordTime;

				BmobLog.i("voice", "已录音长度:" + recordTime);
				if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
					// 需要重置按钮
					btn_speak.setPressed(false);
					btn_speak.setClickable(false);
					// 取消录音框
					layout_record.setVisibility(View.INVISIBLE);
					// 先将语音消息发送出去，再将语音管理器关闭掉
					handler.post(new Runnable() {

						@Override
						public void run() {
							sendVoiceMessage(localPath, recordTime);
						}

					});

					handler.post(new Runnable() {

						@Override
						public void run() {
							recordManager.cancelRecording();
						}
					});
				}
			}
		});

	}

	/**
	 * 发送一个声音消息
	 * 
	 * @param Path
	 * @param length
	 */
	private void sendVoiceMessage(String Path, int length) {

		chatManager.sendVoiceMessage(targetUser, Path, length,
				new UploadListener() {

					@Override
					public void onSuccess() {
						mAdapter.notifyDataSetChanged();

					}

					@Override
					public void onStart(BmobMsg message) {
						mDatas.add(message);
						mListView.setSelection(mAdapter.getCount() - 1);
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						mAdapter.notifyDataSetChanged();
						ShowLog("上传语音失败 -->arg1：" + arg1);
					}
				});

	}

	class VoiceTouchListen implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:// 按下手指

				if (!CommonUtils.checkSdCard()) {
					ShowToast("发送语音需要sdcard支持！");
					return false;    
				}
				try {
					if (shorToast != null) {
						shorToast.cancel();
					}

					v.setPressed(true);
					layout_record.setVisibility(View.VISIBLE);
					tv_voice_tips.setText("松开手指 \n取消发送");
					// 开始录音
					recordManager.startRecording(targetId);
				} catch (Exception e) {
				}

				break;
			case MotionEvent.ACTION_MOVE: {// 移动手指
				if (event.getY() < 0) { // 手指超过当前view时y坐标即变为负数
					tv_voice_tips.setText("松开手指 取消发送");
					tv_voice_tips.setTextColor(Color.RED);
				} else {
					tv_voice_tips.setText("手指上滑 取消发送");
					tv_voice_tips.setTextColor(Color.WHITE);
				}

			}
				break;
			case MotionEvent.ACTION_UP:

				a = 1;
				v.setPressed(false);
				layout_record.setVisibility(View.GONE);

				if (event.getY() < 0) {
					recordManager.cancelRecording();
					BmobLog.i("voice", "放弃发送语音");
					break;
				} else {
					if (recordManager.isRecording()) {
						int recordTime = recordManager.stopRecording(); // 录音的长度

						if (recordTime <= 1) {
							// 录音时间过短

							showShortTip();
						} else {
							ShowLog(1);
							sendVoiceMessage(
									recordManager.getRecordFilePath(targetId),
									recordTime);
						}
					}

				}

				break;
			default:
				break;
			}
			return true;

		}

	}

	private void initXListView() {
		mListView = (XListView) findViewById(R.id.mListView);

		// 首先不允许加载更多
		mListView.setPullLoadEnable(false);
		// 允许下拉
		mListView.setPullRefreshEnable(true);
		// 设置监听器
		mListView.setXListViewListener(this);
		mListView.pullRefreshing();
		mListView.setDividerHeight(0);
		// 加载数据

		initAdapter();
		initHistory();
		mListView.setSelection(mAdapter.getCount() - 1);

	}

	private void initAdapter() {
		mDatas = new ArrayList<BmobMsg>();
		mAdapter = new ChartAdapter(this, mDatas, targetUser);
		mListView.setAdapter(mAdapter);

	}

	private void initHistory() {
		List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,
				MsgPagerNum);
		mDatas.addAll(list);
		mListView.setSelection(mDatas.size() - 1);
		mAdapter.notifyDataSetChanged();
	}

	Toast shorToast;

	public void showShortTip() {

		if (shorToast == null)
			shorToast = new Toast(ChatActivity.this);

		View view = LayoutInflater.from(this).inflate(
				R.layout.include_chat_voice_short, null);
		shorToast.setGravity(Gravity.CENTER, 0, 0);
		shorToast.setDuration(50);
		shorToast.setView(view);
		shorToast.show();

	}

	private void Refresh() {
		if (mAdapter != null) {
			if (MyMessageReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
				int news = MyMessageReceiver.mNewNum;// 有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上

				int size = initMsgData().size();
				for (int i = (news - 1); i >= 0; i--) {
					mDatas.add(initMsgData().get(size - (i + 1)));
					// // 添加最后一条消息到界面显示
				}
				mListView.setSelection(mAdapter.getCount() - 1);
			} else {
				System.out.println("加载12");
				mAdapter.notifyDataSetChanged();
			}
		} else {
			System.out.println("加载2");
			mDatas = new ArrayList<BmobMsg>();
			mAdapter = new ChartAdapter(this, mDatas, targetUser);
			mListView.setAdapter(mAdapter);
			// mAdapter = new ChatAdapter(this, initMsgData());
			// mListView.setAdapter(mAdapter);
		}

	}

	private List<BmobMsg> initMsgData() {
		List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,
				MsgPagerNum);
		return list;
	}

	/**
	 * 隐藏软键盘
	 */

	protected void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) this
				.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}

	/**
	 * 初始化底下的按钮控件
	 */
	private void initBottomView() {

		// 最左边
		btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
		btn_chat_add.setOnClickListener(this);
		// 最右边
		btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
		btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
		btn_chat_voice.setOnClickListener(this);
		btn_chat_keyboard.setOnClickListener(this);
		btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
		btn_chat_send.setOnClickListener(this);
		imgPhoto = (ImageView) this.findViewById(R.id.img_photo);
		imgGetAlbum = (ImageView) this.findViewById(R.id.img_getalbum);
		imgMyPaint = (ImageView) this.findViewById(R.id.img_paint);
		imgPhoto.setOnClickListener(this);
		imgGetAlbum.setOnClickListener(this);
		imgMyPaint.setOnClickListener(this);

		// 语音框
		btn_speak = (Button) findViewById(R.id.btn_speak);
		// 输入框
		edit_user_comment = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
		edit_user_comment.setOnClickListener(this);
		edit_user_comment.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(s)) {
					btn_chat_send.setVisibility(View.VISIBLE);
					btn_chat_keyboard.setVisibility(View.GONE);
					btn_chat_voice.setVisibility(View.GONE);
					btn_chat_add.setClickable(false);
					btn_chat_add.setFocusable(false);
					btn_chat_add.setAlpha(0.2f);
				} else {
					if (btn_chat_voice.getVisibility() != View.VISIBLE) {
						btn_chat_voice.setVisibility(View.VISIBLE);
						btn_chat_send.setVisibility(View.GONE);
						btn_chat_keyboard.setVisibility(View.GONE);
						btn_chat_add.setClickable(true);
						btn_chat_add.setFocusable(true);
						btn_chat_add.setAlpha(1.0f);
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});

	}

	/**
	 * 按下按钮的监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_user_comment:// 点击文本输入框
			mListView.setSelection(mListView.getCount() - 1);
			break;

		case R.id.btn_chat_add:// 添加按钮-显示图片、拍照
			menuToggle(v);
			hideSoftInputView();
			break;
		case R.id.btn_chat_voice:// 语音按钮
			edit_user_comment.setVisibility(View.GONE);
			btn_chat_voice.setVisibility(View.GONE);
			btn_chat_keyboard.setVisibility(View.VISIBLE);
			btn_speak.setVisibility(View.VISIBLE);
			btn_chat_add.setClickable(false);
			btn_chat_add.setFocusable(false);
			btn_chat_add.setAlpha(0.2f);
			hideSoftInputView();
			break;

		case R.id.btn_chat_keyboard:// 键盘按钮，点击就弹出键盘并隐藏掉声音按钮
			// showEditState(false);
			btn_chat_voice.setVisibility(View.VISIBLE);
			btn_chat_keyboard.setVisibility(View.GONE);
			btn_speak.setVisibility(View.GONE);
			edit_user_comment.setVisibility(View.VISIBLE);
			btn_chat_add.setVisibility(View.VISIBLE);
			btn_chat_add.setClickable(true);
			btn_chat_add.setFocusable(true);
			btn_chat_add.setAlpha(1.0f);
			break;
		case R.id.btn_chat_send:// 发送文本
			sendTxt(edit_user_comment.getText().toString(), 0);
			break;
		case R.id.img_photo:// 发送拍照的照片

			imgPhotoToAni();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					takePhoto();
					overridePendingTransition(R.anim.zoom_enter,
							R.anim.zoom_exit);
					edit_user_comment.setVisibility(View.VISIBLE);
					btn_chat_voice.setVisibility(View.VISIBLE);
					isOpen = false;
				}
			}, 500);
			break;
		case R.id.img_getalbum:// 发送相册里的照片
			imgGetAlbumAni();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					getPictureInAlbum();
					overridePendingTransition(R.anim.zoom_enter,
							R.anim.zoom_exit);
					edit_user_comment.setVisibility(View.VISIBLE);
					btn_chat_voice.setVisibility(View.VISIBLE);
					isOpen = false;
				}
			}, 500);
			break;
		case R.id.img_paint:// 发送
			imgPaintAni();

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					startActivity(new Intent(ChatActivity.this,
							MyPaintActivity.class));
					overridePendingTransition(R.anim.zoom_enter,
							R.anim.zoom_exit);
					edit_user_comment.setVisibility(View.VISIBLE);
					btn_chat_voice.setVisibility(View.VISIBLE);
					isOpen = false;
				}
			}, 500);
			break;
		}
	}

	private static final int REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int REQUESTCODE_TAKE_LOCAL = 2; // 从相册里获取
	private HandlePicFile handleFile;
	private String path = new String();

	/**
	 * 从相册里选取照片
	 */
	protected void getPictureInAlbum() {

		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUESTCODE_TAKE_LOCAL);

	}

	/**
	 * 通过照相选取相片
	 */
	protected void takePhoto() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// handleFile = new HandleFile(ChatActivity.this, ImgUir.ALBUM_PATH);
		File dir = new File(ImgUir.ALBUM_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(ImgUir.ALBUM_PATH
				+ String.valueOf(System.currentTimeMillis()) + ".jpg");
		path = file.getPath();
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

		startActivityForResult(cameraIntent, REQUEST_TAKEPHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_TAKEPHOTO:
				edit_user_comment.setVisibility(View.VISIBLE);
				btn_chat_voice.setVisibility(View.VISIBLE);
				sendImagePicture(path);

				break;
			case REQUESTCODE_TAKE_LOCAL:
				edit_user_comment.setVisibility(View.VISIBLE);
				btn_chat_voice.setVisibility(View.VISIBLE);
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(
								selectedImage, null, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex("_data");
						String localSelectPath = cursor.getString(columnIndex);
						cursor.close();
						sendImagePicture(localSelectPath);
					}
				}
				break;

			}
		}

	}

	/**
	 * 将发送的照片上传到服务器，并通过第三方jar包存在本地
	 * 
	 * @param path
	 */
	private void sendImagePicture(String path) {

		chatManager.sendImageMessage(targetUser, path, new UploadListener() {

			@Override
			public void onStart(BmobMsg msg) {
				mDatas.add(msg);
				mListView.setSelection(mAdapter.getCount() - 1);
			}

			@Override
			public void onSuccess() {
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				mAdapter.notifyDataSetChanged();
				ShowToast("上传失败");

			}
		});
	}

	/**
	 * 设置不能被点击，选中。
	 */
	private void setClickAndFocusFalse() {
		imgPhoto.setClickable(false);
		imgGetAlbum.setClickable(false);
		imgMyPaint.setClickable(false);
		imgPhoto.setFocusable(false);
		imgGetAlbum.setFocusable(false);
		imgMyPaint.setFocusable(false);
	}

	/**
	 * 设置能被点击，选中。
	 */
	private void setClickAndFocusTrue() {
		imgPhoto.setClickable(true);
		imgGetAlbum.setClickable(true);
		imgMyPaint.setClickable(true);
		imgPhoto.setFocusable(true);
		imgGetAlbum.setFocusable(true);
		imgMyPaint.setFocusable(true);
	}

	/**
	 * 切换菜单的设置
	 * 
	 * @param v
	 */
	private void menuToggle(View v) {
		if (!isOpen) {
			edit_user_comment.setVisibility(View.GONE);
			btn_chat_voice.setVisibility(View.GONE);
			imgPhoto.setVisibility(View.VISIBLE);
			imgGetAlbum.setVisibility(View.VISIBLE);
			imgMyPaint.setVisibility(View.VISIBLE);
			rotateBtAdd(v, 0f, 360f, 300);
			scaleBigAnim(500, imgPhoto);
			scaleBigAnim(500, imgGetAlbum);
			scaleBigAnim(500, imgMyPaint);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					setClickAndFocusTrue();

				}
			}, 500);
			setClickAndFocusFalse();

			isOpen = true;
		} else {
			edit_user_comment.setVisibility(View.VISIBLE);
			btn_chat_voice.setVisibility(View.VISIBLE);
			imgPhoto.setVisibility(View.GONE);
			imgGetAlbum.setVisibility(View.GONE);
			imgMyPaint.setVisibility(View.GONE);
			rotateBtAdd(v, 0f, 360f, 300);
			scaleSmallAnim(500, imgPhoto);
			scaleSmallAnim(500, imgGetAlbum);
			scaleSmallAnim(500, imgMyPaint);
			setClickAndFocusFalse();
			isOpen = false;

		}

	}

	/*
	 * 为当前点击的Item设置变大或变小和透明度的变化
	 */
	private void scaleSmallAnim(int duration, View v) {
		AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);
		animSet.addAnimation(scaleAnim);
		animSet.addAnimation(alphaAnim);
		animSet.setFillAfter(true);
		animSet.setDuration(duration);
		v.startAnimation(animSet);
	}

	private void scaleBigAnim(int duration, View v) {
		AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scaleAnim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
		animSet.addAnimation(scaleAnim);
		animSet.addAnimation(alphaAnim);
		animSet.setFillAfter(true);
		animSet.setDuration(duration);
		v.startAnimation(animSet);
	}

	private void SelectItemScaleBig(int duration, View v) {
		AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);
		animSet.addAnimation(scaleAnim);
		animSet.addAnimation(alphaAnim);
		animSet.setFillAfter(true);
		animSet.setDuration(duration);
		v.startAnimation(animSet);
		v.setClickable(false);
		v.setFocusable(false);
	}

	private void SelectItemScaleSmall(int duration, View v) {
		AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);
		animSet.addAnimation(scaleAnim);
		animSet.addAnimation(alphaAnim);
		animSet.setFillAfter(true);
		animSet.setDuration(duration);
		v.startAnimation(animSet);
		v.setClickable(false);
		v.setFocusable(false);
	}

	/**
	 * 点击照相按钮的动作
	 */
	private void imgPhotoToAni() {

		SelectItemScaleBig(500, imgPhoto);
		SelectItemScaleSmall(500, imgGetAlbum);
		SelectItemScaleSmall(500, imgMyPaint);
		setClickAndFocusFalse();

	}

	/**
	 * 点击从相册获取的按钮的动作
	 */
	private void imgGetAlbumAni() {
		SelectItemScaleBig(500, imgGetAlbum);
		SelectItemScaleSmall(500, imgPhoto);
		SelectItemScaleSmall(500, imgMyPaint);
		setClickAndFocusFalse();
	}

	/**
	 * 点击从相册获取的按钮的动作
	 */
	private void imgPaintAni() {
		SelectItemScaleBig(500, imgMyPaint);
		SelectItemScaleSmall(500, imgPhoto);
		SelectItemScaleSmall(500, imgGetAlbum);
		setClickAndFocusFalse();
	}

	/**
	 * 这只加号按钮的动作
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
	private void rotateBtAdd(View v, float start, float end, int duration) {
		RotateAnimation ani = new RotateAnimation(start, end,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ani.setDuration(duration);
		ani.setFillAfter(true);// 转完之后就停止
		v.startAnimation(ani);
	}

	private void sendTxt(String msg, int i) {

		if (msg.equals("")) {
			ShowToast("请输入发送消息!");
			return;
		}
		boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		if (!isNetConnected) {
			ShowToast("没有网络连接");
			// return;
		}
		// 组装BmobMessage对象
		BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);

		message.setExtra("Bmob");
		// 默认发送完成，将数据保存到本地消息表和最近会话表中

		chatManager.sendTextMessage(targetUser, message);
		// 刷新界面

		if (i == 1) {
			refreshMessage1(message);
			return;
		} else {
			refreshMessage(message);
		}

	}

	/**
	 * 添加好友成功时显示此消息
	 * 
	 * @param message
	 */
	private void refreshMessage1(BmobMsg message) {

		message.setContent("您已添加他为好友，现在可以开始聊天了");
		mDatas.add(message);
		mListView.setSelection(mAdapter.getCount() - 1);

	}

	/**
	 * 
	 * 
	 * @Title: showEditState
	 * @Description: TODO
	 * @param @param isEmo: 用于区分文字和表情
	 * @return void
	 * @throws
	 */
	@SuppressWarnings("unused")
	private void showEditState(boolean isEmo) {
		edit_user_comment.setVisibility(View.VISIBLE);
		btn_chat_keyboard.setVisibility(View.GONE);
		btn_chat_voice.setVisibility(View.VISIBLE);
		btn_speak.setVisibility(View.GONE);
		edit_user_comment.requestFocus();
		if (isEmo) {
			layout_more.setVisibility(View.VISIBLE);

			hideSoftInputView();
		} else {
			layout_more.setVisibility(View.GONE);
			showSoftInputView();
		}
	}

	private void showSoftInputView() {
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.showSoftInput(edit_user_comment, 0);
		}

	}

	private void refreshMessage(BmobMsg message) {

		mDatas.add(message);
		mListView.setSelection(mAdapter.getCount() - 1);
		edit_user_comment.setText("");

	}

	@Override
	public void onAddUser(BmobInvitation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(BmobMsg msg) {
		System.out.println("pass onMessage" + msg);
		Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
		handlerMsg.obj = msg;
		handler.sendMessage(handlerMsg);

	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOffline() {

	}

	@Override
	public void onReaded(String conversionId, String msgTime) {
		if (conversionId.split("&")[1].equals(targetId)) {
			// 修改界面上指定消息的阅读状态
			for (BmobMsg msg : mDatas) {
				if (msg.getConversationId().equals(conversionId)
						&& msg.getMsgTime().equals(msgTime)) {
					msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);

				}
				mAdapter.notifyDataSetChanged();
			}
		}

	}

	@Override
	public void onRefresh() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				MsgPagerNum++;
				int total = BmobDB.create(ChatActivity.this)
						.queryChatTotalCount(targetId);
				BmobLog.i("记录总数：" + total);
				int currents = mAdapter.getCount();
				if (total <= currents) {
					ShowToast("聊天记录加载完了哦!");
				} else {
					List<BmobMsg> msgList = initMsgData();

					mDatas.clear(); // 必须清空掉，否则会重复
					mDatas.addAll(msgList);

					mListView.setSelection(mAdapter.getCount() - currents - 1);
					mAdapter.notifyDataSetChanged();
				}
				mListView.stopRefresh();
			}
		}, 1000);

	}

	@Override
	public void onLoadMore() {

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == NEW_MESSAGE) {
				BmobMsg message = (BmobMsg) msg.obj;
				String uid = message.getBelongId();
				// BmobMsg m = BmobChatManager.getInstance(ChatActivity.this)
				// .getMessage(message.getConversationId(),
				// message.getMsgTime());
				if (!uid.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
					return;
				mDatas.add(message);
				// 定位
				mListView.setSelection(mAdapter.getCount() - 1);
				mAdapter.notifyDataSetChanged();
				// 取消当前聊天对象的未读标示
				BmobDB.create(ChatActivity.this).resetUnread(targetId);
			}
		}
	};

	// 初始声音动画
	private void initVoiceAnimRes() {
		drawable_Anims = new Drawable[] {
				getResources().getDrawable(R.drawable.chat_icon_voice2),
				getResources().getDrawable(R.drawable.chat_icon_voice3),
				getResources().getDrawable(R.drawable.chat_icon_voice4),
				getResources().getDrawable(R.drawable.chat_icon_voice5),
				getResources().getDrawable(R.drawable.chat_icon_voice6) };
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		addFriend();

		// 新消息到达，重新刷新界面
		Refresh();
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
		BmobNotifyManager.getInstance(this).cancelNotify();
		BmobDB.create(this).resetUnread(targetId);
		// 清空消息未读数-这个要在刷新之后
		MyMessageReceiver.mNewNum = 0;

		// 注册广播

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("sendPaint");
		registerReceiver(broadcastReceiver, intentFilter);

	}

	private void getBroadcastFromPaintActivity() {
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String s = intent.getStringExtra("date");
				Log.i("TAG", Environment.getExternalStorageDirectory()
						+ "/Find/Picture_Regist/" + s + ".jpg");

				sendImagePicture(Environment.getExternalStorageDirectory()
						+ "/Find/Picture_Regist/" + s + ".jpg");

			}
		};
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
	}
}
