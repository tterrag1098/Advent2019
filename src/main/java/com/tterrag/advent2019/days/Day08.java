package com.tterrag.advent2019.days;

import java.util.Arrays;

import com.tterrag.advent2019.util.Day;

public class Day08 extends Day {
    
    private final int w = 25, h = 6;
    
    private int[][][] layers;
    
    @Override
    protected Result doParts() {
        int[] input = blob().trim().chars().map(i -> i - '0').toArray();
        layers = new int[input.length / (w * h)][h][w];
        for (int l = 0; l < layers.length; l++) {
            int[][] layer = layers[l];
            for (int y = 0; y < layer.length; y++) {
                int[] row = layer[y];
                for (int x = 0; x < row.length; x++) {
                    row[x] = input[(l * (w * h)) + ((y * row.length) + x)];
                }
            }
        }
        return super.doParts();
    }

    @Override
    protected Object part1() {
        return Arrays.stream(layers).min((row1, row2) -> Integer.compare(count(row1, 0), count(row2, 0)))
                .map(row -> count(row, 1) * count(row, 2))
                .get();
    }
    
    @Override
    protected Object part2() {
        int[][] image = new int[h][w];
        for (int i = layers.length - 1; i >= 0; i--) {
            int[][] layer = layers[i];
            for (int y = 0; y < layer.length; y++) {
                for (int x = 0; x < layer[y].length; x++) {
                    int color = layer[y][x];
                    if (color != 2) {
                        image[y][x] = color;
                    }
                }
            }
        }
        
        StringBuilder ret = new StringBuilder("\n");
        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[y].length; x++) {
                ret.append(image[y][x] == 1 ? '#' : ' ');
            }
            ret.append('\n');
        }
        return ret;
    }
    
    int count(int[][] row, int digit) {
        return (int) Arrays.stream(row).flatMapToInt(Arrays::stream).filter(i -> i == digit).count();
    }
}
