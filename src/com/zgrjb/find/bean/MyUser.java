package com.zgrjb.find.bean;

import android.R.integer;
import android.os.Parcel;
import android.os.Parcelable;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class MyUser extends BmobChatUser {
	// avatar����ͷ��
	// nick �ǳ�
	// installId�豸id
	// contacts�����б�

	// private boolean sex;// �Ա� ��Ϊtrue ��ŮΪfalse
	// private int age;
	private Boolean sex;// �Ա� ��Ϊtrue ��ŮΪfalse
	private Integer age;

	private BmobGeoPoint location;// �û�λ��
	private String homePage; // ���˽���
	private String province;// ������ʡ����

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
