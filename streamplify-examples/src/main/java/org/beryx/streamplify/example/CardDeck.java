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

import org.beryx.streamplify.permutation.Permutations;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates random arrangements of playing cards in a standard 52-card deck.
 * <br>Illustrates the use of {@link Permutations}.
 */
public class CardDeck {
    private static final int LENGTH = 52;
    private static final String[] RANKS = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
    private static final String[] SUITS = {"Clubs", "Diamonds", "Hearts", "Spades"};

    public Stream<int[]> stream() {
        return new Permutations(LENGTH).shuffle().stream();
    }

    public int[] getShuffledCards() {
        return stream().findFirst().get();
    }

    public static String toString(int val) {
        String rank = RANKS[val % 13];
        String suit = SUITS[val / 13];
        return rank + " of " + suit;
    }

    public static String toString(int[] arrangement) {
        return Arrays.stream(arrangement).mapToObj(val -> toString(val)).collect(Collectors.joining("\n"));
    }

    public static void main(String[] args) {
        System.out.println(toString(new CardDeck().getShuffledCards()));
    }
}
