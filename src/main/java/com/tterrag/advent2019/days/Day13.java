package com.tterrag.advent2019.days;

import java.util.HashMap;
import java.util.Map;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import com.tterrag.advent2019.days.Day11.Point;
import com.tterrag.advent2019.util.Day;
import com.tterrag.advent2019.util.intcode.IntcodeInterpreter;

public class Day13 extends Day {

    private final Map<Point, Long> screen = new HashMap<>();
    private Point paddle;
    private Point ball;
    private int segment;

    class OutputHandler implements LongConsumer {

        int idx = 0;
        int x, y;

        @Override
        public void accept(long value) {
            switch (idx++) {
            case 0:
                x = (int) value;
                break;
            case 1:
                y = (int) value;
                break;
            case 2:
                idx = 0;
                if (x == -1 && y == 0) {
                    segment = (int) value;
                    break;
                }
                screen.put(new Point(x, y), value);
                if (value == 3) {
                    paddle = new Point(x, y);
                }
                if (value == 4) {
                    ball = new Point(x, y);
                }
                break;
            }
        }
    }
    
    class InputHandler implements LongSupplier {
        
        @Override
        public long getAsLong() {
            int targetX = ball.getX();
            int x = paddle.getX();
            if (targetX == x) {
                return 0;
            } else if (targetX < x) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    @Override
    protected Object part1() {
        new IntcodeInterpreter(new OutputHandler()).execute(csvLongArray());
        return screen.values().stream().filter(i -> i == 2).count();
    }

    @Override
    protected Object part2() {
        long[] input = csvLongArray();
        input[0] = 2;
        new IntcodeInterpreter(new InputHandler(), new OutputHandler()).execute(input);
        return segment;
    }
}
