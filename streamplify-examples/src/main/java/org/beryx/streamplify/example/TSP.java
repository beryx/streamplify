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

import javafx.geometry.Point2D;
import org.beryx.streamplify.permutation.Permutations;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A brute force solver for the Travelling salesman problem.
 * <br>Illustrates the use of {@link Permutations}.
 */
public class TSP {
    private final Point2D[] locations;
    private final double[][] distances;

    private class Solution {
        final int[] route;
        final double routeLength;

        Solution(int[] route) {
            this.route = route;
            int len = route.length;
            double d = distances[len-1][0];
            for(int i = 0; i < len-1; i++) {
                d += distances[i][i+1];
            }
            this.routeLength = d;
        }

        @Override
        public String toString() {
            return "Route of length " + routeLength + ": " +
            Arrays.stream(route)
                    .mapToObj(i -> "(" + locations[i].getX() + "," + locations[i].getY() + ")")
                    .collect(Collectors.joining(" -> "));

        }
    }

    public TSP(Point2D... locations) {
        this.locations = locations;
        int len = locations.length;
        this.distances = new double[len][len];
        for(int i = 0; i < len - 1; i++) {
            for(int j = i + 1; j < len; j++) {
                distances[i][j] = distances[j][i] = locations[i].distance(locations[j]);
            }
        }
    }

    public static TSP ofRandomLocations(int length) {
        Random rnd = new Random();
        Point2D[] locations = new Point2D[length];
        for(int i = 0; i < length; i++) {
            locations[i] = new Point2D(rnd.nextInt(100), rnd.nextInt(100));
        }
        return new TSP(locations);
    }

    public Solution solve() {
        return new Permutations(locations.length)
                .parallelStream()
                .map(route -> new Solution(route))
                .min((sol1, sol2) -> Double.compare(sol1.routeLength, sol2.routeLength))
                .get();
    }

    public static void main(String[] args) {
        System.out.println(TSP.ofRandomLocations(10).solve());
    }
}
