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

import org.beryx.streamplify.LongIndexedSpliterator;

import java.math.BigInteger;

public class LongPermutations extends LongIndexedSpliterator<int[], LongPermutations> {
    public LongPermutations(int length) {
        this(factorial(length), length);
    }

    LongPermutations(long factorial, int length) {
        super(0, factorial);
        if(length < 0) throw new IllegalArgumentException("Invalid permutation length: " + length);
        setValueSupplier(new PermutationSupplier.Long(length));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    protected static long factorial(int len) {
        BigInteger bigCount = BigIntegerPermutations.factorial(len);
        if(bigCount.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) >= 0) throw new IllegalArgumentException("Permutation length too big: " + len);
        return bigCount.longValueExact();
    }
}
