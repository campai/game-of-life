package mna.gol.entity;

import java.awt.*;

public interface GameBoard {
    int getWidth();

    int getHeight();

    CellState getCellState(int x, int y);

    void setCellState(int x, int y, CellState state);

    void reset();

    void render(Graphics2D graphics, int width, int height);
}
