package com.zgrjb.find.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.connect.UserInfo;
import com.zgrjb.find.R;
import com.zgrjb.find.bean.UserListInfoBean;

public class ShowUserInfoAdapter extends BaseAdapter {
	private List<UserListInfoBean> infoList = new ArrayList<UserListInfoBean>();
	private LayoutInflater inflater;

	private Context context;
	private onUserItemClickListener itemClickListener;

	public interface onUserItemClickListener {
		public void onclic(View v, int pos);
	}

	public void setOnUserItemClickListener(
			onUserItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	public onUserItemClickListener getItemClickListener() {
		return itemClickListener;
	}

	public ShowUserInfoAdapter(List<UserListInfoBean> infoList, Context context) {
		this.infoList = infoList;
		this.inflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		return infoList.size();
	}

	@Override
	public Object getItem(int position) {
		return infoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.user_item_list, null);
			holder.userSex = (ImageView) convertView
					.findViewById(R.id.id_userSexImg);
			holder.userNick = (TextView) convertView
					.findViewById(R.id.id_userNick);
			holder.addFriendBt = (Button) convertView
					.findViewById(R.id.id_add_friend);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.userSex.setImageBitmap(infoList.get(position).getUserSex());
		holder.userNick.setText(infoList.get(position).getUserNick());
		holder.addFriendBt.setText("Ìí¼Ó");
		holder.addFriendBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemClickListener.onclic(v, position);
			}
		});
		return convertView;
	}

	class Holder {
		public ImageView userSex;
		public TextView userNick;
		public Button addFriendBt;
	}

}
