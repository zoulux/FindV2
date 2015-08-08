package com.zgrjb.find.ui;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.config.ImgUir;
import com.zgrjb.find.file_handle.HandlePicFile;
import com.zgrjb.find.utils.CircleImageDrawable;
import com.zgrjb.find.utils.CommonUtils;
import com.zgrjb.find.utils.FileServiceFlag;

public class RegistActivity extends BaseActivity implements OnClickListener {
	private RadioGroup sexGroup;// 定义一个radioGoup来判断性别
	private RadioButton sexBoy, sexGirl;// 定义一个radioGoup来选择性别
	private NumberPicker agePicker;// 定义一个NumberPicker来选择年龄
	private boolean sex = true;// 性别为true时默认为男
	private int age = 20;// 默认初始年龄为20岁
	// 定义一个确定注册,取消注册的按钮
	private Button ensureRegistButton, cancleRegistButton;

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

	private FileServiceFlag fileService;

	File file = new File("/sdcard/Find/Picture_Regist/");

	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		if (!file.exists()) {
			file.mkdirs();
		}
		init();
		dealWithAvarter();
		initBroadcast();
	}

	private void dealWithAvarter() {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.child);
		imageView.setImageDrawable(new CircleImageDrawable(bitmap));
	}

	/**
	 * 初始化id和监听
	 */
	public void init() {

		sexBoy = (RadioButton) this.findViewById(R.id.id_regist_RaddioBoy);
		sexGirl = (RadioButton) this.findViewById(R.id.id_regist_RaddioGirl);
		agePicker = (NumberPicker) this.findViewById(R.id.id_regist_agePicker);
		sexGroup = (RadioGroup) findViewById(R.id.id_regist_RadioGroup);

		ensureRegistButton = (Button) this.findViewById(R.id.ensureRegist);
		cancleRegistButton = (Button) this.findViewById(R.id.cancleRegist);
		ensureRegistButton.setOnClickListener(this);
		cancleRegistButton.setOnClickListener(this);

		initRadioGroup();
		initNumberPicker();

		fileService = new FileServiceFlag(RegistActivity.this);
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

	private void initNumberPicker() {

		agePicker.setMinValue(1);
		agePicker.setMaxValue(99);
		agePicker.setValue(20);
		agePicker.getChildAt(0).setFocusable(false);

		agePicker.setFormatter(new Formatter() {

			@Override
			public String format(int value) {
				String tmpStr = String.valueOf(value);
				if (value < 10) {
					tmpStr = "0" + tmpStr;
				}
				return tmpStr;
			}
		});

		agePicker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				age = newVal;

			}
		});

	}

	private void initRadioGroup() {
		sexGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkId) {
				if (checkId == sexBoy.getId()) {
					sex = true;
				}
				if (checkId == sexGirl.getId()) {
					sex = false;
				}

			}
		});

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
		} else if (v == ensureRegistButton) {
			if (isOk()) {
				registeToServer();
			}

		} else if (v == cancleRegistButton) {
			Intent intent = new Intent(RegistActivity.this, LogInActivity.class);
			intent.putExtra("failed", 0);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.quit_zoom_enter,
					R.anim.quit_zoom_exit);

		}

	}

	ProgressDialog progress;

	private void registeToServer() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"RegistActivityV2", Context.MODE_PRIVATE); // 私有数据
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putBoolean("isCamera", false);
		editor.commit();// 提交修改

		progress = new ProgressDialog(RegistActivity.this);

		final MyUser user = new MyUser();
		user.setUsername(userNameString);
		user.setNick(nickString);
		user.setPassword(passwordString);
		user.setSex(sex);
		user.setAge(age);
		user.setInstallId(BmobInstallation.getInstallationId(this));
		user.setDeviceType("android");

		progress.setMessage("正在注册...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		user.signUp(RegistActivity.this, new SaveListener() {
			@Override
			public void onSuccess() {
				// ShowToast("注册成功");
				BmobUserManager.getInstance(RegistActivity.this)
						.bindInstallationForRegister(user.getUsername());
				// 先上传头像文件
				uploadAvatar();

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				if (arg0 == 202) {

					ShowToast("用户名已存在");
				}
				progress.dismiss();

			}
		});

	}

	protected void uploadAvatar() {
		String filePath = ImgUir.ALBUM_PATH + "cut.jpg";

		final BmobFile pFile = new BmobFile(new File(filePath));
		pFile.upload(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				String pathAvatar = pFile.getFileUrl(RegistActivity.this);
				System.out.println("图像文件上传成功" + pathAvatar);
				updateMyAvatar(pathAvatar);

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				System.out.println(arg0 + ":" + arg1);

			}
		});

	}

	protected void updateMyAvatar(String path) {
		MyUser user = new MyUser();
		user.setAvatar(path);
		user.update(RegistActivity.this, BmobChatUser.getCurrentUser

		(RegistActivity.this).getObjectId(), new UpdateListener() {

			@Override
			public void onSuccess() {
				progress.dismiss();
				Intent intent = new Intent(RegistActivity.this,
						MainUIActivity.class);
				intent.putExtra("success", 1);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * 判断注册是否成功
	 * 
	 * @return
	 */
	private boolean isOk() {
		SharedPreferences share = getSharedPreferences("RegistActivityV2",
				Activity.MODE_PRIVATE);
		boolean isCamera = share.getBoolean("isCamera", false);

		System.out.println("isCamera>>>" + isCamera);

		userNameString = userName.getText().toString();
		nickString = nick.getText().toString();
		passwordString = password.getText().toString();
		passwordAgainString = passwordAgain.getText().toString();

		if (!isCamera) {
			ShowToast("请先拍摄头像");
			return false;
		}

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

					SharedPreferences sharedPreferences = getSharedPreferences(
							"RegistActivityV2", Context.MODE_PRIVATE); // 私有数据
					Editor editor = sharedPreferences.edit();// 获取编辑器
					editor.putBoolean("isCamera", true);
					editor.commit();// 提交修改

				} catch (IOException e) {
					e.printStackTrace();
				}
				Bitmap bmpBitmap = getBitmap(this, ImgUir.ALBUM_PATH
						+ "cut.jpg");
				imageView.setImageDrawable(new CircleImageDrawable(bmpBitmap));
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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
	}

}
