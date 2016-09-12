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

import org.beryx.streamplify.example.Fibonacci;

import java.math.BigInteger;
import java.util.stream.Stream;

/**
 * Performs parallel and sequential benchmarks on the {@link Fibonacci} class.
 * <br>On multicore and multiprocessor systems the parallel version is typically faster than the sequential one.
 * <br>(However, if you replace {@link Fibonacci.Supplier#forIndex(int)}
 * with {@link Fibonacci.Supplier#forIndexBinet(int)}}, the parallel version is typically slower.)
 */
public class FibonacciBenchmark {
    private static void runBenchmark(int startIndex, int count) {
        System.out.println("\nThe total number of bits needed to store " + count + " Fibonacci numbers, starting with the " + startIndex + "th:");
        runBenchmark(startIndex, count, true);
        runBenchmark(startIndex, count, false);
    }

    /**
     * Measures the time taken to generate a stream of {@code count} Fibonacci numbers starting with {@code startIndex} and to perform a reduce operation on it.
     * The operation performed consists in computing the sum of the number of bits in the representation of each BigInteger element in the stream.
     */
    private static void runBenchmark(int startIndex, int count, boolean parallel) {
        long start = System.currentTimeMillis();
        Fibonacci fib = new Fibonacci(startIndex, count);
        Stream<BigInteger> stream = parallel ? fib.parallelStream() : fib.stream();
        long bitSum = stream.mapToLong(BigInteger::bitLength).reduce(0, (a, b) -> a + b);
        long duration = System.currentTimeMillis() - start;
        System.out.println(bitSum + " bits. Computed " + (parallel ? "in parallel" : "sequentially") + " in " + duration + " ms.");
    }

    public static void main(String[] args) {
        runBenchmark(1000, 1_000_000);
    }
}
