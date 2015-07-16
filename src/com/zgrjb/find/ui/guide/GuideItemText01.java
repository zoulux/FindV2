package com.zgrjb.find.ui.guide;
import com.zgrjb.find.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class GuideItemText01 extends TextView{
	
	/**
	 * 加载引导页中文字的动画
	 * @param context
	 * @param attrs
	 */
	public GuideItemText01(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAnimation(context);
	}

	public GuideItemText01(Context context) {
		super(context);
	}

	private void initAnimation(Context context) {
			startAnimation(AnimationUtils.loadAnimation(context, R.anim.set1));
	}

}
