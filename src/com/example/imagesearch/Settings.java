package com.example.imagesearch;

public class Settings {
	
	private String mSize;
	private String mColor;
	private String mType;
	private String mSiteFilter;

	private Settings() {
		clear();
	}
	
	private static Settings sInstance = new Settings();
	
	public static Settings sharedInstance() {
		return sInstance;
	}

	public String getSize() {
		return mSize;
	}

	public void setSize(String size) {
		mSize = size;
	}

	public String getColor() {
		return mColor;
	}

	public void setColor(String color) {
		mColor = color;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		mType = type;
	}

	public String getSiteFilter() {
		return mSiteFilter;
	}

	public void setSiteFilter(String siteFilter) {
		mSiteFilter = siteFilter;
	}

	public void clear() {
		mSize = "";
		mColor = "";
		mSiteFilter = "";
		mType = "";
	}

}
