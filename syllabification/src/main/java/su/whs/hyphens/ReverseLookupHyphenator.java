package su.whs.hyphens;

import java.util.ArrayList;
import java.util.WeakHashMap;

import static su.whs.syllabification.parent.LineBreaker.HYPHEN;

/**
 * Created by igor n. boulliev on 13.01.17.
 */

/*
 * Copyright 2015 Mathew Kurian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * DefaultHyphenator.java
 * @author Mathew Kurian
 *
 * From TextJustify-Android Library v2.0
 * https://github.com/bluejamesbond/TextJustify-Android
 *
 * Date: 1/27/15 3:35 AM
 */

/*
 * @file   DefaultHyphenator.java
 * @author Murilo Andrade
 * @date   2014-10-20
 */

/*
 * @file   HyphenLineBreaker.java
 * @author Igor N. Boulliev
 * @date   2015-04-03
 */


/*
 * @file   ReverseLookupHyphenator.java
 * @author Igor N. Boulliev
 * @date   2015-04-03
 */

public class ReverseLookupHyphenator {
    private HyphenPattern.TrieNode trie;
    private int leftMin;
    private int rightMin;

    public ReverseLookupHyphenator(HyphenPattern pattern) {
        this.trie = pattern.getTrie();
        this.leftMin = pattern.leftMin;
        this.rightMin = pattern.rightMin;
    }

    /**
     * marks soft-hypen positions for given words
     *
     * @param word
     * @param hyphens
     */
    private static void hyphenate(char[] word, int[] hyphens, HyphenPattern.TrieNode trie, int leftMin, int rightMin) {

        int i, j, k, nodePointsLength;
        ArrayList<Integer> nodePoints;
        HyphenPattern.TrieNode node;

        int[] points = new int[word.length];
        int[] characterPoints = new int[word.length];

        // TODO: in most cases - code points are useles (sufficient char itself)
        for (i = 0; i < word.length; i++) {
            characterPoints[i] = Character.codePointAt(word, i);
        }

        for (i = 0; i < word.length; i++) {
            node = trie;

            for (j = i; j < word.length; j++) {
                node = node.codePoint.get(characterPoints[j]);

                if (node != null) {
                    nodePoints = node._points;
                    if (nodePoints != null) {
                        for (k = 0, nodePointsLength =
                                nodePoints.size(); k < nodePointsLength; k++) {
                            int pp = points[i + k];
                            int pk = nodePoints.get(k);
                            points[i + k] = Math.max(pp, pk);
                        }
                    }
                } else {
                    break;
                }
            }
        }

        for (i = leftMin+1; i < (word.length - rightMin); i++)
            if (points[i+1] % 2 > 0)
                hyphens[i] = 1;

//        dumpHyphens(word,points,hyphens);
//        System.out.println();
    }

    /* */
    private static class LastQueryCache {
        int start = -1;
        int end = -1;
        int limit = -1;
        int wstart = -1;
        int wend = -1;
        int[] hyphens;
    }

    private WeakHashMap<char[],LastQueryCache> mCache = new WeakHashMap<char[], LastQueryCache>();

    private static LastQueryCache lookupCache(char[] text, int start, int end, int limit, WeakHashMap<char[],LastQueryCache> cache) {
        LastQueryCache result = cache.get(text);
        if (null!=result && result.start==start && result.end==end) {
            return result;
        }
        return null;
    }

    private int nearestLineBreakFromCache(char[] text, int start, int end, int limit) {
        int offset = end - start;
        LastQueryCache lastQueryCache = lookupCache(text,start,end,limit, mCache);
        if (lastQueryCache.hyphens == null) {
            throw new IllegalStateException("call lookupCache before putCache");
        }
        for (; offset > 1; offset--) {
            if (lastQueryCache.hyphens[offset] == 1) {
                int result = (start + offset);
                return result;
            }
        }
        return start;
    }

    /**
     *
     * @param text - text we processing
     * @param start - index of first character of 'line' in text (break point must be > start)
     * @param end - index of character we want to be 'breaked' if possible
     * @param limit - length of text
     * @return - index of character for 'new line start' with HYPHEN flag (if soft-hyphen)
     */

    public int nearestLineBreak(char[] text, int start, int end, int limit) {
        boolean letter = isLetter(text[end]);
        if (letter && (end-start)<4) return start;
        if (letter && (end - start) > 1 && (limit - end) > 1) {
            LastQueryCache cache = new LastQueryCache();
            int wstart = firstNonLetterBackward(text, start, end) + 1;
            int wend = firstNonLetterForward(text, end, limit);
            int length = wend - wstart;
            if (length > 3) {
                // TODO: modify hyphenate to accept text, wstart wend instead copying (if hyphen patterns case independed)
                char[] word = new char[length+2];
                for (int i = wstart; i < wend; i++) {
                    word[i - wstart + 1] = Character.toLowerCase(text[i]);
                }
                word[0] = '_';
                word[word.length - 1] = '_';
                // now word[] contains lower-cased word, which must be hyphenated
                cache.start = start;
                cache.end = end;
                cache.limit = limit;
                cache.wstart = wstart;
                cache.wend = wend;
                cache.hyphens = new int[length+2];
                // call hyphenate()
                hyphenate(word, cache.hyphens, trie, leftMin, rightMin);
//                mCache.put(text,cache);
                int offset = end - wstart; // r = nearestLineBreakFromCache(text,start, end,limit);
                for (; offset > 1; offset--) {
                    if (cache.hyphens[offset] == 1) {
//                        System.out.printf("wstart = %d, offset=%d, result=%d\n", wstart, offset, wstart+offset);
                        int result = (wstart + offset);
                        return result | HYPHEN;
                    }
                }
                return wstart;
            }
            return start;
        } else {
            if (letter) {// (end - start)<=1 || (limit-end)<=1
                return firstNonLetterBackward(text, start, end) + 1;
            }
            // not sufficient characters for soft-hyphen, or 'end' is not letter
            if (nonbreak(text[end]) && end > start) {
                return firstNonLetterBackward(text, start, end) + 1;
            }
            return end;
        }
    }

    /* punctuation ranges */
    // 20-3f
    // 5b-60
    // 7b-7e

    /*

        if (c >= 0x0600 && c <=0x06E0)

  }
     */

    public static boolean isLetter(char ch) {
                                          /* 0x0600-0x06e0 - arabic */
        return Character.isLetter(ch) || (ch >=0x0600 && ch<= 0x06e0);
    }

    public static boolean isLetterOrDigit(char ch) {
        return isLetter(ch) || Character.isDigit(ch);
    }

    public static boolean isPunktuation(char ch) {
        return ch < 0x7e &&
                (ch > 0x20 &&
                        (ch < 0x40 ||
                                (ch > 0x5b &&
                                        (ch < 0x60 ||
                                                ch > 0x7e))));
    }

    private static boolean letters(char a, char b) {
        return isLetter(a) && isLetter(b);
    }

    private static boolean nonbreak(char a, char b) {
        return nonbreak(a) && nonbreak(b);
    }

    private static boolean nonbreak(char a) {
        return isLetterOrDigit(a) || isPunktuation(a);
    }

    /**
     *
     * @param text - array of chars we lookup for
     * @param start - minimum result
     * @param end - maximum result
     * @return word begin, start or -1 if start == 0
     */
    private static int firstNonLetterBackward(char[] text, int start, int end) {
        for (; end > start; end--) {
            if (!nonbreak(text[end]) || text[end] == '-') {
                return end;
            }
        }
        return start-1;
    }

    private static int firstNonLetterForward(char[] text, int from, int to) {
        // String sub = new String(text).substring(from, to);
        for (; from < to; from++) {
            if (!Character.isLetterOrDigit(text[from])) {
                // sub = new String(text).substring(from, to);
                return from;
            }
        }
        return to;
    }


    private static void dumpHyphens(char[] word, int[] points, int[] hyphens) {
        StringBuilder sb = new StringBuilder("   word: ");

        for(int i=0; i<word.length; i++)
            sb.append(word[i]);
        sb.append("\npoints : ");
        for(int i=0; i<points.length;i++)
            sb.append(String.valueOf(points[i]));
        sb.append("\n");
        sb.append("\nhyphens: ");
        for(int i=0; i<hyphens.length;i++)
            sb.append(hyphens[i]==0 ? '0' : '1');
        sb.append("\n");
        System.out.print(sb.toString());
    }
}
