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
import java.util.Random;
import java.util.Spliterator;
import java.util.stream.Stream;

/**
 * Objects implementing this interface provide data in form of sequential or parallel {@link Stream}s.
 */
public interface Streamable<T, S extends Streamable<T,S>> {
    /** @return a sequential {@code Stream} */
    Stream<T> stream();

    /** @return a possibly parallel {@code Stream} */
    Stream<T> parallelStream();

    /** @return the number of elements in the data source, or -1 if the number is too big to fit in a long. **/
    long count();

    /** @return the number of elements in the data source as BigInteger. **/
    BigInteger bigCount();

    /**
     * Adds {@code additionalCharacteristics} to the provided streams.
     * @return this instance
     */
    S withAdditionalCharacteristics(int additionalCharacteristics);

    /**
     * Convenience method for adding the {@link Spliterator#ORDERED} characteristic to the provided streams.
     * @return this instance
     */
    default S ordered() {
    	return withAdditionalCharacteristics(Spliterator.ORDERED);
    }

    /**
     * Configure this instance to provide streams that skip the first {}@code n} elements in the data source.
     * It is usually more efficient to call this method instead of {@link Stream#skip(long)}.
     * @param n the number of elements to be skipped (as a long).
     * @return depending on the implementation, it may return this instance or another Streamable.
     */
    <Z extends Streamable<T,?>> Z skip(long n);

    /**
     * Configure this instance to provide streams that skip the first {}@code n} elements in the data source.
     * @param n the number of elements to be skipped (as a BigInteger).
     * @return depending on the implementation, it may return this instance or another Streamable.
     */
    <Z extends Streamable<T,?>> Z skip(BigInteger n);

    /**
     * Configure this instance to provide streams that shuffle elements in the data source.
     * @param random the random number generator to be used to perform the shuffling.
     * @return depending on the implementation, it may return this instance or another Streamable.
     */
    <Z extends Streamable<T,?>> Z shuffle(Random random);

    /**
     * Configure this instance to provide streams that shuffle elements in the data source.
     * <br>This default implementation calls {@link #shuffle(Random)} with a random number generator created using the parameterless constructor of {@link Random}.
     * @return depending on the implementation, it may return this instance or another Streamable.
     */
    default <Z extends Streamable<T,?>> Z shuffle() {
        return shuffle(new Random());
    }
}
