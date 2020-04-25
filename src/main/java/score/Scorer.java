package score;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.util.Duration;

/**
 * Provides scoring.
 *
 * Call start() to start scoring, addMove() after each moves, and end() to end scoring.
 *
 * Time and number of moves are observable.
 */
public class Scorer {
    private final LongProperty time = new SimpleLongProperty();
    private final IntegerProperty numOfMoves = new SimpleIntegerProperty();
    private final Timeline clock;

    public Scorer() {
        clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            time.set(time.get() + 1000);
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Set values to zero and start timer.
     */
    public void start() {
        numOfMoves.set(0);
        time.set(0);
        clock.play();
    }

    /**
     * Stops timer.
     */
    public void end() {
        clock.pause();
    }

    /**
     * Adds 1 to number of moves.
     */
    public void addMove() {
        numOfMoves.set(numOfMoves.get() + 1);
    }

    /**
     * Returns score at the time and of the number of moves.
     * @return
     */
    public int getScore() {
        return 1000 - ((int) time.get() >> 7) - numOfMoves.get() * 10;
    }

    public LongProperty timeProperty() {
        return time;
    }
    public IntegerProperty moveProperty() {
        return numOfMoves;
    }
}
