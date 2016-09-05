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
class CartesianProductSpec extends Specification {

    def "LongCartesianProduct should throw IllegalArgumentException for dimensions #dimensions"() {
        when:
        def cartesianProduct = new LongCartesianProduct(dimensions as int[])

        then:
        thrown(IllegalArgumentException)

        where:
        dimensions                                                | _
        [-1]                                                      | _
        [-1, 1]                                                   | _
        [-1, 2, 3]                                                | _
        [-1, 0]                                                   | _
        [-1, 0, 1]                                                | _
        [0, -1]                                                   | _
        [0, 2, -1]                                                | _
        [2, -1]                                                   | _
        [2, -1, 0]                                                | _
        [2, -1, 1]                                                | _
        [2, 0, -1]                                                | _
        [-1, -2]                                                  | _
        [-1, -2, -3]                                              | _
        [-1, -2, 0]                                               | _
        [0, -1, -2]                                               | _
        [Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE] | _
        [1_000_000, 1_000_000, 1_000_000, 1_000_000]              | _
    }

    def "BigIntegerCartesianProduct should throw IllegalArgumentException for dimensions #dimensions"() {
        when:
        def cartesianProduct = new BigIntegerCartesianProduct(dimensions as int[])

        then:
        thrown(IllegalArgumentException)

        where:
        dimensions   | _
        [-1]         | _
        [-1, 1]      | _
        [-1, 2, 3]   | _
        [-1, 0]      | _
        [-1, 0, 1]   | _
        [0, -1]      | _
        [0, 2, -1]   | _
        [2, -1]      | _
        [2, -1, 0]   | _
        [2, -1, 1]   | _
        [2, 0, -1]   | _
        [-1, -2]     | _
        [-1, -2, -3] | _
        [-1, -2, 0]  | _
        [0, -1, -2]  | _
    }


    def "should use a #delegateClass.simpleName for dimensions #dimensions"() {
        given:
        def cartesianProduct = new CartesianProduct(dimensions as int[])

        expect:
        cartesianProduct.delegate.getClass().name == delegateClass.name

        where:
        dimensions                                                | delegateClass
        []                                                        | LongCartesianProduct
        [0]                                                       | LongCartesianProduct
        [Integer.MAX_VALUE]                                       | LongCartesianProduct
        [1_000_000, 1_000_000, 1_000_000]                         | LongCartesianProduct
        [Integer.MAX_VALUE, Integer.MAX_VALUE]                    | LongCartesianProduct
        [Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE] | BigIntegerCartesianProduct
        [1_000_000, 1_000_000, 1_000_000, 1_000_000]              | BigIntegerCartesianProduct
    }

    def "LongCartesianProduct should correctly produce a cartesian product stream for dimensions #dimensions"() {
        given:
        def stream = new LongCartesianProduct(dimensions as int[]).stream()

        when:
        def prod = stream.map { int[] arr -> (arr as List).toString() }.collect(Collectors.toList())

        then:
        prod == product

        where:
        dimensions | product
        []         | ['[]']
        [0]        | []
        [3, 0, 4]  | []
        [1, 1]     | ['[0, 0]']
        [3, 4]     | ['[0, 0]', '[0, 1]', '[0, 2]', '[0, 3]', '[1, 0]', '[1, 1]', '[1, 2]', '[1, 3]', '[2, 0]', '[2, 1]', '[2, 2]', '[2, 3]']
        [2, 3, 2]  | ['[0, 0, 0]', '[0, 0, 1]', '[0, 1, 0]', '[0, 1, 1]', '[0, 2, 0]', '[0, 2, 1]', '[1, 0, 0]', '[1, 0, 1]', '[1, 1, 0]', '[1, 1, 1]', '[1, 2, 0]', '[1, 2, 1]']
    }

    def "BigIntegerCartesianProduct should correctly produce a cartesian product stream for dimensions #dimensions"() {
        given:
        def stream = new BigIntegerCartesianProduct(dimensions as int[]).stream()

        when:
        def prod = stream.map { int[] arr -> (arr as List).toString() }.collect(Collectors.toList())

        then:
        prod == product

        where:
        dimensions | product
        []         | ['[]']
        [0]        | []
        [3, 0, 4]  | []
        [1, 1]     | ['[0, 0]']
        [3, 4]     | ['[0, 0]', '[0, 1]', '[0, 2]', '[0, 3]', '[1, 0]', '[1, 1]', '[1, 2]', '[1, 3]', '[2, 0]', '[2, 1]', '[2, 2]', '[2, 3]']
        [2, 3, 2]  | ['[0, 0, 0]', '[0, 0, 1]', '[0, 1, 0]', '[0, 1, 1]', '[0, 2, 0]', '[0, 2, 1]', '[1, 0, 0]', '[1, 0, 1]', '[1, 1, 0]', '[1, 1, 1]', '[1, 2, 0]', '[1, 2, 1]']
    }
}
