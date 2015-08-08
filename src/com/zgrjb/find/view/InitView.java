package com.zgrjb.find.view;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
/**
 * 初始化为四个方块，只用一次，后期都是GONE
 * @author ly
 *
 */
public class InitView extends ViewGroup {

	public InitView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				child.setAlpha(0.5f);
				int childWidth = child.getMeasuredWidth();
				int childHeight = child.getMeasuredHeight();
				int cl = 0, ct = 0;
				switch (i) {
				case 0:
					cl = getMeasuredWidth() / 2 - childWidth;
					ct = getMeasuredHeight() / 2 - childHeight;
					break;
				case 1:
					cl = getMeasuredWidth() / 2;
					ct = getMeasuredHeight() / 2 - childHeight;
					break;
				case 2:
					cl = getMeasuredWidth() / 2 - childWidth;
					ct = getMeasuredHeight() / 2;
					break;
				case 3:
					cl = getMeasuredWidth() / 2;
					ct = getMeasuredHeight() / 2;
					break;
				}
				child.layout(cl, ct, childWidth + cl, childHeight + ct);
			}
		}
	}
	
	public void setGone(){
		setVisibility(View.GONE);
	}

}
