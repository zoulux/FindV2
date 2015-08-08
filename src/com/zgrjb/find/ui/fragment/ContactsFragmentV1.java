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
	// ����һ��rootView
	private View rootView;
	// ����һ��list_friends�б�
	private ListView list_friends;
	// ����һ��contactAdapter������
	private ContactAdapter contactAdapter=null;
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
		// ��װ�µ�User
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

		list.clear();// ����գ�������ظ�
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
				Intent intent = new Intent(ContactsFragmentV1.this.getActivity(),
						FriendsDataActivity.class);
				
				//

				intent.putExtra("user", list.get(position));
				Log.i("mm", list.get(position).toString());
				startActivity(intent);
//				ContactsFragment.this.getActivity().overridePendingTransition(
//						R.anim.zoom_enter, R.anim.zoom_exit);


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
	// // ɾ���ڴ�
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
	// Toast.makeText(getActivity(), "ɾ��ʧ��", Toast.LENGTH_SHORT)
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
	// // ע��㲥
	// IntentFilter intentFilter = new IntentFilter();
	// intentFilter.addAction("senddelete");
	// activity.registerReceiver(broadcastReceiver, intentFilter);
	// recieveBroadcastAndDelete();
	// super.onAttach(activity);
	// }

	// /**
	// *ע���㲥
	// * */
	// @Override
	// public void onDestroyView() {
	// getActivity().unregisterReceiver(broadcastReceiver);
	// super.onDestroyView();
	// }

}
