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
package org.beryx.streamplify.shuffler;

import java.math.BigInteger;
import java.util.Random;

public class ShufflerImpl {
    private static final int BYTE_PERMUTATIONS_COUNT = 4;

    private final Random rnd;
    private final int[][] bytePermutations = new int[BYTE_PERMUTATIONS_COUNT][];
    private final int[][] bitPermutations = new int[8][];

    public ShufflerImpl(Random rnd) {
        this.rnd = rnd;
        for(int i = 0; i < BYTE_PERMUTATIONS_COUNT; i++) {
            bytePermutations[i] = getRandomPermutation(256);
        }
        for(int i = 1; i < 8; i++) {
            bitPermutations[i] = getRandomPermutation(1 << i);
        }
    }

    public ShufflerImpl withSeed(long seed) {
        rnd.setSeed(seed);
        return this;
    }

    public long getShuffledIndex(long index, long count) {
        BigInteger bigIndex = BigInteger.valueOf(index);
        BigInteger bigCount = BigInteger.valueOf(count);
        BigInteger bigShuffled = getShuffledIndex(bigIndex, bigCount);
        return bigShuffled.longValueExact();
    }

    public BigInteger getShuffledIndex(BigInteger index, BigInteger count) {
        int bitCount = count.subtract(BigInteger.ONE).bitLength();
        int wholeBytes = bitCount / 8;
        int restBits = bitCount % 8;
        int bytesLen = (bitCount + 7) / 8;

        byte[] bytes = index.toByteArray();
        if(bytes.length < bytesLen) {
            byte[] tmp = bytes;
            bytes = new byte[bytesLen];
            System.arraycopy(tmp, 0, bytes, bytesLen - tmp.length, tmp.length);
        }
        int bytesOff = bytes.length - bytesLen;
        byte[] shuffled = new byte[bytes.length];

        byte xorByte = 0;
        for(int i = 0; i < wholeBytes; i++) {
            int currIdx = (int)((bytes[bytesLen + bytesOff - 1 - i] ^ xorByte ) & 0xFF);
            xorByte ^= currIdx;
            shuffled[i + bytesOff] = (byte)bytePermutations[i % BYTE_PERMUTATIONS_COUNT][currIdx];
        }
        if(restBits > 0) {
            int currIdx = (bytes[bytesOff] ^ xorByte) & ((1 << restBits) - 1);
            int shuffledByte = bitPermutations[restBits][currIdx];
            shuffled[bytesLen + bytesOff - 1] = (byte) (shuffledByte << (8 - restBits));
        }
        BigInteger bigShuffled = new BigInteger(1, shuffled);
        if(restBits > 0) {
            bigShuffled = bigShuffled.shiftRight(8 - restBits);
        }
        if(bigShuffled.compareTo(count) < 0) return bigShuffled;
        else return getShuffledIndex(bigShuffled, count);
    }

    public int[] getRandomPermutation(int length) {
        int[] perm = new int[length];
        for(int i = 0; i < length; i++) {
            int j = rnd.nextInt(i + 1);
            if(j != i) perm[i] = perm[j];
            perm[j] = i;
        }
        return perm;
    }
}
