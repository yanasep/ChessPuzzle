package app;

import board.Board;
import javafx.scene.layout.AnchorPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import piece.Piece;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {

    private static Controller controller;
    private static AnchorPane[][] grid;

    @BeforeAll
    public static void setUp() {
        controller = new Controller();
        AnchorPane[][] tiles = new AnchorPane[Board.ROW_SIZE][Board.COL_SIZE];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                tiles[i][j] = new AnchorPane();
            }
        }
        ReflectionTestUtils.setField(controller, "tiles", tiles);
        grid = tiles;
    }

    @Test
    public void searchPaneTest() {
        var x = ReflectionTestUtils.invokeMethod(controller, "searchPane", grid[2][3]);
        assertEquals(new Piece.Position(2,3), x);
    }
}
