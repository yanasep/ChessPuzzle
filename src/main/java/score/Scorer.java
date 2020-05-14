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
 * <p>
 * Call start() to start scoring, addMove() after each moves, and end() to end scoring.
 * <p>
 * Time and number of moves are observable.
 */
public class Scorer {
    private final LongProperty time = new SimpleLongProperty();
    private final IntegerProperty numOfMoves = new SimpleIntegerProperty();
    private final Timeline clock;

    /**
     * Creates a new scorer.
     */
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
     * @return calculated score
     */
    public int getScore() {
        return calculate(numOfMoves.get(), time.get());
    }

    protected static int calculate(int moves, long time) {
        return 1000 - ((int) time >> 7) - moves * 50;
    }

    public LongProperty timeProperty() {
        return time;
    }

    public IntegerProperty moveProperty() {
        return numOfMoves;
    }
}
