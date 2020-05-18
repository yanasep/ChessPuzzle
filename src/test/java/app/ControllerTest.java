package app;

import state.Board;
import state.Position;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ControllerTest {

    private static Controller controller;
    private static Pane[][] tiles;

    @BeforeAll
    public static void setUp() throws NoSuchFieldException, IllegalAccessException {
        controller = new Controller();
        Pane[][] grid = new Pane[Board.ROW_SIZE][Board.COL_SIZE];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = new Pane();
            }
        }
        var f = Controller.class.getDeclaredField("tiles");
        f.setAccessible(true);
        f.set(controller, grid);
        tiles = (Pane[][]) f.get(controller);
    }

    @Test
    public void searchPaneTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var search = Controller.class.getDeclaredMethod("searchPanesPosition", Pane.class);
        search.setAccessible(true);
        Position pos = (Position) search.invoke(controller, tiles[2][3]);
        assertEquals(new Position(2,3), pos);
    }
}
