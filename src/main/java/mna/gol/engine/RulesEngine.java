package mna.gol.engine;

import mna.gol.entity.Board;

public interface RulesEngine {
    void seedLife(Board board, int initLiveCells);

    void calculateNextGeneration(Board board);
}
