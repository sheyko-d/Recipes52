package com.stockholmapplab.recipes.adapter;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;

import com.stockholmapplab.recipes.BodyStatsActivity;
import com.stockholmapplab.recipes.R;
import com.stockholmapplab.recipes.genericview.GenericView;
import com.stockholmapplab.recipes.pojo.DietCycle;
import com.stockholmapplab.recipes.typeface.CustomTextView;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.Utility;
/**
 * DietHistoryAdapter class is use to show Diet Cycle History information.  
 */
public class DietHistoryAdapter extends BaseExpandableListAdapter {
	private Activity activity;
	private ArrayList<Object> childtems;
	private LayoutInflater inflater;
	private ArrayList<String> child;
	private ArrayList<DietCycle> parentItems;
	public static int one_raw_height = 0;

	public DietHistoryAdapter(ArrayList<DietCycle> parents,
			ArrayList<Object> childern) {
		this.parentItems = parents;
		this.childtems = childern;
	}

	public void setInflater(LayoutInflater inflater, Activity activity) {
		this.inflater = inflater;
		this.activity = activity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		child = (ArrayList<String>) childtems.get(groupPosition);
		ChildViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.raw_cal_used, null);
			holder = new ChildViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ChildViewHolder) convertView.getTag();
		}

		String values[] = child.get(childPosition).split("@");
		holder.mTxtDate.setText(values[0]);
		holder.mTxtCal.setText("used kcal: " + values[1]);
		return convertView;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ParentViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.raw_diet_history, null);
			holder = new ParentViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ParentViewHolder) convertView.getTag();
		}

		Calendar startDate = Calendar.getInstance();
		startDate
				.setTimeInMillis(parentItems.get(groupPosition).getStartTime());

		if (!parentItems.get(groupPosition).isDiet_cycle_is_on()) {
			Calendar endDate = Calendar.getInstance();
			endDate.setTimeInMillis(parentItems.get(groupPosition).getEndTime());
			holder.mTxtDietDate.setText(Utility.getTimeInddMMyy(startDate)
					+ " - " + Utility.getTimeInddMMyy(endDate));
			holder.mImgDietCycle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(activity,
							BodyStatsActivity.class);
					intent.putExtra(BodyStatsActivity.extraStartTime,
							parentItems.get(groupPosition).getStartTime());
					intent.putExtra(BodyStatsActivity.extraEndTime, parentItems
							.get(groupPosition).getEndTime());
					intent.putExtra(BodyStatsActivity.extraGraph, parentItems
							.get(groupPosition).getmGraphs());
					intent.putExtra(BodyStatsActivity.extraUsedCal, parentItems
							.get(groupPosition).getUsedCal().toString());
					intent.putExtra(BodyStatsActivity.extraFromHistory, true);
					activity.startActivity(intent);

				}
			});
		} else {
			holder.mTxtDietDate.setText(Utility.getTimeInddMMyy(startDate)
					+ " - now");
			holder.mImgDietCycle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Calendar endTimeCalendar = Calendar.getInstance();
					endTimeCalendar.set(Calendar.HOUR_OF_DAY, 23);
					endTimeCalendar.set(Calendar.MINUTE, 59);
					endTimeCalendar.set(Calendar.SECOND, 59);
					long endTime = endTimeCalendar.getTimeInMillis();

					Intent intent = new Intent(activity,
							BodyStatsActivity.class);
					intent.putExtra(BodyStatsActivity.extraStartTime,
							BaseConstant.mDietCycle.getStartTime());
					intent.putExtra(BodyStatsActivity.extraEndTime, endTime);
					intent.putExtra(BodyStatsActivity.extraGraph,
							BaseConstant.mDietCycle.getmGraphs());
					intent.putExtra(BodyStatsActivity.extraUsedCal,
							BaseConstant.mDietCycle.getUsedCal().toString());
					intent.putExtra(BodyStatsActivity.extraFromHistory, false);
					activity.startActivity(intent);
				}
			});
		}
		holder.mTxtDietIntensity.setText(getIntensity((parentItems
				.get(groupPosition).getFasting_intensity())));
		return convertView;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getChildrenCount(int groupPosition) {
		return ((ArrayList<String>) childtems.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return parentItems.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return parentItems.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	private class ParentViewHolder {
		CustomTextView mTxtDietDate, mTxtDietIntensity;
		ImageView mImgDietCycle;

		public ParentViewHolder(final View convertView) {
			mTxtDietDate = GenericView.findViewById(convertView,
					R.id.tv_txtDietDate);
			mTxtDietIntensity = GenericView.findViewById(convertView,
					R.id.tv_txtfastingIntensity);
			mImgDietCycle = GenericView.findViewById(convertView,
					R.id.iv_imgDietCycle);

			ViewTreeObserver vto = convertView.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					one_raw_height = convertView.getMeasuredHeight();
					convertView.getViewTreeObserver()
							.removeGlobalOnLayoutListener(this);
				}
			});
		}
	}

	private class ChildViewHolder {
		CustomTextView mTxtDate, mTxtCal;

		public ChildViewHolder(View convertView) {
			mTxtDate = GenericView.findViewById(convertView, R.id.tv_txtDate);
			mTxtCal = GenericView.findViewById(convertView, R.id.tv_txtUsedCal);
		}
	}

	private String getIntensity(int intensity) {
		String str = "";
		switch (intensity) {
		case 2:
			str = "fasting intensity: 5:2";
			break;
		case 3:
			str = "fasting intensity: 4:3";
			break;
		case 1:
			str = "fasting intensity: 6:1";
			break;
		}
		return str;
	}
}