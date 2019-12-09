package com.tterrag.advent2019.days;

import com.tterrag.advent2019.util.Day;
import com.tterrag.advent2019.util.intcode.IntcodeInterpreter;

public class Day09 extends Day {
    
    private final long[] input = csv().mapToLong(Long::parseLong).toArray();

    @Override
    protected Object part1() {
        return new IntcodeInterpreter(1).execute(input);
    }
    
    @Override
    protected Object part2() {
        return new IntcodeInterpreter(2).execute(input);
    }
}
