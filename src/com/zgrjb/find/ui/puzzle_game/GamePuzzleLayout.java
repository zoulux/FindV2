package com.zgrjb.find.ui.puzzle_game;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.zgrjb.find.R;
import com.zgrjb.find.utils.CommonUtils;
import com.zgrjb.find.utils.ImagePiece;
import com.zgrjb.find.utils.ImageSplitterUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GamePuzzleLayout extends RelativeLayout implements OnClickListener {
	// 将一个大的图片切割成 mCulumn行mCulumn列
	private int mCulumn;

	private int mPadding = 3;// 容器的内边距 dp
	private int mMagin;// 每张小图之间的距离

	private ImageView[] mGamePintuItems;
	private int mItemWidth;
	private Bitmap mBitmap;// 游戏的图片

	private int mWidth;// 游戏面版的宽度
	private boolean isGameSuccess;// 判断是否闯关成功
	private boolean isGameOver;// 判断是否闯关失败
	private int level = 1;
	private List<ImagePiece> mItemBitmaps;
	private boolean isCanClick = true;

	private boolean once;
	private CommonUtils tools;

	private int randomNumber;

	private final int RANDOM_NUMBER_ONE = 1;
	private final int RANDOM_NUMBER_TWO = 2;
	private final int RANDOM_NUMBER_THREE = 3;
	private final int RANDOM_NUMBER_FOUR = 4;
	private final int RANDOM_NUMBER_FIVE = 5;

	private final int EASE = 6;
	private final int NORMAL = 7;
	private final int DIFFICULT = 8;

	private MediaPlayer playMp3Click;

	public interface GamePintuListener {
		void youWin(int level);

		void timeChaneged(int currentTime);

		void gameOver();
	}

	public GamePintuListener mListener;

	/**
	 * 设置接口回调
	 */
	public void setOnGamePintuListener(GamePintuListener mListener) {
		this.mListener = mListener;
	}

	private static final int TIMA_CHANGED = 0x110;
	private static final int NEXT_LEVER = 0x111;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TIMA_CHANGED:
				if (isGameSuccess || isGameOver) {
					return;
				}

				if (mTime == 0) {
					isGameOver = true;
					mListener.gameOver();
					return;
				}

				if (mListener != null) {
					mListener.timeChaneged(mTime);
				}
				mTime--;
				mHandler.sendEmptyMessageDelayed(TIMA_CHANGED, 1000);
				break;
			case NEXT_LEVER:
				level++;
				if (mListener != null) {
					mListener.youWin(level);
				} else {
					nextLevel();
				}
				break;

			}
		};

	};

	private boolean isTimeEnabled = false;
	private int mTime;

	public void setTimeEnabled(boolean isTimeEnabled) {
		this.isTimeEnabled = isTimeEnabled;
	}

	public GamePuzzleLayout(Context context) {
		this(context, null);
	}

	public GamePuzzleLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public GamePuzzleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		playMp3Click = MediaPlayer.create(context, R.raw.click);
	}

	private void init() {
		mMagin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				3, getResources().getDisplayMetrics());
		mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(),
				getPaddingBottom());
		tools = new CommonUtils();

	}

	public void setdifficult(int diff) {
		switch (diff) {
		case EASE:
			mCulumn = 3;
			break;
		case NORMAL:
			mCulumn = 4;
			break;
		case DIFFICULT:
			mCulumn = 5;
			break;

		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());
		if (!once) {
			// setdifficult(8);
			initBItmap();// 进行切图，以及排序
			initItem(); // 设置ImageView宽高等属性

			// 判断是否开启时间
			checkTimeEnabled();
			once = true;
		}
		// 强制将容器变为正方形
		setMeasuredDimension(mWidth, mWidth);

	}

	/**
	 * 判断是否开启时间
	 */
	private void checkTimeEnabled() {
		if (isTimeEnabled) {

			// 根据当前的等级设置时间
			countTimeBaseLevel();
			mHandler.sendEmptyMessage(TIMA_CHANGED);
		}
	}

	private void countTimeBaseLevel() {
		mTime = (int) Math.pow(2, level) * 60;
	}

	/**
	 * 设置ImageView宽高等属性
	 */
	private void initItem() {
		mItemWidth = (mWidth - mPadding * 2 - mMagin * (mCulumn - 1)) / mCulumn;
		mGamePintuItems = new ImageView[mCulumn * mCulumn];
		for (int i = 0; i < mGamePintuItems.length; i++) {
			ImageView item = new ImageView(getContext());
			item.setOnClickListener(this);
			item.setImageBitmap(mItemBitmaps.get(i).getBitmap());
			mGamePintuItems[i] = item;

			item.setId(i + 1);
			// 在item的tag中存储了index
			// index就是虽然在拼图的顺序打乱了，但是index的顺序是不变的
			item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					mItemWidth, mItemWidth);
			// 设置item间的横向间隙，通过rightMargin
			// 不是最后一列。
			if ((i + 1) % mCulumn != 0) {
				lp.rightMargin = mMagin;
			}
			// 不是第一列
			if (i % mCulumn != 0) {
				lp.addRule(RelativeLayout.RIGHT_OF,
						mGamePintuItems[i - 1].getId());
			}
			// 设置item间的纵向间隙，通过topMargin
			// 不是第一行。
			if ((i + 1) > mCulumn) {
				lp.topMargin = mMagin;
				lp.addRule(RelativeLayout.BELOW,
						mGamePintuItems[i - mCulumn].getId());
			}
			addView(item, lp);

		}

	}

	public void setRandomNuber(int randomNuber) {
		this.randomNumber = randomNuber;
	}

	/**
	 * 进行切图，以及排序
	 */
	private void initBItmap() {
		if (mBitmap == null) {
			switch (randomNumber) {
			case RANDOM_NUMBER_ONE:
				mBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.fengjing);
				break;
			case RANDOM_NUMBER_TWO:
				mBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.animal);
				break;
			case RANDOM_NUMBER_THREE:
				mBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.lanbo);
				break;
			case RANDOM_NUMBER_FOUR:
				mBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.kelan);
				break;
			case RANDOM_NUMBER_FIVE:
				mBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.jinsha);
				break;

			}

		}
		mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mCulumn);
		// 使用sort完成我们对图片的乱序
		Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {

			@Override
			public int compare(ImagePiece p1, ImagePiece p2) {
				return Math.random() > 0.5 ? 1 : -1;
			}
		});
	}

	private int min(int... params) {
		int min = params[0];
		for (int param : params) {
			if (param < min) {
				min = param;
			}
		}
		return min;
	}

	private ImageView mFirst;
	private ImageView mSecond;

	@Override
	public void onClick(View v) {

		// 两次点击同一个Item
		if (mFirst == v) {
			playMp3Click.start();
			mFirst.setColorFilter(null);
			mFirst = null;
			return;

		}

		if (mFirst == null) {
			playMp3Click.start();
			mFirst = (ImageView) v;
			mFirst.setColorFilter(Color.parseColor("#55FF0000"));
		} else {
			if (isCanClick) {
				playMp3Click.start();
				mSecond = (ImageView) v;
				exchangeView();
			}

		}
	}

	/**
	 * 动画层
	 */
	private RelativeLayout mAnimLayout;

	/**
	 * 构造我们的动画层
	 */
	private void setUpAnimLayout() {
		if (mAnimLayout == null) {
			mAnimLayout = new RelativeLayout(getContext());
			addView(mAnimLayout);
		}
	}

	// 交换图片的item
	private void exchangeView() {
		mFirst.setColorFilter(null);
		// 构造我们的动画层
		setUpAnimLayout();

		ImageView first = new ImageView(getContext());
		final Bitmap firstBitmap = mItemBitmaps.get(
				getImageIdBytag((String) mFirst.getTag())).getBitmap();
		first.setImageBitmap(firstBitmap);
		LayoutParams lP = new LayoutParams(mItemWidth, mItemWidth);
		lP.leftMargin = mFirst.getLeft() - mPadding;
		lP.topMargin = mFirst.getTop() - mPadding;
		first.setLayoutParams(lP);
		mAnimLayout.addView(first);

		ImageView second = new ImageView(getContext());
		final Bitmap secondBitmap = mItemBitmaps.get(
				getImageIdBytag((String) mSecond.getTag())).getBitmap();
		second.setImageBitmap(secondBitmap);
		LayoutParams lP2 = new LayoutParams(mItemWidth, mItemWidth);
		lP2.leftMargin = mSecond.getLeft() - mPadding;
		lP2.topMargin = mSecond.getTop() - mPadding;
		second.setLayoutParams(lP2);
		mAnimLayout.addView(second);

		// 设置动画

		TranslateAnimation animFirst = new TranslateAnimation(0,
				mSecond.getLeft() - mFirst.getLeft(), 0, mSecond.getTop()
						- mFirst.getTop());
		animFirst.setDuration(300);
		animFirst.setFillAfter(true);
		first.startAnimation(animFirst);

		TranslateAnimation animSecond = new TranslateAnimation(0,
				-mSecond.getLeft() + mFirst.getLeft(), 0, -mSecond.getTop()
						+ mFirst.getTop());
		animSecond.setDuration(300);
		animSecond.setFillAfter(true);
		second.startAnimation(animSecond);

		// 动画监听
		animFirst.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mFirst.setVisibility(View.INVISIBLE);
				mSecond.setVisibility(View.INVISIBLE);
				isCanClick = false;

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {

				String firstTag = (String) mFirst.getTag();
				String secondtTag = (String) mSecond.getTag();

				mSecond.setImageBitmap(firstBitmap);

				mFirst.setImageBitmap(secondBitmap);
				mFirst.setTag(secondtTag);
				mSecond.setTag(firstTag);

				mFirst.setVisibility(View.VISIBLE);
				mSecond.setVisibility(View.VISIBLE);
				mFirst = mSecond = null;
				mAnimLayout.removeAllViews();
				isCanClick = true;

				// 判断游戏是否成功
				checkSuccess();

			}

		});

	}

	/**
	 * 判断游戏是否成功
	 */
	private void checkSuccess() {
		boolean isSuccess = true;
		for (int i = 0; i < mGamePintuItems.length; i++) {
			ImageView imageView = mGamePintuItems[i];
			if (getImageIndexByTag((String) imageView.getTag()) != i) {
				isSuccess = false;

			}
		}
		if (isSuccess) {
			isGameSuccess = true;
			mHandler.removeMessages(TIMA_CHANGED);
			Toast.makeText(getContext(), "闯关成功", 1).show();
			mHandler.sendEmptyMessage(NEXT_LEVER);
		}
	}

	private int getImageIdBytag(String tag) {
		String[] split = tag.split("_");
		return Integer.parseInt(split[0]);
	}

	private int getImageIndexByTag(String tag) {
		String[] split = tag.split("_");
		return Integer.parseInt(split[1]);
	}

	public void reStart() {
		isGameOver = false;
		mCulumn--;
		nextLevel();
	}

	private boolean isPause;

	public void pause() {
		isPause = true;
		mHandler.removeMessages(TIMA_CHANGED);
	}

	public void resume() {
		if (isPause) {
			isPause = false;
			mHandler.sendEmptyMessage(TIMA_CHANGED);
		}
	}

	public void stopAndRemove() {
		mHandler.removeMessages(TIMA_CHANGED);
	}

	public void nextLevel() {
		this.removeAllViews();
		mAnimLayout = null;
		mCulumn++;
		isGameSuccess = false;
		checkTimeEnabled();
		initBItmap();
		initItem();
	}

	private void playClickMp3() {

	}

}
