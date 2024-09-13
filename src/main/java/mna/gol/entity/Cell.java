package mna.gol.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
class Cell {
    private CellState state;

    boolean isAlive() {
        return state == CellState.LIVE;
    }
}
