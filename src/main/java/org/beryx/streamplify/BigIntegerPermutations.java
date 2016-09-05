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

public class BigIntegerPermutations extends BigIntegerIndexedSpliterator<int[], BigIntegerPermutations> {
    private final int length;
    private final BigInteger[] divisors;

    public BigIntegerPermutations(int length) {
        super(BigInteger.ZERO, factorial(length));
        if(length < 0) throw new IllegalArgumentException("Invalid permutation length: " + length);
        this.length = length;
        this.divisors = computeDivisors(length);
        setValueSupplier(this::getAt);
        this.withAdditionalCharacteristics(DISTINCT);
    }

    private int[] getAt(BigInteger index) {
        int[] perm = new int[length];
        for(int i = 0; i < length; i++)
            perm[i] = i;

        BigInteger dividend = index;
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

    public static BigInteger factorial(int n) {
        if(n > Permutations.MAX_LENGTH) throw new IllegalArgumentException("Value too big: " + n);
        BigInteger fact = BigInteger.ONE;
        for(int i = 2; i <= n; i++) {
            fact = fact.multiply(BigInteger.valueOf(i));
        }
        return fact;
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
