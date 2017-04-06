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

import org.beryx.streamplify.LongIndexedSpliterator;

/**
 * Provides streams of partial permutations.
 * <br>Can be used for partial permutations with a maximum length of {@value #MAX_LENGTH}.
 * For bigger values, a {@link BigIntegerPartialPermutations} is needed.
 */
public class LongPartialPermutations extends LongIndexedSpliterator<int[], LongPartialPermutations> {
    public static final int MAX_LENGTH = 18;

    /**
     * Constructs partial permutations of {@code length} elements
     */
    public LongPartialPermutations(int length) {
        super(0, numberOfPermutations(length));
        this.withValueSupplier(new PartialPermutationSupplier.Long(length));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    /**
     * @throws IllegalArgumentException if {@code n} is negative or the result does not fit in a long (that is, {@code n} > {@value #MAX_LENGTH})
     */
    public static long numberOfPermutations(int n) {
        if (n < 0) throw new IllegalArgumentException("Invalid partial permutation length: " + n);
        if (n > MAX_LENGTH) throw new IllegalArgumentException("Partial permutation length too big: " + n);
        if (n == 0) return 1;
        if (n == 1) return 2;
        return 2 * n * numberOfPermutations(n - 1) - (long) Math.pow(n - 1, 2) * numberOfPermutations(n - 2);
    }

}
