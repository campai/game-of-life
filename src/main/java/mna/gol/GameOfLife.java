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
    private static final long serialVersionUID = -1234L;

    private static final byte LIVE = 1;
    private static final byte DEAD = 0;

    private static final int WIDTH = 130;
    private static final int HEIGHT = 130;
    private static final int INIT_LIFE_AMOUNT = 1_000;

    private volatile boolean gameResetScheduled = false;
    private volatile boolean gameIsRunning = false;

    public static void main(String[] args) {
        var gameOfLife = new GameOfLife();

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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        var g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!gameIsRunning) {
            new Thread(() -> {
                try {
                    gameIsRunning = true;
                    run(WIDTH, HEIGHT, INIT_LIFE_AMOUNT);
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void run(int width, int height, int initLifeNumber) throws InterruptedException, IOException {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Both width and height of the board should be bigger than 0!");
        }

        var board = new byte[height][width];
        seedLife(board, initLifeNumber);

        while (gameIsRunning) {
            if (this.gameResetScheduled) {
                resetGame(initLifeNumber, board);
            }

            updateBoard(width, height, board);

            drawBoard(board);
            //noinspection BusyWait
            Thread.sleep(50);
        }

    }

    private void updateBoard(int width, int height, byte[][] board) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                board[i][j] = calculateLife(board[i][j], countNeighbors(board, i, j));
            }
        }
    }

    private void resetGame(int initLifeNumber, byte[][] board) {
        Arrays.stream(board)
            .forEach(row -> Arrays.fill(row, (byte) 0));
        seedLife(board, initLifeNumber);
        this.gameResetScheduled = false;
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

        this.getGraphics().drawImage(bufferedImage, 0, 0, null);
    }

    private byte calculateLife(byte cell, byte neighbors) {
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

    private byte countNeighbors(byte[][] board, int x, int y) {
        var neighbors = (byte) 0;
        var height = board[0].length;
        var width = board.length;

        if (x - 1 >= 0) {
            neighbors += board[x - 1][y];
        }

        if (x + 1 < board.length) {
            neighbors += board[x + 1][y];
        }

        if (y - 1 >= 0) {
            neighbors += board[x][y - 1];
        }

        if (y + 1 < height) {
            neighbors += board[x][y + 1];
        }

        if (x - 1 >= 0 && y - 1 >= 0) {
            neighbors += board[x - 1][y - 1];
        }

        if (x - 1 >= 0 && y + 1 < height) {
            neighbors += board[x - 1][y + 1];
        }

        if (x + 1 < width && y - 1 >= 0) {
            neighbors += board[x + 1][y - 1];
        }

        if (x + 1 < width && y + 1 < height) {
            neighbors += board[x + 1][y + 1];
        }

        log.trace("[{}, {}]: {}", x, y, neighbors);
        return neighbors;
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

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            gameResetScheduled = true;
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //intentionally left empty
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //intentionally left empty
    }
}
