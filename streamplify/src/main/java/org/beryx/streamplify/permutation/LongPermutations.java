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

/**
 * Provides streams of permutations.
 * <br>Can be used for permutations with a maximum length of {@value #MAX_LENGTH}.
 * For bigger values, a {@link BigIntegerPermutations} is needed.
 */
public class LongPermutations extends LongIndexedSpliterator<int[], LongPermutations> {
    public static final int MAX_LENGTH = 20;

    /**
     * Constructs permutations of {@code length} elements
     */
    public LongPermutations(int length) {
        super(0, factorial(length));
        this.withValueSupplier(new PermutationSupplier.Long(length));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    /**
     * @throws IllegalArgumentException if {@code n} is negative or the result does not fit in a long (that is, {@code n} > {@value #MAX_LENGTH})
     */
    public static long factorial(int n) {
        if(n < 0) throw new IllegalArgumentException("Invalid permutation length: " + n);
        if(n > MAX_LENGTH) throw new IllegalArgumentException("Permutation length too big: " + n);
        long fact = 1;
        for(int i = 2; i <= n; i ++) fact *= i;
        return fact;
    }
}
