package com.zgrjb.find.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.zgrjb.find.R;
import com.zgrjb.find.adapter.ChatMessageAdapter;
import com.zgrjb.find.bean.ChatMessage;
import com.zgrjb.find.bean.ChatMessage.Type;
import com.zgrjb.find.utils.HttpUtils;

public class RobotActivity extends BaseActivity {

	private ListView mMsgs;
	private ChatMessageAdapter mAdpter;
	private List<ChatMessage> mDates;

	private EditText mIuput;
	private Button mSendMsg;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			// 等待接受，子线程完成的返回数据
			ChatMessage fromMessage = (ChatMessage) msg.obj;
			mDates.add(fromMessage);
			mAdpter.notifyDataSetChanged();
			mMsgs.setSelection(mAdpter.getCount() - 1);

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_robot);

		initView();
		initDates();
		initListener();
	}

	private void initListener() {
		mSendMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String toMsg = mIuput.getText().toString();
				if (TextUtils.isEmpty(toMsg)) {
					Toast.makeText(RobotActivity.this, "消息不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}

				ChatMessage toMessage = new ChatMessage();
				toMessage.setDate(new Date());
				toMessage.setMsg(toMsg);
				toMessage.setType(Type.OUTCOMING);
				toMessage.setName(userName);
				// toMessage.setmImg(android.R.color.black);
				mDates.add(toMessage);
				mAdpter.notifyDataSetChanged();
				mMsgs.setSelection(mAdpter.getCount() - 1);
				mIuput.setText("");

				new Thread() {
					public void run() {
						ChatMessage fromMessage = HttpUtils.sendMessage(toMsg);
						Message m = Message.obtain();
						m.obj = fromMessage;
						mHandler.sendMessage(m);
					};

				}.start();

			}
		});

	}

	private void initDates() {

		userName = userManager.getCurrentUserName();

		mDates = new ArrayList<ChatMessage>();
		mDates.add(new ChatMessage("你好!", Type.INCOMING, new Date()));

		mAdpter = new ChatMessageAdapter(this, mDates);
		mMsgs.setAdapter(mAdpter);
	}

	private void initView() {

		mMsgs = (ListView) findViewById(R.id.id_listview_msgs);
		mIuput = (EditText) findViewById(R.id.id_input_msg);
		mSendMsg = (Button) findViewById(R.id.id_send_msg);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
	}

	private String userName;
}
