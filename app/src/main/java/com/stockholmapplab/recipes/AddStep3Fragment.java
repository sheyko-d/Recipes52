package com.stockholmapplab.recipes;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import com.stockholmapplab.recipes.adapter.AddedIngredientsAdapter;
import com.stockholmapplab.recipes.typeface.CustomButton;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.R;

public class AddStep3Fragment extends DialogFragment {

	private View mRoot;
	private View mUSBtn;
	private View mEUBtn;
	public static  View headerTitleTxt;
	public static CheckBox calculateCheckbox;
	public static View totalKCalView;
	public static TextView totalKCalTxt;
	public static AddedIngredientsAdapter adapter;
	public static TextView emptyTxt;

	public static AddStep3Fragment newInstance() {
		AddStep3Fragment f = new AddStep3Fragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.fr_add_recipe_3, container, false);

		emptyTxt = (TextView) mRoot.findViewById(R.id.emptyTxt);
		totalKCalTxt = (TextView) mRoot.findViewById(R.id.addTotalKCalTxt);
		totalKCalView = mRoot.findViewById(R.id.addTotalKCalView);
		headerTitleTxt = mRoot.findViewById(R.id.recipesHeaderTitleTxt);

		adapter = new AddedIngredientsAdapter(getActivity());
		((ListView) mRoot.findViewById(R.id.addIngredientsList))
				.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		mUSBtn = mRoot.findViewById(R.id.addUSBtn);
		mEUBtn = mRoot.findViewById(R.id.addEUBtn);
		if (PrefHelper.getInt("AddMetricSystem", DBHelper.METRIC_SYSTEM_EU) == DBHelper.METRIC_SYSTEM_US) {
			setButtonBg(mUSBtn, R.drawable.add_btn_1_selected_patch);
			setButtonBg(mEUBtn, R.drawable.add_btn_3_selector);
		} else {
			setButtonBg(mEUBtn, R.drawable.add_btn_3_selected_patch);
			setButtonBg(mUSBtn, R.drawable.add_btn_1_selector);
		}
		mUSBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setButtonBg(mUSBtn, R.drawable.add_btn_1_selected_patch);
				setButtonBg(mEUBtn, R.drawable.add_btn_3_selector);
				PrefHelper.setInt("AddMetricSystem", DBHelper.METRIC_SYSTEM_US);
			}
		});
		mEUBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setButtonBg(mEUBtn, R.drawable.add_btn_3_selected_patch);
				setButtonBg(mUSBtn, R.drawable.add_btn_1_selector);
				PrefHelper.setInt("AddMetricSystem", DBHelper.METRIC_SYSTEM_EU);
			}
		});

		calculateCheckbox = (CheckBox) mRoot
				.findViewById(R.id.addCalculateCheckbox);

		if (PrefHelper.getBoolean("AddCalculateTotalKCal", true)) {
			totalKCalTxt.setText(PrefHelper.getInt("AddTotalKCal", 0) + "");
			calculateCheckbox.setChecked(true);
			totalKCalView.setVisibility(
					View.VISIBLE);
			totalKCalTxt.setCursorVisible(false);
		} else {
			calculateCheckbox.setChecked(false);
			totalKCalTxt.setText(PrefHelper.getInt("AddTotalKCalCustom", 0)
					+ "");
			totalKCalView.setVisibility(View.GONE);
			totalKCalTxt.setCursorVisible(true);
		}

		totalKCalTxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (PrefHelper.getBoolean("AddCalculateTotalKCal", true)) {
					try {
						PrefHelper.setInt("AddTotalKCalCustom",
								Integer.parseInt(s.toString()));
					} catch (Exception e) {
						PrefHelper.setInt("AddTotalKCalCustom", -1);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		calculateCheckbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						PrefHelper.setBoolean("AddCalculateTotalKCal",
								isChecked);
						if (isChecked) {
							totalKCalTxt.setText(PrefHelper.getInt(
									"AddTotalKCal", 0) + "");
							totalKCalTxt.setCursorVisible(false);
							totalKCalView
									.setVisibility(View.VISIBLE);
						} else {
							totalKCalTxt.setText(PrefHelper.getInt(
									"AddTotalKCalCustom", 0) + "");
							totalKCalTxt.setCursorVisible(true);
							totalKCalView
									.setVisibility(View.GONE);
						}
					}
				});

		return mRoot;
	}

	public static void setButtonBg(final View v, final int backgroundResId) {
		final int paddingBottom = v.getPaddingBottom(), paddingLeft = v
				.getPaddingLeft();
		final int paddingRight = v.getPaddingRight(), paddingTop = v
				.getPaddingTop();
		v.setBackgroundResource(backgroundResId);
		v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}
}