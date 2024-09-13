package mna.gol;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mna.gol.engine.ClassicGameOfLifeEngine;
import mna.gol.engine.RulesEngine;
import mna.gol.entity.GameBoard;
import mna.gol.entity.GridGameBoard;
import mna.gol.ui.SwingRenderer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.Serial;
import javax.swing.*;

/**
 * The universe of the Game of Life is an infinite two-dimensional orthogonal grid of square cells, each of which is in one of two possible states, alive or dead, or "populated" or "unpopulated". Every cell interacts with its eight neighbours, which are the cells that are horizontally, vertically, or diagonally adjacent. At each step in time, the following transitions occur:
 * <p>
 * 1. Any live cell with fewer than two live neighbours dies, as if caused by underpopulation.
 * <p>
 * 2. Any live cell with two or three live neighbours lives on to the next generation.
 * <p>
 * 3. Any live cell with more than three live neighbours dies, as if by overpopulation.
 * <p>
 * 4. Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
 * <p>
 * Source: <a href="https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">Wikipedia: Game Of Life</a>
 */
@Slf4j
@RequiredArgsConstructor
@SuppressFBWarnings(value = {"EI_EXPOSE_REP2"}, justification = "GameBoard and RulesEngine are intentionally mutable, by design.")
public class GameOfLife extends JPanel implements KeyListener {
    @Serial
    private static final long serialVersionUID = -2559666472917571856L;

    private static final int BOARD_WIDTH = 200;
    private static final int BOARD_HEIGHT = 200;
    private static final int FIRST_GENERATION_LIVE_CELLS = 1_600;

    private volatile boolean gameResetScheduled = false;
    private volatile boolean gameIsRunning = false;

    private final transient GameBoard board;
    private final transient RulesEngine rulesEngine;

    public static void main(String[] args) {
        var gameOfLife = new GameOfLife(new GridGameBoard(BOARD_WIDTH, BOARD_HEIGHT), new ClassicGameOfLifeEngine());
        createGameUI(gameOfLife);
    }

    private static void createGameUI(GameOfLife gameOfLife) {
        var frame = new JFrame("Game of Life");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.add(gameOfLife);
        frame.setSize(1400, 960);
        frame.addKeyListener(gameOfLife);
        frame.setFocusable(true);
        frame.requestFocus();

        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!gameIsRunning) {
            new Thread(() -> {
                try {
                    gameIsRunning = true;
                    startGame(BOARD_WIDTH, BOARD_HEIGHT, FIRST_GENERATION_LIVE_CELLS);
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void startGame(int boardWidth, int boardHeight, int initLiveCells) throws InterruptedException, IOException {
        if (boardWidth < 1 || boardHeight < 1) {
            throw new IllegalArgumentException("Both boardWidth and boardHeight of the board should be bigger than 0!");
        }

        rulesEngine.seedLife(board, initLiveCells);
        var renderer = new SwingRenderer(this);

        while (gameIsRunning) {
            if (this.gameResetScheduled) {
                resetGame(board, initLiveCells);
            }

            rulesEngine.calculateNextGeneration(board);
            renderer.render(board);

            //noinspection BusyWait
            Thread.sleep(50);
        }

    }

    private void resetGame(GameBoard board, int initLifeNumber) {
        board.reset();
        rulesEngine.seedLife(board, initLifeNumber);
        this.gameResetScheduled = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            gameResetScheduled = true;
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // intentionally left empty
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // intentionally left empty
    }
}
