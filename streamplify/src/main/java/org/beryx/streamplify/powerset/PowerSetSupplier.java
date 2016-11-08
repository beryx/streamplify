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
package org.beryx.streamplify.powerset;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.beryx.streamplify.IntArraySupplier;
import org.beryx.streamplify.Splittable;

/**
 * A value supplier for power sets.
 * <br>It may compute the next set based on the current one, or by unranking an index.
 */
public abstract class PowerSetSupplier implements IntArraySupplier {

    protected final int length;
    protected int[] currentPowerSet;
    protected final int[] binaryCounter;

    public PowerSetSupplier(int length) {
        this.length = length;
        // initial power set would be empty array
        this.currentPowerSet = new int[0];
        this.binaryCounter = new int[length];
    }

    @Override
    public int[] getCurrentSequence() {
        return currentPowerSet;
    }

    @Override
    public void computeNext() {
        int index = length - 1;
        for (; index >= 0 && binaryCounter[index] == 1; index--);
        if (index == -1) {
            // current power set is the last set
            return;
        }
        binaryCounter[index] = 1;
        while (++index < length) {
            binaryCounter[index] = 0;
        }
        List<Integer> powerSetList = new ArrayList<>();
        for (int i = length - 1; i >= 0; i--) {
            if (binaryCounter[i] == 1) {
                powerSetList.add(length - i - 1);
            }
        }
        currentPowerSet = powerSetList.stream().mapToInt(element -> element.intValue()).toArray();
    }

    @Override
    public int[] getNextSequence(boolean useNext) {
        if (useNext) {
            computeNext();
            return getCurrentSequence();
        } else {
            int[] nextSeq = unrank();
            currentPowerSet = nextSeq;
            updateBinaryCounter();
            return nextSeq;
        }
    }

    private void updateBinaryCounter() {
        Arrays.fill(binaryCounter, 0);
        for (int element : currentPowerSet) {
            binaryCounter[element] = 1;
        }
    }

    public static class Long extends PowerSetSupplier implements Splittable.LongIndexed<int[]> {

        private long currentIndex = -2;

        public Long(int length) {
            super(length);
        }

        @Override
        public int[] apply(long index) {
            boolean useNext = (index == currentIndex + 1);
            currentIndex = index;
            return getNextSequence(useNext);
        }

        @Override
        public LongIndexed<int[]> split() {
            return new Long(length);
        }

        @Override
        public int[] unrank() {
            List<Integer> powerSetList = new ArrayList<>();
            long dividend = currentIndex;
            int setElement = 0;
            while(dividend != 0) {
                long reminder = dividend & 1;
                if (reminder == 1) {
                    powerSetList.add(setElement);
                }
                dividend = dividend >> 1;
                setElement++;
            }
            return powerSetList.stream().mapToInt(element -> element.intValue()).toArray();
        }

    }

    public static class BigInt extends PowerSetSupplier implements Splittable.BigIntegerIndexed<int[]> {

        private BigInteger currentIndex = BigInteger.valueOf(-2);

        public BigInt(int length) {
            super(length);
        }

        @Override
        public int[] apply(BigInteger index) {
            boolean useNext = index.equals(currentIndex.add(BigInteger.ONE));
            currentIndex = index;
            return getNextSequence(useNext);
        }

        @Override
        public Splittable.BigIntegerIndexed<int[]> split() {
            return new BigInt(length);
        }

        @Override
        public int[] unrank() {
            List<Integer> powerSetList = new ArrayList<>();
            BigInteger dividend = currentIndex;
            int setElement = 0;
            while(!dividend.equals(BigInteger.ZERO)) {
                BigInteger reminder = dividend.remainder(BigInteger.valueOf(2));
                if (reminder.equals(BigInteger.ONE)) {
                    powerSetList.add(setElement);
                }
                dividend = dividend.divide(BigInteger.valueOf(2));
                setElement++;
            }
            return powerSetList.stream().mapToInt(element -> element.intValue()).toArray();
        }
    }
}
