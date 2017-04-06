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
package org.beryx.streamplify.partperm;

import org.beryx.streamplify.IntArraySupplier;
import org.beryx.streamplify.Splittable;
import org.beryx.streamplify.combination.CombinationSupplier;
import org.beryx.streamplify.shared.Unranking;

import java.math.BigInteger;
import java.util.stream.IntStream;

/**
 * A value supplier for partial permutations.
 * <br>It may compute the next partial permutation based on the current one, or by unranking an index.
 */
public abstract class PartialPermutationSupplier implements IntArraySupplier {
    public static final int HOLE = -1;

    protected final int length;
    protected final int[] currentPartialPermutation;

    PartialPermutationSupplier(int length) {
        this.length = length;
        this.currentPartialPermutation = new int[length];
    }

    @Override
    public int[] getCurrentSequence() {
        return currentPartialPermutation;
    }

    public void computeNext() {
        if (!nextPermutation(currentPartialPermutation)) {
            int[] currentCombination = extractCurrentCombination(currentPartialPermutation);

            if (currentCombination != null && nextCombination(currentCombination, length)) {
                int[] nextCombination = generateInitialSequence(currentCombination, length);
                System.arraycopy(nextCombination, 0, currentPartialPermutation, 0, length);
            } else {
                if (currentCombination.length == length) return;

                int[] newSubset = IntStream.range(0, currentCombination.length + 1).toArray();
                int[] newStartingCombination = generateInitialSequence(newSubset, length);
                System.arraycopy(newStartingCombination, 0, currentPartialPermutation, 0, length);
            }
        }
    }

    private int[] extractCurrentCombination(int[] permutation) {
        int endOfCombination = endOfCombination(permutation);

        int[] combination;
        combination = new int[endOfCombination];
        for (int i = endOfCombination - 1; i >= 0; i--) {
            combination[endOfCombination - i - 1] = permutation[i];
        }

        return combination;
    }

    private int endOfCombination(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == HOLE) return i;
        }
        return array.length;
    }

    private static int[] generateInitialSequence(int[] currentCombination, int totalLength) {
        int[] initialSequence = new int[totalLength];
        int numberOfHoleElements = totalLength - currentCombination.length;

        for (int i = 0; i < totalLength; i++) {
            initialSequence[i] = i < numberOfHoleElements ? HOLE : currentCombination[i - numberOfHoleElements];
        }

        return initialSequence;
    }

    static boolean nextPermutation(int[] array) {
        int pos = array.length - 1;
        while (pos > 0 && array[pos - 1] >= array[pos])
            pos--;

        if (pos <= 0) return false;

        int swapIdx = array.length - 1;
        int pivotPos = pos - 1;
        while (array[swapIdx] <= array[pivotPos])
            swapIdx--;

        swap(array, pivotPos, swapIdx);

        swapIdx = array.length - 1;
        while (pos < swapIdx) {
            swap(array, pos, swapIdx);
            pos++;
            swapIdx--;
        }

        return true;
    }

    private static void swap(int[] array, int pos, int swapIdx) {
        int temp = array[pos];
        array[pos] = array[swapIdx];
        array[swapIdx] = temp;
    }

    /**
     * Parameterized version of {@link CombinationSupplier.Long#computeNext()}
     */
    public static boolean nextCombination(int[] currentCombination, int n) {
        int k = currentCombination.length;
        int pos = k - 1;
        while (pos >= 0 && currentCombination[pos] >= n - k + pos) pos--;
        if (pos < 0) return false;
        int val = currentCombination[pos];
        for (int i = pos; i < k; i++) {
            currentCombination[i] = ++val;
        }
        return true;
    }

    public static class Long extends PartialPermutationSupplier implements Splittable.LongIndexed<int[]> {
        private final long[] divisors;
        private long currentIndex = -2;

        public Long(int length) {
            this(length, computeDivisors(length));
        }

        private Long(int length, long[] divisors) {
            super(length);
            this.divisors = divisors;
        }

        @Override
        public Long split() {
            return new Long(length, divisors);
        }

        @Override
        public int[] apply(long index) {
            boolean useNext = (index == currentIndex + 1);
            currentIndex = index;
            return getNextSequence(useNext);
        }

        @Override
        public int[] unrank() {
            if (length == 0) return new int[0];

            long prevPermutationCounter = 1;
            BigInteger nCk = BigInteger.ONE;

            int subsetSize = 1;
            long permutationCounter = 0;
            while (permutationCounter < currentIndex) {
                prevPermutationCounter = permutationCounter;
                nCk = BigInt.computeNextNchooseK(nCk, length, subsetSize - 1);
                permutationCounter += divisors[subsetSize] * Math.pow(nCk.longValue(), 2);

                subsetSize++;
            }
            subsetSize--;

            long partialPermutationIndex = currentIndex - prevPermutationCounter;
            return urankPartialPermutation(partialPermutationIndex, nCk, subsetSize);
        }

        private int[] urankPartialPermutation(long partialPermutationIndex, BigInteger nCk, int subsetSize) {
            int numberOfHoleElements = length - subsetSize;

            long lengthOfPermutations = divisors[length] / divisors[numberOfHoleElements];
            long combinationIndex = Math.floorDiv(partialPermutationIndex, lengthOfPermutations);
            long permutationIndex = Math.floorMod(partialPermutationIndex, lengthOfPermutations);

            if (permutationIndex == 0) {
                combinationIndex--;
                permutationIndex = lengthOfPermutations;
            }

            int[] currentCombination = Unranking.unrankCombination(length, subsetSize, nCk.intValue(), combinationIndex);
            int[] permutationStart = PartialPermutationSupplier.generateInitialSequence(currentCombination, length);
            return unrankPermutation(permutationStart, permutationIndex - 1, numberOfHoleElements, lengthOfPermutations);
        }

        private int[] unrankPermutation(int[] permutation, long rank, int numberOfHoleElements, long possiblePermutations) {
            for (int step = 0; step < permutation.length; step++) {
                int elementFrequency = Math.max(numberOfHoleElements, 1);
                long possibleSuffixes = possiblePermutations * elementFrequency / (length - step);

                if (rank < possibleSuffixes) {
                    if (permutation[step] == HOLE) {
                        numberOfHoleElements--;
                    }

                    possiblePermutations = possibleSuffixes;
                } else {
                    if (numberOfHoleElements > 1) {
                        rank -= possibleSuffixes;
                        possibleSuffixes = possiblePermutations / (length - step);
                    }
                    int targetOffset = (int) (rank / possibleSuffixes);
                    rank -= possibleSuffixes * targetOffset;

                    if (numberOfHoleElements > 1) targetOffset += numberOfHoleElements;
                    reorderPermutation(permutation, step, targetOffset);

                    possiblePermutations = possibleSuffixes;
                }

            }
            return permutation;
        }

        private static void reorderPermutation(int[] initialPermutation, int currentIndex, int swapOffset) {
            int valToSwap = initialPermutation[currentIndex + swapOffset];
            System.arraycopy(initialPermutation, currentIndex, initialPermutation, currentIndex + 1, swapOffset);
            initialPermutation[currentIndex] = valToSwap;
        }

        private static long[] computeDivisors(int len) {
            if (len < 1) return null;
            long[] divs = new long[len + 1];
            long fac = 1;
            divs[0] = 1;
            for (int i = 1; i <= len; i++) {
                fac *= i;
                divs[i] = fac;
            }
            return divs;
        }
    }

    public static class BigInt extends PartialPermutationSupplier implements Splittable.BigIntegerIndexed<int[]> {
        private final BigInteger[] divisors;
        private BigInteger currentIndex = BigInteger.valueOf(-2);

        public BigInt(int length) {
            this(length, BigIntegerPartialPermutations.computeFactorials(length));
        }

        private BigInt(int length, BigInteger[] divisors) {
            super(length);
            this.divisors = divisors;
        }

        @Override
        public BigInt split() {
            return new BigInt(length, divisors);
        }

        @Override
        public int[] apply(BigInteger index) {
            boolean useNext = index.equals(currentIndex.add(BigInteger.ONE));
            currentIndex = index;
            return getNextSequence(useNext);
        }

        @Override
        public int[] unrank() {
            if (length == 0) return new int[0];

            BigInteger prevPermutationCounter = BigInteger.ONE;
            BigInteger permutationCounter = BigInteger.ZERO;
            BigInteger nCk = BigInteger.ONE;

            int subsetSize = 1;
            while (permutationCounter.compareTo(currentIndex) == -1) {
                prevPermutationCounter = permutationCounter;
                nCk = computeNextNchooseK(nCk, length, subsetSize - 1);
                permutationCounter = permutationCounter.add(divisors[subsetSize].multiply(nCk.pow(2)));

                subsetSize++;
            }
            subsetSize--;
            BigInteger partialPermutationIndex = currentIndex.subtract(prevPermutationCounter);

            return unrankPartialPermutation(partialPermutationIndex, nCk, subsetSize);
        }

        private int[] unrankPartialPermutation(BigInteger partialPermutationIndex, BigInteger nCk, int subsetSize) {
            int numberOfHoleElements = length - subsetSize;
            BigInteger lengthOfPermutations = divisors[length].divide(divisors[numberOfHoleElements]);

            BigInteger[] quotientAndRemainder = partialPermutationIndex.divideAndRemainder(lengthOfPermutations);
            BigInteger combinationIndex = quotientAndRemainder[0];
            BigInteger permutationIndex = quotientAndRemainder[1];

            if (permutationIndex.compareTo(BigInteger.ZERO) == 0) {
                combinationIndex = combinationIndex.subtract(BigInteger.ONE);
                permutationIndex = lengthOfPermutations;
            }

            int[] currentCombination = Unranking.unrankCombination(length, subsetSize, nCk, combinationIndex);
            int[] permutationStart = PartialPermutationSupplier.generateInitialSequence(currentCombination, length);
            return unrankPermutation(permutationStart, permutationIndex.subtract(BigInteger.ONE), numberOfHoleElements, lengthOfPermutations);
        }

        private int[] unrankPermutation(int[] permutation, BigInteger rank, int numberOfHoleElements, BigInteger possiblePermutations) {
            for (int step = 0; step < permutation.length; step++) {
                int elementFrequency = Math.max(numberOfHoleElements, 1);
                BigInteger numRemainingElements = BigInteger.valueOf(length - step);

                BigInteger possibleSuffixes = possiblePermutations.multiply(BigInteger.valueOf(elementFrequency)).divide(numRemainingElements);

                if (rank.compareTo(possibleSuffixes) == -1) {
                    if (permutation[step] == HOLE) {
                        numberOfHoleElements--;
                    }

                    possiblePermutations = possibleSuffixes;
                } else {
                    if (numberOfHoleElements > 1) {
                        rank = rank.subtract(possibleSuffixes);
                        possibleSuffixes = possiblePermutations.divide(numRemainingElements);
                    }
                    int targetOffset = rank.divide(possibleSuffixes).intValue();
                    rank = rank.subtract(possibleSuffixes.multiply(BigInteger.valueOf(targetOffset)));

                    if (numberOfHoleElements > 1) targetOffset += numberOfHoleElements;
                    Long.reorderPermutation(permutation, step, targetOffset);

                    possiblePermutations = possibleSuffixes;
                }

            }

            return permutation;
        }

        private static BigInteger computeNextNchooseK(BigInteger prevNcK, int n, int k) {
            BigInteger nCk = prevNcK.multiply(BigInteger.valueOf(n - k));
            return nCk.divide(BigInteger.valueOf(k + 1));
        }
    }
}
