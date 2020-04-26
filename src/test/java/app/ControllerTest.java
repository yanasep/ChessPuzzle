package app;

import javafx.scene.layout.Pane;
//import org.springframework.test.util.ReflectionTestUtils;


public class ControllerTest {

    private static Controller controller;
    private static Pane[][] grid;

//    @BeforeAll
//    public static void setUp() {
//        controller = new Controller();
//        Pane[][] tiles = new Pane[Board.ROW_SIZE][Board.COL_SIZE];
//        for (int i = 0; i < tiles.length; i++) {
//            for (int j = 0; j < tiles[i].length; j++) {
//                tiles[i][j] = new Pane();
//            }
//        }
//        ReflectionTestUtils.setField(controller, "tiles", tiles);
//        grid = tiles;
//    }
//
//    @Test
//    public void searchPaneTest() {
//        var x = ReflectionTestUtils.invokeMethod(controller, "searchPanesPosition", grid[2][3]);
//        assertEquals(new Position(2,3), x);
//    }
}
