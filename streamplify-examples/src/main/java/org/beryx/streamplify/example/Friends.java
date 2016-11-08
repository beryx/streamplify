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

import org.beryx.streamplify.powerset.PowerSet;

/**
 * Example to demonstrate the use of {@link PowerSet} stream.
 */
public class Friends {

    /**
     * Print number of ways 4 friends (Alice, Bob, Chloe, David ) can go on for a weekend trip.
     */
    public static void main(String[] args) {
        String[] friends = new String[]{"Alice", "Bob", "Chloe", "David"};
        new PowerSet(4).stream().forEach(set -> {
            for (int setElementIndex : set) {
                System.out.print(friends[setElementIndex] + " ");
            }
            System.out.println();
        });
    }
}
