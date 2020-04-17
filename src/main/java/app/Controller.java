package app;

import board.Board;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import piece.Piece;

import java.util.List;

public class Controller {
    @FXML
    private Label msg;
    @FXML
    private GridPane grid;

    private Pane[][] tiles;

    private boolean isPieceSelected = false;
    private Board.PieceType selectedPiece;
    private List<Piece.Position> nextPositions;
    private Board board = new Board();

    private Pane goalPane;
    private ImageView king;
    private ImageView knight;
    private Pane kingPane;
    private Pane knightPane;

    public void initialize() {

        tiles = new StackPane[Board.ROW_SIZE][Board.COL_SIZE];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                var pane = new StackPane();
                tiles[i][j] = pane;
                grid.add(pane, i, j);
                if ((i + j) % 2 == 0)
                    pane.getStyleClass().add("tile1");
                else
                    pane.getStyleClass().add("tile2");

                pane.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onPaneClicked);
            }
        }
        goalPane = tiles[6][convertRowIndex(0)];
        goalPane.getStyleClass().add("goal_pane");

        king = new ImageView();
        setPiece(king, "/images/king.png");
        knight = new ImageView();
        setPiece(knight, "/images/knight.png");

        kingPane = tiles[1][convertRowIndex(2)];
        knightPane = tiles[2][convertRowIndex(2)];
        kingPane.setCursor(Cursor.HAND);
        knightPane.setCursor(Cursor.HAND);
        kingPane.getChildren().add(king);
        knightPane.getChildren().add(knight);
//
//        board.getNextPositions().addListener(new ListChangeListener<Piece.Position>() {
//            @Override
//            public void onChanged(Change<? extends Piece.Position> change) {
//                msg.setText("changed-----------");
//            }
//        });
    }

    public void reset() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                var tile = tiles[i][j];
                tile.getStyleClass().remove("pane_selected");
                tile.setCursor(Cursor.DEFAULT);
            }
        }
        board = new Board();
        isPieceSelected = false;
        nextPositions.clear();

        kingPane.getChildren().remove(king);
        knightPane.getChildren().remove(knight);
        kingPane = tiles[1][convertRowIndex(2)];
        knightPane = tiles[2][convertRowIndex(2)];
        kingPane.setCursor(Cursor.HAND);
        knightPane.setCursor(Cursor.HAND);
        kingPane.getChildren().add(king);
        knightPane.getChildren().add(knight);
        msg.setText("Start");
    }

    private void setPiece(ImageView piece, String path) {

        var image = new Image(getClass().getResourceAsStream(path));
        piece.setImage(image);
        piece.fitWidthProperty().bind(tiles[0][0].widthProperty().subtract(12));
        piece.fitHeightProperty().bind(tiles[0][0].heightProperty().subtract(12));
    }

    private int convertRowIndex(int index) {
        return Board.ROW_SIZE - index - 1;
    }

    public void onKingClicked() {
        if (isPieceSelected)
            deselect();

        if (!board.isMovable(Board.PieceType.KING)) {
            deselect();
            msg.setText("King is not movable");
            return;
        }
        msg.setText("King selected");
        isPieceSelected = true;
        selectedPiece = Board.PieceType.KING;
        nextPositions = board.getMovablePositions(Board.PieceType.KING);
        displayNextPositions();
    }

    public void onKnightClicked() {
        if (isPieceSelected)
            deselect();

        if (!board.isMovable(Board.PieceType.KNIGHT)) {
            deselect();
            msg.setText("Knight is not movable");
            return;
        }
        msg.setText("Knight selected");
        isPieceSelected = true;
        selectedPiece = Board.PieceType.KNIGHT;
        nextPositions = board.getMovablePositions(Board.PieceType.KNIGHT);
        displayNextPositions();
    }

    public void onPaneClicked(MouseEvent e) {
        var selected = (Pane) e.getSource();

        if (selected == kingPane) {
            onKingClicked();
            return;
        } else if (selected == knightPane) {
            onKnightClicked();
            return;
        } else if (!isPieceSelected)
            return;

        if (!selected.getStyleClass().contains("pane_selected")) {
            deselect();
            return;
        }

        var next = searchPane(selected);

        if (selectedPiece == Board.PieceType.KING) {
            kingPane.getChildren().remove(king);
            kingPane.setCursor(Cursor.DEFAULT);
            selected.getChildren().add(king);
            kingPane = selected;
        } else {
            knightPane.getChildren().remove(knight);
            knightPane.setCursor(Cursor.DEFAULT);
            selected.getChildren().add(knight);
            knightPane = selected;
        }
        deselect();
        kingPane.setCursor(Cursor.HAND);
        knightPane.setCursor(Cursor.HAND);
        board.move(selectedPiece, next.getRow(), next.getCol());
        var state = board.getState();
        if (state == Board.State.GOAL)
            msg.setText("Goal!");
        else if (state == Board.State.OVER) {
            msg.setText("Game Over!");
        }
    }

    private Piece.Position searchPane(Pane pane) {

        for (var pos : nextPositions) {
            var p = tiles[pos.getCol()][convertRowIndex(pos.getRow())];
            if (p == pane) {
                return pos;
            }
        }
        return null;
    }

    private void displayNextPositions() {
        for (var p : nextPositions) {
            var pane = tiles[p.getCol()][convertRowIndex(p.getRow())];
            pane.setCursor(Cursor.HAND);
            pane.getStyleClass().add("pane_selected");
        }
    }

    private void deselect() {
        isPieceSelected = false;
        if (nextPositions == null) return;
        for (var p : nextPositions) {
            var pane = tiles[p.getCol()][convertRowIndex(p.getRow())];
            pane.setCursor(Cursor.DEFAULT);
            pane.getStyleClass().remove("pane_selected");
        }
    }
}
