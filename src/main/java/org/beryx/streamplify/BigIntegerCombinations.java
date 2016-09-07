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

public class BigIntegerCombinations extends BigIntegerIndexedSpliterator<int[], BigIntegerCombinations> {
    public BigIntegerCombinations(int n, int k) {
        this(count(n, k), n, k);
    }

    BigIntegerCombinations(BigInteger count, int n, int k) {
        super(BigInteger.ZERO, count);
        if(n < 0 || k < 0 || n < k) throw new IllegalArgumentException("Invalid (n,k): (" + n + "," + k + ")");
        setValueSupplier(new CombinationSupplier.BigInt(count, n, k));
        this.withAdditionalCharacteristics(DISTINCT);
    }

    protected static BigInteger count(int n, int k) {
        if(n > Combinations.MAX_N) throw new IllegalArgumentException("Value too big: " + n);
        BigInteger cnt = BigInteger.ONE;
        for(int i = 0; i < k; i++) {
            cnt = cnt.multiply(BigInteger.valueOf(n - i));
            cnt = cnt.divide(BigInteger.valueOf(i + 1));
        }
        return cnt;
    }
}
