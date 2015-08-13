package com.zgrjb.find.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.zgrjb.find.utils.PxAndDpUtil;

public class AddAndScanView extends ViewGroup {
	private Status mCrurentStatus = Status.CLOSE;
	private onRightTopBarMenuItemClickListener mRightTopBarMenuItemClickListener;
	private boolean isOpen;
	private Context context;

	public enum Status {
		OPEN, CLOSE;
	};

	public interface onRightTopBarMenuItemClickListener {
		public void onClick(View v, int position);
	}

	public onRightTopBarMenuItemClickListener getRightTopBarMenuItemClickListener() {
		return mRightTopBarMenuItemClickListener;
	}

	public void setRightTopBarMenuItemClickListener(
			onRightTopBarMenuItemClickListener rightTopBarMenuItemClickListener) {
		mRightTopBarMenuItemClickListener = rightTopBarMenuItemClickListener;
	}

	public AddAndScanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				child.setVisibility(View.GONE);
				int childWidth = child.getMeasuredWidth();
				int childHeight = child.getMeasuredHeight();
				int cl = 0, ct = 0;
				switch (i) {
				case 0:
					cl = getMeasuredWidth() - childWidth;
					ct = PxAndDpUtil.dipTopx(context, 50) + 5;
					break;
				case 1:
					cl = getMeasuredWidth() - childWidth;
					ct = childHeight + PxAndDpUtil.dipTopx(context, 50) + 10;
					break;
				}
				child.layout(cl, ct, cl + childWidth, ct + childHeight);

			}
		}
	}

	public void toggleMenu(final int duration) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = getChildAt(i);
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();

			int staL = 0, staT = 0;
			switch (i) {
			case 0:
				staL = getMeasuredWidth() - childWidth;
				staT = -childHeight;
				break;
			case 1:
				staL = getMeasuredWidth() - childWidth;
				staT = -2 * childHeight;
				break;

			}
			AnimationSet aniSet = new AnimationSet(true);
			Animation trans = null;
			Animation alpha = null;
			if (mCrurentStatus == Status.CLOSE) {
				trans = new TranslateAnimation(0, 0, staT, 0);
				alpha = new AlphaAnimation(0, 1);
				child.setVisibility(View.VISIBLE);
				child.setClickable(false);
				child.setFocusable(false);
			} else {
				trans = new TranslateAnimation(0, 0, 0, staT);
				alpha = new AlphaAnimation(1, 0);
				child.setVisibility(View.GONE);
				child.setClickable(true);
				child.setFocusable(true);
			}
			trans.setFillAfter(true);
			trans.setStartOffset((i + 1) * 100);
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
					if (mCrurentStatus == Status.CLOSE) {
						child.setVisibility(View.GONE);

					}
				}
			});
			alpha.setDuration(duration);
			alpha.setStartOffset((i + 1) * 100);
			aniSet.addAnimation(trans);
			aniSet.addAnimation(alpha);
			child.startAnimation(aniSet);

			final int pos = i;
			child.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mRightTopBarMenuItemClickListener.onClick(v, pos);
					menuItemAnim(pos, duration);
					changeStatus();
					isOpen = false;
				}
			});

		}
		changeStatus();
		isOpen = !isOpen;
	}

	public void closeMenu() {
		System.out.println(">>>>>close");
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = getChildAt(i);
			child.startAnimation(scaleSmallAnim(300));

			isOpen = false;
			new Handler().postDelayed(new Runnable() {
				public void run() {
					child.setVisibility(View.GONE);
					child.setClickable(false);
					child.setFocusable(false);
				}
			}, 300);
		}
		changeStatus();

	}

	public boolean isOPenMenu() {
		return isOpen;
	}

	/**
	 * 各个选项的动画
	 * 
	 * @param pos
	 *            各个选项的位置
	 */
	private void menuItemAnim(int pos, int duration) {
		for (int i = 0; i < getChildCount(); i++) {
			final View childView = getChildAt(i);
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

	private void changeStatus() {
		mCrurentStatus = (mCrurentStatus == Status.CLOSE ? Status.OPEN
				: Status.CLOSE);
	}
}