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
package org.beryx.streamplify.shuffler;

import java.math.BigInteger;
import java.util.Random;

public class DefaultBigIntegerShuffler implements BigIntegerShuffler {
    private final BigInteger count;
    private final ShufflerImpl shufflerImpl;

    public DefaultBigIntegerShuffler(BigInteger count) {
        this(count, new Random());
    }

    public DefaultBigIntegerShuffler(BigInteger count, Random rnd) {
        this.count = count;
        this.shufflerImpl = new ShufflerImpl(rnd);
    }

    public DefaultBigIntegerShuffler withSeed(long seed) {
        shufflerImpl.withSeed(seed);
        return this;
    }

    @Override
    public BigInteger getShuffledIndex(BigInteger index) {
        return shufflerImpl.getShuffledIndex(index, count);
    }
}
