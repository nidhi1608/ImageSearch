package com.example.imagesearch;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;

import com.example.imagesearch.Async.Block;
import com.loopj.android.http.JsonHttpResponseHandler;

public class Result {
	private static Result sResult = new Result();

	public static Result sharedResult() {
		return sResult;
	}

	private Result() {

	}

	public ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();

	private String query;

	public void resetWithQuery(final String query) {
		imageResults.clear();
		Result.this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public String loadNextPage(final Context context, final String currentUrl,
			final Block<String, ArrayList<ImageResult>> completion) {
		final Settings settings = Settings.sharedInstance();
		final String url = "https://ajax.googleapis.com/ajax/services/search/images?rsz=8&"
				+ "imgsz="
				+ settings.getSize()
				+ "&imgcolor="
				+ settings.getColor()
				+ "&imgtype="
				+ settings.getType()
				+ "&as_sitesearch="
				+ settings.getSiteFilter()
				+ "&start="
				+ Result.sharedResult().imageResults.size()
				+ "&v=1.0&q="
				+ Uri.encode(Result.sharedResult().getQuery());
		
			if (currentUrl != null && currentUrl.equals(url))
				return null;

			Async.getHttpClient().get(context, url,
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject response) {
							JSONArray imageJsonresults = null;
							try {
								imageJsonresults = response.getJSONObject(
										"responseData").getJSONArray("results");
								if (completion != null) {
									completion.call(url, (ImageResult
											.fromJSONArray(imageJsonresults)));
								}
							} catch (JSONException e) {
							}
						}
					});		
		return url;
	}
}
