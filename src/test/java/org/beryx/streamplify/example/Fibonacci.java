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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Spliterator;
import java.util.stream.Collectors;

/**
 * Provides Fibonacci numbers in a given interval of indices as a stream of BigInteger.
 * <br>The implementation illustrates the use of a {@link LongIndexedSpliterator} and the construction of its value supplier.
 * <br>The {@link Supplier#apply(long)} method computes the Fibonacci number with the index given by its argument as follows:<ul>
 * <li>if the requested index is the successor of the previously computed index, it uses the recurrence formula: F(n) = F(n-1) + F(n-2)</li>
 * <li>otherwise, it uses the formula:
 * <br>F(2*k) = F(k) * (2 * F(k+1) − F(k))
 * <br>F(2*k+1) = F(k+1)^2 + F(k)^2
 * </li>
 * </ul>
 * It is also possible to compute the Fibonacci number with a given index by using the closed-form solution given by Binet's formula (see {@link Supplier#forIndexBinet(int)}).
 * However, this involves time consuming BigDecimal computations, leading to situations where using a parallel stream takes longer than using a sequential one.
 * (See {@link Jacobsthal} for an example of a series with computationally cheap closed-form solutions.)
 */
public class Fibonacci extends LongIndexedSpliterator<BigInteger, Fibonacci> {
    public Fibonacci(int fromValue, int count) {
        super(fromValue, fromValue + count);
        if(fromValue < 0 || count < 0 || fromValue > Integer.MAX_VALUE - count) {
            throw new IllegalArgumentException("fromValue: " + fromValue + ", count: " + count);
        }
        setValueSupplier(new Supplier());
        withAdditionalCharacteristics(Spliterator.DISTINCT);
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
                BigInteger newVal = lastVal.add(beforeLastVal);
                beforeLastVal = lastVal;
                lastVal = newVal;
            } else {
                beforeLastVal = forIndex((int)index - 1);
                lastVal = forIndex((int)index);
            }
            return lastVal;
        }

        /**
         * Computes the Fibonacci number with the given index using the formula:
         * <br>F(2*k) = F(k) * (2 * F(k+1) − F(k))
         * <br>F(2*k+1) = F(k+1)^2 + F(k)^2
         */
        private static BigInteger forIndex(int index) {
            if(index == 0) return BigInteger.ZERO;
            if(index == 1 || index == 2) return BigInteger.ONE;
            if(index % 2 == 0) {
                int k = index / 2;
                BigInteger fk = forIndex(k);
                BigInteger fk1 = forIndex(k + 1);
                return fk.multiply(fk1.shiftLeft(1).subtract(fk));
            } else {
                int k = (index - 1) / 2;
                BigInteger fk = forIndex(k);
                BigInteger fk1 = forIndex(k + 1);
                return fk.multiply(fk).add(fk1.multiply(fk1));
            }
        }

        private static final BigDecimal SQRT_5 = new BigDecimal(Math.sqrt(5));
        private static final BigDecimal PHI = new BigDecimal((1 + Math.sqrt(5)) / 2);
        private static final BigDecimal PSI = new BigDecimal((1 - Math.sqrt(5)) / 2);
        /**
         * Another possible implementation of the {@link #forIndex(int)} method, using Binet's formula.
         * The code is shorter, but involves time consuming BigDecimal computations.
         * Using this method instead of the original forIndex will lead to situations where running in parallel takes longer than running sequentially.
         */
        private static BigInteger forIndexBinet(int index) {
            return PHI.pow(index).subtract(PSI.pow(index)).divide(SQRT_5).toBigInteger();
        }

        @Override
        public LongIndexed<BigInteger> split() {
            return new Supplier();
        }
    }

    /**
     * Prints some Fibonacci numbers.
     */
    public static void main(String[] args) {
        System.out.println("20 Fibonacci numbers (starting with index 10):\n" + new Fibonacci(10, 20).stream().collect(Collectors.toList()));
    }
}
