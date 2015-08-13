package com.zgrjb.find.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;

/**
 * �Զ���ȫ��Applcation��
 * 
 * @ClassName: CustomApplcation
 * @Description: TODO
 * @author
 * @date
 */
public class CustomApplcation extends Application {

	public static CustomApplcation mInstance;
	public LocationClient mLocationClient;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// �Ƿ���debugģʽ--Ĭ�Ͽ���״̬
		BmobChat.DEBUG_MODE = true;
		mInstance = this;
		init();
	}

	private void init() {

		mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		initImageLoader(getApplicationContext());
		// ���û���½�������ȴӺ������ݿ���ȡ������list�����ڴ���
		if (BmobUserManager.getInstance(getApplicationContext())
				.getCurrentUser() != null) {
			// ��ȡ���غ���user list���ڴ�,�����Ժ��ȡ����list

			contactList = CollectionUtils.list2map(BmobDB.create(
					getApplicationContext()).getContactList());
		}

	}

	/** ��ʼ��ImageLoader */
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"bmobim/Cache");// ��ȡ�������Ŀ¼��ַ
		// ��������ImageLoader(���е�ѡ��ǿ�ѡ��,ֻʹ����Щ������붨��)����������趨��APPLACATION���棬����Ϊȫ�ֵ����ò���
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// �̳߳��ڼ��ص�����
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// �������ʱ���URI������MD5 ����
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiscCache(cacheDir))// �Զ��建��·��
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// ȫ�ֳ�ʼ��������
	}

	public static CustomApplcation getInstance() {
		return mInstance;
	}

	// ����ģʽ�����ܼ�ʱ��������
	SharePreferenceUtil mSpUtil; // ��������
	public static final String PREFERENCE_NAME = "_sharedinfo";

	public synchronized SharePreferenceUtil getSharePreferenceUtil() {
		if (mSpUtil == null) {
			String currentId = BmobUserManager.getInstance(
					getApplicationContext()).getCurrentUserObjectId();
			String sharedName = currentId + PREFERENCE_NAME;
			mSpUtil = new SharePreferenceUtil(this, sharedName);
		}
		return mSpUtil;
	}

	NotificationManager mNotificationManager;

	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}

	MediaPlayer mMediaPlayer;

	public synchronized MediaPlayer getMediaPlayer() {
		if (mMediaPlayer == null)
			mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		return mMediaPlayer;
	}

	// ɾ����ȡ��γ�ȵ���ش��룬��ΪӦ��ֻ���ڵ�ͼ����Ż��޸�λ����ش���

	private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();
	private Map<String, MyUser> nearPeople = new HashMap<String, MyUser>();

	/**
	 * ��ȡ�ڴ��к���user list
	 * 
	 * @return
	 */
	public Map<String, BmobChatUser> getContactList() {
		return contactList;
	}

	public Map<String, MyUser> getNearPeople() {
		return nearPeople;
	}

	/**
	 * ���ú���user list���ڴ���
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, BmobChatUser> contactList) {
		if (this.contactList != null) {
			this.contactList.clear();
		}
		this.contactList = contactList;
	}

	public void setNearPeople(Map<String, MyUser> nearPeople) {
		if (this.nearPeople != null) {
			this.nearPeople.clear();
		}
		this.nearPeople = nearPeople;
	}

	/**
	 * �˳���¼,��ջ�������
	 */
	public void logout() {
		BmobUserManager.getInstance(getApplicationContext()).logout();
		setContactList(null);
		setNearPeople(null);
	}

	List<MyUser> nearbyUser = new ArrayList<MyUser>();

	public List<MyUser> getNearbyUser() {
		return nearbyUser;
	}

	public void setNearbyUser(List<MyUser> nearbyUser) {
		this.nearbyUser = nearbyUser;
	}

}
