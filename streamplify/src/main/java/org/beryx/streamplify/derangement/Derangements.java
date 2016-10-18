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
package org.beryx.streamplify.derangement;

import org.beryx.streamplify.Streamable;
import org.beryx.streamplify.StreamableProxy;

/**
 * A {@link Streamable} providing streams of derangements.
 * <br>This class is a proxy that delegates to either {@link LongDerangements} or {@link BigIntegerDerangements}, depending on the derangement length.
 */
public class Derangements extends StreamableProxy<int[], Derangements> {

    private final Streamable<int[], ?> delegate;

    /**
     * @param length the derangement length
     */
    public Derangements(int length) {
        if (length <= LongDerangements.MAX_LENGTH) {
            delegate = new LongDerangements(length);
        } else {
            delegate = new BigIntegerDerangements(length);
        }
    }

    @Override
    public Streamable<int[], ?> getDelegate() {
        return delegate;
    }
}
