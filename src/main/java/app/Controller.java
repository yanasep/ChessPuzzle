package app;

import board.Board;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import com.google.gson.Gson;

import score.Scorer;
import util.JsonIO;
import util.Position;

import java.io.*;
import java.util.List;

public class Controller {
    @FXML
    private Label msg;
    @FXML
    private GridPane grid;
    @FXML
    private TableView<ScoreRow> scoreTable;
    @FXML
    private TableColumn<ScoreRow, ScoreRow> numCol;
    @FXML
    private GridPane startPromptPane;
    @FXML
    private TextField nameField;
    @FXML
    private Button playBtn;
    @FXML
    private Label playerNameLabel;
    @FXML
    private GridPane endPromptPane;
    @FXML
    private Label endMsgLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label stepLabel;

    private final StringProperty playerName = new SimpleStringProperty();

    private Pane[][] tiles;
    private final Board board = new Board();

    private final Gson gson = new Gson();

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
        var kingTile = tiles[Board.KING_INIT_POS.getRow()][Board.KING_INIT_POS.getCol()];
        var knightTile = tiles[Board.KNIGHT_INIT_POS.getRow()][Board.KNIGHT_INIT_POS.getCol()];
        kingTile.getChildren().add(king);
        knightTile.getChildren().add(knight);
        kingTile.setCursor(Cursor.HAND);
        knightTile.setCursor(Cursor.HAND);

        msg.textProperty().bind(board.messageProperty());

        board.nextPositionsProperty().addListener((obs, oldVal, newVal) -> updateNextPositions(oldVal, newVal));
        board.getKing().positionProperty().addListener((obs, oldVal, newVal) -> {
            var prevTile = tiles[oldVal.getRow()][oldVal.getCol()];
            var newTile = tiles[newVal.getRow()][newVal.getCol()];
            newTile.getChildren().add(king);
            prevTile.setCursor(Cursor.DEFAULT);
            newTile.setCursor(Cursor.HAND);
        });
        board.getKnight().positionProperty().addListener((obs, oldVal, newVal) -> {
            var prevTile = tiles[oldVal.getRow()][oldVal.getCol()];
            var newTile = tiles[newVal.getRow()][newVal.getCol()];
            newTile.getChildren().add(knight);
            prevTile.setCursor(Cursor.DEFAULT);
            newTile.setCursor(Cursor.HAND);
        });
        initScoreTable();
        playBtn.disableProperty().bind(nameField.textProperty().isEmpty());
        playerName.bind(nameField.textProperty());
        playerNameLabel.textProperty().bind(playerName);
        board.stateProperty().addListener((obs, old, newVal) -> onStateChanged(newVal));
        startPromptPane.setVisible(true);
        Platform.runLater(()->nameField.requestFocus());

        timeLabel.textProperty().bind(board.getScorer().elapsedProperty().asString("%1$tM:%1$tS"));
        stepLabel.textProperty().bind(board.getScorer().stepProperty().asString());
    }

    private ImageView createImageView(String path) {
        var img = new Image(getClass().getResourceAsStream(path));
        var view = new ImageView(img);
        view.fitWidthProperty().bind(tiles[0][0].widthProperty().subtract(12));
        view.fitHeightProperty().bind(tiles[0][0].heightProperty().subtract(12));
        return view;
    }

    /**
     * Search and return the position of the specified pane
     * @param pane Pane to be searched
     * @return Position of the pane
     */
    private Position searchPanesPosition(Pane pane) {
        for (int i = 0; i < Board.ROW_SIZE; i++) {
            for (int j = 0; j < Board.COL_SIZE; j++) {
                if (tiles[i][j] == pane)
                    return new Position(i, j);
            }
        }
        return null;
    }

    /**
     * Update the displayed panes color to show movable positions.
     * @param prevList Previous movable positions
     * @param newList New movable positions
     */
    private void updateNextPositions(List<Position> prevList, List<Position> newList) {
        if (prevList != null) {
            for (var p : prevList) {
                var tile = tiles[p.getRow()][p.getCol()];
                tile.getStyleClass().remove("pane_emp");
                tile.setCursor(Cursor.DEFAULT);
            }
        }
        for (var p : newList) {
            var tile = tiles[p.getRow()][p.getCol()];
            tile.getStyleClass().add("pane_emp");
            tile.setCursor(Cursor.HAND);
        }
    }

    @FXML
    public void reset() {
        board.play();
    }

    private void initScoreTable() {
        // TODO: table height fit to 10 x cell_height

        var nameCol = new TableColumn<ScoreRow, String>("Name");
        var scoreCol = new TableColumn<ScoreRow, Integer>("Score");
        scoreTable.getColumns().addAll(nameCol, scoreCol);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        scoreCol.setComparator(scoreCol.getComparator().reversed());
        scoreTable.getSortOrder().add(scoreCol);

        numCol.setCellValueFactory(x -> new ReadOnlyObjectWrapper<>(x.getValue()));

        numCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ScoreRow, ScoreRow> call(TableColumn<ScoreRow, ScoreRow> x) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(ScoreRow item, boolean empty) {
                        super.updateItem(item, empty);

                        if (this.getTableRow() != null && item != null)
                            setText(this.getTableRow().getIndex() + 1 + "");
                        else
                            setText("");
                    }
                };
            }
        });

        try {
            var list = JsonIO.readJsonStream(getClass().getResourceAsStream("/score.json"));
            for (var item : list) scoreTable.getItems().add(item);
            scoreTable.sort();
        } catch (IOException | NullPointerException e) {
//            e.printStackTrace();
            System.out.println("score file not found");
        }
    }

    private void addScore(String name, int score) {
        scoreTable.getItems().add(new ScoreRow(name, score));
        scoreTable.sort();
        int size = scoreTable.getItems().size();
        if (size > 10)
            scoreTable.getItems().remove(size - 1);
    }

    public class ScoreRow {
        private final String name;
        private final int score;

        public ScoreRow(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }

    @FXML
    public void onPlayButtonPressed(ActionEvent e) {
        startPromptPane.setVisible(false);
        board.play();
    }

    @FXML
    public void onNewGameButtonPressed(ActionEvent e) {
//        board.play();
        endPromptPane.setVisible(false);
        startPromptPane.setVisible(true);
        nameField.requestFocus();
    }

    public void onStateChanged(Board.State newState) {
        if (newState == Board.State.RUNNING)
            return;

        if (newState == Board.State.GOAL) {
            board.end();
            int score = board.getScorer().getScore();
            endMsgLabel.setText("Congratulations! Your score is: " + score);
            addScore(playerName.get(), score);
        }
        else {
            board.end();
            endMsgLabel.setText("Game Over!");
        }

        endPromptPane.setVisible(true);
    }

    /**
     * Save score on exit.
     */
    public void onExit() {

        File f = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "score.json");
        try {
            f.createNewFile();
            JsonIO.writeJsonStream(new FileOutputStream(f), scoreTable.getItems());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
