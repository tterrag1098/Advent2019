package com.tterrag.advent2019.taketwo;

import com.tterrag.advent2019.util.Day;
import com.tterrag.advent2019.util.intcode.IntcodeInterpreter;

public class Day05 extends Day {
    
    public static void main(String[] args) {
        new Day05().run();
    }
    
    private final int[] input = csvIntArray();
    
    @Override
    protected Object part1() {
        return new IntcodeInterpreter(() -> 1).execute(input);
    }
    
    @Override
    protected Object part2() {
        return new IntcodeInterpreter(() -> 5).execute(input);
    }
}
