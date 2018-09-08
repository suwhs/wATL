package su.whs.watl.samples;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import su.whs.hyphens.HyphenLineBreaker;
import su.whs.utils.FileUtils;
import su.whs.watl.text.BaseTextPagerAdapter;
import su.whs.watl.text.HtmlTagHandler;
import su.whs.watl.text.ITextPagesNumber;
import su.whs.watl.ui.ITextView;
import su.whs.watl.ui.MultiColumnTextViewEx;

public class ViewPagerActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {
    private boolean mFullscreenMode = false;
    private ViewPager mPager;
    private SparseArray<String> mArticles;
    private Map<String,String> mTitles = new HashMap<String,String>();
    {
        mTitles.put("opengles1.html","Android OpenGl ES Tutorial");
        mTitles.put("science1a.html","How to rewire the eye");
        mTitles.put("science2a.html","These birds provide their own drum beat");
    }
    /** show current page/total pages counters **/
    private class IndicatorHolder {
        TextView number;
        TextView total;
        TextView title;
        View p;
        int _n = -1;
        int _t = -1;

        public IndicatorHolder(View page) {
            number = (TextView) page.findViewById(R.id.pageNo);
            total = (TextView) page.findViewById(R.id.pagesTotal);
            p = page;
            p.setTag(this);
        }
        public void set(int n, int t) {
            if (_n==n && _t==t) return;
            p.destroyDrawingCache();
            number.setText(String.format("%d",n+1));
            total.setText(String.format("%d",t));
            _n = n;
            _t = t;
        }
    }

    private ITextPagesNumber mPagesIndicator = new ITextPagesNumber() {
        @Override
        public void updateInfo(View page, int number, int total) {
            IndicatorHolder ih = (IndicatorHolder) page.getTag();
            if (ih == null)
                ih = new IndicatorHolder(page);
            ih.set(number,total);
        }
    };

    /** implements adapter with getViewForPage()
     * **/
    private class ArticlesPagesAdapter extends BaseTextPagerAdapter {

        public ArticlesPagesAdapter() {
            super(R.id.contentTextView, mPagesIndicator);
        }

        /**
         * basic example - just create instance of MultiColumnTextViewEx with 1 column in portrait mode and 2 columns in landscape mode
         *
         * @param position
         * @return
         */
        @Override
        public View getViewForPage(int position) {
            /* just inflate layout contains textviewex-derived view with id=@android:id/content */
            View layout = LayoutInflater
                    .from(ViewPagerActivity.this)
                    .inflate(R.layout.article_page_view, null, false);
            MultiColumnTextViewEx tve = (MultiColumnTextViewEx) layout.findViewById(R.id.contentTextView);
            // layout.setBackgroundColor(getResources().getColor(android.R.color.black)); // set background for buggy android versions
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                tve.setColumnsCount(2);
            // DEV setTypeface not working here
            return layout;
        }
    }

    private ArticlesPagesAdapter mAdapter = new ArticlesPagesAdapter();
    private TextOptionsHandler mOptionsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mFullscreenMode) {
            // getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_articles_flow);
        mPager = (ViewPager) findViewById(R.id.viewPager);
        mPager.setPageMargin(2);
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.GINGERBREAD_MR1)
            mPager.setPageMarginDrawable(new ColorDrawable(Color.BLACK));
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
            mPager.setPageTransformer(false,
                    new ReaderViewPagerTransformer(
                            ReaderViewPagerTransformer.TransformType.SLIDE_OVER
                    ));
        /** BaseTextPagerAdapter implements ITextView, so we can configure it with getOptions() **/
        mAdapter.getOptions()
                /* minimum scale factor for drawable to allow wrapping */
                .setDrawableMinimumScaleFactor(0.6f)
                /* eliminate empty lines */
                .setFilterEmptyLines(true)
                /* set paddings for text content */
                .setTextPaddings(5,5,5,5);

        Typeface font = Typeface.createFromAsset(mPager.getContext().getAssets(), "fonts/GamjaFlower-Regular.ttf");
//        mAdapter.getTextLayout().setTypeface(font);
        mAdapter.setTypeface(font);
        mOptionsHandler = new TextOptionsHandler(this,mAdapter);
        mPager.setAdapter(mAdapter);
        if (Build.VERSION.SDK_INT>10) {
            mAdapter.setCustomSelectionActionModeCallback(new PageActionModeCallback(mAdapter));
        }
        loadArticles();
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putBundle("ADAPTER", mAdapter.getOptions().getState());
    }

    @Override
    public void onRestoreInstanceState(Bundle in) {
        super.onRestoreInstanceState(in);
        if (in.containsKey("ADAPTER")) {
            mAdapter.getOptions().set(in.getBundle("ADAPTER"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.text_options, menu);
        mOptionsHandler.restoreState(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (mOptionsHandler.onOptionsItemSelected(item))
            return true;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // ViewPager BUG WORKAROUND
        if (state==ViewPager.SCROLL_STATE_IDLE) {
            for (int i=0; i<mPager.getChildCount(); i++) {
                View child = mPager.getChildAt(i);
                child.destroyDrawingCache();
            }
            mPager.invalidate();
        }
    }

    private String readArticle(String name) {
        try {
            return FileUtils.convertStreamToString(getBaseContext().getAssets().open("articles/"+name));
        } catch (Exception e) {
            return "<strong>error reading article</strong>";
        }
    }

    private CharSequence loadArticle(String fileName) {
        String article = readArticle(fileName);
        CharSequence content = Html.fromHtml(article, new Html.ImageGetter() {
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
                    is = assetManager.open("articles/" + source);
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
        return content;
    }
    /*
    * load articles in backround
    *
    *
    */
    private void loadArticles() {
        new AsyncTask<Void,Void,CharSequence>() {
            private SpannableStringBuilder mText = new SpannableStringBuilder();
            private SparseArray<String> mArticles = new SparseArray<String>();

            @Override
            protected void onPreExecute() {


            }

            @Override
            protected CharSequence doInBackground(Void... params) {
                return loadArticle("science1a.html" /*"opengles1.html"*/);
            }

            @Override
            protected void onPostExecute(CharSequence result) {
                ViewPagerActivity.this.mArticles = mArticles;
                mAdapter.getOptions()
                        // .setImagePlacementHandler(new ImagePlacementHandler.DefaultImagePlacementHandler())
                        .setLineBreaker(HyphenLineBreaker.getInstance(ViewPagerActivity.this,"en_us"));
                // mAdapter.getTextPaint().setColor(getResources().getColor(android.R.color.white));
                mAdapter.setText(result);
                mPager.setOnPageChangeListener(ViewPagerActivity.this);
            }

            private List<String> listArticles() {
                return new ArrayList(mTitles.keySet());
            }

        }.execute();
    }

    private class PageActionModeCallback implements android.support.v7.view.ActionMode.Callback {
        private ITextView mTextView;
        private PageActionModeCallback(ITextView textView) {
            mTextView = textView;
        }
        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            menu.add(0, android.R.id.copy, 0, "copy").setIcon(R.drawable.ic_action_name);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            // Remove the "select all" option
            menu.removeItem(android.R.id.selectAll);
            // Remove the "cut" option
            menu.removeItem(android.R.id.cut);
            // Remove the "copy all" option
            // menu.removeItem(android.R.id.copy);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.copy:
                    CharSequence text = mTextView.getText().subSequence(mTextView.getSelectionStart(), mTextView.getSelectionEnd());
                    if (mTextView instanceof View && Build.VERSION.SDK_INT>=11) {
                        Context context = ((View)mTextView).getContext();
                        ClipboardManager manager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            manager.setPrimaryClip(ClipData.newPlainText(null, text));
                        } else {

                        }
                        mTextView.setSelected(false);
                        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_LONG).show();
                    }
                    mode.finish();
                    return true;
                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {

        }
    }
}
