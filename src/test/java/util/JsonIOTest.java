package util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JsonIOTest {
    File file = new File("testfile5213214321");

    @AfterAll
    public void deleteFile() {
        file.delete();
    }

    @Test
    public void testReadwrite() throws IOException {
        file.createNewFile();

        List<TestObj> list = List.of(
                new TestObj(0, "Hello"),
                new TestObj(1, "World")
                );
        JsonIO.writeJsonStream(new FileOutputStream(file),list);

        List<TestObj> output = JsonIO.readJsonStream(new FileInputStream(file), TestObj.class);
        assertEquals(0, output.get(0).num);
        assertEquals("Hello", output.get(0).str);
        assertEquals(1, output.get(1).num);
        assertEquals("World", output.get(1).str);
    }

    public class TestObj {
        public int num;
        public String str;
        public TestObj(int num, String str) {
            this.num = num;
            this.str = str;
        }
    }
}
