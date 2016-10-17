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
package org.beryx.streamplify.derangement;

import org.beryx.streamplify.LongIndexedSpliterator;

/**
 * Provides streams of derangements.
 * <br>Can be used for derangements with a maximum length of {@value #MAX_LENGTH}.
 * For bigger values, a {@link BigIntegerDerangements} is needed.
 */
public class LongDerangements extends LongIndexedSpliterator<int[], LongDerangements> {
    public static final int MAX_LENGTH = 21;

    /**
     * Constructs derangements of {@code length} elements
     */
    public LongDerangements(int length) {
        super(0, subfactorial(length));
        this.withValueSupplier(new DerangementSupplier.Long(length));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    /**
     * @throws IllegalArgumentException if {@code n} is negative or the result does not fit in a long (that is, {@code n} > {@value #MAX_LENGTH})
     */
    public static long subfactorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Invalid derangement length: " + n);
        if (n > MAX_LENGTH) throw new IllegalArgumentException("Derangement length too big: " + n);
        if (n == 0) return 1;
        long prev = 1;
        long curr = 0;
        for (int i = 2; i <= n; ++i) {
            long next = (i - 1)*(curr + prev);
            prev = curr;
            curr = next;
        }
        return curr;
    }
}
