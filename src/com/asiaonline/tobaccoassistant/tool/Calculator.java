package com.asiaonline.tobaccoassistant.tool;

import android.app.Activity;
import android.content.SharedPreferences;

public class Calculator implements ICalculator {
	private SharedPreferences preferences;
	private int PLANT_NUM_BASE;
	private float YIELD_BASE;
	private float ORDER_YIELD_BASE;
	public Calculator(Activity act) {
		preferences = act.getSharedPreferences("calculate_base", Activity.MODE_PRIVATE);
		PLANT_NUM_BASE = preferences.getInt("PLANT_NUM_BASE", 1100);
		YIELD_BASE = preferences.getFloat("YIELD_BASE", 86.2f);
		ORDER_YIELD_BASE = preferences.getFloat("ORDER_YIELD_BASE", 88.8f);
	}
	@Override
	public int calculatePlantNum(float plantArea) {
		return (int)(PLANT_NUM_BASE*plantArea);
	}

	@Override
	public float calculateYield(float throughput) {
		return throughput*YIELD_BASE/100;
	}

	@Override
	public float calculateThroughput(float plantArea, float yieldPerMu) {
		return plantArea*yieldPerMu;
	}

	@Override
	public float calculateOrderYield(float yield) {
		return ORDER_YIELD_BASE*yield/100;
	}
	@Override
	public float calculateExportYield(float yield, float orderYield) {
		return yield-orderYield;
	}
}
