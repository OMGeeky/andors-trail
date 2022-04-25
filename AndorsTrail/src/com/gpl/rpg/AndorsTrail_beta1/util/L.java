package com.gpl.rpg.AndorsTrail_beta1.util;

import android.util.Log;

import com.gpl.rpg.AndorsTrail_beta1.AndorsTrailApplication;

public final class L {
	private static final String TAG = "AndorsTrail";

	public static void log(String s) {
		if (AndorsTrailApplication.DEVELOPMENT_DEBUGMESSAGES) {
			Log.w(TAG, s);
		}
	}
}
