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
package org.beryx.streamplify;

import java.math.BigInteger;
import java.util.function.Function;
import java.util.function.LongFunction;

public interface Splittable<S extends Splittable<S>> {
    public static interface LongIndexed<T> extends LongFunction<T>, Splittable<LongIndexed<T>> {}
    public static interface BigIntegerIndexed<T> extends Function<BigInteger, T>, Splittable<BigIntegerIndexed<T>> {}

    S split();
}
