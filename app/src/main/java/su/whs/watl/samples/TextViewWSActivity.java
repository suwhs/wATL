package su.whs.watl.samples;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import su.whs.watl.ui.TextViewWS;


public class TextViewWSActivity extends ActionBarActivity implements ArticleView {
    private static final String TAG="TextViewWS.Demo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view_ws);
        ((wATLApp)getApplication()).getArticle(ContentLoader.ARTICLE_SCIENCE2, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_view_w, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setLoadingState(boolean state, int percents) {

    }

    @Override
    public void setContent(String title, String author, String source, CharSequence content) {
        TextViewWS tv = (TextViewWS) findViewById(R.id.textView);
        Typeface font = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/GamjaFlower-Regular.ttf");
        tv.setTypeface(font);
        tv.setText(content);

        tv.setTextIsSelectable(true);
        if (Build.VERSION.SDK_INT > 10)
            tv.setCustomSelectionActionModeCallback(new SampleActionModeCallback(tv));

    }
}
