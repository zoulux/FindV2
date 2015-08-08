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
 * ��ͼƬ�ر���ʱ�򣬻��Զ���С�����ʵĴ�С���ر�С��ʱ�򣬻��Զ��Ŵ󵽺��ʵĴ�С�����Ҿ���
 */
public class ZoomImageView extends ImageView implements OnGlobalLayoutListener,
		OnScaleGestureListener, OnTouchListener {
	private boolean mOnce = false;// ��Ϊ��ʼ���Ĳ���ֻ��Ҫִ��һ��
	// ��ʼ��ʱ���ŵ�ֵ
	private float mInitScale;
	// ˫��ʱ�Ŵ�ʱ�ﵽ��ֵ
	private float mMidScale;
	// �Ŵ�����ֵ
	private float mMaxScale;

	private Matrix mScaleMatrix;
	// �����û���㴥��ʱ���ŵı���
	private ScaleGestureDetector mScaleGestureDetector;

	// ��¼��һ�ζ�㴥�ص�����
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
		// ��ȡϵͳ���һ��ֵ�������Ƿ��ƶ�ͼƬ
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public ZoomImageView(Context context) {
		this(context, null);

	}

	/**
	 * ע��View
	 */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	/**
	 * �Ƴ�View��ע��
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	/**
	 * ��ȡImageView������ɵ�ͼƬ
	 */
	@Override
	public void onGlobalLayout() {
		if (!mOnce) {
			// �õ��ؼ� �Ŀ�͸�
			int width = getWidth();
			int height = getHeight();

			// �õ����ǵ�ͼƬ�Լ���͸�
			Drawable d = getDrawable();
			if (d == null) {
				return;
			}
			// Ĭ�����ŵ�ֵ
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

			// �õ���ʼ��ʱ���ŵı���
			mInitScale = scale;
			mMaxScale = mInitScale * 4;
			mMidScale = mInitScale * 2;

			// ��ͼƬ�ƶ����ؼ�����
			int dx = width / 2 - dw / 2;
			int dy = height / 2 - dh / 2;
			mScaleMatrix.postTranslate(dx, dy);
			mScaleMatrix.postScale(mInitScale, mInitScale, width / 2,
					height / 2);
			setImageMatrix(mScaleMatrix);
			// ��ͼƬ�ƶ�����ǰ�ؼ�������
			mOnce = true;
		}
	}

	/**
	 * ��ȡ��ǰͼƬ���ŵ�ֵ
	 * 
	 * @return
	 */

	private float getScale() {

		float values[] = new float[9];
		mScaleMatrix.getValues(values);
		return values[Matrix.MSCALE_X];
	}

	// ���ŵ����� mInitScale~mMaxScale
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float scale = getScale();

		// scaleFactor�������ŵ�ֵ,ֵ�Ǵ��ڻ�С��1������
		float scaleFactor = detector.getScaleFactor();
		// getDrawable()��ȡͼƬ
		if (getDrawable() == null) {
			return true;
		}

		// ���ŷ�Χ�Ŀ���
		if ((scale < mMaxScale && scaleFactor > 1.0f)
				|| (scale > mInitScale && scaleFactor < 1.0f)) {
			if (scale * scaleFactor < mInitScale) {
				scaleFactor = mInitScale / scale;
			}
			if (scale * scaleFactor > mMaxScale) {
				scale = mMaxScale / scale;
			}
			// ����
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
		// ��event����mScaleGestureDetector����
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
					// ������С�ڿؼ���ȣ�����������ƶ�
					if (rectF.width() < getWidth()) {
						isCheckLeftAndRight = false;
						dx = 0;
					}
					// ����߶�С�ڿؼ��ĸ߶ȣ������������ƶ�
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

	// ���ƶ�ʱ���б߽���

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

	// �ж��Ƿ������ƶ�
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
