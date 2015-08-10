package com.stockholmapplab.recipes.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.stockholmapplab.recipes.EditIngredientActivity;
import com.stockholmapplab.recipes.R;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Activity context;
	private Map<Integer, List<String>> laptopCollections;
	private List<Integer> list;

	public ExpandableListAdapter(Activity context, List<Integer> list,
			Map<Integer, List<String>> measurementCollection) {
		this.context = context;
		this.laptopCollections = measurementCollection;
		this.list = list;
	}

	public Object getChild(int groupPosition, int childPosition) {
		try {
			JSONObject childJSON = new JSONObject(laptopCollections.get(
					list.get(groupPosition)).get(childPosition));

			return context.getResources().getString(
					childJSON.getInt("title_id"));
		} catch (Exception e) {
			return "";
		}
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final String laptop = (String) getChild(groupPosition, childPosition);
		LayoutInflater inflater = context.getLayoutInflater();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.exp_child_item, null);
		}

		CheckedTextView childTitleTxt = (CheckedTextView) convertView
				.findViewById(R.id.measurmentChildTitleTxt);

		if (EditIngredientActivity.checkedChildPosition == childPosition
				& EditIngredientActivity.checkedGroupPosition == groupPosition) {
			childTitleTxt.setChecked(true);
		} else {
			childTitleTxt.setChecked(false);
		}

		/*
		 * delete.setOnClickListener(new OnClickListener() {
		 * 
		 * public void onClick(View v) { AlertDialog.Builder builder = new
		 * AlertDialog.Builder(context);
		 * builder.setMessage("Do you want to remove?");
		 * builder.setCancelable(false); builder.setPositiveButton("Yes", new
		 * DialogInterface.OnClickListener() { public void
		 * onClick(DialogInterface dialog, int id) { List<String> child =
		 * laptopCollections.get(list.get(groupPosition));
		 * child.remove(childPosition); notifyDataSetChanged(); } });
		 * builder.setNegativeButton("No", new DialogInterface.OnClickListener()
		 * { public void onClick(DialogInterface dialog, int id) {
		 * dialog.cancel(); } }); AlertDialog alertDialog = builder.create();
		 * alertDialog.show(); } });
		 */

		childTitleTxt.setText(laptop);
		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		return laptopCollections.get(list.get(groupPosition)).size();
	}

	public Object getGroup(int groupPosition) {
		return context.getResources().getString(list.get(groupPosition));
	}

	public int getGroupCount() {
		return list.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String laptopName = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.exp_group_item, null);
		}
		TextView item = (TextView) convertView.findViewById(R.id.laptop);
		item.setTypeface(null, Typeface.BOLD);
		item.setText(laptopName);
		return convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}