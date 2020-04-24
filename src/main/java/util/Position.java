package util;

/**
 * An immutable object to represent a position in two dimensional array.
 */
public class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Position(Position position) {
        this.row = position.row;
        this.col = position.col;
    }

    /**
     * Returns the position of current moved by specified offset.
     * @param offset Offset from the current position
     * @return Calculated new position
     */
    public Position movedBy(Position offset) {
        return new Position(row + offset.row, col + offset.col);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    /**
     * Returns true if two positions have the same row and the same col.
     * @param obj Object to be compared
     * @return True if equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Position))
            return false;
        var p = (Position) obj;
        return row == p.row && col == p.col;
    }
}
