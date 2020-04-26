package score;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScorerText {
    @Test
    public void normalScoringTest() {
        int score1 = Scorer.calculate(15, 50000);
        int score2 = Scorer.calculate(20, 60000);
        assertTrue(score1 > score2);
    }

    @Test
    public void moveIsMoreImportantTest() {
        int score3 = Scorer.calculate(5, 100000);
        int score4 = Scorer.calculate(30, 10000);
        assertTrue(score3 > score4);
    }

    @Test
    public void addMoveTest() {
        Scorer scorer = new Scorer();
        scorer.moveProperty().set(0);
        scorer.addMove();
        assertEquals(1, scorer.moveProperty().get());
    }
}
