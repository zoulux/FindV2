package com.zgrjb.find.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.sax.StartElementListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.BmobDownloadManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.inteface.DownloadListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.config.ImgUir;
import com.zgrjb.find.ui.FriendsDataActivity;
import com.zgrjb.find.ui.ObservePictureAcitivity;
import com.zgrjb.find.utils.ImageLoadOptions;
import com.zgrjb.find.utils.NewRecordPlayClickListener;
import com.zgrjb.find.utils.TimeUtil;

public class ChartAdapter extends BaseAdapter {
	// 6��Item������
	// �ı�
	private final static int TYPE_RECEIVER_TXT = 0;
	private final static int TYPE_SEND_TXT = 1;
	// ͼƬ
	private final static int TYPE_RECEIVER_IMAGE = 3;
	private final static int TYPE_SEND_IMAGE = 2;

	// ����
	private final static int TYPE_SEND_VOICE = 4;
	private final static int TYPE_RECEIVER_VOICE = 5;

	private LayoutInflater mInflater;
	private List<BmobMsg> mDates;
	private String currentObjectId;
	private Context mContext;
	// ����ʱ���͵�ͼƬ
	private ImageView sendImageView;
	// �����ʱ���յ�ͼƬ
	private ImageView recieveImageView;

	private MyUser friendUser;
	// ������ͼƬ����λͼ��

	// private Bitmap bmAvertar = BitmapFactory.decodeFile(ImgUir.ALBUM_PATH
	// + "cut.jpg");

	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.bitmapConfig(Bitmap.Config.RGB_565)
			.showImageForEmptyUri(R.drawable.ic_launcher)
			.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
			.cacheOnDisc(true).build();

	// DisplayImageOptions options = new DisplayImageOptions.Builder()
	// .showImageForEmptyUri(R.drawable.ic_launcher)
	// .showImageOnFail(R.drawable.ic_launcher)
	// .resetViewBeforeLoading(true).cacheOnDisc(true).cacheInMemory(true)
	// .imageScaleType(ImageScaleType.EXACTLY)
	// .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
	// .displayer(new FadeInBitmapDisplayer(300)).build();;

	private AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();

	private BmobMsg message;

	public ChartAdapter(Context context, List<BmobMsg> list, MyUser user) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mDates = list;
		currentObjectId = BmobUserManager.getInstance(context)
				.getCurrentUserObjectId();
		friendUser = user;
	}

	@Override
	public int getCount() {
		return mDates.size();
	}

	@Override
	public Object getItem(int position) {
		return mDates.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * �������ͷ��ʱ����ת�����ѵ�������
	 * 
	 * @param img
	 *            ����ͷ��
	 */
	private void setOnClick(ImageView img) {
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, FriendsDataActivity.class);
				intent.putExtra("user", friendUser);
				intent.putExtra("isChat",true);
				mContext.startActivity(intent);
			}
		});

	}
	
	

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final BmobMsg message = mDates.get(position);
		int msgType = getItemViewType(position);

		ViewHolde viewHolde = null;

		if (convertView == null) {
			if (msgType == TYPE_RECEIVER_TXT) {
				convertView = mInflater.inflate(
						com.zgrjb.find.R.layout.item_chat_received_message,
						parent, false);
				viewHolde = new ViewHolde();

				viewHolde.tv_time = (TextView) convertView
						.findViewById(R.id.recevied_tv_time);
				viewHolde.iv_avatar = (ImageView) convertView
						.findViewById(com.zgrjb.find.R.id.iv_avatar);
				viewHolde.tv_message = (TextView) convertView
						.findViewById(com.zgrjb.find.R.id.tv_message);
				setOnClick(viewHolde.iv_avatar);

			} else if (msgType == TYPE_SEND_TXT) {
				convertView = mInflater.inflate(
						com.zgrjb.find.R.layout.item_chat_sent_message, parent,
						false);
				viewHolde = new ViewHolde();
				viewHolde.tv_time = (TextView) convertView
						.findViewById(R.id.sent_tv_time);
				viewHolde.iv_avatar = (ImageView) convertView
						.findViewById(R.id.iv_avatar);
				viewHolde.tv_message = (TextView) convertView
						.findViewById(com.zgrjb.find.R.id.tv_message);
				viewHolde.iv_fail_resend = (ImageView) convertView
						.findViewById(R.id.iv_fail_resend);
				viewHolde.tv_send_status = (TextView) convertView
						.findViewById(R.id.tv_send_status);
				viewHolde.pb_upLoad = (ProgressBar) convertView
						.findViewById(R.id.progress_load);
				// ͨ��λͼ���ر���ͼƬ
				// viewHolde.iv_avatar.setImageBitmap(bmAvertar);

			} else if (msgType == TYPE_RECEIVER_VOICE) {
				convertView = mInflater.inflate(
						com.zgrjb.find.R.layout.item_chat_received_voice,
						parent, false);
				viewHolde = new ViewHolde();

				viewHolde.tv_time = (TextView) convertView
						.findViewById(com.zgrjb.find.R.id.tv_time);
				viewHolde.iv_avatar = (ImageView) convertView
						.findViewById(com.zgrjb.find.R.id.iv_avatar);
				viewHolde.ll_voiceLength = (LinearLayout) convertView
						.findViewById(R.id.layout_voice);
				viewHolde.iv_voiceIcon = (ImageView) convertView
						.findViewById(R.id.iv_voice);
				viewHolde.tv_voiceLength = (TextView) convertView
						.findViewById(R.id.tv_voice_length);
				viewHolde.iv_fail_resend = (ImageView) convertView
						.findViewById(R.id.iv_fail_resend);
				viewHolde.tv_send_status = (TextView) convertView
						.findViewById(R.id.tv_send_status);
				viewHolde.pb_upLoad = (ProgressBar) convertView
						.findViewById(R.id.progress_load);
				setOnClick(viewHolde.iv_avatar);

			} else if (msgType == TYPE_SEND_VOICE) {
				convertView = mInflater.inflate(
						com.zgrjb.find.R.layout.item_chat_sent_voice, parent,
						false);
				viewHolde = new ViewHolde();
				viewHolde.tv_time = (TextView) convertView
						.findViewById(com.zgrjb.find.R.id.tv_time);
				viewHolde.iv_avatar = (ImageView) convertView
						.findViewById(com.zgrjb.find.R.id.iv_avatar);
				viewHolde.ll_voiceLength = (LinearLayout) convertView
						.findViewById(R.id.layout_voice);
				viewHolde.iv_voiceIcon = (ImageView) convertView
						.findViewById(R.id.iv_voice);
				viewHolde.tv_voiceLength = (TextView) convertView
						.findViewById(R.id.tv_voice_length);
				viewHolde.iv_fail_resend = (ImageView) convertView
						.findViewById(R.id.iv_fail_resend);
				viewHolde.tv_send_status = (TextView) convertView
						.findViewById(R.id.tv_send_status);
				viewHolde.pb_upLoad = (ProgressBar) convertView
						.findViewById(R.id.progress_load);
				// ͨ��λͼ���ر���ͼƬ
				// viewHolde.iv_avatar.setImageBitmap(bmAvertar);
			} else if (msgType == TYPE_SEND_IMAGE) {
				convertView = mInflater.inflate(R.layout.item_chat_sent_image,
						parent, false);
				viewHolde = new ViewHolde();
				viewHolde.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);
				viewHolde.iv_avatar = (ImageView) convertView
						.findViewById(R.id.iv_avatar);
				viewHolde.iv_picture = (ImageView) convertView
						.findViewById(R.id.iv_picture);
				viewHolde.iv_fail_resend = (ImageView) convertView
						.findViewById(R.id.iv_fail_resend);
				viewHolde.tv_send_status = (TextView) convertView
						.findViewById(R.id.tv_send_status);
				viewHolde.pb_upLoad = (ProgressBar) convertView
						.findViewById(R.id.progress_load);
				// ͨ��λͼ���ر���ͼƬ
				// viewHolde.iv_avatar.setImageBitmap(bmAvertar);
			} else if (msgType == TYPE_RECEIVER_IMAGE) {
				convertView = mInflater.inflate(
						R.layout.item_chat_received_image, parent, false);
				viewHolde = new ViewHolde();
				viewHolde.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);
				viewHolde.iv_avatar = (ImageView) convertView
						.findViewById(R.id.iv_avatar);
				viewHolde.iv_picture = (ImageView) convertView
						.findViewById(R.id.iv_picture);
				viewHolde.pb_upLoad = (ProgressBar) convertView
						.findViewById(R.id.progress_load);
				setOnClick(viewHolde.iv_avatar);
			}

			convertView.setTag(viewHolde);
		} else {
			viewHolde = (ViewHolde) convertView.getTag();
		}

		// ����
		viewHolde.tv_time.setText(TimeUtil.getChatTime(Long.parseLong(message
				.getMsgTime())));
		// viewHolde.tv_time.setText(TimeUtil.getDescriptionTimeFromTimestamp(new
		// Date().getTime()));

		String photo = message.getBelongAvatar();
		if (TextUtils.isEmpty(photo)) {
			viewHolde.iv_avatar.setBackgroundResource(R.drawable.child); // ͷ��
		} else {
			ImageLoader.getInstance().displayImage(photo, viewHolde.iv_avatar,
					ImageLoadOptions.getOptions(), animateFirstListener);

			// viewHolde.iv_avatar.setBackgroundResource(R.drawable.child);

		}

		final ProgressBar pb_upLoad = viewHolde.pb_upLoad; // ������

		String content = message.getContent();
		if (msgType == TYPE_SEND_TXT) {
			handleStatus(viewHolde, message);
			viewHolde.tv_message.setText(content);

		} else if (msgType == TYPE_RECEIVER_TXT) {

			viewHolde.tv_message.setText(content);

		} else if (msgType == TYPE_SEND_VOICE) {
			final TextView tv_voice_length = viewHolde.tv_voiceLength; // ��������
			final ImageView iv_voice = viewHolde.iv_voiceIcon; // ���������Ķ���
			iv_voice.setOnClickListener(new NewRecordPlayClickListener(
					mContext, message, iv_voice));

			if (!TextUtils.isEmpty(content)) {
				handleStatus(viewHolde, message);

				if (message.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED
						|| message.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {
					tv_voice_length.setVisibility(View.VISIBLE);
					String length = content.split("&")[2];
					tv_voice_length.setText(length + "\'");

				} else {
					viewHolde.tv_voiceLength.setVisibility(View.INVISIBLE);
				}

			}
		} else if (msgType == TYPE_RECEIVER_VOICE) {
			final TextView tv_voice_length = viewHolde.tv_voiceLength; // ��������
			final ImageView iv_voice = viewHolde.iv_voiceIcon; // ���������Ķ���
			iv_voice.setOnClickListener(new NewRecordPlayClickListener(
					mContext, message, iv_voice));

			if (!TextUtils.isEmpty(content)) {
				System.out.println(message.getObjectId() + ":" + "msgid");
				boolean isExists = BmobDownloadManager.checkTargetPathExist(
						currentObjectId, message);
				if (!isExists) {// ��ָ����ʽ��¼���ļ������ڣ�����Ҫ���أ���Ϊ���ļ��Ƚ�С���ʷ��ڴ�����
					String netUrl = content.split("&")[0];
					final String length = content.split("&")[1];
					BmobDownloadManager downloadTask = new BmobDownloadManager(
							mContext, message, new DownloadListener() {

								@Override
								public void onStart() {
									pb_upLoad.setVisibility(View.VISIBLE);
									tv_voice_length.setVisibility(View.GONE);
									iv_voice.setVisibility(View.INVISIBLE);// ֻ��������ɲ���ʾ���ŵİ�ť
								}

								@Override
								public void onSuccess() {
									pb_upLoad.setVisibility(View.GONE);
									tv_voice_length.setVisibility(View.VISIBLE);
									tv_voice_length.setText(length + "\''");
									iv_voice.setVisibility(View.VISIBLE);
								}

								@Override
								public void onError(String error) {
									pb_upLoad.setVisibility(View.GONE);
									tv_voice_length.setVisibility(View.GONE);
									iv_voice.setVisibility(View.INVISIBLE);
								}
							});
					downloadTask.execute(netUrl);
				} else {
					// String length = content.split("&")[2]; // ��Խ��
					// tv_voice_length.setText(length + "\''");
				}
			}

		} else if (msgType == TYPE_SEND_IMAGE) {
			if (content != null && !content.equals("")) {// ���ͳɹ�֮��洢��ͼƬ���͵�content�ͽ��յ����ǲ�һ����
				dealWithImage(position, pb_upLoad, viewHolde.iv_fail_resend,
						viewHolde.tv_send_status, viewHolde.iv_picture, message);
				this.message = message;
				final Bitmap bmp = viewHolde.iv_picture.getDrawingCache();
				sendImageView = viewHolde.iv_picture;
				viewHolde.iv_picture.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						sendPictureToObserve(message);
					}
				});
			}

		} else if (msgType == TYPE_RECEIVER_IMAGE) {
			dealWithImage(position, pb_upLoad, viewHolde.iv_fail_resend,
					viewHolde.tv_send_status, viewHolde.iv_picture, message);
			this.message = message;
			recieveImageView = viewHolde.iv_picture;
			viewHolde.iv_picture.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// ��ͼƬ���ݵ�ObservePictureAcitivity�Ŵ���ʾ
					sendPictureToObserve(message);
				}
			});
		}

		return convertView;

	}

	// ��ͼƬ���ݵ�ObservePictureAcitivity�Ŵ���ʾ
	private void sendPictureToObserve(BmobMsg message) {
		Intent intent = new Intent(mContext, ObservePictureAcitivity.class);
		ArrayList<String> photos = new ArrayList<String>();
		photos.add(getImageUrl(message));
		intent.putStringArrayListExtra("photos", photos);
		intent.putExtra("position", 0);
		mContext.startActivity(intent);

	}

	/**
	 * ��ȡͼƬ�ĵ�ַ--
	 * 
	 * @Description: TODO
	 * @param @param item
	 * @param @return
	 * @return String
	 * @throws
	 */
	private String getImageUrl(BmobMsg item) {
		String showUrl = "";
		String text = item.getContent();
		if (item.getBelongId().equals(currentObjectId)) {//
			if (text.contains("&")) {
				showUrl = text.split("&")[0];
			} else {
				showUrl = text;
			}
		} else {// ������յ�����Ϣ������Ҫ����������
			showUrl = text;
		}
		return showUrl;
	}

	private void dealWithImage(int position, final ProgressBar pb_upLoad,
			ImageView iv_fail_resend, TextView tv_send_status,
			ImageView iv_picture, BmobMsg item) {
		String text = item.getContent();
		if (getItemViewType(position) == TYPE_SEND_IMAGE) {// ���͵���Ϣ
			if (item.getStatus() == BmobConfig.STATUS_SEND_START) {
				pb_upLoad.setVisibility(View.VISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {
				pb_upLoad.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("�ѷ���");
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_FAIL) {
				pb_upLoad.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.VISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED) {
				pb_upLoad.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("���Ķ�");
			}
			// ����Ƿ��͵�ͼƬ�Ļ�����Ϊ��ʼ���ʹ洢�ĵ�ַ�Ǳ��ص�ַ�����ͳɹ�֮��洢���Ǳ��ص�ַ+"&"+�����ַ�������Ҫ�ж���
			String showUrl = "";
			if (text.contains("&")) {
				showUrl = text.split("&")[0];
			} else {
				showUrl = text;
			}
			// Ϊ�˷���ÿ�ζ���ȡ����ͼƬ��ʾ
			ImageLoader.getInstance().displayImage(showUrl, iv_picture);
		} else {
			ImageLoader.getInstance().displayImage(text, iv_picture, options,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							pb_upLoad.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							pb_upLoad.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							pb_upLoad.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							pb_upLoad.setVisibility(View.INVISIBLE);
						}
					});
		}

	}

	private void handleStatus(ViewHolde viewHolde, BmobMsg message) {

		if (message.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {// ���ͳɹ�
			viewHolde.pb_upLoad.setVisibility(View.GONE);
			viewHolde.iv_fail_resend.setVisibility(View.GONE);
			if (message.getMsgType() == BmobConfig.TYPE_VOICE) {
				viewHolde.tv_send_status.setVisibility(View.GONE);
				viewHolde.tv_voiceLength.setVisibility(View.VISIBLE);
			} else {
				viewHolde.tv_send_status.setVisibility(View.VISIBLE);
				viewHolde.tv_send_status.setText("�ѷ���");
			}
		} else if (message.getStatus() == BmobConfig.STATUS_SEND_FAIL) {// ����������Ӧ���߲�ѯʧ�ܵ�ԭ����ɵķ���ʧ�ܣ�����Ҫ�ط�
			viewHolde.pb_upLoad.setVisibility(View.GONE);

			viewHolde.iv_fail_resend.setVisibility(View.VISIBLE);
			viewHolde.tv_send_status.setVisibility(View.INVISIBLE);
			if (message.getMsgType() == BmobConfig.TYPE_VOICE) {
				viewHolde.tv_voiceLength.setVisibility(View.GONE);
			}
		} else if (message.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED) {// �Է��ѽ��յ�
			viewHolde.pb_upLoad.setVisibility(View.GONE);
			viewHolde.iv_fail_resend.setVisibility(View.GONE);
			if (message.getMsgType() == BmobConfig.TYPE_VOICE) {
				viewHolde.tv_send_status.setVisibility(View.GONE);
				viewHolde.tv_voiceLength.setVisibility(View.VISIBLE);
			} else {

				viewHolde.tv_send_status.setVisibility(View.VISIBLE);
				viewHolde.tv_send_status.setText("���Ķ�");
			}
		} else if (message.getStatus() == BmobConfig.STATUS_SEND_START) {// ��ʼ�ϴ�
			viewHolde.pb_upLoad.setVisibility(View.VISIBLE);
			viewHolde.iv_fail_resend.setVisibility(View.INVISIBLE);
			viewHolde.tv_send_status.setVisibility(View.INVISIBLE);
			if (message.getMsgType() == BmobConfig.TYPE_VOICE) {
				viewHolde.tv_voiceLength.setVisibility(View.GONE);
			}
		}

	}

	@Override
	public int getItemViewType(int position) {
		BmobMsg msg = mDates.get(position);

		if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
			return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_IMAGE
					: TYPE_RECEIVER_IMAGE;
		} else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
			return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_VOICE
					: TYPE_RECEIVER_VOICE;
		} else if (msg.getMsgType() == BmobConfig.TYPE_TEXT) {
			return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT
					: TYPE_RECEIVER_TXT;
		} else {
			return -1;
		}

	}

	@Override
	public int getViewTypeCount() {
		return 6;

	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
