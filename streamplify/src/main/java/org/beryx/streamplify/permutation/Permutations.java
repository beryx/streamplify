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
package org.beryx.streamplify.permutation;

import org.beryx.streamplify.Streamable;
import org.beryx.streamplify.StreamableProxy;

/**
 * A {@link Streamable} providing streams of permutations.
 * <br>This class is a proxy that delegates to either {@link LongPermutations} or {@link BigIntegerPermutations}, depending on the permutation length.
 */
public class Permutations extends StreamableProxy<int[], Permutations> {

    private final Streamable<int[], ?> delegate;

    /**
     * @param length the permutation length
     */
    public Permutations(int length) {
        if(length <= LongPermutations.MAX_LENGTH) {
            delegate = new LongPermutations(length);
        } else {
            delegate = new BigIntegerPermutations(length);
        }
    }

    @Override
    public Streamable<int[], ?> getDelegate() {
        return delegate;
    }
}
