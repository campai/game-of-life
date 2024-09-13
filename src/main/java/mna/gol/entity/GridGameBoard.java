package mna.gol.entity;

import lombok.Getter;

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
}
