package mna.gol.entity;

import lombok.Getter;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class GridGameBoard implements GameBoard {
    @Getter
    private final int width;
    @Getter
    private final int height;

    private final Cell[][] cells;

    public GridGameBoard(int width, int height) {
        this.width = width;
        this.height = height;

        this.cells = new Cell[height][width];

        initCells();
    }

    private void initCells() {
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                cells[x][y] = new Cell(CellState.DEAD);
            }
        }
    }

    @Override
    public CellState getCellState(int x, int y) {
        return cells[x][y].getState();
    }

    @Override
    public void setCellState(int x, int y, CellState state) {
        cells[x][y].setState(state);
    }

    @Override
    public void reset() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y].setState(CellState.DEAD);
            }
        }
    }

    @Override
    public void draw(Graphics2D graphics, int canvasWidth, int canvasHeight) {
        var bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);

        var canvas = bufferedImage.createGraphics();
        canvas.setBackground(Color.BLACK);
        canvas.clearRect(0, 0, canvasWidth, canvasHeight);

        var xScale = Math.max((double) canvasWidth / width, 1d);
        var yScale = Math.max((double) canvasHeight / height, 1d);

        var padding = 10;
        var liveObjects = 0;
        for (var x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                liveObjects += cells[x][y].isAlive() ? 1 : 0;

                canvas.setColor(cells[x][y].isAlive() ? Color.GREEN : Color.DARK_GRAY);

                var xCanvas = padding + (x * xScale);
                var yCanvas = padding + (y * yScale);
                canvas.draw(new Rectangle2D.Double(xCanvas, yCanvas, 3, 3));
            }
        }

        var stats = "Live: %06d   Dead: %06d   |   Click <SPACE> to reset.".formatted(liveObjects, (width * height) - liveObjects);
        canvas.setColor(Color.WHITE);
        canvas.drawString(stats, 0, canvasHeight - 3);

        graphics.drawImage(bufferedImage, 0, 0, null);
    }
}
