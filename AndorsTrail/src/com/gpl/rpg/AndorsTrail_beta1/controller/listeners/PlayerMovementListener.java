package com.gpl.rpg.AndorsTrail_beta1.controller.listeners;

import com.gpl.rpg.AndorsTrail_beta1.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail_beta1.util.Coord;

public interface PlayerMovementListener {
	void onPlayerMoved(PredefinedMap map, Coord newPosition, Coord previousPosition);
	void onPlayerEnteredNewMap(PredefinedMap map, Coord p);
}
