package com.example.imagesearch;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.imagesearch.Async.Block;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.StrictMode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class FullScreenImageAdapter extends PagerAdapter {

	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();

	private Activity mActivity;
	private LayoutInflater mInflater;
	private String mCurrentUrl;
	private final ArrayList<ImageResult> mResults;
	private final ProgressBar mProgressBar;
	private final ViewPager mViewPager;
	private static final int MAX_IMG_SIZE = 512;
	private HashMap<Integer, Bitmap> mBitmaps = new HashMap<Integer, Bitmap>();
	private HashMap<Integer, Bitmap> mThumbnails = new HashMap<Integer, Bitmap>();
	
	private static final ColorDrawable PLACEHOLDER = new ColorDrawable(Color.parseColor("#00000000"));

	public FullScreenImageAdapter(Activity activity, final ViewPager viewPager, final ProgressBar progressBar, final ArrayList<ImageResult> results) {
		mActivity = activity;
		mResults = results;
		mProgressBar = progressBar;
		mViewPager = viewPager;
	}

	@Override
	public int getCount() {
		return mResults.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}
	
	public void updateProgressBar(int position) {
		if (mBitmaps.get(position) == null) {
			mProgressBar.setVisibility(View.VISIBLE);
		} else {
			mProgressBar.setVisibility(View.INVISIBLE);
		}
	}
	
	public void checkBoundaryConditions(final int currentPosition) {
		if (currentPosition >= mResults.size() - 2) {
			final String url = Result.sharedResult().loadNextPage(mActivity, mCurrentUrl, new Async.Block<String, ArrayList<ImageResult>>() {
				@Override
				public void call(String url, ArrayList<ImageResult> result) {
					if (!url.equals(mCurrentUrl)) return;
					mResults.addAll(result);
					notifyDataSetChanged();
				}
			});
			if (url == null) return;
			mCurrentUrl = url;
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		StrictMode.setThreadPolicy(policy);
		checkBoundaryConditions(position);

		mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View viewLayout = mInflater.inflate(R.layout.activity_image_display,
				container, false);

		final ImageView imgDisplay = (ImageView) viewLayout.findViewById(R.id.ivResult);
		imgDisplay.setImageDrawable(PLACEHOLDER);

		((ViewPager) container).addView(viewLayout);
		if (mViewPager.getCurrentItem() == position) {
			mProgressBar.setVisibility(View.VISIBLE);
		}
		
		Async.decode(mActivity, mResults.get(position).getThumbUrl(), MAX_IMG_SIZE, MAX_IMG_SIZE, new Block<String, Bitmap>(){
			@Override
			public void call(final String url, final Bitmap thumbnail) {
				if (thumbnail != null) {
					mThumbnails.put(position, thumbnail);
					imgDisplay.setImageBitmap(thumbnail);
				}
				Async.decode(mActivity, mResults.get(position).getFullUrl(), MAX_IMG_SIZE, MAX_IMG_SIZE, new Block<String, Bitmap>(){
					@Override
					public void call(String url, Bitmap result) {
						if (mViewPager.getCurrentItem() == position) {
							mProgressBar.setVisibility(View.INVISIBLE);
						}
						if (result != null) {
							mBitmaps.put(position, result);
							imgDisplay.setImageBitmap(result);
						}
					}
				});
			}
		});
		return viewLayout;
	}
	
	public Bitmap getCachedBitmap(int position) {
		Bitmap full = mBitmaps.get(position);
		if (full == null) {
			return mThumbnails.get(position);
		}
		return full;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		final ImageView imgDisplay = (ImageView) container.findViewById(R.id.ivResult);
		imgDisplay.setImageDrawable(PLACEHOLDER);
		mThumbnails.remove(position);
		mBitmaps.remove(position);
	}

}
