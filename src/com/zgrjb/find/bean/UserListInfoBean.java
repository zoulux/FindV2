package com.zgrjb.find.bean;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.TextView;

public class UserListInfoBean {
	private Bitmap userSex;
	private Button addFriendBt;
	private String userNick;

	public Bitmap getUserSex() {
		return userSex;
	}

	public void setUserSex(Bitmap userSex) {
		this.userSex = userSex;
	}

	public Button getAddFriendBt() {
		return addFriendBt;
	}

	public void setAddFriendBt(Button addFriendBt) {
		this.addFriendBt = addFriendBt;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}
}
