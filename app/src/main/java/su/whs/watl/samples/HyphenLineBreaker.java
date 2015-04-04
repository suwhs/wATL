package su.whs.watl.samples;

/**
 * created by igor n. boulliev on 03.04.15.
 */

import android.annotation.SuppressLint;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import su.whs.watl.text.LineBreaker;
import su.whs.watl.text.hyphen.HyphenPattern;


@SuppressLint("UseSparseArrays")
public class HyphenLineBreaker extends LineBreaker {

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

    private static final HashMap<HyphenPattern, LineBreaker> cached;

    static {
        cached = new HashMap<HyphenPattern, LineBreaker>();
    }

    private HyphenPattern.TrieNode trie;
    private int leftMin;
    private int rightMin;

    public HyphenLineBreaker(HyphenPattern pattern) {
        this.trie = HyphenPattern.createTrie(pattern.patternObject);
        this.leftMin = pattern.leftMin;
        this.rightMin = pattern.rightMin;
    }

    public HyphenLineBreaker(HyphenPattern pattern, DataInputStream in) throws IOException {
        this.trie = new HyphenPattern.TrieNode(in);
        this.leftMin = pattern.leftMin;
        this.rightMin = pattern.rightMin;
    }

    public static LineBreaker getInstance(HyphenPattern pattern, DataInputStream in) throws IOException {
        synchronized (cached) {
            if (!cached.containsKey(pattern)) {
                cached.put(pattern, new HyphenLineBreaker(pattern, in));
                return cached.get(pattern);
            }

            return cached.get(pattern);
        }
    }


    public static LineBreaker getInstance(HyphenPattern hyphenationPattern) {
        synchronized (cached) {
            if (!cached.containsKey(hyphenationPattern)) {
                cached.put(hyphenationPattern, new HyphenLineBreaker(hyphenationPattern));
                return cached.get(hyphenationPattern);
            }

            return cached.get(hyphenationPattern);
        }
    }

    // TODO: change to statically created TrieNode for each patternObject


    private void hyphenate(char[] word, int[] hyphens) {

        int i, j, k, nodePointsLength;
        ArrayList<Integer> nodePoints;
        HyphenPattern.TrieNode node, trie = this.trie;

        int[] points = new int[word.length];
        int[] characterPoints = new int[word.length];

        // TODO: in most cases - code points are useles (sufficient char itself)
        for (i = 0; i < word.length; i++) {
            characterPoints[i] = Character.codePointAt(word,i);
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
                            int pp = points[i+k];
                            int pk = nodePoints.get(k);
                            points[i + k] = Math.max(pp, pk);
                        }
                    }
                } else {
                    break;
                }
            }
        }

        for (i = 1; i < word.length - 1; i++) {
            if (i > this.leftMin && i < (word.length - this.rightMin) && points[i] % 2 > 0) {
                hyphens[i] = 1;
            } else {
                if (hyphens[i-1]==1) {
                    hyphens[i-1]=0;
                }
                hyphens[i] = 1;
            }
        }
    }

    /* adaptation code by igor n. boulliev below */


    private class LastQueryCache {
        int start = -1;
        int limit = -1;
        int wstart = -1;
        int wend = -1;
        int[] hyphens;
    }

    private LastQueryCache lastQueryCache = new LastQueryCache();

    private void invalidateLastQueryCache() {
        lastQueryCache.start = -1;
        lastQueryCache.limit = -1;
        lastQueryCache.hyphens = null;
    }

    private int nearestLineBreakFromCache(int start, int end) {
        int offset = end - start;
        if (lastQueryCache.hyphens == null || offset>lastQueryCache.hyphens.length) {
            return start;
        }
        for(;offset>1;offset--) {
            if (lastQueryCache.hyphens[offset]==1) {
                int result = (start + offset) | LineBreaker.HYPHEN;
                return result;
            }
        }
        return start;
    }

    @Override
    public int nearestLineBreak(char[] text, int start, int end, int limit) {
        boolean letter = Character.isLetter(text[end]);
        if (letter && lastQueryCache.start==start && lastQueryCache.limit==limit) {
            return nearestLineBreakFromCache(start, end);
        }
        invalidateLastQueryCache();
        // check if end is letter and (end-start)>1 && limit-end > 1

        if (letter && (end - start)>1 && (limit-end)>1) {
            // fetch word
            // lowercase
            int wstart = firstNonLetterBackward(text,start,end) + 1;
            int wend = firstNonLetterForward(text,end+1,limit);
            int length = wend - wstart;
            if (length>3) {
                char[] word = new char[length+2];
                for (int i = wstart; i < wend; i++) {
                    word[i-wstart+1] = Character.toLowerCase(text[i]);
                }
                word[0] = '_';
                word[word.length-1] = '_';
                // now word[] contains lower-cased word, which must be hyphenated
                lastQueryCache.start = start;
                lastQueryCache.limit = limit;
                lastQueryCache.wstart = wstart;
                lastQueryCache.wend = wend;
                lastQueryCache.hyphens = new int[length+2];
                // call hyphenate()
                hyphenate(word,lastQueryCache.hyphens);
                return nearestLineBreakFromCache(start,end);
            }
            return start;
        } else {
            if (letter)
                return firstNonLetterBackward(text, start, end+1);
            // not sufficient characters for soft-hyphen, or 'end' is not letter
            return end;
        }
    }

    private int firstNonLetterBackward(char[] text, int start, int end) {
        for (;end>start;end--) {
            if (!Character.isLetter(text[end])) return end;
        }
        return start;
    }

    private int firstNonLetterForward(char[] text, int from, int to) {
        for (;from<to;from++) {
            if (!Character.isLetter(text[from])) return from;
        }
        return to;
    }

}
