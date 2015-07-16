package com.zgrjb.find.ui.guide;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zgrjb.find.R;
import com.zgrjb.find.adapter.GuideViewPagerAdapter;
import com.zgrjb.find.ui.BaseActivity;
import com.zgrjb.find.ui.InitializeActivity;
import com.zgrjb.find.ui.LogInActivity;

public class ViewGuideActivity extends Activity implements OnClickListener, OnPageChangeListener {
	// 定义一个viewpager与xml文件里的viewpager相对应
	private ViewPager viewPager;
	// 定义一个GuideViewPagerAdapter适配器
	private GuideViewPagerAdapter vpAdapter;
	// 定义list来接受6个view
	private ArrayList<View> views;
	// 底下 的小圆点
	private ImageView[] points;
	// 小圆点的当前的位置
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_view_guide);
		// showTitleText("Find");
		initView();
		initData();
		// 若是第一次使用该app则进入该acticity
		SharedPreferences preferences = getSharedPreferences("first_pref",
				MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("isFirstIn", false);
		editor.commit();

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		views = new ArrayList<View>();
		viewPager = (ViewPager) this.findViewById(R.id.viewpager);
		LayoutInflater inflater = getLayoutInflater();

		views.add(inflater.inflate(R.layout.guide_item_1, null));
		views.add(inflater.inflate(R.layout.guide_item_2, null));
		views.add(inflater.inflate(R.layout.guide_item_3, null));
		views.add(inflater.inflate(R.layout.guide_item_4, null));
		views.add(inflater.inflate(R.layout.guide_item_5, null));
	//	views.add(inflater.inflate(R.layout.guide_item_6, null));
		vpAdapter = new GuideViewPagerAdapter(views);
	}

	/**
	 * 初始化所需要的数据
	 */
	private void initData() {
		viewPager.setAdapter(vpAdapter);
		viewPager.setOnPageChangeListener(this);
		initPoint();

	}

	/**
	 * 初始化小圆点
	 */
	private void initPoint() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);

		points = new ImageView[4];

		// 循环取得小点图片
		for (int i = 0; i < 4; i++) {
			// 得到一个LinearLayout下面的每一个子元素
			points[i] = (ImageView) linearLayout.getChildAt(i);
			// 默认都设为灰色
			points[i].setEnabled(true);
			// 给每个小点设置监听
			points[i].setOnClickListener(this);
			// 设置位置tag，方便取出与当前位置对应
			points[i].setTag(i);
		}

		// 设置当面默认的位置
		currentIndex = 0;
		// 设置为白色，即选中状态
		points[currentIndex].setEnabled(false);
	}

	@Override
	public void onPageScrollStateChanged(int position) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onPageSelected(int position) {
		// 设置底部小点选中状态
		setCurDot(position);
		
		
		if (position==4) {
			mHandler.sendEmptyMessageAtTime(0, 1000);
			
		}
		
		
		
	/*	
		if (position == 5) {
			setDrawablePath(getResources().getDrawable(R.drawable.right_bt1));
			rightButtonIsVisible(true);
			rightImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					startActivity(new Intent(ViewGuideActivity.this,
							InitializeActivity.class));
					ViewGuideActivity.this.finish();
				}
			});
		}
		*/

	}


	/**
	 * 当点击小圆点的时候，页面发生变化和小圆点发生变化
	 */
	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);

	}

	/**
	 * 设置当前页面的位置
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= 4) {
			return;
		}
		viewPager.setCurrentItem(position);
	}

	/**
	 * 设置当前的小点的位置
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > 4 - 1 || currentIndex == positon) {
			return;
		}
		points[positon].setEnabled(false);
		points[currentIndex].setEnabled(true);

		currentIndex = positon;
	}
	
	Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			
			startActivity(new Intent(ViewGuideActivity.this,
					InitializeActivity.class));
			ViewGuideActivity.this.finish();
			
		};
		
	};

}
