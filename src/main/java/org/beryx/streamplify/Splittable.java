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
import java.util.function.Function;
import java.util.function.LongFunction;

@FunctionalInterface
public interface Splittable<S extends Splittable<S>> {
    interface LongIndexed<T> extends LongFunction<T>, Splittable<LongIndexed<T>> {
        LongIndexed<Long> IDENTITY = new LongIndexed<Long>() {
            @Override
            public LongIndexed<Long> split() {
                return this;
            }

            @Override
            public Long apply(long value) {
                return value;
            }
        };
    }

    interface BigIntegerIndexed<T> extends Function<BigInteger, T>, Splittable<BigIntegerIndexed<T>> {
        BigIntegerIndexed<BigInteger> IDENTITY = new BigIntegerIndexed<BigInteger>() {
            @Override
            public BigIntegerIndexed<BigInteger> split() {
                return this;
            }

            @Override
            public BigInteger apply(BigInteger value) {
                return value;
            }
        };

    }

    S split();
}
