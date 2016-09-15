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

/**
 * This interface acts as a trait that helps implementing the valueSupplier requested by indexed-spliterators
 * (such as {@link LongIndexedSpliterator} or {@link BigIntegerIndexedSpliterator}) with the type parameter {@code int[]}.
 * <br>There is an implicit assumption that the implementing class uses an index (or some similar information) in order to decide
 * whether the value to be supplied can be computed based on the previous value or based on the index.
 */
public interface IntArraySupplier {
    /** @return the current sequence provided by this value supplier */
    int[] getCurrentSequence();

    /**
     * Computes the value to be supplied next, using the currently supplied value.
     * The next call of {@link #getCurrentSequence()} should return this newly computed value.
     */
    void computeNext();

    /**
     * @return the next value to be supplied.
     * It is assumed that the next value cannot be obtained using the current one,
     * but it has to be computed based on an index (or some similar information) handled by the implementing class.
     */
    int[] unrank();

    /**
     * Retrieves the next value to be supplied.
     * Postcondition: a call to {@link #getCurrentSequence()} will return an equal (but not identical) value.
     * @param useNext true, if the next value can be computed based on the current one;
     * @return the next value to be supplied. This should not be a reference to the array that backs {@link #getCurrentSequence()}.
     * @implSpec
     * This default implementation assumes that the value returned by {@link #getCurrentSequence()} is
     * a reference to the array containing the current sequence (and not a copy of it).
     * <br>Therefore, modifying this value is a way to configure the value to be returned by the next call of {@link #getCurrentSequence()}.
     * <br>if {@code useNext} is true, this implementation calls {@link #computeNext()}; otherwise, it calls {@link #unrank()}.
     */
    default int[] getNextSequence(boolean useNext) {
        int[] currSeq = getCurrentSequence();
        int len = currSeq.length;
        if(useNext) {
            computeNext();
            int[] seq = new int[len];
            System.arraycopy(currSeq, 0, seq, 0, len);
            return seq;
        } else {
            int[] prod = unrank();
            System.arraycopy(prod, 0, currSeq, 0, len);
            return prod;
        }
    }

}
