package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/chess.fxml"));
        primaryStage.setTitle("Chess Puzzle");
        Scene scene = new Scene(root, 700, 700);
        scene.getStylesheets().add("/styles.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

/*
===

Consider a chess board, to which a king and a knight piece is placed as shown in the image.
The goal of the player is to move the king or the knight piece to the square marked red (g1).
The pieces move according to the rules of the chess, with the following extra constraint:
    a piece can be moved if and only if it is on a square that is under attack by the other piece.

When a new game is started the program must ask for the name of the player.
The program must maintain a high score table in which the top 10 results are displayed.
You can score the players based on the steps/time required to solve the puzzle.

You can store the high score table in a database, in a JSON file, or in an XML document.
Optionally, you can implement load/save game functionality.

 */