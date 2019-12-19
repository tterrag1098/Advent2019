package com.tterrag.advent2019.days;

import java.util.ArrayList;
import java.util.List;

import com.tterrag.advent2019.days.Day15.Point;
import com.tterrag.advent2019.util.Day;
import com.tterrag.advent2019.util.intcode.IntcodeInterpreter;

public class Day19 extends Day {
    
    final long[] input = csvLongArray();
    
    @Override
    protected Result doParts() {
        
        return super.doParts();
    }
    
    int x, y;
    int affectedCount = 0;

    @Override
    protected Object part1() {
        boolean[][] affected = new boolean[50][50];
        for (y = 0; y < affected.length; y++) {
            for (x = 0; x < affected[y].length; x++) {
                new IntcodeInterpreter(v -> {
                    affected[y][x] = v == 1;
                    affectedCount += v;
                }, x, y).execute(input);
            }
        }
        return affectedCount;
    }
    
    List<Integer> startX = new ArrayList<>(10000);
    List<Integer> endX = new ArrayList<>(10000);
    
    @Override
    protected Object part2() {
        int prevStart = -1;
        for (y = 0; startX.isEmpty() || endX.get(y - 1) - startX.get(y - 1) < 200 ; y++) {
            int start = -1;
            int end = -1;
            for (x = prevStart; x < 10000 || start != -1; x++) {
                boolean affected = new IntcodeInterpreter(x, y).execute(input) == 1;
                if (affected && start < 0) {
                    start = x;
                } else if (start >= 0 && !affected) {
                    end = x - 1;
                    break;
                }
            }
            startX.add(start);
            endX.add(end);
            prevStart = start;
        }
        Point best = null;
        y--;
        while (true) {
            int bottomX = startX.get(y);
            int maxXTop = endX.get(y - 99);
            if (maxXTop >= bottomX + 99) {
                best = new Point(bottomX, y - 99);
                y--;
            } else {
                return (best.getX() * 10000) + best.getY();
            }
        }
    }
}
