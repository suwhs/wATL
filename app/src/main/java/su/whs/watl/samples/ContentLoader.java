package su.whs.watl.samples;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import su.whs.utils.FileUtils;
import su.whs.watl.experimental.SpannedSerializator;
import su.whs.watl.samples.utils.ArticleSerializer;
import su.whs.watl.text.HtmlTagHandler;

/**
 * Created by igor n. boulliev on 07.06.15.
 */
public class ContentLoader {
    public static final String ARTICLE_LOREM_IPSUM = "lorem";
    public static final String ARTICLE_WORLD_OF_SHEAKSPEARE = "sheakspeare";
    public static final String ARTICLE_WESNOTH = "wesnoth";
    public static final String ARTICLE_OPENGLES = "opengles1";
    public static final String ARTICLE_SCIENCE1 = "science1";
    public static final String ARTICLE_SCIENCE2 = "science2";
    public static final String GIF_ANIMATION = "gif_animation";
    public static final String RTL_TEST = "rtl";
    private static Map<String,String> mArticles = new HashMap<String,String>();
    static {
        mArticles.put("lorem", null);
        mArticles.put("rtl","rtl_test_2.html");
        mArticles.put("sheakspeare", null);
        mArticles.put("wesnoth", null);
        mArticles.put("gif_animation",null);
        mArticles.put("opengles1","opengles1.html");
        mArticles.put("science1", "science1a.html");
        mArticles.put("science2", "science2a.html");
    }

    public List<String> list() {
        return new ArrayList(mArticles.keySet());
    }

    public Article get(String uuid) {
        return new Article(uuid);
    }

    public void cacheArticleAsSerializedSpanned(final Context context, Runnable onFinish) {
        for (final String uuid : mArticles.keySet()) {
            final Article a = get(uuid);
            if (isSerialized(context,uuid))
                removeCachedVersion(context,uuid);
            try {
                a.load(context, new ArticleView() {
                    @Override
                    public void setLoadingState(boolean state, int percents) {

                    }

                    @Override
                    public void setContent(String title, String author, String source, CharSequence content) {
                        cacheArticle(context,a.mUuid,title,author,source,content);
                    }
                });
            } catch (Exception e) {
                Log.e("CL.CACHE", "could not load article : " + uuid);
                // e.printStackTrace();
            }
        }
        new Handler(Looper.getMainLooper()).post(onFinish);
    }

    private void cacheArticle(Context context, String uuid, String title, String author, String source, CharSequence content) {
        ArticleSerializer as = new ArticleSerializer(context,(Spanned)content);
        File cache = context.getCacheDir();
        File article = new File(cache,uuid);
        if (article.exists()) {
            article.delete();
        }
        try {
            article.createNewFile();
        } catch (IOException e) {
            Log.e("ContentLoader","could not create cache file");
            return;
        }
        try {
            as.serialize(new DataOutputStream(new FileOutputStream(article)));
        } catch (IOException e) {
            Log.e("ContentLoader","could not write article to cache file");
            if (article.exists())
                article.delete();
        }
    }

    private boolean isSerialized(Context context, String uuid) {
        File cache = context.getCacheDir();
        File article = new File(cache,uuid);
        if (article.exists()) return true;
        return false;
    }

    void removeCachedVersion(Context context, String uuid) {
        File cache = context.getCacheDir();
        File article = new File(cache,uuid);
        if (article.exists()) article.delete();
    }

    public CharSequence loadSerialized(Context context, String uuid) throws IOException, SpannedSerializator.InvalidVersionException, SpannedSerializator.ReadError {
        File cache = context.getCacheDir();
        File article = new File(cache,uuid);
        DataInputStream dis = new DataInputStream(new FileInputStream(article));
        return ArticleSerializer.read(context,dis);
    }

    public class Article {
        private boolean mAssets = false;
        private String mUuid;
        private String mTitle;
        private String mAuthor;
        private String mSource;

        public Article(String uuid) {
            mUuid = uuid;
            if (mArticles.containsKey(uuid))
                mAssets = mArticles.get(uuid)!=null;
            else {
                mUuid = null;
            }
        }
        public String getTitle() {
            if (mAssets) {
                if (mUuid.equals("opengles1")) {
                    return "Android OpenGL ES Tutorial";
                } else if (mUuid.equals("science1")) {
                    return "Sciene article 1";
                } else if (mUuid.equals("science2")){
                    return "Science article 2";
                } else if (mUuid.equals("rtl")) {
                    return "RTL Test";
                } else {
                    return null;
                }
            } else {
                if (mUuid==null) {
                    return "invalid uuid";
                } else if (mUuid.equals("lorem")) {
                    return "Lorem Ipsum...";
                } else if (mUuid.equals("sheakspeare")) {
                    return "World of William Sheakspeare";
                } else if (mUuid.equals("wesnoth")) {
                    return "Battle for Wesnoth";
                } else if (mUuid.equals("gif_animation")) {
                    return "Gif Animation Sample";
                } else {
                    return "unknown article";
                }
            }
        }


        private void deserialize(Context context, ArticleView view) throws SpannedSerializator.InvalidVersionException, SpannedSerializator.ReadError, IOException {
            Spanned content = (Spanned) loadSerialized(context,mUuid);
            view.setLoadingState(false,100);
            view.setContent(mTitle, mAuthor, mSource, content);
            Log.v("CL.Article","content deserialized");
        }

        public void load(final Context context, ArticleView view) throws Exception {
            final String content;
            final Html.ImageGetter imageGetter;
            if (wATLApp.serializationEnabled && isSerialized(context,mUuid)) {
                try {
                    deserialize(context, view);
                    return;
                } catch (SpannedSerializator.InvalidVersionException e) {
                    e.printStackTrace();
                } catch (SpannedSerializator.ReadError readError) {
                    readError.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (mAssets) {
                content = FileUtils.convertStreamToString(context.getAssets().open("articles/"+mArticles.get(mUuid)));
                imageGetter = new Html.ImageGetter() {
                    /**
                     * load images from assets/ folder
                     * @param source - usually value for 'src' attribute of <img> tag
                     * @return Drawable object
                     */
                    @Override
                    public Drawable getDrawable(String source) {
                        AssetManager assetManager = context.getAssets();

                        InputStream is = null;
                        try {
                            is = assetManager.open("articles/"+source);
                        } catch (IOException e) {
                            return null;
                        }
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        Drawable result = new TaggedBitmapDrawable(context.getResources(), bitmap, "articles/"+source);
                        result.setBounds(0, 0, result.getIntrinsicWidth(),
                                result.getIntrinsicHeight());
                        return result;
                    }
                };
            } else {

                if (mUuid==null) {
                    view.setContent(null, null, null, "invalid article uuid");
                    return;
                }
                if (mUuid.equals("lorem")) {
                    content = SampleContent.LOREM;
                    imageGetter = new Html.ImageGetter() {

                        @Override
                        public Drawable getDrawable(String source) {
                            Drawable result = context.getResources()
                                    .getDrawable(R.drawable.pinfish_small);
                            result.setBounds(0, 0, result.getIntrinsicWidth(),
                                    result.getIntrinsicHeight());
                            return result;
                        }
                    };
                } else if (mUuid.equals("sheakspeare")) {
                    content = SampleContent.get();
                    imageGetter = null;
                } else if (mUuid.equals("wesnoth")) {
                    content = SampleContent.wesnoth();
                    imageGetter = new Html.ImageGetter() {
                        /**
                         * load images from assets/ folder
                         * @param source - usually value for 'src' attribute of <img> tag
                         * @return Drawable object
                         */
                        @Override
                        public Drawable getDrawable(String source) {;
                            AssetManager assetManager = context.getAssets();

                            InputStream is = null;
                            try {
                                is = assetManager.open(source);
                            } catch (IOException e) {
                                return null;
                            }
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            Drawable result = new TaggedBitmapDrawable(context.getResources(), bitmap,source);
                            result.setBounds(0, 0, result.getIntrinsicWidth(),
                                    result.getIntrinsicHeight());
                            return result;
                        }
                    };
                } else if (mUuid.equals("gif_animation")) {
                    content = FileUtils.convertStreamToString(context.getAssets().open("articles/gif_animation.html"));
                    imageGetter = new Html.ImageGetter() {
                        @Override
                        public Drawable getDrawable(String source) {
                            AssetGifDrawable dr = new AssetGifDrawable(context,source);
                            Drawable rr = context.getResources().getDrawable(
                                    su.whs.watl.R.mipmap.ic_play_circle_btn
                            );

                            dr.setPlayButtonDrawable(rr);
                            dr.setLoadingDrawable(rr);
                            return dr;
                        }
                    };
                } else {
                    content = "article not found";
                    imageGetter = null;
                }
            }

            CharSequence text = Html.fromHtml(content,imageGetter,new HtmlTagHandler());

            view.setContent(mTitle,mAuthor,mSource,text);
        }

    }
}
