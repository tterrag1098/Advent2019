package com.tterrag.advent2019.taketwo;

import java.util.Arrays;

import com.tterrag.advent2019.util.Day;
import com.tterrag.advent2019.util.intcode.IntcodeInterpreter;

public class Day02 extends Day {
    
    private final long[] ops = csvLongArray();

    @Override
    protected Object part1() {
        return run(12, 2);
    }
    
    @Override
    protected Object part2() {
        int period = 100;
        for (int i = 0;; i++) {
            int noun = i % period;
            int verb = i / period;
            if (run(noun, verb) == 19690720) {
                return 100 * noun + verb;
            }
        }
    }
    
    private long run(int noun, int verb) {
        long[] ops = Arrays.copyOf(this.ops, this.ops.length);
        ops[1] = noun;
        ops[2] = verb;
        IntcodeInterpreter interpreter = new IntcodeInterpreter();
        interpreter.execute(ops);
        return interpreter.getProgram()[0];
    }
}
