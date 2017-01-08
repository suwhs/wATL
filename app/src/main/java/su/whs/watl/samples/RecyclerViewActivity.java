package su.whs.watl.samples;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import su.whs.watl.text.HtmlTagHandler;
import su.whs.watl.text.HyphenLineBreaker;
import su.whs.watl.text.hyphen.HyphenPattern;
import su.whs.watl.text.hyphen.PatternsLoader;
import su.whs.watl.ui.TextViewEx;
import su.whs.watl.ui.TextViewLayoutListener;

public class RecyclerViewActivity extends ActionBarActivity {

    RecyclerView mListView;
    CharSequence mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mListView = (RecyclerView) findViewById(R.id.listView);
        mListView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }

    @Override
    protected void onStart() {
        super.onStart();
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                mContent = makeSpanned();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mListView.setAdapter(makeAdapter());
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    HyphenPattern pat;

    RecyclerView.Adapter makeAdapter() {
        pat = PatternsLoader.getInstance(this).getHyphenPatternAssets("en_us.hyphen.dat");
        final Record[] records = new Record[10000];
        for (int i=0; i<records.length; i++)
            records[i] = makeFakeRecord(i);
        return new RecordsListAdapter(records) {

        };
    }

    Record makeFakeRecord(int position) {
        return new Record(position, makeTitle(position), selectRandomDrawable(), selectRandomText());
    }

    class Record {
        int mNumber;
        CharSequence mTitle;
        String mAsset;
        CharSequence mText;
        Record(int num, CharSequence title, String assetDrawable, CharSequence text) {
            mNumber = num;
            mTitle = title;
            mAsset = assetDrawable;
            mText = text;
        }
    }

    CharSequence makeTitle(int position) {
        return "Title ["+position+"]";
    }

    String selectRandomDrawable() {
        return "";
    }

    Random rnd = new Random();
    CharSequence selectRandomText() {
        Spanned spanned = (Spanned)mContent;
        int start = rnd.nextInt(spanned.length()-191);
        int length = rnd.nextInt(100);
        Object[] real = spanned.getSpans(start,start+90+length, Object.class);
        int min = start;
        for (Object ps : real) {
            int pstart = spanned.getSpanStart(ps);
            if (pstart<min && pstart>=0) min = pstart;
        }
        start = min;
        while(start>1 && spanned.charAt(start-1)!=' ' && spanned.charAt(start-1)!='.') start--;
        return spanned.subSequence(start,start+90+length);
    }

    int randomRgb() {
        return Color.rgb(rnd.nextInt(255),rnd.nextInt(255),rnd.nextInt(255));
    }
    class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextViewEx snippet;
        ImageView drawable;
        Record record;
        boolean layoutFinished=false;
        public ListItemViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            snippet = (TextViewEx) itemView.findViewById(R.id.snippet);
            drawable = (ImageView) itemView.findViewById(R.id.image);
            drawable.setImageDrawable(new ColorDrawable(randomRgb()));
            boolean isAsync = snippet.getOptions().isAsyncReflow();
            snippet.getOptions().setLineBreaker(HyphenLineBreaker.getInstance(pat));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("itemView","text:'"+record.mText+"'");
                    if (!layoutFinished) Toast.makeText(getBaseContext(),"layout not finished :(",Toast.LENGTH_SHORT).show();
                }
            });
            snippet.setTextViewLayoutListener(new TextViewLayoutListener() {
                @Override
                public void onLayoutFinished(int position) {
                    itemView.requestLayout();
                    layoutFinished = true;
                }
            });
        }

        public void bind(Record record) {
            this.record = record;
            title.setText(record.mTitle);
            snippet.setText(record.mText);
        }
    }

    class RecordsListAdapter extends RecyclerView.Adapter<ListItemViewHolder> {
        private Record[] mRecords;
        public RecordsListAdapter(Record[] records) {
            mRecords = records;
        }
        @Override
        public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.records_list_item,parent,false);
            return new ListItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ListItemViewHolder holder, int position) {
            holder.bind(mRecords[position]);
        }

        @Override
        public int getItemCount() {
            return mRecords.length;
        }
    }

    private CharSequence makeSpanned() {
        return Html.fromHtml(LOREM_IPSUM,new HtmlTagHandler(),null);
    }

    String LOREM_IPSUM="<h1>Num igitur eum postea censes anxio animo aut sollicito fuisse?</h1>\n" +
            "\n" +
            "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Negare non possum. Estne, quaeso, inquam, sitienti in bibendo voluptas? Quid enim est a Chrysippo praetermissum in Stoicis? Duo Reges: constructio interrete. Erit enim instructus ad mortem contemnendam, ad exilium, ad ipsum etiam dolorem. Nam quid possumus facere melius? </p>\n" +
            "\n" +
            "<pre>\n" +
            "Nam me ipsum huc modo venientem convertebat ad sese Coloneus\n" +
            "ille locus, cuius incola Sophocles ob oculos versabatur,\n" +
            "quem scis quam admirer quemque eo delecter.\n" +
            "\n" +
            "Vide, ne magis, inquam, tuum fuerit, cum re idem tibi, quod\n" +
            "mihi, videretur, non nova te rebus nomina inponere.\n" +
            "</pre>\n" +
            "\n" +
            "\n" +
            "<ol>\n" +
            "\t<li>Beatus autem esse in maximarum rerum timore nemo potest.</li>\n" +
            "\t<li>Sin te auctoritas commovebat, nobisne omnibus et Platoni ipsi nescio quem illum anteponebas?</li>\n" +
            "\t<li>Si ad corpus pertinentibus, rationes tuas te video compensare cum istis doloribus, non memoriam corpore perceptarum voluptatum;</li>\n" +
            "\t<li>Quae iam oratio non a philosopho aliquo, sed a censore opprimenda est.</li>\n" +
            "\t<li>At cum de plurimis eadem dicit, tum certe de maximis.</li>\n" +
            "\t<li>Videsne, ut haec concinant?</li>\n" +
            "</ol>\n" +
            "\n" +
            "\n" +
            "<p>Ita relinquet duas, de quibus etiam atque etiam consideret. Potius inflammat, ut coercendi magis quam dedocendi esse videantur. At coluit ipse amicitias. Et quidem Arcesilas tuus, etsi fuit in disserendo pertinacior, tamen noster fuit; <i>Nam quid possumus facere melius?</i> Philosophi autem in suis lectulis plerumque moriuntur. Ego quoque, inquit, didicerim libentius si quid attuleris, quam te reprehenderim. Tertium autem omnibus aut maximis rebus iis, quae secundum naturam sint, fruentem vivere. Haec et tu ita posuisti, et verba vestra sunt. Nam et complectitur verbis, quod vult, et dicit plane, quod intellegam; </p>\n" +
            "\n" +
            "<dl>\n" +
            "\t<dt><dfn>Tu quidem reddes;</dfn></dt>\n" +
            "\t<dd>Ab hoc autem quaedam non melius quam veteres, quaedam omnino relicta.</dd>\n" +
            "\t<dt><dfn>Immo videri fortasse.</dfn></dt>\n" +
            "\t<dd>Quodcumque in mentem incideret, et quodcumque tamquam occurreret.</dd>\n" +
            "\t<dt><dfn>Ita prorsus, inquam;</dfn></dt>\n" +
            "\t<dd>Parvi enim primo ortu sic iacent, tamquam omnino sine animo sint.</dd>\n" +
            "\t<dt><dfn>Optime, inquam.</dfn></dt>\n" +
            "\t<dd>Quasi vero aut concedatur in omnibus stultis aeque magna esse vitia, et eadem inbecillitate et inconstantia L.</dd>\n" +
            "</dl>\n" +
            "\n" +
            "\n" +
            "<p><b>Cyrenaici quidem non recusant;</b> <b>Erat enim Polemonis.</b> Quid autem habent admirationis, cum prope accesseris? <b>Erit enim mecum, si tecum erit.</b> Quid censes in Latino fore? <b>Sint modo partes vitae beatae.</b> Superiores tres erant, quae esse possent, quarum est una sola defensa, eaque vehementer. Levatio igitur vitiorum magna fit in iis, qui habent ad virtutem progressionis aliquantum. </p>\n" +
            "\n" +
            "<p>Ego quoque, inquit, didicerim libentius si quid attuleris, quam te reprehenderim. Mihi quidem Antiochum, quem audis, satis belle videris attendere. Sed ille, ut dixi, vitiose. <a href='http://loripsum.net/' target='_blank'>Equidem e Cn.</a> </p>\n" +
            "\n" +
            "<p>Eam tum adesse, cum dolor omnis absit; <a href='http://loripsum.net/' target='_blank'>Prave, nequiter, turpiter cenabat;</a> <a href='http://loripsum.net/' target='_blank'>Facete M.</a> Illa argumenta propria videamus, cur omnia sint paria peccata. Quamquam ab iis philosophiam et omnes ingenuas disciplinas habemus; Ut enim consuetudo loquitur, id solum dicitur honestum, quod est populari fama gloriosum. Hos contra singulos dici est melius. <b>Age sane, inquam.</b> </p>\n" +
            "\n" +
            "<ul>\n" +
            "\t<li>Servari enim iustitia nisi a forti viro, nisi a sapiente non potest.</li>\n" +
            "\t<li>Quid loquor de nobis, qui ad laudem et ad decus nati, suscepti, instituti sumus?</li>\n" +
            "\t<li>Si quicquam extra virtutem habeatur in bonis.</li>\n" +
            "</ul>\n" +
            "\n" +
            "\n" +
            "<blockquote cite='http://loripsum.net'>\n" +
            "\tNam illud quidem adduci vix possum, ut ea, quae senserit ille, tibi non vera videantur.\n" +
            "</blockquote>\n" +
            "\n" +
            "\n";
}
