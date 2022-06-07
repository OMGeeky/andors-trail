package com.gpl.rpg.AndorsTrail.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionEffect;
import com.gpl.rpg.AndorsTrail.model.ability.traits.StatsModifierTraits;
import com.gpl.rpg.AndorsTrail.model.item.ItemTraits_OnHitReceived;

public final class ItemEffectsView_OnHitReceived extends LinearLayout {
	private final LinearLayout itemeffect_onhitreceived_onsource_list;
	private final LinearLayout itemeffect_onhitreceived_ontarget_list;
	private final ActorConditionEffectList itemeffect_onhitreceived_conditions_source;
	private final ActorConditionEffectList itemeffect_onhitreceived_conditions_target;
	private final TextView itemeffect_onhitreceived_source_title;
	private final TextView itemeffect_onhitreceived_target_title;

	public ItemEffectsView_OnHitReceived(Context context, AttributeSet attr) {
		super(context, attr);
		setFocusable(false);
		setOrientation(LinearLayout.VERTICAL);
		inflate(context, R.layout.itemeffectview_onhitreceived, this);

		itemeffect_onhitreceived_onsource_list = (LinearLayout) findViewById(R.id.itemeffect_onhitreceived_onsource_list);
		itemeffect_onhitreceived_ontarget_list = (LinearLayout) findViewById(R.id.itemeffect_onhitreceived_ontarget_list);
		itemeffect_onhitreceived_source_title = (TextView) findViewById(R.id.itemeffect_onhitreceived_source_title);
		itemeffect_onhitreceived_target_title = (TextView) findViewById(R.id.itemeffect_onhitreceived_target_title);
		itemeffect_onhitreceived_conditions_source = (ActorConditionEffectList) findViewById(R.id.itemeffect_onhitreceived_conditions_source);
		itemeffect_onhitreceived_conditions_target = (ActorConditionEffectList) findViewById(R.id.itemeffect_onhitreceived_conditions_target);
	}

	public void update(Collection<ItemTraits_OnHitReceived> effects) {
		ArrayList<ActorConditionEffect> sourceEffects = new ArrayList<ActorConditionEffect>();
		ArrayList<ActorConditionEffect> targetEffects = new ArrayList<ActorConditionEffect>();

		itemeffect_onhitreceived_onsource_list.removeAllViews();
		itemeffect_onhitreceived_ontarget_list.removeAllViews();
		
		boolean sourceHasStatsModifiers = false;
		boolean targetHasStatsModifiers = false;
		if (effects != null) {
			final Context context = getContext();
			final Resources res = getResources();
			for (ItemTraits_OnHitReceived t : effects) {
				if (t.addedConditions_source != null) sourceEffects.addAll(Arrays.asList(t.addedConditions_source));
				if (t.addedConditions_target != null) targetEffects.addAll(Arrays.asList(t.addedConditions_target));

				if (t.changedStats != null) {
					describeStatsModifierTraits(t.changedStats, context, res, itemeffect_onhitreceived_onsource_list);
					if (t.changedStats.currentAPBoost != null || t.changedStats.currentHPBoost != null) sourceHasStatsModifiers = true;
				}
				if (t.changedStats_target != null) {
					describeStatsModifierTraits(t.changedStats_target, context, res, itemeffect_onhitreceived_ontarget_list);
					if (t.changedStats_target.currentAPBoost != null || t.changedStats_target.currentHPBoost != null) targetHasStatsModifiers = true;
				}
			}
		}
		itemeffect_onhitreceived_conditions_source.update(sourceEffects);
		itemeffect_onhitreceived_conditions_target.update(targetEffects);
		if (sourceEffects.isEmpty() && !sourceHasStatsModifiers) {
			itemeffect_onhitreceived_source_title.setVisibility(View.GONE);
		} else {
			itemeffect_onhitreceived_source_title.setVisibility(View.VISIBLE);
		}
		if (targetEffects.isEmpty() && !targetHasStatsModifiers) {
			itemeffect_onhitreceived_target_title.setVisibility(View.GONE);
		} else {
			itemeffect_onhitreceived_target_title.setVisibility(View.VISIBLE);
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
