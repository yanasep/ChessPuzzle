package app;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import state.Board;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

import state.Piece;
import state.PieceType;
import util.JsonIO;
import state.Position;

import java.io.*;
import java.util.List;

/**
 * Gets the click event and manipulates board.
 */
@Slf4j
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
    private Label moveLabel;

    private Pane[][] tiles;
    private final Board board = new Board();
    private final File scoreFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "-score.json");

    /**
     * Initialize the components on launch.
     */
    public void initialize() {
        initTiles();

        // set pieces
        var pieceList = board.getPieceList();
        for (var p : pieceList)
            initPiece(p);

        initScoreTable();

        // set bindings
        board.nextPositionsProperty().addListener((obs, oldVal, newVal) -> updateNextPositions(oldVal, newVal));
        board.stateProperty().addListener((obs, old, newVal) -> onStateChanged(newVal));
        playBtn.disableProperty().bind(nameField.textProperty().isEmpty());

        msg.textProperty().bind(board.messageProperty());
        playerNameLabel.textProperty().bind(nameField.textProperty());
        timeLabel.textProperty().bind(board.getScorer().timeProperty().asString("%1$tM:%1$tS"));
        moveLabel.textProperty().bind(board.getScorer().moveProperty().asString());

        startPromptPane.setVisible(true);
        Platform.runLater(() -> nameField.requestFocus());
    }

    /**
     * Creates grid tiles and add event handler to each tile.
     */
    private void initTiles() {
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
                    var p = (Pane) e.getSource();
                    var pos = searchPanesPosition(p);
                    board.select(pos);
                });
            }
        }
        tiles[Board.GOAL_POS.getRow()][Board.GOAL_POS.getCol()].getStyleClass().add("goal_pane");
    }

    /**
     * Returns a imageView which fits the size of the grid tile.
     *
     * @param path Path of the image
     * @return ImageView
     */
    private ImageView createImageView(String path) {
        var img = new Image(getClass().getResourceAsStream(path));
        var view = new ImageView(img);
        view.fitWidthProperty().bind(tiles[0][0].widthProperty().subtract(12));
        view.fitHeightProperty().bind(tiles[0][0].heightProperty().subtract(12));
        return view;
    }

    /**
     * Initialize gui component of piece and add event listener.
     * @param piece Piece
     */
    private void initPiece(Piece piece) {
        var imgPath = (piece.getType() == PieceType.KING) ? "/images/king.png" : "/images/knight.png";
        var view = createImageView(imgPath);
        var tile = tiles[piece.getPosition().getRow()][piece.getPosition().getCol()];
        tile.getChildren().add(view);
        tile.setCursor(Cursor.HAND);
        piece.positionProperty().addListener((obs, oldVal, newVal) -> {
            var prevTile = tiles[oldVal.getRow()][oldVal.getCol()];
            var newTile = tiles[newVal.getRow()][newVal.getCol()];
            newTile.getChildren().add(view);
            prevTile.setCursor(Cursor.DEFAULT);
            newTile.setCursor(Cursor.HAND);
        });
    }

    /**
     * Search and return the position of the specified pane in grid.
     *
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
     * Update the color of panes to show movable positions.
     *
     * @param prevList Previous movable positions
     * @param newList  New movable positions
     */
    private void updateNextPositions(List<Position> prevList, List<Position> newList) {
        if (prevList != null) {
            for (var p : prevList) {
                var tile = tiles[p.getRow()][p.getCol()];
                tile.getStyleClass().remove("pane_highlight");
                tile.setCursor(Cursor.DEFAULT);
            }
        }
        for (var p : newList) {
            var tile = tiles[p.getRow()][p.getCol()];
            tile.getStyleClass().add("pane_highlight");
            tile.setCursor(Cursor.HAND);
        }
    }

    private void initScoreTable() {
        log.info("Initializing score table");
        var nameCol = new TableColumn<ScoreRow, String>("Name");
        var scoreCol = new TableColumn<ScoreRow, Integer>("Score");
        scoreTable.getColumns().addAll(nameCol, scoreCol);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        scoreCol.setComparator(scoreCol.getComparator().reversed());
        scoreTable.getSortOrder().add(scoreCol);

        // automatically put line number
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

        // load scores if present
        try (var is = new FileInputStream(scoreFile)) {
            log.info("Score file found. Adding entries to the table");
            var list = JsonIO.readJsonStream(is, ScoreRow.class);
            log.debug("Adding {} items", list.size());
            for (var item : list) scoreTable.getItems().add(item);
            scoreTable.sort();
        } catch (IOException | NullPointerException e) {
            log.trace("No score file found");
            System.out.println("score file not found");
        }
    }

    /**
     * Add new record to the score table.
     * @param name Player name
     * @param score Score
     */
    private void addScoreRow(String name, int score) {
        log.debug("Player: {}, score: {}", name, score);
        scoreTable.getItems().add(new ScoreRow(name, score));
        scoreTable.sort();
        int size = scoreTable.getItems().size();
        if (size > 10)
            scoreTable.getItems().remove(size - 1);
    }

    /**
     * Structure of data to be stored in score file.
     */
    @Data
    @AllArgsConstructor
    public class ScoreRow {
        private final String name;
        private final int score;
    }

    /**
     * Reset button action. Restarts game.
     */
    @FXML
    public void reset() {
        log.info("Restarting game...");
        board.play();
    }

    /**
     * Play button action. Starts playing game.
     * @param e
     */
    @FXML
    public void onPlayButtonPressed(ActionEvent e) {
        log.info("Starting game...");
        startPromptPane.setVisible(false);
        board.play();
    }

    /**
     * New Game button action. Shows start prompt.
     * @param e
     */
    @FXML
    public void onNewGameButtonPressed(ActionEvent e) {
        log.debug("{} pressed", ((Button) e.getSource()).getText());
        log.info("Showing new game prompt");
        endPromptPane.setVisible(false);
        startPromptPane.setVisible(true);
        nameField.requestFocus();
    }

    /**
     * Change GUI on state change.
     * @param newState New state
     */
    public void onStateChanged(Board.State newState) {
        if (newState == Board.State.RUNNING)
            return;

        if (newState == Board.State.GOAL) {
            log.info("Goal");
            board.end();
            int score = board.getScorer().getScore();
            endMsgLabel.setText("Congratulations! Your score is: " + score);
            addScoreRow(playerNameLabel.getText(), score);
        } else {
            log.info("Game over");
            board.end();
            endMsgLabel.setText("Game Over!");
        }

        endPromptPane.setVisible(true);
    }

    /**
     * Save score on exit.
     */
    public void onExit() {
        log.info("Saving scores to {}...", scoreFile.getName());
        try {
            scoreFile.createNewFile();
            JsonIO.writeJsonStream(new FileOutputStream(scoreFile), scoreTable.getItems());
        } catch (IOException e) {
            log.error("Failed.");
            e.printStackTrace();
        }
        log.info("Exiting app...");
    }

}
