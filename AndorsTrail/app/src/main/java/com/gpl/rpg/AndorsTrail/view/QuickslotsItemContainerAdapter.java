package com.gpl.rpg.AndorsTrail.view;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer.ItemEntry;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCollection;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;

public class QuickslotsItemContainerAdapter extends ItemContainerAdapter {
	
	public static final ItemEntry EMPTY_ENTRY = new ItemEntry(null, 0);
	public static Drawable EMPTY_DRAWABLE = null;

	public QuickslotsItemContainerAdapter(Context context, TileManager tileManager, ItemContainer items, Player player, TileCollection tileCollection) {
		super(context, tileManager, items, player, tileCollection);
		insert(EMPTY_ENTRY, 0);
		if (EMPTY_DRAWABLE == null) {

			ColorMatrix matrix = new ColorMatrix();
		    matrix.setSaturation(0);
		    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
		    EMPTY_DRAWABLE = context.getResources().getDrawable(R.drawable.ui_icon_equipment).mutate();
		    EMPTY_DRAWABLE.setColorFilter(filter);
		}
	}

	public QuickslotsItemContainerAdapter(Context context, TileManager tileManager, ItemContainer items, Player player) {
		this(context, tileManager, items, player, tileManager.loadTilesFor(items, context.getResources()));
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (getItem(position) == EMPTY_ENTRY) {
			View result = convertView;
			if (result == null) {
				result = View.inflate(getContext(), R.layout.inventoryitemview, null);
			}
			TextView tv = (TextView) result;

		    tv.setCompoundDrawablesWithIntrinsicBounds(EMPTY_DRAWABLE, null, null, null);
			tv.setText(R.string.inventory_unassign);
			return result;
		}
		return super.getView(position, convertView, parent);
	}
	
	@Override
	public long getItemId(int position) {
		if (getItem(position) == EMPTY_ENTRY) return EMPTY_ENTRY.hashCode();
		return super.getItemId(position);
	}

}
