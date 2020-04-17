package board;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import piece.King;
import piece.Knight;
import piece.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Board {
    public static final int ROW_SIZE = 8;
    public static final int COL_SIZE = 8;
    public static final Piece.Position GOAL_POS = new Piece.Position(0, 6);

    private final Piece king;
    private final Piece knight;
    private final List<Piece.Position> nextPositions = new ArrayList<>();
    private final List<Consumer<List<Piece.Position>>> nextPositionsChangeListeners = new ArrayList<>();

    public enum PieceType {
        KING, KNIGHT
    }

    public enum State {
        RUNNING, OVER, GOAL
    }

    private State state;

    public Board() {
        king = new King(2, 1);
        knight = new Knight(2, 2);
        state = State.RUNNING;
    }

    public List<Piece.Position> getMovablePositions(PieceType type) {
        Piece self = (type == PieceType.KING) ? king : knight;
        Piece other = (type == PieceType.KING) ? knight : king;

        var positions = self.getMovablePoints();
        positions.remove(other.getPosition());
        return positions;
    }

    public void move(PieceType type, int row, int col) {
        var self = (type == PieceType.KING) ? king : knight;
        self.moveTo(row, col);
        if (self.getPosition().equals(GOAL_POS))
            state = State.GOAL;
        else if (!isMovable(PieceType.KING) && !isMovable(PieceType.KNIGHT))
            state = State.OVER;
    }

    public Piece.Position getPiecePosition(PieceType type) {
        Piece self = (type == PieceType.KING) ? king : knight;
        return self.getPosition();
    }

    public boolean isMovable(PieceType type) {
        var self = (type == PieceType.KING) ? king : knight;
        var other = (type == PieceType.KING) ? knight : king;
        return other.getMovablePoints().contains(self.getPosition());
    }

    public State getState() {
        return state;
    }

    public void addNextPositionsChangeListener(Consumer<List<Piece.Position>> consumer) {
        nextPositionsChangeListeners.add(consumer);
    }

}
