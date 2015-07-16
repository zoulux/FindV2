package com.zgrjb.find.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

import com.zgrjb.find.R;
import com.zgrjb.find.adapter.ContactAdapter;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.ui.FriendsDataActivity;
import com.zgrjb.find.utils.CollectionUtils;
import com.zgrjb.find.utils.CustomApplcation;

public class ContactsFragmentV1 extends Fragment {
	// 定义一个rootView
	private View rootView;
	// 定义一个list_friends列表
	private ListView list_friends;
	// 定义一个contactAdapter适配器
	private ContactAdapter contactAdapter=null;
	// 定义一个关于user的list列表
	private List<MyUser> list = new ArrayList<MyUser>();
	BmobUserManager userManager = BmobUserManager.getInstance(getActivity());
	// 定义一个广播接收者
	private BroadcastReceiver broadcastReceiver;
	// 定义一个user
	private MyUser user;
	// 定义一个intentFilter
	private IntentFilter intentFilter;
	// 定义广播管理器
	private LocalBroadcastManager broadcastManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.main_ui_contacts_fragment,
				container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		intentFilter = new IntentFilter();
		intentFilter.addAction("senddelete");
		// recieveBroadcastAndDelete();

	}

	/**
	 * 查询好友
	 */
	private void queryFriends() {
		// 在这里再做一次本地的好友数据库的检查，是为了本地好友数据库中已经添加了对方，但是界面却没有显示出来的问题
		// 重新设置下内存中保存的好友列表
		CustomApplcation.getInstance().setContactList(
				CollectionUtils.list2map(BmobDB.create(getActivity())
						.getContactList()));

		// //////

		List<BmobChatUser> contactList = BmobDB.create(getActivity())
				.getContactList();
		Log.i("mm", contactList.size()+"");
		for (BmobChatUser chatUser : contactList) {
			MyUser mUser = new MyUser();
			mUser.setAvatar(chatUser.getAvatar());
			mUser.setNick(chatUser.getNick());
			mUser.setObjectId(chatUser.getObjectId());
			mUser.setUsername(chatUser.getUsername());
			
			
			
			Log.i("mm",
					mUser.getAvatar() + ":" + mUser.getNick() + ":"
							+ mUser.getAge());
		}

		// ////
		Map<String, BmobChatUser> users = CustomApplcation.getInstance()
				.getContactList();
		// 组装新的User
		filledData(CollectionUtils.map2lists(users));
		
		if (contactAdapter==null) {
			contactAdapter = new ContactAdapter(getActivity(),
					R.layout.item_contact, list);
			list_friends.setAdapter(contactAdapter);
			
		}else
		{
			contactAdapter.notifyDataSetChanged();
		}
		

	}

	private void filledData(List<BmobChatUser> listUser) {

		list.clear();// 先清空，否则会重复
		System.out.println("listUserSize" + listUser.size());
		for (BmobChatUser chatUser : listUser) {
			MyUser mUser = new MyUser();
			mUser.setAvatar(chatUser.getAvatar());
			mUser.setNick(chatUser.getNick());
			mUser.setObjectId(chatUser.getObjectId());
			mUser.setUsername(chatUser.getUsername());

			list.add(mUser);

		}

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		list_friends = (ListView) rootView.findViewById(R.id.list_friends);
		/*
		 * 
		 * list_friends.setOnItemLongClickListener(new OnItemLongClickListener()
		 * { //在长按好友选项时会删除好友
		 * 
		 * @Override public boolean onItemLongClick(AdapterView<?> parent, View
		 * view, int position, long id) { System.out.println("长按" + position);
		 * 
		 * final MyUser user = list.get(position);
		 * 
		 * final AlertDialog.Builder dialog = new Builder(getActivity());
		 * dialog.setMessage("删除好友" + user.getNick()); dialog.setTitle("提示");
		 * dialog.setPositiveButton("确定", new OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface arg0, int arg1) {
		 * deleteUser(user);
		 * 
		 * } });
		 * 
		 * dialog.setNegativeButton("取消", new OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface arg0, int arg1) {
		 * 
		 * } }); dialog.create().show(); return false; } });
		 */

		list_friends.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ContactsFragmentV1.this.getActivity(),
						FriendsDataActivity.class);

				//

				intent.putExtra("user", list.get(position));
				Log.i("mm", list.get(position).toString());
				startActivity(intent);

			}
		});

	}

	private boolean hidden;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refresh();
		}
	}

	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					queryFriends();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 在长按好友选项时会删除好友
	 * 
	 * @param user
	 *            好友
	 */
	private void deleteUser(final MyUser user) {

		final ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setMessage("正在删除...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		userManager.deleteContact(user.getObjectId(), new UpdateListener() {

			@Override
			public void onSuccess() {
				System.out.println("success");
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT)
						.show();
				// 删除内存
				CustomApplcation.getInstance().getContactList()
						.remove(user.getUsername());
				// 更新界面
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						progress.dismiss();
						list.remove(user);
						list_friends.setSelection(list.size() - 1);
						contactAdapter.notifyDataSetChanged();

					}
				});
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				System.out.println("fail");
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT)
						.show();
				progress.dismiss();
			}
		});

	}

	// private void recieveBroadcastAndDelete(){
	//
	// broadcastReceiver = new BroadcastReceiver() {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// ContactsFragment.this.getActivity().finish();
	// userManager.deleteContact(user.getObjectId(), new UpdateListener() {
	//
	// @Override
	// public void onSuccess() {
	// System.out.println("success");
	// // 删除内存
	// CustomApplcation.getInstance().getContactList()
	// .remove(user.getUsername());
	// list.remove(user);
	// list_friends.setSelection(list.size() - 1);
	// contactAdapter.notifyDataSetChanged();
	// }
	//
	// @Override
	// public void onFailure(int arg0, String arg1) {
	// System.out.println("fail");
	// // TODO Auto-generated method stub
	// Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT)
	// .show();
	// }
	// });
	// }
	// };
	// broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
	// }

	// @Override
	// public void onAttach(Activity activity) {
	// user = (MyUser)
	// ContactsFragment.this.getActivity().getIntent().getSerializableExtra("user");
	//
	// // 注册广播
	// IntentFilter intentFilter = new IntentFilter();
	// intentFilter.addAction("senddelete");
	// activity.registerReceiver(broadcastReceiver, intentFilter);
	// recieveBroadcastAndDelete();
	// super.onAttach(activity);
	// }

	// /**
	// *注销广播
	// * */
	// @Override
	// public void onDestroyView() {
	// getActivity().unregisterReceiver(broadcastReceiver);
	// super.onDestroyView();
	// }

}
