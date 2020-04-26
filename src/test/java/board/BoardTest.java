package board;

import org.junit.jupiter.api.Test;
import piece.Piece;
import piece.PieceType;
import geom.Position;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    Board board = new Board();

    @Test
    public void playTest() throws NoSuchFieldException, IllegalAccessException {
        board.play();
        var list = board.getPieceList();
        var expectedList = (List<Piece>)board.getClass().getDeclaredField("initialPieces").get(board);
        assertEquals(expectedList.size(), list.size());

        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i).getType(), list.get(i).getType());
            assertEquals(expectedList.get(i).getPosition(), list.get(i).getPosition());
        }
    }

    @Test
    public void selectTest() {
        board.play();
        Piece piece = null;
        var list = board.getPieceList();
        for (var p : list) {
            if (board.isMovable(p))
                piece = p;
        }
        assertNotNull(piece);
        board.select(piece.getPosition());
        var list2 = board.getNextMoves(piece, board.getPieceList());
        assertTrue(list2.size() > 0);
        board.select(list2.get(0));
        assertEquals(list2.get(0), piece.getPosition());
    }

    @Test
    public void moveTest() {
        var piece = board.getPieceList().get(0);
        var pos = new Position(0,0);
        board.move(piece, pos);
        assertEquals(pos, piece.getPosition());
    }

    @Test
    public void getKingNextMovesTest() {
        var king = new Piece(PieceType.KING, 0,0);
        var other = new Piece(PieceType.KING, 0,1);
        var other2 = new Piece(PieceType.KNIGHT, 1,0);
        var list = List.of(king, other, other2);
        var actual = board.getNextMoves(king, list);
        var expected = List.of(new Position(1,1));
        assertEquals(expected, actual);
    }

    @Test
    public void getKnightNextMovesTest() {
        var knight = new Piece(PieceType.KNIGHT, 0,0);
        var other = new Piece(PieceType.KING, 0,1);
        var other2 = new Piece(PieceType.KNIGHT, 2,1);
        var list = List.of(knight, other, other2);
        var actual = board.getNextMoves(knight, list);
        var expected = List.of(new Position(1,2));
        assertEquals(expected, actual);
    }
}
