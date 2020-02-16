package su.whs.watl.samples;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;

import java.text.Bidi;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import su.whs.watl.ui.TextViewEx;


public class RTLTestActivity extends ActionBarActivity implements ArticleView {
    private TextOptionsHandler opts;
    private static final String[] articles = new String[] {
            ContentLoader.RTL_TEST,
            ContentLoader.ARTICLE_SCIENCE2,
            ContentLoader.ARTICLE_OPENGLES,
    };
    private int currentArticle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view_ex_scroll);

        TextViewEx tv = (TextViewEx) findViewById(R.id.textView);
        ((wATLApp)getApplication()).getArticle(ContentLoader.RTL_TEST,this);
        opts = new TextOptionsHandler(this,tv);
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
//        if (id == R.id.test_set_text) {
//            if (currentArticle>2) currentArticle=0;
//            ((wATLApp)getApplication()).getArticle(articles[currentArticle],this);
//            // currentArticle++;
//            return true;
//        }

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
        tv.setText(workaround(content)); //
        tv.setTextIsSelectable(true);
        if (Build.VERSION.SDK_INT>10)
            tv.setCustomSelectionActionModeCallback(new SampleActionModeCallback(tv));
    }

    public Spanned workaround(CharSequence string) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(string);
        Bidi bidi = new Bidi(string.toString(), Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
        List<Integer> inserts = new ArrayList<Integer>();
        if (bidi.isMixed()) {
            for (int run = 0; run < bidi.getRunCount(); run++) {
                int end = bidi.getRunLimit(run);
                inserts.add(end);
            }
            Collections.reverse(inserts);
            for (int pos : inserts) ssb.insert(pos,"\n");
        }
        return ssb;
    }
}
