package com.tterrag.advent2019.days;

import java.util.List;

import com.tterrag.advent2019.util.Day;

public class Day01 extends Day {

    @Override
    protected Result doParts() {
        List<Integer> masses = parseList(Integer::parseInt);
        int fuel = 0;
        for (int mass : masses) {
            fuel += (mass / 3) - 2;
        }
        int realfuel = 0;
        for (int mass : masses) {
            int modulefuel = (mass / 3) - 2;
            realfuel += modulefuel;
            while (modulefuel > 0) {
                modulefuel = (modulefuel / 3) - 2;
                if (modulefuel > 0) {
                    realfuel += modulefuel;
                }
            }
        }
        return new Result(fuel, realfuel);
    }
}
