package piece;

import util.Position;

import java.util.List;

/**
 * Types of chess pieces.
 * Each type holds unmodifiable list of possible moves.
 */
public enum PieceType {
    KING(new Position[]{
            new Position(1,0), // top
            new Position(1,-1), // top-left
            new Position(1,1), // top-right
            new Position(0,-1), // left
            new Position(0,1), // right
            new Position(-1,0), // bottom
            new Position(-1,-1), // bottom-left
            new Position(-1,1) // bottom-right
    }),
    KNIGHT(new Position[]{
            new Position(2, 1), // upper top-left
            new Position(1, 2), // lower top-left
            new Position(-1, 2), // upper bottom-left
            new Position(-2, 1), // lower bottom-left
            new Position(-2, -1), // lower bottom-right
            new Position(-1, -2), // upper bottom-right
            new Position(1, -2), // lower top-right
            new Position(2, -1) // upper top-right
    });

    private final List<Position> moves;
    PieceType(Position[] moves) {
        this.moves = List.of(moves);
    }
    public List<Position> getMoves() {
        return moves;
    }
}
