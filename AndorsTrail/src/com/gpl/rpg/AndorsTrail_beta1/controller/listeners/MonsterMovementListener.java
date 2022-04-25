package com.gpl.rpg.AndorsTrail_beta1.controller.listeners;

import com.gpl.rpg.AndorsTrail_beta1.model.actor.Monster;
import com.gpl.rpg.AndorsTrail_beta1.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail_beta1.util.CoordRect;

public interface MonsterMovementListener {
	void onMonsterSteppedOnPlayer(Monster m);
	void onMonsterMoved(PredefinedMap map, Monster m, CoordRect previousPosition);
}
