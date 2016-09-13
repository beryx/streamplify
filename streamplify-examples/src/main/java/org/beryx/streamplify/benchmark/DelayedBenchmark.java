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

import org.beryx.streamplify.LongIndexedSpliterator;
import org.beryx.streamplify.Splittable;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This benchmark shows that in situations where the iterator's next() method is time consuming,
 * the {@code IteratorSpliterator} created by {@link Spliterators#spliterator(Iterator, long, int)} is not adequate for parallel processing,
 * and the {@link LongIndexedSpliterator} should be used instead.
 */
public class DelayedBenchmark {
    private final long count;
    private final long delay;

    public DelayedBenchmark(long count, long delay) {
        this.count = count;
        this.delay = delay;
    }

    public class DelayedIdentitySpliterator extends LongIndexedSpliterator<Long, DelayedIdentitySpliterator> {
        DelayedIdentitySpliterator() {
            super(0, count);
            setValueSupplier(new Splittable.LongIndexed<Long>() {
                @Override
                public Long apply(long value) {
                    busy(delay);
                    return value;
                }

                @Override
                public LongIndexed<Long> split() {
                    return this;
                }
            });
            this.withAdditionalCharacteristics(DISTINCT);
        }
    }

    public class DelayedIdentityIterator implements Iterator<Long> {
        private long index = 0;

        @Override
        public boolean hasNext() {
            return index < count;
        }

        @Override
        public Long next() {
            busy(delay);
            return index++;
        }
    }

    private static void busy(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.err.println("Interrupted");
        }
    }

    public Stream<Long> getStreamplifyStream() {
        return new DelayedIdentitySpliterator().parallelStream();
    }

    public Stream<Long> getIterSpliterStream() {
        Spliterator<Long> spliterator = Spliterators.spliterator(new DelayedIdentityIterator(), count,
                Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE | Spliterator.DISTINCT);
        return StreamSupport.stream(spliterator, true);
    }


    public void run() {
        System.out.println("########################################################");
        System.out.println("Running DelayedBenchmark for count " + count + " with delay " + delay + " ms.");

        long start1 = System.currentTimeMillis();
        Stream<Long> stream1 = getStreamplifyStream();
        long count1 = stream1.count();
        long duration1 = System.currentTimeMillis() - start1;

        System.out.println("duration(streamplify) = " + duration1 + " ms.");

        long start2 = System.currentTimeMillis();
        Stream<Long> stream2 = getIterSpliterStream();
        long count2 = stream2.count();
        long duration2 = System.currentTimeMillis() - start2;

        if(count1 != count2) throw new AssertionError("count1 = " + count1 + ", count2 = " + count2);

        System.out.println("duration(IterSpliter) = " + duration2 + " ms.");
        System.out.println("--------------------------------------------------------");
    }

    /**
     * Compares the performance of the implementation using {@link LongIndexedSpliterator} with
     * that of the {@code IteratorSpliterator} created by {@link Spliterators#spliterator(Iterator, long, int)}.
     * <br>On multicore and multiprocessor systems, the implementation based on {@link LongIndexedSpliterator} is typically faster.
     */
    public static void main(String[] args) {
        new DelayedBenchmark(1000, 10).run();
        new DelayedBenchmark(1000, 100).run();
        new DelayedBenchmark(10000, 10).run();
        new DelayedBenchmark(100, 1000).run();
    }
}
