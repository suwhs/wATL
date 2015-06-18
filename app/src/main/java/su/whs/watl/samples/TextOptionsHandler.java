package su.whs.watl.samples;

import android.view.MenuItem;

import su.whs.watl.text.ContentView;

/**
 * Created by igor n. boulliev on 17.06.15.
 */
public class TextOptionsHandler {
    private ContentView.Options opts;
    public TextOptionsHandler(ContentView.Options options) {
        opts = options;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.bigfont) {

        } else if (id == R.id.medfont) {

        } else if (id == R.id.smallfont) {

        } else if (id == R.id.filter_empty) {
            item.setChecked(!opts.isFilterEmptyLines());
            opts.setFilterEmptyLines(!opts.isFilterEmptyLines())
                    .apply();
        } else {
            return false;
        }
        return true;
    }
}
