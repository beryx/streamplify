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

import org.beryx.streamplify.combination.Combinations;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Illustrates the use of {@link Combinations} by solving the following problem:
 * <br>Each morning, you must eat 3 different fruits.
 * You can choose from: apple, banana, mango, orange, peach.
 * Print all your options.
 */
public class Diet {
    private static final String[] FRUITS = {"apple", "banana", "mango", "orange", "peach"};

    private final int fruitCount;

    public Diet(int fruitCount) {
        this.fruitCount = fruitCount;
    }

    public Stream<int[]> stream() {
        return new Combinations(FRUITS.length, fruitCount).stream();
    }

    public static String toString(int[] combination) {
        return Arrays.stream(combination).mapToObj(i -> FRUITS[i]).collect(Collectors.joining(", "));
    }

    public static void main(String[] args) {
        System.out.println(new Diet(3).stream().map(Diet::toString).collect(Collectors.joining("\n")));
    }
}
