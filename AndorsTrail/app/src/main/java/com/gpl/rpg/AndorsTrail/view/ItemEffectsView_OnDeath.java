package com.gpl.rpg.AndorsTrail.view;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnUse;

public final class ItemEffectsView_OnDeath extends LinearLayout {
	private final LinearLayout itemeffect_ondeath_ontarget_list;
	private final ActorConditionEffectList itemeffect_ondeath_conditions_source;
	private final TextView itemeffect_ondeath_target_title;

	public ItemEffectsView_OnDeath(Context context, AttributeSet attr) {
		super(context, attr);
		setFocusable(false);
		setOrientation(LinearLayout.VERTICAL);
		inflate(context, R.layout.itemeffectview_ondeath, this);

		itemeffect_ondeath_ontarget_list = (LinearLayout) findViewById(R.id.itemeffect_ondeath_ontarget_list);
		itemeffect_ondeath_target_title = (TextView) findViewById(R.id.itemeffect_ondeath_target_title);
		itemeffect_ondeath_conditions_source = (ActorConditionEffectList) findViewById(R.id.itemeffect_ondeath_conditions_source);
	}

	public void update(ItemTraits_OnUse effects) {
		ArrayList<ActorConditionEffect> sourceEffects = new ArrayList<ActorConditionEffect>();

		itemeffect_ondeath_ontarget_list.removeAllViews();
		
		boolean sourceHasStatsModifiers = false;
		if (effects != null) {
			final Context context = getContext();
			final Resources res = getResources();
			if (effects.addedConditions_source != null) sourceEffects.addAll(Arrays.asList(effects.addedConditions_source));

			if (effects.changedStats != null) {
				describeStatsModifierTraits(effects.changedStats, context, res, itemeffect_ondeath_ontarget_list);
				if (effects.changedStats.currentAPBoost != null || effects.changedStats.currentHPBoost != null) sourceHasStatsModifiers = true;
			}
		}
		itemeffect_ondeath_conditions_source.update(sourceEffects);
		if (sourceEffects.isEmpty() && !sourceHasStatsModifiers) {
			itemeffect_ondeath_target_title.setVisibility(View.GONE);
		} else {
			itemeffect_ondeath_target_title.setVisibility(View.VISIBLE);
		}
	}

	public static void describeStatsModifierTraits(StatsModifierTraits traits, Context context, Resources res, LinearLayout listView) {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		if (traits.currentAPBoost != null) {
			final int label = traits.currentAPBoost.max > 0 ? R.string.iteminfo_effect_increase_current_ap : R.string.iteminfo_effect_decrease_current_ap;
			final TextView tv = new TextView(context);
			tv.setText(res.getString(label, traits.currentAPBoost.toMinMaxAbsString()));
			listView.addView(tv, layoutParams);
		}
		if (traits.currentHPBoost != null) {
			final int label = traits.currentHPBoost.max > 0 ? R.string.iteminfo_effect_increase_current_hp : R.string.iteminfo_effect_decrease_current_hp;
			final TextView tv = new TextView(context);
			tv.setText(res.getString(label, traits.currentHPBoost.toMinMaxAbsString()));
			listView.addView(tv, layoutParams);
		}
	}
}
