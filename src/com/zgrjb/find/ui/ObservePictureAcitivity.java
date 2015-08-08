package com.zgrjb.find.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zgrjb.find.R;
import com.zgrjb.find.utils.CustomApplcation;
import com.zgrjb.find.utils.ImageLoadOptions;
import com.zgrjb.find.view.CustomViewPager;
import com.zgrjb.find.view.ZoomImageView;

public class ObservePictureAcitivity extends BaseActivity {

	String imgUrl;

	// 定义一个放大缩小的图片的工具类，从而显示自己发送和接收的图片
	private ZoomImageView zoomImageView;
	// 定义一个自定义的视图页面
	private CustomViewPager mPager;
	// 定义一个图片的浏览适配器
	private ImageBrowserAdapter mAdapter;
	// 定义一张图片的位置，也就是从chartactiity获取的是哪一张图片
	private int mPosition;

	private ArrayList<String> mPhotos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_observe_picture);
		init();

	}

	/**
	 * 初始化
	 */
	private void init() {
		mPhotos = getIntent().getStringArrayListExtra("photos");
		mPosition = getIntent().getIntExtra("position", 0);
		initViews();
	}

	/**
	 * 初始化视图
	 */
	protected void initViews() {
		mPager = (CustomViewPager) findViewById(R.id.pagerview);
		mAdapter = new ImageBrowserAdapter(this);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(mPosition, false);

	}

	/**
	 * 定义一个图片浏览适配器的类
	 * 
	 * @author ly
	 * 
	 */
	private class ImageBrowserAdapter extends PagerAdapter {

		private LayoutInflater inflater;

		public ImageBrowserAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mPhotos.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {

			View imageLayout = inflater.inflate(R.layout.item_show_picture,
					container, false);
			zoomImageView = (ZoomImageView) imageLayout
					.findViewById(R.id.photoview);

			initBuilder();

			final ProgressBar progress = (ProgressBar) imageLayout
					.findViewById(R.id.progress);

			imgUrl = mPhotos.get(position);

			ImageLoader.getInstance().displayImage(imgUrl, zoomImageView,
					ImageLoadOptions.getOptions(),
					new SimpleImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							progress.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							progress.setVisibility(View.GONE);

						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							progress.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							progress.setVisibility(View.GONE);

						}
					});

			container.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
	}

	public void initBuilder() {
		builder = new AlertDialog.Builder(ObservePictureAcitivity.this);
		builder.setTitle("温馨提示");
		builder.setIcon(R.drawable.pikaqiu);
		builder.setMessage("保存到手机");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				ShowToast("文件保存在：" + new File(imgUrl).getPath());

				// File file = new
				// File(Environment.getExternalStorageDirectory()
				// + "/a2.jpg");
				// File file1 = new File(imgUrl);
				// System.out.println(imgUrl);
				// System.out.println(file1.getAbsolutePath());
				// System.out.println(file1.getPath());
				// String path = file1.getPath();
				// path = path.substring(path.indexOf("storage"));
				// System.out.println(path.substring(path.indexOf("storage")));
				//
				// try {
				// copyFile(path, file.getAbsolutePath());
				//
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});

	}

	private final static String TAG = "TAG";

	public static boolean copyFile(String srcFileName, String destFileName)
			throws IOException {
		Log.d(TAG, "copyFile, begin");
		File srcFile = new File(srcFileName);
		File destFile = new File(destFileName);
		if (!srcFile.exists()) {
			Log.d(TAG, "copyFile, source file not exist.");
			return false;
		}
		if (!srcFile.isFile()) {
			Log.d(TAG, "copyFile, source file not a file.");
			return false;
		}
		if (!srcFile.canRead()) {
			Log.d(TAG, "copyFile, source file can't read.");
			return false;
		}
		if (destFile.exists()) {
			Log.d(TAG, "copyFile, before copy File, delete first.");
			destFile.delete();
		}

		try {
			InputStream inStream = new FileInputStream(srcFile);
			FileOutputStream outStream = new FileOutputStream(destFile);
			byte[] buf = new byte[1024];
			int byteRead = 0;
			while ((byteRead = inStream.read(buf)) != -1) {
				outStream.write(buf, 0, byteRead);
			}
			outStream.flush();
			outStream.close();
			inStream.close();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}

		saveImgToGallery(destFileName);
		Log.d(TAG, "copyFile, success");
		return true;
	}

	public static boolean CopyFile(String fromFile, String toFile) {
		try {
			InputStream fosfrom = new FileInputStream(fromFile);
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[4096];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			bt = null;
			System.out.println("success...");
			return true;

		} catch (Exception ex) {
			System.out.println("faild...");
			return false;
		}
	}

	public static boolean saveImgToGallery(String fileName) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (!sdCardExist)
			return false;

		try {

			ContentValues values = new ContentValues();
			values.put("datetaken", new Date().toString());
			values.put("mime_type", "image/png");
			values.put("_data", fileName);

			Application app = CustomApplcation.getInstance();
			ContentResolver cr = app.getContentResolver();
			cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	AlertDialog.Builder builder;

}
