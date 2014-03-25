package com.example.imagesearch;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageResultsArrayAdapter extends ArrayAdapter<ImageResult> {

	public ImageResultsArrayAdapter(Context context, List<ImageResult> images) {
		super(context, R.layout.item_image_result, images);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageResult imageInfo = this.getItem(position);
		SquareImageView ivImage;
		if (convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			ivImage = (SquareImageView) inflator.inflate(
					R.layout.item_image_result, parent, false);
		} else {
			ivImage = (SquareImageView) convertView;
		}
		ivImage.setImageUrl(imageInfo.getThumbUrl());
		return ivImage;
	}

	class ViewHolder {
		public ImageView image;
		public TextView title;
	}
}
