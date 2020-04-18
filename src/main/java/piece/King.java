package piece;

import util.Position;

public class King extends Piece {

    private static final Position[] steps = {
            new Position(1,0), // up
            new Position(1,-1), // up-left
            new Position(1,1), // up-right
            new Position(0,-1), // left
            new Position(0,1), // right
            new Position(-1,0), // down
            new Position(-1,-1), // down-left
            new Position(-1,1) // down-right
    };

    public King(int row, int col) {
        super(row, col, steps);
    }
}
