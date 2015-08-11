package com.zgrjb.find.ui.dialog;
import com.zgrjb.find.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * �Զ���dialog
 * 
 * @author Mr.Xu
 * 
 */
public class MyCustomDialog extends Dialog {

	private String name;
	private ImageView takePhotoImg;
	private ImageView getAlbumImg;

	private OnCustomDialogListener customDialogListener;

	// ����ص��¼�������dialog�ĵ���¼�
	public interface OnCustomDialogListener {
		public void back(int value);
	}

	public MyCustomDialog(Context context, String name,
			OnCustomDialogListener customDialogListener) {
		super(context);
		this.name = name;
		this.customDialogListener = customDialogListener;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_avatar_regist_dialog);
		takePhotoImg = (ImageView) findViewById(R.id.id_regist_take_photo);
		getAlbumImg = (ImageView) findViewById(R.id.id_regist_get_album);
		// ���ñ���
		setTitle(name);
		takePhotoImg.setOnClickListener(clickListener);
		getAlbumImg.setOnClickListener(clickListener2);
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			customDialogListener.back(1);
			MyCustomDialog.this.dismiss();
		}
	};
	private View.OnClickListener clickListener2 = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			customDialogListener.back(3);
			MyCustomDialog.this.dismiss();
		}
	};

}