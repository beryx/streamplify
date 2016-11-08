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
package org.beryx.streamplify.powerset;

import org.beryx.streamplify.LongIndexedSpliterator;

/**
 * Provides stream of power set.
 * <br>Can be used for permutations with a maximum length of {@value #MAX_LENGTH}.
 * For bigger values, a {@link BigIntegerPowerSet} is needed.
 */
@SuppressWarnings("unchecked")
public class LongPowerSet extends LongIndexedSpliterator<int[], LongPowerSet> {

    public static final int MAX_LENGTH = 63;

    /**
     * Constructs power set for {@code length} number of elements.
     */
    public LongPowerSet(int length) {
        super(0, powerOfTwo(length));
        this.withValueSupplier(new PowerSetSupplier.Long(length));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    /**
     * Calculates 2 ^ length.
     * 
     * @throws {@link IllegalArgumentException} if length < 0 or length >= 63.
     */
    private static long powerOfTwo(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Invalid length of power set");
        }
        if (length >= MAX_LENGTH) {
            throw new IllegalArgumentException("Size of power set too long for length " + length);
        }
        return (long) 1 << length;
    }
}
