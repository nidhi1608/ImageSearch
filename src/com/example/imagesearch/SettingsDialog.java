package com.example.imagesearch;

import com.example.imagesearch.ImageSearchActivity.FontsOverride;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class SettingsDialog extends DialogFragment {
	public SettingsDialog() {

	}

	public static SettingsDialog newInstance(String title) {
		SettingsDialog frag = new SettingsDialog();
		Bundle args = new Bundle();
		args.putString("title", title);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments().getString("title");
		LayoutInflater i = getActivity().getLayoutInflater();
		View view = i.inflate(R.layout.settings_dialog, null);
		FontsOverride.setDefaultFont(view.getContext(), "MONOSPACE");

		final Spinner spImageSize = (Spinner) view
				.findViewById(R.id.spImageSize);
		ArrayAdapter<CharSequence> imageSizeAdapter = ArrayAdapter
				.createFromResource(this.getActivity(),
						R.array.imageSize_array,
						android.R.layout.simple_spinner_item);
		imageSizeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spImageSize.setAdapter(imageSizeAdapter);

		final Spinner spImageColor = (Spinner) view
				.findViewById(R.id.spImageColor);
		ArrayAdapter<CharSequence> imageColorAdapter = ArrayAdapter
				.createFromResource(this.getActivity(),
						R.array.imageColor_array,
						android.R.layout.simple_spinner_item);
		imageColorAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spImageColor.setAdapter(imageColorAdapter);

		final Spinner spImageType = (Spinner) view
				.findViewById(R.id.spImageType);
		ArrayAdapter<CharSequence> imageTypeAdapter = ArrayAdapter
				.createFromResource(this.getActivity(),
						R.array.imageType_array,
						android.R.layout.simple_spinner_item);
		imageTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spImageType.setAdapter(imageTypeAdapter);

		final EditText etSiteFilter = (EditText) view
				.findViewById(R.id.etSiteFilter);

		final Settings settings = Settings.sharedInstance();
		spImageSize.setSelection(imageSizeAdapter.getPosition(settings.getSize()));
		spImageColor.setSelection(imageColorAdapter.getPosition(settings.getColor()));
		spImageType.setSelection(imageTypeAdapter.getPosition(settings.getType()));
		etSiteFilter.setText(settings.getSiteFilter());

		AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
				.setTitle(title)
				.setPositiveButton(getActivity().getString(R.string.btnOK), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						settings.setSize(spImageSize.getSelectedItem().toString());
						settings.setColor(spImageColor.getSelectedItem().toString());
						settings.setType(spImageType.getSelectedItem().toString());
						settings.setSiteFilter(etSiteFilter.getText().toString());
						mListener = (OnDialogCompleteListener) getActivity();
						mListener.onDialogComplete(settings);
					}
				})
				.setNegativeButton(getActivity().getString(R.string.btnCancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
							}
						});

		b.setView(view);
		return b.create();
	}

	public static interface OnDialogCompleteListener {
		public abstract void onDialogComplete(Settings settings);
	}

	private OnDialogCompleteListener mListener;

}
