package piece;

import board.Board;

import java.util.*;
import java.util.function.Predicate;

public abstract class Piece {

    public static class Position {
        private int row;
        private int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public Position movedBy(Position offset) {
            return new Position(row + offset.row, col + offset.col);
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Position))
                return false;
            var p = (Position) obj;
            return row == p.row && col == p.col;
        }
    }

    private Position position;
    private List<Position> steps;
    private List<Position> nextPositions;

    protected Piece(int row, int col, Position[] steps) {
        this.position = new Position(row, col);
        this.steps = Arrays.asList(steps);
        this.nextPositions = new ArrayList<>();
        updateNextPositions();
    }

    /**
     * Move the piece to the specified position.
     * @param row Row index of board
     * @param col Column index of board
     */
    public void moveTo(int row, int col) {
        position.row = row;
        position.col = col;
        updateNextPositions();
    }

    /**
     * Returns the piece's next movable positions as readonly.
     * @return Next movable positions
     */
    public List<Position> getMovablePoints() {
        return nextPositions;
    }

    /**
     * Updates the next movable positions of the piece by applying steps.
     */
    private void updateNextPositions() {

        Predicate<Position> isOnBoard = p ->
            p.row >= 0 && p.row < Board.ROW_SIZE && p.col >= 0 && p.col < Board.COL_SIZE;

        nextPositions.clear();

        for (var p : steps) {
            var newPos = position.movedBy(p);
            if (isOnBoard.test(newPos))
                nextPositions.add(newPos);
        }
    }

    /**
     * Returns copy of the piece's current position.
     * @return Current position of the piece
     */
    public Position getPosition() {
        return new Position(position.row, position.col);
    }
}
