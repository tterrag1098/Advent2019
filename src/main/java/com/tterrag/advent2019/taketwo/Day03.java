package com.tterrag.advent2019.taketwo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.tterrag.advent2019.util.Day;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

public class Day03 extends Day {
    
    public static void main(String[] args) {
        // Uncomment to run x1000 for JIT warmup, just because it's cool
        //for (int i = 0; i < 1000; i++) {
            new Day03().run();
        //}
    }
    
    @Value
    private static class Point {
        int x, y, depthA, depthB;
        
        public int manhattanDistance() {
            return Math.abs(x) + Math.abs(y);
        }
    }
    
    @RequiredArgsConstructor
    @ToString(exclude = "prev")
    @Getter(AccessLevel.NONE)
    private static class Segment {
        final Segment prev;
        final int x1, x2, y1, y2;
        
        int minX, minY, maxX, maxY;
        boolean boundsInitialized;
        
        int rootDepth = -1;
        
        public Segment(Segment prev, char dir, int len) {
            this(prev,
                 prev == null ? 0 : prev.x2, (prev == null ? 0 : prev.x2) + (dir == 'R' ? len : dir == 'L' ? -len : 0),
                 prev == null ? 0 : prev.y2, (prev == null ? 0 : prev.y2) + (dir == 'U' ? len : dir == 'D' ? -len : 0));
        }
        
        public Point intersection(Segment other) {
            int x = Integer.MIN_VALUE;
            int y = Integer.MIN_VALUE;
            if (x1 != x2 && other.x1 == other.x2) {
                x = other.x1;
            } else if (x1 == x2 && other.x1 != other.x2) {
                x = x1;
            }
            if (y1 != y2 && other.y1 == other.y2) {
                y = other.y1;
            } else if (y1 == y2 && other.y1 != other.y2) {
                y = y1;
            }
            if (isOnLine(x, y) && other.isOnLine(x, y)) {
                return new Point(x, y, getDepth(x, y), other.getDepth(x, y));
            }
            return null;
        }
        
        public boolean isOnLine(int x, int y) {
            if (!boundsInitialized) {
                minX = Math.min(x1, x2);
                maxX = Math.max(x1, x2);
                minY = Math.min(y1, y2);
                maxY = Math.max(y1, y2);
            }
            return (x == x1 && y >= minY && y <= maxY)
                || (y == y1 && x >= minX && x <= maxX);
        }
        
        private int getDepth(int x, int y) {
            if (rootDepth == -1) {
                rootDepth = prev == null ? 0 : prev.getDepth(prev.x2, prev.y2);
            }
            int distanceAlong;
            if (x1 == x2) {
                distanceAlong = Math.abs(y1 - y);
            } else {
                distanceAlong = Math.abs(x1 - x);
            }
            return rootDepth + distanceAlong;
        }
    }
    
    private List<Segment> pathA = parse(csvList(linesList().get(0)));
    private List<Segment> pathB = parse(csvList(linesList().get(1)));
    
    private List<Segment> parse(List<String> segments) {
        List<Segment> ret = new ArrayList<>();
        Segment prev = null;
        for (String instr : segments) {
            char dir = instr.charAt(0);
            int amt = Integer.parseInt(instr.substring(1));
            Segment s = new Segment(prev, dir, amt);
            ret.add(s);
            prev = s;
        }
        return ret;
    }

    @Override
    protected Result doParts() {
        Set<Point> intersections = new HashSet<>();
        ListIterator<Segment> itrA = pathA.listIterator();
        while (itrA.hasNext()) {
            int idx = itrA.nextIndex();
            Segment s1 = itrA.next();
            ListIterator<Segment> itrB = pathB.listIterator(idx);
            while (itrB.hasNext()) {
                Point i = s1.intersection(itrB.next());
                if (i != null) {
                    intersections.add(i);
                }
            }
        }
        intersections.remove(new Point(0, 0, 0, 0));

        return new Result(intersections.stream().mapToInt(Point::manhattanDistance).min().orElseThrow(IllegalStateException::new),
                          intersections.stream().mapToInt(n -> n.getDepthA() + n.getDepthB()).min().orElseThrow(IllegalStateException::new));
    }
}
