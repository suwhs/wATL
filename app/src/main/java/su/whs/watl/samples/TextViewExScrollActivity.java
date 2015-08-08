package su.whs.watl.samples;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import su.whs.watl.ui.TextViewEx;


public class TextViewExScrollActivity extends ActionBarActivity implements ArticleView {
    private TextOptionsHandler opts;
    private static final String[] articles = new String[] {
            ContentLoader.ARTICLE_SCIENCE1,
            ContentLoader.ARTICLE_SCIENCE2,
            ContentLoader.ARTICLE_OPENGLES,
    };
    private int currentArticle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view_ex_scroll);

        TextViewEx tv = (TextViewEx) findViewById(R.id.textView);
        ((wATLApp)getApplication()).getArticle(articles[currentArticle],this);
        currentArticle++;
        opts = new TextOptionsHandler(this,tv.getOptions());
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

    @Override
    public void setContent(String title, String author, String source, CharSequence content) {
        TextViewEx tv = (TextViewEx) findViewById(R.id.textView);
        tv.setText(content);
        tv.setTextIsSelectable(true);
        if (Build.VERSION.SDK_INT>10)
            tv.setCustomSelectionActionModeCallback(new SampleActionModeCallback(tv));
    }
}
