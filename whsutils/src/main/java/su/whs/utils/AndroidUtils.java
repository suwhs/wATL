package su.whs.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class AndroidUtils {
	/* resolve resource id from string '@id/name' */
	public static int resolveId(Context context, Resources res, String string) {
		if (string.startsWith("@")) {
			String subs = string.substring(1, string.length());
			String[] parts = subs.split("/");
			if (parts[0].startsWith("+"))
				parts[0] = parts[0].substring(1);
			return res.getIdentifier(parts[1], parts[0], context
					.getPackageName());

		}
		return Integer.parseInt(string);
	}

	public static String resolveString(Context context, Resources res, String string) {
		if (string.startsWith("@")) {
			String subs = string.substring(1, string.length());
			String[] parts = subs.split("/");
			int id = res.getIdentifier(parts[1], parts[0], context
					.getPackageName());
			if (id == 0x0) {
				return string;
			}
			return res.getString(id);
		}
		return string;
	}
	
	public static Drawable getRotateDrawable(Resources res, final Bitmap bitmap, final float angle) {
	    final BitmapDrawable drawable = new BitmapDrawable(res, bitmap) {
	        @Override
	        public void draw(final Canvas canvas) {
	            canvas.save();
	            canvas.rotate(angle, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
	            super.draw(canvas);
	            canvas.restore();
	        }
	    };
	    return drawable;
	}
}
