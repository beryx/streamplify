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

import java.util.Random;

/**
 * An implementation of {@link LongShuffler} based on {@link ShufflerImpl}.
 */
public class DefaultLongShuffler implements LongShuffler {
    private final long count;
    private final ShufflerImpl shufflerImpl;

    /**
     * Constructs a shuffler for indices in the range [0 .. {@code count} - 1].
     * @param count the number of indices in the range
     */
    public DefaultLongShuffler(long count) {
        this(count, new Random());
    }

    /**
     * Constructs a shuffler for indices in the range [0 .. {@code count} - 1], specifying the random number generator to be used.
     * @param count the number of indices in the range
     * @param rnd the random number generator to be used
     */
    public DefaultLongShuffler(long count, Random rnd) {
        this.count = count;
        this.shufflerImpl = new ShufflerImpl(rnd);
    }

    /**
     * @param seed the seed of the random generator used by this instance
     * @return this instance
     */
    public DefaultLongShuffler withSeed(long seed) {
        shufflerImpl.withSeed(seed);
        return this;
    }

    @Override
    public long getShuffledIndex(long index) {
        return shufflerImpl.getShuffledIndex(index, count);
    }
}
