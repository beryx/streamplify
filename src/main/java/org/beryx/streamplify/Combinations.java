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
package org.beryx.streamplify;

import java.math.BigInteger;
import java.util.stream.Stream;
import static org.beryx.streamplify.BigIntegerCombinations.count;

public class Combinations implements Streamable<int[], Combinations> {
    public static final int MAX_N = 50_000;

    private final Streamable<int[], ?> delegate;

    public Combinations(int n, int k) {
        BigInteger count = count(n, k);
        BigInteger maxVal = count.multiply(BigInteger.valueOf(n - 1));
        if(maxVal.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) < 0) {
            delegate = new LongCombinations(n, k);
        } else {
            delegate = new BigIntegerCombinations(n, k);
        }
    }

    public Combinations withAdditionalCharacteristics(int additionalCharacteristics) {
        delegate.withAdditionalCharacteristics(additionalCharacteristics);
        return this;
    }

    protected Streamable<int[], ?> getDelegate() {
        return delegate;
    }

    @Override
    public Stream<int[]> stream() {
        return delegate.stream();
    }

    @Override
    public Stream<int[]> parallelStream() {
        return delegate.parallelStream();
    }
}
