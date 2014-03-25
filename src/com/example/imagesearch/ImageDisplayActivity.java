package com.example.imagesearch;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class ImageDisplayActivity extends Activity {
	ImageResult result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_display, menu);

		return true;
	}

}
