package com.zgrjb.find.ui;

import java.io.File;
import java.io.IOException;
import com.zgrjb.find.R;
import com.zgrjb.find.file_handle.HandlePicFile;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.config.ImgUir;
import com.zgrjb.find.utils.CommonUtils;

public class RegistActivity extends BaseActivity implements OnClickListener {
	private EditText userName, nick, password, passwordAgain;// 定义用户民，昵称，密码，重复密码的editText
	private BroadcastReceiver broadcastReceiver;

	private String userNameString, nickString, passwordString,
			passwordAgainString;
	// 定义一个与头像相关的layout
	private LinearLayout linearLayout;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_CUT = 2;// 结果
	// 定义一个HandlePicFile类
	private HandlePicFile handleFile;
	// 定义一个注册的头像
	private ImageView imageView;
	// 定义的一个照片的位图
	private Bitmap photo;

	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		File file = new File("/sdcard/Find/Picture_Regist/");
		if (!file.exists()) {
			file.mkdirs();
		}
		init();
		initBroadcast();
		initRightTitleBarSet();

	}

	/**
	 * 初始化标题栏右边的按钮和监听
	 */
	private void initRightTitleBarSet() {
		setDrawablePath(getResources().getDrawable(R.drawable.jiantou));
		rightButtonIsVisible(true);
		showTitleText("注册");
		rightImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isOk()) {
					Intent intent = new Intent(RegistActivity.this,
							RegistFinalActivity.class);
					intent.putExtra("userNameString", userNameString);
					intent.putExtra("nickString", nickString);
					intent.putExtra("passwordString", passwordString);
					startActivity(intent);

				}

			}
		});
	}

	/**
	 * 初始化id和监听
	 */
	public void init() {
		// ensureRegistButton = (Button) this.findViewById(R.id.ensureRegist);
		linearLayout = (LinearLayout) this
				.findViewById(R.id.LinerLayoutToImageView);
		// cancleRegistButton = (Button) this.findViewById(R.id.cancleRegist);
		imageView = (ImageView) this.findViewById(R.id.pictureRegist);
		// ensureRegistButton.setOnClickListener(this);
		linearLayout.setOnClickListener(this);

		// cancleRegistButton.setOnClickListener(this);
		handleFile = new HandlePicFile(this, ImgUir.ALBUM_PATH);
		imageView
				.setImageBitmap(getBitmap(this, ImgUir.ALBUM_PATH + "cut.jpg"));

		userName = (EditText) findViewById(R.id.id_regist_userName);
		nick = (EditText) findViewById(R.id.id_regist_nick);
		password = (EditText) findViewById(R.id.id_regist_ps);
		passwordAgain = (EditText) findViewById(R.id.id_regist_psAgain);

	}

	/**
	 * 初始化广播接收者
	 */
	private void initBroadcast() {
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				RegistActivity.this.finish();
			}
		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:// 选择拍照时调用
			startPhotoZoom(Uri.fromFile(ImgUir.tempFile));
			break;
		case PHOTO_REQUEST_CUT:
			if (data != null) {
				Bitmap bmpBitmap = getBitmap(this, ImgUir.ALBUM_PATH
						+ "cut.jpg");
				imageView.setImageBitmap(bmpBitmap);
				sentPicToNext(data);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 设置监听
	 */
	@Override
	public void onClick(View v) {
		if (v == linearLayout) {
			Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 指定调用相机拍照后照片的储存路径
			cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(ImgUir.tempFile));
			startActivityForResult(cameraintent, PHOTO_REQUEST_TAKEPHOTO);
		}

	}

	/**
	 * 判断注册是否成功
	 * 
	 * @return
	 */
	private boolean isOk() {
		userNameString = userName.getText().toString();
		nickString = nick.getText().toString();
		passwordString = password.getText().toString();
		passwordAgainString = passwordAgain.getText().toString();

		if (TextUtils.isEmpty(userNameString)) {
			ShowToast("请输入用户名");
			return false;
		}

		if (TextUtils.isEmpty(nickString)) {
			ShowToast("请输入昵称");
			return false;
		}

		if (TextUtils.isEmpty(passwordString)) {
			ShowToast("请输入密码");
			password.setText("");
			return false;
		}
		if (TextUtils.isEmpty(passwordAgainString)) {
			ShowToast("请重复密码");
			passwordAgain.setText("");
			return false;
		}
		if (!passwordString.equals(passwordAgainString)) {
			ShowToast("两次密码不一致");
			return false;
		}

		// String mat1 = "^[a-zA-Z][a-zA-Z0-9_]{3,15}$";
		String mat = "[a-zA-Z0-9_]{4,15}$";
		if (!userNameString.matches(mat)) {
			ShowToast("账户建议使用4位以上的字母加数字的组合");
			return false;
		}

		if (!passwordString.matches(mat)) {
			ShowToast("密码建议使用4位以上的字母加数字的组合");
			return false;
		}

		if (!CommonUtils.isNetworkAvailable(RegistActivity.this)) {
			ShowToast("设备没有网络");
			return false;
		}

		return true;
	}

	/**
	 * 将指定路径的图片交给Bitmap来处理
	 * 
	 * @param context
	 *            上下文
	 * @param resName
	 *            是获取图片的路径
	 * @return
	 */
	public static Bitmap getBitmap(Context context, String resName) {
		try {
			return BitmapFactory.decodeFile(resName);
		} catch (Exception e) {
			return null;
		}
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
	 * 将裁切后的图片显示在Regist目录上
	 * 
	 * @param picdata
	 */
	private void sentPicToNext(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			photo = bundle.getParcelable("data");
			if (photo == null) {
				imageView.setImageResource(R.drawable.ic_launcher);
			} else {
				try {
					handleFile.savePictureFile(photo, "cut.jpg");
				} catch (IOException e) {
					e.printStackTrace();
				}
				imageView.setImageBitmap(photo);
			}

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("finish_RegistActivity");
		registerReceiver(broadcastReceiver, intentFilter);
	}

}
