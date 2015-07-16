package com.zgrjb.find.ui.puzzle_game;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.zgrjb.find.R;
import com.zgrjb.find.utils.CommonUtils;

public class ChoiceDifferPictureToPlay {
	// 初始化CommonUtils工具类
	private static CommonUtils tools = new CommonUtils();
	// 初始化一个随机数
	private static int randomNumBer;
	private final static int RANDOM_NUMBER_ONE = 1;
	private final static int RANDOM_NUMBER_TWO = 2;
	private final static int RANDOM_NUMBER_THREE = 3;
	private final static int RANDOM_NUMBER_FOUR = 4;
	private final static int RANDOM_NUMBER_FIVE = 5;

	public static int getRandomNumber() {
		return randomNumBer;
	}

	/**
	 * 获得随机的图片
	 * 
	 * @param imageview
	 *            图片
	 * @param context
	 *            上下文
	 */
	public static void getRandomPicture(ImageView imageview, Context context) {
		randomNumBer = tools.getRandomNumber();
		switch (randomNumBer) {
		case RANDOM_NUMBER_ONE:
			imageview.setImageDrawable(context.getResources().getDrawable(
					R.drawable.fengjing));
			break;
		case RANDOM_NUMBER_TWO:
			imageview.setImageDrawable(context.getResources().getDrawable(
					R.drawable.animal));
			break;

		case RANDOM_NUMBER_THREE:
			imageview.setImageDrawable(context.getResources().getDrawable(
					R.drawable.lanbo));
			break;

		case RANDOM_NUMBER_FOUR:
			imageview.setImageDrawable(context.getResources().getDrawable(
					R.drawable.kelan));
			break;

		case RANDOM_NUMBER_FIVE:
			imageview.setImageDrawable(context.getResources().getDrawable(
					R.drawable.jinsha));
			break;

		}
	}

}
