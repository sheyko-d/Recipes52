package com.stockholmapplab.recipes.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.stockholmapplab.recipes.BaseApplication;
import com.stockholmapplab.recipes.R;
import com.stockholmapplab.recipes.util.BaseConstant;
import com.stockholmapplab.recipes.util.MapSort;
import com.stockholmapplab.recipes.util.PrefHelper;

/**
 * DietCycle class is use for maintain data of diet cycle. This class get data
 * from preference in JSON format and save data in preference from diet object
 */
public class DietCycle {

	private static final String KEY_DIET_CYCLE_HISTORY = "key_diet_cycle_hstory";
	private static final String KEY_DIET_CYCLE = "key_diet_cycle";
	private static final String KEY_DIET_DURATION = "key_diet_duration";
	private static final String KEY_FASTING_INTENSITY = "key_fasting_intensity";
	private static final String KEY_SELECTED_FASTING_DAYS = "key_selected_fasting_days";
	private static final String KEY_CALORIES_CONSUMPTION = "key_calories_consumption";
	private static final String KEY_FOOD_ITEMS_DURING_FASTING_DAYS = "key_food_items_during_fasting_days";
	private static final String KEY_FOOD_ITEM_NAME = "key_food_item_name";
	private static final String KEY_FOOD_ITEM_VALUE = "key_food_item_value";
	private static final String KEY_FOOD_ITEM_DATE = "key_food_item_date";
	private static final String KEY_DIET_CYCLE_IS_ON = "key_diet_cycle_is_on";
	private static final String KEY_FASTINGDAYS = "fastingDays";
	private static final String KEY_FASTINGDAYSMISSED = "fastingDaysMissed";
	private static final String KEY_IS_NOW = "key_is_now";
	private static final String KEY_START_DATE = "start_date";
	private static final String KEY_END_DATE = "end_date";
	private static final String KEY_USED_CAL_HISTORY = "used_cal_history";
	private static final String KEY_USED_CAL_DATE = "used_cal_date";
	private static final String KEY_USED_CAL = "used_cal";
	private static final String KEY_CONSUME_FOOD_ITEM = "consume_food_item";
	private static final String KEY_LAUNCH_HISTORY = "launch_history";
	private static final String KEY_LAST_LAUNCH_DATE = "last_launch_date";
	private static final String KEY_ONE_HOUR_NOTIFICATION = "one_hour_notification";
	private static final String KEY_GRAPH = "graph";
	private static final String KEY_GOAL = "goal";
	private static final String KEY_VALUE = "value";
	private static final String KEY_CONSUME_FOOD_POINTS = "consume_food_points";

	private ArrayList<Long> launch_history = new ArrayList<Long>();
	private long last_launch;
	private int diet_duration;
	private int fasting_intensity;
	private int calories_consumption;
	private ArrayList<Integer> selected_fasting_days = new ArrayList<Integer>();
	private ArrayList<FoodItem> food_items_during_fasting_days = new ArrayList<FoodItem>();
	private ArrayList<FoodItem> consume_food_item = new ArrayList<FoodItem>();
	private int mConsumeFoodPoints;
	private boolean diet_cycle_is_on;
	private long startTime;
	private long endTime;
	private int fastingDays;
	private int fastingDaysMissed;
	private boolean isNow;
	private long one_notification_time;
	private Map<String, Integer> usedCal = new HashMap<String, Integer>();
	private ArrayList<Graph> mGraphs = new ArrayList<Graph>();

	public ArrayList<Graph> getmGraphs() {
		return mGraphs;
	}

	public void setmGraphs(Graph mGraphs) {
		this.mGraphs.add(mGraphs);
	}

	public void setmGraphs(ArrayList<Graph> mGraphs) {
		this.mGraphs = mGraphs;
	}

	public boolean isNow() {
		return isNow;
	}

	public void setNow(boolean isNow) {
		this.isNow = isNow;
	}

	public ArrayList<Long> getLaunch_history() {
		return launch_history;
	}

	public void setLaunch_history(Long launch_history) {
		this.launch_history.add(launch_history);
	}

	public long getLast_launch() {
		return last_launch;
	}

	public void setLast_launch(long last_launch) {
		this.last_launch = last_launch;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getDiet_duration() {
		return diet_duration;
	}

	public void setDiet_duration(int diet_duration) {
		this.diet_duration = diet_duration;
	}

	public int getFasting_intensity() {
		return fasting_intensity;
	}

	public void setFasting_intensity(int fasting_intensity) {
		this.fasting_intensity = fasting_intensity;
	}

	public int getCalories_consumption() {
		return calories_consumption;
	}

	public void setCalories_consumption(int calories_consumption) {
		this.calories_consumption = calories_consumption;
	}

	public ArrayList<Integer> getSelected_fasting_days() {
		return selected_fasting_days;
	}

	public void setSelected_fasting_days(int selected_fasting_day) {
		this.selected_fasting_days.add(selected_fasting_day);
	}

	public void removeSelected_fasting_days(int index) {
		this.selected_fasting_days.remove(index);
	}

	public ArrayList<FoodItem> getFood_items_during_fasting_days() {
		return food_items_during_fasting_days;
	}

	public void setFood_items_during_fasting_days(
			FoodItem food_items_during_fasting_day) {
		this.food_items_during_fasting_days.add(food_items_during_fasting_day);
	}

	public boolean isDiet_cycle_is_on() {
		return diet_cycle_is_on;
	}

	public void setDiet_cycle_is_on(boolean diet_cycle_is_on) {
		this.diet_cycle_is_on = diet_cycle_is_on;
	}

	public int getFastingDays() {
		return fastingDays;
	}

	public void setFastingDays(int fastingDays) {
		this.fastingDays = fastingDays;
	}

	public int getFastingDaysMissed() {
		return fastingDaysMissed;
	}

	public void setFastingDaysMissed(int fastingDaysMissed) {
		this.fastingDaysMissed = fastingDaysMissed;
	}

	public Map<String, Integer> getUsedCal() {
		return usedCal;
	}

	public void setUsedCal(String date, int cal) {
		this.usedCal.put(date, cal);
	}

	public ArrayList<FoodItem> getConsume_food_item() {
		return consume_food_item;
	}

	public void setConsume_food_item(FoodItem consume_food_item) {
		this.consume_food_item.add(consume_food_item);
	}

	public static DietCycle getDietCycle() {
		String json = PrefHelper.getString(KEY_DIET_CYCLE, "");
		return setDietObjectFromJson(json, null);
	}

	public static void setDietCycle() {
		PrefHelper.setString(KEY_DIET_CYCLE,
				setJsonFromDietObject(BaseConstant.mDietCycle).toString());
	}

	/**
	 * This method return diet history
	 * 
	 * @return ArrayList of DietCycle
	 */
	public static ArrayList<DietCycle> getDietCycleHistory() {
		ArrayList<DietCycle> dietCycleHistory = new ArrayList<DietCycle>();
		String jsonString = PrefHelper.getString(KEY_DIET_CYCLE_HISTORY, "");
		if (!jsonString.equalsIgnoreCase("")) {
			try {
				JSONArray dietCycleHistoryJsonArray = new JSONArray(jsonString);
				for (int j = 0; j < dietCycleHistoryJsonArray.length(); j++) {
					dietCycleHistory.add(setDietObjectFromJson(null,
							dietCycleHistoryJsonArray.getJSONObject(j)));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return dietCycleHistory;
	}

	/**
	 * This method is use for save diet history in preference
	 */
	public static void setDietCycleHistory() {
		String dietCycleHistoryJson = PrefHelper.getString(
				KEY_DIET_CYCLE_HISTORY, "");
		JSONArray dietCycleHistoryJsonArray;
		try {
			if (!dietCycleHistoryJson.equalsIgnoreCase("")) {
				dietCycleHistoryJsonArray = new JSONArray(dietCycleHistoryJson);
			} else {
				dietCycleHistoryJsonArray = new JSONArray();
			}
			dietCycleHistoryJsonArray
					.put(setJsonFromDietObject(BaseConstant.mDietCycle));
			PrefHelper.setString(KEY_DIET_CYCLE_HISTORY,
					dietCycleHistoryJsonArray.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method save DietCycle object into preference in json format
	 * 
	 * @param dietObject
	 * @return
	 */
	private static JSONObject setJsonFromDietObject(DietCycle dietObject) {
		JSONObject json = new JSONObject();
		try {

			json.put(KEY_DIET_DURATION, dietObject.getDiet_duration());
			json.put(KEY_FASTING_INTENSITY, dietObject.getFasting_intensity());
			json.put(KEY_CALORIES_CONSUMPTION,
					dietObject.getCalories_consumption());
			json.put(KEY_FASTINGDAYS, dietObject.getFastingDays());
			json.put(KEY_FASTINGDAYSMISSED, dietObject.getFastingDaysMissed());
			json.put(KEY_LAST_LAUNCH_DATE, dietObject.getLast_launch());
			json.put(KEY_START_DATE, dietObject.getStartTime());
			json.put(KEY_IS_NOW, dietObject.isNow());
			json.put(KEY_CONSUME_FOOD_POINTS,
					dietObject.getmConsumeFoodPoints());
			json.put(KEY_ONE_HOUR_NOTIFICATION,
					dietObject.getOne_notification_time());
			JSONArray selected_fasting_days = new JSONArray();
			for (int i = 0; i < dietObject.getSelected_fasting_days().size(); i++) {
				selected_fasting_days.put(dietObject.getSelected_fasting_days()
						.get(i));
			}
			json.put(KEY_SELECTED_FASTING_DAYS, selected_fasting_days);
			json.put(KEY_END_DATE, dietObject.getEndTime());
			JSONArray foodItemJsonArray = new JSONArray();
			for (int i = 0; i < dietObject.getFood_items_during_fasting_days()
					.size(); i++) {
				JSONObject foodItemJson = new JSONObject();
				foodItemJson.put(KEY_FOOD_ITEM_NAME, dietObject
						.getFood_items_during_fasting_days().get(i).getName());
				foodItemJson.put(KEY_FOOD_ITEM_VALUE, dietObject
						.getFood_items_during_fasting_days().get(i).getCal());
				foodItemJsonArray.put(foodItemJson);
			}
			json.put(KEY_FOOD_ITEMS_DURING_FASTING_DAYS, foodItemJsonArray);
			json.put(KEY_DIET_CYCLE_IS_ON, dietObject.isDiet_cycle_is_on());
			JSONArray usedCalJsonArray = new JSONArray();
			for (Map.Entry<String, Integer> entry : dietObject.getUsedCal()
					.entrySet()) {
				JSONObject usedCalJson = new JSONObject();
				usedCalJson.put(KEY_USED_CAL_DATE, entry.getKey());
				usedCalJson.put(KEY_USED_CAL, entry.getValue());
				usedCalJsonArray.put(usedCalJson);
			}
			json.put(KEY_USED_CAL_HISTORY, usedCalJsonArray);
			JSONArray consumeFoodItemJsonArray = new JSONArray();
			for (int i = 0; i < dietObject.getConsume_food_item().size(); i++) {
				JSONObject foodItemJson = new JSONObject();
				foodItemJson.put(KEY_FOOD_ITEM_NAME, dietObject
						.getConsume_food_item().get(i).getName());
				foodItemJson.put(KEY_FOOD_ITEM_VALUE, dietObject
						.getConsume_food_item().get(i).getCal());
				foodItemJson.put(KEY_FOOD_ITEM_DATE, dietObject
						.getConsume_food_item().get(i).getDate());
				consumeFoodItemJsonArray.put(foodItemJson);
			}
			json.put(KEY_CONSUME_FOOD_ITEM, consumeFoodItemJsonArray);

			JSONArray historyJsonArray = new JSONArray();
			for (int i = 0; i < dietObject.getLaunch_history().size(); i++) {
				historyJsonArray.put(dietObject.getLaunch_history().get(i));
			}
			PrefHelper.setString(KEY_LAUNCH_HISTORY,
					historyJsonArray.toString());
			JSONArray mainJsonArray = new JSONArray();
			for (int i = 0; i < dietObject.getmGraphs().size(); i++) {
				JSONObject kCalJson = new JSONObject();
				kCalJson.put(KEY_GOAL, dietObject.getmGraphs().get(i).getGoal());
				kCalJson.put(KEY_VALUE, dietObject.getmGraphs().get(i)
						.getValues().toString());
				mainJsonArray.put(kCalJson);
			}
			json.put(KEY_GRAPH, mainJsonArray);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * This method get current diet cycle json from preference and set into
	 * DietCycle object
	 * 
	 * @param jsonStr
	 * @param jsonObj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static DietCycle setDietObjectFromJson(String jsonStr,
			JSONObject jsonObj) {
		DietCycle dietCycle = new DietCycle();
		try {
			JSONObject jsonObject = null;
			if (jsonStr != null) {
				if (jsonStr.equalsIgnoreCase(""))
					jsonObject = null;
				else
					jsonObject = new JSONObject(jsonStr);
			} else {
				jsonObject = jsonObj;
			}

			if (jsonObject == null) {
				dietCycle.setDiet_duration(0);
				dietCycle.setFasting_intensity(2);
				dietCycle.setCalories_consumption(0);
				dietCycle.setDiet_cycle_is_on(false);
				dietCycle.setFastingDays(0);
				dietCycle.setFastingDaysMissed(0);
				dietCycle.setLast_launch(-1);
				dietCycle.setEndTime(0);
				dietCycle.setNow(false);
				dietCycle.setmConsumeFoodPoints(0);
				dietCycle.setOne_notification_time(0);
				dietCycle.setmGraphs(new Graph(0));
				dietCycle.setmGraphs(new Graph(0));
				dietCycle.setmGraphs(new Graph(0));
				dietCycle.setmGraphs(new Graph(0));
				dietCycle.setmGraphs(new Graph(0));
				dietCycle.setmGraphs(new Graph(0));
				dietCycle.setmGraphs(new Graph(0));

				String[] food_names = BaseApplication.getAppContext()
						.getResources().getStringArray(R.array.food_item_name);
				int[] food_cal = BaseApplication.getAppContext().getResources()
						.getIntArray(R.array.food_cal);

				Map<String, Integer> sortedMap = MapSort.sortByKey(food_names,
						food_cal);
				Set<String> keySet = sortedMap.keySet();
				for (String key : keySet) {
					dietCycle.setFood_items_during_fasting_days(new FoodItem(
							key, sortedMap.get(key)));
				}

			} else {
				dietCycle
						.setDiet_duration(jsonObject.getInt(KEY_DIET_DURATION));
				dietCycle.setFasting_intensity(jsonObject
						.getInt(KEY_FASTING_INTENSITY));
				dietCycle.setCalories_consumption(jsonObject
						.getInt(KEY_CALORIES_CONSUMPTION));
				dietCycle.setDiet_cycle_is_on(jsonObject
						.getBoolean(KEY_DIET_CYCLE_IS_ON));
				dietCycle.setFastingDays(jsonObject.getInt(KEY_FASTINGDAYS));
				dietCycle.setFastingDaysMissed(jsonObject
						.getInt(KEY_FASTINGDAYSMISSED));
				dietCycle.setStartTime(jsonObject.getLong(KEY_START_DATE));
				dietCycle.setNow(jsonObject.getBoolean(KEY_IS_NOW));
				dietCycle.setEndTime(jsonObject.getLong(KEY_END_DATE));
				dietCycle.setmConsumeFoodPoints(jsonObject
						.getInt(KEY_CONSUME_FOOD_POINTS));
				dietCycle.setOne_notification_time(jsonObject
						.getLong(KEY_ONE_HOUR_NOTIFICATION));
				JSONArray selected_fasting_days = jsonObject
						.getJSONArray(KEY_SELECTED_FASTING_DAYS);
				for (int i = 0; i < selected_fasting_days.length(); i++) {
					dietCycle.setSelected_fasting_days(selected_fasting_days
							.getInt(i));
				}

				JSONArray foodItemJsonArray = jsonObject
						.getJSONArray(KEY_FOOD_ITEMS_DURING_FASTING_DAYS);
				for (int i = 0; i < foodItemJsonArray.length(); i++) {
					String itemName = foodItemJsonArray.getJSONObject(i)
							.getString(KEY_FOOD_ITEM_NAME);
					int itemCal = foodItemJsonArray.getJSONObject(i).getInt(
							KEY_FOOD_ITEM_VALUE);
					dietCycle.setFood_items_during_fasting_days(new FoodItem(
							itemName, itemCal));
				}
				dietCycle.setLast_launch(jsonObject
						.getLong(KEY_LAST_LAUNCH_DATE));

				JSONArray usedCalJsonArray = jsonObject
						.getJSONArray(KEY_USED_CAL_HISTORY);
				for (int i = 0; i < usedCalJsonArray.length(); i++) {
					dietCycle.setUsedCal(usedCalJsonArray.getJSONObject(i)
							.getString(KEY_USED_CAL_DATE), usedCalJsonArray
							.getJSONObject(i).getInt(KEY_USED_CAL));
				}
				JSONArray consumeFoodItemJsonArray = jsonObject
						.getJSONArray(KEY_CONSUME_FOOD_ITEM);
				for (int i = 0; i < consumeFoodItemJsonArray.length(); i++) {
					String itemName = consumeFoodItemJsonArray.getJSONObject(i)
							.getString(KEY_FOOD_ITEM_NAME);
					int itemCal = consumeFoodItemJsonArray.getJSONObject(i)
							.getInt(KEY_FOOD_ITEM_VALUE);
					String date = consumeFoodItemJsonArray.getJSONObject(i)
							.getString(KEY_FOOD_ITEM_DATE);
					dietCycle.setConsume_food_item(new FoodItem(itemName,
							itemCal, date));
				}

				if (!PrefHelper.getString(KEY_LAUNCH_HISTORY, "")
						.equalsIgnoreCase("")) {
					JSONArray historyArray = new JSONArray(
							PrefHelper.getString(KEY_LAUNCH_HISTORY, ""));
					for (int i = 0; i < historyArray.length(); i++) {
						dietCycle.setLaunch_history(historyArray.getLong(i));
					}
				}

				JSONArray mainJsonArray = jsonObject.getJSONArray(KEY_GRAPH);
				for (int i = 0; i < mainJsonArray.length(); i++) {

					JSONObject kCalJson = mainJsonArray.getJSONObject(i);
					Graph graph = new Graph(kCalJson.getDouble(KEY_GOAL));
					graph.setValues(getMap(kCalJson.getString(KEY_VALUE)));
					dietCycle.setmGraphs(graph);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dietCycle;
	}

	public int getmConsumeFoodPoints() {
		return mConsumeFoodPoints;
	}

	public void setmConsumeFoodPoints(int mConsumeFoodPoints) {
		this.mConsumeFoodPoints = mConsumeFoodPoints;
	}

	public long getOne_notification_time() {
		return one_notification_time;
	}

	public void setOne_notification_time(long one_notification_time) {
		this.one_notification_time = one_notification_time;
	}

	/**
	 * This method return Map object from String
	 * 
	 * @param x
	 * @return
	 */
	private static Map<String, Double> getMap(String x) {

		Map<String, Double> xxx = new HashMap<String, Double>();
		String a[] = x.replace("{", "").replace("}", "").replace(" ", "")
				.split(",");
		for (int i = 0; i < a.length; i++) {
			String b[] = a[i].split("=");
			if (b.length >= 2) {
				xxx.put(b[0], Double.parseDouble(b[1]));
			}

		}
		return xxx;
	}

}
