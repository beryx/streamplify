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
package org.beryx.streamplify.benchmark;

import org.beryx.streamplify.permutation.LongPermutations;

import java.util.stream.Stream;

public class NQueensBenchmark {
    private final int length;
    private final boolean parallel;

    public NQueensBenchmark(int length, boolean parallel) {
        this.length = length;
        this.parallel = parallel;
    }

    public Stream<int[]> getStreamplifyStream() {
        LongPermutations longPermutations = new LongPermutations(length);
        Stream<int[]> stream = parallel ? longPermutations.parallelStream() : longPermutations.stream();
        return stream.filter(perm -> isNQueensSolution(perm));
    }

    public Stream<int[]> getIterSpliterStream() {
        IterSpliterPermutations iterSpliterPermutations = new IterSpliterPermutations(length);
        Stream<int[]> stream = parallel ? iterSpliterPermutations.parallelStream() : iterSpliterPermutations.stream();
        stream = stream.filter(perm -> isNQueensSolution(perm));
        if(parallel) stream.parallel();
        return stream;
    }

    public void run() {
        System.out.println("########################################################");
        System.out.println("Solving NQueens for size " + length + " using " + (parallel ? "parallel" : "sequential") + " streams.");

        long start1 = System.currentTimeMillis();
        Stream<int[]> stream1 = getStreamplifyStream();
        long count1 = stream1.count();
        long duration1 = System.currentTimeMillis() - start1;

        System.out.println("count: " + count1);
        System.out.println("duration(streamplify) = " + duration1);

        long start2 = System.currentTimeMillis();
        Stream<int[]> stream2 = getIterSpliterStream();
        long count2 = stream2.count();
        long duration2 = System.currentTimeMillis() - start2;

        if(count1 != count2) throw new AssertionError("count1 = " + count1 + ", count2 = " + count2);

        System.out.println("duration(IterSpliter) = " + duration2);
        System.out.println("--------------------------------------------------------");
    }

    private static boolean isNQueensSolution(int[] perm) {
        int size = perm.length;
        for(int i = 0; i < size - 1; i++) {
            for(int j = i + 1; j < size; j++) {
                if(Math.abs(perm[j] - perm[i]) == j - i) return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        new NQueensBenchmark(11, true).run();
        new NQueensBenchmark(11, false).run();
        new NQueensBenchmark(12, true).run();
        new NQueensBenchmark(12, false).run();
        new NQueensBenchmark(13, true).run();
        new NQueensBenchmark(13, false).run();
    }
}
