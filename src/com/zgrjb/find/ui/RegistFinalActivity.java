package com.zgrjb.find.ui;

import java.io.File;

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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TimePicker;

public class RegistFinalActivity extends BaseActivity implements
		OnClickListener {
	private RadioGroup sexGroup;// 定义一个radioGoup来判断性别
	private RadioButton sexBoy, sexGirl;// 定义一个radioGoup来选择性别
	private NumberPicker agePicker;// 定义一个NumberPicker来选择年龄
	// 从registactivity里获取用户名，昵称和密码
	private String getUserName, getUserNick, getUserPassWord;
	private boolean sex = true;// 性别为true时默认为男
	private int age = 20;// 默认初始年龄为22岁

	// 定义一个确定注册的按钮
	private Button ensureRegistButton;

	// 定义一个取消注册的按钮
	private Button cancleRegistButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_regist_final);
		init();
		initIntent();
	}

	/**
	 * 初始化从Registactivity里获取的值
	 */
	private void initIntent() {
		Intent intent = getIntent();
		getUserName = intent.getStringExtra("userNameString");
		getUserNick = intent.getStringExtra("nickString");
		getUserPassWord = intent.getStringExtra("passwordString");

	}

	/**
	 * 初始化id和监听
	 */
	private void init() {
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
	}

	/**
	 * 初始化年您选择器
	 */
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

	/**
	 * 初始化性别选择的radioGroup
	 */
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
	 * 将填好的信息上传到服务器完成注册
	 */

	ProgressDialog progress;

	private void registeToServer() {

		// final ProgressDialog progress = new
		// ProgressDialog(RegistActivity.this);
		progress = new ProgressDialog(RegistFinalActivity.this);

		final MyUser user = new MyUser();
		user.setUsername(getUserName);
		user.setNick(getUserNick);
		user.setPassword(getUserPassWord);
		user.setSex(sex);
		user.setAge(age);
		user.setInstallId(BmobInstallation.getInstallationId(this));
		user.setDeviceType("android");

		progress.setMessage("正在注册...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		user.signUp(RegistFinalActivity.this, new SaveListener() {
			@Override
			public void onSuccess() {
				// ShowToast("注册成功");
				BmobUserManager.getInstance(RegistFinalActivity.this)
						.bindInstallationForRegister(user.getUsername());
				// 先上传头像文件
				uploadAvatar();

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				if (arg0 == 202) {
					ShowToast("用户名已存在");
				}

			}
		});

	}

	/**
	 * 上传头像
	 */
	protected void uploadAvatar() {
		String filePath = ImgUir.ALBUM_PATH + "cut.jpg";
		final BmobFile pFile = new BmobFile(new File(filePath));
		pFile.upload(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				String pathAvatar = pFile.getFileUrl(RegistFinalActivity.this);
				System.out.println("图像文件上传成功" + pathAvatar);
				updateMyAvatar(pathAvatar);

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				System.out.println(arg0 + ":" + arg1);

			}
		});

	}

	/**
	 * 更新头像
	 * 
	 * @param path
	 */
	protected void updateMyAvatar(String path) {

		MyUser user = new MyUser();
		user.setAvatar(path);
		user.update(RegistFinalActivity.this, BmobChatUser.getCurrentUser

		(RegistFinalActivity.this).getObjectId(), new UpdateListener() {

			@Override
			public void onSuccess() {
				progress.dismiss();
				Intent intent = new Intent(RegistFinalActivity.this,
						MainUIActivity.class);
				intent.putExtra("success", 1);
				startActivity(intent);
				finish();

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * 监听的设置
	 */
	@Override
	public void onClick(View v) {
		if (v == ensureRegistButton) {
			registeToServer();
			Intent intent = new Intent("finish_RegistActivity");
			sendBroadcast(intent);
		} else if (v == cancleRegistButton) {
			Intent intent = new Intent(RegistFinalActivity.this,
					LogInActivity.class);
			intent.putExtra("failed", 0);
			startActivity(intent);
			finish();

		}
	}
}
