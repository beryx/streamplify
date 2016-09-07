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
package org.beryx.streamplify;

import java.math.BigInteger;

public abstract class CombinationSupplier implements IntArraySupplier {
    protected final int n;
    protected final int k;
    protected final int[] currentCombination;

    CombinationSupplier(int n, int k) {
        this.n = n;
        this.k = k;
        this.currentCombination = new int[k];
    }

    @Override
    public int[] getCurrentSequence() {
        return currentCombination;
    }

    public void computeNext() {
        int pos = k - 1;
        while(pos >= 0 && currentCombination[pos] >= n - k + pos) pos--;
        if(pos < 0) return;
        int val = currentCombination[pos];
        for(int i = pos; i < k; i++) {
            currentCombination[i] = ++val;
        }
    }

    public static class Long extends CombinationSupplier implements Splittable.LongIndexed<int[]> {
        private final long count;
        private long currentIndex = -2;

        public Long(long count, int n, int k) {
            super(n, k);
            this.count = count;
        }

        @Override
        public Long split() {
            return new Long(count, n, k);
        }

        @Override
        public int[] apply(long index) {
            boolean useNext = (index == currentIndex + 1);
            currentIndex = index;
            return getNextSequence(useNext);
        }

        /**
         * This implementation uses the UNRANKCOMB-D algorithm introduced in:
         * Kokosinski, Zbigniew, and Ikki-Machi Tsuruga. "Algorithms for unranking combinations and other related choice functions." (1995).
         */
        @Override
        public int[] unrank() {
            if(k == 0) return new int[0];
            int[] combi = new int[k];
            long rank = count - 1 - currentIndex;
            long e = (n - k) * count / n;
            int t = n - k + 1;
            int m = k;
            int p = n - 1;
            do {
                if(e <= rank) {
                    combi[k - m] = n - t - m + 1;
                    if(e > 0) {
                        rank = rank - e;
                        e = m * e / p;
                    }
                    m--;
                    p--;
                } else {
                    e = (p - m) * e / p;
                    t--;
                    p--;
                }
            } while(m > 0);
            return combi;
        }
    }

    public static class BigInt extends CombinationSupplier implements Splittable.BigIntegerIndexed<int[]> {
        private final BigInteger count;
        private BigInteger currentIndex = BigInteger.valueOf(-2);

        public BigInt(BigInteger count, int n, int k) {
            super(n, k);
            this.count = count;
        }

        @Override
        public BigInt split() {
            return new BigInt(count, n, k);
        }

        @Override
        public int[] apply(BigInteger index) {
            boolean useNext = index.equals(currentIndex.add(BigInteger.ONE));
            currentIndex = index;
            return getNextSequence(useNext);
        }

        /**
         * This implementation uses the UNRANKCOMB-D algorithm introduced in:
         * Kokosinski, Zbigniew, and Ikki-Machi Tsuruga. "Algorithms for unranking combinations and other related choice functions." (1995).
         */
        @Override
        public int[] unrank() {
            if(k == 0) return new int[0];
            int[] combi = new int[k];
            BigInteger rank = count.subtract(BigInteger.ONE).subtract(currentIndex);
            BigInteger e = count.multiply(BigInteger.valueOf(n - k)).divide(BigInteger.valueOf(n));
            int t = n - k + 1;
            int m = k;
            int p = n - 1;
            do {
                if(e.compareTo(rank) <= 0) {
                    combi[k - m] = n - t - m + 1;
                    if(e.compareTo(BigInteger.ZERO) > 0) {
                        rank = rank.subtract(e);
                        e = e.multiply(BigInteger.valueOf(m)).divide(BigInteger.valueOf(p));
                    }
                    m--;
                    p--;
                } else {
                    e = e.multiply(BigInteger.valueOf(p - m)).divide(BigInteger.valueOf(p));
                    t--;
                    p--;
                }
            } while(m > 0);
            return combi;
        }
    }
}
