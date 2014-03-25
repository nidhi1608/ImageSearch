package com.example.imagesearch;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private String fullUrl;
	private String thumbUrl;
	private String title;
	private String subtitle;

	public ImageResult(JSONObject json) {
		try {
			this.title = json.getString("contentNoFormatting");
			this.subtitle = json.getString("visibleUrl") + " - " + json.getString("titleNoFormatting");
			this.fullUrl = json.getString("url");
			this.thumbUrl = json.getString("tbUrl");
		} catch (JSONException e) {
			this.fullUrl = null;
			this.thumbUrl = null;
		}
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getSubtitle() {
		return subtitle;
	}

	public String getFullUrl() {
		return fullUrl;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public String toString() {
		return this.thumbUrl;
	}

	public static ArrayList<ImageResult> fromJSONArray(JSONArray array) {
		ArrayList<ImageResult> results = new ArrayList<ImageResult>();
		for (int x = 0; x < array.length(); x++) {
			try {
				results.add(new ImageResult(array.getJSONObject(x)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
	
}
