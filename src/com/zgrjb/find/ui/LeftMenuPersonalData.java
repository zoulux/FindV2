package com.zgrjb.find.ui;

import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.Contacts.AggregationSuggestions;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.utils.ImageLoadOptions;

public class LeftMenuPersonalData extends BaseActivity implements
		OnClickListener {
	// 定义一个个人头像的layout
	private LinearLayout personalImageLayout;
	// 定义一个个人昵称的layout
	private LinearLayout nickNameLayout;
	// 定义一个个人年您的layout
	private LinearLayout ageLayout;
	// 定义一个个人性别的layout
	private LinearLayout sexLayout;
	// 定义头像位图
	private Bitmap avertarBitmap;
	// 定义一个个人头像的ImageView，可修改
	private ImageView personalAvertarImageview;
	// 定义一个个人昵称的TextView，可修改
	private TextView personalNickNameTextView;
	// 定义一个个人年您的TextView，可修改
	private TextView personalAgeTextView;
	// 定义一个个人性别的TextView，可修改
	private TextView personalSexTextView;
	// 修改昵称的时候所出现的editText
	private EditText editText;
	// 初始化年龄为0
	private String ageWho = "0";
	// 初始化性别为0
	private int sexWho = 0;

	private String[] sexString = new String[2];
	private String[] ageString = new String[101];

	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			MyUser user = (MyUser) msg.obj;
			setUserInfo(user);

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_left_menu_personal_data);

		showTitleText("个人资料");
		init();

	}

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

					//
				}

			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});

	};

	private void setUserInfo(MyUser user) {

		// `修改头像
		String avatarPath = user.getAvatar();
		setAvatar(avatarPath);

		// 修改昵称
		String nick = user.getNick();
		personalNickNameTextView.setText(nick);

		// 设置年龄
		int age = user.getAge();
		personalAgeTextView.setText(String.valueOf(age));
		// 性别
		boolean sex = user.getSex();
		setSex(sex);
	}

	private void setSex(boolean sex) {
		if (sex) {
			personalSexTextView.setText("男");
		} else {
			personalSexTextView.setText("女");

		}

	}

	// 异步加载头像
	private void setAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar,
					personalAvertarImageview, ImageLoadOptions.getOptions());
		} else {
			personalAvertarImageview.setImageResource(R.drawable.default_head);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		new Thread() {
			public void run() {

				queryCurrentUser();
			}
		}.start();

		// requestFromServer(); // 向服务器请求数据

	}

	/**
	 * 初始化广播，id，和监听等
	 */
	private void init() {

		personalImageLayout = (LinearLayout) this
				.findViewById(R.id.id_linerlayout_personal_image);
		nickNameLayout = (LinearLayout) this
				.findViewById(R.id.id_linerlayout_nickname);
		ageLayout = (LinearLayout) this.findViewById(R.id.id_linerlayout_age);
		sexLayout = (LinearLayout) this.findViewById(R.id.id_linerlayout_sex);

		personalAvertarImageview = (ImageView) this
				.findViewById(R.id.personalAvertarImageView);

		personalNickNameTextView = (TextView) this
				.findViewById(R.id.personalNickNameTextView);

		personalAgeTextView = (TextView) this
				.findViewById(R.id.personalAgeTextView);

		personalSexTextView = (TextView) this
				.findViewById(R.id.personalSexTextView);

		/**
		 * // 从本地获取头像
		 * 
		 * File file = new File(ImgUir.tempFile.toString()); if (file.exists())
		 * { avertarBitmap = BitmapFactory.decodeFile(ImgUir.ALBUM_PATH +
		 * "cut.jpg"); personalAvertarImageview.setImageBitmap(avertarBitmap); }
		 * else { ShowToast("从服务器获取"); }
		 **/

		// 从服务器获取头像

		personalImageLayout.setOnClickListener(this);
		nickNameLayout.setOnClickListener(this);
		ageLayout.setOnClickListener(this);
		sexLayout.setOnClickListener(this);
		for (int i = 0; i <= 100; i++) {
			ageString[i] = i + "";
		}
		sexString[0] = "男";
		sexString[1] = "女";
	}

	/**
	 * 设置监听
	 */
	@Override
	public void onClick(View v) {
		if (v == personalImageLayout) {
			Intent intent = new Intent(LeftMenuPersonalData.this,
					ChoiceAvertarImageActivity.class);
			startActivity(intent);

		} else if (v == nickNameLayout) {
			showNickNameDialog();
		} else if (v == ageLayout) {
			showAgeDialog();
		} else if (v == sexLayout) {
			showSexDialog();
		}
	}

	/**
	 * 设置性别选择的dialog
	 */
	private void showSexDialog() {
		new AlertDialog.Builder(this)
				.setTitle("请选择性别")
				.setSingleChoiceItems(sexString, 0,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int whitch) {

								sexWho = whitch;
							}
						})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						personalSexTextView.setText(sexString[sexWho]);

						updateInfo("sex", sexString[sexWho]);

						Log.i("mm", sexWho + "");
					}
				}).setNegativeButton("取消", null).show();

	}

	/**
	 * 设置昵称填写的dialog
	 */
	private void showNickNameDialog() {
		editText = new EditText(this);
		new AlertDialog.Builder(this).setTitle("请修改昵称").setView(editText)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						personalNickNameTextView.setText(editText.getText()
								.toString());

						updateInfo("nick", editText.getText().toString());

					}
				}).setNegativeButton("取消", null).show();

	}

	private void updateInfo(String type, String value) {
		MyUser user = new MyUser();
		if (type.equals("nick")) {
			user.setNick(value);
		} else if (type.equals("age")) {
			user.setAge(Integer.parseInt(value));
		} else if (type.equals("sex")) {
			if (value.equals("男")) {
				user.setSex(true);
			} else {
				user.setSex(false);
			}

		}

		user.update(LeftMenuPersonalData.this,
				userManager.getCurrentUserObjectId(), new UpdateListener() {

					@Override
					public void onSuccess() {
						ShowToast("修改成功");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowToast("修改失败");

					}
				});

	}

	/**
	 * 设置年您选择的dialog
	 */
	private void showAgeDialog() {
		new AlertDialog.Builder(this)
				.setTitle("请选择年龄")
				.setSingleChoiceItems(ageString, 0,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int whitch) {
								ageWho = whitch + "";
							}
						})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						personalAgeTextView.setText(ageWho);
						updateInfo("age", ageWho);

					}
				}).setNegativeButton("取消", null).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
	}

}
