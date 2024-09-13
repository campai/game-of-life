package mna.gol.engine;

import mna.gol.entity.GameBoard;

public interface RulesEngine {
    void seedLife(GameBoard board, int initLiveCells);

    void calculateNextGeneration(GameBoard board);
}
