package com.zgrjb.find.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.im.bean.BmobMsg;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.ui.dialog.MyDialog;
import com.zgrjb.find.ui.fragment.ContactsFragment;
import com.zgrjb.find.utils.CustomApplcation;
import com.zgrjb.find.utils.ImageLoadOptions;

public class FriendsDataActivity extends BaseActivity {
	private ImageView friendsAvartarImageView;
	private TextView friendNickNameTextView;
	private TextView friendAgeTextView;
	private TextView friendSexTextView;
	private Button btDelete;
	private Button btChat;

	private MyDialog dialog;

	private MyUser mUser;
	private boolean isTrue;

	// MyUser friend;
	String targetId;
	MyUser targetUser;

	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			targetUser = (MyUser) msg.obj;

			setFriendInfo(targetUser); // 设置好友资料

		};
	};

	protected void onResume() {
		super.onResume();

		new Thread() {
			public void run() {

				queryTargetFrind();
			}
		}.start();

	};

	private void queryTargetFrind() {

		BmobQuery<MyUser> query = new BmobQuery<MyUser>();
		query.addWhereEqualTo("objectId", targetId);
		query.findObjects(this, new FindListener<MyUser>() {

			@Override
			public void onSuccess(List<MyUser> arg0) {

				if (arg0.size() != 0) {
					Message message = new Message();
					message.obj = arg0.get(0);
					mHandler.sendMessage(message);

					//
				}

			}

			@Override
			public void onError(int arg0, String arg1) {

			}
		});

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_dada);
		getUserIntent();
		showTitleText("好友资料");
		init();
		if (isTrue) {
			btChat.setVisibility(View.GONE);
		}else{
			btChat.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 点击好友头像放大显示
	 * 
	 * @param v
	 */
	public void circleImageViewLis(View v) {
		sendPictureToObserve(mUser.getAvatar());

	}

	// 将图片传递到ObservePictureAcitivity放大显示
	private void sendPictureToObserve(String url) {
		Intent intent = new Intent(FriendsDataActivity.this,
				ObservePictureAcitivity.class);
		ArrayList<String> photos = new ArrayList<String>();
		photos.add(url);
		intent.putStringArrayListExtra("photos", photos);
		intent.putExtra("position", 0);
		FriendsDataActivity.this.startActivity(intent);

	}

	protected void setFriendInfo(MyUser user) {
		mUser = user;
		if (!TextUtils.isEmpty(user.getAvatar())) {
			ImageLoader.getInstance().displayImage(user.getAvatar(),
					friendsAvartarImageView, ImageLoadOptions.getOptions());
		} else {
			friendsAvartarImageView.setImageResource(R.drawable.child);
		}

		friendNickNameTextView.setText(user.getNick());
		friendAgeTextView.setText(String.valueOf(user.getAge()));
		setFriendSex(user.getSex());
	}

	/*
	 * @SuppressLint("NewApi") private void setFriendInfo() {
	 * 
	 * if (!TextUtils.isEmpty(friend.getAvatar())) {
	 * ImageLoader.getInstance().displayImage(friend.getAvatar(),
	 * friendsAvartarImageView, ImageLoadOptions.getOptions()); } else {
	 * friendsAvartarImageView.setImageResource(R.drawable.child); }
	 * 
	 * friendNickNameTextView.setText(friend.getNick());
	 * friendAgeTextView.setText(String.valueOf(friend.getAge()));
	 * setFriendSex(friend.getSex());
	 * 
	 * }
	 */

	private void setFriendSex(boolean sex) {
		if (sex) {
			friendSexTextView.setText("男");
		} else {
			friendSexTextView.setText("女");

		}

	}

	private void getUserIntent() {
		Intent intent = getIntent();
		// friend = (MyUser) getIntent().getSerializableExtra("user");
		targetId = ((MyUser) getIntent().getSerializableExtra("user"))
				.getObjectId();
		
		isTrue = intent.getBooleanExtra("isChat", false);
	//	System.err.println(isTrue+">>>>>>>>>>>>>>");
		

	}

	private void init() {
		friendsAvartarImageView = (ImageView) this
				.findViewById(R.id.friendAvertarImageView);
		friendNickNameTextView = (TextView) this
				.findViewById(R.id.friendNickNameTextView);
		friendAgeTextView = (TextView) this
				.findViewById(R.id.friendAgeTextView);
		friendSexTextView = (TextView) this
				.findViewById(R.id.friendSexTextView);
		dialog = new MyDialog(this);
		btDelete = (Button) this.findViewById(R.id.delete_friend_bt);
		btChat = (Button) findViewById(R.id.bt_start_talking);
		btDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteDialog();
			}
		});

		btChat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				chatUser();

			}
		});

	}

	protected void chatUser() {
		MyUser user = new MyUser();
		user.setAvatar(targetUser.getAvatar());
		user.setNick(targetUser.getNick());
		user.setUsername(targetUser.getUsername());
		user.setObjectId(targetUser.getObjectId());
		Intent intent = new Intent(FriendsDataActivity.this, ChatActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	}

	/**
	 * 删除好友有待解决
	 */

	private void deleteDialog() {

		final AlertDialog.Builder dialog = new Builder(FriendsDataActivity.this);
		dialog.setMessage("删除好友" + targetUser.getNick());
		dialog.setTitle("提示");

		dialog.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		dialog.setPositiveButton("确定",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteUser();

					}
				});
		dialog.create().show();

	}

	protected void deleteUser() {

		final ProgressDialog progress = new ProgressDialog(
				FriendsDataActivity.this);
		progress.setMessage("正在删除...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		userManager.deleteContact(targetUser.getObjectId(),
				new UpdateListener() {

					@Override
					public void onSuccess() {
						System.out.println("success");
						// TODO Auto-generated method stub
						ShowToast("删除成功");
						// 删除内存
						CustomApplcation.getInstance().getContactList()
								.remove(targetUser.getUsername());
						// 取消进度条
						progress.dismiss();

						finish();

					}

					@Override
					public void onFailure(int arg0, String arg1) {
						System.out.println("fail");
						// TODO Auto-generated method stub
						ShowToast("删除失败");
						progress.dismiss();
						finish();
					}
				});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
	
	}

}
