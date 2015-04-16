package su.whs.watl.samples;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import su.whs.watl.text.HyphenLineBreaker;
import su.whs.watl.text.hyphen.HyphenPattern;
import su.whs.watl.text.hyphen.PatternsLoader;
import su.whs.watl.ui.TextViewEx;


public class HyphenTextViewExActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hyphen_text_view_ex);
        TextViewEx tv = (TextViewEx) findViewById(R.id.textView);
        CharSequence text = Html.fromHtml(SampleContent.get(), new Html.ImageGetter() {

            @Override
            public Drawable getDrawable(String source) {
                Drawable result = getResources()
                        .getDrawable(R.drawable.pinfish_small);
                result.setBounds(0, 0, result.getIntrinsicWidth(),
                        result.getIntrinsicHeight());
                return result;
            }
        }, null);
        tv.setText(text);
        tv.setTextIsSelectable(true);
        tv.setCustomSelectionActionModeCallback(new SampleActionModeCallback(tv));
        HyphenPattern pat = PatternsLoader.getInstance(this).getHyphenPatternAssets("en_us.hyphen.dat");
        tv.getOptions()
                .setLineBreaker(HyphenLineBreaker.getInstance(pat))
                .setNewLineLeftMargin(20)
                .setNewLineTopMargin(8);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hyphen_text_view_ex, menu);
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
}
