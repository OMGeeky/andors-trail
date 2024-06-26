package com.gpl.rpg.AndorsTrail.model.script;

public final class ScriptEffect {
	public static enum ScriptEffectType {
		questProgress
		, dropList
		, skillIncrease
		, actorCondition
		, actorConditionImmunity
		, alignmentChange
		, alignmentSet
		, giveItem
		, createTimer
		, spawnAll
		, removeSpawnArea
		, deactivateSpawnArea
		, activateMapObjectGroup
		, deactivateMapObjectGroup
		, removeQuestProgress
		, changeMapFilter
		, mapchange
	}

	public final ScriptEffectType type;
	public final String effectID;
	public final int value;
	public final String mapName;

	public ScriptEffect(
			ScriptEffectType type
			, String effectID
			, int value
			, String mapName
	) {
		this.type = type;
		this.effectID = effectID;
		this.value = value;
		this.mapName = mapName;
	}
}
