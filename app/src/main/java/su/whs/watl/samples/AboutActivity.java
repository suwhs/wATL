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
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import su.whs.watl.text.HtmlTagHandler;
import su.whs.watl.text.hyphen.HyphenPattern;
import su.whs.watl.ui.TextViewEx;


public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextViewEx tv = (TextViewEx) findViewById(R.id.textView);

        Context ctx = getApplicationContext();
        AssetManager assetManager = ctx.getAssets();

        InputStream is = null;
        CharSequence text = "error reading about.html";

        try {
            is = assetManager.open("about.html");
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(is, "utf-8"));
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            String str;
            int emptyLinesCount = 0;
            while ((str = in.readLine()) != null) {
                if (str.length() < 1)
                    emptyLinesCount++;
                if (emptyLinesCount > 1)
                    ssb.append('\n');

                if (str.length() > 0) {
                    ssb.append(str);
                    ssb.append(" ");
                    emptyLinesCount = 0;
                }
            }

            text = Html.fromHtml(ssb.toString(), new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    if ("logo.png".equals(source)) {
                        Drawable result = getResources()
                                .getDrawable(R.mipmap.ic_logo);
                        result.setBounds(0, 0, result.getIntrinsicWidth(),
                                result.getIntrinsicHeight());
                        return result;
                    }
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tv.setText(text);
        tv.setTextIsSelectable(true);
        tv.setCustomSelectionActionModeCallback(new SampleActionModeCallback(tv));
        tv.getOptions().setImagePlacementHandler(null);
        tv.getOptions().setLineBreaker(HyphenLineBreaker.getInstance(HyphenPattern.EN_US));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
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
