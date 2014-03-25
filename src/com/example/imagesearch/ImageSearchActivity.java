package com.example.imagesearch;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.example.imagesearch.Async.Block;
import com.example.imagesearch.SettingsDialog.OnDialogCompleteListener;

public class ImageSearchActivity extends Activity implements
		OnDialogCompleteListener {

	public static final class FontsOverride {

		public static void setDefaultFont(Context context,
				String staticTypefaceFieldName) {
			final Typeface regular = Typeface.create("Roboto-Light",
					Typeface.NORMAL);
			replaceFont(staticTypefaceFieldName, regular);
		}

		protected static void replaceFont(String staticTypefaceFieldName,
				final Typeface newTypeface) {
			try {
				final Field StaticField = Typeface.class
						.getDeclaredField(staticTypefaceFieldName);
				StaticField.setAccessible(true);
				StaticField.set(null, newTypeface);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private GridView gvResults;
	private MenuItem mSearchMenuItem;
	private static final int kPrefetch = 0;
	private ImageResultsArrayAdapter imageResultsArrayAdapter;
	private String mCurrentRequestUrl;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FontsOverride.setDefaultFont(this, "MONOSPACE");
		setContentView(R.layout.activity_image_search);		
		
		setupViews();
		handleIntent(getIntent());		

		imageResultsArrayAdapter = new ImageResultsArrayAdapter(this,
				Result.sharedResult().imageResults);
		gvResults.setAdapter(imageResultsArrayAdapter);

		gvResults.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View parent,
					int position, long rowId) {
				Intent i = new Intent(getApplicationContext(),
						FullScreenViewActivity.class);
				i.putExtra("position", position);
				startActivity(i);
			}
		});
		gvResults.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			} 

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				checkBoundaryConditions();
			}
		});
		getActionBar().setIcon(new ColorDrawable(Color.TRANSPARENT));
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setBackgroundDrawable(null);		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_search, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		mSearchMenuItem = (MenuItem) menu.findItem(R.id.miSearch);
		SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setQuery(Result.sharedResult().getQuery(), false);
		searchView.setIconifiedByDefault(false);
		searchView.setIconified(false);
		searchView.setMaxWidth(gvResults.getWidth() - getActionBar().getHeight());
		return true;
	}

	private void setupViews() {
		gvResults = (GridView) findViewById(R.id.gvResults);
	}

	public void onSettingsPress(MenuItem mi) {
		FragmentManager fmanager = this.getFragmentManager();
		SettingsDialog settingsDialog = SettingsDialog
				.newInstance(this.getString(R.string.advanced_search_options));
		settingsDialog.show(fmanager, "tag");
	}

	public void performQuery() {
		if (Result.sharedResult().getQuery() == null
				|| Result.sharedResult().getQuery().length() == 0)
			return;
		ImagesRecentSearchSuggestions.createSearchRecentSuggestions(this)
				.saveRecentQuery(Result.sharedResult().getQuery(), null);
		loadNextPage();
	}

	public void loadNextPage() {
		if (Result.sharedResult().getQuery() == null
				|| Result.sharedResult().getQuery().length() == 0)
			return;
		final String url = Result.sharedResult().loadNextPage(this, mCurrentRequestUrl,
				new Block<String, ArrayList<ImageResult>>() {
					@Override
					public void call(final String url,
							final ArrayList<ImageResult> result) {
						if (!url.equals(mCurrentRequestUrl))
							return;
						mProgressBar.setVisibility(View.INVISIBLE);
						imageResultsArrayAdapter.addAll(result);
						checkBoundaryConditions();
					}
				});
		if (url == null)
			return;
		mCurrentRequestUrl = url;
		if (Utils.isNetworkAvailable(ImageSearchActivity.this)) {
			mProgressBar.setVisibility(View.VISIBLE);
		}
	}

	public void checkBoundaryConditions() {
		int lastPosition = gvResults.getLastVisiblePosition();
		if (lastPosition >= Result.sharedResult().imageResults.size() - 1
				- kPrefetch) {
			loadNextPage();
		}
	}

	@Override
	public void onDialogComplete(final Settings settings) {
		Result.sharedResult().resetWithQuery(Result.sharedResult().getQuery());
		performQuery();
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			if (mSearchMenuItem != null) {
				mSearchMenuItem.getActionView().clearFocus();
			}
			Result.sharedResult().resetWithQuery(query);
			if (Utils.isNetworkAvailable(ImageSearchActivity.this)) {
				performQuery();
			} else {
				Utils.showAlertDialog(ImageSearchActivity.this, this.getString(R.string.network_error), false);
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

}
