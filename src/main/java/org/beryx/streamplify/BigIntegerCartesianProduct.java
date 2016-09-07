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
import java.util.Arrays;

public class BigIntegerCartesianProduct extends BigIntegerIndexedSpliterator<int[], BigIntegerCartesianProduct> {
    public BigIntegerCartesianProduct(int... dimensions) {
        this(count(dimensions), dimensions);
    }

    BigIntegerCartesianProduct(BigInteger count, int... dimensions) {
        super(BigInteger.ZERO, count);
        if(Arrays.stream(dimensions).anyMatch(dim -> dim < 0)) throw new IllegalArgumentException("Invalid dimensions: " + Arrays.toString(dimensions));
        setValueSupplier(new CartesianProductSupplier.BigInt(dimensions, count));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    protected static BigInteger count(int[] dimensions) {
        BigInteger cnt = BigInteger.ONE;
        for(long dim : dimensions) {
            cnt = cnt.multiply(BigInteger.valueOf(dim));
        }
        return cnt;
    }
}
