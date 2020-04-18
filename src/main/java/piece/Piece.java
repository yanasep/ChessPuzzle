package piece;

import board.Board;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import util.Position;

import java.util.*;
import java.util.function.Predicate;

public abstract class Piece {

    private final ObjectProperty<Position> position;
    private List<Position> steps;

    protected Piece(int row, int col, Position[] steps) {
        this.position = new SimpleObjectProperty<>();
        this.position.set(new Position(row, col));
        this.steps = Arrays.asList(steps);
    }

    /**
     * Returns the piece's next movable positions by applying steps to the current position.
     * @return Next movable positions
     */
    public List<Position> getMovablePoints() {

        Predicate<Position> isOnBoard = p ->
                p.getRow() >= 0 && p.getRow() < Board.ROW_SIZE && p.getCol() >= 0 && p.getCol() < Board.COL_SIZE;

        List<Position> list = new ArrayList<>();

        for (var p : steps) {
            var newPos = position.get().movedBy(p);
            if (isOnBoard.test(newPos))
                list.add(newPos);
        }
        return list;
    }

    /**
     * Returns copy of the piece's current position.
     * @return Current position
     */
    public Position getPosition() {
        return new Position(position.get());
    }

    public void setPosition(Position position) { this.position.set(position); }

    public ObjectProperty<Position> positionProperty() {
        return position;
    }

}
