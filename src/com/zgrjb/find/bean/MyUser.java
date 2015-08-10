package com.zgrjb.find.bean;

import android.R.integer;
import android.os.Parcel;
import android.os.Parcelable;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class MyUser extends BmobChatUser {
	// avatar好友头像
	// nick 昵称
	// installId设备id
	// contacts好友列表

	// private boolean sex;// 性别 男为true ，女为false
	// private int age;
	private Boolean sex;// 性别 男为true ，女为false
	private Integer age;

	private BmobGeoPoint location;// 用户位置
	private String homePage; // 个人介绍
	private String province;// 地区，省，市

	private String brand;
	private String model;
	private String sdkVersion;

	private Boolean isQQ;

	public Boolean getIsQQ() {
		return isQQ;
	}

	public void setIsQQ(Boolean isQQ) {
		this.isQQ = isQQ;
	}

	public void setSex(Boolean sex) {
		this.sex = sex;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSdkVersion() {
		return sdkVersion;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public boolean getSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public BmobGeoPoint getLocation() {
		return location;
	}

	public void setLocation(BmobGeoPoint location) {
		this.location = location;
	}

	public String getHomePage() {
		return homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	@Override
	public String toString() {
		return "MyUser [sex=" + sex + ", age=" + age + ", location=" + location
				+ ", homePage=" + homePage + ", province=" + province
				+ ", brand=" + brand + ", model=" + model + ", sdkVersion="
				+ sdkVersion + "]";
	}

}
