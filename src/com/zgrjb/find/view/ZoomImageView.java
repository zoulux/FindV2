package com.zgrjb.find.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

/*
 * 当图片特别大的时候，会自动缩小到合适的大小，特别小的时候，会自动放大到合适的大小，并且居中
 */
public class ZoomImageView extends ImageView implements OnGlobalLayoutListener,
		OnScaleGestureListener, OnTouchListener {
	private boolean mOnce = false;// 因为初始化的操作只需要执行一次
	// 初始化时缩放的值
	private float mInitScale;
	// 双击时放大时达到的值
	private float mMidScale;
	// 放大的最大值
	private float mMaxScale;

	private Matrix mScaleMatrix;
	// 捕获用户多点触控时缩放的比列
	private ScaleGestureDetector mScaleGestureDetector;

	// 记录上一次多点触控的数量
	private int mLastPointCount;
	private float mLastX;
	private float mLastY;
	private int mTouchSlop;
	private boolean isCanDrag;

	private boolean isCheckLeftAndRight;
	private boolean isCheckTopAndBottom;

	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// init
		mScaleMatrix = new Matrix();
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		setOnTouchListener(this);
		// 获取系统里的一个值来衡量是否移动图片
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public ZoomImageView(Context context) {
		this(context, null);

	}

	/**
	 * 注册View
	 */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	/**
	 * 移除View的注册
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	/**
	 * 获取ImageView加载完成的图片
	 */
	@Override
	public void onGlobalLayout() {
		if (!mOnce) {
			// 得到控件 的宽和高
			int width = getWidth();
			int height = getHeight();

			// 得到我们的图片以及宽和高
			Drawable d = getDrawable();
			if (d == null) {
				return;
			}
			// 默认缩放的值
			float scale = 1.0f;
			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();
			if (dw > width && dh < height) {
				scale = width * 1.0f / dw;
			}
			if (dh > height && dw < width) {
				scale = height * 1.0f / dh;
			}
			if ((dw > width && dh > height) || (dw < width && dh < height)) {
				scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
			}

			// 得到初始化时缩放的比列
			mInitScale = scale;
			mMaxScale = mInitScale * 4;
			mMidScale = mInitScale * 2;

			// 将图片移动至控件中心
			int dx = width / 2 - dw / 2;
			int dy = height / 2 - dh / 2;
			mScaleMatrix.postTranslate(dx, dy);
			mScaleMatrix.postScale(mInitScale, mInitScale, width / 2,
					height / 2);
			setImageMatrix(mScaleMatrix);
			// 将图片移动至当前控件的中心
			mOnce = true;
		}
	}

	/**
	 * 获取当前图片缩放的值
	 * 
	 * @return
	 */

	private float getScale() {

		float values[] = new float[9];
		mScaleMatrix.getValues(values);
		return values[Matrix.MSCALE_X];
	}

	// 缩放的区间 mInitScale~mMaxScale
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float scale = getScale();

		// scaleFactor就是缩放的值,值是大于或小于1的正数
		float scaleFactor = detector.getScaleFactor();
		// getDrawable()获取图片
		if (getDrawable() == null) {
			return true;
		}

		// 缩放范围的控制
		if ((scale < mMaxScale && scaleFactor > 1.0f)
				|| (scale > mInitScale && scaleFactor < 1.0f)) {
			if (scale * scaleFactor < mInitScale) {
				scaleFactor = mInitScale / scale;
			}
			if (scale * scaleFactor > mMaxScale) {
				scale = mMaxScale / scale;
			}
			// 缩放
			mScaleMatrix.postScale(scaleFactor, scaleFactor, getWidth() / 2,
					getHeight() / 2);
			setImageMatrix(mScaleMatrix);
		}
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {

	}

	public Drawable getRe() {
		return getDrawable();
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// 将event交给mScaleGestureDetector处理
		mScaleGestureDetector.onTouchEvent(event);
		float x = 0;
		float y = 0;

		int pointCount = event.getPointerCount();
		for (int i = 0; i < pointCount; i++) {
			x += event.getX(i);
			y += event.getY(i);
		}
		x /= pointCount;
		y /= pointCount;
		if (mLastPointCount != pointCount) {
			isCanDrag = false;
			mLastX = x;
			mLastY = y;
		}
		mLastPointCount = pointCount;
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float dx = x - mLastX;
			float dy = y - mLastY;

			if (!isCanDrag) {
				isCanDrag = isMoveAction(dx, dy);
			}
			if (isCanDrag) {
				RectF rectF = getMatrixRectF();
				if (getDrawable() != null) {
					isCheckLeftAndRight = isCheckTopAndBottom = true;
					// 如果宽度小于控件宽度，不允许横向移动
					if (rectF.width() < getWidth()) {
						isCheckLeftAndRight = false;
						dx = 0;
					}
					// 如果高度小于控件的高度，不允许纵向移动
					if (rectF.height() < getHeight()) {
						isCheckTopAndBottom = false;
						dy = 0;
					}
					mScaleMatrix.postTranslate(dx, dy);
					checkBorderWhenTranslate();
					setImageMatrix(mScaleMatrix);
				}
			}
			mLastX = x;
			mLastY = y;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mLastPointCount = 0;
			break;
		}
		return true;
	}

	// 当移动时进行边界检查

	private void checkBorderWhenTranslate() {
		RectF rectF = getMatrixRectF();
		float deltX = 0;
		float deltY = 0;
		int width = getWidth();
		int height = getHeight();
		if (rectF.top > 0 && isCheckTopAndBottom) {
			deltY = -rectF.top;
		}
		if (rectF.bottom < height && isCheckTopAndBottom) {
			deltY = height - rectF.bottom;
		}
		if (rectF.left > 0 && isCheckLeftAndRight) {
			deltX = -rectF.left;
		}
		if (rectF.right < width && isCheckLeftAndRight) {
			deltX = width - rectF.right;
		}
		mScaleMatrix.postTranslate(deltX, deltY);
	}

	private RectF getMatrixRectF() {
		Matrix matrix = mScaleMatrix;
		RectF rectF = new RectF();
		Drawable d = getDrawable();
		if (d != null) {
			rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rectF);

		}
		return rectF;
	}

	// 判断是否足以移动
	private boolean isMoveAction(float dx, float dy) {
		// TODO Auto-generated method stub
		return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
	}

	public int getW() {
		return getMaxWidth();
	}

	public int getH() {
		return getMaxHeight();
	}

}
