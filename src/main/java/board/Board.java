package board;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import piece.Piece;
import piece.PieceType;
import score.Scorer;
import geom.Position;

import java.util.*;

/**
 * Board of the chess puzzle.
 * <p>
 * Initialized with a king and a knight.
 *
 * Override protected methods to change rules of the puzzle.
 *
 * Pieces are reused and not recreated on each play.
 */
public class Board {
    public static final int ROW_SIZE = 8;
    public static final int COL_SIZE = 8;
    public static final Position GOAL_POS = new Position(0, 6);

    protected static final List<Piece> initialPieces = List.of(
            new Piece(PieceType.KING, 2, 1),
            new Piece(PieceType.KNIGHT, 2, 2)
    );

    private final ObjectProperty<Piece> selectedPiece = new SimpleObjectProperty<>();
    private final ObjectProperty<List<Position>> nextPositions = new SimpleObjectProperty<>();
    private final StringProperty message = new SimpleStringProperty();
    private final Scorer scorer = new Scorer();
    private final List<Piece> pieceList;

    public enum State {
        RUNNING, OVER, GOAL
    }

    private final ObjectProperty<State> state = new SimpleObjectProperty<>();

    /**
     * Creates a new board.
     */
    public Board() {
        pieceList = new ArrayList<>(initialPieces.size());
        for (var p : initialPieces)
            pieceList.add(new Piece(p.getType(), p.getPosition().getRow(), p.getPosition().getCol()));

        selectedPiece.addListener((obs, oldVal, newVal) -> {
            if (newVal == null) message.set("");
            else message.set(newVal.getType() + " selected");
        });
        selectedPiece.addListener((observable, oldVal, newVal) -> updateNextPositions(newVal));
    }

    /**
     * Starts new game.
     */
    public void play() {
        // push all pieces to a map
        Map<PieceType, LinkedList<Piece>> map = new HashMap<>();
        for (var p : pieceList) {
            map.computeIfAbsent(p.getType(), k -> new LinkedList<>());
            map.get(p.getType()).add(p);
        }
        pieceList.clear();
        // set pieces
        for (var p : initialPieces) {
            var piece = map.get(p.getType()).poll();
            assert piece != null;
            piece.setPosition(p.getPosition());
            pieceList.add(piece);
        }

        state.set(State.RUNNING);
        nextPositions.set(List.of());
        selectedPiece.set(null);
        scorer.start();
    }

    /**
     * Ends the puzzle and finalize the score.
     */
    public void end() {
        scorer.end();
    }

    /**
     * Selects the specified position on the board.
     * The consequences are either piece selection, deselection,
     * or piece movement.
     * @param position
     */
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

        // deselect if selected position is not in possible moves
        if (!nextPositions.get().contains(position)) {
            selectedPiece.set(null);
            return;
        }

        // move
        selectedPiece.set(null);
        move(piece, position);
        updateState();
        scorer.addMove();
    }

    /**
     * Move a piece to the specified position.
     * @param piece Piece to move
     * @param position Position where piece move to
     */
    protected void move(Piece piece, Position position) {
        piece.setPosition(position);
    }

    /**
     * Returns list of next possible positions of the specified piece.
     * @param piece Current piece selection. Cannot be null.
     * @return List of next possible positions.
     */
    protected List<Position> getNextMoves(Piece piece, List<Piece> pieceList) {
        var moves = piece.getNextMoves();
        for (var p : pieceList)
            moves.remove(p.getPosition());

        return moves;
    }

    /**
     * Returns true if selected piece is under attack by other pieces.
     *
     * @return True if movable
     */
    protected boolean isMovable(Piece piece) {
        for (var p : pieceList) {
            if (p == piece) continue;
            if (p.getNextMoves().contains(piece.getPosition()))
                return true;
        }
        return false;
    }

    /**
     * Update the current state of the game.
     */
    protected void updateState() {
        if (pieceList.stream().anyMatch(p -> p.getPosition().equals(GOAL_POS))) {
            state.set(State.GOAL);
            scorer.end();
        } else if (pieceList.stream().noneMatch(this::isMovable)) {
            state.set(State.OVER);
        }
    }

    private void updateNextPositions(Piece piece) {

        if (piece == null) {
            nextPositions.set(List.of());
            return;
        }
        if (!isMovable(piece)) {
            nextPositions.set(List.of());
            message.set(selectedPiece.get().getType() + " is not movable");
            return;
        }

        var moves = getNextMoves(piece, pieceList);
        nextPositions.set(moves);

        if (moves.size() == 0)
            message.set(selectedPiece.get().getType() + " is not movable");
    }

    public List<Piece> getPieceList() {
        return pieceList;
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
