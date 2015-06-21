package su.whs.watl.samples;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import su.whs.watl.text.ContentView;

/**
 * Created by igor n. boulliev on 17.06.15.
 */
public class TextOptionsHandler {
    private ContentView.Options opts;
    private Context ctx;

    public TextOptionsHandler(Context context, ContentView.Options options) {
        ctx = context; opts = options;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.bigfont) {
            setFontSize(android.R.style.TextAppearance_Large,item);
        } else if (id == R.id.medfont) {
            setFontSize(android.R.style.TextAppearance_Medium,item);
        } else if (id == R.id.smallfont) {
            setFontSize(android.R.style.TextAppearance_Small,item);
        } else if (id == R.id.filter_empty) {
            if (item.isChecked()) {
                item.setChecked(false);
                opts.setFilterEmptyLines(false)
                        .apply();
            } else {
                item.setChecked(true);
                opts.setFilterEmptyLines(true)
                        .apply();
            }
        } else if (id == R.id.additionals_paragraphs_margins) {
            if (item.isChecked()) {
                item.setChecked(false);
                opts
                        .setNewLineTopMargin(0)
                        .setNewLineLeftMargin(0)
                        .apply();
            } else {
                item.setChecked(true);
                opts
                        .setNewLineTopMargin(5)
                        .setNewLineLeftMargin(15)
                        .apply();
            }
        } else {
            return false;
        }
        return true;
    }

    MenuItem mi_textBig;
    MenuItem mi_textMed;
    MenuItem mi_textSmall;

    public void restoreState(Menu menu) {
        MenuItem item;

        item = menu.findItem(R.id.filter_empty);
        if (item!=null) {
            item.setChecked(opts.isFilterEmptyLines());
        }
        item = menu.findItem(R.id.additionals_paragraphs_margins);
        if (item!=null) {
            item.setChecked(opts.getNewLineLeftMargin()>0);
        }

        mi_textBig = menu.findItem(R.id.bigfont);
        mi_textSmall = menu.findItem(R.id.smallfont);
        mi_textMed = menu.findItem(R.id.medfont);
    }

    private void setFontSize(int appearance, MenuItem item) {
        Button b =new Button(ctx);
        b.setTextAppearance(ctx,appearance);
        float size = b.getTextSize();
        opts
                .setTextSize(size)
                .apply();
        uncheckAll();
        item.setChecked(true);
    }

    private void uncheckAll() {
        mi_textBig.setChecked(false);
        mi_textMed.setChecked(false);
        mi_textSmall.setChecked(false);
    }
}
