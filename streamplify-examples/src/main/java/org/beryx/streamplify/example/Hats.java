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

import org.beryx.streamplify.derangement.Derangements;
import org.beryx.streamplify.derangement.LongDerangements;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates <a href="https://en.wikipedia.org/wiki/Derangement">derangements</a>.
 * <br>This implementation makes use of the {@link Derangements} class.
 */
public class Hats {
    /**
     * Solves the following problem:<pre>
     * Alice, Bob, Chloe, David, and Emma have one hat each with their name on it.
     * List all possible ways of assigning one hat to each person so that no one
     * gets the hat that has their own name.</pre>
     */
    public static void main(String[] args) {
        String[] names = {"Alice", "Bob", "Chloe", "David", "Emma"};
        System.out.println("Original sequence:");
        System.out.println(Arrays.toString(names));
        System.out.println("All possible derangements:");
        System.out.println(new Derangements(5)
                .stream()
                .map(arr -> Arrays.stream(arr).mapToObj(i -> names[i])
                .collect(Collectors.toList()).toString())
                .collect(Collectors.joining("\n")));
    }
}
