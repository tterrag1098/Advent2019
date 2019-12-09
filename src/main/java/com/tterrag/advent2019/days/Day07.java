package com.tterrag.advent2019.days;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.tterrag.advent2019.util.Day;
import com.tterrag.advent2019.util.intcode.IntcodeInterpreter;

import lombok.RequiredArgsConstructor;

public class Day07 extends Day {
    
    private final long[] input = csvLongArray();

    @Override
    protected Object part1() {
        return runPermutations(new int[] { 0, 1, 2, 3, 4 }, false);
    }
    
    @Override
    protected Object part2() {
        return runPermutations(new int[] { 5, 6, 7, 8, 9 }, true);
    }
    
    private long runPermutations(int[] phases, boolean loop) {
        int[] indices = new int[phases.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = 0;
        }
        
        long max = run(phases, loop);
         
        int i = 0;
        while (i < phases.length) {
            if (indices[i] < i) {
                swap(phases, i % 2 == 0 ?  0: indices[i], i);
                long res = run(phases, loop);
                max = Math.max(max, res);
                indices[i]++;
                i = 0;
            }
            else {
                indices[i] = 0;
                i++;
            }
        }
        return max;
    }
    
    private void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }
    
    @RequiredArgsConstructor
    private class VM extends Thread {
        private final int phase;
        private IntcodeInterpreter interpreter;
        
        private BlockingQueue<Long> inputQueue = new LinkedBlockingQueue<>();
        
        private boolean primed;
        
        public void setOutput(VM output) {
            this.interpreter = new IntcodeInterpreter(() -> {
                if (!primed) {
                    primed = true;
                    return phase;
                }
                try {
                    return inputQueue.poll(1, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, output.inputQueue::add);
        }
        
        @Override
        public void run() {
            interpreter.execute(Day07.this.input);
        }
    }
    
    private long run(int[] phases, boolean loop) {
        long lastOutput = 0;
        if (loop) {
            VM[] vms = IntStream.of(phases).mapToObj(VM::new).toArray(VM[]::new);
            for (int i = 0; i < vms.length; i++) {
                vms[i].setOutput(vms[(i + 1) % vms.length]);
            }
            for (VM vm : vms) {
                vm.start();
            }
            vms[0].inputQueue.add(0L);
            for (VM vm : vms) {
                try {
                    vm.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return vms[vms.length - 1].interpreter.getLastOutput();
        } else {
            for (int i = 0; i < phases.length; i++) {
                lastOutput = new IntcodeInterpreter(phases[i], lastOutput).execute(input);
            }
        }
        return lastOutput;
    }
}
