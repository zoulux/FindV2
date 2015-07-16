package com.zgrjb.find.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.listener.UpdateListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.utils.CustomApplcation;
import com.zgrjb.find.utils.ImageLoadOptions;

public class ContactAdapter extends ArrayAdapter<MyUser> {
	private List<MyUser> mData;
	private Context mContext;
	private LayoutInflater inflater;
	private BmobUserManager userManager;

	public ContactAdapter(Context context, int resource, List<MyUser> list) {
		super(context, resource, list);
		mContext = context;
		mData = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		MyUser user = mData.get(position);
		ViewHolde viewHolde = null;

		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.item_contact, parent, false);
			viewHolde = new ViewHolde();
			viewHolde.iv_avatar = (ImageView) convertView
					.findViewById(R.id.iv_contact_avatar);
			viewHolde.tv_contact_nick = (TextView) convertView
					.findViewById(R.id.tv_contact_nick);

			convertView.setTag(viewHolde);

		} else {
			viewHolde = (ViewHolde) convertView.getTag();
		}
		// …Ë÷√ ˝æ›

		String avatar = user.getAvatar();
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, viewHolde.iv_avatar,
					ImageLoadOptions.getOptions());
		} else {
			viewHolde.iv_avatar.setImageResource(R.drawable.child);
		}

		viewHolde.tv_contact_nick.setText(user.getNick());
		

		return convertView;
	}

}
