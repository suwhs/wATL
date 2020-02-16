package su.whs.watl.samples;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import su.whs.watl.ui.TextViewEx;


public class TextViewExScrollActivity extends ActionBarActivity implements ArticleView {
    /** helper for option menu bindings to TextViewEx options **/
    private TextOptionsHandler opts;
    /** articles variants **/
    private static final String[] articles = new String[] {
            ContentLoader.ARTICLE_SCIENCE1,
            ContentLoader.ARTICLE_SCIENCE2,
            ContentLoader.ARTICLE_OPENGLES,
    };
    private int currentArticle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** activity layout **/
        setContentView(R.layout.activity_text_view_ex_scroll);
        /** find TextViewEx **/
        TextViewEx tv = (TextViewEx) findViewById(R.id.textView);
        Typeface font = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/GamjaFlower-Regular.ttf");
        tv.setTypeface(font);
        /**
         * use article loader to assign content to TextViewEx
         * see setContent() method for this activity
         * **/
        ((wATLApp)getApplication()).getArticle(articles[currentArticle],this);
        currentArticle++;
        /** initialize option menu bindings **/
        opts = new TextOptionsHandler(this,tv);
        /** turn on empty lines elimination **/
        tv.getOptions().setFilterEmptyLines(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_view_ex_scroll, menu);
        opts.restoreState(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (opts.onOptionsItemSelected(item))
            return true;
        //noinspection SimplifiableIfStatement
        if (id == R.id.test_set_text) {
            if (currentArticle>2) currentArticle=0;
            ((wATLApp)getApplication()).getArticle(articles[currentArticle],this);
            currentArticle++;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        TextViewEx tv = (TextViewEx) findViewById(R.id.textView);
        out.putBundle("OPTIONS",tv.getOptions().getState());
    }

    @Override
    public void onRestoreInstanceState(Bundle in) {
        super.onRestoreInstanceState(in);
        if (in.containsKey("OPTIONS")) {
            TextViewEx tv = (TextViewEx) findViewById(R.id.textView);
            tv.getOptions().set(in.getBundle("OPTIONS"));
        }
    }

    @Override
    public void setLoadingState(boolean state, int percents) {

    }

    /**
     * called when Article content ready
     * @param title - title of article
     * @param author - author
     * @param source - source url
     * @param content - content
     */

    @Override
    public void setContent(String title, String author, String source, CharSequence content) {
        TextViewEx tv = (TextViewEx) findViewById(R.id.textView);
        /** setText() with given content **/
        tv.setText(content);
        /** enable selection **/
        tv.setTextIsSelectable(true);
        if (Build.VERSION.SDK_INT>10) // if android version > 3.0 - turn on action mode
            tv.setCustomSelectionActionModeCallback(new SampleActionModeCallback(tv));
    }
}
