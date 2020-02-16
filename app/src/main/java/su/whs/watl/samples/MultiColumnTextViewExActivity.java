package su.whs.watl.samples;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;

import su.whs.hyphens.HyphenLineBreaker;
import su.whs.watl.text.HtmlTagHandler;
import su.whs.watl.ui.MultiColumnTextViewEx;


public class MultiColumnTextViewExActivity extends ActionBarActivity {
    private TextOptionsHandler opts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_column_text_view_ex);
        MultiColumnTextViewEx tv = (MultiColumnTextViewEx) findViewById(R.id.textView);
        Typeface font = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/GamjaFlower-Regular.ttf");
        tv.setTypeface(font);
        opts = new TextOptionsHandler(this,tv);
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
        // tv.setColumnLimits(300, 600); // overrides attributes in xml
        tv.setText(text);
        tv.setTextIsSelectable(true);
        if (Build.VERSION.SDK_INT>10)
            tv.setCustomSelectionActionModeCallback(new SampleActionModeCallback(tv));
        tv.getOptions().setLineBreaker(HyphenLineBreaker.getInstance(this,"en_us")).setFilterEmptyLines(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.text_options, menu);
        opts.restoreState(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (opts.onOptionsItemSelected(item))
            return true;
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
