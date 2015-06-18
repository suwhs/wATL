package su.whs.watl.samples;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import su.whs.utils.FileUtils;
import su.whs.watl.text.BaseTextPagerAdapter;
import su.whs.watl.text.HtmlTagHandler;
import su.whs.watl.text.HyphenLineBreaker;
import su.whs.watl.text.ITextPagesNumber;
import su.whs.watl.text.hyphen.HyphenPattern;
import su.whs.watl.text.hyphen.PatternsLoader;
import su.whs.watl.ui.MultiColumnTextViewEx;

public class ArticlesFlowActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {

    private ViewPager mPager;
    private SparseArray<String> mArticles;
    private Map<String,String> mTitles = new HashMap<String,String>();
    {
        mTitles.put("opengles1.html","Android OpenGl ES Tutorial");
        mTitles.put("science1a.html","How to rewire the eye");
        mTitles.put("science2a.html","These birds provide their own drum beat");
    }
    private class IndicatorHolder {
        TextView number;
        TextView total;
        TextView title;
        int _n = -1;
        int _t = -1;
        public IndicatorHolder(View page) {
            number = (TextView) page.findViewById(R.id.pageNo);
            total = (TextView) page.findViewById(R.id.pagesTotal);
            page.setTag(this);
        }
        public void set(int n, int t) {
            if (_n==n && _t==t) return;
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

    private class ArticlesPagesAdapter extends BaseTextPagerAdapter {

        public ArticlesPagesAdapter() {
            super(R.id.contentTextView, mPagesIndicator);
        }

        @Override
        public View getViewForPage(int position) {
            /* just inflate layout contains textviewex-derived view with id=@android:id/content */
            View layout = LayoutInflater
                    .from(ArticlesFlowActivity.this)
                    .inflate(R.layout.article_page_view, null, false);
            MultiColumnTextViewEx tve = (MultiColumnTextViewEx) layout.findViewById(R.id.contentTextView);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                tve.setColumnsCount(2);
            return layout;
        }
    }

    private ArticlesPagesAdapter mAdapter = new ArticlesPagesAdapter();
    private TextOptionsHandler mOptionsHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_flow);
        mPager = (ViewPager) findViewById(R.id.viewPager);
        mPager.setPageMargin(2);
        mPager.setPageMarginDrawable(android.R.color.background_dark);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
            mPager.setPageTransformer(false, new ReaderViewPagerTransformer(ReaderViewPagerTransformer.TransformType.SLIDE_OVER));
        mOptionsHandler = new TextOptionsHandler(mAdapter.getOptions());
        loadArticles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.text_options, menu);
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
    public void onPageScrollStateChanged(int i) {

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
                return loadArticle("opengles1.html");
            }

            @Override
            protected void onPostExecute(CharSequence result) {
                ArticlesFlowActivity.this.mArticles = mArticles;
                HyphenPattern pat = PatternsLoader.getInstance(getBaseContext()).getHyphenPatternAssets("en_us.hyphen.dat");
                mAdapter.getOptions()
                        // .setImagePlacementHandler(new ImagePlacementHandler.DefaultImagePlacementHandler())
                        .setLineBreaker(HyphenLineBreaker.getInstance(pat));
                mAdapter.setText(result);
                mPager.setAdapter(mAdapter);
                mPager.setOnPageChangeListener(ArticlesFlowActivity.this);
            }



            private List<String> listArticles() {
                return new ArrayList(mTitles.keySet());
            }

        }.execute();
    }
}
