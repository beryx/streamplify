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

@Unroll
class LongIndexedSpliteratorSpec extends Specification {
    def "should throw IllegalArgumentException for origin=#origin and fence=#fence"() {
        when:
        new LongIndexedSpliterator(origin, fence)

        then:
        thrown(IllegalArgumentException)

        where:
        origin | fence
        -1     | -1
        -1     | 0
        -1     | 1
        0      | -1
        1      | -1
        7      | 6
    }

    def "count() should return #count for origin=#origin and fence=#fence"() {
        given:
        def streamable = new LongIndexedSpliterator(origin, fence)

        expect:
        streamable.count() == count

        where:
        origin | fence          | count
        0      | 0              | 0
        0      | 1              | 1
        1      | 1              | 0
        0      | 2              | 2
        111    | 111            | 0
        111    | 222            | 111
        0      | Long.MAX_VALUE | Long.MAX_VALUE
        111    | Long.MAX_VALUE | (Long.MAX_VALUE - 111)
    }

    def "bigCount() should return #count for origin=#origin and fence=#fence"() {
        given:
        def streamable = new LongIndexedSpliterator(origin, fence)

        expect:
        streamable.bigCount() == new BigInteger("$count")

        where:
        origin | fence          | count
        0      | 0              | 0
        0      | 1              | 1
        1      | 1              | 0
        0      | 2              | 2
        111    | 111            | 0
        111    | 222            | 111
        0      | Long.MAX_VALUE | Long.MAX_VALUE
        111    | Long.MAX_VALUE | (Long.MAX_VALUE - 111)
    }

    def "skip(#n) should set index to #newIndex for origin=#origin and fence=#fence"() {
        given:
        def streamable = new LongIndexedSpliterator(origin, fence)

        when:
        streamable.skip(n)

        then:
        streamable.getIndex() == newIndex

        where:
        origin | fence                  | n                      | newIndex
        0      | 0                      | 0                      | 0
        0      | 1                      | 1                      | 1
        0      | 1                      | 2                      | 1
        0      | 0                      | Long.MAX_VALUE         | 0
        0      | 1                      | Long.MAX_VALUE         | 1
        0      | Long.MAX_VALUE         | 0                      | 0
        0      | Long.MAX_VALUE         | Long.MAX_VALUE         | Long.MAX_VALUE
        111    | 333                    | 111                    | 222
        111    | 222                    | 111                    | 222
        111    | 222                    | 333                    | 222
        111    | 222                    | Long.MAX_VALUE         | 222
        111    | 222                    | (Long.MAX_VALUE - 111) | 222
        111    | Long.MAX_VALUE         | 222                    | 333
        111    | (Long.MAX_VALUE - 111) | 222                    | 333
        111    | Long.MAX_VALUE         | Long.MAX_VALUE         | Long.MAX_VALUE
        111    | Long.MAX_VALUE         | (Long.MAX_VALUE - 111) | Long.MAX_VALUE
        111    | Long.MAX_VALUE         | (Long.MAX_VALUE - 222) | (Long.MAX_VALUE - 111)
        111    | (Long.MAX_VALUE - 111) | Long.MAX_VALUE         | (Long.MAX_VALUE - 111)
        111    | (Long.MAX_VALUE - 111) | (Long.MAX_VALUE - 111) | (Long.MAX_VALUE - 111)
        111    | (Long.MAX_VALUE - 111) | (Long.MAX_VALUE - 222) | (Long.MAX_VALUE - 111)
        111    | (Long.MAX_VALUE - 333) | (Long.MAX_VALUE - 222) | (Long.MAX_VALUE - 333)
        111    | (Long.MAX_VALUE - 111) | (Long.MAX_VALUE - 333) | (Long.MAX_VALUE - 222)
    }


    def "big skip(#n) should set index to #newIndex for origin=#origin and fence=#fence"() {
        given:
        def streamable = new LongIndexedSpliterator(origin, fence)

        when:
        streamable.skip(new BigInteger("$n"))

        then:
        streamable.getIndex() == newIndex

        where:
        origin | fence                  | n                      | newIndex
        0      | 0                      | 0                      | 0
        0      | 1                      | 1                      | 1
        0      | 1                      | 2                      | 1
        0      | 0                      | Long.MAX_VALUE         | 0
        0      | 1                      | Long.MAX_VALUE         | 1
        0      | Long.MAX_VALUE         | 0                      | 0
        0      | Long.MAX_VALUE         | Long.MAX_VALUE         | Long.MAX_VALUE
        111    | 333                    | 111                    | 222
        111    | 222                    | 111                    | 222
        111    | 222                    | 333                    | 222
        111    | 222                    | Long.MAX_VALUE         | 222
        111    | 222                    | (Long.MAX_VALUE - 111) | 222
        111    | Long.MAX_VALUE         | 222                    | 333
        111    | (Long.MAX_VALUE - 111) | 222                    | 333
        111    | Long.MAX_VALUE         | Long.MAX_VALUE         | Long.MAX_VALUE
        111    | Long.MAX_VALUE         | (Long.MAX_VALUE - 111) | Long.MAX_VALUE
        111    | Long.MAX_VALUE         | (Long.MAX_VALUE - 222) | (Long.MAX_VALUE - 111)
        111    | (Long.MAX_VALUE - 111) | Long.MAX_VALUE         | (Long.MAX_VALUE - 111)
        111    | (Long.MAX_VALUE - 111) | (Long.MAX_VALUE - 111) | (Long.MAX_VALUE - 111)
        111    | (Long.MAX_VALUE - 111) | (Long.MAX_VALUE - 222) | (Long.MAX_VALUE - 111)
        111    | (Long.MAX_VALUE - 333) | (Long.MAX_VALUE - 222) | (Long.MAX_VALUE - 333)
        111    | (Long.MAX_VALUE - 111) | (Long.MAX_VALUE - 333) | (Long.MAX_VALUE - 222)
        0      | 0                      | "12345678901234567890" | 0
        0      | 1                      | "12345678901234567890" | 1
        0      | (Long.MAX_VALUE - 1)   | "12345678901234567890" | (Long.MAX_VALUE - 1)
        0      | Long.MAX_VALUE         | "12345678901234567890" | Long.MAX_VALUE
        111    | (Long.MAX_VALUE - 222) | "12345678901234567890" | (Long.MAX_VALUE - 222)

    }
}
