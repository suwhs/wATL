package su.whs.watl.samples;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import su.whs.utils.FileUtils;
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

    private static Map<String,String> mArticles = new HashMap<String,String>();
    static {
        mArticles.put("lorem", null);
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
                } else {
                    return "Science article 2";
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

        public void load(final Context context, ArticleView view) throws Exception {
            final String content;
            final Html.ImageGetter imageGetter;

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
                        Drawable result = new BitmapDrawable(context.getResources(), bitmap);
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
                            Drawable result = new BitmapDrawable(context.getResources(), bitmap);
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
                            return new AssetGifDrawable(context,source);
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
