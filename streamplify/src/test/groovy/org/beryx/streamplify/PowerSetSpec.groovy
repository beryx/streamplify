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

import org.beryx.streamplify.powerset.BigIntegerPowerSet;
import org.beryx.streamplify.powerset.LongPowerSet
import org.beryx.streamplify.powerset.PowerSet
import spock.lang.Specification
import spock.lang.Unroll;

import java.util.stream.Collectors;

@Unroll
class PowerSetSpec extends Specification {

    def "LongPowerSet should throw IllegalArgumentException for length #length"() {
        when:
        def powerSet = new LongPowerSet(length);

        then:
        thrown(IllegalArgumentException);

        where:
        length              | _
        -1                  | _
        -2                  | _
        63                  | _
        Integer.MAX_VALUE   | _
        -Integer.MAX_VALUE  | _
    }

    def "BigIntegerPowerSet should throw IllegalArgumentException for length #length"() {
        when:
        def powerSet = new BigIntegerPowerSet(length);

        then:
        thrown(IllegalArgumentException);

        where:
        length              | _
        -1                  | _
        -2                  | _
        512                 | _
        Integer.MAX_VALUE   | _
        -Integer.MAX_VALUE  | _
    }

    def "should use a #delegateClass.simpleName for length #length"() {
        given:
        def powerSet = new PowerSet(length)

        expect:
        powerSet.delegate.getClass().name == delegateClass.name

        where:
        length                            | delegateClass
        0                                 | LongPowerSet
        1                                 | LongPowerSet
        62                                | LongPowerSet
        63                                | BigIntegerPowerSet
    }

    def "LongPowerSet should correctly produce a powerset stream for length #length"() {
        given:
        def stream = new LongPowerSet(length).stream()

        when:
        def powerSet = stream.map { int[] arr -> (arr as List).toString() }.collect(Collectors.toList())

        then:
        powerSet == expectedPowerSet

        where:
        length | expectedPowerSet
        0         | ['[]']
        1         | ['[]', '[0]']
        2         | ['[]', '[0]', '[1]', '[0, 1]']
        3         | ['[]', '[0]', '[1]', '[0, 1]', '[2]', '[0, 2]', '[1, 2]', '[0, 1, 2]']
    }

    def "BigIntegerPowerSet should correctly produce a powerset stream for length #length"() {
        given:
        def stream = new BigIntegerPowerSet(length).stream()

        when:
        def powerSet = stream.map { int[] arr -> (arr as List).toString() }.collect(Collectors.toList())

        then:
        powerSet == expectedPowerSet

        where:
        length | expectedPowerSet
        0         | ['[]']
        1         | ['[]', '[0]']
        2         | ['[]', '[0]', '[1]', '[0, 1]']
        3         | ['[]', '[0]', '[1]', '[0, 1]', '[2]', '[0, 2]', '[1, 2]', '[0, 1, 2]']
    }
}
