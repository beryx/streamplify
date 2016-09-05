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

public class LongPermutations extends LongIndexedSpliterator<int[], LongPermutations> {
    private final int length;
    private final long[] divisors;

    public LongPermutations(int length) {
        super(0, count(length));
        if(length < 0) throw new IllegalArgumentException("Invalid permutation length: " + length);
        this.length = length;
        this.divisors = computeDivisors(length);
        setValueSupplier(this::getAt);
        this.withAdditionalCharacteristics(DISTINCT);
    }

    private int[] getAt(long index) {
        int[] perm = new int[length];
        for(int i = 0; i < length; i++)
            perm[i] = i;

        long dividend = index;
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

    protected static long count(int len) {
        BigInteger bigCount = BigIntegerPermutations.factorial(len);
        if(bigCount.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) >= 0) throw new IllegalArgumentException("Permutation length too big: " + len);
        return bigCount.longValueExact();
    }

    static long[] computeDivisors(int len) {
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
