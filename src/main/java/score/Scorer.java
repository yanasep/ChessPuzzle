package score;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.util.Duration;

public class Scorer {
    private final LongProperty elapsed = new SimpleLongProperty();
    private final IntegerProperty numOfSteps = new SimpleIntegerProperty();
//    private static final Scorer instance = new Scorer();
    private final Timeline clock;

    public Scorer() {
        clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            elapsed.set(elapsed.get() + 1000);
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
    }

//    public static Scorer getInstance() {
//        return instance;
//    }

    public void start() {
        numOfSteps.set(0);
        elapsed.set(0);
        clock.play();
    }

    public void end() {
        clock.pause();
    }

    public void step() {
        numOfSteps.set(numOfSteps.get() + 1);
    }

    public int getScore() {
        return 10_000 - (numOfSteps.get()) * 10;
    }

    public LongProperty elapsedProperty() {
        return elapsed;
    }
    public IntegerProperty stepProperty() {
        return numOfSteps;
    }
}
