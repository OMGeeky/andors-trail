package com.gpl.rpg.AndorsTrail.model;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.gpl.rpg.AndorsTrail.util.ByteUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public final class WorldData {
	private long worldTime = 0; // Measured in number of game rounds
	private final HashMap<String, Long> timers = new HashMap<String, Long>();

	public WorldData() {}

	public void tickWorldTime() {
		++worldTime;
	}
	public void tickWorldTime(int ticks) {
		worldTime += ticks;
	}
	public long getWorldTime() {
		return worldTime;
	}

	public void createTimer(String name) {
		timers.put(name, worldTime);
	}

	public void removeTimer(String name) {
		timers.remove(name);
	}

	public boolean hasTimerElapsed(String name, long duration) {
		Long v = timers.get(name);
		if (v == null) return false;
		return v + duration <= worldTime;
	}

	// ====== PARCELABLE ===================================================================

	public WorldData(DataInputStream src, int fileversion) throws IOException {
		worldTime = src.readLong();
		final int numTimers = src.readInt();
		for(int i = 0; i < numTimers; ++i) {
			final String timerName = src.readUTF();
			final long value = src.readLong();
			this.timers.put(timerName, value);
		}
	}

	public void writeToParcel(DataOutputStream dest) throws IOException {
		dest.writeLong(worldTime);
		dest.writeInt(timers.size());
		for(Map.Entry<String, Long> e : timers.entrySet()) {
			dest.writeUTF(e.getKey());
			dest.writeLong(e.getValue());
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void createHash(MessageDigest digest) {
		digest.update(ByteUtils.toBytes(worldTime));
		for (Map.Entry<String, Long> e: timers.entrySet() ) {
			digest.update(ByteUtils.toBytes(e.getKey()));
			digest.update(ByteUtils.toBytes(e.getValue()));
		}
	}
}
