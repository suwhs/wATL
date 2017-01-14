package su.whs.hyphens;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by igor n. boulliev on 13.01.17.
 */
public class InputStreamPatternLoader {
    public HyphenPattern getHyphenPattern(String fileName) {
        return getHyphenPatternInputStream(getClass().getResourceAsStream(fileName));
    }

    public HyphenPattern getHyphenPatternInputStream(InputStream inputStream) {
        try {
            DataInputStream in = new DataInputStream(inputStream);
            return new HyphenPattern(in);
        } catch (IOException e) {
            return null;
        }
    }
}
