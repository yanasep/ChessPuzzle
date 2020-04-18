package app;

import board.Board;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import util.Position;

import java.util.List;

public class Controller {
    @FXML
    private Label msg;
    @FXML
    private GridPane grid;

    private Pane[][] tiles;
    private final Board board = new Board();

    public void initialize() {

        tiles = new StackPane[Board.ROW_SIZE][Board.COL_SIZE];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                var pane = new StackPane();
                tiles[i][j] = pane;
                grid.add(pane, j, Board.ROW_SIZE - i - 1);
                if ((i + j) % 2 == 0)
                    pane.getStyleClass().add("tile1");
                else
                    pane.getStyleClass().add("tile2");

                pane.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    var p = (Pane)e.getSource();
                    var pos = searchPanesPosition(p);
                    board.select(pos);
                });
            }
        }
        tiles[Board.GOAL_POS.getRow()][Board.GOAL_POS.getCol()].getStyleClass().add("goal_pane");

        var king = createImageView("/images/king.png");
        var knight = createImageView("/images/knight.png");
        tiles[Board.KING_INIT_POS.getRow()][Board.KING_INIT_POS.getCol()].getChildren().add(king);
        tiles[Board.KNIGHT_INIT_POS.getRow()][Board.KNIGHT_INIT_POS.getCol()].getChildren().add(knight);

        msg.textProperty().bind(board.messageProperty());

        board.nextPositionsProperty().addListener((obs, oldVal, newVal) -> updateNextPositions(oldVal, newVal));
        board.getKing().positionProperty().addListener((obs, oldVal, newVal) -> {
            tiles[newVal.getRow()][newVal.getCol()].getChildren().add(king);
        });
        board.getKnight().positionProperty().addListener((obs, oldVal, newVal) -> {
            tiles[newVal.getRow()][newVal.getCol()].getChildren().add(knight);
        });
    }

    private ImageView createImageView(String path) {
        var img = new Image(getClass().getResourceAsStream(path));
        var view = new ImageView(img);
        view.fitWidthProperty().bind(tiles[0][0].widthProperty().subtract(12));
        view.fitHeightProperty().bind(tiles[0][0].heightProperty().subtract(12));
        return view;
    }

    private Position searchPanesPosition(Pane pane) {
        for (int i = 0; i < Board.ROW_SIZE; i++) {
            for (int j = 0; j < Board.COL_SIZE; j++) {
                if (tiles[i][j] == pane)
                    return new Position(i, j);
            }
        }
        return null;
    }

    private void updateNextPositions(List<Position> prevList, List<Position> newList) {
        for (var p : prevList)
            tiles[p.getRow()][p.getCol()].getStyleClass().remove("pane_emp");

        for (var p : newList)
            tiles[p.getRow()][p.getCol()].getStyleClass().add("pane_emp");

    }

    public void reset() {
        board.reset();
    }

}
