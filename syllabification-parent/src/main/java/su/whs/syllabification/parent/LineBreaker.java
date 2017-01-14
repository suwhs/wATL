package su.whs.syllabification.parent;

/**
 *
 *
 * @author igor n. boulliev <igor@whs.su>
 * Apache License 2.0
 *
 */

public abstract class LineBreaker {
    /**
     * LineBreaker interface
     *
     * @param text
     * @param start
     * @param end
     * @return type and position of linebreak between start and end NOTE: position of
     * last character at the line
     */

    public static final int HYPHEN = 0xf0000000;

    public abstract int nearestLineBreak(char[] text, int start, int end, int limit);

    public static int getPosition(int value) {
        return value & 0x0fffffff;
    }

    public static boolean isHyphen(int value) {
        return (value & HYPHEN) == HYPHEN;
    }

    /* punctuation ranges */
    // 20-3f
    // 5b-60
    // 7b-7e

    /*

        if (c >= 0x0600 && c <=0x06E0)

  }
     */

    public static boolean isLetter(char ch) {
                                          /* 0x0600-0x06e0 - arabic */
        return Character.isLetter(ch) || (ch >=0x0600 && ch<= 0x06e0);
    }

    public static boolean isPunktuation(char ch) {
        return ch < 0x7e &&
                (ch > 0x20 &&
                        (ch < 0x40 ||
                                (ch > 0x5b &&
                                        (ch < 0x60 ||
                                                ch > 0x7e))));
    }
}
