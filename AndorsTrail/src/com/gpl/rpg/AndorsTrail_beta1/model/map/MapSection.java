package com.gpl.rpg.AndorsTrail_beta1.model.map;

import com.gpl.rpg.AndorsTrail_beta1.util.ByteUtils;
import com.gpl.rpg.AndorsTrail_beta1.util.CoordRect;

public final class MapSection {
	public final MapLayer layerBase;
	public final MapLayer layerGround;
	public final MapLayer layerObjects;
	public final MapLayer layerAbove;
	public final MapLayer layerTop;
	public final boolean[][] isWalkable;
	private final byte[] layoutHash;

	public MapSection(
			MapLayer layerBase
			, MapLayer layerGround
			, MapLayer layerObjects
			, MapLayer layerAbove
			, MapLayer layerTop
			, boolean[][] isWalkable
			, byte[] layoutHash
	) {
		this.layerBase = layerBase;
		this.layerGround = layerGround;
		this.layerObjects = layerObjects;
		this.layerAbove = layerAbove;
		this.layerTop = layerTop;
		this.isWalkable = isWalkable;
		this.layoutHash = layoutHash;
	}

	public void replaceLayerContentsWith(final MapSection replaceLayersWith, final CoordRect replacementArea) {
		replaceTileLayerSection(layerBase, replaceLayersWith.layerBase, replacementArea);
		replaceTileLayerSection(layerGround, replaceLayersWith.layerGround, replacementArea);
		replaceTileLayerSection(layerObjects, replaceLayersWith.layerObjects, replacementArea);
		replaceTileLayerSection(layerAbove, replaceLayersWith.layerAbove, replacementArea);
		replaceTileLayerSection(layerTop, replaceLayersWith.layerTop, replacementArea);
		if (replaceLayersWith.isWalkable != null) {
			final int dy = replacementArea.topLeft.y;
			final int height = replacementArea.size.height;
			for (int sx = 0, dx = replacementArea.topLeft.x; sx < replacementArea.size.width; ++sx, ++dx) {
				System.arraycopy(replaceLayersWith.isWalkable[sx], 0, isWalkable[dx], dy, height);
			}
		}
		ByteUtils.xorArray(layoutHash, replaceLayersWith.layoutHash);
	}

	private static void replaceTileLayerSection(MapLayer dest, MapLayer src, CoordRect area) {
		if (src == null) return;
		final int dy = area.topLeft.y;
		final int height = area.size.height;
		for (int sx = 0, dx = area.topLeft.x; sx < area.size.width; ++sx, ++dx) {
			System.arraycopy(src.tiles[sx], 0, dest.tiles[dx], dy, height);
		}
	}

	public String calculateHash(String filter) {
		byte[] hash = layoutHash.clone();
		if (filter != null) ByteUtils.xorArray(hash, filter.getBytes());
		return ByteUtils.toHexString(hash, 4);
	}
}
