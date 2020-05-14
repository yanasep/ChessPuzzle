package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChessApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chess.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        primaryStage.setTitle("Chess Puzzle");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles.css");
        primaryStage.setScene(scene);
        primaryStage.setOnHidden(e -> controller.onExit());
        primaryStage.show();
    }
}
