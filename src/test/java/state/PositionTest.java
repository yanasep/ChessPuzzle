package state;

import org.junit.jupiter.api.Test;
import state.Position;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {
    @Test
    public void TestMovedBy() {
        Position pos = new Position(5, 5);
        Position moved = pos.movedBy(30, -1);
        assertEquals(new Position(35, 4), moved);
    }

    @Test
    public void testEquals() {
        var pos1 = new Position(-3, -100);
        var pos2 = new Position(-3, -100);
        assertEquals(pos1, pos2);
    }

    @Test
    public void testCompare() {
        var pos1 = new Position(10, 5);
        var pos2 = new Position(-1, 5);
        var pos3 = new Position(3, -3);
        var pos4 = new Position(3, 5);
        var pos5 = new Position(3, 5);
        var actual = Arrays.asList(pos1, pos2, pos3, pos4, pos5);
        Collections.sort(actual);
        var expected = Arrays.asList(pos2, pos3, pos4, pos5, pos1);
        assertEquals(expected, actual);
    }
}
