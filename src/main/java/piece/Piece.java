package piece;

import board.Board;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import util.Position;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Piece holds pieceType and observable current position.
 * This object calculates next possible moves of the type
 * relative to the current position.
 */
public class Piece {

    private final ObjectProperty<Position> position = new SimpleObjectProperty<>();
    private final PieceType type;

    /**
     * Creates a new piece of the specified type and position.
     * @param type Type of piece
     * @param row Row index of the board
     * @param col Column index of the board
     */
    public Piece(PieceType type, int row, int col) {
        this.position.set(new Position(row, col));
        this.type = type;
    }

    /**
     * Returns the piece's next movable positions by applying steps to the current position.
     *
     * @return Next movable positions
     */
    public List<Position> getNextMoves() {

        if (position.get() == null) return null;

        Predicate<Position> isOnBoard = p ->
                p.getRow() >= 0 && p.getRow() < Board.ROW_SIZE && p.getCol() >= 0 && p.getCol() < Board.COL_SIZE;

        return type.getMoves().stream().map(m -> position.get().movedBy(m)).filter(isOnBoard).collect(Collectors.toList());
    }

    public PieceType getType() {
        return type;
    }

    public Position getPosition() {
        return position.get();
    }

    public void setPosition(Position position) {
        this.position.set(position);
    }

    public ObjectProperty<Position> positionProperty() {
        return position;
    }

}
