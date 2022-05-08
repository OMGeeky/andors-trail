package com.gpl.rpg.AndorsTrail_beta1.controller.listeners;

import com.gpl.rpg.AndorsTrail_beta1.controller.VisualEffectController.SpriteMoveAnimation;
import com.gpl.rpg.AndorsTrail_beta1.controller.VisualEffectController.VisualEffectAnimation;
import com.gpl.rpg.AndorsTrail_beta1.util.CoordRect;

public interface VisualEffectFrameListener {
	void onNewAnimationFrame(VisualEffectAnimation animation, int tileID, int textYOffset);
	void onAnimationCompleted(VisualEffectAnimation animation);
	void onSpriteMoveStarted(SpriteMoveAnimation animation);
	void onNewSpriteMoveFrame(SpriteMoveAnimation animation);
	void onSpriteMoveCompleted(SpriteMoveAnimation animation);
	void onAsyncAreaUpdate(CoordRect area);
}
