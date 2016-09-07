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

import org.beryx.streamplify.permutation.LongPermutations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BigIntegerIndexedSpliterator<T, S extends BigIntegerIndexedSpliterator<T,S>> implements Spliterator<T>, Streamable<T,S> {
    private static final Logger logger =  LoggerFactory.getLogger(LongPermutations.class);

    private Splittable.BigIntegerIndexed<T> valueSupplier;
    private BigInteger index;
    private final BigInteger fence;
    int characteristics = Spliterator.IMMUTABLE;

    protected BigIntegerIndexedSpliterator(BigInteger origin, BigInteger fence) {
        logger.trace("BigIntegerIndexedSpliterator({}, {})", origin, fence);
        if(origin.compareTo(BigInteger.ZERO) < 0 || fence.compareTo(origin) < 0) throw new IllegalArgumentException("origin: " + origin + ", fence: " + fence);
        this.index = origin;
        this.fence = fence;
    }

    @SuppressWarnings("unchecked")
	public final S withAdditionalCharacteristics(int additionalCharacteristics) {
        this.characteristics |= additionalCharacteristics;
        return (S)this;
    }

    public final void setValueSupplier(Splittable.BigIntegerIndexed<T> valueSupplier) {
        this.valueSupplier = valueSupplier;
    }

    protected BigInteger getIndex() {
        return index;
    }
    
    protected BigInteger getFence() {
        return fence;
    }

    @Override
    public long count() {
        BigInteger size = bigCount();
        if(size.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0) {
            return size.longValueExact();
        }
        return -1;
    }

    @Override
    public BigInteger bigCount() {
        return fence.subtract(index);
    }

    @Override
    public S skip(long n) {
        return skip(BigInteger.valueOf(n));
    }

    @Override
    public S skip(BigInteger n) {
        if(n.compareTo(BigInteger.ZERO) < 0) throw new IllegalArgumentException("skip(" + n + ")");
        BigInteger targetIndex = index.add(n);
        index = (targetIndex.compareTo(fence) >= 0) ? fence : targetIndex;
        return (S)this;
    }

    @Override
    public long estimateSize() {
        long size = count();
        return size < 0 ? Long.MAX_VALUE : size;
    }

    @Override
    public int characteristics() {
        return characteristics;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (action == null) throw new NullPointerException();
        if (index.compareTo(BigInteger.ZERO) >= 0 && index.compareTo(fence) < 0) {
            T val = valueSupplier.apply(index);
            index = index.add(BigInteger.ONE);
            action.accept(val);
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<T> trySplit() {
        BigInteger mid = index.add(fence).divide(BigInteger.valueOf(2));
        if(index.compareTo(mid) >= 0) return null;
        BigIntegerIndexedSpliterator<T,S> spliterator = new BigIntegerIndexedSpliterator<>(index, mid);
        spliterator.withAdditionalCharacteristics(characteristics);
        spliterator.setValueSupplier(valueSupplier.split());
        index = mid;
        return spliterator;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        if (action == null) throw new NullPointerException();
        if(index.compareTo(BigInteger.ZERO) >= 0 && index.compareTo(fence) < 0) {
            for(BigInteger i = index; i.compareTo(fence) < 0; i = i.add(BigInteger.ONE)) {
                T val = valueSupplier.apply(i);
                action.accept(val);
            }
            index = fence;
        }
    }

    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(this, false);
    }

    @Override
    public Stream<T> parallelStream() {
        return StreamSupport.stream(this, true);
    }
}
