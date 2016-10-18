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

import org.beryx.streamplify.IntArraySupplier;
import org.beryx.streamplify.Splittable;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * A value supplier for derangements.
 * <br>Computes the next derangement based on an index.
 */
public abstract class DerangementSupplier implements IntArraySupplier {
    protected final int length;
    protected final int[] currentSequence;

    DerangementSupplier(int length) {
        this.length = length;
        this.currentSequence = new int[length];
    }

    public void init() {
        int[] tmp = new DerangementSupplier.Long(length).apply(0);
        System.arraycopy(tmp, 0, currentSequence, 0, length);
    }

    @Override
    public int[] getCurrentSequence() {
        return currentSequence;
    }

    public void computeNext() {
        throw new UnsupportedOperationException("computeNext is not supported");
    }

    public static class Long extends DerangementSupplier implements Splittable.LongIndexed<int[]> {
        private final long[] subfactorial;

        private long currentIndex = -2;

        public Long(int length) {
            this(length, computeSubfactorial(length));
        }

        private Long(int length, long[] subfactorial) {
            super(length);
            this.subfactorial = subfactorial;
        }

        @Override
        public Long split() {
            return new Long(length, subfactorial);
        }

        @Override
        public int[] apply(long index) {
            currentIndex = index;
            return getNextSequence(false);
        }

        @Override
        public int[] unrank() {
            int[] seq = new int[length];
            Arrays.fill(seq, -1);
            int[] avoid = new int[length];
            for (int i = 0; i < length; ++i) {
                avoid[i] = i;
            }
            int[] reverse = new int[length];
            System.arraycopy(avoid, 0, reverse, 0, length);
            boolean[] taken = new boolean[length];
            long index = currentIndex;
            int remaining = length;
            for (int i = 0; i < length; ++i) {
                if (seq[i] == -1) {
                    int peer = 0;
                    if (remaining > 1) {
                        long comb = subfactorial[remaining - 1] + subfactorial[remaining - 2];
                        peer = (int) (index/comb);
                        index -= peer*comb;
                    }
                    int j = 0;
                    while (taken[j] || j == avoid[i] || peer > 0) {
                        if (!taken[j] && j != avoid[i]) --peer;
                        ++j;
                    }
                    seq[i] = j;
                    taken[j] = true;
                    if (index < subfactorial[remaining - 1]) {
                        avoid[reverse[j]] = avoid[i];
                        reverse[avoid[i]] = reverse[j];
                        --remaining;
                    } else {
                        seq[reverse[j]] = avoid[i];
                        taken[avoid[i]] = true;
                        index -= subfactorial[remaining - 1];
                        remaining -= 2;
                    }
                }
            }
            return seq;
        }

        private static long[] computeSubfactorial(int len) {
            if (len < 0) {
                return null;
            }
            long[] subf = new long[len + 1];
            subf[0] = 1;
            if (len < 1) {
                return subf;
            }
            subf[1] = 0;
            for (int i = 2; i <= len; ++i) {
                subf[i] = (i - 1)*(subf[i - 1] + subf[i - 2]);
            }
            return subf;
        }
    }

    public static class BigInt extends DerangementSupplier implements Splittable.BigIntegerIndexed<int[]> {
        private final BigInteger[] subfactorial;
        private BigInteger currentIndex = BigInteger.valueOf(-2);

        public BigInt(int length) {
            this(length, computeSubfactorial(length));
        }

        private BigInt(int length, BigInteger[] subfactorial) {
            super(length);
            this.subfactorial = subfactorial;
        }

        @Override
        public BigInt split() {
            return new BigInt(length, subfactorial);
        }

        @Override
        public int[] apply(BigInteger index) {
            currentIndex = index;
            return getNextSequence(false);
        }

        @Override
        public int[] unrank() {
            int[] seq = new int[length];
            Arrays.fill(seq, -1);
            int[] avoid = new int[length];
            for (int i = 0; i < length; ++i) {
                avoid[i] = i;
            }
            int[] reverse = new int[length];
            System.arraycopy(avoid, 0, reverse, 0, length);
            boolean[] taken = new boolean[length];
            BigInteger index = currentIndex;
            int remaining = length;
            for (int i = 0; i < length; ++i) {
                if (seq[i] == -1) {
                    int peer = 0;
                    if (remaining > 1) {
                        BigInteger comb = subfactorial[remaining - 1].add(subfactorial[remaining - 2]);
                        peer = index.divide(comb).intValue();
                        index = index.subtract(BigInteger.valueOf(peer).multiply(comb));
                    }
                    int j = 0;
                    while (taken[j] || j == avoid[i] || peer > 0) {
                        if (!taken[j] && j != avoid[i]) --peer;
                        ++j;
                    }
                    seq[i] = j;
                    taken[j] = true;
                    if (index.compareTo(subfactorial[remaining - 1]) == -1) {
                        avoid[reverse[j]] = avoid[i];
                        reverse[avoid[i]] = reverse[j];
                        --remaining;
                    } else {
                        seq[reverse[j]] = avoid[i];
                        taken[avoid[i]] = true;
                        index = index.subtract(subfactorial[remaining - 1]);
                        remaining -= 2;
                    }
                }
            }
            return seq;
        }

        private static BigInteger[] computeSubfactorial(int len) {
            if (len < 0) {
                return null;
            }
            BigInteger[] subf = new BigInteger[len + 1];
            subf[0] = BigInteger.ONE;
            if (len < 1) {
                return subf;
            }
            subf[1] = BigInteger.ZERO;
            for (int i = 2; i <= len; ++i) {
                subf[i] = BigInteger.valueOf(i - 1).multiply(subf[i - 1].add(subf[i - 2]));
            }
            return subf;
        }
    }
}
