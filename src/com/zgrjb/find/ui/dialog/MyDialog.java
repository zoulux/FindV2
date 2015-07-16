package com.zgrjb.find.ui.dialog;

import com.zgrjb.find.bean.MyUser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

public class MyDialog {
	// 定义一个上下文
	private Context context;
	// 定义dialog
	private AlertDialog.Builder builder;

	public MyDialog(Context context) {
		this.context = context;
		builder = new AlertDialog.Builder(context);
	}

	/**
	 * 跟换主题的dialog
	 * 
	 * @param whatTheme
	 *            哪一个主题
	 */
	public void showChoiceThemeDialog(final int whatTheme) {
		builder.setTitle("提示");
		builder.setMessage("确定更换主题？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent("choiceIcon");
				intent.putExtra("theme", whatTheme);
				context.sendBroadcast(intent);
				Toast.makeText(context, "主题更换成功", 1).show();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();

	}

	/**
	 * 删除好友的dialog
	 * 
	 * @param user
	 *            某个好友
	 */
	public void showDeleteUserDialog(final MyUser user) {
		builder.setTitle("提示");
		builder.setMessage("确定删除好友" + user.getNick() + "?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent("senddelete");
				intent.putExtra("user", user);
				context.sendBroadcast(intent);
				// context.startActivity(new
				// Intent(context,ContactsFragment.class));
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();

	}
}
