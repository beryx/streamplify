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

import java.math.BigInteger;

import org.beryx.streamplify.BigIntegerIndexedSpliterator;

/**
 * Provides stream of power set for the given length.
 * <br>For length < 63, you may consider using the more efficient {@link LongPowerSet}.
 */
@SuppressWarnings("unchecked")
public class BigIntegerPowerSet extends BigIntegerIndexedSpliterator<int[], BigIntegerPowerSet> {

    private static final int MAX_LENGTH = 512;

    /**
     * Constructs power set for {@code length} number of elements.
     */
    public BigIntegerPowerSet(int length) {
        super(BigInteger.ZERO, powerOfTwo(length));
        this.withValueSupplier(new PowerSetSupplier.BigInt(length));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    /**
     * Calculates 2 ^ length.
     * 
     * @throws {@link IllegalArgumentException} if length < 0 or length >= 512
     */
    private static BigInteger powerOfTwo(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Invalid length of power set");
        }
        if (length >= MAX_LENGTH) {
            throw new IllegalArgumentException("Power set size will be too big for length " + length);
        }
        return BigInteger.ONE.shiftLeft(length);
    }

}
