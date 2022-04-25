package com.gpl.rpg.AndorsTrail_beta1.controller.listeners;

import java.util.Collection;

import com.gpl.rpg.AndorsTrail_beta1.model.actor.Monster;
import com.gpl.rpg.AndorsTrail_beta1.model.item.Loot;
import com.gpl.rpg.AndorsTrail_beta1.model.map.MapObject;

public interface WorldEventListener {
	void onPlayerStartedConversation(Monster m, String phraseID);
	void onScriptAreaStartedConversation(String phraseID);
	void onPlayerSteppedOnMonster(Monster m);
	void onPlayerSteppedOnMapSignArea(MapObject area);
	void onPlayerSteppedOnKeyArea(MapObject area);
	void onPlayerSteppedOnRestArea(MapObject area);
	void onPlayerSteppedOnGroundLoot(Loot loot);
	void onPlayerPickedUpGroundLoot(Loot loot);
	void onPlayerFoundMonsterLoot(Collection<Loot> loot, int exp);
	void onPlayerPickedUpMonsterLoot(Collection<Loot> loot, int exp);
	void onPlayerRested();
	void onPlayerDied(int lostExp);
}
