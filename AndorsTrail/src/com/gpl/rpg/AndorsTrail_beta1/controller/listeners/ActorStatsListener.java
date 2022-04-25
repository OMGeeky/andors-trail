package com.gpl.rpg.AndorsTrail_beta1.controller.listeners;

import com.gpl.rpg.AndorsTrail_beta1.model.actor.Actor;
import com.gpl.rpg.AndorsTrail_beta1.model.actor.Player;

public interface ActorStatsListener {
	void onActorHealthChanged(Actor actor);
	void onActorAPChanged(Actor actor);
	void onActorAttackCostChanged(Actor actor, int newAttackCost);
	void onActorMoveCostChanged(Actor actor, int newMoveCost);
	void onPlayerReequipCostChanged(Player actor, int newAttackCost);
	void onPlayerUseCostChanged(Player actor, int newMoveCost);
}
