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
package org.beryx.streamplify.example;

import org.beryx.streamplify.LongIndexedSpliterator;
import org.beryx.streamplify.Splittable;

import java.math.BigInteger;
import java.util.Spliterator;
import java.util.stream.Collectors;

/**
 * Provides Jacobsthal numbers in a given interval of indices as a stream of BigInteger.
 * <br>The implementation illustrates the use of a {@link LongIndexedSpliterator} and the construction of its value supplier.
 * <br>The {@link Supplier#apply(long)} method computes the Jacobsthal number with the index given by its argument as follows:<ul>
 * <li>if the requested index is the successor of the previously computed index, it uses the recurrence formula: J(n) = J(n-1) + 2 * J(n-2)</li>
 * <li>otherwise, it uses the closed-form solution: J(n) = (2^n - (-1)^n) / 3  </li>
 * </ul>
 * (Note that the closed-form solution is computationally cheap.
 * See {@link Fibonacci} for an example of a series where the closed-form solution involves time consuming computations, thus prohibiting its use.)
 */
@SuppressWarnings("unchecked")
public class Jacobsthal extends LongIndexedSpliterator<BigInteger, Jacobsthal> {
    public Jacobsthal(int fromValue, int count) {
        super(fromValue, fromValue + count);
        if(fromValue < 0 || count < 0 || fromValue > Integer.MAX_VALUE - count) {
            throw new IllegalArgumentException("fromValue: " + fromValue + ", count: " + count);
        }
        this.withValueSupplier(new Supplier());
        this.withAdditionalCharacteristics(Spliterator.DISTINCT);
    }

    private static class Supplier implements Splittable.LongIndexed<BigInteger> {

        long currentIndex = -2;
        BigInteger lastVal;
        BigInteger beforeLastVal;

        @Override
        public BigInteger apply(long index) {
            if(index < 0 || index > Integer.MAX_VALUE) throw new AssertionError("index: " + index);
            if(index == 0) return BigInteger.ZERO;
            boolean useNext = (index == currentIndex + 1);
            currentIndex = index;
            if(useNext) {
                BigInteger newVal = lastVal.add(beforeLastVal.shiftLeft(1));
                beforeLastVal = lastVal;
                lastVal = newVal;
            } else {
                beforeLastVal = forIndex((int)index - 1);
                lastVal = forIndex((int)index);
            }
            return lastVal;
        }

        /** Computes the Jacobsthal number with the given index using the closed-form equation: J(n) = (2^n - (-1)^n) / 3 */
        private static BigInteger forIndex(int n) {
            if(n == 0) return BigInteger.ZERO;
            BigInteger val = BigInteger.ONE.shiftLeft(n);
            if(n % 2 == 0) {
                val = val.subtract(BigInteger.ONE);
            } else {
                val = val.add(BigInteger.ONE);
            }
            return val.divide(BigInteger.valueOf(3));
        }

        @Override
        public LongIndexed<BigInteger> split() {
            return new Supplier();
        }
    }

    /**
     * Prints some Jacobsthal numbers.
     */
    public static void main(String[] args) {
        System.out.println("20 Jacobsthal numbers (starting with index 10):\n" + new Jacobsthal(10, 20).stream().collect(Collectors.toList()));
    }
}
