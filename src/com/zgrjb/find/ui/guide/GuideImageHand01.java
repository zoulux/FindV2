package com.zgrjb.find.ui.guide;

import com.zgrjb.find.R;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class GuideImageHand01 extends ImageView{
	/**
	 * 加载引导页1中手指的动作
	 * @param context
	 * @param attrs
	 */
	public GuideImageHand01(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAnimation(context);
	}

	private void initAnimation(final Context context) {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				startAnimation(AnimationUtils.loadAnimation(context, R.anim.hand_show_in_item_1));
			}
		}, 3000);

}
}
