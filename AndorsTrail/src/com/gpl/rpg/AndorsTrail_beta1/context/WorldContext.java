package com.gpl.rpg.AndorsTrail_beta1.context;

import com.gpl.rpg.AndorsTrail_beta1.model.ModelContainer;
import com.gpl.rpg.AndorsTrail_beta1.model.ability.ActorConditionTypeCollection;
import com.gpl.rpg.AndorsTrail_beta1.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail_beta1.model.actor.MonsterTypeCollection;
import com.gpl.rpg.AndorsTrail_beta1.model.item.DropListCollection;
import com.gpl.rpg.AndorsTrail_beta1.model.item.ItemCategoryCollection;
import com.gpl.rpg.AndorsTrail_beta1.model.item.ItemTypeCollection;
import com.gpl.rpg.AndorsTrail_beta1.model.map.MapCollection;
import com.gpl.rpg.AndorsTrail_beta1.model.quest.QuestCollection;
import com.gpl.rpg.AndorsTrail_beta1.resource.ConversationLoader;
import com.gpl.rpg.AndorsTrail_beta1.resource.VisualEffectCollection;
import com.gpl.rpg.AndorsTrail_beta1.resource.tiles.TileManager;

public final class WorldContext {
	//Objectcollections
	public final ConversationLoader conversationLoader;
	public final ItemTypeCollection itemTypes;
	public final ItemCategoryCollection itemCategories;
	public final MonsterTypeCollection monsterTypes;
	public final VisualEffectCollection visualEffectTypes;
	public final DropListCollection dropLists;
	public final QuestCollection quests;
	public final ActorConditionTypeCollection actorConditionsTypes;
	public final SkillCollection skills;

	//Objectcollections
	public final TileManager tileManager;

	//Model
	public final MapCollection maps;
	public ModelContainer model;

	public WorldContext() {
		this.conversationLoader = new ConversationLoader();
		this.itemTypes = new ItemTypeCollection();
		this.itemCategories = new ItemCategoryCollection();
		this.monsterTypes = new MonsterTypeCollection();
		this.visualEffectTypes = new VisualEffectCollection();
		this.dropLists = new DropListCollection();
		this.tileManager = new TileManager();
		this.maps = new MapCollection();
		this.quests = new QuestCollection();
		this.actorConditionsTypes = new ActorConditionTypeCollection();
		this.skills = new SkillCollection();
	}
	public WorldContext(WorldContext copy) {
		this.conversationLoader = copy.conversationLoader;
		this.itemTypes = copy.itemTypes;
		this.itemCategories = copy.itemCategories;
		this.monsterTypes = copy.monsterTypes;
		this.visualEffectTypes = copy.visualEffectTypes;
		this.dropLists = copy.dropLists;
		this.tileManager = copy.tileManager;
		this.maps = copy.maps;
		this.quests = copy.quests;
		this.model = copy.model;
		this.actorConditionsTypes = copy.actorConditionsTypes;
		this.skills = copy.skills;
	}
	public void resetForNewGame() {
		maps.resetForNewGame();
	}
}
