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

public class BigIntegerPermutations extends BigIntegerIndexedSpliterator<int[], BigIntegerPermutations> {
    public BigIntegerPermutations(int length) {
        this(factorial(length), length);
    }

    BigIntegerPermutations(BigInteger factorial, int length) {
        super(BigInteger.ZERO, factorial);
        if(length < 0) throw new IllegalArgumentException("Invalid permutation length: " + length);
        setValueSupplier(new PermutationSupplier.BigInt(length));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    public static BigInteger factorial(int n) {
        if(n > Permutations.MAX_LENGTH) throw new IllegalArgumentException("Value too big: " + n);
        BigInteger fact = BigInteger.ONE;
        for(int i = 2; i <= n; i++) {
            fact = fact.multiply(BigInteger.valueOf(i));
        }
        return fact;
    }
}
