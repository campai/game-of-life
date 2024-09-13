package mna.gol.graphic;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import mna.gol.entity.CellState;
import mna.gol.entity.GameBoard;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.*;

@RequiredArgsConstructor
@SuppressFBWarnings(value = {"EI_EXPOSE_REP2"}, justification = "'uiPanelCanvas' is by design mutable.")
public class SwingRenderer implements Renderer {
    private static final Color CANVAS_BACKGROUND_COLOR = Color.BLACK;
    private static final Color LIVE_CELL_COLOR = Color.GREEN;
    private static final Color DEAD_CELL_COLOR = Color.DARK_GRAY;

    private static final int EDGES_PADDING = 10;

    private final JComponent uiPanelCanvas;
    private final Font font = new Font("Arial", Font.PLAIN, 12);

    private BufferedImage screenImage;
    private Graphics2D imageCanvas;
    private FontMetrics fontMetrics;

    @Override
    public void render(GameBoard board) {
        var canvasWidth = uiPanelCanvas.getWidth();
        var canvasHeight = uiPanelCanvas.getHeight();

        if (screenImage == null || screenImage.getWidth() != canvasWidth || screenImage.getHeight() != canvasHeight) {
            screenImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);

            imageCanvas = screenImage.createGraphics();
            imageCanvas.setBackground(CANVAS_BACKGROUND_COLOR);
            imageCanvas.setFont(font);

            fontMetrics = imageCanvas.getFontMetrics();
        }

        var xScale = (double) (canvasWidth - EDGES_PADDING * 2) / board.getWidth();
        var yScale = (double) (canvasHeight - EDGES_PADDING * 2) / board.getHeight();

        var liveObjects = 0;
        for (var x = 0; x < board.getWidth(); x++) {
            for (var y = 0; y < board.getHeight(); y++) {
                var isAlive = board.getCellState(x, y) == CellState.LIVE;
                liveObjects += isAlive ? 1 : 0;

                imageCanvas.setColor(isAlive ? LIVE_CELL_COLOR : DEAD_CELL_COLOR);

                var xCanvas = EDGES_PADDING + (x * xScale);
                var yCanvas = EDGES_PADDING + (y * yScale);
                imageCanvas.draw(new Ellipse2D.Double(xCanvas, yCanvas, 1, 1));
            }
        }

        drawStatistics(liveObjects, board, canvasHeight);

        uiPanelCanvas.getGraphics()
            .drawImage(screenImage, 0, 0, null);
    }

    private void drawStatistics(int liveObjects, GameBoard board, int canvasHeight) {
        var stats = "Live: %06d   Dead: %06d   |   Click <SPACE> to reset.".formatted(
            liveObjects,
            (board.getWidth() * board.getHeight()) - liveObjects
        );
        var statsWidth = fontMetrics.stringWidth(stats);

        imageCanvas.setColor(Color.WHITE);
        imageCanvas.clearRect(0, canvasHeight - fontMetrics.getHeight(), statsWidth, fontMetrics.getHeight());
        imageCanvas.drawString(stats, 0, canvasHeight);
    }
}
