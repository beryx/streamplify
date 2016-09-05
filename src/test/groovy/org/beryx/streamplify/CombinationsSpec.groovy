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
package org.beryx.streamplify

import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors

@Unroll
class CombinationsSpec extends Specification {

    def "LongCombinations should throw IllegalArgumentException for n=#n and k=#k"() {
        when:
        def combinations = new LongCombinations(n, k)

        then:
        thrown(IllegalArgumentException)

        where:
        n                      | k
        -1                     | 1
        1                      | -1
        -1                     | -1
        0                      | -1
        -1                     | 0
        0                      | 1
        0                      | 2
        1                      | 2
        3                      | 4
        61                     | 30
        Combinations.MAX_N + 1 | 1
        Combinations.MAX_N + 1 | Combinations.MAX_N

    }

    def "BigIntegerCombinations should throw IllegalArgumentException for n=#n and k=#k"() {
        when:
        def combinations = new BigIntegerCombinations(n, k)

        then:
        thrown(IllegalArgumentException)

        where:
        n                      | k
        -1                     | 1
        1                      | -1
        -1                     | -1
        0                      | -1
        -1                     | 0
        0                      | 1
        0                      | 2
        1                      | 2
        3                      | 4
        Combinations.MAX_N + 1 | 1
        Combinations.MAX_N + 1 | Combinations.MAX_N

    }


    def "should use a #delegateClass.simpleName for n=#n, k=#k"() {
        given:
        def combinations = new Combinations(n, k)

        expect:
        combinations.delegate.getClass().name == delegateClass.name

        where:
        n                  | k                                | delegateClass
        0                  | 0                                | LongCombinations
        1                  | 0                                | LongCombinations
        1                  | 1                                | LongCombinations
        2                  | 0                                | LongCombinations
        2                  | 1                                | LongCombinations
        2                  | 2                                | LongCombinations
        10                 | 5                                | LongCombinations
        20                 | 19                               | LongCombinations
        61                 | 20                               | LongCombinations
        61                 | 30                               | BigIntegerCombinations
        Combinations.MAX_N | 1                                | LongCombinations
        Combinations.MAX_N | Combinations.MAX_N               | LongCombinations
        Combinations.MAX_N | ((int) (Combinations.MAX_N / 2)) | BigIntegerCombinations
    }

    def "LongCombinations should correctly produce a combination stream for n=#n, k=#k"() {
        given:
        def stream = new LongCombinations(n, k).stream()

        when:
        def comb = stream.map { int[] arr -> (arr as List).toString() }.collect(Collectors.toList())

        then:
        comb == combinations

        where:
        n | k  | combinations
        0 | 0        | ['[]']
        1 | 0        | ['[]']
        1 | 1        | ['[0]']
        2 | 0        | ['[]']
        2 | 1        | ['[0]', '[1]']
        2 | 2        | ['[0, 1]']
        5 | 3        | ['[0, 1, 2]', '[0, 1, 3]', '[0, 1, 4]', '[0, 2, 3]', '[0, 2, 4]', '[0, 3, 4]', '[1, 2, 3]', '[1, 2, 4]', '[1, 3, 4]', '[2, 3, 4]']
    }


    def "BigIntegerCombinations should correctly produce a combination stream for n=#n, k=#k"() {
        given:
        def stream = new BigIntegerCombinations(n, k).stream()

        when:
        def comb = stream.map { int[] arr -> (arr as List).toString() }.collect(Collectors.toList())

        then:
        comb == combinations

        where:
        n | k  | combinations
        0 | 0        | ['[]']
        1 | 0        | ['[]']
        1 | 1        | ['[0]']
        2 | 0        | ['[]']
        2 | 1        | ['[0]', '[1]']
        2 | 2        | ['[0, 1]']
        5 | 3        | ['[0, 1, 2]', '[0, 1, 3]', '[0, 1, 4]', '[0, 2, 3]', '[0, 2, 4]', '[0, 3, 4]', '[1, 2, 3]', '[1, 2, 4]', '[1, 3, 4]', '[2, 3, 4]']
    }
}
