package com.tterrag.advent2019.days;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.tterrag.advent2019.util.Day;

public class Day16 extends Day {
    
    private final int[] input = blob().chars().map(i -> i - '0').toArray();
    private final int[] pattern = {0, 1, 0, -1};
    
    @Override
    protected Result doParts() {
        return super.doParts();
    }
    
    private int[] fft(int[] input, int phases) {
        System.out.println(Arrays.toString(input));
        int[] output = Arrays.copyOf(input, input.length);
        for (int phase = 0; phase < phases; phase++) {
            for (int i = 0; i < output.length; i++) {
                int resolution = i + 1;
                
                int sum = 0;
                for (int j = 0; j < output.length; j++) {
                    int patternPos = ((j + 1) / resolution);
                    patternPos %= pattern.length;
                    int p = pattern[patternPos];
                    int n = output[j] * p;
                    sum += n;
                }
                output[i] = Math.abs(sum % 10);
            }
        }
        
        return output;
    }
    
    @Override
    protected Object part1() {
        return Arrays.stream(fft(input, 100)).limit(8).mapToObj(Integer::toString).collect(Collectors.joining());
    }
    
    @Override
    protected Object part2() {
        int[] input = new int[this.input.length * 10000];
        for (int i = 0; i < 10000; i++) {
            System.arraycopy(this.input, 0, input, this.input.length * i, this.input.length);
        }
        int skip = Integer.parseInt(Arrays.stream(this.input).limit(7).mapToObj(Integer::toString).collect(Collectors.joining()));
        if (skip < input.length / 2) throw new IllegalStateException();
        for (int i = 0; i < 100; i++) {
            for (int j = input.length - 1; j >= input.length / 2; j--) {
                input[j] = j == input.length - 1 ? input[j] : input[j + 1] + input[j];
                input[j] %= 10;
            }
        }
        StringBuilder res = new StringBuilder();
        for (int i = skip; i < skip + 8; i++) {
            res.append(input[i]);
        }
        return res;
    }
}
