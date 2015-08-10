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
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
import com.stockholmapplab.recipes.adapter.ChooseIngredientAdapter;
import com.stockholmapplab.recipes.adapter.ExpandableListAdapter;
import com.stockholmapplab.recipes.typeface.TypefaceSpan;
import com.stockholmapplab.recipes.util.DBHelper;
import com.stockholmapplab.recipes.util.PrefHelper;
import com.stockholmapplab.recipes.R;

public class EditIngredientActivity extends ActionBarActivity {
	private int mId = -1;
	private int mKCal = 0;
	private int mMeasurementVolume = -1;
	private TextView mTotalSubtitleTxt;
	private EditText mTitleEdittxt;
	private EditText mQuantityEdittxt;
	private EditText mKcalEdittxt;
	public static EditIngredientActivity activity;

	public static String sortBy = "";
	public static final String SORT_TYPE_TITLE_EN = "title_en ASC";
	public static final String SORT_TYPE_TITLE_SW = "title_sw ASC";
	public static final String SORT_TYPE_KCAL = "kcal ASC";

	private List<Integer> groupList;
	private List<String> childList;
	private Map<Integer, List<String>> measurementCollection;
	private TextView mMeasurementTxt;
	private AlertDialog mMeasurementDialog;
	public static int checkedGroupPosition = -1;
	public static int checkedChildPosition = -1;

	private ArrayList<String> mVolumesArrayList = new ArrayList<String>();
	private ArrayList<String> mWeightsArrayList = new ArrayList<String>();
	private ArrayList<String> mLengthsArrayList = new ArrayList<String>();
	private CheckBox mIngredientCheckbox;
	private TextView mPerUnitSubtitleTxt;
	private boolean mTitleImageIsTransparent = false;
	private boolean mQuantityImageIsTransparent = true;
	private String mTitleEn = "";
	private String mTitleSv = "";
	private int mCategory;
	private int mMeasurementValue = -1;
	private int mTotalKCal = 0;
	private int mMeasurementQuantity;
	private String mMeasurementValueTxt = "";
	private Cursor mCursor;
	private String mTitleExtra = "";
	private boolean mAddToMenu = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BaseApplication.lockOrientation(this);
		setContentView(R.layout.ac_edit_ingredient);
		mId = getIntent().getIntExtra("Id", -1);
		mTitleExtra = getIntent().getStringExtra("Title");
		if (mTitleExtra == null) {
			mTitleExtra = "";
		}

		initActionBar();

		mTitleEdittxt = (EditText) findViewById(R.id.editTitleEdittxt);
		mQuantityEdittxt = (EditText) findViewById(R.id.editQuantityEdittxt);
		mKcalEdittxt = (EditText) findViewById(R.id.editKcalEdittxt);
		mMeasurementTxt = (TextView) findViewById(R.id.editMeasurementTxt);
		mIngredientCheckbox = (CheckBox) findViewById(R.id.editIngredientCheckbox);

		BaseApplication.fadeOut(mIngredientCheckbox);
		if (mMeasurementVolume == -1) {
			mMeasurementVolume = 1;
		}

		mIngredientCheckbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						mAddToMenu = isChecked;
						if (!isChecked) {
							showCalorieMenuDialog();
						}
					}
				});
		mTotalSubtitleTxt = (TextView) findViewById(R.id.editTotalSubtitleTxt);
		setTextNA(mTotalSubtitleTxt, R.string.str_edit_total_subtitle);
		if (mId != -1 || !mTitleExtra.equals("")) {
			SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
					.getWritableDatabase();
			Cursor c;
			Cursor c2 = null;
			if (mId != -1) {
				c = DB.query("ingredients", null, "id='" + mId + "'", null,
						null, null, null);
			} else {
				c = DB.query("add_ingredients", null, "title_en='"
						+ mTitleExtra + "' OR title_sw='" + mTitleExtra + "'",
						null, null, null, null);
				c2 = DB.query("ingredients", null, "title_en='" + mTitleExtra
						+ "' OR title_sw='" + mTitleExtra + "'", null, null,
						null, null);
			}
			if (c.moveToFirst()) {
				if (!mTitleExtra.equals("")) {
					mQuantityEdittxt.setText(c.getInt(c
							.getColumnIndex("measurement")) + "");

				}
				mTitleEn = c.getString(c.getColumnIndex("title_en"));
				mTitleSv = c.getString(c.getColumnIndex("title_sw"));
				// Set title text
				if (BaseApplication.isSwedish()) {
					setTitle(mTitleSv);
					mTitleEdittxt.setText(mTitleSv);
				} else {
					setTitle(mTitleEn);
					mTitleEdittxt.setText(mTitleEn);
				}

				mMeasurementValue = c.getInt(c
						.getColumnIndex("measurement_value"));
				Cursor measurementCursor = DB.query("measurements", null,
						"id='" + mMeasurementValue + "'", null, null, null,
						null);
				if (measurementCursor.moveToFirst()) {
					mMeasurementValueTxt = getResources()
							.getString(
									getResources()
											.getIdentifier(
													measurementCursor
															.getString(measurementCursor
																	.getColumnIndex("title_id")),
													"string",
													BaseApplication
															.getAppContext()
															.getPackageName()));
					mMeasurementVolume = measurementCursor
							.getInt(measurementCursor
									.getColumnIndex("measurement_unit"));
				}
				mMeasurementTxt.setText(mMeasurementValueTxt);
				if (!mTitleExtra.equals("")) {
					try {
						c2.moveToFirst();
						mKCal = c2.getInt(c2.getColumnIndex("kcal"));
					} catch (Exception e) {
						mKCal = c.getInt(c.getColumnIndex("kcal"))
								* c.getInt(c
										.getColumnIndex("measurement_volume"))
								/ c.getInt(c.getColumnIndex("measurement"));
					}
					try {
						mMeasurementQuantity = c.getInt(c
								.getColumnIndex("measurement"));
						mTotalKCal = c.getInt(c.getColumnIndex("measurement"))
								* mKCal / mMeasurementVolume;
						mTotalSubtitleTxt.setText(Html.fromHtml(getResources()
								.getString(R.string.str_edit_total_subtitle)
								+ "<font color='#AAAAAA'> "
								+ mTotalKCal
								+ " kcal "
								+ getResources().getString(
										R.string.str_edit_total_txt)
								+ " "
								+ c.getInt(c.getColumnIndex("measurement"))
								+ " " + mMeasurementValueTxt + "</font>"));
					} catch (Exception e) {
						setTextNA(mTotalSubtitleTxt,
								R.string.str_edit_total_subtitle);
					}
				} else {
					mKCal = c.getInt(c.getColumnIndex("kcal"));
				}
				mMeasurementVolume = c.getInt(c
						.getColumnIndex("measurement_volume"));
				mKcalEdittxt.setText(mKCal + "");
				if (!mMeasurementValueTxt.equals("")) {
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
				mCategory = c.getInt(c.getColumnIndex("category"));
			}
			c.close();
			DB.close();
		} else {
			mAddToMenu = false;
			mMeasurementTxt.setText(R.string.category_none);
			mKcalEdittxt.setText("0");
		}
		if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_tsp))) {
			checkedGroupPosition = 0;
			checkedChildPosition = 0;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_tbsp))) {
			checkedGroupPosition = 0;
			checkedChildPosition = 1;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_cup))) {
			checkedGroupPosition = 0;
			checkedChildPosition = 2;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_piece))) {
			checkedGroupPosition = 0;
			checkedChildPosition = 3;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_ml))) {
			checkedGroupPosition = 0;
			checkedChildPosition = 4;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_l))) {
			checkedGroupPosition = 0;
			checkedChildPosition = 5;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_dl))) {
			checkedGroupPosition = 0;
			checkedChildPosition = 6;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_pinch))) {
			checkedGroupPosition = 0;
			checkedChildPosition = 7;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_mcg))) {
			checkedGroupPosition = 1;
			checkedChildPosition = 0;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_mg))) {
			checkedGroupPosition = 1;
			checkedChildPosition = 1;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_g))) {
			checkedGroupPosition = 1;
			checkedChildPosition = 2;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_kg))) {
			checkedGroupPosition = 1;
			checkedChildPosition = 3;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_mm))) {
			checkedGroupPosition = 2;
			checkedChildPosition = 0;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_cm))) {
			checkedGroupPosition = 2;
			checkedChildPosition = 1;
		} else if (mMeasurementValueTxt.equals(getResources().getString(
				R.string.measurement_m))) {
			checkedGroupPosition = 2;
			checkedChildPosition = 2;
		}

		createGroupList();
		createCollection();

		mPerUnitSubtitleTxt = (TextView) findViewById(R.id.editPerUnitSubtitleTxt);
		setTextNA(mPerUnitSubtitleTxt, R.string.str_edit_per_init_subtitle);

		// Add text listener to title filed
		mTitleEdittxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!s.toString().equals("")) {
					// Check if this ingredient is already in DB
					SQLiteDatabase DB = new DBHelper(BaseApplication
							.getAppContext()).getReadableDatabase();
					Log.d("Log", "search = " + s.toString());
					Cursor cursor;
					if (BaseApplication.isSwedish()) {
						cursor = DB
								.query("ingredients", null, "title_sw=?",
										new String[] { s.toString() }, null,
										null, null);
					} else {
						cursor = DB
								.query("ingredients", null, "title_en=?",
										new String[] { s.toString() }, null,
										null, null);
					}
					if (cursor.moveToFirst()) {
						mTitleEn = cursor.getString(cursor
								.getColumnIndex("title_en"));
						mTitleSv = cursor.getString(cursor
								.getColumnIndex("title_sw"));
						BaseApplication.fadeOut(mIngredientCheckbox);
						mAddToMenu = false;
					} else {
						mTitleEn = mTitleEdittxt.getText().toString();
						mTitleSv = mTitleEn;
						BaseApplication.fadeIn(mIngredientCheckbox);
						mAddToMenu = mIngredientCheckbox.isChecked();
					}

					setTitle(mTitleEdittxt.getText().toString());

					cursor.close();
					DB.close();
					if (mTitleImageIsTransparent) {
						BaseApplication
								.fadeIn(findViewById(R.id.editTitleClearImg));
						mTitleImageIsTransparent = false;
					}
				} else {
					setTitle(R.string.str_new_ingredient);
					BaseApplication.fadeOut(mIngredientCheckbox);
					if (!mTitleImageIsTransparent) {
						BaseApplication
								.fadeOut(findViewById(R.id.editTitleClearImg));
						mTitleImageIsTransparent = true;
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

		mQuantityEdittxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!s.toString().equals("")) {
					try {
						mMeasurementQuantity = (int) (Float.parseFloat(s + ""));
						mTotalKCal = (int) (Float.parseFloat(s + "") * mKCal / mMeasurementVolume);
						if (!mMeasurementValueTxt.equals("")) {
							mTotalSubtitleTxt.setText(Html
									.fromHtml(getResources().getString(
											R.string.str_edit_total_subtitle)
											+ "<font color='#AAAAAA'> "
											+ mTotalKCal
											+ " kcal "
											+ getResources()
													.getString(
															R.string.str_edit_total_txt)
											+ " "
											+ s
											+ " "
											+ mMeasurementValueTxt + "</font>"));
						}
					} catch (Exception e) {
						setTextNA(mTotalSubtitleTxt,
								R.string.str_edit_total_subtitle);
					}
					if (mQuantityImageIsTransparent) {
						BaseApplication
								.fadeIn(findViewById(R.id.editQuantityClearImg));
						mQuantityImageIsTransparent = false;
					}
				} else {
					setTextNA(mTotalSubtitleTxt,
							R.string.str_edit_total_subtitle);
					if (!mQuantityImageIsTransparent) {
						BaseApplication
								.fadeOut(findViewById(R.id.editQuantityClearImg));
						mQuantityImageIsTransparent = true;
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

		mKcalEdittxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!s.toString().equals("")) {
					try {
						Log("mKcalEdittxt changed");
						mKCal = (int) Float.parseFloat(s.toString());
						if (!mMeasurementValueTxt.equals("")) {
							mPerUnitSubtitleTxt.setText(Html
									.fromHtml(getResources()
											.getString(
													R.string.str_edit_per_init_subtitle)
											+ "<font color='#AAAAAA'> "
											+ mKCal
											+ " kcal "
											+ getResources()
													.getString(
															R.string.str_edit_per_init_txt)
											+ " "
											+ mMeasurementVolume
											+ " "
											+ mMeasurementValueTxt + "</font>"));
						}
						if (!mQuantityEdittxt.getText().toString().equals("")) {
							mTotalKCal = (int) (Float
									.parseFloat(mQuantityEdittxt.getText()
											.toString())
									* mKCal / mMeasurementVolume);
							if (!mMeasurementValueTxt.equals("")) {
								mTotalSubtitleTxt.setText(Html
										.fromHtml(getResources()
												.getString(
														R.string.str_edit_total_subtitle)
												+ "<font color='#AAAAAA'> "
												+ (int) (Float
														.parseFloat(mQuantityEdittxt
																.getText()
																.toString())
														* mKCal / mMeasurementVolume)
												+ " kcal "
												+ getResources()
														.getString(
																R.string.str_edit_total_txt)
												+ " "
												+ mQuantityEdittxt.getText()
														.toString()
												+ " "
												+ mMeasurementValueTxt
												+ "</font>"));
							}
						} else {
							setTextNA(mTotalSubtitleTxt,
									R.string.str_edit_total_subtitle);
						}
					} catch (Exception e) {
						setTextNA(mPerUnitSubtitleTxt,
								R.string.str_edit_per_init_subtitle);
						setTextNA(mTotalSubtitleTxt,
								R.string.str_edit_total_subtitle);
						Log(e);
					}
				} else {
					setTextNA(mPerUnitSubtitleTxt,
							R.string.str_edit_per_init_subtitle);
					setTextNA(mTotalSubtitleTxt,
							R.string.str_edit_total_subtitle);
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

		// Fade out "Clear" icon on "Quantity" field
		BaseApplication.fadeOut(findViewById(R.id.editQuantityClearImg));
		if (mMeasurementValue != -1 & !mMeasurementValueTxt.equals("")) {
			mPerUnitSubtitleTxt.setText(Html.fromHtml(getResources().getString(
					R.string.str_edit_per_init_subtitle)
					+ "<font color='#AAAAAA'> "
					+ mKCal
					+ " kcal "
					+ getResources().getString(R.string.str_edit_per_init_txt)
					+ " "
					+ mMeasurementVolume
					+ " "
					+ mMeasurementValueTxt
					+ "</font>"));
		}

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

		if (mId == -1) {
			setTitle(getString(R.string.str_new_ingredient));
		}
	}

	private void setTitle(String title) {
		ActionBar actionBar = getSupportActionBar();
		SpannableString s = new SpannableString(title);
		s.setSpan(new TypefaceSpan(this, "ClementePDai-Regular.otf"), 0,
				s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_ingredient, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_new:
			newIngredient();
			return true;
		case R.id.action_clear:
			clear();
			return true;
		default:
			finish();
			return super.onOptionsItemSelected(item);
		}
	}

	public void clearTitle(View v) {
		mTitleEdittxt.setText("");
	}

	public void clearQuantity(View v) {
		mQuantityEdittxt.setText("");
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
				if (!mQuantityEdittxt.getText().toString().equals("")) {
					mKCal = (int) Float.parseFloat(mKcalEdittxt.getText()
							.toString());
					mTotalKCal = (int) (Float.parseFloat(mQuantityEdittxt
							.getText().toString()) * mKCal / mMeasurementVolume);
					mTotalSubtitleTxt.setText(Html.fromHtml(getResources()
							.getString(R.string.str_edit_total_subtitle)
							+ "<font color='#AAAAAA'> "
							+ (int) (Float.parseFloat(mQuantityEdittxt
									.getText().toString()) * mKCal / mMeasurementVolume)
							+ " kcal "
							+ getResources().getString(
									R.string.str_edit_total_txt)
							+ " "
							+ mQuantityEdittxt.getText().toString()
							+ " "
							+ mMeasurementValueTxt + "</font>"));
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
				if (!mQuantityEdittxt.getText().toString().equals("")) {
					mTotalKCal = (int) (Float.parseFloat(mQuantityEdittxt
							.getText().toString()) * mKCal / mMeasurementVolume);
					mTotalSubtitleTxt.setText(Html.fromHtml(getResources()
							.getString(R.string.str_edit_total_subtitle)
							+ "<font color='#AAAAAA'> "
							+ (int) (Float.parseFloat(mQuantityEdittxt
									.getText().toString()) * mKCal / mMeasurementVolume)
							+ " kcal "
							+ getResources().getString(
									R.string.str_edit_total_txt)
							+ " "
							+ mQuantityEdittxt.getText().toString()
							+ " "
							+ mMeasurementValueTxt + "</font>"));

					mMeasurementTxt.setText(mMeasurementValueTxt);
				}
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
				if (!mQuantityEdittxt.getText().toString().equals("")) {
					mTotalKCal = (int) (Float.parseFloat(mQuantityEdittxt
							.getText().toString()) * mKCal / mMeasurementVolume);
					mTotalSubtitleTxt.setText(Html.fromHtml(getResources()
							.getString(R.string.str_edit_total_subtitle)
							+ "<font color='#AAAAAA'> "
							+ (int) (Float.parseFloat(mQuantityEdittxt
									.getText().toString()) * mKCal / mMeasurementVolume)
							+ " kcal "
							+ getResources().getString(
									R.string.str_edit_total_txt)
							+ " "
							+ mQuantityEdittxt.getText().toString()
							+ " "
							+ mMeasurementValueTxt + "</font>"));

					mMeasurementTxt.setText(mMeasurementValueTxt);
				}
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
				if (!mQuantityEdittxt.getText().toString().equals("")) {
					mTotalKCal = (int) (Float.parseFloat(mQuantityEdittxt
							.getText().toString()) * mKCal / mMeasurementVolume);
					mTotalSubtitleTxt.setText(Html.fromHtml(getResources()
							.getString(R.string.str_edit_total_subtitle)
							+ "<font color='#AAAAAA'> "
							+ (int) (Float.parseFloat(mQuantityEdittxt
									.getText().toString()) * mKCal / mMeasurementVolume)
							+ " kcal "
							+ getResources().getString(
									R.string.str_edit_total_txt)
							+ " "
							+ mQuantityEdittxt.getText().toString()
							+ " "
							+ mMeasurementValueTxt + "</font>"));
				}

				mMeasurementTxt.setText(mMeasurementValueTxt);
			}
		});
		calorieMenuDialog.show();
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

	public void showCalorieMenuDialog() {
		final Dialog calorieMenuDialog = new Dialog(this,
				R.style.CustomDialogTheme);
		calorieMenuDialog.setContentView(R.layout.raw_dialog5);
		((TextView) calorieMenuDialog.findViewById(R.id.tv_txt_guide1))
				.setText(R.string.str_calorie_dialog_title);
		((TextView) calorieMenuDialog.findViewById(R.id.tv_txt_guide2))
				.setText(R.string.str_calorie_dialog_txt);
		Button btnOk = (Button) calorieMenuDialog.findViewById(R.id.bt_btn_ok);
		btnOk.setText(R.string.str_dialog_2);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				calorieMenuDialog.dismiss();
			}
		});
		calorieMenuDialog.show();
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

	public void clear() {
		final Dialog clearDialog = new Dialog(this, R.style.CustomDialogTheme);
		clearDialog.setContentView(R.layout.raw_dialog3);
		((TextView) clearDialog.findViewById(R.id.tv_txt_guide1))
				.setText(R.string.str_clear_dialog_title);
		((TextView) clearDialog.findViewById(R.id.tv_txt_guide2))
				.setText(R.string.str_clear_dialog_txt);
		Button btnOk = (Button) clearDialog.findViewById(R.id.bt_btn_ok);
		btnOk.setText(R.string.str_dialog_2);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTitleEdittxt.setText("");
				mKcalEdittxt.setText("");
				mQuantityEdittxt.setText("");
				mMeasurementTxt.setText("-");
				setTextNA(mTotalSubtitleTxt, R.string.str_edit_total_subtitle);
				setTextNA(mPerUnitSubtitleTxt,
						R.string.str_edit_per_init_subtitle);
				clearDialog.dismiss();
			}
		});
		Button btnClear = (Button) clearDialog.findViewById(R.id.bt_btn_Cancel);
		btnClear.setText(R.string.str_dialog_5);
		btnClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearDialog.dismiss();
			}
		});
		clearDialog.show();
	}

	public void newIngredient() {
		final Dialog newDialog = new Dialog(this, R.style.CustomDialogTheme);
		newDialog.setContentView(R.layout.raw_dialog3);
		((TextView) newDialog.findViewById(R.id.tv_txt_guide1))
				.setText(R.string.str_new_dialog_title);
		((TextView) newDialog.findViewById(R.id.tv_txt_guide2))
				.setText(R.string.str_new_dialog_txt);
		Button btnOk = (Button) newDialog.findViewById(R.id.bt_btn_ok);
		btnOk.setText(R.string.str_dialog_2);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ChooseIngredientAdapter.selectedItemId = -1;
				finish();
			}
		});
		Button btnClear = (Button) newDialog.findViewById(R.id.bt_btn_Cancel);
		btnClear.setText(R.string.str_dialog_5);
		btnClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				newDialog.dismiss();
			}
		});
		newDialog.show();
	}

	public void create(View v) {
		if (mKcalEdittxt.getText().toString().equals("")) {
			mKCal = 0;
		}
		String title = mTitleEdittxt.getText().toString();
		if (title.replaceAll(" ", "").equals("")) {
			showEmptyDialog(R.string.str_title_dialog_txt);
		} else {
			SQLiteDatabase DB = new DBHelper(BaseApplication.getAppContext())
					.getWritableDatabase();
			Cursor cursor = DB.query("add_ingredients", null, "title_en LIKE '"
					+ mTitleEn + "' OR title_sw='" + mTitleSv + "'", null,
					null, null, null);
			if (!cursor.moveToNext()) {
				if (mMeasurementValue == -1) {
					showEmptyDialog(R.string.str_measurement_type_dialog_txt);
				} else if (mQuantityEdittxt.getText().toString().equals("")
						|| mQuantityEdittxt.getText().toString().equals("0")) {
					showEmptyDialog(R.string.str_measurement_dialog_txt);
				} else {
					if (mAddToMenu) {
						// Add to calorie menu
						ContentValues contentValues = new ContentValues();
						contentValues.put("title_en", mTitleEn);
						contentValues.put("title_sw", mTitleSv);
						contentValues.put("kcal", mKCal);
						contentValues.put("category", mCategory);
						contentValues.put("measurement_value",
								mMeasurementValue);
						contentValues.put("measurement_volume",
								mMeasurementVolume);
						contentValues.put("is_favorite", 0);
						DB.insert("ingredients", null, contentValues);
					}

					// Add to DB
					ContentValues contentValues = new ContentValues();
					contentValues.put("id", mId);
					contentValues.put("title_en", mTitleEn);
					contentValues.put("title_sw", mTitleSv);
					contentValues.put("category", mCategory);
					contentValues.put("kcal", mTotalKCal);
					contentValues.put("measurement_value", mMeasurementValue);
					contentValues.put("measurement_volume", mMeasurementVolume);
					contentValues.put("measurement", mMeasurementQuantity);
					DB.insert("add_ingredients", null, contentValues);
					finish();
					ChooseIngredientActivity.activity.finish();
					AddStep3Fragment.adapter.notifyDataSetChanged();

				}
			} else {
				showExistDialog();
			}
			DB.close();
		}
	}

	private void showExistDialog() {
		final Dialog calorieMenuDialog = new Dialog(this,
				R.style.CustomDialogTheme);
		calorieMenuDialog.setCancelable(false);
		calorieMenuDialog.setContentView(R.layout.raw_dialog3);
		((TextView) calorieMenuDialog.findViewById(R.id.tv_txt_guide1))
				.setText(R.string.str_dialog_exist_title);
		((TextView) calorieMenuDialog.findViewById(R.id.tv_txt_guide2))
				.setText(R.string.str_dialog_exist_txt);
		Button btnOk = (Button) calorieMenuDialog.findViewById(R.id.bt_btn_ok);
		btnOk.setText(R.string.str_dialog_2);
		Button btnCancel = (Button) calorieMenuDialog
				.findViewById(R.id.bt_btn_Cancel);
		btnCancel.setText(R.string.str_dialog_5);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				calorieMenuDialog.dismiss();
				Intent editIntent = new Intent(EditIngredientActivity.this,
						EditIngredientActivity.class);
				if (BaseApplication.isSwedish()) {
					editIntent.putExtra("Title", mTitleSv);
				} else {
					editIntent.putExtra("Title", mTitleEn);
				}
				startActivity(editIntent);
				finish();
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

	private void loadChild(ArrayList<String> measurementTypes) {
		childList = new ArrayList<String>();
		for (String type : measurementTypes)
			childList.add(type);
	}

	public void back(View v) {
		finish();
	}

	public static void Log(Object text) {
		Log.d("Log", text + "");
	}

}
