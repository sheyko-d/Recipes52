package com.stockholmapplab.recipes;

import org.json.JSONArray;
import org.json.JSONException;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.stockholmapplab.recipes.R;

public class AddStep1Fragment extends DialogFragment {

	public static LinearLayout photoLayout;
	public static ImageLoader imgLoader;
	public static DisplayImageOptions options;
	public static HorizontalScrollView horizontalScrollView;
	private int mSignature = 1;
	private View mRoot;
	public static Button button2;
	public static Button button1;
	public static Button button3;
	public static EditText titleEdittxt;
	public static Boolean dialogClicked = false;

	public static AddStep1Fragment newInstance() {
		AddStep1Fragment f = new AddStep1Fragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.fr_add_recipe_1, container, false);
		photoLayout = (LinearLayout) mRoot.findViewById(R.id.photoLayout);
		horizontalScrollView = (HorizontalScrollView) mRoot
				.findViewById(R.id.horizontalScrollView);

		// initialise ImageLoader
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getActivity()).build();
		imgLoader = ImageLoader.getInstance();
		imgLoader.init(config);
		options = new DisplayImageOptions.Builder().displayer(
				new FadeInBitmapDisplayer(300)).build();

		button1 = (Button) mRoot.findViewById(R.id.button1);
		button2 = (Button) mRoot.findViewById(R.id.button2);
		button3 = (Button) mRoot.findViewById(R.id.button3);

		button1.setOnClickListener(signatureClickListener);
		button2.setOnClickListener(signatureClickListener);
		button3.setOnClickListener(signatureClickListener);

		if (dialogClicked) {
			if (PrefHelper.getInt("AddSignature", 1) == 2) {
				setButtonBg(button1, R.drawable.add_btn_1_selected_patch);
			} else if (PrefHelper.getInt("AddSignature", 1) == 1) {
				setButtonBg(button2, R.drawable.add_btn_2_selected_patch);
			} else if (PrefHelper.getInt("AddSignature", 1) == 0) {
				setButtonBg(button3, R.drawable.add_btn_3_selected_patch);
			}
			Log.d("Log", "add photos2");
			AddPhotos();
		} else {
			setButtonBg(button2, R.drawable.add_btn_2_selected_patch);
		}

		mRoot.findViewById(R.id.addButton).setOnClickListener(addPhotoListener);
		mRoot.findViewById(R.id.addPhoto).setOnClickListener(addPhotoListener);

		mRoot.findViewById(R.id.editPhoto).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						for (int i = 0; i < photoLayout.getChildCount(); i++) {
							View deleteTxt = photoLayout.getChildAt(i)
									.findViewById(R.id.deleteTxt);
							if (deleteTxt.getVisibility() == View.GONE) {
								deleteTxt.setVisibility(View.VISIBLE);
							} else {
								deleteTxt.setVisibility(View.GONE);
							}
							deleteTxt.setTag(i + "");
							deleteTxt.setOnClickListener(removePhotoListener);
						}
					}
				});

		titleEdittxt = (EditText) mRoot.findViewById(R.id.addTitleEdittxt);
		titleEdittxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				PrefHelper.setString("AddTitle", titleEdittxt.getText()
						.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		registerForContextMenu(mRoot.findViewById(R.id.addButton));
		registerForContextMenu(mRoot.findViewById(R.id.addPhoto));

		return mRoot;
	}

	@Override
	public void setMenuVisibility(final boolean visible) {
		super.setMenuVisibility(visible);
		if (visible) {
			AddRecipeActivity.pagerPos = 0;
		}
	}

	OnClickListener removePhotoListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			JSONArray oldPhotoJSON;
			try {
				oldPhotoJSON = new JSONArray(PrefHelper.getString("AddPhotos",
						"[]"));
				JSONArray photoJSON = new JSONArray();
				int len = oldPhotoJSON.length();
				for (int i = 0; i < len; i++) {
					// Excluding the item at position
					if (i != Integer.parseInt(v.getTag() + "")) {
						photoJSON.put(oldPhotoJSON.get(i));
					}
				}
				PrefHelper.setString("AddPhotos", photoJSON.toString());
				Log.d("Log", "add photos1");
				AddPhotos();
			} catch (JSONException e) {
				Toast.makeText(BaseApplication.getAppContext(),
						R.string.str_error_remove, Toast.LENGTH_SHORT).show();
			}
		}
	};

	// Add old photos
	public static void AddPhotos() {

		try {
			photoLayout.removeAllViews();
			JSONArray oldPhotoJSON = new JSONArray(PrefHelper.getString(
					"AddPhotos", "[]"));
			for (int i = 0; i < oldPhotoJSON.length(); i++) {
				View photoFrame = AddRecipeActivity.activity
						.getLayoutInflater().inflate(R.layout.photo, null);
				ImageView photoImage = (ImageView) photoFrame
						.findViewById(R.id.photoImg);
				photoLayout.addView(photoFrame);
				Log.d("Log", oldPhotoJSON.getString(i));
				imgLoader.displayImage(oldPhotoJSON.getString(i), photoImage,
						options);
				horizontalScrollView
						.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			}
		} catch (JSONException e) {
		}
	}

	OnClickListener addPhotoListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				JSONArray oldPhotoJSON = new JSONArray(PrefHelper.getString(
						"AddPhotos", "[]"));
				if (oldPhotoJSON.length() >= 5) {
					Toast.makeText(
							BaseApplication.getAppContext(),
							getResources().getString(
									R.string.str_add_photo_count_error),
							Toast.LENGTH_SHORT).show();
				} else {
					getActivity().openContextMenu(v);
				}
			} catch (Exception e) {
				Toast.makeText(BaseApplication.getAppContext(),
						getResources().getString(R.string.str_add_photo_error),
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	OnClickListener signatureClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				if (mSignature != 2) {
					setButtonBg(v, R.drawable.add_btn_1_selected_patch);
					setButtonBg(button2, R.drawable.add_btn_2_selector);
					setButtonBg(button3, R.drawable.add_btn_3_selector);
					mSignature = 2;
					PrefHelper.setInt("AddSignature", mSignature);
				}
				break;
			case R.id.button2:
				if (mSignature != 1) {
					setButtonBg(v, R.drawable.add_btn_2_selected_patch);
					setButtonBg(button1, R.drawable.add_btn_1_selector);
					setButtonBg(button3, R.drawable.add_btn_3_selector);
					mSignature = 1;
					PrefHelper.setInt("AddSignature", mSignature);
				}
				break;
			case R.id.button3:
				if (mSignature != 0) {
					setButtonBg(v, R.drawable.add_btn_3_selected_patch);
					setButtonBg(button1, R.drawable.add_btn_1_selector);
					setButtonBg(button2, R.drawable.add_btn_2_selector);
					mSignature = 0;
					PrefHelper.setInt("AddSignature", mSignature);
				}
				break;
			default:
				break;
			}
		}
	};

	public static void setButtonBg(final View v, final int backgroundResId) {
		final int paddingBottom = v.getPaddingBottom(), paddingLeft = v
				.getPaddingLeft();
		final int paddingRight = v.getPaddingRight(), paddingTop = v
				.getPaddingTop();
		v.setBackgroundResource(backgroundResId);
		v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}

}