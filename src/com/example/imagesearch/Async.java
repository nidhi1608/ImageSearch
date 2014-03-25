package com.example.imagesearch;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class Async {
	private static class RunnableTask extends AsyncTask<Object, Object, Object> {
		private final Runnable mRunnable;

		public RunnableTask(final Runnable runnable) {
			mRunnable = runnable;
		}

		@Override
		protected Object doInBackground(Object... params) {
			mRunnable.run();
			return null;
		}
	}

	public static interface Block<U, V> {
		public void call(U context, V result);
	}

	private static AsyncHttpClient HTTP_CLIENT = new AsyncHttpClient();
	{
		HTTP_CLIENT.setMaxConnections(4);
		HTTP_CLIENT.setMaxRetriesAndTimeout(3, 10);
	}

	public static AsyncHttpClient getHttpClient() {
		return HTTP_CLIENT;
	}

	public static void dispatch(final Runnable runnable) {
		new RunnableTask(runnable).execute(null, null, null);
	}

	private static final Handler MAIN_HANDLER = new Handler(
			Looper.getMainLooper());

	public static void dispatchMain(final Runnable runnable) {
		MAIN_HANDLER.post(runnable);
	}

	public static void dispatchMain(final Runnable runnable, final long delayMs) {
		MAIN_HANDLER.postDelayed(runnable, delayMs);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static void decode(final Context context, final String url,
			final int requiredWidth, final int requiredHeight,
			final Block<String, Bitmap> completion) {
		Async.getHttpClient().get(context, url,
				new BinaryHttpResponseHandler() {
					@Override
					public void onSuccess(final byte[] bytes) {
						Async.dispatch(new Runnable() {
							@Override
							public void run() {
								BitmapFactory.Options options = new BitmapFactory.Options();
								options.inJustDecodeBounds = true;
								BitmapFactory.decodeByteArray(bytes, 0,
										bytes.length, options);
								options.inSampleSize = calculateInSampleSize(
										options, requiredWidth, requiredHeight);
								options.inJustDecodeBounds = false;
								Bitmap bm = null;
								try {
									bm = BitmapFactory.decodeByteArray(bytes,
											0, bytes.length, options);
								} catch (Exception e) {
								} catch (Error err) {
								}
								final Bitmap bitmap = bm;
								Async.dispatchMain(new Runnable() {
									@Override
									public void run() {
										if (bitmap == null) {
											Log.d("ImageResult",
													"Bitmap not decoded for "
															+ url);
										}
										if (completion != null) {
											completion.call(url, bitmap);
										}
									}
								});
							}
						});
					}

					@Override
					public void onFailure(int statusCode,
							org.apache.http.Header[] headers,
							byte[] binaryData, java.lang.Throwable error) {
						if (completion != null) {
							completion.call(url, null);
						}
					}
				});
	}
}
