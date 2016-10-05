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

import org.beryx.streamplify.permutation.Permutations;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A brute force solver for the N-Queens problem.
 * <br>Illustrates the use of {@link Permutations}.
 */
public class NQueens {
    private final int length;

    public NQueens(int length) {
        this.length = length;
    }

    public Stream<int[]> stream() {
        return new Permutations(length).stream().filter(NQueens::isNQueensSolution);
    }

    public Stream<int[]> parallelStream() {
        return new Permutations(length).parallelStream().filter(NQueens::isNQueensSolution);
    }

    public static boolean isNQueensSolution(int[] perm) {
        int size = perm.length;
        for(int i = 0; i < size - 1; i++) {
            for(int j = i + 1; j < size; j++) {
                if(Math.abs(perm[j] - perm[i]) == j - i) return false;
            }
        }
        return true;
    }

    /**
     * @return a representation of the solution in table format. Example:
     * <pre>
     * <br>|---|---|---|---|---|---|
     * <br>|   | * |   |   |   |   |
     * <br>|---|---|---|---|---|---|
     * <br>|   |   |   | * |   |   |
     * <br>|---|---|---|---|---|---|
     * <br>|   |   |   |   |   | * |
     * <br>|---|---|---|---|---|---|
     * <br>| * |   |   |   |   |   |
     * <br>|---|---|---|---|---|---|
     * <br>|   |   | * |   |   |   |
     * <br>|---|---|---|---|---|---|
     * <br>|   |   |   |   | * |   |
     * <br>|---|---|---|---|---|---|
     * </pre>
     */
    public static String toString(int[] solution) {
        StringBuilder sb = new StringBuilder();
        int len = solution.length;
        for(int i = 0;  i < len; i++) {
            appendLine(sb, len, -1);
            appendLine(sb, len, solution[i]);
        }
        appendLine(sb, len, -1);
        sb.append('\n');
        return sb.toString();
    }

    private static void appendLine(StringBuilder sb, int len, int queenPos) {
            char fill = (queenPos < 0) ? '-' : ' ';
            sb.append('|');
            for(int k = 0; k < len; k++) {
                char ch = (queenPos < 0) ? '-' : (queenPos == k ? '*' : ' ');
                sb.append(fill).append(ch).append(fill).append('|');
            }
            sb.append('\n');
    }

    /**
     * Prints at most 10 solutions of the N-Queens problem with size 12.
     */
    public static void main(String[] args) {
        System.out.println("Solutions:\n"
                + new NQueens(12)
                .parallelStream()
                .limit(10)
                .map(NQueens::toString)
                .collect(Collectors.joining("\n\n")));
    }
}
