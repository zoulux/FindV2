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
	private RadioGroup sexGroup;// ����һ��radioGoup���ж��Ա�
	private RadioButton sexBoy, sexGirl;// ����һ��radioGoup��ѡ���Ա�
	private NumberPicker agePicker;// ����һ��NumberPicker��ѡ������
	private boolean sex = true;// �Ա�ΪtrueʱĬ��Ϊ��
	private int age = 20;// Ĭ�ϳ�ʼ����Ϊ20��
	// ����һ��ȷ��ע��,ȡ��ע��İ�ť
	private Button ensureRegistButton, cancleRegistButton;

	private EditText userName, nick, password, passwordAgain;// �����û����ǳƣ����룬�ظ������editText

	private BroadcastReceiver broadcastReceiver;

	private String userNameString, nickString, passwordString,
			passwordAgainString;
	// ����һ����ͷ����ص�layout
	private LinearLayout linearLayout;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// ����
	private static final int PHOTO_REQUEST_CUT = 2;// ���
	// ����һ��HandlePicFile��
	private HandlePicFile handleFile;
	// ����һ��ע���ͷ��
	private ImageView imageView;
	// �����һ����Ƭ��λͼ
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
	 * ��ʼ��id�ͼ���
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
	 * ��ʼ���㲥������
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
		case PHOTO_REQUEST_TAKEPHOTO:// ѡ������ʱ����
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
	 * ���ü���
	 */
	@Override
	public void onClick(View v) {
		if (v == linearLayout) {
			Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// ָ������������պ���Ƭ�Ĵ���·��
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
				"RegistActivityV2", Context.MODE_PRIVATE); // ˽������
		Editor editor = sharedPreferences.edit();// ��ȡ�༭��
		editor.putBoolean("isCamera", false);
		editor.commit();// �ύ�޸�

		progress = new ProgressDialog(RegistActivity.this);

		final MyUser user = new MyUser();
		user.setUsername(userNameString);
		user.setNick(nickString);
		user.setPassword(passwordString);
		user.setSex(sex);
		user.setAge(age);
		user.setInstallId(BmobInstallation.getInstallationId(this));
		user.setDeviceType("android");

		progress.setMessage("����ע��...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		user.signUp(RegistActivity.this, new SaveListener() {
			@Override
			public void onSuccess() {
				// ShowToast("ע��ɹ�");
				BmobUserManager.getInstance(RegistActivity.this)
						.bindInstallationForRegister(user.getUsername());
				// ���ϴ�ͷ���ļ�
				uploadAvatar();

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				if (arg0 == 202) {

					ShowToast("�û����Ѵ���");
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
				System.out.println("ͼ���ļ��ϴ��ɹ�" + pathAvatar);
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
	 * �ж�ע���Ƿ�ɹ�
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
			ShowToast("��������ͷ��");
			return false;
		}

		if (TextUtils.isEmpty(userNameString)) {
			ShowToast("�������û���");
			return false;
		}

		if (TextUtils.isEmpty(nickString)) {
			ShowToast("�������ǳ�");
			return false;
		}

		if (TextUtils.isEmpty(passwordString)) {
			ShowToast("����������");
			password.setText("");
			return false;
		}
		if (TextUtils.isEmpty(passwordAgainString)) {
			ShowToast("���ظ�����");
			passwordAgain.setText("");
			return false;
		}
		if (!passwordString.equals(passwordAgainString)) {
			ShowToast("�������벻һ��");
			return false;
		}

		// String mat1 = "^[a-zA-Z][a-zA-Z0-9_]{3,15}$";
		String mat = "[a-zA-Z0-9_]{4,15}$";
		if (!userNameString.matches(mat)) {
			ShowToast("�˻�����ʹ��4λ���ϵ���ĸ�����ֵ����");
			return false;
		}

		if (!passwordString.matches(mat)) {
			ShowToast("���뽨��ʹ��4λ���ϵ���ĸ�����ֵ����");
			return false;
		}

		if (!CommonUtils.isNetworkAvailable(RegistActivity.this)) {
			ShowToast("�豸û������");
			return false;
		}

		return true;
	}

	/**
	 * ��ָ��·����ͼƬ����Bitmap������
	 * 
	 * @param context
	 *            ������
	 * @param resName
	 *            �ǻ�ȡͼƬ��·��
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
	 * �����к��ͼƬ��ʾ��RegistĿ¼��
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
							"RegistActivityV2", Context.MODE_PRIVATE); // ˽������
					Editor editor = sharedPreferences.edit();// ��ȡ�༭��
					editor.putBoolean("isCamera", true);
					editor.commit();// �ύ�޸�

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
		// ע��㲥
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
