package su.whs.watl.samples;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by igor n. boulliev on 15.02.15.
 * copy-n-paste from http://stackoverflow.com/questions/22832123/get-selected-text-from-textview
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SampleActionModeCallback implements ActionMode.Callback {
    public TextView mTextView = null;

    public SampleActionModeCallback(TextView textView) {
        mTextView = textView;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // menu.add(0, DEFINITION, 0, "Definition").setIcon(R.drawable.ic_action_book);
        menu.add(0, android.R.id.copy, 0, "copy").setIcon(R.drawable.ic_action_name);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // Remove the "select all" option
        menu.removeItem(android.R.id.selectAll);
        // Remove the "cut" option
        menu.removeItem(android.R.id.cut);
        // Remove the "copy all" option
        // menu.removeItem(android.R.id.copy);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.copy:
                CharSequence text = mTextView.getText().subSequence(mTextView.getSelectionStart(), mTextView.getSelectionEnd());
                ClipboardManager manager = (ClipboardManager) mTextView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText(null, text));
                mTextView.setSelected(false);
                Toast.makeText(mTextView.getContext(),"Text copied to clipboard",Toast.LENGTH_LONG).show();
                mode.finish();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mTextView.setTextIsSelectable(false);
        mTextView.setTextIsSelectable(true);
    }
}
