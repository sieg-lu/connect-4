package com.game.connect4;

import org.json.JSONArray;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * This is the set of LibGdx related utility functions
 * 
 * @author Yufan Lu
 */

public class LibGdxUtility {

	/* FUNC: makeLabel(BitmapFont, String, float, float, float, Color) -> Label
	 * DESC:
	 * 	making a label with the given informations
	 * ARGS:
	 * 	font 			-- the font used by text
	 * 	content 		-- the label content
	 * 	centerX 		-- the x center the label should be
	 * 	centerY 		-- the y center the label should be
	 * 	scale 			-- to scale the label
	 * 	color 			-- the text's color
	 * RET:
	 * 	the label with given informations
	 */
	public static Label makeLabel(BitmapFont font, String content, float centerX, float centerY, float scale, Color color) {
		LabelStyle style = new LabelStyle(font, font.getColor());
		Label label = new Label(content, style);
		label.setPosition(centerX - label.getWidth() / 2.0f, centerY - label.getHeight() / 2.0f);
		label.setFontScale(scale);
		label.setColor(color);
		return label;
	}
	
	/* FUNC: makeButton(String, float, float, ClickListener) -> ImageButton
	 * DESC:
	 * 	making a ImageButton with the given informations
	 * ARGS:
	 * 	texName 		-- the texture's file name
	 * 	centerX 		-- the x center the label should be
	 * 	centerY 		-- the y center the label should be
	 * 	buttonListener 	-- the clicking event listener
	 * RET:
	 * 	the ImageButton with given informations
	 */
	public static ImageButton makeButton(String texName, float centerX, float centerY, ClickListener buttonListener) {
		Texture tex = new Texture(Gdx.files.internal(texName));
		SpriteDrawable drawable = new SpriteDrawable(new Sprite(tex));
		ImageButton button = new ImageButton(drawable);
		button.setPosition(centerX - button.getWidth() / 2.0f, centerY - button.getHeight() / 2.0f);
		button.addListener(buttonListener);
		return button;
	}
	
	/* FUNC: makeBackground(String, float, float) -> Image
	 * DESC:
	 * 	making a background Image
	 * ARGS:
	 * 	texName 		-- the texture's file name
	 * 	width 			-- the image's width
	 * 	height 			-- the image's height
	 * RET:
	 * 	the Image
	 */
	public static Image makeBackground(String texName, float width, float height) {
		Texture tex = new Texture(Gdx.files.internal(texName));
		Sprite sprite = new Sprite(tex);
		sprite.setSize(width, height);
		SpriteDrawable drawable = new SpriteDrawable(sprite);
		return new Image(drawable);
	}
	
	/* FUNC: setupButtons(JSONArray, Group, float, float, ClickListener) -> ImageButton[]
	 * DESC:
	 * 	make a list of buttons according to the information in JSONArray
	 * ARGS:
	 * 	buttonObjArray 	-- the JSON setting node
	 * 	groupToPut 		-- the group the buttons should belong to
	 * 	screenWidth		-- the screen's width
	 * 	screenHeight	-- the screen's height
	 * 	buttonListener 	-- the clicking event listener
	 * RET:
	 * 	the button list
	 */
	public static ImageButton[] setupButtons(JSONArray buttonObjArray, Group groupToPut, float screenWidth, float screenHeight, ClickListener buttonListener) {
		ImageButton[] buttons = new ImageButton[buttonObjArray.length()];
		for (int i = 0; i < buttonObjArray.length(); i++) {
			JSONObject buttonObj = buttonObjArray.getJSONObject(i);
			buttons[i] = makeButton(
					buttonObj.getString("image"),
					screenWidth * (float) buttonObj.getDouble("x_center_ratio"), 
					screenHeight * (float) buttonObj.getDouble("y_center_ratio"),
					buttonListener);
			if (groupToPut != null) {
				groupToPut.addActor(buttons[i]);
			}
			float x = buttons[i].getX();
			float y = buttons[i].getY();
			buttons[i].setPosition(0.0f, 0.0f);
			buttons[i].addAction(Actions.moveTo(x, y, 1.0f, Interpolation.exp10Out));
		}
		return buttons;
	}
	
	/* FUNC: setupButtons(JSONArray, BitmapFont, Group, float, float) -> ImageButton[]
	 * DESC:
	 * 	make a list of labels according to the information in JSONArray
	 * ARGS:
	 * 	buttonObjArray 	-- the JSON setting node
	 * 	font 			-- the text font
	 * 	groupToPut 		-- the group the buttons should belong to
	 * 	screenWidth		-- the screen's width
	 * 	screenHeight	-- the screen's height
	 * RET:
	 * 	the button list
	 */
	public static LabelWithCenter[] setupLabels(JSONArray labelObjArray, BitmapFont font, Group groupToPut, float screenWidth, float screenHeight) {
		LabelWithCenter[] labels = new LabelWithCenter[labelObjArray.length()];
		for (int i = 0; i < labelObjArray.length(); i++) {
			JSONObject labelObj = labelObjArray.getJSONObject(i);
			labels[i] = new LabelWithCenter();
			labels[i].center.posX = screenWidth * (float) labelObj.getDouble("x_center_ratio");
			labels[i].center.posY = screenHeight * (float) labelObj.getDouble("y_center_ratio");
			labels[i].label = makeLabel(
					font, labelObj.getString("content"),
					labels[i].center.posX, labels[i].center.posY,
					(float) labelObj.getDouble("scale"),
					Color.valueOf(labelObj.getString("color")));
			if (groupToPut != null) {
				groupToPut.addActor(labels[i].label);
			}
			labels[i].fadeIn(1.0f);
		}
		return labels;
	}
}
