import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

public class ApplicationTest {
    @Test
    void test() {
        assert Charset.defaultCharset().name().equals("UTF-8");
    }
}
