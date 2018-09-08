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
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;

import su.whs.hyphens.HyphenLineBreaker;
import su.whs.watl.text.HtmlTagHandler;
import su.whs.watl.ui.TextViewEx;

/**
 * TextViewEx with HyphenLineBreaker sample
 * also includes:
 *  - ImageGetter sample for loading images from assets/ folder
 *  - HtmlTagHandler() usage
 */
public class AnotherHyphenTextViewExActivity extends ActionBarActivity {

    private TextOptionsHandler opts;
    private CharSequence text;
    private TextViewEx tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hyphen_text_view_ex);
        tv = (TextViewEx) findViewById(R.id.textView);
        Typeface font = Typeface.createFromAsset(tv.getContext().getAssets(), "fonts/GamjaFlower-Regular.ttf");
        tv.setTypeface(font);
        /**
         * use stock Html.fromHtml with custom tag handler and image loader (loads images from assets folder)
         * **/
        text = Html.fromHtml(SampleContent.wesnoth(), new Html.ImageGetter() {
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

        tv.setText(text);
        tv.setTextIsSelectable(true);
        if (Build.VERSION.SDK_INT>10)
            tv.setCustomSelectionActionModeCallback(new SampleActionModeCallback(tv));

        /** create HyphenLineBreaker with preloaded patterns and assign it to TextViewEx **/
        tv.getOptions().setLineBreaker(HyphenLineBreaker.getInstance(this,"en_us"));
        /* screenshoting */
        tv.getOptions()
                /** configure line spacing multiplier **/
                .setLineSpacingMultiplier(0.5f)
                /** reduce line spacing by 4px **/
                .setLineSpacingAdd(-4)
                /** set paragraph first line margin to left **/
                .setNewLineLeftMargin(25)
                /** configure text paddings **/
                .setTextPaddings(5,4,5,4);

        opts = new TextOptionsHandler(this,tv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_hyphen_text_view_ex, menu);
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
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.test_set_text) {
            tv.setText(text);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putBundle("OPTIONS", tv.getOptions().getState());
    }

    @Override
    public void onRestoreInstanceState(Bundle in) {
        super.onRestoreInstanceState(in);
        if (in.containsKey("OPTIONS")) {
            tv.getOptions().set(in.getBundle("OPTIONS"));
        }
    }
}
