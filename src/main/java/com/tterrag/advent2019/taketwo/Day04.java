package com.tterrag.advent2019.taketwo;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tterrag.advent2019.util.Day;

public class Day04 extends Day {
    
    public static void main(String[] args) {
        new Day04().run();
    }
    
    private static final int MIN = 134564, MAX = 585159;
    private static final Pattern MULTIDIGIT = Pattern.compile("(\\d)\\1+");

    @Override
    protected Result doParts() {
        int validP1 = 0;
        int validP2 = 0;
        for (int i = MIN; i <= MAX; i++) {
            String digits = Integer.toString(i);
            char[] chars = digits.toCharArray();
            Arrays.sort(chars);
            if (!digits.equals(new String(chars))) {
                continue;
            }
            Matcher m = MULTIDIGIT.matcher(digits);
            if (m.find()) {
                validP1++;
            }
            m.reset();
            while (m.find()) {
                if (m.group().length() == 2) {
                    validP2++;
                    break;
                }
            }
        }

        return new Result(validP1, validP2);
    }
}
