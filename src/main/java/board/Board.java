package board;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import piece.Piece;
import piece.PieceType;
import score.Scorer;
import util.Position;
import java.util.List;

/**
 * Board of the chess puzzle.
 *
 * Initialized with a king and a knight at the initial position.
 */
public class Board {
    public static final int ROW_SIZE = 8;
    public static final int COL_SIZE = 8;
    public static final Position GOAL_POS = new Position(0, 6);
    public static final Position KING_INIT_POS = new Position(2, 1);
    public static final Position KNIGHT_INIT_POS = new Position(2, 2);

    private final Piece king = new Piece(PieceType.KING, KING_INIT_POS.getRow(),KING_INIT_POS.getCol());
    private final Piece knight = new Piece(PieceType.KNIGHT, KNIGHT_INIT_POS.getRow(),KNIGHT_INIT_POS.getRow());
    private final List<Piece> pieceList;
    private final ObjectProperty<Piece> selectedPiece = new SimpleObjectProperty<>();
    private final ObjectProperty<List<Position>> nextPositions = new SimpleObjectProperty<>();
    private final StringProperty message = new SimpleStringProperty();
    private final Scorer scorer = new Scorer();

    public enum State {
        RUNNING, OVER, GOAL
    }

    private final ObjectProperty<State> state = new SimpleObjectProperty<>();

    public Board() {
        pieceList = List.of(king, knight);
        selectedPiece.addListener((obs, oldVal, newVal) -> {
            if (newVal == null) message.set("");
            else message.set(newVal.getType() + " selected");
        });
        selectedPiece.addListener((observable, oldVal, newVal) -> updateNextPositions());
    }

    public void play() {
        king.setPosition(new Position(KING_INIT_POS.getRow(),KING_INIT_POS.getCol()));
        knight.setPosition(new Position(KNIGHT_INIT_POS.getRow(),KNIGHT_INIT_POS.getRow()));
        state.set(State.RUNNING);
        nextPositions.set(List.of());
        selectedPiece.set(null);
        scorer.start();
    }

    public void end() {
        scorer.end();
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
        selectedPiece.set(null);
        piece.setPosition(position);
        updateState();
        scorer.addMove();
    }

    private void updateNextPositions() {
        var piece = selectedPiece.get();

        if (piece == null) {
            nextPositions.set(List.of());
            return;
        }
        if (!isMovable(piece)) {
            nextPositions.set(List.of());
            message.set(selectedPiece.get().getType() + " is not movable");
            return;
        }

        var prev = nextPositions;
        var newList = piece.getNextMoves();
        for (var p : pieceList)
            newList.remove(p.getPosition());

        nextPositions.set(newList);

        if (newList.size() == 0)
            message.set(selectedPiece.get().getType() + " is not movable");
    }

    /**
     * Returns true if selected piece is under attack by other pieces.
     * @return True if movable
     */
    private boolean isMovable(Piece piece) {
        for (var p : pieceList) {
            if (p == piece) continue;
            if (p.getNextMoves().contains(piece.getPosition()))
                return true;
        }
        return false;
    }

    private void updateState() {
        if (pieceList.stream().anyMatch(p -> p.getPosition().equals(GOAL_POS))) {
            state.set(State.GOAL);
            scorer.end();
        }
        else if (pieceList.stream().noneMatch(this::isMovable)) {
            state.set(State.OVER);
        }
    }

    public Piece getKing() {
        return king;
    }

    public Piece getKnight() {
        return knight;
    }

    public StringProperty messageProperty() {
        return message;
    }

    public ObjectProperty<List<Position>> nextPositionsProperty() {
        return nextPositions;
    }

    public ObjectProperty<State> stateProperty() {
        return state;
    }

    public Scorer getScorer() {
        return scorer;
    }

}
