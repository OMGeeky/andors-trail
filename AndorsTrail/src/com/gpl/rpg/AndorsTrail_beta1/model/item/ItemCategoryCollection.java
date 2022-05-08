package com.gpl.rpg.AndorsTrail_beta1.model.item;

import java.util.HashMap;

import com.gpl.rpg.AndorsTrail_beta1.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail_beta1.resource.parsers.ItemCategoryParser;
import com.gpl.rpg.AndorsTrail_beta1.util.L;

public final class ItemCategoryCollection {
	private final HashMap<String, ItemCategory> itemCategories = new HashMap<String, ItemCategory>();

	public ItemCategory getItemCategory(String id) {
		if (AndorsTrailApplication.DEVELOPMENT_VALIDATEDATA) {
			if (!itemCategories.containsKey(id)) {
				L.log("WARNING: Cannot find ItemCategory for id \"" + id + "\".");
			}
		}
		return itemCategories.get(id);
	}

	public void initialize(final ItemCategoryParser parser, String input) {
		parser.parseRows(input, itemCategories);
	}

	// Unit test method. Not part of the game logic.
	public HashMap<String, ItemCategory> UNITTEST_getAllItemCategories() {
		return itemCategories;
	}
}
