package board;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.junit.jupiter.api.Test;
import piece.King;
import piece.Piece;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    ObjectProperty<Piece> piece = new SimpleObjectProperty<>();
    String str;

    @Test
    public void changeTest() {
        piece.addListener(new ChangeListener<Piece>() {
            @Override
            public void changed(ObservableValue<? extends Piece> observableValue, Piece piece, Piece t1) {
                str = "changed";
            }
        });

        str = "";
        piece.set(new King(1,1));
        assertEquals("changed", str);
    }
}
