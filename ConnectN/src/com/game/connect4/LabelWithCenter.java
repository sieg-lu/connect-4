package com.game.connect4;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * This is the extended Label class,
 * with the label's center stored
 * 
 * @author Yufan Lu
 */
class LabelWithCenter {
	Label label;
	MyPoint<Float> center = new MyPoint<Float>();
	
	/* FUNC: fadeIn(float) -> void
	 * DESC:
	 * 	fade in the label
	 * ARG:
	 * 	duration -- duration time
	 */
	public void fadeIn(float duration) {
		label.getColor().a = 0.0f;
		label.addAction(Actions.alpha(1.0f, duration));
	}
};

