package com.tterrag.advent2019.days;

import com.tterrag.advent2019.util.Day;

public class Day04 extends Day {
    
    private static final int MIN = 134564, MAX = 585159;

    @Override
    protected Result doParts() {
        int validP1 = 0;
        int validP2 = 0;
        main:
        for (int i = MIN; i <= MAX; i++) {
            int[] digits = Integer.toString(i).chars().map(c -> c - '0').toArray();
            boolean foundDoubleDigitP1 = false;
            boolean foundDoubleDigitP2 = false;
            for (int j = 0; j < digits.length; j++) {
                int digit = digits[j];
                int prevDigit = j == 0 ? -1 : digits[j - 1];
                if (digit == prevDigit) {
                    foundDoubleDigitP1 = true;
                }
                int surrounding = 0;
                for (int k = 1;;k++) {
                    int dn = j - k;
                    int up = j + k;
                    int prev = surrounding;
                    if (dn >= 0 && digits[dn] == digit) {
                        surrounding++;
                    }
                    if (up < digits.length && digits[up] == digit) {
                        surrounding++;
                    }
                    if (prev == surrounding) break;
                }
                if (surrounding == 1) {
                    foundDoubleDigitP2 = true;
                }
                if (digit < prevDigit) {
                    continue main;
                }
                prevDigit = digit;
            }
            if (foundDoubleDigitP1) {
                validP1++;
            }
            if (foundDoubleDigitP2) {
                validP2++;
            }
        }

        return new Result(validP1, validP2);
    }
}
