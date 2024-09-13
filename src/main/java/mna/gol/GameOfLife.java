package mna.gol;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mna.gol.engine.RulesEngine;
import mna.gol.entity.GameBoard;
import mna.gol.graphic.Renderer;

/**
 * The universe of the Game of Life is an infinite two-dimensional orthogonal grid of square cells, each of which is in one of two possible states, alive or dead, or "populated" or "unpopulated". Every cell interacts with its eight neighbours, which are the cells that are horizontally, vertically, or diagonally adjacent. At each step in time, the following transitions occur:
 * <p>
 * 1. Any live cell with fewer than two live neighbours dies, as if caused by underpopulation.
 * <p>
 * 2. Any live cell with two or three live neighbours lives on to the next generation.
 * <p>
 * 3. Any live cell with more than three live neighbours dies, as if by overpopulation.
 * <p>
 * 4. Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
 * <p>
 * Source: <a href="https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">Wikipedia: Game Of Life</a>
 */
@Slf4j
@RequiredArgsConstructor
@SuppressFBWarnings(value = {"EI_EXPOSE_REP2"}, justification = "GameBoard and RulesEngine are intentionally mutable, by design.")
public class GameOfLife {
    @Getter
    private volatile boolean isRunning = false;
    private volatile boolean scheduledGameReset = false;

    private final GameBoard board;
    private final RulesEngine rulesEngine;

    public void startGame(Renderer renderer) throws InterruptedException {
        this.isRunning = true;
        rulesEngine.seedLife(board);

        while (isRunning) {
            if (this.scheduledGameReset) {
                resetGame(board);
            }

            rulesEngine.calculateNextGeneration(board);
            renderer.render(board);

            //noinspection BusyWait
            Thread.sleep(30);
        }

    }

    public void scheduleReset() {
        this.scheduledGameReset = true;
    }

    private void resetGame(GameBoard board) {
        board.reset();
        rulesEngine.seedLife(board);
        this.scheduledGameReset = false;
    }
}
