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

import org.beryx.streamplify.derangement.BigIntegerDerangements
import org.beryx.streamplify.derangement.LongDerangements
import org.beryx.streamplify.derangement.Derangements
import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors

@Unroll
class DerangementsSpec extends Specification {

    def "LongDerangements should throw IllegalArgumentException for length #length"() {
        when:
        def derangements = new LongDerangements(length)

        then:
        thrown(IllegalArgumentException)

        where:
        length                      | _
        -1                          | _
        -2                          | _
        22                          | _
        Integer.MAX_VALUE           | _
        -Integer.MAX_VALUE          | _
    }

    def "BigIntegerDerangements should throw IllegalArgumentException for length #length"() {
        when:
        def derangements = new BigIntegerDerangements(length)

        then:
        thrown(IllegalArgumentException)

        where:
        length                                | _
        -1                                    | _
        -2                                    | _
        BigIntegerDerangements.MAX_LENGTH + 1 | _
        Integer.MAX_VALUE                     | _
        -Integer.MAX_VALUE                    | _
    }

    def "should use a #delegateClass.simpleName for length #length"() {
        given:
        def derangements = new Derangements(length)

        expect:
        derangements.delegate.getClass().name == delegateClass.name

        where:
        length                            | delegateClass
        0                                 | LongDerangements
        1                                 | LongDerangements
        2                                 | LongDerangements
        LongDerangements.MAX_LENGTH       | LongDerangements
        (LongDerangements.MAX_LENGTH + 1) | BigIntegerDerangements
        BigIntegerDerangements.MAX_LENGTH | BigIntegerDerangements
    }

    def "LongDerangements should correctly produce a derangement stream for length #length"() {
        given:
        def stream = new LongDerangements(length).stream()

        when:
        def seq = stream.map { int[] arr -> (arr as List).toString() }.collect(Collectors.toList())

        then:
        seq == derangements

        where:
        length | derangements
        0      | ['[]']
        1      | []
        2      | ['[1, 0]']
        3      | ['[1, 2, 0]', '[2, 0, 1]']
        4      | ['[1, 2, 3, 0]', '[1, 3, 0, 2]', '[1, 0, 3, 2]', '[2, 0, 3, 1]', '[2, 3, 1, 0]', '[2, 3, 0, 1]', '[3, 0, 1, 2]', '[3, 2, 0, 1]', '[3, 2, 1, 0]']
        5      | ['[1, 2, 3, 4, 0]', '[1, 2, 4, 0, 3]', '[1, 2, 0, 4, 3]', '[1, 3, 0, 4, 2]', '[1, 3, 4, 2, 0]', '[1, 3, 4, 0, 2]', '[1, 4, 0, 2, 3]', '[1, 4, 3, 0, 2]', '[1, 4, 3, 2, 0]', '[1, 0, 3, 4, 2]', '[1, 0, 4, 2, 3]', '[2, 0, 3, 4, 1]', '[2, 0, 4, 1, 3]', '[2, 0, 1, 4, 3]', '[2, 3, 1, 4, 0]', '[2, 3, 4, 0, 1]', '[2, 3, 4, 1, 0]', '[2, 4, 1, 0, 3]', '[2, 4, 3, 1, 0]', '[2, 4, 3, 0, 1]', '[2, 3, 0, 4, 1]', '[2, 4, 0, 1, 3]', '[3, 0, 1, 4, 2]', '[3, 0, 4, 2, 1]', '[3, 0, 4, 1, 2]', '[3, 2, 0, 4, 1]', '[3, 2, 4, 1, 0]', '[3, 2, 1, 4, 0]', '[3, 4, 0, 1, 2]', '[3, 4, 1, 2, 0]', '[3, 4, 0, 2, 1]', '[3, 2, 4, 0, 1]', '[3, 4, 1, 0, 2]', '[4, 0, 1, 2, 3]', '[4, 0, 3, 1, 2]', '[4, 0, 3, 2, 1]', '[4, 2, 0, 1, 3]', '[4, 2, 3, 0, 1]', '[4, 2, 1, 0, 3]', '[4, 3, 0, 2, 1]', '[4, 3, 1, 0, 2]', '[4, 3, 0, 1, 2]', '[4, 2, 3, 1, 0]', '[4, 3, 1, 2, 0]']
    }

    def "BigIntegerDerangements should correctly produce a derangement stream for length #length"() {
        given:
        def stream = new BigIntegerDerangements(length).stream()

        when:
        def seq = stream.map { int[] arr -> (arr as List).toString() }.collect(Collectors.toList())

        then:
        seq == derangements

        where:
        length | derangements
        0      | ['[]']
        1      | []
        2      | ['[1, 0]']
        3      | ['[1, 2, 0]', '[2, 0, 1]']
        4      | ['[1, 2, 3, 0]', '[1, 3, 0, 2]', '[1, 0, 3, 2]', '[2, 0, 3, 1]', '[2, 3, 1, 0]', '[2, 3, 0, 1]', '[3, 0, 1, 2]', '[3, 2, 0, 1]', '[3, 2, 1, 0]']
        5      | ['[1, 2, 3, 4, 0]', '[1, 2, 4, 0, 3]', '[1, 2, 0, 4, 3]', '[1, 3, 0, 4, 2]', '[1, 3, 4, 2, 0]', '[1, 3, 4, 0, 2]', '[1, 4, 0, 2, 3]', '[1, 4, 3, 0, 2]', '[1, 4, 3, 2, 0]', '[1, 0, 3, 4, 2]', '[1, 0, 4, 2, 3]', '[2, 0, 3, 4, 1]', '[2, 0, 4, 1, 3]', '[2, 0, 1, 4, 3]', '[2, 3, 1, 4, 0]', '[2, 3, 4, 0, 1]', '[2, 3, 4, 1, 0]', '[2, 4, 1, 0, 3]', '[2, 4, 3, 1, 0]', '[2, 4, 3, 0, 1]', '[2, 3, 0, 4, 1]', '[2, 4, 0, 1, 3]', '[3, 0, 1, 4, 2]', '[3, 0, 4, 2, 1]', '[3, 0, 4, 1, 2]', '[3, 2, 0, 4, 1]', '[3, 2, 4, 1, 0]', '[3, 2, 1, 4, 0]', '[3, 4, 0, 1, 2]', '[3, 4, 1, 2, 0]', '[3, 4, 0, 2, 1]', '[3, 2, 4, 0, 1]', '[3, 4, 1, 0, 2]', '[4, 0, 1, 2, 3]', '[4, 0, 3, 1, 2]', '[4, 0, 3, 2, 1]', '[4, 2, 0, 1, 3]', '[4, 2, 3, 0, 1]', '[4, 2, 1, 0, 3]', '[4, 3, 0, 2, 1]', '[4, 3, 1, 0, 2]', '[4, 3, 0, 1, 2]', '[4, 2, 3, 1, 0]', '[4, 3, 1, 2, 0]']
    }

    def "should skip correctly #skip derangements with length #length"() {
        given:
        def stream = new Derangements(length).skip(skip).stream()

        when:
        def seq = stream.map { int[] arr -> (arr as List).toString() }.findFirst().orElse('')

        then:
        seq == derangement

        where:
        length | skip                                           | derangement
        0      | 0                                              | '[]'
        0      | 1                                              | ''
        1      | 0                                              | ''
        2      | 0                                              | '[1, 0]'
        2      | 1                                              | ''
        3      | 0                                              | '[1, 2, 0]'
        3      | 1                                              | '[2, 0, 1]'
        3      | 2                                              | ''
        4      | 0                                              | '[1, 2, 3, 0]'
        4      | 8                                              | '[3, 2, 1, 0]'
        4      | 9                                              | ''
        5      | 42                                             | '[4, 2, 3, 1, 0]'
        25     | (new BigInteger('1234567890123456789012345'))  | '[6, 5, 17, 0, 23, 2, 10, 3, 11, 16, 4, 7, 13, 14, 15, 20, 21, 8, 22, 9, 1, 19, 12, 24, 18]'
        25     | (new BigInteger('12345678901234567890123456')) | ''
    }
}
