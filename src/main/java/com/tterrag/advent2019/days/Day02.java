package com.tterrag.advent2019.days;

import java.util.Arrays;

import com.tterrag.advent2019.util.Day;

public class Day02 extends Day {
    
    private final int[] ops = csvLongs().mapToInt(l -> (int) l).toArray();

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
    
    private int run(int noun, int verb) {
        int[] ops = Arrays.copyOf(this.ops, this.ops.length);
        ops[1] = noun;
        ops[2] = verb;
        for (int i = 0;; i += 4) {
            int op = ops[i];
            int a = ops[ops[i + 1]];
            int b = ops[ops[i + 2]];
            int r = ops[i + 3];
            if (op == 1) {
                ops[r] = a + b;
            } else if (op == 2) {
                ops[r] = a * b;
            } else if (op == 99) {
                break;
            } else {
                throw new IllegalStateException();
            }
        }
        return ops[0];
    }
}
