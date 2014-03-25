package com.example.imagesearch;

import com.loopj.android.image.SmartImageView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;

public class SquareImageView extends SmartImageView {
	private String mUrl;
	private static final ColorDrawable PLACEHOLDER = new ColorDrawable(
			Color.parseColor("#ccccdd"));

	public SquareImageView(Context arg0) {
		super(arg0);
	}

	public SquareImageView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
	}

	public SquareImageView(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
	}

	public void setImageUrl(final String url) {
		if (mUrl != null && mUrl.equals(url))
			return;
		mUrl = url;
		setImageDrawable(PLACEHOLDER);
		super.setImageUrl(url);
	}
}
