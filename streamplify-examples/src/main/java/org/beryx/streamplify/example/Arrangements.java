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

import org.beryx.streamplify.combination.Combinations;
import org.beryx.streamplify.permutation.Permutations;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates <a href="https://en.wikipedia.org/wiki/Permutation#k-permutations_of_n">k-permutaions of n</a>.
 * <br>This implementation makes use of the {@link Combinations} and {@link Permutations} classes.
 * It generates all k-combinations of n, and for each combination generates all permutations of its k elements.
 * <br>This is a simple and easy to understand solution.
 * However, for improved performance, you may provide implementations based on the
 * {@link org.beryx.streamplify.LongIndexedSpliterator} and {@link org.beryx.streamplify.BigIntegerIndexedSpliterator}.
 */
public class Arrangements {
    private final int n;
    private final int k;

    public Arrangements(int n, int k) {
        this.n = n;
        this.k = k;
    }

    public Stream<int[]> stream() {
        return new Combinations(n, k)
                .stream()
                .flatMap(comb -> new Permutations(k)
                        .stream()
                        .map(perm -> Arrays.stream(perm).map(p -> comb[p]).toArray()));
    }

    public Stream<int[]> parallelStream() {
        return new Combinations(n, k)
                .parallelStream()
                .flatMap(comb -> new Permutations(k)
                        .parallelStream()
                        .map(perm -> Arrays.stream(perm).map(p -> comb[p]).toArray()));
    }

    /**
     * Solves the following problem:<pre>
     * Alice, Bob, Chloe, David, and Emma take part in a competition.
     * List all possible outcomes for the top 3 ranking.</pre>
     */
    public static void main(String[] args) {
        String[] names = {"Alice", "Bob", "Chloe", "David", "Emma"};
        System.out.println(new Arrangements(5, 3)
                .stream()
                .map(arr -> Arrays.stream(arr).mapToObj(i -> names[i]).collect(Collectors.toList()).toString())
                .collect(Collectors.joining("\n")));
    }
}
