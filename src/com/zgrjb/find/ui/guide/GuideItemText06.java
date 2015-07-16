package com.zgrjb.find.ui.guide;

import com.zgrjb.find.R;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class GuideItemText06 extends TextView {
/**
 * 加载引导页6中文字的动画
 * @param context
 * @param attrs
 */
	public GuideItemText06(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(final Context context) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				startAnimation(AnimationUtils.loadAnimation(context,
						R.anim.text_alpha_in_item_6));
				// bt.setVisibility(View.VISIBLE);
			}
		}, 2000);

	}

}
