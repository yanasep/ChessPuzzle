package state;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Board of the chess puzzle.
 * <p>
 * Initialized with a king and a knight.
 *
 * Override protected methods to change rules of the puzzle.
 *
 * Pieces are reused and not recreated on each play.
 */
@Slf4j
public class Board {
    /**
     * Row size of the board.
     */
    public static final int ROW_SIZE = 8;
    /**
     * Column size of the board.
     */
    public static final int COL_SIZE = 8;
    /**
     * Goal position.
     */
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

    /**
     * Game state.
     */
    public enum State {
        /**
         * game is running.
         */
        RUNNING,
        /**
         * game is over.
         */
        OVER,
        /**
         * puzzle is solved.
         */
        GOAL
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
            else { message.set(newVal.getType() + " selected"); log.info("{} selected", newVal.getType()); }
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
     * @param position Position to select
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

        // deselect if selected position is not in next moves
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
        log.info("Moving {}", piece.getType());
        piece.setPosition(position);
    }

    /**
     * Returns list of next possible positions of the specified piece.
     * A piece cannot move to other pieces' position.
     * @param piece Current piece selection. Cannot be null.
     * @param pieceList List of pieces on the board
     * @return List of next possible positions of the piece.
     */
    protected List<Position> getNextMoves(Piece piece, List<Piece> pieceList) {
        var moves = piece.getNextMoves();

        Predicate<Position> isOnBoard = p ->
                p.getRow() >= 0 && p.getRow() < Board.ROW_SIZE && p.getCol() >= 0 && p.getCol() < Board.COL_SIZE;

        moves = moves.stream().filter(isOnBoard).collect(Collectors.toList());

        for (var p : pieceList)
            moves.remove(p.getPosition());

        return moves;
    }

    /**
     * Returns true if selected piece is under attack by other pieces.
     * @param piece Piece to test
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
        else
            log.debug("Next positions: {}", nextPositions.get());
    }

    /**
     * Gets list of pieces on the board.
     * @return list of pieces
     */
    public List<Piece> getPieceList() {
        return pieceList;
    }

    /**
     * Gets message property.
     * @return message property
     */
    public StringProperty messageProperty() {
        return message;
    }

    /**
     * Gets next positions property of selected piece.
     * @return next positions property
     */
    public ObjectProperty<List<Position>> nextPositionsProperty() {
        return nextPositions;
    }

    /**
     * Gets game state property.
     * @return state property
     */
    public ObjectProperty<State> stateProperty() {
        return state;
    }

    /**
     * Gets scorer object.
     * @return scorer
     */
    public Scorer getScorer() {
        return scorer;
    }

}
