package com.game.connect4;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * This is the Main Menu Screen class
 * 
 * @author Yufan Lu
 */
public class MainScreen implements Screen {
	
	// Variables holding the persistent informations
	private final Stage stage;
	private final SpriteBatch spriteBatch;
	private final ConnectNApplication parentApp;
	private float screenWidth;
	private float screenHeight;
	
	// Constants
	private final static String MENU_SETTING_FILE_NAME = "Menu.json";
	
	// UI Controls
	private JSONObject apiObject;
	private BitmapFont menuFont;
	private Sound sfxClickedSound;
	
	private Group controlGroup;
	
	private LabelWithCenter[] labels;
	private ImageButton[] buttons;
	
	// Click Listener, the clicking event resides here
	private ClickListener buttonListener = new ClickListener() {
		public void clicked(InputEvent event, float x, float y) {
			sfxClickedSound.play(1.0f);
			if (event.getListenerActor() == buttons[0]) {
				// SFX button, turn the background music on/off
				parentApp.turnBackgroundMusic();
			} else if (event.getListenerActor() == buttons[1]) {
				// API button, fetch the weather info from url
				// fill the labels with the weather info
				WeatherInfo weather;
				try {
					weather = new WeatherInfo(apiObject);
				} catch (Exception ex) {
					return;
				}
				
				labels[1].label.setText(weather.title);
				labels[2].label.setText(weather.city);
				labels[3].label.setText(weather.description);
				labels[4].label.setText(weather.windInfo);
				
				for (int i = 1; i < 5; i++) {
					labels[i].label.pack();
					float posx = labels[i].center.posX - labels[i].label.getWidth() / 2.0f;
					float posy = labels[i].center.posY - labels[i].label.getHeight() / 2.0f;
					labels[i].label.setPosition(posx, posy);
					labels[i].fadeIn(1.0f);
				}
			} else if (event.getListenerActor() == buttons[2]) {
				// GAME button, start the game!
				controlGroup.addAction(Actions.sequence(Actions.alpha(0.0f, 0.5f), Actions.run(new Runnable() {
					@Override
					public void run() {
						parentApp.screen = new GameScreen(parentApp);
						parentApp.screen.show();
					}
				})));
			}
		}
	};
	
	/* FUNC: initialize(JSONObject) -> void
	 * DESC:
	 * 	initialize the whole menu screen and its UI controls
	 * ARG:
	 * 	setting -- the JSON node holding the settings (see Menu.json for example)
	 */
	private void initialize(JSONObject setting) {
		// save api node
		apiObject = setting.getJSONObject("api");
		
		// initialize the font and clicking sound
		FileHandle fontFile = Gdx.files.internal(setting.getJSONObject("font").getString("font_file"));
		FileHandle imageFile = Gdx.files.internal(setting.getJSONObject("font").getString("image_file"));
		menuFont = new BitmapFont(fontFile, imageFile, false);
		sfxClickedSound = Gdx.audio.newSound(Gdx.files.internal("button_click.wav"));
		
		// initialize the group and background
		controlGroup = new Group();
		controlGroup.setBounds(0, 0, screenWidth, screenHeight);
		Texture tex = new Texture(Gdx.files.internal(setting.getString("background")));
		Sprite sprite = new Sprite(tex);
		sprite.setSize(screenWidth, screenHeight);
		SpriteDrawable drawable = new SpriteDrawable(sprite);
		controlGroup.addActor(new Image(drawable));
		controlGroup.getColor().a = 0.0f;
		controlGroup.addAction(Actions.alpha(1.0f, 0.35f));
		
		// initialize the labels and buttons
		labels = LibGdxUtility.setupLabels(setting.getJSONArray("labels"), menuFont, controlGroup, screenWidth, screenHeight);
		buttons = LibGdxUtility.setupButtons(setting.getJSONArray("buttons"), controlGroup, screenWidth, screenHeight, buttonListener);
		
		stage.addActor(controlGroup);
	}
	
	/*
	 * FUNC: Constructor(ProjectApplication)
	 * DESC:
	 * 	Constructor for the MainScreen
	 * ARGS:
	 * 	parent -- parent window's pointer
	 */
	public MainScreen(ConnectNApplication parent) {
		spriteBatch = new SpriteBatch();
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		stage = new Stage(screenWidth, screenHeight, false, spriteBatch);
		parentApp = parent;
		JSONObject menuSetting = new JSONObject(Gdx.files.internal(MENU_SETTING_FILE_NAME).readString());
		initialize(menuSetting);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		stage.dispose();
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		screenWidth = width;
		screenHeight = height;
		stage.setViewport(width, height, false);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void pause() {
		// Irrelevant on desktop, ignore this
	}

	@Override
	public void resume() {
		// Irrelevant on desktop, ignore this
	}

}
