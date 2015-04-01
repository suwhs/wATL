package su.whs.watl.samples;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class SampleContent {
	public static String get() {
        try {
            return Base64DecodeAndGunzipString(CONTENT);
        } catch (IOException e) {
            return "Error decoding sample cotent";
        }
    }

    public static String wesnoth() {
        try {
            return Base64DecodeAndGunzipString(WESNOTH);
        } catch (IOException e) {
            return "Error decoding sample content";
        }
    }
	
	private static String Base64DecodeAndGunzipString(String b64) throws IOException {
		byte[] decoded = Base64.decode(b64, Base64.DEFAULT);
		GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(
				decoded));
		byte[] inflated = new byte[1024];
		StringBuilder sb = new StringBuilder();
		int wasRead = gzis.read(inflated);
		while (wasRead > 0) {
			sb.append(new String(inflated, 0, wasRead));
			wasRead = gzis.read(inflated);
		}
		gzis.read(inflated);
		return sb.toString();
	}

    public static final String LOREM= ",    ,       <i>Lorem</i> <a href=\"http://ya.ru\">ipsum dolor</a> <font color=\"blue\">sit amet</font>, <b>consectetur adipisicing elit</b>, <b>sed</b> do eiusmod tempor incididunt. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt. Lorem ipsum dolor sit amet, <h2>consectetur M</h2> adipisicing elit, sed do eiusmod tempor incididunt!"
             + "<img src=\"blabla\"/><blockquote><i>Lorem</i> <ul><li>ipsum</li><li> dolor sit amet,\\</li></ul> <b>consectetur1234567 adipisicing elit</b>,</blockquote><b>sed</b> do eiusmod tempor incididunt. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt. Lorem ipsum dolor sit amet, <h2>consectetur</h2> adipisicing elit, sed do eiusmod tempor incididunt!"
            + "<i>Lorem</i> ipsum dolor sit amet, <b>consectetur adipisicing elit</b>, <b>sed</b> do eiusmod tempor incididunt. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt. Lorem ipsum dolor sit amet, <h2>consectetur</h2> adipisicing elit, sed do eiusmod tempor incididunt!"
            + "<i>Lorem</i> ipsum dolor sit amet, <b>consectetur adipisicing elit,sed do eiusmod</b> tempor incididunt. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt. Lorem ipsum dolor sit amet, <h2>consectetur</h2> adipisicing elit, sed do eiusmod <i>tempor incididunt</i>!!<br/><p align=\"right\">the end.</p>";
       //     + "aasdsds sdsdds dsdsds dsdssf dfdadfad asdfaddas dsafdas <i>sdsdfdsf sdfdssf</i> sdfsdfdsfds sdfsdsdf sdfsdfsf sdfsdsdf <i>italic</i> sdfsdfdsf sdfsdfsd <i>italic</i> non-italic tail";
           // +  "<p>In his Early Plays a remarkable explosion of work came from his pen. In a matter of four years (1592-1595) he wrote seven popular plays of wide variety. He began developing the categories of interest he would pursue his whole career: History plays, Latin (or Roman) plays, Comedies, and the Tragedies. These plays are as follows: \"Richard III\" (1592-1593)- History "
    //        + "<p>\"The Comedy of Errors\"  (1592-1593)- Comedy \"Titus Andronicus\" (1593-1594)- Roman \"The Taming of the Shrew\"  (1593-1595)- Comedies \"The Two Gentlemen of Verona\" \"Love's Labor's Lost\" \"Romeo and Juliet\" (1594-1595)- Tragedy The latter play, \"Romeo and Juliet\" is sometimes referred to as an imperfect tragedy as it begins and apparently runs toward a comedy's resolution, but exactly halfway through the play it shifts directly into tragedy. This has led some to use the term \"tragi-comedy\" with this play. "
            //  By 1596 Shakespeare's income and status was growing briskly. He solidified this status by buying a huge manor house in Stratford and applying
    // + "_______ Further, <i>Romeo and Juliet</i> remains the play most often taught in schools around the world. ______  to the Heraldic College for a <i>coat of arms</i>. ";
    // "This plan was successful and his status rose both at home and in London. Over the next four years he continued to act, write, and now became a shareholder of a theater. He was respected by the literary community of the day and was solidly a part of aristocratic society. By 1600 he had given six command performances in the Court of Queen Elizabeth.</p>";

    public static final String CONTENT="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\n" + 
			"";
	

    public static final String WESNOTH="H4sIAB/90FQAA7VUTW/TQBC951eMckBFahNV5QIkkSoKnJAQFCRO1doe29Pueqz9SJp/z8w6gbQE\n" +
            "t0JCkex49u2bzzeLitZQWhPCchqwjMTddLU4MEaKFnvT4GDePdqLB+dyZoCq5bQxDm9C6RGVZm5W\n" +
            "57ML+b3oitC/vW4RPgoAvmbAYt5eCCbzHT771SL0pts7oM5Shw4rMlzcSohCTK6B4MvB39ng7+x8\n" +
            "9nr2anbbN1MwNi6nB640FuWUV68OvmBjfGUxBOAaNi3GFj1sOYHxCL01W+oaMBBK7IwnBvbyVRrX\n" +
            "G2q6UxA4FCZQOREop6gsatNwYAgHKGRTENMMNHVnbtlT3O7Bv3E1WYvVZEOxFTfO9BISlS2EljdB\n" +
            "krH7KyY3SP6aCNHcYdBYSwRSG06a7OvSc+qqjFcqzWitSaQAaKWQXQw7/t7zmiqEFLBOVlhq9s6o\n" +
            "i4kpNK1fORkhVKIKJWoqsFKXjrMlGrJQoOXNbFfe5/cvcn8jWPyjedfcw2c5sI9bd1l6DkNp5fKj\n" +
            "WtaeHVisJXIGT00bc9SKqNlKhNpWiujCm0x3MOfsK/RYWQoaINtjdojbHpfTcwFY2gP0RCmnq8kn\n" +
            "7BIUKUYp4GJu6S+wy9zF8AzkdfJSR2lnlPE8KZP30j2IYp07c08uOeiSK+RQ6yDm8HKM7gcnDw3b\n" +
            "agz0XYZR5B7gZKvw9e5zHjkae+Buf/C0x+Fm6iiGJ7Gpv0Psn4RRV7LDMdi7fbFIxlfkm995NE5U\n" +
            "LfJVgUs2kqpdMtIxH89lz9jiPfQcKEvx5P6sZJkR6kzEU9gefI2SXWGNXTkIy/E6y3I/zLs+2y0E\n" +
            "EWwZJVItHmTlq6j9nZgkjOdGq2O7w87Z/suezVo6rtQvWWZHtXrFmyHkQYpHtKoSFqkWLEpwqtX/\n" +
            "ossPSVao7MJT8W10045JLs9LDZXZypRVVJrIfuzCN22NbFJZ4rplPAgiPuzc2PX3MgBZ5g+2wUGb\n" +
            "8nPyE9AE/naoBwAA";
}
