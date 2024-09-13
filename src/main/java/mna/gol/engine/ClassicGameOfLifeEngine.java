package mna.gol.engine;

import static mna.gol.entity.CellState.DEAD;
import static mna.gol.entity.CellState.LIVE;

import lombok.extern.slf4j.Slf4j;
import mna.gol.entity.Board;
import mna.gol.entity.CellState;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class ClassicGameOfLifeEngine implements RulesEngine {
    @Override
    public void seedLife(Board board, int initLifeNumber) {
        var randGen = ThreadLocalRandom.current();
        var seededLife = initLifeNumber;

        while (seededLife > 0) {
            var xCoordinate = randGen.nextInt(board.getWidth());
            var yCoordinate = randGen.nextInt(board.getHeight());

            if (!board.isCellAlive(xCoordinate, yCoordinate)) {
                board.setCellState(xCoordinate, yCoordinate, CellState.LIVE);
                seededLife--;
            }
        }
    }

    @Override
    public void calculateNextGeneration(Board board) {
        var neighbors = 0;
        for (var x = 0; x < board.getHeight(); x++) {
            for (var y = 0; y < board.getWidth(); y++) {
                neighbors = countNeighbors(board, x, y);

                board.setCellState(x, y, calculateNextGenCellState(board, x, y, neighbors));
            }
        }
    }

    private CellState calculateNextGenCellState(Board board, int x, int y, int neighbors) {
        return switch (board.getCellState(x, y)) {
            case DEAD -> neighbors == 3 ? LIVE : DEAD;
            case LIVE -> {
                if (neighbors < 2 || neighbors > 3) {
                    yield DEAD;
                } else {
                    yield LIVE;
                }
            }
        };
    }

    private int countNeighbors(Board board, int x, int y) {
        var width = board.getWidth();
        var height = board.getHeight();

        // @formatter:off
        int[][] neighborsRelativePositions = {
            {-1, -1}, { 0, -1}, {1, -1},
            {-1,  0},           {1,  0},
            {-1,  1}, { 0,  1}, {1,  1}
        };
        // @formatter:on

        var neighbors = 0;
        for (var neighborPosition : neighborsRelativePositions) {
            int neighborX = x + neighborPosition[0];
            int neighborY = y + neighborPosition[1];

            if (neighborX >= 0 && neighborX < width && neighborY >= 0 && neighborY < height) {
                neighbors += board.isCellAlive(neighborX, neighborY) ? 1 : 0;
            }
        }

        log.trace("[{}, {}] has neighbors: {}", x, y, neighbors);
        return neighbors;
    }
}
