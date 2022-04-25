package com.gpl.rpg.AndorsTrail_beta1.model.map;

import com.gpl.rpg.AndorsTrail_beta1.util.Size;

public final class MapLayer {
	public final int[][] tiles;

	public MapLayer(Size size) {
		tiles = new int[size.width][size.height];
	}
	public void setTile(int type, int x, int y) {
		tiles[x][y] = type;
	}
}
