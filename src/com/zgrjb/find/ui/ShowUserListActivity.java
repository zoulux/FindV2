package com.zgrjb.find.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.UpdateListener;

import com.zgrjb.find.R;
import com.zgrjb.find.adapter.ShowUserInfoAdapter;
import com.zgrjb.find.adapter.ShowUserInfoAdapter.onUserItemClickListener;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.bean.UserListInfoBean;
import com.zgrjb.find.ui.puzzle_game.PreparePuzzleGameActivity;

public class ShowUserListActivity extends BaseActivity {
	private List<MyUser> userList;
	private List<UserListInfoBean> list = new ArrayList<UserListInfoBean>();
	private ListView allUserList;
	private ShowUserInfoAdapter userInfoAdapter;

	AlertDialog.Builder builder2;
	private int whatDiff;

	private int userNum = 0;

	SharedPreferences sharedPreferences = null;
	Editor editor = null;

	int shareType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_user_list);
		builder2 = new AlertDialog.Builder(this);
		initUserFlag();
		initUserList();
		initTitle();
	}

	private void initUserFlag() {
		sharedPreferences = getSharedPreferences("ShowUserListActivity",
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

	}

	private void initTitle() {
		showTitleText("附近的人");
		initLeftTitle();
		initRightTitle();
	}

	private void initRightTitle() {
		setRightDrawablePath(getResources().getDrawable(R.drawable.maplist));
		rightButtonIsVisible(true);
		rightImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ShowToast(">>>>>");

				showMyDialog(); // 弹出过滤选项

			}
		});
	}

	protected void showMyDialog() {
		// new AlertDialog.Builder(ShowUserListActivity.this).setTitle("请选择")
		// .setSingleChoiceItems(new String[] { "11", "22", "33"
		//
		// }, null, null).setPositiveButton("确定", null)
		// .setNegativeButton("取消", null).show();

		AlertDialog.Builder dialog = new AlertDialog.Builder(
				ShowUserListActivity.this);

		dialog.setTitle("请选择")
				.setSingleChoiceItems(
						new String[] { "查看全部", "只看男生", "只看女生", "清除当前位置并退出" },
						shareType, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									showAllUser(dialog);
									break;
								case 1:
									showBoy(dialog);
									break;
								case 2:
									showGirl(dialog);
									break;
								case 3:
									deleteMyLocation(dialog);
									break;

								default:
									break;
								}

								// ShowToast(which);
								// dialog.dismiss();
								// Toast.makeText(ShowUserListActivity.this,
								// which + "", 1).show();
							}
						}).setNegativeButton("取消", null).show();
	}

	protected void deleteMyLocation(DialogInterface dialog) {
		MyUser mUser = new MyUser();
		BmobGeoPoint point = new BmobGeoPoint(0.0, 0.0);
		mUser.setLocation(point);
		mUser.update(ShowUserListActivity.this,
				userManager.getCurrentUserObjectId(), new UpdateListener() {

					@Override
					public void onSuccess() {
						ShowToast("清除成功");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowToast(arg1);

					}
				});

		Intent i = new Intent("deleteMyLocation");
		sendBroadcast(i);
		finish();
		dialog.dismiss();

	}

	protected void showGirl(DialogInterface dialog) {
		editor.putString("usertype", "girl");
		editor.commit();
		dialog.dismiss();

		startActivity(new Intent(ShowUserListActivity.this,
				ShowUserListActivity.class));
		finish();
	}

	protected void showBoy(DialogInterface dialog) {
		editor.putString("usertype", "boy");
		editor.commit();
		dialog.dismiss();
		startActivity(new Intent(ShowUserListActivity.this,
				ShowUserListActivity.class));
		finish();
	}

	protected void showAllUser(DialogInterface dialog) {
		editor.putString("usertype", "all");
		editor.commit();
		dialog.dismiss();
		startActivity(new Intent(ShowUserListActivity.this,
				ShowUserListActivity.class));
		finish();
	}

	private void initLeftTitle() {
		setDrawablePath(getResources().getDrawable(R.drawable.jiantou2));
		leftButtonIsVisible(true);
		leftImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowUserListActivity.this.finish();
				overridePendingTransition(R.anim.quit_zoom_enter,
						R.anim.quit_zoom_exit);
			}
		});
	}

	private void initUserList() {
		// userList = (List<MyUser>)
		// getIntent().getSerializableExtra("userList");

		String type = sharedPreferences.getString("usertype", "all");

		if (type.equals("all")) {
			userList = mApplication.getNearbyUser();
			ShowToast("1");
			shareType = 0;

		} else if (type.equals("girl")) {
			List<MyUser> mList = new ArrayList<MyUser>();
			List<MyUser> list = mApplication.getNearbyUser();
			for (MyUser myUser : list) {
				if (!myUser.getSex()) {
					mList.add(myUser);
				}
			}
			userList = mList;
			ShowToast("2");
			shareType = 2;
		} else if (type.equals("boy")) {
			List<MyUser> mList = new ArrayList<MyUser>();
			List<MyUser> list = mApplication.getNearbyUser();
			for (MyUser myUser : list) {
				if (myUser.getSex()) {
					mList.add(myUser);
				}
			}
			userList = mList;
			ShowToast("3");
			shareType = 1;
		}

		allUserList = (ListView) findViewById(R.id.id_show_user_list);
		setData();
		userInfoAdapter = new ShowUserInfoAdapter(list, this);
		allUserList.setAdapter(userInfoAdapter);
		userInfoAdapter
				.setOnUserItemClickListener(new onUserItemClickListener() {

					@Override
					public void onclic(View v, int pos) {
						userNum = pos;
						showDialogToChoiceDifficult();
					}
				});
		// for (int i = 0; i < list.size(); i++) {
		// final int t=i;
		// list.get(i).getUserSex().setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// ShowToast(t);
		// }
		// });
		// }

		// allUserList.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// ShowToast(position);
		// // final int t=position;
		// // list.get(t).getAddFriendBt().setOnClickListener(new
		// OnClickListener() {
		// //
		// // @Override
		// // public void onClick(View v) {
		// // ShowToast(t);
		// // }
		// // });
		// }
		// });
	}

	private void setData() {
		for (int i = 0; i < userList.size(); i++) {
			UserListInfoBean info = new UserListInfoBean();
			if (userList.get(i).getSex()) {
				info.setUserSex(BitmapFactory.decodeResource(getResources(),
						R.drawable.icon_gcoding1));
			} else {
				info.setUserSex(BitmapFactory.decodeResource(getResources(),
						R.drawable.icon_gcoding2));
			}
			info.setUserNick(userList.get(i).getNick());
			info.setAddFriendBt(new Button(this));
			list.add(info);
		}
	}

	/**
	 * 选择游戏难度的dialog
	 */
	private void showDialogToChoiceDifficult() {
		builder2.setTitle("拼图游戏难度选择");
		final String[] hobby = { "容易", "正常", "艰难" };
		builder2.setSingleChoiceItems(hobby, 0,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						whatDiff = which;

					}
				});

		builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				Intent intent = new Intent(ShowUserListActivity.this,
						PreparePuzzleGameActivity.class);
				intent.putExtra("diff", whatDiff + 6);
				intent.putExtra("diffValue", hobby[whatDiff]);
				intent.putExtra("user", userList.get(userNum));
				ShowUserListActivity.this.startActivity(intent);
				ShowUserListActivity.this.finish();
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
		});
		builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		builder2.show();
	}
}
