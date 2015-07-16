package com.zgrjb.find.adapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewHolde {
	// 文本类型
	ImageView iv_avatar; // 头像
	ImageView iv_fail_resend; // 发送失败
	TextView tv_send_status; // 发送状态
	TextView tv_time; // 发送时间
	TextView tv_message; // 文本内容

	// 声音
	ProgressBar pb_upLoad; // 上传的进度条
	ImageView iv_voiceIcon; // 播放声音时的小动画
	LinearLayout ll_voiceLength; // 声音的长度条状
	TextView tv_voiceLength; // 声音的长度左边的文字
	
	//最近会话
	TextView tv_recent_name;   //最近会话列表中的逆臣
	TextView tv_recent_msg;    //消息
	TextView tv_recent_time;	//
	TextView tv_recent_unread;	//未读标志
	
	//图片
	ImageView iv_picture;
	
	//通讯录
		TextView tv_contact_nick,tv_contact_userName; //用户昵称
}

