package com.gpl.rpg.AndorsTrail_beta1.controller.listeners;

import com.gpl.rpg.AndorsTrail_beta1.model.actor.Monster;
import com.gpl.rpg.AndorsTrail_beta1.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail_beta1.util.Coord;
import com.gpl.rpg.AndorsTrail_beta1.util.CoordRect;

public interface MonsterSpawnListener {
	void onMonsterSpawned(PredefinedMap map, Monster m);
	void onMonsterRemoved(PredefinedMap map, Monster m, CoordRect previousPosition);
	void onSplatterAdded(PredefinedMap map, Coord p);
	void onSplatterChanged(PredefinedMap map, Coord p);
	void onSplatterRemoved(PredefinedMap map, Coord p);
}
