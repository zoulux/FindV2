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

public class ContactsFragment extends Fragment {
	// ����һ��rootView
	private View rootView;
	// ����һ��list_friends�б�
	private ListView list_friends;
	// ����һ��contactAdapter������
	private ContactAdapter contactAdapter;
	// ����һ������user��list�б�
	private List<MyUser> list = new ArrayList<MyUser>();
	BmobUserManager userManager = BmobUserManager.getInstance(getActivity());
	// ����һ���㲥������
	private BroadcastReceiver broadcastReceiver;
	// ����һ��user
	private MyUser user;
	// ����һ��intentFilter
	private IntentFilter intentFilter;
	// ����㲥������
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
	 * ��ѯ����
	 */
	private void queryFriends() {
		// ����������һ�α��صĺ������ݿ�ļ�飬��Ϊ�˱��غ������ݿ����Ѿ�����˶Է������ǽ���ȴû����ʾ����������
		// �����������ڴ��б���ĺ����б�
		CustomApplcation.getInstance().setContactList(
				CollectionUtils.list2map(BmobDB.create(getActivity())
						.getContactList()));

		// //////

		Map<String, BmobChatUser> users = CustomApplcation.getInstance()
				.getContactList();
		Log.i("mm", users.size() + ">>size");

		filledData(CollectionUtils.map2lists(users));
		// dealData();

		// ////

		/*
		 * Map<String, BmobChatUser> users = CustomApplcation.getInstance()
		 * .getContactList(); // ��װ�µ�User
		 * filledData(CollectionUtils.map2lists(users));
		 */

		contactAdapter = new ContactAdapter(getActivity(),
				R.layout.item_contact, list);
		list_friends.setAdapter(contactAdapter);

	}

	private void dealData() {
		list.clear();
		List<BmobChatUser> contactList = BmobDB.create(getActivity())
				.getContactList();
		// Log.i("mm", contactList.size() + "");

		for (int i = 0; i < contactList.size(); i++) {
			BmobChatUser chatUser = contactList.get(i);

			MyUser mUser = new MyUser();
			// Log.i("mm", chatUser.getAvatar() + ">>" + chatUser.getNick() +
			// ">>"
			// + chatUser.getObjectId());

			mUser.setAvatar(chatUser.getAvatar());
			// Log.i("mm", "1");

			mUser.setNick(chatUser.getNick());
			// Log.i("mm", "2");
			mUser.setObjectId(chatUser.getObjectId());
			// Log.i("mm", "3");

			list.add(mUser);
			// Log.i("mm", "4");
			// Log.i("mm",
			// chatUser.getAvatar() + ":" + chatUser.getNick() + ":"
			// + chatUser.getObjectId());
			// Log.i("mm", "5");

		}

		/*
		 * for (BmobChatUser chatUser : contactList) { MyUser mUser = new
		 * MyUser(); Log.i("mm", chatUser.getAvatar() + ">>" +
		 * chatUser.getNick() + ">>" + chatUser.getObjectId());
		 * 
		 * mUser.setAvatar(chatUser.getAvatar()); Log.i("mm", "1");
		 * 
		 * mUser.setNick(chatUser.getNick()); Log.i("mm", "2");
		 * mUser.setObjectId(chatUser.getObjectId()); Log.i("mm", "3");
		 * 
		 * list.add(mUser); Log.i("mm", mUser.getAvatar() + ":" +
		 * mUser.getNick() + ":" + mUser.getAge()); }
		 */
	}

	private void filledData(List<BmobChatUser> listUser) {

		list.clear();// ����գ�������ظ�
		System.out.println("listUserSize" + listUser.size());
		for (BmobChatUser chatUser : listUser) {
			MyUser mUser = new MyUser();

			mUser.setAvatar(chatUser.getAvatar());
			mUser.setNick(chatUser.getNick());
			mUser.setObjectId(chatUser.getObjectId());

			list.add(mUser);

		}

	}

	/**
	 * ��ʼ��View
	 */
	private void initView() {
		list_friends = (ListView) rootView.findViewById(R.id.list_friends);
		/*
		 * 
		 * list_friends.setOnItemLongClickListener(new OnItemLongClickListener()
		 * { //�ڳ�������ѡ��ʱ��ɾ������
		 * 
		 * @Override public boolean onItemLongClick(AdapterView<?> parent, View
		 * view, int position, long id) { System.out.println("����" + position);
		 * 
		 * final MyUser user = list.get(position);
		 * 
		 * final AlertDialog.Builder dialog = new Builder(getActivity());
		 * dialog.setMessage("ɾ������" + user.getNick()); dialog.setTitle("��ʾ");
		 * dialog.setPositiveButton("ȷ��", new OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface arg0, int arg1) {
		 * deleteUser(user);
		 * 
		 * } });
		 * 
		 * dialog.setNegativeButton("ȡ��", new OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface arg0, int arg1) {
		 * 
		 * } }); dialog.create().show(); return false; } });
		 */

		list_friends.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ContactsFragment.this.getActivity(),
						FriendsDataActivity.class);
				intent.putExtra("user", list.get(position));
				intent.putExtra("isChat", false);
				startActivity(intent);
				ContactsFragment.this.getActivity().overridePendingTransition(
						R.anim.zoom_enter, R.anim.zoom_exit);

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
	 * �ڳ�������ѡ��ʱ��ɾ������
	 * 
	 * @param user
	 *            ����
	 */
	private void deleteUser(final MyUser user) {

		final ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setMessage("����ɾ��...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		userManager.deleteContact(user.getObjectId(), new UpdateListener() {

			@Override
			public void onSuccess() {
				System.out.println("success");
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "ɾ���ɹ�", Toast.LENGTH_SHORT)
						.show();
				// ɾ���ڴ�
				CustomApplcation.getInstance().getContactList()
						.remove(user.getUsername());
				// ���½���
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
				Toast.makeText(getActivity(), "ɾ��ʧ��", Toast.LENGTH_SHORT)
						.show();
				progress.dismiss();
			}
		});

	}
}
