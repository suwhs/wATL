package su.whs.watl.samples;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import su.whs.utils.FileUtils;
import su.whs.watl.text.BaseTextPagerAdapter;
import su.whs.watl.text.HtmlTagHandler;
import su.whs.watl.text.ImagePlacementHandler;
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
    private class ArticlesPagesAdapter extends BaseTextPagerAdapter {

        public ArticlesPagesAdapter() {
            super(R.id.contentTextView);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_flow);
        mPager = (ViewPager) findViewById(R.id.viewPager);
        loadArticles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_articles_flow, menu);
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
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    /*
    * load articles in backround
    *
    *
    */
    private void loadArticles() {
        new AsyncTask<Void,Void,Void>() {
            private SpannableStringBuilder mText = new SpannableStringBuilder();
            private SparseArray<String> mArticles = new SparseArray<String>();

            @Override
            protected void onPreExecute() {


            }

            @Override
            protected Void doInBackground(Void... params) {
                List<String> fileNames = listArticles();
                for (String fileName : fileNames) {
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
                    ClickableSpan[] clickableSpans = mText.getSpans(0, mText.length(), ClickableSpan.class);
                    mArticles.put(mText.length(),fileName);
                    mText.append(content);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                ArticlesFlowActivity.this.mArticles = mArticles;
                mAdapter.getOptions().setImagePlacementHandler(new ImagePlacementHandler.DefaultImagePlacementHandler());
                mAdapter.setText(mText);

                mPager.setAdapter(mAdapter);
                mPager.setOnPageChangeListener(ArticlesFlowActivity.this);
            }

            private String readArticle(String name) {
                try {
                    return FileUtils.convertStreamToString(getBaseContext().getAssets().open("articles/"+name));
                } catch (Exception e) {
                    return "<strong>error reading article</strong>";
                }
            }

            private List<String> listArticles() {
                return new ArrayList(mTitles.keySet());
            }

        }.execute();
    }
}
