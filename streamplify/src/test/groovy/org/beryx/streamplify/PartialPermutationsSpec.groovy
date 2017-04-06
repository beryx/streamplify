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

import org.beryx.streamplify.partperm.BigIntegerPartialPermutations
import org.beryx.streamplify.partperm.LongPartialPermutations
import org.beryx.streamplify.partperm.PartialPermutations
import spock.lang.Specification

import java.util.stream.Collectors

class PartialPermutationsSpec extends Specification {

    def "LongPartialPermutations should throw IllegalArgumentException for length #length"() {
        when:
        def partialPermutations = new LongPartialPermutations(length)

        then:
        thrown(IllegalArgumentException)

        where:
        length                                 | _
        -1                                     | _
        -2                                     | _
        LongPartialPermutations.MAX_LENGTH + 1 | _
        Integer.MAX_VALUE                      | _
        -Integer.MAX_VALUE                     | _
    }

    def "BigIntegerPartialPermutations should throw IllegalArgumentException for length #length"() {
        when:
        def partialPermutations = new BigIntegerPartialPermutations(length)

        then:
        thrown(IllegalArgumentException)

        where:
        length                                       | _
        -1                                           | _
        -2                                           | _
        BigIntegerPartialPermutations.MAX_LENGTH + 1 | _
        Integer.MAX_VALUE                            | _
        -Integer.MAX_VALUE                           | _
    }

    def "should use a #delegateClass.simpleName for length #length"() {
        given:
        def partialPermutations = new PartialPermutations(length)

        expect:
        partialPermutations.delegate.getClass().name == delegateClass.name

        where:
        length                                   | delegateClass
        0                                        | LongPartialPermutations
        1                                        | LongPartialPermutations
        2                                        | LongPartialPermutations
        LongPartialPermutations.MAX_LENGTH       | LongPartialPermutations
        (LongPartialPermutations.MAX_LENGTH + 1) | BigIntegerPartialPermutations
    }

    def "LongPartialPermutations should correctly produce a partial permutation stream for length #length"() {
        given:
        def stream = new LongPartialPermutations(length).stream()

        when:
        def partialPerm = stream.map { int[] arr -> (arr as List).toString() }.collect(Collectors.toList())

        then:
        partialPerm == partialPermutations

        where:
        length | partialPermutations
        0      | ['[]']
        1      | ['[-1]', '[0]']
        2      | ['[-1, -1]', '[-1, 0]', '[0, -1]', '[-1, 1]', '[1, -1]', '[0, 1]', '[1, 0]']
    }

    def "BigIntegerPartialPermutations should correctly produce a partial permutation stream for length #length"() {
        given:
        def stream = new BigIntegerPartialPermutations(length).stream()

        when:
        def partialPerm = stream.map { int[] arr -> (arr as List).toString() }.collect(Collectors.toList())

        then:
        partialPerm == partialPermutations

        where:
        length | partialPermutations
        0      | ['[]']
        1      | ['[-1]', '[0]']
        2      | ['[-1, -1]', '[-1, 0]', '[0, -1]', '[-1, 1]', '[1, -1]', '[0, 1]', '[1, 0]']
    }

    def "PartialPermutations should skip correctly #skip partials permutations with length #length"() {
        given:
        def stream = new PartialPermutations(length).skip(skip).stream()

        when:
        def partialPerm = stream.map { int[] arr -> (arr as List).toString() }.findFirst().orElse('')

        then:
        partialPerm == permutation

        where:
        length | skip                                        | permutation
        2      | 0                                           | '[-1, -1]'
        2      | 1                                           | '[-1, 0]'
        6      | 7329                                        | '[1, -1, -1, 4, 2, 5]'
        25     | 10                                          | '[-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1]'
        21     | (new BigInteger('44552237162692939114280')) | '[20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 0, 1]'
    }

    def "LongPartialPermutations should calculate the correct number of partial permutations given length #length"() {
        expect:
        LongPartialPermutations.numberOfPermutations(length) == numberOfPartialPermutations

        where:
        length | numberOfPartialPermutations
        0      | 1
        1      | 2
        7      | 130922
        8      | 1441729
        18     | 2968971263911288999
    }

    def "BigIntegerPartialPermutations should calculate the correct number of partial permutations given length #length"() {
        expect:
        BigIntegerPartialPermutations.numberOfPermutations(length) == numberOfPartialPermutations

        where:
        length | numberOfPartialPermutations
        0      | 1
        1      | 2
        7      | 130922
        30     | new BigInteger('1240758969214239528262796909096631871')
    }

}
