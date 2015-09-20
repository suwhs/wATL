package su.whs.watl.samples.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import su.whs.watl.experimental.SpannedSerializator;
import su.whs.watl.samples.AssetGifDrawable;
import su.whs.watl.samples.TaggedBitmapDrawable;

/**
 * Created by igor n. boulliev on 02.09.15.
 */
public class ArticleSerializer extends SpannedSerializator {

    private Context mContext;

    public ArticleSerializer(Context context, Spanned string) {
        super(string);
        mContext = context;
    }

    public ArticleSerializer(Context context) {
        super();
        mContext = context;
    }

    public static Spanned read(Context context,DataInputStream dis) throws IOException, InvalidVersionException, ReadError {
        SpannedSerializator ss = new ArticleSerializer(context);
        return ss.deserialize(dis);
    }

    /**
    @Override
    public void write(Object span, DataOutputStream dos) {

    }

    @Override
    public Object read(int tag, DataInputStream dis) {

    } */

    @Override
    public void writeDynamicDrawableSpan(DynamicDrawableSpan span, DataOutputStream dos) {
        Drawable drawable = span.getDrawable();
        if (drawable instanceof TaggedBitmapDrawable) {
            String tag = ((TaggedBitmapDrawable)drawable).getTag();
            try {
                dos.writeInt(21);
                dos.writeUTF(tag);
            } catch (IOException e) {
                //
            }
        } else if (drawable instanceof AssetGifDrawable) {
            String tag = ((AssetGifDrawable)drawable).getPath();
            try {
                dos.writeInt(22);
                dos.writeUTF(tag);
            } catch (IOException e) {

            }
        } else {
            try {
                dos.writeInt(23);
                super.writeDynamicDrawableSpan(span,dos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // throw new IllegalArgumentException("could not serialize drawable class: " + drawable.getClass());
        }
        try {
            dos.writeInt(0xaaaa); // sync mark
        } catch (IOException e) {

        }
    }

    @Override
    public DynamicDrawableSpan readDynamicDrawableSpan(DataInputStream dis) throws ReadError {
        DynamicDrawableSpan result;
        int type = -1;
        try {
            type = dis.readInt();
            if (type == 21) {
                String tag = dis.readUTF();
                InputStream is = mContext.getAssets().open(tag);
                result = new ImageSpan(mContext, BitmapFactory.decodeStream(is));
            } else if (type == 22) {
                String tag = dis.readUTF();
                result = new ImageSpan(new AssetGifDrawable(mContext, tag), tag);
            } else if (type == 23) {
                result = super.readDynamicDrawableSpan(dis);
            } else {
                result = null;
                throw new ReadError("unknown type while readDynamicDrawableSpan:" + type);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = null;
        }
        int syncMark = 0;
        try {
            syncMark = dis.readInt();
        } catch (IOException e) {
            Log.e("AS", "error reading sync mark");
        }
        if (syncMark!=0xaaaa) {
            throw new ReadError("lost sync after read drawable with tag:"+type);
        }
        return result;
    }
}
