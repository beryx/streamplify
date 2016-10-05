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
package org.beryx.streamplify.combination;

import org.beryx.streamplify.LongIndexedSpliterator;

import java.math.BigInteger;

/**
 * Provides streams of combinations.
 * <br>Can be used when the binomial coefficient (n choose k) is a number that can fit in a long.
 * For bigger values, a {@link BigIntegerCombinations} is needed.
 */
public class LongCombinations extends LongIndexedSpliterator<int[], LongCombinations> {
    /**
     * {@code k}-combinations from a set of {@code n} elements
     */
    public LongCombinations(int n, int k) {
        this(count(n, k), n, k);
    }

    LongCombinations(long count, int n, int k) {
        super(0, count);
        if(n < 0 || k < 0 || n < k) throw new IllegalArgumentException("Invalid (n,k): (" + n + "," + k + ")");
        this.withValueSupplier(new CombinationSupplier.Long(count, n, k));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    protected static long count(int n, int k) {
        BigInteger bigCount = BigIntegerCombinations.count(n, k);
        BigInteger maxVal = bigCount.multiply(BigInteger.valueOf(n - 1));
        if(maxVal.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) >= 0) throw new IllegalArgumentException("Combination arguments too big: " + n + ", " + k);
        return bigCount.longValueExact();
    }
}
