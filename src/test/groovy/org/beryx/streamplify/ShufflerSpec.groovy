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

import org.beryx.streamplify.shuffler.ShufflerImpl
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
public class ShufflerSpec extends Specification {
    def shuffler = new ShufflerImpl(new Random())

    def "should shuffle all #count elements using long index"() {
        when:
        def shuffledIndexes = [] as Set
        for(long i = 0; i < count; i++) {
            long shuffled = shuffler.getShuffledIndex(i, count);
            shuffledIndexes << shuffled
        }

        then:
        shuffledIndexes.size() == count
        shuffledIndexes.min() == 0
        shuffledIndexes.max() == count - 1

        where:
        count << (1..258) + (1022..1026) + (32766..32770) + (65534..65538)
    }

    def "should shuffle all #count elements using BigInteger index"() {
        when:
        def shuffledIndexes = [] as Set
        for(long i = 0; i < count; i++) {
            BigInteger shuffled = shuffler.getShuffledIndex(i, BigInteger.valueOf(count));
            shuffledIndexes << shuffled
        }

        then:
        shuffledIndexes.size() == count
        shuffledIndexes.min() == BigInteger.ZERO
        shuffledIndexes.max() == BigInteger.valueOf(count) - 1

        where:
        count << (1..258) + (1022..1026) + (32766..32770) + (65534..65538)
    }

    def "should shuffle #sampleCount elements from a total of #count, starting with index #startIndex"() {
        when:
        Set<BigInteger> shuffledIndexes = []
        def bigCount = new BigInteger("$count")
        def bigStartIndex = new BigInteger("$startIndex")
        for(long i = 0; i < sampleCount; i++) {
            def bigIndex = bigStartIndex.add(BigInteger.valueOf(i))
            BigInteger shuffled = shuffler.getShuffledIndex(bigIndex, bigCount);
            shuffledIndexes << shuffled
        }

        then:
        shuffledIndexes.size() == sampleCount
        (shuffledIndexes.min() <=> BigInteger.ZERO) >= 0
        (shuffledIndexes.max() <=> bigCount) < 0

        where:
        startIndex                | count                     | sampleCount
        0                         | (Long.MAX_VALUE - 2)      | 10000
        (Long.MAX_VALUE >> 1)     | (Long.MAX_VALUE - 1)      | 10000
        (Long.MAX_VALUE >> 2)     | Long.MAX_VALUE            | 10000
        0                         | "10000000000000000000000" | 10000
        "10000000000000000000000" | "20000000000000000000000" | 10000
        "20000000000000000000000" | "20000000000000000011111" | 11111
    }
}
