package com.zgrjb.find.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.Window;
import android.widget.ImageView;

import com.zgrjb.find.R;
import com.zgrjb.find.zxing.encoding.EncodingHandler;

public class CreateQrCodeActivity extends BaseActivity {
	private ImageView QRCodeImg;
	private String QrValue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_qr_code);
		WindowManager m = getWindowManager();    
	    Display d = m.getDefaultDisplay();  //Ϊ��ȡ��Ļ����    
	        
	    LayoutParams p = getWindow().getAttributes();  //��ȡ�Ի���ǰ�Ĳ���ֵ    
	    p.height = (int) (d.getHeight() * 1.0);   //�߶�����Ϊ��Ļ��1.0   
	    p.width = (int) (d.getWidth() * 1.0);    //�������Ϊ��Ļ��0.8   
	    p.alpha = 1.0f;      //���ñ���͸����  
	    p.dimAmount = 0.0f;      //���úڰ���  
	        
	    getWindow().setAttributes(p);     //������Ч  
	    getWindow().setGravity(Gravity.CENTER);       //���þ��ж���
		init();

	}
	private void init() {
			initActionBar();
		
		    Intent intent = getIntent();
		    QRCodeImg = (ImageView) findViewById(R.id.id_qr_codeImage);
		    QrValue = intent.getStringExtra("QrStringValue");
		    try {
				Bitmap bitmap = EncodingHandler.createQRCode(QrValue, 400);
				QRCodeImg.setImageBitmap(bitmap);
			} catch (Exception e) {
				ShowToast("ʧ��");
				e.printStackTrace();
				
			}
			
	}
	private void initActionBar() {
		    showTitleText("��ά��");
			setDrawablePath(getResources().getDrawable(R.drawable.back));
			leftButtonIsVisible(true);
			leftImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
						CreateQrCodeActivity.this.finish();
						overridePendingTransition(R.anim.fade, R.anim.hold);
				}
			});
	}
	
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
		//overridePendingTransition(R.anim.fade, R.anim.hold);
		
	}
}
