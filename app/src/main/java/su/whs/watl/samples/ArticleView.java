package su.whs.watl.samples;

/**
 * Created by igor n. boulliev on 12.06.15.
 */
public interface ArticleView {
    void setLoadingState(boolean state, int percents);
    void setContent(String title, String author, String source, CharSequence content);
}
