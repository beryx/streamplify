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

import org.beryx.streamplify.permutation.BigIntegerPermutations
import org.beryx.streamplify.permutation.LongPermutations
import org.beryx.streamplify.permutation.Permutations
import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors

@Unroll
class PermutationsSpec extends Specification {

    def "LongPermutations should throw IllegalArgumentException for length #length"() {
        when:
        def permutations = new LongPermutations(length)

        then:
        thrown(IllegalArgumentException)

        where:
        length                      | _
        -1                          | _
        -2                          | _
        21                          | _
        Integer.MAX_VALUE           | _
        -Integer.MAX_VALUE          | _
    }

    def "BigIntegerPermutations should throw IllegalArgumentException for length #length"() {
        when:
        def permutations = new BigIntegerPermutations(length)

        then:
        thrown(IllegalArgumentException)

        where:
        length                      | _
        -1                          | _
        -2                          | _
        Permutations.MAX_LENGTH + 1 | _
        Integer.MAX_VALUE           | _
        -Integer.MAX_VALUE          | _
    }

    def "should use a #delegateClass.simpleName for length #length"() {
        given:
        def permutations = new Permutations(length)

        expect:
        permutations.delegate.getClass().name == delegateClass.name

        where:
        length                  | delegateClass
        0                       | LongPermutations
        1                       | LongPermutations
        2                       | LongPermutations
        20                      | LongPermutations
        21                      | BigIntegerPermutations
        Permutations.MAX_LENGTH | BigIntegerPermutations
    }

    def "LongPermutations should correctly produce a permutation stream for length #length"() {
        given:
        def stream = new LongPermutations(length).stream()

        when:
        def perm = stream.map { int[] arr -> (arr as List).toString() }.collect(Collectors.toList())

        then:
        perm == permutations

        where:
        length | permutations
        0         | ['[]']
        1         | ['[0]']
        2         | ['[0, 1]', '[1, 0]']
        3         | ['[0, 1, 2]', '[0, 2, 1]', '[1, 0, 2]', '[1, 2, 0]', '[2, 0, 1]', '[2, 1, 0]']
    }

    def "BigIntegerPermutations should correctly produce a permutation stream for length #length"() {
        given:
        def stream = new BigIntegerPermutations(length).stream()

        when:
        def perm = stream.map { int[] arr -> (arr as List).toString() }.collect(Collectors.toList())

        then:
        perm == permutations

        where:
        length | permutations
        0         | ['[]']
        1         | ['[0]']
        2         | ['[0, 1]', '[1, 0]']
        3         | ['[0, 1, 2]', '[0, 2, 1]', '[1, 0, 2]', '[1, 2, 0]', '[2, 0, 1]', '[2, 1, 0]']
    }
}
