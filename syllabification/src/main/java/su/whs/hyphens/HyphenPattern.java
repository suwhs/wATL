package su.whs.hyphens;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by igor n. boulliev on 04.04.15.
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

public class HyphenPattern {
    private TrieNode trie;

    public TrieNode getTrie() {
        return this.trie;
    }


    public static class TrieNode {
        public SparseArray<TrieNode> codePoint = new SparseArray<TrieNode>();
        public ArrayList<Integer> _points;

        public int serialize(DataOutputStream out) throws IOException {
            out.writeInt(_points == null ? 0 : _points.size());
            if (_points!=null) for (int i : _points) out.writeInt(i);
            out.writeInt(codePoint == null ? 0 : codePoint.size());
            if (codePoint!=null) for (int i=0; i < codePoint.size(); i++) {
                int key = codePoint.keyAt(i);
                out.writeInt(key);
                codePoint.get(key).serialize(out);
            }
            return 0;
        }

        public TrieNode() { }

        public TrieNode(DataInputStream in) throws IOException {
            int _pointsSize = in.readInt();
            if (_pointsSize > 0) {
                _points = new ArrayList<Integer>();
                for (int i=0; i<_pointsSize; i++) {
                    _points.add(in.readInt());
                }
            }
            int _childsSize = in.readInt();
            if (_childsSize>0) {
                codePoint = new SparseArray<TrieNode>();
                for (int i=0; i<_childsSize; i++) {
                    codePoint.put(in.readInt(), new TrieNode(in));
                }
            }
        }
    }

    public static TrieNode createTrie(Map<Integer, String> patternObject) {

        int i = 0, c = 0, p = 0, codePoint;

        String[] chars, points;
        TrieNode t, tree = new TrieNode();
        ArrayList<String> patterns = new ArrayList<String>();

        for (Map.Entry<Integer, String> entry : patternObject.entrySet()) {
            Matcher matcher = Pattern.compile(".{1," + String.valueOf(entry.getKey()) + "}")
                    .matcher(entry.getValue());
            while (matcher.find()) {
                patterns.add(matcher.group(0));
            }

            for (i = 0; i < patterns.size(); i++) {
                chars = patterns.get(i).replaceAll("[0-9]", "").split("");
                points = patterns.get(i).split("\\D");

                t = tree;

                for (c = 0; c < chars.length; c++) {
                    if (chars[c].length() == 0) {
                        continue;
                    }

                    codePoint = chars[c].codePointAt(0);

                    if (t.codePoint.get(codePoint) == null) {
                        t.codePoint.put(codePoint, new TrieNode());
                    }

                    t = t.codePoint.get(codePoint);
                }

                t._points = new ArrayList<Integer>();

                for (p = 0; p < points.length; p++) {
                    try {
                        t._points.add(Integer.parseInt(points[p]));
                    } catch (NumberFormatException e) {
                        t._points.add(0);
                    }
                }
            }
        }

        return tree;
    }

    public final int leftMin;
    public final int rightMin;

    /*
     * Common language patterns
     * More info at https://github.com/bramstein/hyphenation-patterns
     */
    public Map<Integer, String> patternObject = null;

    public HyphenPattern(int leftMin, int rightMin, Map<Integer, String> patternObject) {
        this.leftMin = leftMin;
        this.rightMin = rightMin;
        this.patternObject = patternObject;
    }

    public HyphenPattern(DataInputStream in) throws IOException {
        this.leftMin = in.readInt();
        this.rightMin = in.readInt();
        this.trie = new TrieNode(in);
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.leftMin);
        out.writeInt(this.rightMin);
        if (this.trie==null)
            this.trie = createTrie(this.patternObject);
        this.trie.serialize(out);
    }


    /**
     * HyphenaPattern.java is an adaptation of Bram Steins hypher.js-Project:
     * https://github.com/bramstein/Hypher
     * <p/>
     * Code from this project belongs to the following license:
     * <p/>
     * Copyright (c) 2011, Bram Stein All rights reserved.
     * <p/>
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions are met:
     * <p/>
     * 1. Redistributions of source code must retain the above copyright notice,
     * this list of conditions and the following disclaimer. 2. Redistributions in
     * binary form must reproduce the above copyright notice, this list of
     * conditions and the following disclaimer in the documentation and/or other
     * materials provided with the distribution. 3. The name of the author may not
     * be used to endorse or promote products derived from this software without
     * specific prior written permission.
     * <p/>
     * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED
     * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
     * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
     * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
     * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
     * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
     * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
     * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
     * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
     * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     */

}
