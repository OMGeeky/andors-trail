package com.gpl.rpg.AndorsTrail_beta1.model.quest;

public final class QuestLogEntry {
	public final int progress;
	public final String logtext;
	public final int rewardExperience;
	public final boolean finishesQuest;

	public QuestLogEntry(
			int progress
			, String logtext
			, int rewardExperience
			, boolean finishesQuest
	) {
		this.progress = progress;
		this.logtext = logtext;
		this.rewardExperience = rewardExperience;
		this.finishesQuest = finishesQuest;
	}
}
