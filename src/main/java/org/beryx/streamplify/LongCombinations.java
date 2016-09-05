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
import java.util.function.LongFunction;

public class LongCombinations extends LongIndexedSpliterator<int[], LongCombinations> {
    private final int n;
    private final int k;

    public LongCombinations(int n, int k) {
        super(0, count(n, k));
        if(n < 0 || k < 0 || n < k) throw new IllegalArgumentException("Invalid (n,k): (" + n + "," + k + ")");
        this.n = n;
        this.k = k;
        this.setValueSupplier(new ValueSupplier(getFence()));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    private class ValueSupplier implements LongFunction<int[]> {
        private final long count;
        
        ValueSupplier(long count) {
            this.count = count;
        }
        
        /**
         * This implementation uses the UNRANKCOMB-D algorithm introduced in:
         * Kokosinski, Zbigniew, and Ikki-Machi Tsuruga. "Algorithms for unranking combinations and other related choice functions." (1995).
         */
        @Override
        public int[] apply(long index) {
            if(k == 0) return new int[0];
            int[] val = new int[k];
            long rank = count - 1 - index;
            long e = (n - k) * count / n;
            int t = n - k + 1;
            int m = k;
            int p = n - 1;
            do {
                if(e <= rank) {
                    val[k - m] = n - t - m + 1;
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
            return val;
        }        
    }

    protected static long count(int n, int k) {
        BigInteger bigCount = BigIntegerCombinations.count(n, k);
        BigInteger maxVal = bigCount.multiply(BigInteger.valueOf(n - 1));
        if(maxVal.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) >= 0) throw new IllegalArgumentException("Combination arguments too big: " + n + ", " + k);
        return bigCount.longValueExact();
    }
}
