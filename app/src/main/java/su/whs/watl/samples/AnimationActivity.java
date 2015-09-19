package su.whs.watl.samples;

/**
 * Created by igor n. boulliev on 31.08.15.
 */


import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.style.DynamicDrawableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import su.whs.watl.text.DynamicDrawableInteractionListener;
import su.whs.watl.text.ImagePlacementHandler;
import su.whs.watl.ui.TextViewEx;


public class AnimationActivity extends ActionBarActivity implements ArticleView {
    private TextOptionsHandler opts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view_ex_scroll);

        TextViewEx tv = (TextViewEx) findViewById(R.id.textView);
        ((wATLApp)getApplication()).getArticle("gif_animation",this);
        opts = new TextOptionsHandler(this,tv);
        tv.getOptions()
                .setDrawableMinimumScaleFactor(1.0f)
                .setDrawablePaddings(10,10,10,10)
                .setDrawableWrapRatioTreshold(.2f)
                .setImagePlacementHandler(new ImagePlacementHandler.DefaultImagePlacementHandler());
        tv.setDynamicDrawableInteractionListener(new DynamicDrawableInteractionListener() {
            @Override
            public void onClicked(DynamicDrawableSpan span, Rect bounds, View view) {
                Drawable drawable = span.getDrawable();
                if (drawable instanceof Animatable) {
                    if (!((Animatable)drawable).isRunning())
                        ((Animatable)drawable).start();
                    else
                        ((Animatable)drawable).stop();
                }
            }

            @Override
            public void onLongClick(DynamicDrawableSpan span, RectF bounds, View view) {

            }
        });
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
            ((wATLApp)getApplication()).getArticle("gif_animation",this);
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
