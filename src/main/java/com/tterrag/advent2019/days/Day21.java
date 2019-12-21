package com.tterrag.advent2019.days;

import java.util.function.LongSupplier;

import com.tterrag.advent2019.util.Day;
import com.tterrag.advent2019.util.intcode.IntcodeInterpreter;

public class Day21 extends Day {
    
    final long[] input = csvLongArray();
        
    @Override
    protected Result doParts() {        
        return super.doParts();
    }
    
    private long survey(boolean run, String... progInput) {
        if (progInput.length > 15) {
            throw new IllegalArgumentException("Program too long");
        }
        return new IntcodeInterpreter(new LongSupplier() {
            
            int lineIdx = 0, charIdx = 0;
            
            @Override
            public long getAsLong() {
                if (lineIdx == progInput.length) {
                    return (run ? "RUN\n" : "WALK\n").charAt(charIdx++);
                }
                if (charIdx >= progInput[lineIdx].length()) {
                    lineIdx++;
                    charIdx = 0;
                    return '\n';
                } else {
                    return progInput[lineIdx].charAt(charIdx++);
                }
            }
        }, v -> { if (v <= 0x7F) System.out.print((char) v); })
                .execute(input);
    }
    
    @Override
    protected Object part1() {
        return survey(false, 
          "NOT C J",
          "NOT A T",
          "OR T J",
          "AND D J");
    }
    
    @Override
    protected Object part2() {
        return survey(true,
          "NOT A J",
          "OR B T",
          "OR E T",
          "NOT T T",
          "OR T J",
          "OR C T",
          "OR F T",
          "NOT T T",
          "OR T J",
          "AND D J");
    }
}
