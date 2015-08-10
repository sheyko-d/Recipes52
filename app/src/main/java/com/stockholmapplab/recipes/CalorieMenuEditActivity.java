package com.stockholmapplab.recipes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
import com.stockholmapplab.recipes.adapter.ExpandableListAdapter;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.R;

public class CalorieMenuEditActivity extends ActionBarActivity {
	private EditText mTitleEdittxt;
	private int mId;
	private EditText mKcalEdittxt;
	private TextView mMeasurementTxt;
	private String mTitle = "";
	private int mMeasurementValue = -1;
	private String mMeasurementValueTxt;
	private int mKCal = 0;
	private int mMeasurementVolume;
	private List<Integer> groupList;
	private List<String> childList;
	private Map<Integer, List<String>> measurementCollection;
	private AlertDialog mMeasurementDialog;
	public static int checkedGroupPosition = -1;
	public static int checkedChildPosition = -1;

	private ArrayList<String> mVolumesArrayList = new ArrayList<String>();
	private ArrayList<String> mWeightsArrayList = new ArrayList<String>();
	private ArrayList<String> mLengthsArrayList = new ArrayList<String>();
	private Cursor mCursor;
	private TextView mPerUnitSubtitleTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);

		setContentView(R.layout.ac_calorie_menu_edit);

		initActionBar();

		mTitleEdittxt = (EditText) findViewById(R.id.editTitleEdittxt);
		mKcalEdittxt = (EditText) findViewById(R.id.editKcalEdittxt);
		mMeasurementTxt = (TextView) findViewById(R.id.editMeasurementTxt);

		mId = getIntent().getIntExtra("id", -1);
		Resources resources = BaseApplication.getAppContext().getResources();
		if (mId != -1) {
			SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
					.getWritableDatabase();
			Cursor c = DB.query("ingredients", null, "id='" + mId + "'", null,
					null, null, null);
			if (c.moveToFirst()) {
				if (BaseApplication.isSwedish()) {
					mTitle = c.getString(c.getColumnIndex("title_sw"));
				} else {
					mTitle = c.getString(c.getColumnIndex("title_en"));
				}
				setTitle(mTitle);
				mTitleEdittxt.setText(mTitle);

				mMeasurementValue = c.getInt(c
						.getColumnIndex("measurement_value"));
				Cursor measurementCursor = DB.query("measurements", null,
						"id='" + mMeasurementValue + "'", null, null, null,
						null);
				if (measurementCursor.moveToFirst()) {
					mMeasurementValueTxt = resources.getString(resources
							.getIdentifier(measurementCursor
									.getString(measurementCursor
											.getColumnIndex("title_id")),
									"string", BaseApplication.getAppContext()
											.getPackageName()));
				}
				mMeasurementTxt.setText(mMeasurementValueTxt);
				mKCal = c.getInt(c.getColumnIndex("kcal"));
				mMeasurementVolume = c.getInt(c
						.getColumnIndex("measurement_volume"));
				mKcalEdittxt.setText(mKCal + "");
				((TextView) findViewById(R.id.editPerUnitSubtitleTxt))
						.setText(Html.fromHtml(getResources().getString(
								R.string.str_edit_per_init_subtitle)
								+ "<font color='#AAAAAA'> "
								+ mKCal
								+ " kcal "
								+ getResources().getString(
										R.string.str_edit_per_init_txt)
								+ " "
								+ mMeasurementVolume
								+ " "
								+ mMeasurementValueTxt + "</font>"));

			}
			c.close();
			DB.close();
		} else {
			((Button) findViewById(R.id.updateBtn))
					.setText(R.string.str_edit_create_btn);
			mMeasurementTxt.setText(R.string.category_none);
			mKcalEdittxt.setText("0");
			setTitle(R.string.str_new_ingredient);
			BaseApplication.fadeOut(findViewById(R.id.updateBtn));
		}

		// Add text listener to title filed
		mTitleEdittxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mTitle = s.toString();
				if (!mTitle.equals("")) {
					setTitle(mTitle);
					SQLiteDatabase DB = new DBHelper(
							CalorieMenuEditActivity.this).getReadableDatabase();
					Cursor c = DB.query("ingredients", null, "title_en='"
							+ mTitle + "' OR title_sw='" + mTitle + "'", null,
							null, null, null);
					if (c.moveToFirst()) {
						BaseApplication.fadeOut(findViewById(R.id.updateBtn));
					} else {
						BaseApplication.fadeIn(findViewById(R.id.updateBtn));
					}
					c.close();
					DB.close();

				} else {
					BaseApplication.fadeOut(findViewById(R.id.updateBtn));
					setTitle(R.string.str_new_ingredient);
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

		mKcalEdittxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				try {
					mKCal = Integer.parseInt(s.toString());
					if (!mMeasurementValueTxt.equals("")) {
						mPerUnitSubtitleTxt.setText(Html
								.fromHtml(getResources().getString(
										R.string.str_edit_per_init_subtitle)
										+ "<font color='#AAAAAA'> "
										+ mKCal
										+ " kcal "
										+ getResources().getString(
												R.string.str_edit_per_init_txt)
										+ " "
										+ mMeasurementVolume
										+ " "
										+ mMeasurementValueTxt + "</font>"));
					}
				} catch (Exception e) {
					mKCal = 0;
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

		mPerUnitSubtitleTxt = (TextView) findViewById(R.id.editPerUnitSubtitleTxt);
		setTextNA(mPerUnitSubtitleTxt, R.string.str_edit_per_init_subtitle);

		createGroupList();
		createCollection();
	}

	private void setTextNA(TextView textView, int stringId) {
		textView.setText(Html.fromHtml(getResources().getString(stringId)
				+ " <font color='#AAAAAA'>"
				+ getResources().getString(R.string.str_edit_total_na)
				+ "</font>"));
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setElevation(0f);
	}

	private void setTitle(String title) {
		SpannableString s = new SpannableString(title);
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		getSupportActionBar().setTitle(s);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		default:
			finish();
			return super.onOptionsItemSelected(item);
		}
	}

	public void clearTitle(View v) {
		mTitleEdittxt.setText("");
	}

	public void info(View v) {
		final Dialog infoDialog = new Dialog(this, R.style.CustomDialogTheme);
		infoDialog.setContentView(R.layout.raw_dialog5);
		((TextView) infoDialog.findViewById(R.id.tv_txt_guide1))
				.setText(R.string.str_info_dialog_title);
		((TextView) infoDialog.findViewById(R.id.tv_txt_guide2))
				.setText(R.string.str_info_dialog_txt);
		Button btnOk = (Button) infoDialog.findViewById(R.id.bt_btn_ok);
		btnOk.setText(R.string.str_dialog_2);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				infoDialog.dismiss();
			}
		});
		infoDialog.show();
	}

	private void showUnitDialog(String measurementValueTxt,
			final String newTitleId, final int oldMeasurementUnit,
			final int newMeasurementUnit) {
		final Dialog calorieMenuDialog = new Dialog(this,
				R.style.CustomDialogTheme);
		calorieMenuDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				mKCal = (int) (Float.parseFloat(mKcalEdittxt.getText()
						.toString()));
				Log(mKCal + " * " + oldMeasurementUnit + " / "
						+ newMeasurementUnit);
				mKCal = mKCal * oldMeasurementUnit / newMeasurementUnit;
				if (mKCal == 0) {
					mKCal = 1;
				}

				mPerUnitSubtitleTxt.setText(Html.fromHtml(getResources()
						.getString(R.string.str_edit_per_init_subtitle)
						+ "<font color='#AAAAAA'> "
						+ mKCal
						+ " kcal "
						+ getResources().getString(
								R.string.str_edit_per_init_txt)
						+ " "
						+ mMeasurementVolume
						+ " "
						+ mMeasurementValueTxt
						+ "</font>"));
				mKcalEdittxt.setText(mKCal + "");
			}
		});
		calorieMenuDialog.setContentView(R.layout.raw_dialog_conversion);
		((TextView) calorieMenuDialog.findViewById(R.id.tv_txt_guide1))
				.setText(R.string.str_dialog_convertion_title);
		String text = String.format(
				getResources().getString(R.string.str_dialog_convertion_txt),
				measurementValueTxt + "",
				getResources().getString(
						getResources().getIdentifier(
								newTitleId,
								"string",
								BaseApplication.getAppContext()
										.getPackageName()))
						+ "");
		((TextView) calorieMenuDialog.findViewById(R.id.tv_txt_guide2))
				.setText(text);
		Button btnOk = (Button) calorieMenuDialog.findViewById(R.id.bt_btn_ok);
		btnOk.setText(R.string.str_dialog_convertion_convert);
		Button btnCancel = (Button) calorieMenuDialog
				.findViewById(R.id.bt_btn_Cancel);
		btnCancel.setText(R.string.str_dialog_convertion_current);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				calorieMenuDialog.dismiss();
				mKCal = (int) (Float.parseFloat(mKcalEdittxt.getText()
						.toString()));
				Log(mKCal + " * " + oldMeasurementUnit + " / "
						+ newMeasurementUnit);
				mKCal = mKCal * oldMeasurementUnit / newMeasurementUnit;
				if (mKCal == 0) {
					mKCal = 1;
				}

				mPerUnitSubtitleTxt.setText(Html.fromHtml(getResources()
						.getString(R.string.str_edit_per_init_subtitle)
						+ "<font color='#AAAAAA'> "
						+ mKCal
						+ " kcal "
						+ getResources().getString(
								R.string.str_edit_per_init_txt)
						+ " "
						+ mMeasurementVolume
						+ " "
						+ mMeasurementValueTxt
						+ "</font>"));
				mKcalEdittxt.setText(mKCal + "");
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				calorieMenuDialog.dismiss();
			}
		});
		calorieMenuDialog.show();
	}

	private void showUnitErrorDialog(String measurementValueTxt,
			final String newTitleId, final int oldMeasurementUnit,
			final int newMeasurementUnit) {
		final Dialog calorieMenuDialog = new Dialog(this,
				R.style.CustomDialogTheme);
		calorieMenuDialog.setCancelable(false);
		calorieMenuDialog.setContentView(R.layout.raw_dialog_conversion);
		((TextView) calorieMenuDialog.findViewById(R.id.tv_txt_guide1))
				.setText(R.string.str_dialog_convertion_title);
		String text = String.format(
				getResources().getString(
						R.string.str_dialog_convertion_error_txt),
				measurementValueTxt + "",
				getResources().getString(
						getResources().getIdentifier(
								newTitleId,
								"string",
								BaseApplication.getAppContext()
										.getPackageName()))
						+ "");
		((TextView) calorieMenuDialog.findViewById(R.id.tv_txt_guide2))
				.setText(text);
		Button btnOk = (Button) calorieMenuDialog.findViewById(R.id.bt_btn_ok);
		btnOk.setText(R.string.str_dialog_convertion_current);
		Button btnCancel = (Button) calorieMenuDialog
				.findViewById(R.id.bt_btn_Cancel);
		btnCancel.setText(R.string.str_dialog_convertion_zero);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				calorieMenuDialog.dismiss();

			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				calorieMenuDialog.dismiss();
				mKCal = 0;
				mKcalEdittxt.setText("0");
				mPerUnitSubtitleTxt.setText(Html.fromHtml(getResources()
						.getString(R.string.str_edit_per_init_subtitle)
						+ "<font color='#AAAAAA'> "
						+ mKCal
						+ " kcal "
						+ getResources().getString(
								R.string.str_edit_per_init_txt)
						+ " "
						+ mMeasurementVolume
						+ " "
						+ mMeasurementValueTxt
						+ "</font>"));
				mMeasurementTxt.setText(mMeasurementValueTxt);
			}
		});
		calorieMenuDialog.show();
	}

	public void chooseMeasurement(View v) {
		AlertDialog.Builder measurementDialogBuilder = new AlertDialog.Builder(
				this);

		final ExpandableListView expListView = new ExpandableListView(this);
		final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
				this, groupList, measurementCollection);
		expListView.setAdapter(expListAdapter);

		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Boolean sectionIsChanged;
				if (!mMeasurementTxt
						.getText()
						.toString()
						.equals(getResources()
								.getString(R.string.category_none))) {
					if (checkedGroupPosition == -1
							|| checkedChildPosition == -1) {

						if (mMeasurementValueTxt.equals(getResources()
								.getString(R.string.measurement_tsp))) {
							checkedGroupPosition = 0;
							checkedChildPosition = 0;
						} else if (mMeasurementValueTxt.equals(getResources()
								.getString(R.string.measurement_tbsp))) {
							checkedGroupPosition = 0;
							checkedChildPosition = 1;
						} else if (mMeasurementValueTxt.equals(getResources()
								.getString(R.string.measurement_fl_oz))) {
							checkedGroupPosition = 0;
							checkedChildPosition = 2;
						} else if (mMeasurementValueTxt.equals(getResources()
								.getString(R.string.measurement_gill))) {
							checkedGroupPosition = 0;
							checkedChildPosition = 3;
						} else if (mMeasurementValueTxt.equals(getResources()
								.getString(R.string.measurement_cup))) {
							checkedGroupPosition = 0;
							checkedChildPosition = 4;
						} else if (mMeasurementValueTxt.equals(getResources()
								.getString(R.string.measurement_pint))) {
							checkedGroupPosition = 0;
							checkedChildPosition = 5;
						} else if (mMeasurementValueTxt.equals(getResources()
								.getString(R.string.measurement_quart))) {
							checkedGroupPosition = 0;
							checkedChildPosition = 6;
						} else if (mMeasurementValueTxt.equals(getResources()
								.getString(R.string.measurement_gal))) {
							checkedGroupPosition = 0;
							checkedChildPosition = 7;
						} else if (mMeasurementValueTxt.equals(getResources()
								.getString(R.string.measurement_piece))) {
							checkedGroupPosition = 0;
							checkedChildPosition = 8;
						} else if (mMeasurementValueTxt.equals(getResources()
								.getString(R.string.measurement_pinch))) {
							checkedGroupPosition = 0;
							checkedChildPosition = 9;
						} else if (mMeasurementValueTxt.equals(getResources()
								.getString(R.string.measurement_lb))) {
							checkedGroupPosition = 1;
							checkedChildPosition = 0;
						} else if (mMeasurementValueTxt.equals(getResources()
								.getString(R.string.measurement_oz))) {
							checkedGroupPosition = 1;
							checkedChildPosition = 1;
						} else if (mMeasurementValueTxt.equals(getResources()
								.getString(R.string.measurement_in))) {
							checkedGroupPosition = 2;
							checkedChildPosition = 0;
						}

					}
					if (checkedGroupPosition != groupPosition) {
						checkedGroupPosition = groupPosition;
						sectionIsChanged = true;
					} else {
						sectionIsChanged = false;
					}
				} else {
					sectionIsChanged = false;
				}

				checkedChildPosition = childPosition;
				expListAdapter.notifyDataSetChanged();
				try {
					JSONObject mMeasurementValueJSON = new JSONObject(
							measurementCollection.get(
									groupList.get(groupPosition)).get(
									childPosition));

					String newTitleId = mMeasurementValueJSON
							.getString("title_id");
					mMeasurementValue = mMeasurementValueJSON
							.getInt("measurement_value");
					mMeasurementValueTxt = getResources().getString(
							getResources().getIdentifier(
									newTitleId,
									"string",
									BaseApplication.getAppContext()
											.getPackageName()));
					if (sectionIsChanged) {
						if (!mMeasurementTxt
								.getText()
								.toString()
								.equals(getResources().getString(
										R.string.category_none))) {
							showUnitErrorDialog(mMeasurementValueTxt,
									newTitleId, mMeasurementVolume,
									mMeasurementValueJSON
											.getInt("measurement_unit"));
						}
					} else if (mMeasurementVolume != mMeasurementValueJSON
							.getInt("measurement_unit")) {
						if (!mMeasurementTxt
								.getText()
								.toString()
								.equals(getResources().getString(
										R.string.category_none))) {
							showUnitDialog(mMeasurementValueTxt, newTitleId,
									mMeasurementVolume, mMeasurementValueJSON
											.getInt("measurement_unit"));
						}
						mMeasurementVolume = mMeasurementValueJSON
								.getInt("measurement_unit");
					}
					mMeasurementVolume = mMeasurementValueJSON
							.getInt("measurement_unit");
				} catch (JSONException e) {
					mMeasurementValueTxt = "";
					mMeasurementValue = 0;
				}
				mMeasurementTxt.setText(mMeasurementValueTxt);

				try {
					mKCal = Integer.parseInt(mKcalEdittxt.getText().toString());
				} catch (Exception e) {
					mKCal = 0;
				}
				if (mMeasurementVolume != -1) {
					((TextView) findViewById(R.id.editPerUnitSubtitleTxt))
							.setText(Html.fromHtml(getResources().getString(
									R.string.str_edit_per_init_subtitle)
									+ "<font color='#AAAAAA'> "
									+ mKCal
									+ " kcal "
									+ getResources().getString(
											R.string.str_edit_per_init_txt)
									+ " "
									+ mMeasurementVolume
									+ " "
									+ mMeasurementValueTxt + "</font>"));
				} else {
					setTextNA(mPerUnitSubtitleTxt,
							R.string.str_edit_per_init_subtitle);
				}
				if (mMeasurementTxt.getText().toString().length() > 6) {
					mMeasurementTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,
							getResources().getDimension(R.dimen.edit_txt_size));
				} else {
					mMeasurementTxt.setTextSize(
							TypedValue.COMPLEX_UNIT_PX,
							getResources().getDimension(
									R.dimen.edit_txt_size_large));
				}
				mMeasurementDialog.cancel();
				return false;
			}
		});

		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				if (groupPosition == 0) {
					expListView.collapseGroup(1);
					expListView.collapseGroup(2);
				} else if (groupPosition == 1) {
					expListView.collapseGroup(0);
					expListView.collapseGroup(2);
				} else {
					expListView.collapseGroup(0);
					expListView.collapseGroup(1);
				}
			}
		});

		measurementDialogBuilder.setView(expListView);

		mMeasurementDialog = measurementDialogBuilder.create();
		mMeasurementDialog.show();
	}

	private void createGroupList() {
		groupList = new ArrayList<Integer>();
		groupList.add(R.string.str_group_volume);
		groupList.add(R.string.str_group_weight);
		groupList.add(R.string.str_group_length);
	}

	private void createCollection() {
		// preparing measurements collection(child)
		SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
				.getReadableDatabase();
		if (PrefHelper.getInt("AddMetricSystem", DBHelper.METRIC_SYSTEM_EU) == DBHelper.METRIC_SYSTEM_EU) {
			mCursor = DB.query("measurements", null,
					"category=0 AND (metric_system="
							+ DBHelper.METRIC_SYSTEM_ALL + " OR metric_system="
							+ DBHelper.METRIC_SYSTEM_EU + ")", null, null,
					null, null);
		} else {
			mCursor = DB.query("measurements", null,
					"category=0 AND (metric_system="
							+ DBHelper.METRIC_SYSTEM_ALL + " OR metric_system="
							+ DBHelper.METRIC_SYSTEM_US + ")", null, null,
					null, null);
		}
		while (mCursor.moveToNext()) {
			try {
				JSONObject volumeItemJSON = new JSONObject();
				volumeItemJSON.put("title_id",
						mCursor.getInt(mCursor.getColumnIndex("title_id")));
				volumeItemJSON.put("measurement_unit", mCursor.getInt(mCursor
						.getColumnIndex("measurement_unit")));
				volumeItemJSON.put("measurement_value",
						mCursor.getInt(mCursor.getColumnIndex("id")));
				mVolumesArrayList.add(volumeItemJSON.toString());
			} catch (JSONException e) {
				mVolumesArrayList.add("n/a");
			}
		}
		if (PrefHelper.getInt("AddMetricSystem", DBHelper.METRIC_SYSTEM_EU) == DBHelper.METRIC_SYSTEM_EU) {
			mCursor = DB.query("measurements", null,
					"category=1 AND (metric_system="
							+ DBHelper.METRIC_SYSTEM_ALL + " OR metric_system="
							+ DBHelper.METRIC_SYSTEM_EU + ")", null, null,
					null, null);
		} else {
			mCursor = DB.query("measurements", null,
					"category=1 AND (metric_system="
							+ DBHelper.METRIC_SYSTEM_ALL + " OR metric_system="
							+ DBHelper.METRIC_SYSTEM_US + ")", null, null,
					null, null);
		}
		while (mCursor.moveToNext()) {
			try {
				JSONObject weightItemJSON = new JSONObject();
				weightItemJSON.put("title_id",
						mCursor.getInt(mCursor.getColumnIndex("title_id")));
				weightItemJSON.put("measurement_unit", mCursor.getInt(mCursor
						.getColumnIndex("measurement_unit")));
				weightItemJSON.put("measurement_value",
						mCursor.getInt(mCursor.getColumnIndex("id")));
				mWeightsArrayList.add(weightItemJSON.toString());
			} catch (JSONException e) {
				mWeightsArrayList.add("n/a");
			}
		}
		if (PrefHelper.getInt("AddMetricSystem", DBHelper.METRIC_SYSTEM_EU) == DBHelper.METRIC_SYSTEM_EU) {
			mCursor = DB.query("measurements", null,
					"category=2 AND (metric_system="
							+ DBHelper.METRIC_SYSTEM_ALL + " OR metric_system="
							+ DBHelper.METRIC_SYSTEM_EU + ")", null, null,
					null, null);
		} else {
			mCursor = DB.query("measurements", null,
					"category=2 AND (metric_system="
							+ DBHelper.METRIC_SYSTEM_ALL + " OR metric_system="
							+ DBHelper.METRIC_SYSTEM_US + ")", null, null,
					null, null);
		}
		while (mCursor.moveToNext()) {
			try {
				JSONObject lengthItemJSON = new JSONObject();
				lengthItemJSON.put("title_id",
						mCursor.getInt(mCursor.getColumnIndex("title_id")));
				lengthItemJSON.put("measurement_unit", mCursor.getInt(mCursor
						.getColumnIndex("measurement_unit")));
				lengthItemJSON.put("measurement_value",
						mCursor.getInt(mCursor.getColumnIndex("id")));
				mLengthsArrayList.add(lengthItemJSON.toString());
			} catch (JSONException e) {
				mLengthsArrayList.add("n/a");
			}
		}

		measurementCollection = new LinkedHashMap<Integer, List<String>>();

		for (Integer measurement : groupList) {
			if (measurement == R.string.str_group_volume)
				loadChild(mVolumesArrayList);
			else if (measurement == R.string.str_group_weight)
				loadChild(mWeightsArrayList);
			else
				loadChild(mLengthsArrayList);

			measurementCollection.put(measurement, childList);
		}
	}

	private void loadChild(ArrayList<String> measurementTypes) {
		childList = new ArrayList<String>();
		for (String type : measurementTypes)
			childList.add(type);
	}

	private void showEmptyDialog(int id) {
		final Dialog measurementDialog = new Dialog(this,
				R.style.CustomDialogTheme);
		measurementDialog.setContentView(R.layout.raw_dialog2);
		((TextView) measurementDialog
				.findViewById(R.id.tv_txtCalendarValidation)).setText(id);
		Button btnOk = (Button) measurementDialog.findViewById(R.id.bt_btn_ok);
		btnOk.setText(R.string.str_dialog_2);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				measurementDialog.cancel();
			}
		});
		measurementDialog.show();
	}

	public void update(View v) {
		if (mKcalEdittxt.getText().toString().equals("")) {
			mKCal = 0;
		}
		if (mTitle.equals("")) {
			showEmptyDialog(R.string.str_title_dialog_txt);
		} else if (mMeasurementValue == -1) {
			showEmptyDialog(R.string.str_measurement_type_dialog_txt);
		} else {
			SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
					.getWritableDatabase();
			if (mId != -1) {
				ContentValues ingredientCV = new ContentValues();
				if (BaseApplication.isSwedish()) {
					ingredientCV.put("title_sw", mTitle);
				} else {
					ingredientCV.put("title_en", mTitle);
				}
				ingredientCV.put("kcal", mKCal);
				ingredientCV.put("measurement_value", mMeasurementValue);
				ingredientCV.put("measurement_volume", mMeasurementVolume);

				DB.update("ingredients", ingredientCV, "id='" + mId + "'", null);
				finish();
				CalorieMenuFragment.showIngredients();
			} else {
				ContentValues ingredientCV = new ContentValues();
				ingredientCV.put("title_en", mTitle);
				ingredientCV.put("title_sw", mTitle);
				ingredientCV.put("kcal", mKCal);
				ingredientCV.put("category", 0);
				ingredientCV.put("measurement_value", mMeasurementValue);
				ingredientCV.put("measurement_volume", mMeasurementVolume);
				ingredientCV.put("is_favorite", 0);
				DB.insert("ingredients", null, ingredientCV);
				finish();
				CalorieMenuFragment.showIngredients();
			}
			DB.close();
		}
	}

	public void back(View v) {
		finish();
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

}