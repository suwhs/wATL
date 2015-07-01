package su.whs.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class TextUtils {
	private static final String TAG = "HintTextUtils";

	private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();
	private static Paint mDebugRect = new Paint();

	private static Typeface get(Context c, String assetPath) {
		synchronized (cache) {
			if (!cache.containsKey(assetPath)) {
				try {
					Typeface t = Typeface.createFromAsset(c.getAssets(),
							assetPath);
					cache.put(assetPath, t);
				} catch (Exception e) {
					Log.e(TAG, "Could not get typeface '" + assetPath
							+ "' because " + e.getMessage());
					return null;
				}
			}
			return cache.get(assetPath);
		}
	}

	public static class MultiLineTextMeasureInfo {
		public String[] lines;
		public float[] widths;
		public float[] heights;
		public int height;
		public int width;
		public Paint paint;

		private MultiLineTextMeasureInfo(String line, Paint p) {
			paint = p;
			lines = line.split("\n");
		}
	}

	public static MultiLineTextMeasureInfo measureMultiLineText(String text,
			Paint p) {
		MultiLineTextMeasureInfo result = new MultiLineTextMeasureInfo(text, p);
		getMultilineBounds(result);
		return result;
	}

	public static void getMultilineBounds(MultiLineTextMeasureInfo info) {
		info.widths = new float[info.lines.length];
		info.heights = new float[info.lines.length];

		Rect r = new Rect();
		float tH = 0f;
		float tW = 0f;

		for (int i = 0; i < info.lines.length; i++) {
			if (info.lines[i].length() < 1)
				continue;
			info.paint.getTextBounds(info.lines[i], 0, info.lines[i].length(),
					r);
			info.widths[i] = r.width();
			info.heights[i] = r.height();

			if (r.width() > info.width) {
				info.width = r.width();
				tW = r.width();
			}
			info.height += r.height() + info.paint.getFontSpacing();
			tH += r.height(); // + info.paint.getFontSpacing();
		}

		if (r.width() > info.width) {
			info.width = r.width();
			tW = r.width();
		}

		info.height = (int) Math.ceil(tH);
		info.width = (int) Math.ceil(tW);
		/*
		 * if (info.lines.length > 1) info.height -=
		 * info.paint.getFontSpacing();
		 */
	}

	public static void drawMultilineText(Canvas c,
			MultiLineTextMeasureInfo info, Point center, Align a) {
		/*
		 * int totalHeight = 0; int maxWidth = 0; Rect r = new Rect();
		 * for(String line: lines ) { if (line.length()<1) continue;
		 * p.getTextBounds(line, 0, line.length(), r); if (r.right>maxWidth)
		 * maxWidth = r.right; totalHeight += r.bottom + LINESPACE; } if
		 * (lines.length>1) totalHeight -= LINESPACE;
		 */
		// Log.v(TAG,"text height: "+String.valueOf(bounds.height()));
		// Log.v(TAG,"text width: " + String.valueOf(bounds.width()));
		// int x = bounds.left;
		// int y = bounds.top;
		int lH = (int) info.paint.getFontMetrics(info.paint.getFontMetrics());
		int tY = (int) ((int) (center.y - info.height / 2));

		for (int i = 0; i < info.lines.length; i++) {

			int shiftX = 0;
			int shiftY = (int) info.heights[i];

			switch (a) {
			case LEFT:
				shiftX = (int) (-info.width / 2);
				break;
			case RIGHT:
				shiftX = (int) (info.width / 2 - info.widths[i]);
				break;
			case CENTER:
				shiftX = (int) (-info.widths[i] / 2);
				break;
			}
			c.drawText(info.lines[i], center.x + shiftX, tY + shiftY,
					info.paint);
			tY += shiftY; // info.heights[i];
		}
		/* TODO: debug */
		mDebugRect.setColor(Color.RED);
		mDebugRect.setStyle(Paint.Style.STROKE);
		c.drawRect(center.x - info.width / 2, center.y - info.height / 2,
				center.x + info.width / 2, center.y + info.height / 2,
				mDebugRect);
	}
	
	private static class GlyphsCache {
		private static Map<String, Map<Character, Float>> mCache = new HashMap<String, Map<Character, Float>>();

		public static synchronized float glyphWidth(char glyph,
				String metricString, TextPaint paint) {
			if (!mCache.containsKey(metricString)) {
				mCache.put(metricString, new HashMap<Character, Float>());
			}
			Map<Character, Float> map = mCache.get(metricString);
			if (!map.containsKey(glyph)) {
				char[] glyphs = new char[] { glyph };
				float[] widths = new float[1];
				paint.getTextWidths(glyphs, 0, 1, widths);
				map.put(glyph, widths[0]);
				return widths[0];
			}
			return map.get(glyph);
		}

		private static String glyphMetricsString(TextPaint paint) {
			int metricsInt = paint.getFontMetricsInt(paint.getFontMetricsInt());
			float textSize = paint.getTextSize();
			float textScale = paint.getTextScaleX();
			float textSkew = paint.getTextSkewX();
			Typeface typeface = paint.getTypeface();
			return String.format("%i%.2f%.2f%.2f", metricsInt, textSize,
					textScale, textSkew);
		}
	}

	/*
	private static class TextLine {
		public static final int POSITION_MASK = 0xffff0000;
		public static final int COUNT_MASK = 0x0000ffff;
		public int lineStart = 0;
		public int lineEnd = 0;
		public float measuredWidth = 0;
		public float widths[] = null;
		public int[] addedSpaces = null; // if justified - holds position << 16
											// & count
		public boolean hyphen = false;

	}
	
	public interface LineBreak {
		int breakText(CharSequence text, int limit);
	}

	public static class TextLayout {
		private CharSequence mText = null;
		private List<TextLine> mLines = new ArrayList<TextLine>();
		private int mWidth = 0;
		private int mHeight = 0;
		private TextLine mMeasuredLine = null;
		
		public int getLinesCount() {
			return mLines.size();
		}

		public CharSequence getText() {
			return mText;
		}

		public int getLineStart(int line) {
			if (mLines.size() > line)
				return mLines.get(line).lineStart;
			throw new IndexOutOfBoundsException();
		}

		public int getLineEnd(int line) {
			if (mLines.size() > line)
				return mLines.get(line).lineEnd;
			throw new IndexOutOfBoundsException();
		}
		
		public int getWidth() { return mWidth; }
		public int getHeight() { return mHeight; }
		
		public TextLayout(CharSequence text, int start, int end, TextPaint paint) {
			TextLine measured = new TextLine();
			measured.widths = new float[end-start];
			measured.lineStart = start;
			measured.lineEnd = end;
			
		}
		
		private int breakTextRunner(CharSequence text, int start, int end, int width, TextPaint paint) {
			
			if (text instanceof Spanned) {
				Object[] spans = ((Spanned)text).getSpans(start, end, Object.class);
				for(Object span: spans) {
					if (span instanceof MetricAffectingSpan) {
						int spanStart = ((Spanned)text).getSpanStart(span);
						int spanEnd = ((Spanned)text).getSpanEnd(span);
						
					} else {
						continue;
					}
				}
			}
			return 0;
		}
		
		private int breakTextRunner(CharSequence text, int width, TextPaint paint) {
			return breakTextRunner(text, 0, text.length(), width, paint);
		}
		
		
		
		public void draw(Canvas canvas) {
			
		}
		
		
	}
 	*/

	public static Typeface loadFont(Context context, String hintFontName) {
		/*
		 * Typeface result = Typeface.createFromAsset(context.getAssets(),
		 * hintFontName);
		 */
		return get(context, hintFontName);
	}

	/*
	private TextLayout measureText(CharSequence text, int width, int height,
			TextPaint paint) {
		if (text instanceof Spanned) {
			return measureSpannedText((Spanned) text, width, height, paint);
		} else {
			return measureText(text, width, height, paint);
		}
	}

	private TextLayout measureText(String text, int width, int height,
			TextPaint paint) {
		return null;
	}

	private TextLayout measureSpannedText(Spanned text, int width, int height,
			TextPaint paint) {
		return null;
	} */
}
