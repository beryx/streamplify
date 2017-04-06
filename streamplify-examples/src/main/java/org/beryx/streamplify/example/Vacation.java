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

import org.beryx.streamplify.partperm.PartialPermutationSupplier;
import org.beryx.streamplify.partperm.PartialPermutations;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Illustrates the use of {@link PartialPermutations} by solving the following problem:<pre>
 * You have an activity you want to do for each day of your vacation
 * but on any day you may instead choose to explore.
 * Print all possible itineraries for your vacation.</pre>
 */
public class Vacation {

    public static void main(String[] args) {
        String[] thingsToDoOnVacation = {"Hiking", "Museum"};
        String thingToDoInstead = "Explore";

        System.out.print("Things I want to do on vacation: ");
        printFormattedString(Arrays.toString(thingsToDoOnVacation), ", ");
        System.out.println("But instead I might " + thingToDoInstead + "\n");
        System.out.println("All possible itineraries:\n");

        System.out.println(IntStream.range(1, thingsToDoOnVacation.length + 1)
                .mapToObj(i -> "-Day " + i + "-")
                .collect(Collectors.joining("\t")));

        printFormattedString(new PartialPermutations(thingsToDoOnVacation.length)
                .stream()
                .map(arr -> Arrays.stream(arr).mapToObj(i -> i == PartialPermutationSupplier.HOLE ? thingToDoInstead : thingsToDoOnVacation[i])
                        .collect(Collectors.toList()).toString())
                .collect(Collectors.joining("\n")), "\t");
    }

    private static void printFormattedString(String output, String delimiter) {
        System.out.println(output.replaceAll(",? ", delimiter).replaceAll("]|\\[", ""));
    }
}
