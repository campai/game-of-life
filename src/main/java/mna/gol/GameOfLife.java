package mna.gol;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serial;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;

@Slf4j
public class GameOfLife extends JPanel implements KeyListener {
    @Serial
    private static final long serialVersionUID = -2559666472917571856L;

    private static final byte LIVE = 1;
    private static final byte DEAD = 0;

    private static final int BOARD_WIDTH = 130;
    private static final int BOARD_HEIGHT = 130;
    private static final int FIRST_GENERATION_LIVE_CELLS = 1_000;

    private volatile boolean gameResetScheduled = false;
    private volatile boolean gameIsRunning = false;

    public static void main(String[] args) {
        var gameOfLife = new GameOfLife();
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

        var board = new byte[boardHeight][boardWidth];
        seedLife(board, initLiveCells);

        while (gameIsRunning) {
            if (this.gameResetScheduled) {
                resetGame(initLiveCells, board);
            }

            updateBoard(boardWidth, boardHeight, board);

            drawBoard(board);
            //noinspection BusyWait
            Thread.sleep(50);
        }

    }

    private void updateBoard(int width, int height, byte[][] board) {
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                board[x][y] = calculateNextGeneration(board[x][y], countNeighbors(board, x, y));
            }
        }
    }

    private byte calculateNextGeneration(byte cell, int neighbors) {
        return switch (cell) {
            case DEAD -> neighbors == 3 ? LIVE : DEAD;
            case LIVE -> {
                if (neighbors < 2 || neighbors > 3) {
                    yield DEAD;
                } else {
                    yield LIVE;
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + cell);
        };
    }

    private void resetGame(int initLifeNumber, byte[][] board) {
        Arrays.stream(board)
            .forEach(row -> Arrays.fill(row, (byte) 0));

        seedLife(board, initLifeNumber);
        this.gameResetScheduled = false;
    }

    private void seedLife(byte[][] board, int initLifeNumber) {
        var randGen = ThreadLocalRandom.current();
        var seededLife = initLifeNumber;

        while (seededLife > 0) {
            var xCoordinate = randGen.nextInt(board.length);
            var yCoordinate = randGen.nextInt(board[0].length);

            if (board[xCoordinate][yCoordinate] == DEAD) {
                board[xCoordinate][yCoordinate] = LIVE;
                seededLife--;
            }
        }

    }

    private void drawBoard(byte[][] board) {
        var bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        var canvas = bufferedImage.createGraphics();
        canvas.setBackground(Color.BLACK);
        canvas.clearRect(0, 0, getWidth(), getHeight());

        var xScale = Math.max((double) getWidth() / board.length, 1d);
        var yScale = Math.max((double) getHeight() / board[0].length, 1d);

        var padding = 10;
        var liveObjects = 0;
        for (var x = 0; x < board.length; x++) {
            for (int y = 0; y < board[0].length; y++) {
                liveObjects += board[x][y];

                canvas.setColor(board[x][y] == LIVE ? Color.GREEN : Color.DARK_GRAY);

                var xCanvas = padding + (x * xScale);
                var yCanvas = padding + (y * yScale);
                canvas.draw(new Rectangle2D.Double(xCanvas, yCanvas, 3, 3));
            }
        }

        var stats = "Live: %06d   Dead: %06d   |   Click <SPACE> to reset.".formatted(liveObjects, (board.length * board[0].length) - liveObjects);
        canvas.setColor(Color.WHITE);
        canvas.drawString(stats, 0, getHeight() - 3);

        log.trace(stats);
        this.getGraphics().drawImage(bufferedImage, 0, 0, null);
    }

    private int countNeighbors(byte[][] board, int x, int y) {
        var width = board.length;
        var height = board[0].length;

        // @formatter:off
        int[][] neighborsRelativePositions = {
            {-1, -1}, { 0, -1}, {1, -1},
            {-1,  0},           {1,  0},
            {-1,  1}, { 0,  1}, {1,  1}
        };
        // @formatter:on

        var neighbors = 0;
        for (var neighborPosition : neighborsRelativePositions) {
            int neighborX = x + neighborPosition[0];
            int neighborY = y + neighborPosition[1];

            if (neighborX >= 0 && neighborX < width && neighborY >= 0 && neighborY < height) {
                neighbors += board[neighborX][neighborY];
            }
        }

        log.trace("[{}, {}] has neighbors: {}", x, y, neighbors);
        return neighbors;
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
