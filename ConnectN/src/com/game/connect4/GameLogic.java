package com.game.connect4;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the Game Logic class,
 * the high level logic and game state
 * 
 * @author Yufan Lu
 */
public class GameLogic {
	// Constants
	public final static int GRID_EMPTY = 0;
	public final static int GRID_FIRST_PLAYER = 1;
	public final static int GRID_SECOND_PLAYER = 2;
	public final static int GRID_PLACEABLE = 3;
	private final int winningSize;
	private static final int[][] DIRECTION = { 
		{ -1, +1 }, { 0, +1 }, { +1, +1 },
		{ -1,  0 },            { +1,  0 },
		{ -1, -1 }, { 0, -1 }, { +1, -1 }
	};
	
	// Game related variables
	private boolean isFirstPlayer;
	private int[][] grids;

	// Used to keep the four line track
	private List<MyPoint<Integer> > winningTrack;
	
	public List<MyPoint<Integer> > GetWinningTrack() {
		return winningTrack;
	}
	
	public int getGrid(int x, int y) {
		return grids[x][y];
	}

	/*
	 * FUNC: Constructor(int, int)
	 * DESC:
	 * 	Constructor for the GameLogic
	 * ARGS:
	 * 	xLen 				-- x size
	 * 	yLen 				-- y size
	 * 	winningRequiredSize -- the winning condition
	 */
	public GameLogic(int xLen, int yLen, int winningRequiredSize) {
		winningSize = winningRequiredSize;
		isFirstPlayer = true;
		winningTrack = new ArrayList<MyPoint<Integer> >();
		grids = new int[xLen][yLen];
		for (int i = 0; i < xLen; i++) {
			for (int j = 0; j < yLen; j++) {
				grids[i][j] = GRID_EMPTY;
			}
		}
	}
	
	/* FUNC: markPlaceable() -> void
	 * DESC:
	 * 	mark the grids that can be placed with next unit
	 */
	public void markPlaceable() {
		for (int i = 0; i < grids.length; i++) {
			for (int j = 0; j < grids[i].length; j++) {
				if (grids[i][j] == GRID_EMPTY) {
					grids[i][j] = GRID_PLACEABLE;
					break;
				} else if (grids[i][j] == GRID_PLACEABLE) {
					break;
				}
			}
		}
	}

	/* FUNC: clickGrid(int, int) -> boolean
	 * DESC:
	 * 	test if current grid is clickable, then update the game state
	 * ARG:
	 * 	xIndex -- x index
	 * 	yIndex -- y index
	 * RET:
	 * 	whether current grid is clickable
	 */
	public boolean clickGrid(int xIndex, int yIndex) {
		if (grids[xIndex][yIndex] == GRID_PLACEABLE) {
			grids[xIndex][yIndex] = isFirstPlayer ? GRID_FIRST_PLAYER : GRID_SECOND_PLAYER;
			isFirstPlayer = !isFirstPlayer;
			markPlaceable();
			return true;
		}
		return false;
	}

	/* FUNC: checkEightDirection(int, int) -> boolean
	 * DESC:
	 * 	start from current xy grid, check if there's a four same continuous grids,
	 * 	if so, store the path into the winningTrack variable
	 * ARG:
	 * 	xIndex -- x index
	 * 	yIndex -- y index
	 * RET:
	 * 	whether there's a four same continuous grids
	 */
	private boolean checkEightDirection(int xIndex, int yIndex) {
		int startGridMark = grids[xIndex][yIndex];
		if (startGridMark != GRID_FIRST_PLAYER && startGridMark != GRID_SECOND_PLAYER) {
			return false;
		}
		MyPoint<Integer> p;
		for (int i = 0; i < 8; i++) {
			int count = 1;
			winningTrack.clear();
			p = new MyPoint<Integer>();
			p.posX = xIndex; p.posY = yIndex;
			winningTrack.add(p);
			for (int j = 1; j < winningSize; j++) {
				int newX = xIndex + DIRECTION[i][0] * j;
				int newY = yIndex + DIRECTION[i][1] * j;
				if (!(newX >= 0 && newX < grids.length) ||
					!(newY >= 0 && newY < grids[newX].length) ||
					grids[newX][newY] != startGridMark) {
					break;
				}
				p = new MyPoint<Integer>();
				p.posX = newX; p.posY = newY;
				winningTrack.add(p);
				count++;
			}
			if (count == winningSize) {
				return true;
			}
		}
		return false;
	}

	/* FUNC: winner() -> int
	 * DESC:
	 * 	check the winner
	 * RET:
	 * 	return winner's index, if no winner return -1
	 */
	public int winner() {
		for (int i = 0; i < grids.length; i++) {
			for (int j = 0; j < grids[i].length; j++) {
				if (checkEightDirection(i, j)) {
					return grids[i][j];
				}
			}
		}
		return -1;
	}
}
