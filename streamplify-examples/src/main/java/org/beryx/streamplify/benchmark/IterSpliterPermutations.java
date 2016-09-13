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
package org.beryx.streamplify.benchmark;

import org.beryx.streamplify.permutation.LongPermutations;
import org.beryx.streamplify.permutation.PermutationSupplier;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class is functionally equivalent to {@link LongPermutations}, but it doesn't use the {@link org.beryx.streamplify.LongIndexedSpliterator}.
 * <br>Instead, it uses the {@code IteratorSpliterator} created by {@link Spliterators#spliterator(Iterator, long, int)}.
 * <br>{@link NQueensBenchmark} compares the performance of this class with that of {@link LongPermutations}.
 */
public class IterSpliterPermutations {
    private final int length;
    private final long count;

    public IterSpliterPermutations(int length) {
        this.length = length;
        this.count = LongPermutations.factorial(length);
    }

    private class PermutationIterator implements Iterator<int[]> {
        private long index = 0;
        private final PermutationSupplier.Long supplier = new PermutationSupplier.Long(length);

        @Override
        public boolean hasNext() {
            return index < count;
        }

        @Override
        public int[] next() {
            return supplier.apply(index++);
        }
    }

    public Stream<int[]> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public Stream<int[]> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    private Spliterator<int[]> spliterator() {
        PermutationIterator permIterator = new PermutationIterator();
        return Spliterators.spliterator(permIterator, count,
                Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE | Spliterator.DISTINCT);
    }
}
