package com.zgrjb.find.ui;

import com.nineoldandroids.view.ViewHelper;
import com.zgrjb.find.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

public class SlidingMenu extends HorizontalScrollView {
	private LinearLayout mWarpper;
	// ����һ��View��˵�
	private ViewGroup mMenu;
	// ����һ��view������
	private ViewGroup mContent;
	// ������Ļ�Ŀ��
	private int mScreenWidth;
	// ����˵��Ŀ��
	private int mMenuWidth;
	private int mMenuRightPadding = 100; // dp
	private boolean once = false;
	// �жϲ˵��Ƿ��
	public boolean isOpen;

	public SlidingMenu(Context context) {
		this(context, null);
	}

	/**
	 * ����ʹ���Զ�������ʱ���͵��ô˹��췽��
	 * 
	 * @param context
	 * @param attrs
	 */
	public SlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// WindowManager wm = (WindowManager) context
		// .getSystemService(Context.WINDOW_SERVICE);
		// DisplayMetrics outMetrics = new DisplayMetrics();
		// wm.getDefaultDisplay().getMetrics(outMetrics);
		// mScreenWidth = outMetrics.widthPixels;
		// // ��dp��ת��Ϊ����ֵpx
		// mMenuRightPadding = (int) TypedValue.applyDimension(
		// TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources()
		// .getDisplayMetrics());
	}

	/**
	 * ��ʹ�����Զ��������ʱ�򣬻��߸÷���
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// ��ȡ���Ƕ��������
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.slidingMenu, defStyle, 0);
		int n = array.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = array.getIndex(i);
			switch (attr) {
			case R.styleable.slidingMenu_rightPading:
				mMenuRightPadding = array.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 10, context
										.getResources().getDisplayMetrics()));
				break;
			}
		}

		array.recycle();

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;

	}

	/**
	 * ������View�Ŀ�͸ߺ������Լ��Ŀ�͸�
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!once) {
			mWarpper = (LinearLayout) getChildAt(0);
			mMenu = (ViewGroup) mWarpper.getChildAt(0);
			mContent = (ViewGroup) mWarpper.getChildAt(1);
			mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth
					- mMenuRightPadding;
			mContent.getLayoutParams().width = mScreenWidth;
			once = true;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	/**
	 * ͨ������ƫ������menu����
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		super.onLayout(changed, l, t, r, b);
		if (changed) {
			// ��menu���������
			scrollTo(mMenuWidth, 0);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();

		switch (action) {
		case MotionEvent.ACTION_UP:
			// scrollX��������������ߵĿ��
			int scrollX = getScrollX();

			if (scrollX >= mMenuWidth / 2) {
				this.smoothScrollTo(mMenuWidth, 0);
				isOpen = false;
			} else {
				this.smoothScrollTo(0, 0);
				isOpen = true;
			}
			return true;
		case MotionEvent.ACTION_MOVE:
			int scrollX1 = getScrollX();
			// System.out.println("scrollX1 "+ev.getX());
			break;
		}
		return super.onTouchEvent(ev);
	}

	private boolean statement = true;
	private ImageView leftMenuTitleBar;
	private float rotation;
	/**
	 * �򿪲˵�
	 */
	public void openMenu() {
		if (isOpen) {
			return;
		}
		this.smoothScrollTo(0, 0);
		isOpen = true;
		statement = isOpen;
	}

	/**
	 * �رղ˵�
	 */

	public void closeMenu() {
		if (!isOpen) {
			return;
		}
		this.smoothScrollTo(mMenuWidth, 0);
		isOpen = false;
		statement = isOpen;
	}

	public void closeMore() {
		this.smoothScrollTo(mMenuWidth, 0);
		isOpen = false;
	}

	/**
	 * �л��˵�
	 */
	public void toggle() {
		if (isOpen) {
			closeMenu();
		} else {
			openMenu();
		}

	}

	public void toggle2() {
		if (isOpen) {

			openMenu();
		} else {
			closeMenu();
		}

	}
	
	public void rotating(ImageView leftMenuTitleBar) {
		this.leftMenuTitleBar = leftMenuTitleBar;
}

	/**
	 * L���൱��getScrollX ��������ʱ�͵��ô˷���
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		float scale = l * 1.0f / mMenuWidth; // 1~0
		rotation = (1-scale)*360;
		float rightScale = 0.7f + 0.3f * scale;
		float leftScale = 1.0f - scale * 0.3f;
		float leftAlpha = 0.6f + 0.4f * (1 - scale);
		// �������Զ���������TranslationX
		// view
		ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.6f);
		ViewHelper.setPivotX(mContent, 0);
		ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
		ViewHelper.setScaleX(mContent, rightScale);
		ViewHelper.setScaleY(mContent, rightScale);
		ViewHelper.setScaleX(mMenu, leftScale);
		ViewHelper.setScaleY(mMenu, leftScale);
		ViewHelper.setAlpha(mMenu, leftAlpha);
		ViewHelper.setRotation(leftMenuTitleBar, rotation);
	}

}
