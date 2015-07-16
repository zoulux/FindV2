package com.zgrjb.find.adapter;

import java.util.List;

import android.content.Context;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zgrjb.find.R;
import com.zgrjb.find.utils.ImageLoadOptions;
import com.zgrjb.find.utils.TimeUtil;

public class RecentAdapter extends ArrayAdapter<BmobRecent> {
	private LayoutInflater inflater;
	private List<BmobRecent> mData;
	private Context mContext;

	public RecentAdapter(Context context, int resource, List<BmobRecent> list) {

		super(context, resource, list);

		inflater = LayoutInflater.from(context);

		mData = list;

		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		BmobRecent contact = mData.get(position);
		
		ViewHolde viewHolde = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_conversation, parent,
					false);
			Log.i("adapter", "11");
			viewHolde = new ViewHolde();
			viewHolde.iv_avatar = (ImageView) convertView
					.findViewById(R.id.iv_recent_avatar);
			viewHolde.tv_recent_msg = (TextView) convertView
					.findViewById(R.id.tv_recent_msg);
			viewHolde.tv_recent_name = (TextView) convertView
					.findViewById(R.id.tv_recent_name);
			viewHolde.tv_recent_time = (TextView) convertView
					.findViewById(R.id.tv_recent_time);
			viewHolde.tv_recent_unread = (TextView) convertView
					.findViewById(R.id.tv_recent_unread);
			convertView.setTag(viewHolde);
		} else {
			viewHolde = (ViewHolde) convertView.getTag();
		}
		// 设置数据
		String avatar = contact.getAvatar();
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, viewHolde.iv_avatar,
					ImageLoadOptions.getOptions());
		} else {
			viewHolde.iv_avatar.setImageResource(R.drawable.child);
		}


		viewHolde.tv_recent_name.setText(contact.getUserName());
		viewHolde.tv_recent_time
				.setText(TimeUtil.getChatTime(contact.getTime()));
		// 显示内容
		System.out.println(contact.getMessage()+"adapter message");
		if (contact.getType() == BmobConfig.TYPE_TEXT) {
			viewHolde.tv_recent_msg.setText(contact.getMessage());
		} else if (contact.getType() == BmobConfig.TYPE_IMAGE) {
			viewHolde.tv_recent_msg.setText("[图片]");
		} else if (contact.getType() == BmobConfig.TYPE_VOICE) {
			viewHolde.tv_recent_msg.setText("[语音]");
		}
		
		int num = BmobDB.create(mContext).getUnreadCount(contact.getTargetid());
		if (num > 0) {
			viewHolde.tv_recent_unread.setVisibility(View.VISIBLE);
			viewHolde.tv_recent_unread.setText(num + "");
		} else {
			viewHolde.tv_recent_unread.setVisibility(View.GONE);
		}

		return convertView;

	}

}
