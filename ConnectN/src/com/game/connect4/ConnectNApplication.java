package com.game.connect4;

import java.io.IOException;

import org.json.JSONException;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.GL20;

/**
 * The {@link ApplicationListener} for this project, create(), resize() and
 * render() are the only methods that are relevant
 * 
 * @author Yufan Lu
 * */
public class ConnectNApplication implements ApplicationListener {

	Screen screen;
	private Sound backgroundMusic;
	private boolean isMusicOn = false;
	
	int sizeX;
	int sizeY;
	int winningLength;

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: java -jar Connect4.jar [GridSizeX] [GridSizeY] [WinningLength]");
			return;
		}
		new LwjglApplication(new ConnectNApplication(
				Integer.parseInt(args[0]), 
				Integer.parseInt(args[1]), 
				Integer.parseInt(args[2])), "Connect N(4)", 1280, 720, true);
	}
	
	public ConnectNApplication(int gridSizeX, int gridSizeY, int winningLen) {
		sizeX = gridSizeX;
		sizeY = gridSizeY;
		winningLength = winningLen;
	}
	
	public void turnBackgroundMusic() {
		if (isMusicOn) {
			backgroundMusic.stop();
			isMusicOn = false;
		} else {
			backgroundMusic.loop();
			isMusicOn = true;
		}
	}

	@Override
	public void create() {
		screen = new MainScreen(this);
		backgroundMusic = Gdx.audio.newSound(Gdx.files.internal("Kiss_the_Rain.mp3"));
		screen.show();
	}

	@Override
	public void dispose() {
		screen.hide();
		screen.dispose();
	}

	@Override
	public void pause() {
		screen.pause();
	}

	@Override
	public void render() {
		clearWhite();
		screen.render(Gdx.graphics.getDeltaTime());
	}

	/** Clears the screen with a white color */
	private void clearWhite() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void resize(int width, int height) {
		screen.resize(width, height);
	}

	@Override
	public void resume() {
		screen.resume();
	}
}
