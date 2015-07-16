package com.zgrjb.find.view;

import com.zgrjb.find.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HeaderLayout extends LinearLayout {
	private LayoutInflater mLayoutInflater;
	private TextView mTextView;
	View rootView;
	public HeaderLayout(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}
	private TextView titleTextView;
	private LayoutInflater mInflater;
	public void init(Context context){
		mLayoutInflater = LayoutInflater.from(context);
		rootView = mInflater.inflate(R.layout.include_actionbar,null);
		addView(rootView);
		initView();
	}
	private void initView() {
			mTextView = (TextView)findViewById(R.id.titleText);
	}
	Context context;
	public void createTitle(String text){
		titleTextView.setText(text);
	}

}
