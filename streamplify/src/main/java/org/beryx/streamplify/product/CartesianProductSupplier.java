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
package org.beryx.streamplify.product;

import org.beryx.streamplify.IntArraySupplier;
import org.beryx.streamplify.Splittable;

import java.math.BigInteger;

/**
 * A value supplier for cartesian product.
 * <br>It may compute the next tuple based on the current one, or by unranking an index.
 */
public abstract class CartesianProductSupplier implements IntArraySupplier {
    protected final int[] dimensions;
    protected final int[] currentProduct;

    CartesianProductSupplier(int[] dimensions) {
        this.dimensions = dimensions;
        this.currentProduct = new int[dimensions.length];
    }

    @Override
    public int[] getCurrentSequence() {
        return currentProduct;
    }

    @Override
    public void computeNext() {
        int pos = dimensions.length - 1;
        while(pos >= 0 && currentProduct[pos] >= dimensions[pos] - 1) pos--;
        if(pos < 0) return;
        currentProduct[pos]++;
        for(int i = pos + 1; i < dimensions.length; i++) {
            currentProduct[i] = 0;
        }
    }

    public static class Long extends CartesianProductSupplier implements Splittable.LongIndexed<int[]> {
        private final long count;
        private long currentIndex = -2;

        public Long(long count, int[] dimensions) {
            super(dimensions);
            this.count = count;
        }

        @Override
        public Long split() {
            return new Long(count, dimensions);
        }

        @Override
        public int[] apply(long index) {
            boolean useNext = (index == currentIndex + 1);
            currentIndex = index;
            return getNextSequence(useNext);
        }

        @Override
        public int[] unrank() {
            int[] product = new int[dimensions.length];
            long dividend = currentIndex;
            for(int k = dimensions.length - 1; k >= 0; k--) {
                product[k] = (int)(dividend % dimensions[k]);
                dividend /= dimensions[k];
            }
            return product;
        }
    }

    public static class BigInt extends CartesianProductSupplier implements Splittable.BigIntegerIndexed<int[]> {
        private final BigInteger count;
        private BigInteger currentIndex = BigInteger.valueOf(-2);

        public BigInt(int[] dimensions, BigInteger count) {
            super(dimensions);
            this.count = count;
        }

        @Override
        public BigInt split() {
            return new BigInt(dimensions, count);
        }

        @Override
        public int[] apply(BigInteger index) {
            boolean useNext = index.equals(currentIndex.add(BigInteger.ONE));
            currentIndex = index;
            return getNextSequence(useNext);
        }

        @Override
        public int[] unrank() {
            int[] product = new int[dimensions.length];
            BigInteger dividend = currentIndex;
            for(int k = dimensions.length - 1; k >= 0; k--) {
                BigInteger[] quotientAndRemainder = dividend.divideAndRemainder(BigInteger.valueOf(dimensions[k]));
                product[k] = quotientAndRemainder[1].intValueExact();
                dividend = quotientAndRemainder[0];
            }
            return product;
        }
    }
}
