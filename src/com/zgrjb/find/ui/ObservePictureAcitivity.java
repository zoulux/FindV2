package com.zgrjb.find.ui;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zgrjb.find.R;
import com.zgrjb.find.utils.ImageLoadOptions;
import com.zgrjb.find.view.CustomViewPager;
import com.zgrjb.find.view.ZoomImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class ObservePictureAcitivity extends BaseActivity  {
	//定义一个放大缩小的图片的工具类，从而显示自己发送和接收的图片
    private ZoomImageView zoomImageView;
    //定义一个自定义的视图页面
    private CustomViewPager mPager;
    //定义一个图片的浏览适配器
	private ImageBrowserAdapter mAdapter;
	//定义一张图片的位置，也就是从chartactiity获取的是哪一张图片
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
	 * @author ly
	 *
	 */
private class ImageBrowserAdapter extends PagerAdapter{
		
		private LayoutInflater inflater;
		
		public ImageBrowserAdapter (Context context){
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
	        final ProgressBar progress = (ProgressBar)imageLayout.findViewById(R.id.progress);
	        
	        final String imgUrl = mPhotos.get(position);
	        ImageLoader.getInstance().displayImage(imgUrl, zoomImageView, ImageLoadOptions.getOptions(),new SimpleImageLoadingListener() {
				
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
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					progress.setVisibility(View.GONE);
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
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
	
	

}
