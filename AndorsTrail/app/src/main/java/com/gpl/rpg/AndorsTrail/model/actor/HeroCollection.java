package com.gpl.rpg.AndorsTrail.model.actor;

import java.util.LinkedList;
import java.util.List;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.resource.DynamicTileLoader;

public class HeroCollection {

	//Id in save is the index in the list +1
	private static final List<HeroDesc> listOfHeroes = new LinkedList<HeroCollection.HeroDesc>();
	public static boolean isInitialized = false;
	
	public static class HeroDesc {
		int tileIdInSpritesheet, tileIdInManager, smallSpriteResId, largeSpriteResId;
		public HeroDesc(int tileIdInSpritesheet, int smallSpriteResId, int largeSpriteResId) {
			this.smallSpriteResId = smallSpriteResId;
			this.largeSpriteResId = largeSpriteResId;
			this.tileIdInSpritesheet = tileIdInSpritesheet;
			//tileIdInManager will be filled by prepareHeroesTileId, called by ResourceLoader
		}
	}
	
	static {
		listOfHeroes.add(new HeroDesc(0, R.drawable.char_hero, R.drawable.char_hero_large));
		listOfHeroes.add(new HeroDesc(0, R.drawable.char_hero_maksiu_girl_01, R.drawable.char_hero_maksiu_girl_01_large));
		listOfHeroes.add(new HeroDesc(0, R.drawable.char_hero_maksiu_boy_01, R.drawable.char_hero_maksiu_boy_01_large));
	}
	
	public static int getHeroTileIdForTileManage(int heroId) {
		//Id in save is the index in the list +1
		heroId--;
		if (heroId >= listOfHeroes.size()) return -1;
		return listOfHeroes.get(heroId).tileIdInManager;
	}

	public static int getHeroSmallSpriteId(int heroId) {
		//Id in save is the index in the list +1
		heroId--;
		if (heroId >= listOfHeroes.size()) return -1;
		return listOfHeroes.get(heroId).smallSpriteResId;
	}

	public static int getHeroLargeSprite(int heroId) {
		//Id in save is the index in the list +1
		heroId--;
		if (heroId >= listOfHeroes.size()) return -1;
		return listOfHeroes.get(heroId).largeSpriteResId;
	}
	
	public static void prepareHeroesTileId(DynamicTileLoader loader) {
		for (HeroDesc hero : listOfHeroes) {
			hero.tileIdInManager = loader.prepareTileID(hero.smallSpriteResId, hero.tileIdInSpritesheet);
		}
	}
	
}
