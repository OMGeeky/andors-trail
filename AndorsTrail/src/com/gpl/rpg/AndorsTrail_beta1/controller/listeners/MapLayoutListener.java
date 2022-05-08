package com.gpl.rpg.AndorsTrail_beta1.controller.listeners;

import com.gpl.rpg.AndorsTrail_beta1.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail_beta1.model.map.PredefinedMap;
import com.gpl.rpg.AndorsTrail_beta1.util.Coord;

public interface MapLayoutListener {
	void onLootBagCreated(PredefinedMap map, Coord p);
	void onLootBagRemoved(PredefinedMap map, Coord p);
	void onMapTilesChanged(PredefinedMap map, LayeredTileMap tileMap);
}
