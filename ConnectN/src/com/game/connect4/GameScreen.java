package com.game.connect4;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * This is the Game Screen class
 * 
 * @author Yufan Lu
 */
public class GameScreen implements Screen {
	// Persistent variables
	private final Stage stage;
	private final SpriteBatch spriteBatch;
	private final ConnectNApplication parentApp;
	private float screenWidth;
	private float screenHeight;
	
	// Constants
	private final static String GAME_SETTING_FILE_NAME = "Game.json";
	
	// Controls
	private BitmapFont gameFont;
	private Sound sfxClickedSound;
	
	private Group gameGroup;
	private Group winningGroup;
	private Group buttonGroup;

	private GameLogic gameLogic;
	private Drawable[] gridBackground;
	private ImageButton[][] gridButtons;
	
	private Map<Actor, Integer> map;
	private LabelWithCenter[] winningLabels;

	private ImageButton[] buttons;
	
	private int winner;
	
	// The grids' listener
	private ClickListener buttonListener = new ClickListener() {
		public void clicked(InputEvent event, float x, float y) {
			if (winner != -1) {
				return;
			}
			sfxClickedSound.play(1.0f);
			int tmp = map.get(event.getListenerActor());
			final int xIndex = tmp >> 16;
			final int yIndex = tmp & 0x0000ffff;
			
			if (gameLogic.clickGrid(xIndex, yIndex)) {
				float xpos = gridButtons[xIndex][yIndex].getX();
				float ypos = gridButtons[xIndex][yIndex].getY();
				gridButtons[xIndex][yIndex].setY(Gdx.graphics.getHeight());
				gridButtons[xIndex][yIndex].getStyle().imageUp = gridBackground[gameLogic.getGrid(xIndex, yIndex)];
				Gdx.input.setInputProcessor(null);
				gridButtons[xIndex][yIndex].addAction(
						Actions.sequence(Actions.moveTo(xpos, ypos, 0.5f), Actions.run(new Runnable() {
					@Override
					public void run() {
						winner = gameLogic.winner();
						if (winner != -1) {
							final List<MyPoint<Integer> > track = gameLogic.GetWinningTrack();
							for (int i = 0; i < track.size(); i++) {
								final MyPoint<Integer> point = track.get(i);
								gridButtons[point.posX][point.posY].addAction(
										Actions.sequence(Actions.alpha(0.0f, 0.5f), 
										Actions.run(new Runnable() {
											@Override
											public void run() {
												gridButtons[point.posX][point.posY].getStyle().imageUp = gridBackground[4];
											}											
										}),
										Actions.sequence(Actions.alpha(1.0f, 0.5f))));
							}
							gameGroup.addAction(Actions.sequence(Actions.alpha(0.0f, 5.0f), Actions.run(new Runnable() {
								@Override
								public void run() {
									gameGroup.remove();
									stage.addActor(winningGroup);
									stage.addActor(buttonGroup);
									Gdx.input.setInputProcessor(stage);
									winningLabels[0].label.setText(winner == 1 ? "Winner Is First Player!" : "Winner Is Second Player!");
									winningGroup.getColor().a = 0.0f;
									winningGroup.addAction(Actions.alpha(1.0f, 0.5f));
								}
							})));
						}
						Gdx.input.setInputProcessor(stage);
					}
				})));
				if (yIndex < gridButtons[xIndex].length - 1) {
					gridButtons[xIndex][yIndex + 1].getStyle().imageUp = 
							gridBackground[gameLogic.getGrid(xIndex, yIndex + 1)];
					float upypos = gridButtons[xIndex][yIndex + 1].getY();
					gridButtons[xIndex][yIndex + 1].setY(ypos);
					gridButtons[xIndex][yIndex + 1].addAction(Actions.moveTo(xpos, upypos, 0.5f));
				}
				updateMap(gameLogic);
			}
		}
	};
	
	// return and restart listener
	private ClickListener functionalButtonListener = new ClickListener() {
		public void clicked(InputEvent event, float x, float y) {
			if (event.getListenerActor() == buttons[0]) {
				stage.addAction(Actions.sequence(Actions.alpha(0.0f, 0.5f), Actions.run(new Runnable() {
					@Override
					public void run() {
						parentApp.screen = new MainScreen(parentApp);
						parentApp.screen.show();
					}
				})));
			} else if (event.getListenerActor() == buttons[1]) {
				stage.clear();
				JSONObject gameSetting;
				gameSetting = new JSONObject(Gdx.files.internal(GAME_SETTING_FILE_NAME).readString());
				initialize(gameSetting);
			}
		}
	};
	
	/* FUNC: updateMap(GameLogic) -> void
	 * DESC:
	 * 	update the grids according to the GameLogic
	 * ARG:
	 * 	logic -- current game state
	 */
	private void updateMap(GameLogic logic) {
		for (int i = 0; i < gridButtons.length; i++) {
			for (int j = 0; j < gridButtons[i].length; j++) {
				int gridType = logic.getGrid(i, j);
				gridButtons[i][j].getStyle().imageUp = gridBackground[gridType];
				switch (gridType) {
				case GameLogic.GRID_EMPTY:
					gridButtons[i][j].getColor().a = 0.45f;
					break;
					
				case GameLogic.GRID_PLACEABLE:
					gridButtons[i][j].addAction(
							Actions.sequence(Actions.alpha(0.1f, 1.0f), Actions.alpha(0.8f, 1.0f)));
					break;
					
				case GameLogic.GRID_FIRST_PLAYER:
				case GameLogic.GRID_SECOND_PLAYER:
					gridButtons[i][j].addAction(Actions.alpha(1.0f, 1.0f));
					break;
					
				}
			}
		}
	}
	
	/* FUNC: setupGridDrawables(JSONArray) -> void
	 * DESC:
	 * 	setup the gridBackgrund Drawables
	 * ARG:
	 * 	drawableObj -- the setting JSON node
	 */
	private void setupGridDrawables(JSONArray drawableObj) {
		gridBackground = new Drawable[drawableObj.length()];
		for (int i = 1; i < drawableObj.length(); i++) {
			JSONObject tmp = drawableObj.getJSONObject(i);
			Texture tex = new Texture(Gdx.files.internal(tmp.getString("image")));
			gridBackground[i] = new SpriteDrawable(new Sprite(tex));
		}
	}
	
	/* FUNC: setupGrids(JSONObject, Drawable, Group) -> void
	 * DESC:
	 * 	setup the grids
	 * ARG:
	 * 	setting 			-- the setting JSON node
	 *  defaultBackground 	-- the default background
	 *  group 				-- the group to be put into
	 */
	private void setupGrids(JSONObject setting, Drawable defaultBackground, Group group, int xLen, int yLen) {
		float buttonSizeX = (float) setting.getJSONObject("full_length").getDouble("x") / xLen;
		float buttonSizeY = (float) setting.getJSONObject("full_length").getDouble("y") / yLen;
		
		float startingX = (float) setting.getJSONObject("start_pos").getDouble("x");
		float startingY = (float) setting.getJSONObject("start_pos").getDouble("y");
		
		gameLogic = new GameLogic(xLen, yLen, parentApp.winningLength);
		gridButtons = new ImageButton[xLen][yLen];
		
		map = new HashMap<Actor, Integer>();
		for (int i = 0; i < xLen; i++) {
			for (int j = 0; j < yLen; j++) {
				gridButtons[i][j] = new ImageButton(defaultBackground);
				gridButtons[i][j].setPosition(
						startingX + i * buttonSizeX, 
						startingY + j * buttonSizeY);
				gridButtons[i][j].setSize(buttonSizeX, buttonSizeY);
				gridButtons[i][j].addListener(buttonListener);
				group.addActor(gridButtons[i][j]);
				int tmp = (i << 16) | j;
				map.put(gridButtons[i][j], tmp);
			}
		}
	}
	
	/* FUNC: initialize(JSONObject) -> void
	 * DESC:
	 * 	initialize the whole menu screen and its UI controls
	 * ARG:
	 * 	setting -- the JSON node holding the settings (see Menu.json for example)
	 */
	private void initialize(JSONObject setting) {
		gameGroup = new Group();
		winningGroup = new Group();
		buttonGroup = new Group();
		gameGroup.setBounds(0, 0, screenWidth, screenHeight);
		winningGroup.setBounds(0, 0, screenWidth, screenHeight);
		gameGroup.addActor(LibGdxUtility.makeBackground(setting.getString("normal_background"), screenWidth, screenHeight));
		winningGroup.addActor(LibGdxUtility.makeBackground(setting.getString("winning_background"), screenWidth, screenHeight));
		
		winner = -1;
		
		gameGroup.getColor().a = 0.0f;
		gameGroup.addAction(Actions.alpha(1.0f, 1.0f));
		
		FileHandle fontFile = Gdx.files.internal(setting.getJSONObject("font").getString("font_file"));
		FileHandle imageFile = Gdx.files.internal(setting.getJSONObject("font").getString("image_file"));
		gameFont = new BitmapFont(fontFile, imageFile, false);
		sfxClickedSound = Gdx.audio.newSound(Gdx.files.internal("button_click.wav"));
		
		setupGridDrawables(setting.getJSONArray("drawables"));
		setupGrids(setting, gridBackground[0], gameGroup, parentApp.sizeX, parentApp.sizeY);
		LibGdxUtility.setupLabels(setting.getJSONObject("labels").getJSONArray("normal"), gameFont, gameGroup, screenWidth, screenHeight);
		winningLabels = LibGdxUtility.setupLabels(setting.getJSONObject("labels").getJSONArray("winning"), gameFont, winningGroup, screenWidth, screenHeight);
		buttons = LibGdxUtility.setupButtons(setting.getJSONArray("functional_buttons"), buttonGroup, screenWidth, screenHeight, functionalButtonListener);
		
		gameLogic.markPlaceable();
		updateMap(gameLogic);

		stage.addActor(gameGroup);
		stage.addActor(buttonGroup);
	}
	
	/*
	 * FUNC: Constructor(ProjectApplication)
	 * DESC:
	 * 	Constructor for the GameScreen
	 * ARGS:
	 * 	parent -- parent window's pointer
	 */
	public GameScreen(ConnectNApplication parent) {
		spriteBatch = new SpriteBatch();
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		stage = new Stage(screenWidth, screenHeight, false, spriteBatch);
		parentApp = parent;
		
		JSONObject gameSetting = new JSONObject(Gdx.files.internal(GAME_SETTING_FILE_NAME).readString());
		initialize(gameSetting);
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
