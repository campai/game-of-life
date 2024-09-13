package mna.gol.ui;

import lombok.RequiredArgsConstructor;
import mna.gol.entity.CellState;
import mna.gol.entity.GameBoard;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.*;

@RequiredArgsConstructor
public class SwingRenderer implements Renderer {
    private static final Color CANVAS_BACKGROUND_COLOR = Color.BLACK;
    private static final Color LIVE_CELL_COLOR = Color.GREEN;
    private static final Color DEAD_CELL_COLOR = Color.DARK_GRAY;

    private static final int EDGES_PADDING = 10;

    private final JComponent canvas;

    @Override
    public void render(GameBoard board) {
        var canvasWidth = canvas.getWidth();
        var canvasHeight = canvas.getHeight();

        var bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);

        var image = bufferedImage.createGraphics();
        image.setBackground(CANVAS_BACKGROUND_COLOR);
        image.clearRect(0, 0, canvasWidth, canvasHeight);

        var xScale = (double) (canvasWidth - EDGES_PADDING * 2) / board.getWidth();
        var yScale = (double) (canvasHeight - EDGES_PADDING * 2) / board.getHeight();

        var liveObjects = 0;
        for (var x = 0; x < board.getWidth(); x++) {
            for (var y = 0; y < board.getHeight(); y++) {
                var isAlive = board.getCellState(x, y) == CellState.LIVE;
                liveObjects += isAlive ? 1 : 0;

                image.setColor(isAlive ? LIVE_CELL_COLOR : DEAD_CELL_COLOR);

                var xCanvas = EDGES_PADDING + (x * xScale);
                var yCanvas = EDGES_PADDING + (y * yScale);
                image.draw(new Ellipse2D.Double(xCanvas, yCanvas, 1, 1));
            }
        }

        var stats = "Live: %06d   Dead: %06d   |   Click <SPACE> to reset.".formatted(liveObjects, (canvasWidth * canvasHeight) - liveObjects);
        image.setColor(Color.WHITE);
        image.drawString(stats, 0, canvasHeight);

        canvas.getGraphics()
            .drawImage(bufferedImage, 0, 0, null);
    }
}
