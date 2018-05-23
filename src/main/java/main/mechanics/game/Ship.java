package main.mechanics.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class Ship {
    private Integer rowPos;
    private Integer colPos;
    private Integer length;
    private Boolean isVertical;

    @JsonCreator
    public Ship(@JsonProperty("rowPos") Integer rowPos,
                @JsonProperty("colPos") Integer colPos,
                @JsonProperty("length") Integer length,
                @JsonProperty("isVertical") Boolean isVertical) {
        this.rowPos = rowPos;
        this.colPos = colPos;
        this.length = length;
        this.isVertical = isVertical;
    }

    public Integer getRowPos() {
        return rowPos;
    }

    public void setRowPos(Integer rowPos) {
        this.rowPos = rowPos;
    }

    public Integer getColPos() {
        return colPos;
    }

    public void setColPos(Integer colPos) {
        this.colPos = colPos;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Boolean getVertical() {
        return isVertical;
    }

    public void setVertical(Boolean vertical) {
        isVertical = vertical;
    }

    public ArrayList<Cell> getCells() {
        final ArrayList<Cell> res = new ArrayList<>();

        if (isVertical.equals(Boolean.TRUE)) {
            for (Integer i = 0; i < length; i++) {
                res.add(new Cell(rowPos + i, colPos));
            }
        } else {
            for (Integer i = 0; i < length; i++) {
                res.add(new Cell(rowPos + i, colPos));
            }
        }
        return res;
    }

    public Boolean isInShip(Cell cell) {
        if (isVertical.equals(Boolean.TRUE)) {
            return cell.getColPos().equals(colPos) && (cell.getRowPos() < (rowPos + length));
        }
        return cell.getRowPos().equals(rowPos) && (cell.getColPos() < (colPos + length));
    }

    // !!! дописать
    public ArrayList<Cell> getCellsArounShip() {
        final ArrayList<Cell> res = new ArrayList<>();
        return res;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final Ship ship = (Ship) object;

        if (rowPos != null) {
            if (!rowPos.equals(ship.rowPos)) {
                return false;
            }
        } else {
            if (ship.rowPos != null) {
                return false;
            }
        }
        if (colPos != null) {
            if (!colPos.equals(ship.colPos)) {
                return false;
            }
        } else {
            if (ship.colPos != null) {
                return false;
            }
        }
        if (length != null) {
            if (!length.equals(ship.length)) {
                return false;
            }
        } else {
            if (ship.length != null) {
                return false;
            }
        }

        if (isVertical != null) {
            return isVertical.equals(ship.isVertical);
        } else {
            return ship.isVertical == null;
        }
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (rowPos != null) {
            result = rowPos.hashCode();
        }
        final int sizeInt = 31;
        if (colPos != null) {
            result = sizeInt * result + colPos.hashCode();
        } else {
            result = sizeInt * result;
        }
        if (length != null) {
            result = sizeInt * result + length.hashCode();
        } else {
            result = sizeInt * result;
        }
        if (isVertical != null) {
            result = sizeInt * result + isVertical.hashCode();
        } else {
            result = sizeInt * result;
        }

        return result;

    }
}
