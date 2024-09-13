package mna.gol.entity;

public interface GameBoard {
    int getWidth();

    int getHeight();

    CellState getCellState(int x, int y);

    void setCellState(int x, int y, CellState state);

    void reset();
}
