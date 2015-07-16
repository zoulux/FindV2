package com.zgrjb.find.ui;

import java.io.File;
import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.config.ImgUir;
import com.zgrjb.find.file_handle.HandlePicFile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class ChoiceAvertarImageActivity extends BaseActivity implements
		OnClickListener {
	// 定义照相按钮
	private Button takePhotoBt;
	// 定义从相册获取的按钮
	private Button getAlbumBt;
	// 定义位图photo
	private Bitmap photo;
	// 定义HandlePicFile
	private HandlePicFile handleFile;

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_CUT = 2;// 裁剪后结果
	private static final int PHOTO_REQUEST_GETALBUM = 3;// 从相册里获取

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choice_avertar_image);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		handleFile = new HandlePicFile(this, ImgUir.ALBUM_PATH);
		initBt();
	}

	/**
	 * 初始化按钮
	 */
	private void initBt() {
		takePhotoBt = (Button) this
				.findViewById(R.id.bt_choice_avertar_takephoto);
		getAlbumBt = (Button) this
				.findViewById(R.id.bt_choice_avertar_getAlbum);
		takePhotoBt.setOnClickListener(this);
		getAlbumBt.setOnClickListener(this);
	}

	/**
	 * 设置点击事件
	 */
	@Override
	public void onClick(View v) {
		if (v == takePhotoBt) {
			setAvertarFromTakePhoto();
		} else if (v == getAlbumBt) {
			setAvertarFromAlbum();
		}
	}

	/**
	 * 从相册里或取照片
	 */
	private void setAvertarFromAlbum() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}

		startActivityForResult(intent, PHOTO_REQUEST_GETALBUM);

	}

	/**
	 * 通过拍照获取照片
	 */
	private void setAvertarFromTakePhoto() {

		Intent showCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		showCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(ImgUir.tempFile));
		startActivityForResult(showCameraIntent, PHOTO_REQUEST_TAKEPHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PHOTO_REQUEST_TAKEPHOTO:
				startPhotoZoom(Uri.fromFile(ImgUir.tempFile));	
				break;
			case PHOTO_REQUEST_CUT:
				sentPicToNext(data);
				break;
			case PHOTO_REQUEST_GETALBUM:
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(
								selectedImage, null, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex("_data");
						String localSelectPath = cursor.getString(columnIndex);
						cursor.close();
						startPhotoZoom(Uri.fromFile(new File(localSelectPath)));
					}else{
						System.out.println("空的》》》》》》》》》》》》》》》》》》》》》");
					}
				}
				break;

			}
		}

	}

	private void upLoadUserImg(String filePath) {
		progress=new ProgressDialog(ChoiceAvertarImageActivity.this);

		progress.setMessage("正在上传...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		
		

		final BmobFile pFile = new BmobFile(new File(filePath));

		pFile.upload(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				String pathAvatar = pFile
						.getFileUrl(ChoiceAvertarImageActivity.this);
				System.out.println("图像文件上传成功" + pathAvatar);
				setUserAvatar(pathAvatar);

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				System.out.println(arg0 + ":" + arg1);

			}
		});

	}

	protected void setUserAvatar(String pathAvatar) {
		MyUser user = new MyUser();
		user.setAvatar(pathAvatar);

		user.update(ChoiceAvertarImageActivity.this,
				userManager.getCurrentUserObjectId(), new UpdateListener() {

					@Override
					public void onSuccess() {
						//设置成功的时候将前面的activity结束掉
						ShowToast("设置成功");
						progress.dismiss();
						finish();
						
						
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub

					}
				});

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
	 * 将裁切好的照片存储起来
	 * 
	 * @param picdata
	 */
	
	
	ProgressDialog progress;
	
	private void sentPicToNext(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			photo = bundle.getParcelable("data");
			try {
				handleFile.savePictureFile(photo, "cut.jpg");
				upLoadUserImg(ImgUir.ALBUM_PATH + "cut.jpg");
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			ShowToast("获取照片失败");
		}
	}

}
