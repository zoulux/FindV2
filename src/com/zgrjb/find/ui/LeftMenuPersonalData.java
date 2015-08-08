package com.zgrjb.find.ui;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.Contacts.AggregationSuggestions;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.utils.CircleImageDrawable;
import com.zgrjb.find.utils.ImageLoadOptions;


public class LeftMenuPersonalData extends BaseActivity implements
		OnClickListener {
	// ����һ������ͷ���layout
	private LinearLayout personalImageLayout;
	// ����һ�������ǳƵ�layout
	private LinearLayout nickNameLayout;
	// ����һ������������layout
	private LinearLayout ageLayout;
	// ����һ�������Ա��layout
	private LinearLayout sexLayout;
	// ����ͷ��λͼ
	private Bitmap avertarBitmap;
	// ����һ������ͷ���ImageView�����޸�
	private ImageView personalAvertarImageview;
	// ����һ�������ǳƵ�TextView�����޸�
	private TextView personalNickNameTextView;
	// ����һ������������TextView�����޸�
	private TextView personalAgeTextView;
	// ����һ�������Ա��TextView�����޸�
	private TextView personalSexTextView;
	// �޸��ǳƵ�ʱ�������ֵ�editText
	private EditText editText;
	// ��ʼ������Ϊ0
	private String ageWho = "0";
	// ��ʼ���Ա�Ϊ0
	private int sexWho = 0;

	private String[] sexString = new String[2];
	private String[] ageString = new String[101];

	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			MyUser user = (MyUser) msg.obj;
			setUserInfo(user);

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_left_menu_personal_data);

		showTitleText("��������");
		init();

	}

	private void queryCurrentUser() {

		BmobQuery<MyUser> query = new BmobQuery<MyUser>();
		query.addWhereEqualTo("objectId", userManager.getCurrentUserObjectId());
		query.findObjects(this, new FindListener<MyUser>() {

			@Override
			public void onSuccess(List<MyUser> arg0) {

				if (arg0.size() != 0) {
					Message message = new Message();
					message.obj = arg0.get(0);
					mHandler.sendMessage(message);

					//
				}

			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});

	};

	private void setUserInfo(MyUser user) {

		// `�޸�ͷ��
		String avatarPath = user.getAvatar();
		setAvatar(avatarPath);

		// �޸��ǳ�
		String nick = user.getNick();
		personalNickNameTextView.setText(nick);

		// ��������
		int age = user.getAge();
		personalAgeTextView.setText(String.valueOf(age));
		// �Ա�
		boolean sex = user.getSex();
		setSex(sex);
	}

	private void setSex(boolean sex) {
		if (sex) {
			personalSexTextView.setText("��");
		} else {
			personalSexTextView.setText("Ů");

		}

	}

	// �첽����ͷ��
	private void setAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			//File f = ImageLoader.getInstance().getDiscCache().get("");
			//Bitmap b=ImageLoadOptions.getOptions().getDecodingOptions().inBitmap;
			ImageLoader.getInstance().displayImage(avatar,
					personalAvertarImageview, ImageLoadOptions.getOptions());
		} else {
			personalAvertarImageview.setImageResource(R.drawable.default_head);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		new Thread() {
			public void run() {

				queryCurrentUser();
			}
		}.start();

		// requestFromServer(); // ���������������

	}

	/**
	 * ��ʼ���㲥��id���ͼ�����
	 */
	private void init() {

		personalImageLayout = (LinearLayout) this
				.findViewById(R.id.id_linerlayout_personal_image);
		nickNameLayout = (LinearLayout) this
				.findViewById(R.id.id_linerlayout_nickname);
		ageLayout = (LinearLayout) this.findViewById(R.id.id_linerlayout_age);
		sexLayout = (LinearLayout) this.findViewById(R.id.id_linerlayout_sex);

		personalAvertarImageview = (ImageView) this
				.findViewById(R.id.personalAvertarImageView);

		personalNickNameTextView = (TextView) this
				.findViewById(R.id.personalNickNameTextView);

		personalAgeTextView = (TextView) this
				.findViewById(R.id.personalAgeTextView);

		personalSexTextView = (TextView) this
				.findViewById(R.id.personalSexTextView);

		/**
		 * // �ӱ��ػ�ȡͷ��
		 * 
		 * File file = new File(ImgUir.tempFile.toString()); if (file.exists())
		 * { avertarBitmap = BitmapFactory.decodeFile(ImgUir.ALBUM_PATH +
		 * "cut.jpg"); personalAvertarImageview.setImageBitmap(avertarBitmap); }
		 * else { ShowToast("�ӷ�������ȡ"); }
		 **/

		// �ӷ�������ȡͷ��

		personalImageLayout.setOnClickListener(this);
		nickNameLayout.setOnClickListener(this);
		ageLayout.setOnClickListener(this);
		sexLayout.setOnClickListener(this);
		for (int i = 0; i <= 100; i++) {
			ageString[i] = i + "";
		}
		sexString[0] = "��";
		sexString[1] = "Ů";
	}

	/**
	 * ���ü���
	 */
	@Override
	public void onClick(View v) {
		if (v == personalImageLayout) {
			Intent intent = new Intent(LeftMenuPersonalData.this,
					ChoiceAvertarImageActivity.class);
			startActivity(intent);

		} else if (v == nickNameLayout) {
			showNickNameDialog();
		} else if (v == ageLayout) {
			showAgeDialog();
		} else if (v == sexLayout) {
			showSexDialog();
		}
	}

	/**
	 * �����Ա�ѡ���dialog
	 */
	private void showSexDialog() {
		new AlertDialog.Builder(this)
				.setTitle("��ѡ���Ա�")
				.setSingleChoiceItems(sexString, 0,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int whitch) {

								sexWho = whitch;
							}
						})
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						personalSexTextView.setText(sexString[sexWho]);

						updateInfo("sex", sexString[sexWho]);

						Log.i("mm", sexWho + "");
					}
				}).setNegativeButton("ȡ��", null).show();

	}

	/**
	 * �����ǳ���д��dialog
	 */
	private void showNickNameDialog() {
		editText = new EditText(this);
		new AlertDialog.Builder(this).setTitle("���޸��ǳ�").setView(editText)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						personalNickNameTextView.setText(editText.getText()
								.toString());

						updateInfo("nick", editText.getText().toString());

					}
				}).setNegativeButton("ȡ��", null).show();

	}

	private void updateInfo(String type, String value) {
		MyUser user = new MyUser();
		if (type.equals("nick")) {
			user.setNick(value);
		} else if (type.equals("age")) {
			user.setAge(Integer.parseInt(value));
		} else if (type.equals("sex")) {
			if (value.equals("��")) {
				user.setSex(true);
			} else {
				user.setSex(false);
			}

		}

		user.update(LeftMenuPersonalData.this,
				userManager.getCurrentUserObjectId(), new UpdateListener() {

					@Override
					public void onSuccess() {
						ShowToast("�޸ĳɹ�");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowToast("�޸�ʧ��");

					}
				});

	}

	/**
	 * ��������ѡ���dialog
	 */
	private void showAgeDialog() {
		new AlertDialog.Builder(this)
				.setTitle("��ѡ������")
				.setSingleChoiceItems(ageString, 0,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int whitch) {
								ageWho = whitch + "";
							}
						})
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						personalAgeTextView.setText(ageWho);
						updateInfo("age", ageWho);

					}
				}).setNegativeButton("ȡ��", null).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
	}
}
