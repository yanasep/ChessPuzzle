package state;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;

public class PieceTest {
    @Test
    public void testKingNextMoves() {
        var king = new Piece(PieceType.KING, 5, 6);
        var actual = king.getNextMoves();
        var expected = Arrays.asList(
                new Position(6,5),
                new Position(6,6),
                new Position(6,7),
                new Position(5,7),
                new Position(4,7),
                new Position(4,6),
                new Position(4,5),
                new Position(5,5)
        );
        Collections.sort(actual);
        Collections.sort(expected);
        assertEquals(expected, actual);
    }

    @Test
    public void testKnightNextMoves() {
        var knight = new Piece(PieceType.KNIGHT, 5, 6);
        var actual = knight.getNextMoves();
        var expected = Arrays.asList(
                new Position(7,7),
                new Position(6,8),
                new Position(4,8),
                new Position(3,7),
                new Position(3,5),
                new Position(4,4),
                new Position(6,4),
                new Position(7,5)
        );
        Collections.sort(actual);
        Collections.sort(expected);
        assertEquals(expected, actual);
    }
}
