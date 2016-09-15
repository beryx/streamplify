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
package org.beryx.streamplify.permutation;

import org.beryx.streamplify.IntArraySupplier;
import org.beryx.streamplify.Splittable;

import java.math.BigInteger;

/**
 * A value supplier for permutations.
 * <br>It may compute the next permutation based on the current one, or by unranking an index.
 */
public abstract class PermutationSupplier implements IntArraySupplier {
    protected final int length;
    protected final int[] currentPermutation;

    PermutationSupplier(int length) {
        this.length = length;
        this.currentPermutation = new int[length];
    }

    @Override
    public int[] getCurrentSequence() {
        return currentPermutation;
    }

    public void computeNext() {
        int pos = length - 1;
        while(pos > 0 && currentPermutation[pos] <= currentPermutation[pos - 1]) pos--;
        if(pos == 0) return;
        int pivotPos = pos - 1;
        int pivotVal = currentPermutation[pivotPos];
        int swapIdx = length - 1;
        while(swapIdx > pivotPos && currentPermutation[swapIdx] < pivotVal) swapIdx--;
        currentPermutation[pivotPos] = currentPermutation[swapIdx];
        currentPermutation[swapIdx] = pivotVal;
        for(int i = 0; i < (length - pivotPos - 1) / 2; i++) {
            int tmp = currentPermutation[pivotPos + i + 1];
            currentPermutation[pivotPos + i + 1] = currentPermutation[length - i - 1];
            currentPermutation[length - i - 1] = tmp;
        }
    }

    public static class Long extends PermutationSupplier implements Splittable.LongIndexed<int[]> {
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
            int[] perm = new int[length];
            for(int i = 0; i < length; i++)
                perm[i] = i;

            long dividend = currentIndex;
            for(int step = 0; step < length - 1; step++) {
                int idx = (int) (dividend / divisors[step]);
                if(idx > 0) {
                    int val = perm[step + idx];
                    System.arraycopy(perm, step, perm, step + 1, idx);
                    perm[step] = val;
                }
                dividend = dividend % divisors[step];
            }
            return perm;
        }

        private static long[] computeDivisors(int len) {
            if(len < 1) return null;
            long[] divs = new long[len - 1];
            long fac = 1;
            for(int i = 1; i < len; i++) {
                fac *= i;
                divs[len - i - 1] = fac;
            }
            return divs;
        }
    }

    public static class BigInt extends PermutationSupplier implements Splittable.BigIntegerIndexed<int[]> {
        private final BigInteger[] divisors;
        private BigInteger currentIndex = BigInteger.valueOf(-2);

        public BigInt(int length) {
            this(length, computeDivisors(length));
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
            int[] perm = new int[length];
            for(int i = 0; i < length; i++)
                perm[i] = i;

            BigInteger dividend = currentIndex;
            for(int step = 0; step < length - 1; step++) {
                BigInteger[] quotientAndRemainder = dividend.divideAndRemainder(divisors[step]);
                int idx = quotientAndRemainder[0].intValueExact();
                if(idx > 0) {
                    int val = perm[step + idx];
                    System.arraycopy(perm, step, perm, step + 1, idx);
                    perm[step] = val;
                }
                dividend = quotientAndRemainder[1];
            }
            return perm;
        }

        private static BigInteger[] computeDivisors(int len) {
            if(len < 1) return null;
            BigInteger[] divs = new BigInteger[len - 1];
            BigInteger fac = BigInteger.ONE;
            for(int i = 1; i < len; i++) {
                fac = fac.multiply(BigInteger.valueOf(i));
                divs[len - i - 1] = fac;
            }
            return divs;
        }
    }
}
