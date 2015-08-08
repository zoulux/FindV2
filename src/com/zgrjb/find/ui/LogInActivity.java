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
import com.zgrjb.find.utils.CircleImageDrawable;
import com.zgrjb.find.utils.CommonUtils;
import com.zgrjb.find.utils.FileServiceFlag;

public class LogInActivity extends BaseActivity implements OnClickListener {
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

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// ����
	private static final int PHOTO_REQUEST_CUT = 2;// ���
	private File file = new File("sdcard/Find/Picture_Regist");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
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
		Intent intent = getIntent();
		int value = intent.getIntExtra("success", -1);
		logInImageView = (ImageView) this.findViewById(R.id.loginImageView);
		if (!file.exists() || value != 1) {
			Bitmap bm = BitmapFactory.decodeResource(getResources(),
					R.drawable.child);
			logInImageView.setImageDrawable(new CircleImageDrawable(bm));

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

		// startAvertarAnimation();
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
		}
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

		final ProgressDialog progress = new ProgressDialog(LogInActivity.this);
		progress.setMessage("���ڵ�½...");
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
}
