package piece;

public class Knight extends Piece {

    private static final Position[] steps = {
            new Position(2,1), // up-left 1
            new Position(1,2), // up-left 2
            new Position(-1,2), // down-left 1
            new Position(-2,1), // down-left 2
            new Position(-2,-1), // down-right 1
            new Position(-1,-2), // down-right 2
            new Position(1,-2), // up-right 1
            new Position(2,-1) // up-right 2
    };

    public Knight(int row, int col) {
        super(row, col, steps);
    }
}
