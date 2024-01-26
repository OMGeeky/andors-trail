package com.gpl.rpg.AndorsTrail.model.ability;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.util.ByteUtils;

public final class ActorCondition {
	public static final int MAGNITUDE_REMOVE_ALL = -99;
	public static final int DURATION_FOREVER = 999;
	public static final int DURATION_NONE = 0;

	public final ActorConditionType conditionType;
	public int magnitude;
	public int duration;

	public ActorCondition(
			ActorConditionType conditionType
			, int magnitude
			, int duration
	) {
		this.conditionType = conditionType;
		this.magnitude = magnitude;
		this.duration = duration;
	}

	public boolean isTemporaryEffect() { return isTemporaryEffect(duration); }
	public static boolean isTemporaryEffect(int duration) {
		return duration != DURATION_FOREVER;
	}


	// ====== PARCELABLE ===================================================================

	public ActorCondition(DataInputStream src, WorldContext world, int fileversion) throws IOException {
		final String conditionTypeID = src.readUTF();
		this.conditionType = world.actorConditionsTypes.getActorConditionType(conditionTypeID);
		this.magnitude = src.readInt();
		this.duration = src.readInt();
	}

	public void writeToParcel(DataOutputStream dest) throws IOException {
		dest.writeUTF(conditionType.conditionTypeID);
		dest.writeInt(magnitude);
		dest.writeInt(duration);
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void createHash(MessageDigest digest) {
		digest.update(ByteUtils.toBytes(conditionType.conditionTypeID));
		digest.update(ByteUtils.toBytes(magnitude));
		digest.update(ByteUtils.toBytes(duration));

	}
}
