package su.whs.watl.samples;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import su.whs.watl.ui.TextViewEx;


public class TextViewExScrollActivity extends ActionBarActivity {
    private TextOptionsHandler opts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view_ex_scroll);

        TextViewEx tv = (TextViewEx) findViewById(R.id.textView);

        CharSequence text = Html.fromHtml(SampleContent.LOREM,new Html.ImageGetter() {

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
        if (Build.VERSION.SDK_INT>10)
            tv.setCustomSelectionActionModeCallback(new SampleActionModeCallback(tv));
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
            TextViewEx tv = (TextViewEx) findViewById(R.id.textView);
            CharSequence text = Html.fromHtml(SampleContent.LOREM,new Html.ImageGetter() {

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

}
