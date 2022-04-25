package com.gpl.rpg.AndorsTrail_beta1.controller.listeners;

import com.gpl.rpg.AndorsTrail_beta1.model.actor.Monster;

public interface CombatTurnListener {
	void onCombatStarted();
	void onCombatEnded();
	void onNewPlayerTurn();
	void onMonsterIsAttacking(Monster m);
}
