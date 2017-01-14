package su.whs.hyphens;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import su.whs.syllabification.parent.LineBreaker;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("su.whs.hyphens.test", appContext.getPackageName());
    }

    @Test
    public void testIsPunctionation() {

        for(int i=0; i<P0.length(); i++) {
            char ch = P0.charAt(i);
            assertEquals(true,ReverseLookupHyphenator.isPunktuation(ch));
        }
    }
    @Test
    public void testHyphenator() {
        InputStreamPatternLoader loader = new InputStreamPatternLoader();
        HyphenPattern pattern = loader.getHyphenPattern("/hyphenation-rules/en_us.hyphen.dat");
        assertNotNull(pattern);
        ReverseLookupHyphenator hyphenator = new ReverseLookupHyphenator(pattern);
        testHyphenator(hyphenator,TS0,VS0);
        testHyphenator(hyphenator,TS1,VS1);
        testHyphenator(hyphenator,TS2,VS2);
        testHyphenator(hyphenator,TS3,VS3);
        pattern = loader.getHyphenPattern("/hyphenation-rules/ru.hyphen.dat");
        hyphenator = new ReverseLookupHyphenator(pattern);
        testHyphenator(hyphenator,TS4,VS4);
    }

    private void testHyphenator(ReverseLookupHyphenator hyphenator, String source, String valids) {
        assertEquals(source.length(), valids.length());
        char[] text = new char[source.length()];
        source.getChars(0,source.length(),text,0);
        List<Integer> stack = new ArrayList<Integer>();
        List<Integer> results = new ArrayList<Integer>();
        int previousBreakPosition = -1;
        for(int i=0; i<valids.length(); i++) {
            char ch = valids.charAt(i);
            int breakPoint = hyphenator.nearestLineBreak(text,0,i,text.length);
            if (previousBreakPosition!= breakPoint && LineBreaker.isHyphen(breakPoint)) {
                stack.add(0,i);
                previousBreakPosition = breakPoint;
            } else if (breakPoint!=previousBreakPosition) {
                results.add(i);
                previousBreakPosition = breakPoint;
            }
            int pos = LineBreaker.getPosition(breakPoint);
            if (pos<0 || pos>source.length()) {
                throw new RuntimeException(String.format(
                        "%s\nbreak position = %d\ni=%d, ch='%s'\n", source, pos, i, ch
                ));
            }


            char test = valids.charAt(pos);

            if (test=='I') {
                throw new RuntimeException(String.format(
                        "%s\nbreak position = %d\ni=%d, ch='%s'\n", source, pos, i, ch
                ));
            } else if (test=='H' && !LineBreaker.isHyphen(breakPoint)) {
                throw new RuntimeException(String.format(
                        "%s\nbreak position = %d\ni=%d, ch='%s' - expcted HYPHEN\n", source, pos, i, ch
                ));
            }
        }
        StringBuilder sb = new StringBuilder(source);
        for(int p : stack) {
            sb.insert(p,'-');
        }
        System.out.printf("%s\n",sb.toString());
        sb = new StringBuilder(source);
        for(int i=0; i< source.length(); i++)
            sb.replace(i,i+1,"I");
        for(int p : stack) {
            sb.replace(p,p+1,"H");
        }
        for(int p : results) {
            sb.replace(p,p+1,"V");
        }
        System.out.printf("%s\n",sb.toString());
    }


    /**
     * tests
     **/

    //           0         1         2         3         4         5         6         7
    static String TS0 = "Hello, world. 1234567, and 1002.045 and p.193 (A4320.4) and 23/45.";
    static String VS0 = "VVVVVIVVVVVVIVVIIIIIIIVVVVVVIIIIIIIVVVVVVIIIIVVIIIIIIIIVVVVVVIIIII";
    static String TS1 = "Transforming nerve cells into light-sensing cells aims to restore sight in some blind patients.";
    static String VS1 = "VIIIIHIIIHIIVVIIIIVVIIIIVVIIIVVIIIVIVIIIHIIVVIIIIVVIIIVVIVVIIIIIIVVIIIIVVIVVIIIVVIIIIVVIIIIIIII";
    static String TS2 = "This simple form is powered by hyphen library, a part of hunspell project.";
    static String VS2 = "VIIIVVIIHIIVVIIIVVIVVIIVIIIVVIVVVVIIIVVVVIIIIIVVVVIIIVVIVVIVVIIIIVVIVVIIII";
    static String TS3 = "Biographers attempting to account for this period have reported many apocryphal stories. Nicholas Rowe, Shakespeare's first biographer, recounted a Stratford legend that Shakespeare fled the town for London to escape prosecution for deer poaching in the estate of local squire Thomas Lucy. Shakespeare is also supposed to have taken his revenge on Lucy by writing a scurrilous ballad about him.";
    static String VS3 = "VIIIHIHIIIIVVIIIIIIHIIVVIVVIIIIIIVVIIVVIIIVVIIIHIVVIIIVVIIIIIHIVVIIIVVIIIHIIIIIVVIIHIIIVVVIIIIIIIVVIIIIVVIIIIHIIIIIVHVVIIIIVVIIIHIHIHIVVVIIIIIIHIVVVVIIIIHIIIVVIIHIIVVIIIVVIIIIHIIIIIVVIIIVVIIVVIIIVVIIVVIIHIIVVIVVIIIIIVVIIIHHIHIIIVVIIVVIIIVVIIIIHIIVVIVVIIVVIIIIIVVIVVIIIIVVIIIIIVVIIIIIVVIIIIVVIIIIHIIIIIVVIVVIIIVVIIHIIIIVVIVVIIIVVIIHIVVIIVVIIIIIIVVIVVIIIVVIVVIIIHIIVVVVIIIHIIIIIVVIIHIIVVIIIIVVIIV";
    static String TS4 = "Хотя не сохранилось записей о посещении, Шекспир вероятно получил образование в новой королевской школе в Стратфорде,[1] бесплатной школе, удалённой на около четверти мили от его дома. Школы грамоты елизаветинской эпохи варьировали качество обучения, но оно было стандантизировано королевским указом,[2] и школа точно должна была дать сильное образование в области латинской грамматики и литературы. Частью обучения была постановка учениками латинской пьесы для лучшего понимания языка.\n" +
            "В период, когда Шекспир вероятно постоянно проживал в Стратфорде, театральные труппы посетили город по меньшей мере 12 раз, в том числе 2 раза выступая перед чиновниками, в числе которых был отец Шекспира, который, как судебный пристав, должен был до выступления труппы пролицензировать её.";
    static String VS4 = "VIIIVVIVVIIIIHIHIIIVVIIIHIIVVVVIIIHIHIHVVVIIIHIHVVIIIHIHIVVIIIHIHVVIIIHIHIHIHVVVVIIIIVVIIIHIIHIIIVVIIHIVVVVIIIIHIIHIVVVIVVIIHIIIHIIVVIIHIVVVIIHIIHIIVVIVVIIHIVVIIHIIHIVVIIIVVIVVIIVVIIIIVVIIHIVVIIHIHIVVIIHIHIHIIHIIIVVIIHIVVIIIIHIHIHIVVIIIHIIIVVIIHIHIHVVVIVVIIVVIIIVVIIIHIIHIHIHIHIHIVVIIIHIIHIIHVVIIHIHVVVIVVVVIIHIVVIIHIVVIIIHIVVIIIVVIIIVVIIIHIHVVIIIHIHIHIHVVVVIIIHIIVVIIIIHIIIVVIIIHIHIHIVVVVIIIHIHIHIVVVIIIIIVVIIHIHIHVVIIIVVIIIIHIIHIVVIIHIHIHIVVIIIIHIIIVVIIHIVVIIVVIIHIHIVVIIIHIHIHVVIIHIVVVVVIIIHIVVVIIIIVVIIIHIHVVIIIHIHIVVIIIIHIHIVVIIHIHIIVVVVIIIIHIIHIVVVIIIHIIIHIHVVIIIHIVVIIIHIHIVVIIIIVVIVVIIIHIIVVIIIVVIVVIIVVVVVIIVVIIHIVVVVIIIVVIIIIHIHVVIIIIVVIIIIHIHIHIVVVVVIIHIVVIIIHIIVVIIVVIIHVVIIIHIHIVVVIIIHIIVVVIIVVIIIIHIIVVIIHIIIVVVIIHIIVVIIVVIVVIIIIIHIHIHVVIIIHIVVIIHIHIIHIHIHIIIVVIV";


    static String P0 = ",.!?#$%()'\"";
    //                   1 3 5 7 9 B D F H J L N P R T V X Z

    @SuppressWarnings(value = "unused")
    static String stubValids(String source) {
        StringBuilder sb = new StringBuilder(source);
        for(int i=0; i<sb.length();i++) sb.replace(i,i+1,"V");
        return sb.toString();
    }
}
