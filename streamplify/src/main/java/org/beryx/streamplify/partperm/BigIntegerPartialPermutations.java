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

import org.beryx.streamplify.BigIntegerIndexedSpliterator;

import java.math.BigInteger;

/**
 * Provides streams of partial permutations.
 * <br>For permutations with a length <= 18, you may consider using the more efficient {@link LongPartialPermutations}.
 */
public class BigIntegerPartialPermutations extends BigIntegerIndexedSpliterator<int[], BigIntegerPartialPermutations> {
    public static final int MAX_LENGTH = 10_000;

    /**
     * Constructs partial permutations of {@code length} elements
     */
    public BigIntegerPartialPermutations(int length) {
        super(BigInteger.ZERO, numberOfPermutations(length));
        this.withValueSupplier(new PartialPermutationSupplier.BigInt(length));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    /**
     * @throws IllegalArgumentException if {@code n} is negative or too big (> {@value #MAX_LENGTH})
     */
    public static BigInteger numberOfPermutations(int n) {
        if (n < 0) throw new IllegalArgumentException("Invalid partial permutation length: " + n);
        if (n > MAX_LENGTH) throw new IllegalArgumentException("Partial permutation length too big: " + n);

        BigInteger[] factorials = computeFactorials(n);
        BigInteger numberOfPermutations = BigInteger.ZERO;
        for (int i = 0; i <= n; i++) {
            numberOfPermutations = numberOfPermutations.add(computeNextNcK(n, factorials, i));
        }
        return numberOfPermutations;
    }

    public static BigInteger[] computeFactorials(int length) {
        BigInteger[] factorials = new BigInteger[length + 1];
        factorials[0] = BigInteger.ONE;
        for (int i = 1; i <= length; i++) {
            factorials[i] = factorials[i - 1].multiply(BigInteger.valueOf(i));
        }
        return factorials;
    }

    private static BigInteger computeNextNcK(int n, BigInteger[] factorials, int i) {
        BigInteger nCk = factorials[n].divide(factorials[i].multiply(factorials[n - i]));
        return factorials[i].multiply(nCk.pow(2));
    }

}
