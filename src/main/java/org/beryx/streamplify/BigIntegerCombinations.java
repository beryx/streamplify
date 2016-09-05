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
import java.util.function.Function;

public class BigIntegerCombinations extends BigIntegerIndexedSpliterator<int[], BigIntegerCombinations> {
    private final int n;
    private final int k;

    public BigIntegerCombinations(int n, int k) {
        super(BigInteger.ZERO, count(n, k));
        if(n < 0 || k < 0 || n < k) throw new IllegalArgumentException("Invalid (n,k): (" + n + "," + k + ")");
        this.n = n;
        this.k = k;
        setValueSupplier(new ValueSupplier(getFence()));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    private class ValueSupplier implements Function<BigInteger, int[]> {
        private final BigInteger count;
        
        ValueSupplier(BigInteger count) {
            this.count = count;
        }
        
        /**
         * This implementation uses the UNRANKCOMB-D algorithm introduced in:
         * Kokosinski, Zbigniew, and Ikki-Machi Tsuruga. "Algorithms for unranking combinations and other related choice functions." (1995).
         */
        @Override
        public int[] apply(BigInteger index) {
            if(k == 0) return new int[0];
            int[] val = new int[k];
            BigInteger rank = count.subtract(BigInteger.ONE).subtract(index);
            BigInteger e = count.multiply(BigInteger.valueOf(n - k)).divide(BigInteger.valueOf(n));
            int t = n - k + 1;
            int m = k;
            int p = n - 1;
            do {
                if(e.compareTo(rank) <= 0) {
                    val[k - m] = n - t - m + 1;
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
            return val;
        }        
    }
    
    protected static BigInteger count(int n, int k) {
        if(n > Combinations.MAX_N) throw new IllegalArgumentException("Value too big: " + n);
        BigInteger cnt = BigInteger.ONE;
        for(int i = 0; i < k; i++) {
            cnt = cnt.multiply(BigInteger.valueOf(n - i));
            cnt = cnt.divide(BigInteger.valueOf(i + 1));
        }
        return cnt;
    }
}
