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
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.beryx.streamplify.permutation.LongPermutations;
import org.beryx.streamplify.shuffler.DefaultLongShuffler;
import org.beryx.streamplify.shuffler.LongShuffler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongIndexedSpliterator<T, S extends LongIndexedSpliterator<T, S>> implements Spliterator<T>, Streamable<T, S> {
    private static final Logger logger =  LoggerFactory.getLogger(LongPermutations.class);

    private Splittable.LongIndexed<T> valueSupplier;
    private long index;
    private final long fence;
    private int characteristics =  Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE;
    private LongShuffler shuffler = LongShuffler.IDENTITY;

    protected LongIndexedSpliterator(long origin, long fence) {
    	logger.trace("LongIndexedSpliterator({}, {})", origin, fence);
        if(origin < 0 || fence < origin) throw new IllegalArgumentException("origin: " + origin + ", fence: " + fence);
        this.index = origin;
        this.fence = fence;
    }

    @SuppressWarnings("unchecked")
	public final S withAdditionalCharacteristics(int additionalCharacteristics) {
        this.characteristics |= additionalCharacteristics;
        return (S)this;
    }

    public final void setValueSupplier(Splittable.LongIndexed<T> valueSupplier) {
        this.valueSupplier = valueSupplier;
    }

    protected final long getIndex() {
        return index;
    }
    
    protected final long getFence() {
        return fence;
    }

    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(this, false);
    }

    @Override
    public Stream<T> parallelStream() {
        return StreamSupport.stream(this, true);
    }

    @Override
    public long count() {
        return fence - index;
    }

    @Override
    public BigInteger bigCount() {
        return BigInteger.valueOf(count());
    }

    @Override
    public S skip(long n) {
        if(n < 0) throw new IllegalArgumentException("skip(" + n + ")");
        index = (fence - index <= n) ? fence : (index + n);
        return (S)this;
    }

    @Override
    public S skip(BigInteger bigN) {
        if(bigN.compareTo(BigInteger.ZERO) < 0) throw new IllegalArgumentException("skip(" + bigN + ")");
        if(bigN.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
            index = fence;
        } else {
            long n = bigN.longValueExact();
            index = (fence - index <= n) ? fence : (index + n);
        }
        return (S)this;
    }

    @Override
    public long estimateSize() {
        return fence - index;
    }

    @Override
    public int characteristics() {
        return characteristics;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (action == null) throw new NullPointerException();
        if (index >= 0 && index < fence) {
            long shuffledIndex = shuffler.getShuffledIndex(index);
            T val = valueSupplier.apply(shuffledIndex);
            index++;
            action.accept(val);
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<T> trySplit() {
        long mid = (index + fence) >>> 1;
        if(index >= mid) return null;
        LongIndexedSpliterator<T,S> spliterator = new LongIndexedSpliterator<>(index, mid);
        spliterator.withAdditionalCharacteristics(characteristics);
        spliterator.setValueSupplier(valueSupplier.split());
        spliterator.setShuffler(shuffler);
        index = mid;
        return spliterator;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        if (action == null) throw new NullPointerException();
        if(index >= 0 && index < fence) {
            for(long i = index; i < fence; i++) {
                long shuffledIndex = shuffler.getShuffledIndex(i);
                T val = valueSupplier.apply(shuffledIndex);
                action.accept(val);
            }
            index = fence;
        }
    }

    protected void setShuffler(LongShuffler shuffler) {
        this.shuffler = shuffler;
    }

    @Override
    public S shuffle() {
        shuffler = new DefaultLongShuffler(fence);
        return (S)this;
    }
}
