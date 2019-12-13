package com.tterrag.advent2019.days;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongConsumer;

import com.tterrag.advent2019.util.Day;
import com.tterrag.advent2019.util.intcode.IntcodeInterpreter;

import lombok.Value;
import lombok.experimental.Wither;

public class Day11 extends Day {
    
    private final long[] input = csvLongArray();
    
    @Value
    @Wither
    static class Point implements Comparable<Point> {
        int x, y;
        
        @Override
        public int compareTo(Point o) {
            int res = Integer.compare(y, o.y);
            if (res == 0) {
                res = Integer.compare(x, o.x);
            }
            return res;
        }
    }
    
    private final Map<Point, Long> painted = new HashMap<>();
    Point robotpos = new Point(0, 0);
    int dir;

    @Override
    protected Result doParts() {
        LongConsumer outputHandler = new LongConsumer() {
            
            boolean painting = true;
            @Override
            public void accept(long value) {
                System.out.println(value);
                if (painting) {
                    painted.put(robotpos, value);
                } else {
                    rotateRobot(value == 0);
                    moveRobot();
                }
                painting = !painting;
            }
        };

        new IntcodeInterpreter(() -> painted.getOrDefault(robotpos, 0L), outputHandler).execute(input);
        int p1 = painted.size();
        
        robotpos = new Point(0, 0);
        painted.clear();
        dir = 0;
        painted.put(new Point(0, 0), 1L);
        
        new IntcodeInterpreter(() -> painted.getOrDefault(robotpos, 0L), outputHandler).execute(input);
        
        StringBuilder picture = new StringBuilder("\n");
        Point min = Collections.min(painted.keySet());
        Point max = Collections.max(painted.keySet());
        for (int y = min.y; y <= max.y; y++) {
            for (int x = min.x; x <= max.x; x++) {
                picture.append(painted.getOrDefault(new Point(x, y), 0L) == 1 ? '#' : '.');
            }
            picture.append("\n");
        }
        
        return new Result(p1, picture);
    }
    
    void rotateRobot(boolean ccw) {
        if (ccw) {
            dir--;
        } else {
            dir++;
        }
        dir = (dir + 4) % 4;
    }
    
    void moveRobot() {
        switch(dir) {
        case 0:
            robotpos = robotpos.withY(robotpos.getY() - 1);
            break;
        case 1:
            robotpos = robotpos.withX(robotpos.getX() + 1);
            break;
        case 2:
            robotpos = robotpos.withY(robotpos.getY() + 1);
            break;
        case 3:
            robotpos = robotpos.withX(robotpos.getX() - 1);
            break;
        }
    }
}
