package app;

import board.Board;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import piece.Piece;
import util.Position;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {

    private static Controller controller;
    private static Pane[][] grid;

    @BeforeAll
    public static void setUp() {
        controller = new Controller();
        Pane[][] tiles = new Pane[Board.ROW_SIZE][Board.COL_SIZE];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                tiles[i][j] = new Pane();
            }
        }
        ReflectionTestUtils.setField(controller, "tiles", tiles);
        grid = tiles;
    }

    @Test
    public void searchPaneTest() {
        var x = ReflectionTestUtils.invokeMethod(controller, "searchPanesPosition", grid[2][3]);
        assertEquals(new Position(2,3), x);
    }
}
