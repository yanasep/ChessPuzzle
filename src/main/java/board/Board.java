package board;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import piece.King;
import piece.Knight;
import piece.Piece;
import util.Position;
import java.util.List;

public class Board {
    public static final int ROW_SIZE = 8;
    public static final int COL_SIZE = 8;
    public static final Position GOAL_POS = new Position(0, 6);
    public static final Position KING_INIT_POS = new Position(2, 1);
    public static final Position KNIGHT_INIT_POS = new Position(2, 2);

    private final Piece king;
    private final Piece knight;
    private final List<Piece> pieceList;
    private final ObjectProperty<Piece> selectedPiece = new SimpleObjectProperty<>();
    private final ObjectProperty<List<Position>> nextPositions = new SimpleObjectProperty<>();
    private final StringProperty message = new SimpleStringProperty();

    public enum State {
        RUNNING, OVER, GOAL
    }

    private State state;

    public Board() {
        king = new King(2, 1);
        knight = new Knight(2, 2);
        state = State.RUNNING;
        pieceList = List.of(king, knight);
        nextPositions.set(List.of());
        selectedPiece.addListener((obs, oldVal, newVal) -> {
            if (newVal == null) message.set("");
            else message.set(newVal.getClass().getSimpleName() + " selected");
        });
        selectedPiece.addListener((observable, oldVal, newVal) -> updateNextPositions());
        message.set("Start!");
    }

    public void reset() {
        king.setPosition(new Position(2,1));
        knight.setPosition(new Position(2,2));
        state = State.RUNNING;
        nextPositions.set(List.of());
        selectedPiece.set(null);
        message.set("Start!");
    }

    public void select(Position position) {
        for (var piece : pieceList) {
            if (position.equals(piece.getPosition()))
                selectedPiece.set(piece);
        }

        var piece = selectedPiece.get();

        if (piece == null)
            return;

        // if piece is newly selected
        if (piece.getPosition().equals(position))
            return;

        // deselect if selected pane is not in movable positions
        if (!nextPositions.get().contains(position)) {
            selectedPiece.set(null);
            return;
        }

        // move
        piece.setPosition(position);
        selectedPiece.set(null);
        updateState();
    }

    private void updateNextPositions() {
        var piece = selectedPiece.get();

        if (piece == null) {
            nextPositions.set(List.of());
            return;
        }
        if (!isMovable(piece)) {
            nextPositions.set(List.of());
            message.set(selectedPiece.get().getClass().getSimpleName() + " is not movable");
            return;
        }

        var prev = nextPositions;
        var newList = piece.getMovablePoints();
        for (var p : pieceList)
            newList.remove(p.getPosition());

        nextPositions.set(newList);
    }

    /**
     * Returns true if selected piece is under attacked by other pieces.
     * @return True if movable
     */
    private boolean isMovable(Piece piece) {
        for (var p : pieceList) {
            if (p == piece) continue;
            if (p.getMovablePoints().contains(piece.getPosition()))
                return true;
        }
        return false;
    }

    private void updateState() {
        if (pieceList.stream().anyMatch(p -> p.getPosition().equals(GOAL_POS))) {
            state = State.GOAL;
            message.set("Goal");
        }
        else if (pieceList.stream().noneMatch(this::isMovable)) {
            state = State.OVER;
            message.set("Game Over");
        }
    }

    public Piece getKing() {
        return king;
    }

    public Piece getKnight() {
        return knight;
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public ObjectProperty<List<Position>> nextPositionsProperty() {
        return nextPositions;
    }
}
