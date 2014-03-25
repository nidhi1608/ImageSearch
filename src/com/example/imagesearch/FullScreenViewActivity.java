package com.example.imagesearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FullScreenViewActivity extends Activity {
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_screen_view);
		viewPager = (ViewPager) findViewById(R.id.pager);
		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);
		adapter = new FullScreenImageAdapter(this, viewPager, mProgressBar, Result.sharedResult().imageResults);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(position);		
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setBackgroundDrawable(null);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int currentItem) {
				adapter.updateProgressBar(currentItem);
				setCurrentPosition(currentItem);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
	    TextView yourTextView = (TextView) findViewById(titleId);
	    yourTextView.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
	    
	    int subtitleId = getResources().getIdentifier("action_bar_subtitle", "id", "android");
	    TextView yourTextView2 = (TextView) findViewById(titleId);
	    yourTextView2.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
		setCurrentPosition(position);
	}
	
	public void setCurrentPosition(int position) {
		final ImageResult result = Result.sharedResult().imageResults.get(position);
		getActionBar().setTitle(result.getTitle());
		getActionBar().setSubtitle(result.getSubtitle());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.full_screen_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.miShare:
			Bitmap bitmap = adapter.getCachedBitmap(viewPager.getCurrentItem());
			File cache = getApplicationContext().getExternalCacheDir();
			File sharefile = new File(cache, "toshare" + System.currentTimeMillis() + ".jpg");
			try {
				FileOutputStream out = new FileOutputStream(sharefile);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
				out.flush();
				out.close();
			} catch (IOException e) {
				Log.e("FullScreenViewActivity", e.toString());
				e.printStackTrace();
			}

			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("image/*");
			shareIntent.putExtra(Intent.EXTRA_STREAM,
					Uri.parse("file://" + sharefile));
			startActivity(Intent.createChooser(shareIntent, getResources()
					.getString(R.string.share)));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private ProgressBar mProgressBar;

	@Override
	public void setContentView(View view) {
		init().addView(view);
	}

	@Override
	public void setContentView(int layoutResID) {
		getLayoutInflater().inflate(layoutResID, init(), true);
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		init().addView(view, params);
	}

	private ViewGroup init() {
		super.setContentView(R.layout.progress);
		mProgressBar = (ProgressBar) findViewById(R.id.activity_bar);
		mProgressBar.setVisibility(View.INVISIBLE);
		return (ViewGroup) findViewById(R.id.activity_frame);
	}

	protected ProgressBar getProgressBar() {
		return mProgressBar;
	}



}
