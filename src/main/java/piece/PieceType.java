package piece;

import util.Position;

import java.util.List;

/**
 * Types of chess piece.
 * Each type holds unmodifiable list of possible moves.
 */
public enum PieceType {
    KING(new Position[]{
            new Position(1,0), // up
            new Position(1,-1), // up-left
            new Position(1,1), // up-right
            new Position(0,-1), // left
            new Position(0,1), // right
            new Position(-1,0), // down
            new Position(-1,-1), // down-left
            new Position(-1,1) // down-right
    }),
    KNIGHT(new Position[]{
            new Position(2, 1), // up-left 1
            new Position(1, 2), // up-left 2
            new Position(-1, 2), // down-left 1
            new Position(-2, 1), // down-left 2
            new Position(-2, -1), // down-right 1
            new Position(-1, -2), // down-right 2
            new Position(1, -2), // up-right 1
            new Position(2, -1) // up-right 2
    });

    private final List<Position> moves;
    PieceType(Position[] moves) {
        this.moves = List.of(moves);
    }
    public List<Position> getMoves() {
        return moves;
    }
}
