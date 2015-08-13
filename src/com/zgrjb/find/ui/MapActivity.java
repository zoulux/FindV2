package com.zgrjb.find.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.task.BRequest;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.zgrjb.find.R;
import com.zgrjb.find.bean.MyUser;
import com.zgrjb.find.ui.puzzle_game.PreparePuzzleGameActivity;
import com.zgrjb.find.utils.CollectionUtils;

public class MapActivity extends BaseActivity {
	// ��λ���
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();
	private BDLocation lastLocation = null;
	private double QUERY_KILOMETERS = 100;
	private boolean isFirstLoc = true;
	private BitmapDescriptor bdicon = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_gcoding);
	private BitmapDescriptor bdBoy = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_gcoding2);
	private BitmapDescriptor bdGirl = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_gcoding1);
	InfoWindow mInfoWindow;
	private static MyUser user;

	AlertDialog.Builder builder;
	AlertDialog.Builder builder2;
	private int whatDiff;

	// marker
	Marker selectMarker;

	// Handler���
	Handler handlerQuery = new Handler() {
		public void handleMessage(android.os.Message msg) {
			List<MyUser> lists = (List<MyUser>) msg.obj;
			addOverlayConfig(lists);
		};
	};

	Handler handlerAddMarker = new Handler() {

		public void handleMessage(Message msg) {
			String userName = (String) msg.obj;

			user = mApplication.getNearPeople().get(userName);
			System.out.println(user.getUsername()
					+ "getUsername+?????????????????");
			System.out.println(user.getNick() + "getNick+????????????????????");
			System.out.println(user.getAvatar()
					+ "getAvatar+??????????????????????");

			LatLng ll = selectMarker.getPosition();

			TextView tv = new TextView(getApplicationContext());
			tv.setBackgroundResource(R.drawable.popup);
			tv.setTextColor(Color.parseColor("#333333"));
			tv.setText(user.getNick());
			tv.setGravity(Gravity.CENTER);
			OnInfoWindowClickListener listener = null;
			listener = new OnInfoWindowClickListener() {
				public void onInfoWindowClick() {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// ����Ϸ�ĶԻ���
							showDialogToPlayGame();
						}
					}, 1);

					mBaiduMap.hideInfoWindow();
				}
			};
			mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(tv),
					ll, -47, listener);// ����ť��InfoWindow��
			mBaiduMap.showInfoWindow(mInfoWindow);

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		initBD();
		setClick();
		builder = new AlertDialog.Builder(this);
		builder2 = new AlertDialog.Builder(this);

	}

	private void setClick() {

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				selectMarker = marker;
				// String id = marker.getExtraInfo().getString("id");
				// Message message = new Message();
				// message.obj = id;
				// handlerSelectThis.sendMessage(message);

				String username = marker.getExtraInfo().getString("username");

				Message message = new Message();
				message.obj = username;
				handlerAddMarker.sendMessage(message);
				/*
				 * new Handler().post(new Runnable() {
				 * 
				 * @Override public void run() { // ����Ϸ�ĶԻ���
				 * showDialogToPlayGame(); } });
				 */

				// new Handler().postDelayed(new Runnable() {
				//
				// @Override
				// public void run() {
				// // ����Ϸ�ĶԻ���
				// showDialogToPlayGame();
				// }
				// }, 1000);

				return false;
			}
		});
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi mPoint) {
				System.out.println(mPoint.getName());

				return false;
			}

			@Override
			public void onMapClick(LatLng ll) {
				mBaiduMap.hideInfoWindow();

			}
		});

	}

	public static MyUser getUser() {
		return user;
	}

	protected void addOverlayConfig(List<MyUser> lists) {

		for (MyUser user : lists) {
			BmobGeoPoint p = user.getLocation();
			if (p != null) {
				boolean sex = user.getSex();
				LatLng ll = new LatLng(p.getLatitude(), p.getLongitude());
				OverlayOptions oo = null;
				if (sex) {
					oo = new MarkerOptions().position(ll)
							.title(user.getUsername()).zIndex(5)
							.draggable(true).icon(bdBoy);
				} else {
					oo = new MarkerOptions().position(ll)
							.title(user.getUsername()).zIndex(5)
							.draggable(true).icon(bdGirl);
				}

				Marker marker = (Marker) mBaiduMap.addOverlay(oo);
				Bundle bundle = new Bundle();

				bundle.putString("username", user.getUsername());
				marker.setExtraInfo(bundle);

			}

			// Marker marker = (Marker) mBaiduMap.addOverlay(oo);
			// Bundle bundle = new Bundle();
			// bundle.putString("id", person.getObjectId());
			// marker.setExtraInfo(bundle);

		}

		mApplication.setNearPeople(CollectionUtils.list2maps(lists));

	}

	/**
	 * ͨ����·��ѯ��������
	 */
	private void queryNearPeople() {
		BRequest.QUERY_LIMIT_COUNT = 10;

		userManager.queryKiloMetersListByPage(false, 0, "location",
				lastLocation.getLongitude(), lastLocation.getLatitude(), true,
				QUERY_KILOMETERS, "sex", null, new FindListener<MyUser>() {

					@Override
					public void onError(int arg0, String arg1) {
						ShowToast("��ѯʧ��");

					}

					@Override
					public void onSuccess(List<MyUser> lists) {
						if (lists.size() == 0) {
							ShowToast("����û���ҵ�����");
						} else {
							Message msg = new Message();
							msg.obj = lists;
							handlerQuery.sendMessage(msg);
						}
					}
				});

	}

	private void initBD() {
		SDKInitializer.initialize(getApplicationContext());

		// ��ʼ����ͼ
		mMapView = (MapView) findViewById(R.id.id_map_bdmap);
		mBaiduMap = mMapView.getMap();

		// ������λ
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap
				.setMyLocationConfigeration(new MyLocationConfiguration(
						com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL,
						true, null));
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomBy(3.0f);
		mBaiduMap.setMapStatus(msu);
		// �������
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(myListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setIgnoreKillProcess(true);
		option.setScanSpan(1000);
		option.setLocationMode(LocationMode.Hight_Accuracy);
		mLocClient.setLocOption(option);
		mLocClient.start();

	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// �˳�ʱ���ٶ�λ
		mLocClient.stop();
		// �رն�λͼ��
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	private class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mMapView == null)
				return;

			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude()
						&& lastLocation.getLongitude() == location
								.getLongitude()) {
					BmobLog.i("��ȡ������ͬ");// �����������ȡ���ĵ���λ����������ͬ�ģ����ٶ�λ
					mLocClient.stop();
					return;
				}
			}
			lastLocation = location;

			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);

			if (isFirstLoc) {
				LatLng ll = new LatLng(lastLocation.getLatitude(),
						lastLocation.getLongitude());
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(msu);
				isFirstLoc = false;
				updateMyLocation();
				queryNearPeople();
			}

		}

	}

	public void updateMyLocation() {
		MyUser user = new MyUser();
		MyUser nowUser = userManager.getCurrentUser(MyUser.class);
		double lon = lastLocation.getLongitude();
		double lat = lastLocation.getLatitude();
		BmobGeoPoint point = new BmobGeoPoint(lon, lat);
		user.setLocation(point);
		user.setAge(nowUser.getAge());
		user.setSex(nowUser.getSex());
		String nowUserId = nowUser.getObjectId();

		user.update(MapActivity.this, nowUserId, new UpdateListener() {

			@Override
			public void onSuccess() {
				ShowToast("���µ�ǰλ�óɹ�");

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("���µ�ǰλ��ʧ��");

			}
		});

	}

	private void addFriend(MyUser user) {

		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("�������...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		// ����tag����
		BmobChatManager.getInstance(this).sendTagMessage(
				BmobConfig.TAG_ADD_CONTACT, user.getObjectId(),
				new PushListener() {

					@Override
					public void onSuccess() {
						progress.dismiss();
						ShowToast("��������ɹ����ȴ��Է���֤��");
					}

					@Override
					public void onFailure(int arg0, final String arg1) {
						progress.dismiss();
						ShowToast("��������ɹ����ȴ��Է���֤��");
						ShowLog("��������ʧ��:" + arg1);
					}
				});
	}

	/**
	 * ����Ϸ�ĶԻ���
	 */
	private void showDialogToPlayGame() {
		builder.setTitle("��ܰ��ʾ");
		builder.setIcon(R.drawable.pikaqiu);
		builder.setMessage("�Ӻ���ǰ��Ҫ����ϷŶ��");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				showDialogToChoiceDifficult();

			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});
		builder.show();
	}

	/**
	 * ѡ����Ϸ�Ѷȵ�dialog
	 */
	private void showDialogToChoiceDifficult() {
		builder2.setTitle("ƴͼ��Ϸ�Ѷ�ѡ��");
		final String[] hobby = { "����", "����", "����" };
		builder2.setSingleChoiceItems(hobby, 0,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						whatDiff = which;

					}
				});

		builder2.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				Intent intent = new Intent(MapActivity.this,
						PreparePuzzleGameActivity.class);
				intent.putExtra("diff", whatDiff + 6);
				intent.putExtra("diffValue", hobby[whatDiff]);
				intent.putExtra("user", MapActivity.getUser());
				MapActivity.this.startActivity(intent);
				MapActivity.this.finish();
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
		});
		builder2.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		builder2.show();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.quit_zoom_enter, R.anim.quit_zoom_exit);
	}
}
