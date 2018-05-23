package main.mechanics.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class Field {
    private static final Integer FIELD_SIZE = 10;
    private List<List<CellStatus>> field = new ArrayList<>();

    public Field() {
        for (int i = 0; i < FIELD_SIZE; i++) {
            final List<CellStatus> line = new ArrayList<>(Collections.nCopies(FIELD_SIZE, CellStatus.FREE));
            field.add(line);
        }
    }

    public Field(List<Ship> ships) {
        for (int i = 0; i < FIELD_SIZE; i++) {
            final List<CellStatus> line = new ArrayList<>(Collections.nCopies(FIELD_SIZE, CellStatus.FREE));
            field.add(line);
        }
        ships.forEach(ship -> ship.getCells().forEach(cell -> setCellStatus(cell, CellStatus.OCCUPIED)));
    }

    public void setCellStatus(Cell cell, CellStatus status) {
        if (!checkCellBounds(cell)) {
            return;
        }
        field.get(cell.getRowPos()).set(cell.getColPos(), status);
    }

    public Boolean checkCellBounds(Cell cell) {
        final Boolean checkRow = (cell.getRowPos() >= 0 && cell.getRowPos() < FIELD_SIZE);
        final Boolean checkCol = (cell.getColPos() >= 0 && cell.getColPos() < FIELD_SIZE);
        return checkCol && checkRow;
    }
}
