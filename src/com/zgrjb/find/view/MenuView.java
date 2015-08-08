package com.zgrjb.find.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class MenuView extends ViewGroup {
	// 中间可点击的按钮
	private View mCButton;
	// 判断菜单是否打开，默认为关闭状态
	private Status mCurrentStatus = Status.CLOSE;
	private InitView initView;
	private onMenuItemClickListener menuItemClickListener;

	/**
	 * 点击子菜单项的回调接口
	 * 
	 * @author ly
	 * 
	 */
	public interface onMenuItemClickListener {
		void onClick(View view, int position);
	}

	public onMenuItemClickListener getMenuItemClickListener() {
		return menuItemClickListener;
	}

	public void setOnMenuItemClickListener(
			onMenuItemClickListener menuItemClickListener) {
		this.menuItemClickListener = menuItemClickListener;
	}

	public enum Status {
		CLOSE, OPEN
	}

	public MenuView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public MenuView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public MenuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 测量各个控件的宽高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 各个控件的摆放位置
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			layoutCButton();
			int childCount = getChildCount();
			for (int i = 0; i < childCount - 1; i++) {
				View child = getChildAt(i);

				child.setVisibility(View.GONE);
				int childWidth = child.getMeasuredWidth();
				int childHeight = child.getMeasuredHeight();
				int cl = 0, ct = 0;
				switch (i) {
				case 0:
					cl = getMeasuredWidth() / 4 - childWidth / 2;
					ct = getMeasuredHeight() / 4 - childHeight / 2;
					break;
				case 1:
					cl = getMeasuredWidth() / 2 + getMeasuredWidth() / 4
							- childWidth / 2;
					ct = getMeasuredHeight() / 4 - childHeight / 2;
					break;
				case 2:
					cl = getMeasuredWidth() / 4 - childWidth / 2;
					ct = getMeasuredHeight() / 2 + getMeasuredHeight() / 4
							- childHeight / 2;
					break;
				case 3:
					cl = getMeasuredWidth() / 2 + getMeasuredWidth() / 4
							- childWidth / 2;
					ct = getMeasuredHeight() / 2 + getMeasuredHeight() / 4
							- childHeight / 2;
					break;
				}
				child.layout(cl, ct, childWidth + cl, childHeight + ct);
			}
		}
	}

	/**
	 * 中间按钮的位置及其点击事件的控制
	 */
	private void layoutCButton() {
		mCButton = getChildAt(4);
		mCButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				 rotateCButton(v, 0f, 360f, 300);
				toggle(300);
				initView.setVisibility(View.GONE);
				
			}
		});
		int mCBtWidth = mCButton.getMeasuredWidth();
		int mCBtHeight = mCButton.getMeasuredHeight();
		int l = getMeasuredWidth() / 2 - mCBtWidth / 2;
		int t = getMeasuredHeight() / 2 - mCBtHeight / 2;
		mCButton.layout(l, t, mCBtWidth + l, mCBtHeight + t);
	}

	/**
	 * 开辟一个公有的方法，来传递initView
	 * 
	 * @param initView
	 */
	public void GetValue(InitView initView) {
		this.initView = initView;
	}

	/**
	 * 切换菜单
	 * 
	 * @param duration
	 *            时间
	 */
	@SuppressLint("NewApi")
	private void toggle(int duration) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount - 1; i++) {
			final View child = getChildAt(i);
			int startL = 0, startT = 0;
			switch (i) {
			case 0:
				startL = getMeasuredWidth() / 4 - child.getMeasuredWidth() / 2;
				startT = getMeasuredHeight() / 4 - child.getMeasuredHeight()
						/ 2;
				break;
			case 1:
				startL = -getMeasuredWidth() / 2 + getMeasuredWidth() / 4
						+ child.getMeasuredWidth() / 2;
				startT = getMeasuredHeight() / 4 - child.getMeasuredHeight()
						/ 2;
				break;
			case 2:
				startL = getMeasuredWidth() / 4 - child.getMeasuredWidth() / 2;
				startT = -getMeasuredHeight() / 2 + getMeasuredHeight() / 4
						+ child.getMeasuredHeight() / 2;
				break;
			case 3:
				startL = -getMeasuredWidth() / 2 + getMeasuredWidth() / 4
						+ child.getMeasuredWidth() / 2;
				startT = -getMeasuredHeight() / 2 + getMeasuredHeight() / 4
						+ child.getMeasuredHeight() / 2;
				break;
			}
			AnimationSet aniSet = new AnimationSet(true);
			Animation trans = null;

			if (mCurrentStatus == Status.CLOSE) {

				trans = new TranslateAnimation(startL, 0, startT, 0);
				child.setAlpha(1.0f);
				child.setClickable(true);
				child.setFocusable(true);

			} else {

				trans = new TranslateAnimation(0, startL, 0, startT);
				child.setClickable(false);
				child.setFocusable(false);
			}

			trans.setFillAfter(true);
			trans.setDuration(duration);
			trans.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					if (mCurrentStatus == Status.CLOSE) {
						child.setVisibility(View.GONE);
						child.setAlpha(0.5f);
					}
				}
			});

			child.startAnimation(trans);
			final int pos = i;
			child.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					menuItemClickListener.onClick(child, pos);
					menuItemAnim(pos);
					recoverItem();
					scaleSmallCBt(300, mCButton);
					changeStatus();

				}
			});
		}
		changeStatus();
	}

	/**
	 * 各个选项的动画
	 * 
	 * @param pos
	 *            各个选项的位置
	 */
	private void menuItemAnim(int pos) {
		for (int i = 0; i < getChildCount() - 1; i++) {
			View childView = getChildAt(i);
			if (i == pos) {
				childView.startAnimation(scaleBigAnim(300));

			} else {
				childView.startAnimation(scaleSmallAnim(300));
			}
			childView.setClickable(false);
			childView.setFocusable(false);
		}

	}

	/*
	 * 为当前点击的Item设置变大或变小和透明度的变化
	 */

	private Animation scaleSmallAnim(int duration) {
		AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);
		animSet.addAnimation(scaleAnim);
		animSet.addAnimation(alphaAnim);
		animSet.setFillAfter(true);
		animSet.setDuration(duration);
		return animSet;
	}

	private Animation scaleBigAnim(int duration) {
		AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);
		animSet.addAnimation(scaleAnim);
		animSet.addAnimation(alphaAnim);
		animSet.setFillAfter(true);
		animSet.setDuration(duration);
		return animSet;
	}

	private void rotateCButton(View v, float start, float end, int duration) {
		RotateAnimation ani = new RotateAnimation(start, end,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ani.setDuration(duration);
		ani.setFillAfter(true);// 转完之后就停止
		v.startAnimation(ani);

	}

	/**
	 * 当点击某个选项时，过段时间后就恢复原来的样子
	 */
	private void recoverItem() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				initView.setVisibility(View.VISIBLE);
			}
		}, 300);
	}

	private void changeStatus() {
		mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN
				: Status.CLOSE);

	}

	private void scaleSmallCBt(int duration, View v) {
		ScaleAnimation scaleAnim = new ScaleAnimation(4.0f, 1.0f, 4.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleAnim.setDuration(duration);
		scaleAnim.setFillAfter(true);
		v.startAnimation(scaleAnim);

	}

	private void scaleBigCBt(int duration, View v) {
		ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleAnim.setDuration(duration);
		scaleAnim.setFillAfter(true);
		v.startAnimation(scaleAnim);

	}

}
