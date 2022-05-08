package com.gpl.rpg.AndorsTrail_beta1.controller.listeners;

import com.gpl.rpg.AndorsTrail_beta1.model.actor.Monster;
import com.gpl.rpg.AndorsTrail_beta1.util.Coord;

public interface CombatSelectionListener {
	void onMonsterSelected(Monster m, Coord selectedPosition, Coord previousSelection);
	void onMovementDestinationSelected(Coord selectedPosition, Coord previousSelection);
	void onCombatSelectionCleared(Coord previousSelection);
}
