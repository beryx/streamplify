package org.beryx.streamplify.shared;

import java.math.BigInteger;

public class Unranking {
    /**
     * Unranks the combination with the given long index.
     * <br>This implementation uses the UNRANKCOMB-D algorithm introduced in:
     * Kokosinski, Zbigniew, and Ikki-Machi Tsuruga. "Algorithms for unranking combinations and other related choice functions." (1995).
     *
     * @param n the total number of elements in the set
     * @param k the number of elements taken
     * @param count the number of {@code k}-combinations
     * @param index the index of the combination to be unranked
     * @return the combination corresponding to the given index
     */
    public static int[] unrankCombination(int n, int k, long count, long index) {
        if (k == 0) return new int[0];
        int[] combi = new int[k];
        long rank = count - 1 - index;
        long e = (n - k) * count / n;
        int t = n - k + 1;
        int m = k;
        int p = n - 1;
        do {
            if (e <= rank) {
                combi[k - m] = n - t - m + 1;
                if (e > 0) {
                    rank = rank - e;
                    e = m * e / p;
                }
                m--;
                p--;
            } else {
                e = (p - m) * e / p;
                t--;
                p--;
            }
        } while (m > 0);
        return combi;
    }

    /**
     /**
     * Unranks the combination with the given BigInteger index.
     * This implementation uses the UNRANKCOMB-D algorithm introduced in:
     * Kokosinski, Zbigniew, and Ikki-Machi Tsuruga. "Algorithms for unranking combinations and other related choice functions." (1995).
     *
     * @param n the total number of elements in the set
     * @param k the number of elements taken
     * @param count number of {@code k}-combinations
     * @param index the index of the combination to be unranked
     * @return the combination corresponding to the given index
     */
    public static int[] unrankCombination(int n, int k, BigInteger count, BigInteger index) {
        if (k == 0) return new int[0];
        int[] combi = new int[k];
        BigInteger rank = count.subtract(BigInteger.ONE).subtract(index);
        BigInteger e = count.multiply(BigInteger.valueOf(n - k)).divide(BigInteger.valueOf(n));
        int t = n - k + 1;
        int m = k;
        int p = n - 1;
        do {
            if (e.compareTo(rank) <= 0) {
                combi[k - m] = n - t - m + 1;
                if (e.compareTo(BigInteger.ZERO) > 0) {
                    rank = rank.subtract(e);
                    e = e.multiply(BigInteger.valueOf(m)).divide(BigInteger.valueOf(p));
                }
                m--;
                p--;
            } else {
                e = e.multiply(BigInteger.valueOf(p - m)).divide(BigInteger.valueOf(p));
                t--;
                p--;
            }
        } while (m > 0);
        return combi;
    }
}
