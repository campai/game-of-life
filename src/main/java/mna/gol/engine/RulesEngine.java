package mna.gol.engine;

import mna.gol.entity.GameBoard;

public interface RulesEngine {
    void seedLife(GameBoard board);

    void calculateNextGeneration(GameBoard board);
}
