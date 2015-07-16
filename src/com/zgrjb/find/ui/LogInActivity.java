package com.zgrjb.find.ui;

import java.io.File;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.v3.listener.SaveListener;

import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.config.ImgUir;
import com.zgrjb.find.file_handle.HandlePicFile;
import com.zgrjb.find.utils.CommonUtils;

public class LogInActivity extends BaseActivity implements OnClickListener {
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

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_CUT = 2;// 结果
	private File file = new File("sdcard/Find/Picture_Regist");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
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
		Intent intent = getIntent();
		int value = intent.getIntExtra("success", -1);
		logInImageView = (ImageView) this.findViewById(R.id.loginImageView);
		if (!file.exists() || value != 1) {
			Bitmap bm = BitmapFactory.decodeResource(getResources(),
					R.drawable.child);
			logInImageView.setImageBitmap(bm);

		} else {
			logInImageView.setImageBitmap(getBitmap(this, ImgUir.ALBUM_PATH
					+ "cut.jpg"));
		}
		userLogIneEditText = (EditText) this
				.findViewById(R.id.userLogInEditText);
		passwordLogInEditText = (EditText) this
				.findViewById(R.id.passWordLoginEditText);
		newUserTextView = (TextView) findViewById(R.id.newUserTextView);
		LogInBt = (Button) this.findViewById(R.id.logInBt);
		newUserTextView.setOnClickListener(this);
		LogInBt.setOnClickListener(this);
		handleFile = new HandlePicFile(this, ImgUir.ALBUM_PATH);

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
			// 若注册，则先照相
			takePhotos();
		}

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

		final ProgressDialog progress = new ProgressDialog(LogInActivity.this);
		progress.setMessage("正在登陆...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();

		MyUser user = new MyUser();
		user.setUsername(userName);
		user.setPassword(password);
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
}
