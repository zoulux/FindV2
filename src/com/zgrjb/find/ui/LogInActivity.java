package com.zgrjb.find.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.config.ImgUir;
import com.zgrjb.find.file_handle.HandlePicFile;
import com.zgrjb.find.utils.CommonUtils;
import com.zgrjb.find.utils.ImageLoadOptions;

public class LogInActivity extends BaseActivity implements OnClickListener {
	private Tencent mTencent;

	// ��ʼ����¼�����ͷ��
	private ImageView logInImageView;
	// ����һ���û����������edittext
	private EditText userLogIneEditText;
	// ����һ�����������edittext
	private EditText passwordLogInEditText;
	// ����һ�������������button
	private Button LogInBt;
	// ����һ�����û���textview
	private TextView newUserTextView;
	// ����һ��HandlePicFile�࣬����ͼƬ�Ĵ���
	private HandlePicFile handleFile;
	// ����һ����Ƭ��λͼ
	private Bitmap photo;

	private Button btByQQ;

	private ProgressDialog progress = null;

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// ����
	private static final int PHOTO_REQUEST_CUT = 2;// ���
	private File file = new File("sdcard/Find/Picture_Regist");

	private static String APP_ID = "1104804412";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mTencent = Tencent.createInstance(APP_ID, this);
		progress = new ProgressDialog(LogInActivity.this);
		showTitleText("��¼");
		init();

		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * ��ʼ��id��������ͷ���ѡ��
	 */
	private void init() {
		logInImageView = (ImageView) this.findViewById(R.id.loginImageView);
		userLogIneEditText = (EditText) this
				.findViewById(R.id.userLogInEditText);
		passwordLogInEditText = (EditText) this
				.findViewById(R.id.passWordLoginEditText);
		newUserTextView = (TextView) findViewById(R.id.newUserTextView);
		LogInBt = (Button) this.findViewById(R.id.logInBt);
		newUserTextView.setOnClickListener(this);
		LogInBt.setOnClickListener(this);
		handleFile = new HandlePicFile(this, ImgUir.ALBUM_PATH);

		try {
			MyUser user = ((MyUser) getIntent().getSerializableExtra("user"));
			String avatarPath = user.getAvatar();
			String userName = user.getUsername();
			setAvatar(avatarPath);
			if (!user.getIsQQ()) {
				setCurrentUserNmae(userName);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		btByQQ = (Button) findViewById(R.id.bt_byqq);
		btByQQ.setOnClickListener(this);

		// startAvertarAnimation();
	}

	private void setCurrentUserNmae(String userName) {
		userLogIneEditText.setText(userName);
	}

	public static Bitmap getBitmap(Context context, String resName) {
		try {
			return BitmapFactory.decodeFile(resName);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:// ѡ������ʱ����
			startPhotoZoom(Uri.fromFile(ImgUir.tempFile));
			break;
		case PHOTO_REQUEST_CUT:

			sentPicToNext(data);
			Intent intent = new Intent(this, RegistActivity.class);
			startActivity(intent);
			finish();

			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ��ʼ����ͼƬ
	 * 
	 * @param uri
	 */
	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// cropΪtrue�������ڿ�����intent��������ʾ��view���Լ���
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY �Ǽ���ͼƬ�Ŀ��
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	/**
	 * ���ü���
	 */
	@Override
	public void onClick(View v) {
		if (v == LogInBt) {
			// ��ֱ�ӵ�¼�����ж��û����������Ƿ���ȷ
			checkUser();

		} else if (v == newUserTextView) {
			startActivity(new Intent(LogInActivity.this, RegistActivity.class));
			LogInActivity.this.finish();
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		} else if (v == btByQQ) {
			// ͨ��qqע��
			System.out.println("find111");
			doRegistByQQ();
		}
	}

	private void doRegistByQQ() {
		System.out.println("find222");
		mTencent.logout(this);
		mTencent.login(this, "all", new IUiListener() {

			@Override
			public void onError(UiError arg0) {
				ShowLog("qq error" + arg0);

			}

			@Override
			public void onComplete(Object response) {
				JSONObject jsonObject = (JSONObject) response;
				System.out.println("find333");

				progress.setMessage("���ڵ�¼...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();

				try {
					String openId = jsonObject
							.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);// ���֤

					String token = jsonObject
							.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN); // ��������

					// �Ȳ�ѯ���ݿ��Ƿ�����˻������ڽ�������ȶԣ������ڽ���ע��

					MyUser user = new MyUser();
					user.setUsername(openId);
					user.setPassword(token);

					Message msg = new Message();
					msg.obj = user;
					msg.what = 1;
					mhHandler.sendMessage(msg);

				} catch (Exception e) {
					// TODO: handle exception
				}

				// getInfoByQQ();

				// bindBmob((JSONObject) arg0);

			}

			@Override
			public void onCancel() {
				ShowLog("qq cancel");

			}
		});

	}

	protected void getInfoByQQ(final MyUser user) {
		System.out.println("find444");
		UserInfo mInfo = new UserInfo(this, mTencent.getQQToken());
		mInfo.getUserInfo(new IUiListener() {

			@Override
			public void onError(UiError arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete(Object response) {
				System.out.println("find55");

				// JSONObject json = (JSONObject) response;
				Message msg = new Message();
				msg.what = 0;
				msg.obj = response;
				Bundle bundle = new Bundle();
				bundle.putString("username", user.getUsername());
				bundle.putString("pwd", user.getPassword());
				msg.setData(bundle);
				mhHandler.sendMessage(msg);
				// System.out.println(json);

			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * ��ֱ�ӵ�¼�����ж��û����������Ƿ���ȷ
	 */
	private void checkUser() {
		String userName = userLogIneEditText.getText().toString();
		String password = passwordLogInEditText.getText().toString();

		if (TextUtils.isEmpty(userName)) {
			ShowToast("�˺�Ϊ��");
			return;
		}

		if (TextUtils.isEmpty(password)) {
			ShowToast("����Ϊ��");
			return;
		}

		if (!CommonUtils.isNetworkAvailable(LogInActivity.this)) {
			ShowToast("�豸û������");
			return;
		}

		// final ProgressDialog progress = new
		// ProgressDialog(LogInActivity.this);
		progress.setMessage("���ڵ�½...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();

		MyUser user = new MyUser();
		user.setUsername(userName);
		user.setPassword(password);
		login(user);

	}

	private void login(MyUser user) {
		userManager.login(user, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						progress.setMessage("���ڻ�ȡ�����б�...");
					}
				});
				// �����û��ĵ���λ���Լ����ѵ�����
				updateUserInfos();
				progress.dismiss();
				ShowToast("��½�ɹ�");
				Intent intent = new Intent(LogInActivity.this,
						MainUIActivity.class);

				startActivity(intent);
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				finish();
			}

			@Override
			public void onFailure(int code, String msg) {
				// TODO Auto-generated method stub
				progress.dismiss();

				if (code == 101) {
					ShowToast("���������˺Ų�����");
				}

			}
		});

	}

	// �첽����ͷ��
	private void setAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, logInImageView,
					ImageLoadOptions.getOptions());
		} else {
			logInImageView.setImageResource(R.drawable.default_head);
		}
	}

	/**
	 * ��ע�ᣬ��������
	 */
	public void takePhotos() {
		Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// ָ������������պ���Ƭ�Ĵ���·��
		cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(ImgUir.tempFile));
		startActivityForResult(cameraintent, PHOTO_REQUEST_TAKEPHOTO);
	}

	private void sentPicToNext(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			photo = bundle.getParcelable("data");
			try {
				handleFile.savePictureFile(photo, "cut.jpg");
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			ShowToast("��ȡ��Ƭʧ��");
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
	}

	private Handler mhHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			System.out.println("find666");

			if (msg.what == 0) {

				Bundle data = msg.getData();

				System.out.println("find661");
				JSONObject json = (JSONObject) (msg.obj);
				String avatar = null, nickName = null;
				boolean gender = true;

				try {
					System.out.println("find661");
					if (json.has("nickname")) {
						nickName = json.getString("nickname");
					}

					if (json.has("figureurl_qq_2")) {
						avatar = json.getString("figureurl_qq_2");
					}
					if (json.has("gender")) {
						String temp = json.getString("gender");
						if (temp.equals("Ů")) {
							gender = false;
						}
					}

				} catch (Exception e) {
					System.out.println("find662");
					Log.i("TAG", e.toString());
				}

				final MyUser user = new MyUser();
				user.setUsername(data.getString("username"));
				user.setPassword(data.getString("pwd"));
				user.setAge(20);
				user.setAvatar(avatar);
				user.setNick(nickName);
				user.setSex(gender);
				user.setIsQQ(true);
				System.out.println("find663");

				System.out.println("find664");
				user.signUp(LogInActivity.this, new SaveListener() {
					@Override
					public void onSuccess() {
						System.out.println("find777");
						// ShowToast("ע��ɹ�");
						BmobUserManager
								.getInstance(LogInActivity.this)
								.bindInstallationForRegister(user.getUsername());
						progress.dismiss();

						Intent intent = new Intent(LogInActivity.this,
								MainUIActivity.class);
						intent.putExtra("success", 1);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.zoom_enter,
								R.anim.zoom_exit);
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						System.out.println("find888");
						System.out.println("arg0:" + arg0);
						System.out.println("arg1:" + arg1);
						if (arg0 == 202) {

							ShowToast("�û����Ѵ���");
						}
						progress.dismiss();

					}
				});

			} else if (msg.what == 1) {
				final MyUser user = (MyUser) msg.obj;
				String userName = user.getUsername();

				BmobQuery<MyUser> query = new BmobQuery<MyUser>();

				query.addWhereEqualTo("username", userName);

				// ִ�в�ѯ����

				query.findObjects(LogInActivity.this,
						new FindListener<MyUser>() {

							@Override
							public void onError(int arg0, String arg1) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onSuccess(List<MyUser> users) {
								Log.i("TAG", users.toString());

								if (users.size() != 0) {
									// �����˺�

									login(user);

								} else {
									// �����ھ�ȥע��
									getInfoByQQ(user);

								}

							}
						});

			}

		}
	};
}
