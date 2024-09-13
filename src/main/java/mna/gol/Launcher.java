package mna.gol;

import mna.gol.engine.ClassicGameOfLifeEngine;
import mna.gol.entity.GridGameBoard;
import mna.gol.ui.SwingUserInterface;

public class Launcher {
    private static final int BOARD_WIDTH = 200;
    private static final int BOARD_HEIGHT = 200;
    private static final int FIRST_GENERATION_LIVE_CELLS = 1_600;

    public static void main(String[] args) {
        var rulesEngine = new ClassicGameOfLifeEngine(FIRST_GENERATION_LIVE_CELLS);
        var gameBoard = new GridGameBoard(BOARD_WIDTH, BOARD_HEIGHT);

        var gameOfLife = new GameOfLife(gameBoard, rulesEngine);

        new SwingUserInterface(gameOfLife)
            .createUI(1400, 960);
    }
}
