package com.gpl.rpg.AndorsTrail.util;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class ByteUtils {

	private static int bytes;

	public static String toHexString(byte[] bytes) { return toHexString(bytes, bytes.length); }
	public static String toHexString(byte[] bytes, int numBytes) {
		if (bytes == null) return "";
		if (bytes.length == 0) return "";
		final int len = Math.min(numBytes, bytes.length);
		StringBuilder result = new StringBuilder(len * 2);
		for (int i = 0; i < len; i++) {
			String h = Integer.toHexString(0xFF & bytes[i]);
			if (h.length() < 2) result.append('0');
			result.append(h);
		}
		return result.toString();
	}

	public static void xorArray(byte[] array, byte[] mask) {
		final int len = Math.min(array.length, mask.length);
		for(int i = 0; i < len; ++i) {
			array[i] ^= mask[i];
		}
	}

	public static byte[] toBytes(long l){

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			bytes = Long.BYTES;
		}else{
			bytes = 8;
		}
		ByteBuffer buffer = ByteBuffer.allocate(bytes);
		buffer.putLong(l);
		return buffer.array();
	}

	public static byte[] toBytes(int i){

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			bytes = Integer.BYTES;
		}else{
			bytes = 4;
		}
		ByteBuffer buffer = ByteBuffer.allocate(bytes);
		buffer.putInt(i);
		return buffer.array();
	}

	public static byte[] toBytes(float f) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			bytes = Float.BYTES;
		}else{
			bytes = 4;
		}
		ByteBuffer buffer = ByteBuffer.allocate(bytes);
		buffer.putFloat(f);
		return buffer.array();
	}
	public static byte[] toBytes(boolean bool){
		byte b;
		if(bool){
			b = 0;
		}else{
			b = 1;
		}
		return new byte[] {b};
	}
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public static byte[] toBytes(String x) {
		if (x == null) {
			return new byte[]{};
		}
		return x.getBytes(StandardCharsets.UTF_8);
	}


}
