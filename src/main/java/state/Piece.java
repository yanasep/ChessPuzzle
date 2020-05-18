package state;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Piece holds pieceType and observable current position.
 * This object calculates next possible moves of the type
 * relative to the current position.
 */
public final class Piece {

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
     * Returns the piece's next movable positions.
     *
     * @return Next movable positions
     */
    public List<Position> getNextMoves() {

        if (position.get() == null) return null;

        return type.getMoves().stream().map(position.get()::movedBy).collect(Collectors.toList());
    }

    /**
     * Gets type of piece.
     * @return piece type
     */
    public PieceType getType() {
        return type;
    }

    /**
     * Gets current position of piece.
     * @return current position
     */
    public Position getPosition() {
        return position.get();
    }

    /**
     * Sets the position of pieces.
     * @param position destination
     */
    public void setPosition(Position position) {
        this.position.set(position);
    }

    /**
     * Gets the position property.
     * @return position property
     */
    public ObjectProperty<Position> positionProperty() {
        return position;
    }
}
