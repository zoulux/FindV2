package com.zgrjb.find.view;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MyPaintView extends View {
	private Context context;
	private Canvas canvas;
	private Paint paint;
	private float mX, mY;
	private Path mPath;
	private Paint mPaint;
	private static final float TOUCH_TOLERANCE = 4;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mBitmapPaint;

	public MyPaintView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;
		init();

	}

	private void init() {

		mPaint = new Paint();// ����������Ⱦ����
		mPaint.setAntiAlias(true);// ���ÿ���ݣ��û滭�Ƚ�ƽ��
		mPaint.setDither(true);// ���õ�ɫ
		mPaint.setColor(Color.GREEN);// ���û��ʵ���ɫ
		mPaint.setStyle(Paint.Style.STROKE);// ���ʵ����������֣�1.FILL 2.FILL_AND_STROKE
											// 3.STROKE ��
		mPaint.setStrokeJoin(Paint.Join.ROUND);// Ĭ��������MITER��1.BEVEL 2.MITER
												// 3.ROUND ��
		mPaint.setStrokeCap(Paint.Cap.ROUND);// Ĭ��������BUTT��1.BUTT 2.ROUND
												// 3.SQUARE ��
		mPaint.setStrokeWidth(5);// ������ߵĿ�ȣ�������õ�ֵΪ0��ô����һ����ϸ����
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);

		int mScreenWidth = outMetrics.widthPixels;
		int mScreenHeight = outMetrics.heightPixels;
		mBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight,
				Bitmap.Config.ARGB_8888);// ���ƹ̶���С��bitmap����
		mCanvas = new Canvas(mBitmap);// ���̶���bitmap����Ƕ�뵽canvas������
		mCanvas.drawColor(Color.WHITE);
		mPath = new Path();// ��������·��
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		// canvas = mCanvas;
		paint = mPaint;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(0xFFAAAAAA);
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		canvas.drawPath(mPath, mPaint);

		super.onDraw(canvas);

	}

	private void onTouchDown(float x, float y) {
		// mPath.reset();//���ϴε�·�������������������µ�·����
		mPath.moveTo(x, y);// �����µ�·�����������Ŀ�ʼ
		mX = x;
		mY = y;
	}

	private void onTouchMove(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void onTouchUp(float x, float y) {
		mPath.lineTo(mX, mY);// �����һ��ָ����xy�����һ���ߣ����û����moveTo��������ô��ʼ���ʾ��0��0���㡣
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);// ��ָ�뿪��Ļ�󣬻��ƴ����ġ����С�·����
		// kill this so we don't double draw
		// mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:// ��ָ��ʼ��ѹ��Ļ��������������˳�ʼ��λ��
			onTouchDown(x, y);
			invalidate();// ˢ�»�������������onDraw��������
			break;
		case MotionEvent.ACTION_MOVE:// ��ָ��ѹ��Ļʱ��λ�õĸı䴥�������������ACTION_DOWN��ACTION_UP֮�䡣
			onTouchMove(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:// ��ָ�뿪��Ļ�����ٰ�ѹ��Ļ
			onTouchUp(x, y);
			invalidate();
			break;
		default:
			break;
		}
		return true;
	}

	private String dateString;

	public void save() {
		// dateString = DateToStr(new Date());
		dateString = System.currentTimeMillis() + "";
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/Find/Picture_Regist/" + dateString + ".jpg");

		try {

			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();

			// FileOutputStream fos = new FileOutputStream(file.getPath() + "/"
			// + dateString + ".jpg");

			// mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			// fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDateString() {
		return dateString;
	}

	public String DateToStr(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format.format(date);
		return str;
	}
}
