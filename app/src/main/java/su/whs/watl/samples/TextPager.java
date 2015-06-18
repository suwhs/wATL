package su.whs.watl.samples;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by igor n. boulliev on 18.06.15.
 */
public class TextPager extends ViewPager {

    public TextPager(Context context) {
        super(context);
    }

    public TextPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 0:
                    _invalidatePageTransformer();
                    break;
            }
        }
    };

    private void _invalidatePageTransformer()
    {
        //no need to invalidate if we have no adapter or no items
        if (this.getAdapter() != null && this.getAdapter().getCount() > 0)
        {
            //import check here, only fakeDrag if "beginFakeDrag()" returns true
            if (this.beginFakeDrag())
            {
                this.fakeDragBy(0f);
                this.endFakeDrag();
            }
        }
    }

    public void sendInvalidatePageTransformer()
    {
        this.handler.sendEmptyMessage(0);
    }
}
