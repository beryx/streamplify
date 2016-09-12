/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.streamplify.example;

import org.beryx.streamplify.product.CartesianProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates poems by randomly selecting words from some lists of words.
 * <br>Illustrates the use of {@link CartesianProduct}.
 */
public class RandomPoetry {
    private final List<String[]> wordList = new ArrayList<>();

    public RandomPoetry withOneOf(String... words) {
        wordList.add(words);
        return this;
    }

    public String generate(int lineCount) {
        int[] dimensions = wordList.stream().mapToInt(lst -> lst.length).toArray();
        return new CartesianProduct(dimensions)
                .shuffle()
                .stream()
                .limit(lineCount)
                .map(seq -> {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < seq.length; i++) {
                        sb.append(wordList.get(i)[seq[i]]).append(' ');
                    }
                    return sb.toString();
                })
                .collect(Collectors.joining("\n"));
    }

    /**
     * Generates a random poem. Example output:
     * <pre>
     * One eagle cannot kill the light
     * One girl wants to leave this fight
     * The woman cannot break this site
     * The eagle wants to leave the night
     * </pre>
     * @param args
     */
    public static final void main(String[] args) {
        System.out.println(new RandomPoetry()
                .withOneOf("The", "One", "A")
                .withOneOf("cat", "dog", "eagle", "man", "woman", "child", "girl", "boy")
                .withOneOf("can", "cannot", "hopes to", "fears to", "wants to")
                .withOneOf("break", "leave", "kill", "get")
                .withOneOf("the", "this", "that")
                .withOneOf("night", "light", "fight", "knight", "byte", "height", "kite", "site")
                .generate(4));
    }
}
