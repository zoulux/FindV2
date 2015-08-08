package com.zgrjb.find.ui.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

import com.zgrjb.find.R;
import com.zgrjb.find.adapter.RecentAdapter;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.ui.ChatActivity;

public class RecentFragment extends Fragment implements OnItemClickListener,
		OnItemLongClickListener {

	private ListView recentContact;
	private RecentAdapter recentAdapter = null;
	// 定义一个rootview
	private View rootView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.main_ui_recent_fragment,
				container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();

	}

	/**
	 * 初始化view
	 */
	private void initView() {
		recentContact = (ListView) rootView
				.findViewById(R.id.lv_recent_contact);
		recentContact.setOnItemClickListener(this);
		recentContact.setOnItemLongClickListener(this);

		recentAdapter = new RecentAdapter(rootView.getContext(),
				R.layout.item_conversation, BmobDB
						.create(rootView.getContext()).queryRecents());
		recentContact.setAdapter(recentAdapter);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BmobRecent contact = recentAdapter.getItem(position);
		// 重置未读消息
		BmobDB.create(getActivity()).resetUnread(contact.getTargetid());
		// 组装聊天对象

		MyUser user = new MyUser();
		user.setAvatar(contact.getAvatar());
		user.setNick(contact.getNick());
		user.setUsername(contact.getUserName());
		user.setObjectId(contact.getTargetid());

		System.out.println("contact.getUserName()" + contact.getUserName());
		System.out.println("contact.getTargetid()" + contact.getTargetid());

		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra("user", user);
		startActivity(intent);
		RecentFragment.this.getActivity().overridePendingTransition(
				R.anim.zoom_enter, R.anim.zoom_exit);
		// viewHolde.tv_recent_unread.setVisibility(View.GONE);
	}

	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					System.out.println("size>>>"
							+ BmobDB.create(getActivity()).queryRecents()
									.size());

					if (recentAdapter == null) {
						recentAdapter = new RecentAdapter(getActivity(),
								R.layout.item_conversation, BmobDB.create(
										getActivity()).queryRecents());
						recentContact.setAdapter(recentAdapter);
						System.out.println("1>>");
					} else {

						List<BmobRecent> queryList = BmobDB.create(
								getActivity()).queryRecents();
						// recentAdapter = new RecentAdapter(getActivity(),
						// R.layout.item_conversation, queryList);
						//
						// recentContact.setAdapter(recentAdapter);
						recentAdapter.getmData().clear();
						recentAdapter.getmData().addAll(queryList);
						recentAdapter.notifyDataSetChanged();

						// recentAdapter.setmData(recentAdapter.getmData().addAll(queryList));

						// recentAdapter = (RecentAdapter) recentContact
						// .getAdapter();
						// recentContact.setAdapter(recentAdapter);

						// recentAdapter.notifyDataSetChanged();

						System.out.println("2>>" + recentAdapter.getCount());

					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		return false;
	}
}
