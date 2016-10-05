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

import org.beryx.streamplify.BigIntegerIndexedSpliterator;

import java.math.BigInteger;

/**
 * Provides streams of permutations.
 * <br>For permutations with a length <= 20, you may consider using the more efficient {@link LongPermutations}.
 */
public class BigIntegerPermutations extends BigIntegerIndexedSpliterator<int[], BigIntegerPermutations> {
    public static final int MAX_LENGTH = 20_000;

    /**
     * Constructs permutations of {@code length} elements
     */
    public BigIntegerPermutations(int length) {
        super(BigInteger.ZERO, factorial(length));
        this.withValueSupplier(new PermutationSupplier.BigInt(length));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    /**
     * @throws IllegalArgumentException if {@code n} is negative or too big (> {@value #MAX_LENGTH})
     */
    public static BigInteger factorial(int n) {
        if(n < 0) throw new IllegalArgumentException("Invalid permutation length: " + n);
        if(n > MAX_LENGTH) throw new IllegalArgumentException("Value too big: " + n);
        BigInteger fact = BigInteger.ONE;
        for(int i = 2; i <= n; i++) {
            fact = fact.multiply(BigInteger.valueOf(i));
        }
        return fact;
    }
}
