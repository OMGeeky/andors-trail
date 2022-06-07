package com.gpl.rpg.AndorsTrail.util;

import java.util.HashMap;
import java.util.Map;

import com.gpl.rpg.AndorsTrail.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

public class ThemeHelper {
	
	private static final class ThemeSet {
		int baseThemeRes, noBackgroundThemeRes, dialogThemeRes;
		public ThemeSet(int baseThemeRes, int noBackgroundThemeRes, int dialogThemeRes) {
			this.baseThemeRes = baseThemeRes;
			this.noBackgroundThemeRes = noBackgroundThemeRes;
			this.dialogThemeRes = dialogThemeRes;
		}
	}
	
	public static enum Theme {
		blue,
		green,
		charcoal
	}
	
	private static final Map<Theme, ThemeSet> THEME_SETS = new HashMap<ThemeHelper.Theme, ThemeHelper.ThemeSet>();
	private static Theme SELECTED_THEME = Theme.blue;
	private static boolean first = true;
	
	static {
		THEME_SETS.put(Theme.blue, new ThemeSet(R.style.AndorsTrailTheme_Blue, R.style.AndorsTrailTheme_Blue_NoBackground, R.style.AndorsTrailDialogTheme_Blue));
		THEME_SETS.put(Theme.green, new ThemeSet(R.style.AndorsTrailTheme_Green, R.style.AndorsTrailTheme_Green_NoBackground, R.style.AndorsTrailDialogTheme_Green));
		THEME_SETS.put(Theme.charcoal, new ThemeSet(R.style.AndorsTrailTheme_Charcoal, R.style.AndorsTrailTheme_Charcoal_NoBackground, R.style.AndorsTrailDialogTheme_Charcoal));
	}
	
	public static int getThemeColor(Context context, int attrResId) {
		TypedArray ta = context.getTheme().obtainStyledAttributes(new int[] {attrResId});
		int c = ta.getColor(0, Color.BLACK);
		ta.recycle();
		return c;
	}
	
	public static int getThemeResource(Context context, int attrResId) {
		TypedArray ta = context.getTheme().obtainStyledAttributes(new int[] {attrResId});
		int resId = ta.getResourceId(0, 0);
		ta.recycle();
		return resId;
	}
	
	public static Drawable getThemeDrawable(Context context, int attrResId) {
		TypedArray ta = context.getTheme().obtainStyledAttributes(new int[] {attrResId});
		Drawable d = ta.getDrawable(0);
		ta.recycle();
		return d;
	}
	
	public static int getBaseTheme() {
		return THEME_SETS.get(SELECTED_THEME).baseThemeRes;
	}
	
	public static int getNoBackgroundTheme() {
		return THEME_SETS.get(SELECTED_THEME).noBackgroundThemeRes;
	}
	
	public static int getDialogTheme() {
		return THEME_SETS.get(SELECTED_THEME).dialogThemeRes;
	}
	
	//Returns true if theme has changed after startup.
	public static boolean changeTheme(int id) {
		Theme t = Theme.values()[id];
		if (t == SELECTED_THEME) {
			first = false;
			return false;
		}
		SELECTED_THEME = t;
		if (first) {
			first = false;
			return false;
		}
		return true;
	}
}
