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

import org.beryx.streamplify.Streamable;
import org.beryx.streamplify.StreamableProxy;

import java.math.BigInteger;

public class Permutations extends StreamableProxy<int[], Permutations> {
    public static final int MAX_LENGTH = 20_000;

    private final Streamable<int[], ?> delegate;

    public Permutations(int length) {
        BigInteger count = BigIntegerPermutations.factorial(length);
        if(count.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) < 0) {
            delegate = new LongPermutations(count.longValueExact(), length);
        } else {
            delegate = new BigIntegerPermutations(count, length);
        }
    }

    @Override
    public Streamable<int[], ?> getDelegate() {
        return delegate;
    }
}
