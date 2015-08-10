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

	// 初始化登录界面的头像
	private ImageView logInImageView;
	// 定义一个用户名的输入的edittext
	private EditText userLogIneEditText;
	// 定义一个密码输入的edittext
	private EditText passwordLogInEditText;
	// 定义一个进入主界面的button
	private Button LogInBt;
	// 定义一个新用户的textview
	private TextView newUserTextView;
	// 定义一个HandlePicFile类，进行图片的处理
	private HandlePicFile handleFile;
	// 定义一个相片的位图
	private Bitmap photo;

	private Button btByQQ;

	private ProgressDialog progress = null;

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_CUT = 2;// 结果
	private File file = new File("sdcard/Find/Picture_Regist");

	private static String APP_ID = "1104804412";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mTencent = Tencent.createInstance(APP_ID, this);
		progress = new ProgressDialog(LogInActivity.this);
		showTitleText("登录");
		init();

		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 初始化id，监听和头像的选择
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
		case PHOTO_REQUEST_TAKEPHOTO:// 选择拍照时调用
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
	 * 开始裁切图片
	 * 
	 * @param uri
	 */
	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	/**
	 * 设置监听
	 */
	@Override
	public void onClick(View v) {
		if (v == LogInBt) {
			// 若直接登录，则判断用户名和密码是否正确
			checkUser();

		} else if (v == newUserTextView) {
			startActivity(new Intent(LogInActivity.this, RegistActivity.class));
			LogInActivity.this.finish();
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		} else if (v == btByQQ) {
			// 通过qq注册
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

				progress.setMessage("正在登录...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();

				try {
					String openId = jsonObject
							.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);// 身份证

					String token = jsonObject
							.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN); // 访问令牌

					// 先查询数据库是否存在账户，存在进行密码比对，不存在进行注册

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
	 * 若直接登录，则判断用户名和密码是否正确
	 */
	private void checkUser() {
		String userName = userLogIneEditText.getText().toString();
		String password = passwordLogInEditText.getText().toString();

		if (TextUtils.isEmpty(userName)) {
			ShowToast("账号为空");
			return;
		}

		if (TextUtils.isEmpty(password)) {
			ShowToast("密码为空");
			return;
		}

		if (!CommonUtils.isNetworkAvailable(LogInActivity.this)) {
			ShowToast("设备没有网络");
			return;
		}

		// final ProgressDialog progress = new
		// ProgressDialog(LogInActivity.this);
		progress.setMessage("正在登陆...");
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
						progress.setMessage("正在获取好友列表...");
					}
				});
				// 更新用户的地理位置以及好友的资料
				updateUserInfos();
				progress.dismiss();
				ShowToast("登陆成功");
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
					ShowToast("密码错误或账号不存在");
				}

			}
		});

	}

	// 异步加载头像
	private void setAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, logInImageView,
					ImageLoadOptions.getOptions());
		} else {
			logInImageView.setImageResource(R.drawable.default_head);
		}
	}

	/**
	 * 若注册，则先照相
	 */
	public void takePhotos() {
		Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 指定调用相机拍照后照片的储存路径
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
			ShowToast("获取照片失败");
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
						if (temp.equals("女")) {
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
						// ShowToast("注册成功");
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

							ShowToast("用户名已存在");
						}
						progress.dismiss();

					}
				});

			} else if (msg.what == 1) {
				final MyUser user = (MyUser) msg.obj;
				String userName = user.getUsername();

				BmobQuery<MyUser> query = new BmobQuery<MyUser>();

				query.addWhereEqualTo("username", userName);

				// 执行查询方法

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
									// 存在账号

									login(user);

								} else {
									// 不存在就去注册
									getInfoByQQ(user);

								}

							}
						});

			}

		}
	};
}
