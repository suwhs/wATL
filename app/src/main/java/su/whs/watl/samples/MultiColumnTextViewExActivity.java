package su.whs.watl.samples;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;

import su.whs.watl.text.HtmlTagHandler;
import su.whs.watl.text.HyphenLineBreaker;
import su.whs.watl.text.hyphen.HyphenPattern;
import su.whs.watl.text.hyphen.PatternsLoader;
import su.whs.watl.ui.MultiColumnTextViewEx;


public class MultiColumnTextViewExActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_column_text_view_ex);
        MultiColumnTextViewEx tv = (MultiColumnTextViewEx) findViewById(R.id.textView);

        CharSequence text = Html.fromHtml(SampleContent.get(), new Html.ImageGetter() {
            /**
             * load images from assets/ folder
             * @param source - usually value for 'src' attribute of <img> tag
             * @return Drawable object
             */
            @Override
            public Drawable getDrawable(String source) {
                Context ctx = getApplicationContext();
                AssetManager assetManager = ctx.getAssets();

                InputStream is = null;
                try {
                    is = assetManager.open(source);
                } catch (IOException e) {
                    return null;
                }
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Drawable result = new BitmapDrawable(getResources(), bitmap);
                result.setBounds(0, 0, result.getIntrinsicWidth(),
                        result.getIntrinsicHeight());
                return result;
            }
        }, new HtmlTagHandler());
        tv.setColumnLimits(300, 600);
        tv.setText(text);
        tv.setTextIsSelectable(true);
        tv.setCustomSelectionActionModeCallback(new SampleActionModeCallback(tv));
        HyphenPattern pat = PatternsLoader.getInstance(this).getHyphenPatternAssets("en_us.hyphen.dat");
        tv.getOptions().setLineBreaker(HyphenLineBreaker.getInstance(pat));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multi_column_text_view_ex, menu);
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
