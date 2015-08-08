package com.zgrjb.find.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgrjb.find.R;
import com.zgrjb.find.bean.ChatMessage;
import com.zgrjb.find.bean.ChatMessage.Type;

public class ChatMessageAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<ChatMessage> mDates;

	public ChatMessageAdapter(Context context, List<ChatMessage> list) {
		mInflater = LayoutInflater.from(context);
		mDates = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDates.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mDates.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ChatMessage chatMessage = mDates.get(position);
		ViewHolder viewHolder = null;

		if (converView == null) {

			// 先通过ItemType设置布局
			if (getItemViewType(position) == 0) {
				converView = mInflater.inflate(R.layout.item_from_msg, parent,
						false);
				viewHolder = new ViewHolder();

				viewHolder.mDate = (TextView) converView
						.findViewById(R.id.item_from_msg_data);
				viewHolder.mMsg = (TextView) converView
						.findViewById(R.id.item_from_msg_info);

//				viewHolder.mName = (TextView) converView
//						.findViewById(R.id.tv_robotName);
//				viewHolder.mImg = (ImageView) converView
//						.findViewById(R.id.iv_robotImg);

			} else {
				converView = mInflater.inflate(R.layout.item_to_msg, parent,
						false);
				viewHolder = new ViewHolder();
				viewHolder.mName = (TextView) converView
						.findViewById(R.id.tv_robotName);
				viewHolder.mImg = (ImageView) converView
						.findViewById(R.id.iv_robotImg);
				viewHolder.mDate = (TextView) converView
						.findViewById(R.id.item_to_msg_data);
				viewHolder.mMsg = (TextView) converView
						.findViewById(R.id.item_to_msg_info);
			}
			converView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) converView.getTag();
		}

		// 设置
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		viewHolder.mDate.setText(df.format(chatMessage.getDate()));
		viewHolder.mMsg.setText(chatMessage.getMsg());

		if (getItemViewType(position) == 1) {

			viewHolder.mName.setText(chatMessage.getName());
	//		viewHolder.mImg.setBackgroundColor(chatMessage.getmImg());
		}

		return converView;
	}

	private final class ViewHolder {
		TextView mDate;
		TextView mMsg;
		ImageView mImg;
		TextView mName;

	}

	@Override
	public int getItemViewType(int position) {
		ChatMessage chatMessage = mDates.get(position);
		if (chatMessage.getType() == Type.INCOMING) {
			return 0;
		} else {
			return 1;
		}

	}

	@Override
	public int getViewTypeCount() {

		return 2;
	}

}
