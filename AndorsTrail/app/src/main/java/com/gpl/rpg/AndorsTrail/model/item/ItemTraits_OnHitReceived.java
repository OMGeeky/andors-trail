package com.gpl.rpg.AndorsTrail.model.item;

import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;

public class ItemTraits_OnHitReceived extends ItemTraits_OnUse {
	
	public final StatsModifierTraits changedStats_target;
	
	public ItemTraits_OnHitReceived(
			StatsModifierTraits changedStats
			, StatsModifierTraits changedStats_target
			, ActorConditionEffect[] addedConditions_source
			, ActorConditionEffect[] addedConditions_target
			) {
		super(changedStats_target, addedConditions_source, addedConditions_target);
		this.changedStats_target = changedStats_target;
	}

	public int calculateHitReceivedCost() {
		int costStats = changedStats == null ? 0 : changedStats.calculateHitCost();
		costStats += changedStats_target == null ? 0 : -changedStats_target.calculateHitCost();
		return costStats;
	}
	
}
