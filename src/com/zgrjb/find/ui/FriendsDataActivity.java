package com.zgrjb.find.ui;

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

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.ui.dialog.MyDialog;
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
	}

	protected void setFriendInfo(MyUser user) {

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

		// friend = (MyUser) getIntent().getSerializableExtra("user");
		targetId = ((MyUser) getIntent().getSerializableExtra("user"))
				.getObjectId();

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

	// private void deleteUser(final MyUser user) {
	// final ProgressDialog progress = new ProgressDialog(this);
	// progress.setMessage("正在删除...");
	// progress.setCanceledOnTouchOutside(false);
	// progress.show();
	// userManager.deleteContact(user.getObjectId(), new UpdateListener() {
	//
	// @Override
	// public void onSuccess() {
	// System.out.println("success");
	// // TODO Auto-generated method stub
	// Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT)
	// .show();
	// // 删除内存
	// CustomApplcation.getInstance().getContactList()
	// .remove(user.getUsername());
	// // 更新界面
	// runOnUiThread(new Runnable() {
	// public void run() {
	// progress.dismiss();
	// list.remove(user);
	// list_friends.setSelection(list.size() - 1);
	// contactAdapter.notifyDataSetChanged();
	//
	// }
	// });
	// }
	//
	// @Override
	// public void onFailure(int arg0, String arg1) {
	// System.out.println("fail");
	// // TODO Auto-generated method stub
	// Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT)
	// .show();
	// progress.dismiss();
	// }
	// });
	//
	// }

}
